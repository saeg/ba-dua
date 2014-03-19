/**
 * Copyright (c) 2014 University of Sao Paulo and Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Roberto Araujo - initial API and implementation and/or initial documentation
 */
package br.usp.each.saeg.badua.core.internal.instr.df;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import br.usp.each.saeg.badua.core.internal.instr.IdGenerator;
import br.usp.each.saeg.badua.core.internal.instr.InstrSupport;
import br.usp.each.saeg.badua.core.internal.instr.LabelFrameNode;
import br.usp.each.saeg.badua.core.internal.instr.MethodTransformer;
import br.usp.each.saeg.bytecode.analysis.BytecodeInstruction;
import br.usp.each.saeg.bytecode.analysis.BytecodeInstructionType;
import br.usp.each.saeg.bytecode.analysis.CouldNotBuildGraphException;
import br.usp.each.saeg.bytecode.analysis.GraphBuilder;
import br.usp.each.saeg.bytecode.analysis.graph.GraphNode;
import br.usp.each.saeg.bytecode.analysis.graph.PreOrderTraversalStrategy;
import br.usp.each.saeg.bytecode.analysis.graph.ProgramGraph;
import br.usp.each.saeg.bytecode.analysis.graph.defuse.VariableRef;
import br.usp.each.saeg.bytecode.analysis.graph.defuse.VariablesCollector;
import br.usp.each.saeg.commons.BitSetUtils;
import br.usp.each.saeg.opal.Block;
import br.usp.each.saeg.opal.Graph;
import br.usp.each.saeg.opal.requirement.DepthFirstDuaAnalyzer;
import br.usp.each.saeg.opal.requirement.Dua;

public class CoverageMethodTransformer extends MethodTransformer {

    private static final DepthFirstDuaAnalyzer DUA = new DepthFirstDuaAnalyzer();

    private final String className;

    private final IdGenerator idGen;

    private ProgramGraph programGraph;

    private List<VariableRef> variables;

    private Graph<Block> graph;

    private Dua[] duas;

    public ProgramGraph getProgramGraph() {
        return programGraph;
    }

    public List<VariableRef> getVariables() {
        return variables;
    }

    public Graph<Block> getGraph() {
        return graph;
    }

    public Dua[] getDuas() {
        return duas;
    }

    public CoverageMethodTransformer(final String className, final IdGenerator idGen) {
        this.className = className;
        this.idGen = idGen;
    }

    @Override
    public void transform(final MethodNode mn) {
        try {
            programGraph = new GraphBuilder(className, mn).buildDefUseGraph();
        } catch (final CouldNotBuildGraphException e) {
            throw new RuntimeException(e);
        }

        // Collect all variables from this program graph
        final VariablesCollector varCollector = new VariablesCollector();
        new PreOrderTraversalStrategy(varCollector).traverse(programGraph.getRootNode());
        variables = varCollector.getVariableRefs();

        graph = new Graph<Block>();
        new PreOrderTraversalStrategy(new GraphBlockCreator(graph, variables))
                .traverse(programGraph.getRootNode());

        duas = DUA.analyze(graph, variables.size());

        instrument(mn);
    }

    private void instrument(final MethodNode mn) {
        if (duas.length == 0) {
            // do not instrument methods without definition-use-associations
            return;
        } else if (duas.length <= 32) {
            // instrument using integers
            instrument32(mn, programGraph, graph, duas);
        } else {
            // instrument using longs
            instrument64(mn, programGraph, graph, duas);
        }
    }

    private void instrument32(final MethodNode methodNode, final ProgramGraph g,
            final Graph<Block> dfg, final Dua[] duas) {

        final Graph<Block> inverse = dfg.inverse();

        final int index = idGen.nextId();
        final int var = methodNode.maxLocals;

        methodNode.instructions.insert(init32(var));

        for (final GraphNode node : g.getNodes()) {
            if (node.isGotoNode()) {
                continue;
            }

            final BitSets sets = new BitSets(duas, dfg.get(node.id));
            final InsnList insns = methodNode.instructions;
            final boolean singlePred = inverse.neighbors(node.id).size() == 1;

            if (node == g.getRootNode() && node.getParents().length == 0) {
                LabelFrameNode.insertBefore(first(node), insns, init32(sets, var));
            } else {
                LabelFrameNode.insertBefore(first(node), insns, probe32(sets, var, singlePred));
            }
            if (node.getChildren().length == 0) {
                LabelFrameNode.insertBefore(last(node), insns, update32(var, index));
            }
        }

        AbstractInsnNode insn = methodNode.instructions.getFirst();
        while (insn != null) {
            if (insn instanceof FrameNode) {
                final FrameNode frame = (FrameNode) insn;
                frame.local = new ArrayList<Object>(frame.local);
                int size = 0;
                for (final Object obj : frame.local) {
                    size++;
                    if (obj.equals(Opcodes.DOUBLE) || obj.equals(Opcodes.LONG)) {
                        size++;
                    }
                }
                while (size < methodNode.maxLocals) {
                    frame.local.add(Opcodes.TOP);
                    size++;
                }
                frame.local.add(Opcodes.INTEGER);
                frame.local.add(Opcodes.INTEGER);
                frame.local.add(Opcodes.INTEGER);
                frame.local.add(InstrSupport.DATAFIELD_DESC);

            }
            insn = insn.getNext();
        }
        methodNode.maxLocals = methodNode.maxLocals + 4;
        methodNode.maxStack = methodNode.maxStack + 6;
    }

    private void instrument64(final MethodNode methodNode, final ProgramGraph g,
            final Graph<Block> dfg, final Dua[] duas) {

        final Graph<Block> inverse = dfg.inverse();

        final int windows = (duas.length + 63) / 64;
        final int[] index = new int[windows];
        final int[] vars = new int[windows];
        for (int i = 0; i < windows; i++) {
            index[i] = idGen.nextId();
            vars[i] = i * 6 + methodNode.maxLocals;
        }

        methodNode.instructions.insert(init64(vars));

        for (final GraphNode node : g.getNodes()) {
            if (node.isGotoNode()) {
                continue;
            }

            final BitSets sets = new BitSets(duas, dfg.get(node.id));
            final InsnList insns = methodNode.instructions;
            final boolean singlePred = inverse.neighbors(node.id).size() == 1;

            if (node == g.getRootNode() && node.getParents().length == 0) {
                LabelFrameNode.insertBefore(first(node), insns, init64(sets, vars));
            } else {
                LabelFrameNode.insertBefore(first(node), insns, probe64(sets, vars, singlePred));
            }
            if (node.getChildren().length == 0) {
                LabelFrameNode.insertBefore(last(node), insns, update64(vars, ldata(vars), index));
            }
        }

        AbstractInsnNode insn = methodNode.instructions.getFirst();
        while (insn != null) {
            if (insn instanceof FrameNode) {
                final FrameNode frame = (FrameNode) insn;
                frame.local = new ArrayList<Object>(frame.local);
                int size = 0;
                for (final Object obj : frame.local) {
                    size++;
                    if (obj.equals(Opcodes.DOUBLE) || obj.equals(Opcodes.LONG)) {
                        size++;
                    }
                }
                while (size < methodNode.maxLocals) {
                    frame.local.add(Opcodes.TOP);
                    size++;
                }

                for (int i = 0; i < windows; i++) {
                    frame.local.add(Opcodes.LONG);
                    frame.local.add(Opcodes.LONG);
                    frame.local.add(Opcodes.LONG);
                }
                frame.local.add(InstrSupport.DATAFIELD_DESC);

            }
            insn = insn.getNext();
        }
        methodNode.maxLocals = methodNode.maxLocals + windows * 6 + 1;
        methodNode.maxStack = methodNode.maxStack + 6;
    }

    private InsnList init32(final int var) {
        final InsnList il = new InsnList();
        il.add(new InsnNode(Opcodes.ICONST_0));
        il.add(new VarInsnNode(Opcodes.ISTORE, covered(var)));
        il.add(new InsnNode(Opcodes.ICONST_0));
        il.add(new VarInsnNode(Opcodes.ISTORE, alive(var)));
        il.add(new InsnNode(Opcodes.ICONST_M1));
        il.add(new VarInsnNode(Opcodes.ISTORE, sleepy(var)));
        il.add(new MethodInsnNode(Opcodes.INVOKESTATIC, className,
                InstrSupport.INITMETHOD_NAME, InstrSupport.INITMETHOD_DESC));
        il.add(new VarInsnNode(Opcodes.ASTORE, data(var)));
        return il;
    }

    private InsnList init64(final int[] vars) {
        final InsnList il = new InsnList();
        for (int i = 0; i < vars.length; i++) {
            il.add(new InsnNode(Opcodes.LCONST_0));
            il.add(new VarInsnNode(Opcodes.LSTORE, lcovered(vars, i)));
            il.add(new InsnNode(Opcodes.LCONST_0));
            il.add(new VarInsnNode(Opcodes.LSTORE, lalive(vars, i)));
            il.add(new LdcInsnNode(-1L));
            il.add(new VarInsnNode(Opcodes.LSTORE, lsleepy(vars, i)));
        }
        il.add(new MethodInsnNode(Opcodes.INVOKESTATIC, className,
                InstrSupport.INITMETHOD_NAME, InstrSupport.INITMETHOD_DESC));
        il.add(new VarInsnNode(Opcodes.ASTORE, ldata(vars)));
        return il;
    }

    private static InsnList init32(final BitSets sets, final int var) {
        final int born = BitSetUtils.toInteger(sets.born);
        final int node_sleepy = BitSetUtils.toInteger(sets.sleepy);

        final InsnList il = new InsnList();
        if (born != 0) {
            il.add(InstrSupport.push(born));
            il.add(new VarInsnNode(Opcodes.ISTORE, alive(var)));
        }
        if (node_sleepy != 0) {
            il.add(InstrSupport.push(~node_sleepy));
            il.add(new VarInsnNode(Opcodes.ISTORE, sleepy(var)));
        }
        return il;
    }

    private static InsnList init64(final BitSets sets, final int[] vars) {
        final long[] long_born = BitSetUtils.toLongArray(sets.born, vars.length);
        final long[] long_sleepy = BitSetUtils.toLongArray(sets.sleepy, vars.length);

        final InsnList il = new InsnList();
        for (int i = 0; i < vars.length; i++) {
            if (long_born[i] != 0) {
                il.add(new LdcInsnNode(long_born[i]));
                il.add(new VarInsnNode(Opcodes.LSTORE, lalive(vars, i)));
            }
            if (long_sleepy[i] != 0) {
                il.add(new LdcInsnNode(~long_sleepy[i]));
                il.add(new VarInsnNode(Opcodes.LSTORE, lsleepy(vars, i)));
            }
        }
        return il;
    }

    private static InsnList probe32(final BitSets sets, final int var, final boolean singlePred) {
        final int potcov = BitSetUtils.toInteger(sets.potcov);
        final int born = BitSetUtils.toInteger(sets.born);
        final int disabled = BitSetUtils.toInteger(sets.disabled);
        final int node_sleepy = BitSetUtils.toInteger(sets.sleepy);
        final int potcov_p = BitSetUtils.toInteger(sets.potcovpuse);

        final InsnList il = new InsnList();
        // Update covered
        if (potcov != 0) {
            il.add(new VarInsnNode(Opcodes.ILOAD, alive(var)));
            if (!singlePred && potcov_p != 0) {
                il.add(new VarInsnNode(Opcodes.ILOAD, sleepy(var)));
                il.add(new InsnNode(Opcodes.IAND));
            }
            il.add(InstrSupport.push(potcov));
            il.add(new InsnNode(Opcodes.IAND));
            il.add(new VarInsnNode(Opcodes.ILOAD, covered(var)));
            il.add(new InsnNode(Opcodes.IOR));
            il.add(new VarInsnNode(Opcodes.ISTORE, covered(var)));
        }
        // Update alive
        if (disabled != 0) {
            il.add(InstrSupport.push(~disabled));
            il.add(new VarInsnNode(Opcodes.ILOAD, alive(var)));
            il.add(new InsnNode(Opcodes.IAND));
        }
        if (born != 0) {
            if (disabled == 0) {
                il.add(new VarInsnNode(Opcodes.ILOAD, alive(var)));
            }
            il.add(InstrSupport.push(born));
            il.add(new InsnNode(Opcodes.IOR));
        }
        if (disabled != 0 || born != 0) {
            il.add(new VarInsnNode(Opcodes.ISTORE, alive(var)));
        }
        // Update sleepy
        il.add(InstrSupport.push(~node_sleepy));
        il.add(new VarInsnNode(Opcodes.ISTORE, sleepy(var)));
        return il;
    }

    private static InsnList probe64(final BitSets sets, final int[] var, final boolean singlePred) {
        final long[] potcov = BitSetUtils.toLongArray(sets.potcov, var.length);
        final long[] born = BitSetUtils.toLongArray(sets.born, var.length);
        final long[] disabled = BitSetUtils.toLongArray(sets.disabled, var.length);
        final long[] node_sleepy = BitSetUtils.toLongArray(sets.sleepy, var.length);
        final long[] potcov_p = BitSetUtils.toLongArray(sets.potcovpuse, var.length);

        final InsnList il = new InsnList();
        for (int i = 0; i < var.length; i++) {
            // Update covered
            if (potcov[i] != 0) {
                il.add(new VarInsnNode(Opcodes.LLOAD, lalive(var, i)));
                if (!singlePred && potcov_p[i] != 0) {
                    il.add(new VarInsnNode(Opcodes.LLOAD, lsleepy(var, i)));
                    il.add(new InsnNode(Opcodes.LAND));
                }
                il.add(new LdcInsnNode(potcov[i]));
                il.add(new InsnNode(Opcodes.LAND));
                il.add(new VarInsnNode(Opcodes.LLOAD, lcovered(var, i)));
                il.add(new InsnNode(Opcodes.LOR));
                il.add(new VarInsnNode(Opcodes.LSTORE, lcovered(var, i)));
            }
            // Update alive
            if (disabled[i] != 0) {
                il.add(new LdcInsnNode(~disabled[i]));
                il.add(new VarInsnNode(Opcodes.LLOAD, lalive(var, i)));
                il.add(new InsnNode(Opcodes.LAND));
            }
            if (born[i] != 0) {
                if (disabled[i] == 0) {
                    il.add(new VarInsnNode(Opcodes.LLOAD, lalive(var, i)));
                }
                il.add(new LdcInsnNode(born[i]));
                il.add(new InsnNode(Opcodes.LOR));
            }
            if (disabled[i] != 0 || born[i] != 0) {
                il.add(new VarInsnNode(Opcodes.LSTORE, lalive(var, i)));
            }
            // Update sleepy
            if (node_sleepy[i] != 0) {
                il.add(new LdcInsnNode(~node_sleepy[i]));
                il.add(new VarInsnNode(Opcodes.LSTORE, lsleepy(var, i)));
            } else {
                il.add(new LdcInsnNode(-1L));
                il.add(new VarInsnNode(Opcodes.LSTORE, lsleepy(var, i)));
            }
        }
        return il;
    }

    private static InsnList update32(final int var, final int index) {
        final InsnList il = new InsnList();
        il.add(new VarInsnNode(Opcodes.ILOAD, covered(var)));
        il.add(new InsnNode(Opcodes.I2L));
        il.add(new VarInsnNode(Opcodes.ALOAD, data(var)));
        il.add(InstrSupport.push(index));
        il.add(new InsnNode(Opcodes.DUP2_X2));
        il.add(new InsnNode(Opcodes.LALOAD));
        il.add(new InsnNode(Opcodes.LOR));
        il.add(new InsnNode(Opcodes.LASTORE));
        return il;
    }

    private static InsnList update64(final int[] var, final int data, final int[] index) {
        final InsnList il = new InsnList();
        for (int i = 0; i < var.length; i++) {
            il.add(new VarInsnNode(Opcodes.LLOAD, lcovered(var, i)));
            il.add(new VarInsnNode(Opcodes.ALOAD, data));
            il.add(InstrSupport.push(index[i]));
            il.add(new InsnNode(Opcodes.DUP2_X2));
            il.add(new InsnNode(Opcodes.LALOAD));
            il.add(new InsnNode(Opcodes.LOR));
            il.add(new InsnNode(Opcodes.LASTORE));
        }
        return il;
    }

    // --- Auxiliary methods

    private static AbstractInsnNode first(final GraphNode node) {
        for (final BytecodeInstruction i : node.instructions) {
            // skip pseudo-instruction and NOP
            if (i.getType() == BytecodeInstructionType.FRAME ||
                i.getType() == BytecodeInstructionType.LINE_NUMBER ||
                i.getType() == BytecodeInstructionType.LABEL ||
                i.getType() == BytecodeInstructionType.NOP) {
                continue;
            }
            return i.getInstruction();
        }
        return null;
    }

    private static AbstractInsnNode last(final GraphNode node) {
        int n = node.instructions.size() - 1;
        while (n >= 0) {
            final BytecodeInstruction i = node.instructions.get(n--);
            // skip pseudo-instruction and NOP
            if (i.getType() == BytecodeInstructionType.FRAME ||
                i.getType() == BytecodeInstructionType.LINE_NUMBER ||
                i.getType() == BytecodeInstructionType.LABEL ||
                i.getType() == BytecodeInstructionType.NOP) {
                continue;
            }
            return i.getInstruction();
        }

        throw null;
    }

    private static int covered(final int var) {
        return var;
    }

    private static int alive(final int var) {
        return var + 1;
    }

    private static int sleepy(final int var) {
        return var + 2;
    }

    private static int data(final int var) {
        return var + 3;
    }

    private static int lcovered(final int[] vars, final int window) {
        return vars[window];
    }

    private static int lalive(final int[] vars, final int window) {
        return vars[window] + 2;
    }

    private static int lsleepy(final int[] vars, final int window) {
        return vars[window] + 4;
    }

    private static int ldata(final int[] vars) {
        return vars[vars.length - 1] + 6;
    }

}
