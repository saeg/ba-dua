/**
 * Copyright (c) 2014, 2020 University of Sao Paulo and Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Roberto Araujo - initial API and implementation and/or initial documentation
 */
package br.usp.each.saeg.badua.core.analysis;

public class SourceLineDefUseChain {

    public static final int UNKNOWN_LINE = -1;

    public static final int NONE = -2;

    public final int def;

    public final int use;

    public final int target;

    public final String var;

    public final boolean covered;

    public SourceLineDefUseChain(final int def,
                                 final int use,
                                 final String var,
                                 final boolean covered) {

        this(def, use, NONE, var, covered);
    }

    public SourceLineDefUseChain(final int def,
                                 final int use,
                                 final int target,
                                 final String var,
                                 final boolean covered) {
        this.def = def;
        this.use = use;
        this.var = var;
        this.target = target;
        this.covered = covered;
    }

}
