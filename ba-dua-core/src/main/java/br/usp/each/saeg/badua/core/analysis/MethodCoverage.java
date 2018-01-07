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

import static org.jacoco.core.internal.analysis.CounterImpl.COUNTER_0_1;
import static org.jacoco.core.internal.analysis.CounterImpl.COUNTER_1_0;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import org.jacoco.core.analysis.ICounter;

public class MethodCoverage extends CoverageNode {

    public static final int UNKNOWN_LINE = -1;

    private final Collection<SourceLineDefUseChain> defUses = new ArrayList<SourceLineDefUseChain>();

    private final String desc;

    public MethodCoverage(final String name, final String desc) {
        super(name);
        this.desc = desc;
        this.methodCounter = COUNTER_1_0;
    }

    public String getDesc() {
        return desc;
    }

    public Collection<SourceLineDefUseChain> getDefUses() {
        return Collections.unmodifiableCollection(defUses);
    }

    public void increment(final ICounter counter) {
        duCounter = duCounter.increment(counter);
        if (duCounter.getCoveredCount() > 0) {
            methodCounter = COUNTER_0_1;
        }
    }

    public void increment(final int def, final int use, final String var, final boolean covered) {
        if (def != UNKNOWN_LINE && use != UNKNOWN_LINE && var != null) {
            defUses.add(new SourceLineDefUseChain(def, use, var, covered));
        }
        increment(covered ? COUNTER_0_1 : COUNTER_1_0);
    }

    public void increment(final int def, final int use, final int target, final String var, final boolean covered) {
        if (def != UNKNOWN_LINE && use != UNKNOWN_LINE && target != UNKNOWN_LINE && var != null) {
            defUses.add(new SourceLineDefUseChain(def, use, target, var, covered));
        }
        increment(covered ? COUNTER_0_1 : COUNTER_1_0);
    }

}
