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
import java.security.CodeSource;
import java.util.jar.JarFile;

import br.usp.each.saeg.badua.core.runtime.IExecutionDataAccessorGenerator;
import br.usp.each.saeg.badua.core.runtime.IRuntime;
import br.usp.each.saeg.badua.core.runtime.ModifiedSystemClassRuntime;

public final class PreMain {

    private PreMain() {
        // No instances
    }

    public static void premain(final String opts, final Instrumentation inst) throws Exception {
        final CodeSource codeSource = PreMain.class.getProtectionDomain().getCodeSource();
        inst.appendToBootstrapClassLoaderSearch(new JarFile(codeSource.getLocation().getPath()));

        Init.init(inst);
    }

    /**
     *
     * I don't know if this is the correct way to avoid LinkageError.
     *
     * If code is in same method {@link IExecutionDataAccessorGenerator}
     * will be loaded by the application class loader before it was
     * appended to bootstrap class loader. So a LinkageError will be
     * thrown as we expect a class loaded by the bootstrap class loader.
     *
     * Since everything is in a method from another class it doesn't
     * happen. But again, IDK if this is the correct approach.
     *
     */
    public static class Init {
        public static void init(final Instrumentation inst) throws Exception {
            final IRuntime runtime = ModifiedSystemClassRuntime.createFor(inst, "java/lang/UnknownError");
            runtime.startup(Agent.getInstance().getData());
            inst.addTransformer(new CoverageTransformer(runtime, PreMain.class.getPackage().getName()));
        }
    }

}
