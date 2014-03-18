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

import br.usp.each.saeg.badua.core.internal.instr.df.DFAnalysis.AnalysisBlock;
import br.usp.each.saeg.opal.Block;
import br.usp.each.saeg.opal.Graph;
import br.usp.each.saeg.opal.requirement.Analyzer;
import br.usp.each.saeg.opal.requirement.CUse;
import br.usp.each.saeg.opal.requirement.Dua;
import br.usp.each.saeg.opal.requirement.PUse;

public class DuaAnalyzer implements Analyzer {

    @Override
    public Dua[] analyze(final Graph<Block> graph, final int vars) {
        final DFAnalysis analysis = new DFAnalysis(graph, vars);
        analysis.computeInAndOut();

        final List<Dua> duas = new ArrayList<Dua>();

        // for each block b
        for (final AnalysisBlock b : analysis) {
            // for each variable v
            for (int v = 0; v < analysis.varSize(); v++) {
                // if v is used in block b
                if (b.block().isCUse(v)) {
                    final CUse use = new CUse(b.block().id);
                    // find blocks (def) where definitions of v are propagated
                    // to b i.e, definitions that reach b
                    for (final AnalysisBlock def : analysis)
                        if (b.in().get(def.pos() * analysis.varSize() + v))
                            duas.add(new Dua(def.block().id, use, v));
                } else if (b.block().isPUse(v)) {
                    for (final AnalysisBlock puse : analysis.neighbors(b.id())) {
                        final PUse use = new PUse(b.block().id, puse.block().id);
                        for (final AnalysisBlock def : analysis)
                            if (b.out().get(def.pos() * analysis.varSize() + v))
                                duas.add(new Dua(def.block().id, use, v));
                    }
                }
            }
        }

        return duas.toArray(new Dua[duas.size()]);
    }

}
