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

import static org.jacoco.core.internal.analysis.CounterImpl.COUNTER_0_1;
import static org.jacoco.core.internal.analysis.CounterImpl.COUNTER_1_0;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class ClassCoverage extends CoverageNode {

    private final Collection<MethodCoverage> methods = new ArrayList<MethodCoverage>();

    public ClassCoverage(final String name) {
        super(name);
        this.classCounter = COUNTER_1_0;
    }

    public Collection<MethodCoverage> getMethods() {
        return Collections.unmodifiableCollection(methods);
    }

    public void addMethod(final MethodCoverage method) {
        methods.add(method);
        increment(method);
        if (methodCounter.getCoveredCount() > 0) {
            classCounter = COUNTER_0_1;
        }
    }

}
