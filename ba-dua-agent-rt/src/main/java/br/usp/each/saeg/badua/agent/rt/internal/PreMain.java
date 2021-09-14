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
package br.usp.each.saeg.badua.agent.rt.internal;

import java.lang.instrument.Instrumentation;

import br.usp.each.saeg.badua.core.runtime.IRuntime;
import br.usp.each.saeg.badua.core.runtime.ModifiedSystemClassRuntime;

public final class PreMain {

    private PreMain() {
        // No instances
    }

    public static void premain(final String opts, final Instrumentation inst) throws Exception {
        final IRuntime runtime = ModifiedSystemClassRuntime.createFor(inst, "java/lang/UnknownError");
        runtime.startup(Agent.getInstance().getData());
        inst.addTransformer(new CoverageTransformer(runtime, PreMain.class.getPackage().getName()));
    }

}
