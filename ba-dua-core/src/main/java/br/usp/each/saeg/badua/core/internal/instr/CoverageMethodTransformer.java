/**
 * Copyright (c) 2014, 2017 University of Sao Paulo and Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Roberto Araujo - initial API and implementation and/or initial documentation
 */
package br.usp.each.saeg.badua.core.internal.instr;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;

import br.usp.each.saeg.asm.defuse.DefUseAnalyzer;
import br.usp.each.saeg.asm.defuse.DefUseChain;
import br.usp.each.saeg.asm.defuse.DefUseFrame;
import br.usp.each.saeg.asm.defuse.DefUseInterpreter;
import br.usp.each.saeg.asm.defuse.DepthFirstDefUseChainSearch;
import br.usp.each.saeg.asm.defuse.FlowAnalyzer;
import br.usp.each.saeg.asm.defuse.Value;
import br.usp.each.saeg.asm.defuse.Variable;
import br.usp.each.saeg.commons.BitSetUtils;

public class CoverageMethodTransformer extends MethodTransformer {

    private final String className;

    private final IdGenerator idGen;

    public CoverageMethodTransformer(final String className, final IdGenerator idGen) {
        this.className = className;
        this.idGen = idGen;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void transform(final MethodNode methodNode) {

        final DefUseInterpreter interpreter = new DefUseInterpreter();
        final FlowAnalyzer<Value> flowAnalyzer = new FlowAnalyzer<Value>(interpreter);
        final DefUseAnalyzer analyzer = new DefUseAnalyzer(flowAnalyzer, interpreter);
        try {
            analyzer.analyze(className, methodNode);
        } catch (final AnalyzerException e) {
            throw new RuntimeException(e);
        }

        final DefUseFrame[] frames = analyzer.getDefUseFrames();
        final Variable[] variables = analyzer.getVariables();
        final int[][] successors = flowAnalyzer.getSuccessors();
        final int[][] predecessors = flowAnalyzer.getPredecessors();
        final int[][] basicBlocks = flowAnalyzer.getBasicBlocks();
        final int[] leaders = flowAnalyzer.getLeaders();

        final DefUseChain[] chains = DefUseChain.toBasicBlock(new DepthFirstDefUseChainSearch()
                .search(frames, variables, successors, predecessors), leaders, basicBlocks);

        if (chains.length == 0)
            return;

        // basic block definitions
        final Set<Variable>[] defs = (Set<Variable>[]) new Set<?>[basicBlocks.length];
        for (int b = 0; b < basicBlocks.length; b++) {
            defs[b] = new HashSet<Variable>();
            for (final int insnIndex : basicBlocks[b]) {
                defs[b].addAll(frames[insnIndex].getDefinitions());
            }
        }

        // bit-sets
        final BitSet[] potcov = new BitSet[basicBlocks.length];
        final BitSet[] potcovpuse = new BitSet[basicBlocks.length];
        final BitSet[] born = new BitSet[basicBlocks.length];
        final BitSet[] disabled = new BitSet[basicBlocks.length];
        final BitSet[] sleepy = new BitSet[basicBlocks.length];
        for (int b = 0; b < basicBlocks.length; b++) {

            potcov[b] = new BitSet(chains.length);
            potcovpuse[b] = new BitSet(chains.length);
            born[b] = new BitSet(chains.length);
            disabled[b] = new BitSet(chains.length);
            sleepy[b] = new BitSet(chains.length);

            for (int i = 0; i < chains.length; i++) {

                final DefUseChain chain = chains[i];

                if (chain.isPredicateChain() ? chain.target == b : chain.use == b) {
                    potcov[b].set(i);
                    if (chain.isPredicateChain()) {
                        potcovpuse[b].set(i);
                    }
                }

                if (chain.def == b) {
                    born[b].set(i);
                }

                if (chain.def != b && defs[b].contains(variables[chain.var])) {
                    disabled[b].set(i);
                }

                if (chain.isPredicateChain() && chain.use != b) {
                    sleepy[b].set(i);
                }

            }
        }

        // first/last valid instructions
        final AbstractInsnNode[] first = new AbstractInsnNode[basicBlocks.length];
        final AbstractInsnNode[] last = new AbstractInsnNode[basicBlocks.length];
        for (int b = 0; b < basicBlocks.length; b++) {
            for (final int insnIndex : basicBlocks[b]) {
                final AbstractInsnNode insn = methodNode.instructions.get(insnIndex);

                // skip
                switch (insn.getType()) {
                case AbstractInsnNode.LABEL:
                case AbstractInsnNode.FRAME:
                case AbstractInsnNode.LINE:
                    continue;
                }

                if (first[b] == null) {
                    first[b] = insn;
                }
                last[b] = insn;
            }
        }

        AbstractInsnNode insn = methodNode.instructions.getFirst();
        final int windows = (chains.length + 63) / 64;
        final int[] indexes = new int[windows];
        for (int w = 0; w < windows; w++) {
            indexes[w] = idGen.nextId();
            LabelFrameNode.insertBefore(insn, methodNode.instructions, init(chains, methodNode, w));
        }

        for (int b = 0; b < basicBlocks.length; b++) {

            final long[] lPotcov = BitSetUtils.toLongArray(potcov[b], windows);
            final long[] lPotcovpuse = BitSetUtils.toLongArray(potcovpuse[b], windows);
            final long[] lBorn = BitSetUtils.toLongArray(born[b], windows);
            final long[] lDisabled = BitSetUtils.toLongArray(disabled[b], windows);
            final long[] lSleepy = BitSetUtils.toLongArray(sleepy[b], windows);

            for (int w = 0; w < windows; w++) {

                final int nPredecessors = predecessors[basicBlocks[b][0]].length;
                final Probe p = probe(chains, methodNode, w, nPredecessors == 0);

                p.potcov = lPotcov[w];
                p.potcovpuse = lPotcovpuse[w];
                p.born = lBorn[w];
                p.disabled = lDisabled[w];
                p.sleepy = lSleepy[w];
                p.singlePredecessor = nPredecessors == 1;

                LabelFrameNode.insertBefore(first[b], methodNode.instructions, p);

            }
        }

        // Finally, update the frames and add exit probes
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
                final Integer type = typeOfVars(chains);
                for (int i = 0; i < windows; i++) {
                    frame.local.add(type);
                    frame.local.add(type);
                    frame.local.add(type);
                }
            } else if (isReturn(insn.getOpcode())) {
                for (int w = 0; w < windows; w++) {
                    final Probe p = update(chains, methodNode, w, indexes[w]);
                    LabelFrameNode.insertBefore(insn, methodNode.instructions, p);
                }
            }
            insn = insn.getNext();
        }

        methodNode.maxLocals = methodNode.maxLocals + windows * numOfVars(chains);
        methodNode.maxStack = methodNode.maxStack + 6;
    }

    private Probe init(final DefUseChain[] chains, final MethodNode methodNode, final int window) {
        if (chains.length <= 32) {
            return new IntegerInitProbe(methodNode);
        } else {
            return new LongInitProbe(methodNode, window);
        }
    }

    private Probe probe(final DefUseChain[] chains, final MethodNode methodNode, final int window, final boolean root) {
        if (chains.length <= 32) {
            if (root) {
                return new IntegerRootProbe(methodNode);
            } else {
                return new IntegerProbe(methodNode);
            }
        } else {
            if (root) {
                return new LongRootProbe(methodNode, window);
            } else {
                return new LongProbe(methodNode, window);
            }
        }
    }

    private Probe update(final DefUseChain[] chains, final MethodNode methodNode, final int window, final int index) {
        if (chains.length <= 32) {
            return new IntegerUpdateProbe(methodNode, className, index);
        } else {
            return new LongUpdateProbe(methodNode, window, className, index);
        }
    }

    private boolean isReturn(final int opcode) {
        if (opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN)
            return true;

        return opcode == Opcodes.ATHROW;
    }

    private int numOfVars(final DefUseChain[] chains) {
        if (chains.length <= 32) {
            // three integers
            return 3;
        } else {
            // three longs
            return 6;
        }
    }

    private Integer typeOfVars(final DefUseChain[] chains) {
        if (chains.length <= 32) {
            // three integers
            return Opcodes.INTEGER;
        } else {
            // three longs
            return Opcodes.LONG;
        }
    }

}
