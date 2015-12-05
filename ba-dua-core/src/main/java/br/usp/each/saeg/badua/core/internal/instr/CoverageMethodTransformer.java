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
package br.usp.each.saeg.badua.core.internal.instr;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Set;

import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.JumpInsnNode;
import org.objectweb.asm.tree.LabelNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.analysis.AnalyzerException;

import br.usp.each.saeg.asm.defuse.DefUseAnalyzer;
import br.usp.each.saeg.asm.defuse.DefUseChain;
import br.usp.each.saeg.asm.defuse.DefUseFrame;
import br.usp.each.saeg.asm.defuse.DepthFirstDefUseChainSearch;
import br.usp.each.saeg.asm.defuse.Variable;
import br.usp.each.saeg.commons.ArrayUtils;
import br.usp.each.saeg.commons.BitSetUtils;

public class CoverageMethodTransformer extends MethodTransformer {

    private final String className;

    private final IdGenerator idGen;

    private DefUseChain[] chains;

    public CoverageMethodTransformer(final String className, final IdGenerator idGen) {
        this.className = className;
        this.idGen = idGen;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void transform(final MethodNode methodNode) {

        final DefUseAnalyzer analyzer = new DefUseAnalyzer();
        try {
            analyzer.analyze(className, methodNode);
        } catch (final AnalyzerException e) {
            throw new RuntimeException(e);
        }

        final AbstractInsnNode[] insns = methodNode.instructions.toArray();
        final DefUseFrame[] frames = analyzer.getDefUseFrames();
        final Variable[] variables = analyzer.getVariables();
        final int[][] paths = analyzer.getPaths();
        final int[][] successors = analyzer.getSuccessors();
        final int[][] predecessors = analyzer.getPredecessors();
        final int[][] basicBlocks = analyzer.getBasicBlocks();
        final int[] leaders = analyzer.getLeaders();

        final DefUseChain[] chains = DefUseChain.toBasicBlock(new DepthFirstDefUseChainSearch()
                .search(frames, variables, successors, predecessors), leaders, basicBlocks);

        this.chains = chains;
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
        final BitSet[] potcovcuse = new BitSet[basicBlocks.length];
        final BitSet[] potcovpuse = new BitSet[basicBlocks.length];
        final BitSet[] born = new BitSet[basicBlocks.length];
        final BitSet[] disabled = new BitSet[basicBlocks.length];
        for (int b = 0; b < basicBlocks.length; b++) {

            potcovcuse[b] = new BitSet(chains.length);
            potcovpuse[b] = new BitSet(chains.length);
            born[b] = new BitSet(chains.length);
            disabled[b] = new BitSet(chains.length);

            for (int i = 0; i < chains.length; i++) {

                final DefUseChain chain = chains[i];

                if (chain.isPredicateChain() ? chain.target == b : chain.use == b) {
                    if (chain.isPredicateChain()) {
                        potcovpuse[b].set(i);
                    } else {
                        potcovcuse[b].set(i);
                    }
                }

                if (chain.def == b) {
                    born[b].set(i);
                }

                if (chain.def != b && defs[b].contains(variables[chain.var])) {
                    disabled[b].set(i);
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

        final int windows = (chains.length + 63) / 64;
        final int[] indexes = new int[windows];
        for (int w = 0; w < windows; w++) {
            indexes[w] = idGen.nextId();
            methodNode.instructions.insert(init(methodNode, w));
        }

        for (int i = 0; i < paths.length; i++) {
            final int[] path = paths[i];
            if (path != null) {
                final long[] lCovered = BitSetUtils.toLongArray(
                        cov(path, potcovcuse, potcovpuse, born, disabled), windows);

                final long[] lPotcov = BitSetUtils.toLongArray(
                        potcov(path, potcovcuse, potcovpuse), windows);

                final long[] lBorn = BitSetUtils.toLongArray(
                        orThenAndNot(path, born, disabled), windows);

                final long[] lDisabled = BitSetUtils.toLongArray(
                        orThenAndNot(path, disabled, born), windows);

                final InsnList probes = new InsnList();
                for (int w = 0; w < windows; w++) {
                    final Probe p = probe(methodNode, w);
                    p.potcov = lPotcov[w];
                    p.born = lBorn[w];
                    p.disabled = lDisabled[w];
                    p.covered = lCovered[w];
                    probes.add(p);
                }

                final int lastBlock = path[path.length - 1];
                final AbstractInsnNode lastInsn = last[lastBlock];
                if (InstrSupport.isPredicate(lastInsn.getOpcode())) {
                    final JumpInsnNode jmpInsn = (JumpInsnNode) lastInsn;
                    if (predecessors[ArrayUtils.indexOf(insns, jmpInsn.getNext())].length > 1) {
                        final int next = leaders[ArrayUtils.indexOf(insns, jmpInsn.getNext())];
                        final long[] lPotcovPuse = BitSetUtils.toLongArray(potcovpuse[next], windows);
                        for (int w = 0; w < windows; w++) {
                            final Probe p = (Probe) probes.get(w);
                            p.potcov |= lPotcovPuse[w];
                        }
                        methodNode.instructions.insertBefore(lastInsn, probes);
                    }
                    if (predecessors[ArrayUtils.indexOf(insns, jmpInsn.label)].length > 1) {
                        final int target = leaders[ArrayUtils.indexOf(insns, jmpInsn.label)];
                        final long[] lPotcovPuse = BitSetUtils.toLongArray(potcovpuse[target], windows);
                        for (int w = 0; w < windows; w++) {
                            final Probe p = (Probe) probes.get(w);
                            p.potcov |= lPotcovPuse[w];
                        }
                        final InsnList probeJmp = new InsnList();
                        final LabelNode intermediate = new LabelNode();
                        probeJmp.add(new JumpInsnNode(InstrSupport.getInverted(jmpInsn.getOpcode()), intermediate));
                        probeJmp.add(probes);
                        probeJmp.add(new JumpInsnNode(Opcodes.GOTO, jmpInsn.label));
                        probeJmp.add(intermediate);
                        methodNode.instructions.insert(lastInsn, probeJmp);
                        methodNode.instructions.remove(lastInsn);
                    }
                } else if (isReturn(lastInsn.getOpcode()) || InstrSupport.isGOTO(lastInsn.getOpcode())) {
                    methodNode.instructions.insertBefore(lastInsn, probes);
                } else {
                    methodNode.instructions.insert(lastInsn, probes);
                }

                if (isReturn(lastInsn.getOpcode())) {
                    for (int w = 0; w < windows; w++) {
                        final Probe p = update(methodNode, w, indexes[w]);
                        methodNode.instructions.insertBefore(lastInsn, p);
                    }
                }
            }
        }

        // Finally, update the frames and add exit probes
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
                final Integer type = typeOfVars();
                for (int i = 0; i < windows; i++) {
                    frame.local.add(type);
                    frame.local.add(type);
                }
            } else if (isReturn(insn.getOpcode())) {
                for (int w = 0; w < windows; w++) {
                    final Probe p = update(methodNode, w, indexes[w]);
                    LabelFrameNode.insertBefore(insn, methodNode.instructions, p);
                }
            }
            insn = insn.getNext();
        }

        methodNode.maxLocals = methodNode.maxLocals + windows * numOfVars();
        methodNode.maxStack = methodNode.maxStack + 6;
    }

    private BitSet cov(final int[] path,
                       final BitSet[] potcovcuse,
                       final BitSet[] potcovpuse,
                       final BitSet[] born,
                       final BitSet[] disabled) {

        final BitSet alive = new BitSet(chains.length);
        final BitSet covered = new BitSet(chains.length);
        for (final int b : path) {
            final BitSet potcov = new BitSet(chains.length);
            potcov.or(potcovcuse[b]);
            potcov.or(potcovpuse[b]);

            alive.and(potcov);
            covered.or(alive);
            alive.andNot(disabled[b]);
            alive.or(born[b]);
        }
        return covered;
    }

    private BitSet potcov(final int[] path,
                          final BitSet[] potcovcuse,
                          final BitSet[] potcovpuse) {

        final BitSet result = new BitSet(chains.length);
        result.or(potcovcuse[path[0]]);
        for (int i = 1; i < path.length; i++) {
            result.or(potcovcuse[path[i]]);
            result.or(potcovpuse[path[i]]);
        }
        return result;
    }

    private BitSet orThenAndNot(final int path[],
                                final BitSet[] or,
                                final BitSet[] andNot) {

        final BitSet result = new BitSet(chains.length);
        for (final int b : path) {
            result.or(or[b]);
            result.andNot(andNot[b]);
        }
        return result;
    }

    private Probe init(final MethodNode methodNode, final int window) {
        if (chains.length <= 32) {
            return new IntegerInitProbe(methodNode);
        } else {
            return new LongInitProbe(methodNode, window);
        }
    }

    private Probe probe(final MethodNode methodNode, final int window) {
        if (chains.length <= 32) {
            return new IntegerProbe(methodNode);
        } else {
            return new LongProbe(methodNode, window);
        }
    }

    private Probe update(final MethodNode methodNode, final int window, final int index) {
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

    private int numOfVars() {
        if (chains.length <= 32) {
            // two integers
            return 2;
        } else {
            // two longs
            return 4;
        }
    }

    private Integer typeOfVars() {
        if (chains.length <= 32) {
            return Opcodes.INTEGER;
        } else {
            return Opcodes.LONG;
        }
    }

    public DefUseChain[] getDefUseChains() {
        return chains.clone();
    }

}
