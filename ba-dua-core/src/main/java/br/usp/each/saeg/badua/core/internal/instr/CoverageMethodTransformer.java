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
import java.util.TreeSet;

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
import br.usp.each.saeg.commons.ArrayUtils;
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

        final DefUseChain[] allChains = new DepthFirstDefUseChainSearch()
                .search(frames, variables, successors, predecessors);

        final int[] locals = getBlocks(DefUseChain.locals(allChains, leaders, basicBlocks), leaders);
        final DefUseChain[] chains = DefUseChain.toBasicBlock(allChains, leaders, basicBlocks);
        final int length = chains.length + locals.length;

        if (length == 0)
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

            potcov[b] = new BitSet(length);
            potcovpuse[b] = new BitSet(length);
            born[b] = new BitSet(length);
            disabled[b] = new BitSet(length);
            sleepy[b] = new BitSet(length);

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
        for (int i = 0; i < locals.length; i++) {
            potcov[locals[i]].set(chains.length + i);
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
        final int windows = (length + 63) / 64;
        final int[] indexes = new int[windows];

        final BitSet alive = new BitSet();
        for (int i = 0; i < locals.length; i++) {
            alive.set(length - locals.length + i);
        }
        final long[] alives = BitSetUtils.toLongArray(alive, windows);
        for (int w = 0; w < windows; w++) {
            indexes[w] = idGen.nextId();
            LabelFrameNode.insertBefore(insn, methodNode.instructions, init(length, methodNode, w, alives[w]));
        }

        for (int b = 0; b < basicBlocks.length; b++) {

            final long[] lPotcov = BitSetUtils.toLongArray(potcov[b], windows);
            final long[] lPotcovpuse = BitSetUtils.toLongArray(potcovpuse[b], windows);
            final long[] lBorn = BitSetUtils.toLongArray(born[b], windows);
            final long[] lDisabled = BitSetUtils.toLongArray(disabled[b], windows);
            final long[] lSleepy = BitSetUtils.toLongArray(sleepy[b], windows);

            for (int w = 0; w < windows; w++) {

                final int nPredecessors = predecessors[basicBlocks[b][0]].length;
                final Probe p = probe(length, methodNode, w);

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
                final Integer type = typeOfVars(length);
                for (int i = 0; i < windows; i++) {
                    frame.local.add(type);
                    frame.local.add(type);
                    frame.local.add(type);
                }
            } else if (isReturn(insn.getOpcode())) {
                for (int w = 0; w < windows; w++) {
                    final Probe p = update(length, methodNode, w, indexes[w]);
                    LabelFrameNode.insertBefore(insn, methodNode.instructions, p);
                }
            }
            insn = insn.getNext();
        }

        methodNode.maxLocals = methodNode.maxLocals + windows * numOfVars(length);
        methodNode.maxStack = methodNode.maxStack + 6;
    }

    private int[] getBlocks(final DefUseChain[] localChains, final int[] leaders) {
        final Set<Integer> blocks = new TreeSet<Integer>(); // Using tree set to keep order
        for (final DefUseChain chain : localChains) {
            blocks.add(leaders[chain.def]);
        }
        return ArrayUtils.toArray(blocks, new int[blocks.size()]);
    }

    private Probe init(final int length, final MethodNode methodNode, final int window, final long alive) {
        if (length <= 32) {
            return new IntegerInitProbe(methodNode, (int) alive);
        } else {
            return new LongInitProbe(methodNode, window, alive);
        }
    }

    private Probe probe(final int length, final MethodNode methodNode, final int window) {
        if (length <= 32) {
            return new IntegerProbe(methodNode);
        } else {
            return new LongProbe(methodNode, window);
        }
    }

    private Probe update(final int length, final MethodNode methodNode, final int window, final int index) {
        if (length <= 32) {
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

    private int numOfVars(final int length) {
        if (length <= 32) {
            // three integers
            return 3;
        } else {
            // three longs
            return 6;
        }
    }

    private Integer typeOfVars(final int length) {
        if (length <= 32) {
            // three integers
            return Opcodes.INTEGER;
        } else {
            // three longs
            return Opcodes.LONG;
        }
    }

}
