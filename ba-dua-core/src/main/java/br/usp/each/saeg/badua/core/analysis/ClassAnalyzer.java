/**
 * Copyright (c) 2014, 2018 University of Sao Paulo and Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Roberto Araujo - initial API and implementation and/or initial documentation
 */
package br.usp.each.saeg.badua.core.analysis;

import static br.usp.each.saeg.commons.ArrayUtils.indexOf;
import static br.usp.each.saeg.commons.BitSetUtils.valueOf;

import java.util.Arrays;
import java.util.BitSet;

import org.jacoco.core.internal.analysis.StringPool;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.LineNumberNode;
import org.objectweb.asm.tree.LocalVariableNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;

import br.usp.each.saeg.asm.defuse.DefUseAnalyzer;
import br.usp.each.saeg.asm.defuse.DefUseChain;
import br.usp.each.saeg.asm.defuse.DefUseInterpreter;
import br.usp.each.saeg.asm.defuse.DepthFirstDefUseChainSearch;
import br.usp.each.saeg.asm.defuse.FlowAnalyzer;
import br.usp.each.saeg.asm.defuse.Local;
import br.usp.each.saeg.asm.defuse.ObjectField;
import br.usp.each.saeg.asm.defuse.StaticField;
import br.usp.each.saeg.asm.defuse.Value;
import br.usp.each.saeg.asm.defuse.Variable;
import br.usp.each.saeg.badua.core.data.ExecutionData;
import br.usp.each.saeg.badua.core.internal.instr.InstrSupport;

public class ClassAnalyzer extends ClassVisitor {

    private final ExecutionData execData;

    private final StringPool stringPool;

    private ClassCoverage coverage;

    private boolean interfaceType;

    private int window;

    public ClassAnalyzer(final ExecutionData execData, final StringPool stringPool) {
        super(Opcodes.ASM6);
        this.execData = execData;
        this.stringPool = stringPool;
    }

    @Override
    public void visit(final int version,
                      final int access,
                      final String name,
                      final String signature,
                      final String superName,
                      final String[] interfaces) {

        if (!execData.getName().equals(name)) {
            throw new IllegalStateException("The provided data is incompatible.");
        }
        coverage = new ClassCoverage(stringPool.get(name));
        interfaceType = (access & Opcodes.ACC_INTERFACE) != 0;
        window = 0;
    }

    @Override
    public FieldVisitor visitField(final int access,
                                   final String name,
                                   final String desc,
                                   final String signature,
                                   final Object value) {

        InstrSupport.assertNotInstrumented(name, coverage.getName());
        return null;
    }

    @Override
    public MethodVisitor visitMethod(final int access,
                                     final String name,
                                     final String desc,
                                     final String signature,
                                     final String[] exceptions) {

        InstrSupport.assertNotInstrumented(name, coverage.getName());

        // Does not analyze:
        // 1. Interfaces
        if (interfaceType)
            return null;
        // 2. Abstract methods
        else if ((access & Opcodes.ACC_ABSTRACT) != 0)
            return null;
        // 3. Static class initialization
        else if (name.equals("<clinit>"))
            return null;

        return new MethodNode(Opcodes.ASM6, access, name, desc, signature, exceptions) {

            @Override
            public void visitEnd() {

                final DefUseInterpreter interpreter = new DefUseInterpreter();
                final FlowAnalyzer<Value> flowAnalyzer = new FlowAnalyzer<Value>(interpreter);
                final DefUseAnalyzer analyzer = new DefUseAnalyzer(flowAnalyzer, interpreter);
                try {
                    analyzer.analyze(coverage.getName(), this);
                } catch (final AnalyzerException e) {
                    throw new RuntimeException(e);
                }

                // Variables
                final Variable[] vars = analyzer.getVariables();

                // Instructions by line number
                final int[] lines = getLines();

                // All DU from current method node
                final DefUseChain[] insnChains = new DepthFirstDefUseChainSearch().search(
                        analyzer.getDefUseFrames(), analyzer.getVariables(),
                        flowAnalyzer.getSuccessors(),flowAnalyzer.getPredecessors());

                // Only global DU
                final DefUseChain[] globalInsnChains = DefUseChain.globals(insnChains,
                        flowAnalyzer.getLeaders(), flowAnalyzer.getBasicBlocks());

                // DU by basic block (the ones we monitor)
                final DefUseChain[] blockChains = DefUseChain.toBasicBlock(insnChains,
                        flowAnalyzer.getLeaders(), flowAnalyzer.getBasicBlocks());

                final BitSet data = getData(execData.getData(), blockChains.length);

                final MethodCoverage methodCoverage = new MethodCoverage(name, desc);
                for (final DefUseChain c : globalInsnChains) {

                    // Get the DU coverage status
                    final boolean covered = data.get(indexOf(blockChains,
                            DefUseChain.toBasicBlock(c, flowAnalyzer.getLeaders())));

                    if (c.isComputationalChain()) {
                        methodCoverage.increment(lines[c.def], lines[c.use], getVar(c, vars), covered);
                    } else {
                        methodCoverage.increment(lines[c.def], lines[c.use], lines[c.target], getVar(c, vars), covered);
                    }
                }
                if (methodCoverage.getDUCounter().getTotalCount() > 0) {
                    coverage.addMethod(methodCoverage);
                }
            }

            public BitSet getData(final long[] raw, final int length) {
                if (raw != null) {
                    return valueOf(Arrays.copyOfRange(raw, window, incrementWindow(length)));
                }
                return new BitSet();
            }

            public int[] getLines() {
                final int[] lines = new int[instructions.size()];
                Arrays.fill(lines, MethodCoverage.UNKNOWN_LINE);
                for (int i = 0; i < instructions.size(); i++) {
                    if (instructions.get(i).getType() == AbstractInsnNode.LINE) {
                        final LineNumberNode line = (LineNumberNode) instructions.get(i);
                        Arrays.fill(lines, instructions.indexOf(line.start), lines.length, line.line);
                    }
                }
                return lines;
            }

            public String getVar(final DefUseChain c, final Variable[] vars) {
                return getVar(vars[c.var], c.use);
            }

            public String getVar(final Value v, final int insn) {
                if (v instanceof StaticField) {
                    return ((StaticField) v).name;
                } else if (v instanceof ObjectField) {
                    final ObjectField objField = (ObjectField) v;
                    final String var = getVar(objField.getRoot(), insn);
                    if (var != null) {
                        return String.format("%s.%s", var, objField.name);
                    }
                } else if (v instanceof Local) {
                    return getVar((Local) v, insn);
                }
                return null;
            }

            public String getVar(final Local local, final int insn) {
                for (final LocalVariableNode lvn : localVariables) {
                    if (lvn.index == local.var
                            && insn >= instructions.indexOf(lvn.start)
                            && insn < instructions.indexOf(lvn.end)) {
                        return lvn.name;
                    }
                }
                return null;
            }

        };
    }

    private int incrementWindow(final int n) {
        return window += (n + 63) / 64;
    }

    public ClassCoverage getCoverage() {
        return coverage;
    }

}
