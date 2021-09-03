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

import java.io.IOException;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

import br.usp.each.saeg.badua.core.instr.Instrumenter;
import br.usp.each.saeg.badua.core.runtime.IExecutionDataAccessorGenerator;

public class CoverageTransformer implements ClassFileTransformer {

    private final String skipPackageName;

    private final Instrumenter instrumenter;

    public CoverageTransformer(
            final IExecutionDataAccessorGenerator accessorGenerator, final String skipPackageName) {
        this.skipPackageName = skipPackageName.replace('.', '/');
        instrumenter = new Instrumenter(accessorGenerator);
    }

    @Override
    public byte[] transform(final ClassLoader loader,
                            final String className,
                            final Class<?> classBeingRedefined,
                            final ProtectionDomain protectionDomain,
                            final byte[] classfileBuffer) throws IllegalClassFormatException {

        if (skip(loader, className)) {
            return null;
        }
        try {
            return instrumenter.instrument(classfileBuffer, className);
        } catch (final IOException e) {
            final IllegalClassFormatException ex = new IllegalClassFormatException(e.getMessage());
            ex.initCause(e);
            throw ex;
        }
    }

    private boolean skip(final ClassLoader loader, final String className) {
        return loader == null
                || loader.getClass().getName().equals("sun.reflect.DelegatingClassLoader")
                || loader.getClass().getName().equals("sun.misc.Launcher$ExtClassLoader")
                || className.startsWith(skipPackageName);
    }

}
