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

import br.usp.each.saeg.badua.test.validation.targets.Max;

public abstract class AbstractMaxSourceTest extends ValidationTargetsTest {

    public AbstractMaxSourceTest() {
        super(Max.class);
    }

    @Override
    public final void run(final Class<?> klass) throws Exception {
        try {
            max(klass, input());
        } catch (final Exception e) {
            handle(e.getCause());
        }
    }

    protected int size(final int[] array) {
        return array.length;
    }

    protected void handle(final Throwable e) throws Exception {
        if (e instanceof Exception) {
            throw (Exception) e;
        }
        throw new Exception(e);
    }

    public abstract int[] input();

    private int maxLength(final Class<?> klass, final int[] array, final int length) throws Exception {
        return (Integer) klass.getMethod("max", int[].class, int.class).invoke(null, array, length);
    }

    private int max(final Class<?> klass, final int[] array) throws Exception {
        return maxLength(klass, array, size(array));
    }

}
