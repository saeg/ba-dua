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

import br.usp.each.saeg.opal.Block;
import br.usp.each.saeg.opal.requirement.Dua;
import br.usp.each.saeg.opal.requirement.Use.Type;

public class BitSets {

    public final BitSet potcov;
    public final BitSet born;
    public final BitSet disabled;
    public final BitSet sleepy;
    public final BitSet potcovpuse;

    public BitSets(final Dua[] duas, final Block n) {

        potcov = new BitSet(duas.length);
        born = new BitSet(duas.length);
        disabled = new BitSet(duas.length);
        sleepy = new BitSet(duas.length);
        potcovpuse = new BitSet(duas.length);

        for (int i = 0; i < duas.length; i++) {

            final Dua dua = duas[i];

            // potentially(n) covered definition-use-associations
            if (dua.use.id() == n.id) {
                potcov.set(i);
                if (dua.use.type == Type.P_USE) {
                    potcovpuse.set(i);
                }
            }

            // born(n) definition-use-associations
            if (dua.def == n.id) {
                born.set(i);
            }

            // disabled(n) definition-use-associations
            if (dua.def != n.id && n.isDef(dua.var))
                disabled.set(i);

            // sleepy(n) definition-use-associations
            if (dua.use.type == Type.P_USE) {
                final int origin = dua.use.puse().from;
                if (origin != n.id) {
                    sleepy.set(i);
                }
            }

        }
    }

}
