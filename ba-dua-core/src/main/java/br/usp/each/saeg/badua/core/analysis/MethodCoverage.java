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

import static org.jacoco.core.internal.analysis.CounterImpl.COUNTER_0_1;
import static org.jacoco.core.internal.analysis.CounterImpl.COUNTER_1_0;

import org.jacoco.core.analysis.ICounter;

public class MethodCoverage extends CoverageNode {

    private final String desc;

    public MethodCoverage(final String name, final String desc) {
        super(name);
        this.desc = desc;
        this.methodCounter = COUNTER_1_0;
    }

    public String getDesc() {
        return desc;
    }

    public void increment(final ICounter counter) {
        duCounter = duCounter.increment(counter);
        if (duCounter.getCoveredCount() > 0) {
            methodCounter = COUNTER_0_1;
        }
    }

}
