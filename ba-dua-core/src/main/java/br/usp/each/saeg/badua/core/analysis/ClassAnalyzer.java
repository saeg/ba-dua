/**
 * Copyright (c) 2014, 2016 University of Sao Paulo and Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Roberto Araujo - initial API and implementation and/or initial documentation
 */
package br.usp.each.saeg.badua.core.analysis;

import static br.usp.each.saeg.commons.BitSetUtils.valueOf;
import static java.util.Arrays.copyOfRange;
import static org.jacoco.core.internal.analysis.CounterImpl.COUNTER_0_1;
import static org.jacoco.core.internal.analysis.CounterImpl.COUNTER_1_0;

import java.util.BitSet;

import org.jacoco.core.internal.analysis.StringPool;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;

import br.usp.each.saeg.asm.defuse.DefUseAnalyzer;
import br.usp.each.saeg.asm.defuse.DefUseChain;
import br.usp.each.saeg.asm.defuse.DefUseInterpreter;
import br.usp.each.saeg.asm.defuse.DepthFirstDefUseChainSearch;
import br.usp.each.saeg.asm.defuse.FlowAnalyzer;
import br.usp.each.saeg.asm.defuse.Value;
import br.usp.each.saeg.badua.core.data.ExecutionData;
import br.usp.each.saeg.badua.core.internal.instr.InstrSupport;

public class ClassAnalyzer extends ClassVisitor {

    private final ExecutionData execData;

    private final StringPool stringPool;

    private ClassCoverage coverage;

    private boolean interfaceType;

    private int window;

    public ClassAnalyzer(final ExecutionData execData, final StringPool stringPool) {
        super(Opcodes.ASM5);
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

        // Does not instrument:
        // 1. Interfaces
        if (interfaceType)
            return null;
        // 2. Abstract methods
        else if ((access & Opcodes.ACC_ABSTRACT) != 0)
            return null;
        // 3. Static class initialization
        else if (name.equals("<clinit>"))
            return null;

        return new MethodNode(Opcodes.ASM5, access, name, desc, signature, exceptions) {

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

                // All DU from current method node
                final DefUseChain[] insnChains = new DepthFirstDefUseChainSearch().search(
                        analyzer.getDefUseFrames(), analyzer.getVariables(),
                        flowAnalyzer.getSuccessors(),flowAnalyzer.getPredecessors());

                // DU by basic block (the ones we monitor)
                final DefUseChain[] blockChains = DefUseChain.toBasicBlock(insnChains,
                        flowAnalyzer.getLeaders(), flowAnalyzer.getBasicBlocks());

                final BitSet data = getData(execData.getData(), blockChains.length);

                final MethodCoverage methodCoverage = new MethodCoverage(name, desc);
                for (int i = 0; i < blockChains.length; i++) {
                    methodCoverage.increment(data.get(i) ? COUNTER_0_1 : COUNTER_1_0);
                }
                if (methodCoverage.getDUCounter().getTotalCount() > 0) {
                    coverage.addMethod(methodCoverage);
                }
            }

            public BitSet getData(final long[] raw, final int length) {
                if (raw != null) {
                    return valueOf(copyOfRange(raw, window, incrementWindow(length)));
                }
                return new BitSet();
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
