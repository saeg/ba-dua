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

import java.util.BitSet;

import br.usp.each.saeg.badua.core.internal.instr.df.DFAnalysis.AnalysisBlock;
import br.usp.each.saeg.opal.Block;
import br.usp.each.saeg.opal.Graph;
import br.usp.each.saeg.opal.Identifiable;

public class DFAnalysis extends Graph<AnalysisBlock> {

    private final int vars;

    public DFAnalysis(final Graph<Block> graph, final int vars) {
        this.vars = vars;

        // Add nodes before (to maintain the same order)
        for (final Block from : graph) {
            add(new AnalysisBlock(from));
        }

        // Add Edges
        for (final Block from : graph) {
            for (final Block to : graph.neighbors(from.id)) {
                addEdge(from.id, to.id);
            }
        }
    }

    private void init() {
        int pos = 0;
        for (final AnalysisBlock b : this) {
            b.in = new BitSet(vars * size());
            b.out = new BitSet(vars * size());
            b.gen = new BitSet(vars * size());
            b.kill = new BitSet(vars * size());
            b.pos = pos;
            pos++;
        }
        for (final AnalysisBlock b : this) {
            computeGenAndKill(b);
            b.out.or(b.gen);
        }
    }

    public void computeInAndOut() {
        init();

        // I use inverse to compute predecessors
        final Graph<AnalysisBlock> inverse = inverse();

        boolean changed = true;
        while (changed) {
            changed = false;
            for (final AnalysisBlock b : this) {

                // in[B] := U out[P] | P a predecessor of B
                b.in.clear();
                for (final AnalysisBlock pred : inverse.neighbors(b.id()))
                    b.in.or(pred.out);

                // oldout := out
                final BitSet oldout = new BitSet(vars * size());
                oldout.or(b.out);

                // out[B] := gen[B] U (in[B] - kill[B])
                final BitSet temp = new BitSet(vars * size());
                temp.or(b.in);
                temp.andNot(b.kill); // temp := in[B] - kill[B] ;)

                b.out.clear();
                b.out.or(b.gen);
                b.out.or(temp); // out[B] := gen[B] U temp

                if (!b.out.equals(oldout))
                    changed = true;
            }
        }
    }

    /*
     * This method can be a little confused, so...
     * 
     * For each variable in the Graph, check if block B define the current
     * variable V. If YES set the 'pair' (V, B) as true in the "gen" and for all
     * others block B', where V is definied, set the 'pair' (V, B') as true in
     * the "kill".
     */
    private void computeGenAndKill(final AnalysisBlock b) {
        // For each variable...
        for (int i = 0; i < vars; i++) {
            if (b.block.isDef(i)) {
                b.gen.set(b.pos * vars + i);
                for (final AnalysisBlock other : this) {
                    if (other != b && other.block.isDef(i)) {
                        b.kill.set(other.pos * vars + i);
                    }
                }
            }
        }
    }

    public int varSize() {
        return vars;
    }

    public static class AnalysisBlock implements Identifiable {

        private int pos;
        private BitSet in;
        private BitSet out;
        private BitSet gen;
        private BitSet kill;
        private final Block block;

        public AnalysisBlock(final Block block) {
            this.block = block;
        }

        @Override
        public int id() {
            return block.id;
        }

        public BitSet in() {
            return in;
        }

        public BitSet out() {
            return out;
        }

        public Block block() {
            return block;
        }

        public int pos() {
            return pos;
        }

    }

}
