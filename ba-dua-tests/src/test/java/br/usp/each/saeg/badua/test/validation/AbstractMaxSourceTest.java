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
package br.usp.each.saeg.badua.test.validation;

import br.usp.each.saeg.badua.test.validation.targets.Max;

public abstract class AbstractMaxSourceTest extends ValidationTargetsTest {

    public AbstractMaxSourceTest() {
        super(Max.class);
    }

    @Override
    public final void run(final Class<?> klass) throws Exception {
        max(klass, input());
    }

    public abstract int[] input();

    private int maxLength(final Class<?> klass, final int[] array, final int length) {
        try {
            return (Integer) klass.getMethod("max", int[].class, int.class).invoke(null, array, length);
        } catch (final Exception ignore) {
            throw new RuntimeException(ignore.getCause());
        }
    }

    private int max(final Class<?> klass, final int[] array) {
        return maxLength(klass, array, array.length);
    }

}
