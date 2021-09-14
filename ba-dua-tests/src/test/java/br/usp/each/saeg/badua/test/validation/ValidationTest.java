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
package br.usp.each.saeg.badua.test.validation;

import java.io.IOException;

import br.usp.each.saeg.badua.core.instr.Instrumenter;
import br.usp.each.saeg.badua.core.runtime.RuntimeData;
import br.usp.each.saeg.badua.core.runtime.StaticAccessGenerator;

public abstract class ValidationTest {

    public static RuntimeData DATA;

    public static long[] getData(final long classId, final String className, final int size) {
        return DATA.getExecutionData(classId, className, size).getData();
    }

    protected ValidationTestClassLoader loader;

    public void setUp() throws Exception {
        loader = new ValidationTestClassLoader();
        DATA = new RuntimeData();
    }

    public Class<?> addClass(final String name, final byte[] bytes) {
        return loader.add(name, instrument(name, bytes));
    }

    private byte[] instrument(final String name, final byte[] bytes) {
        final Instrumenter instrumenter = new Instrumenter(
                new StaticAccessGenerator(ValidationTest.class.getName()));

        try {
            return instrumenter.instrument(bytes, name);
        } catch (final IOException ignore) {
            /* never happens */
            throw new RuntimeException(ignore);
        }
    }

}
