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

import br.usp.each.saeg.badua.test.validation.targets.Sort;

public abstract class AbstractSortSourceTest extends ValidationTargetsTest {

    public AbstractSortSourceTest() {
        super(Sort.class);
    }

    @Override
    public final void run(final Class<?> klass) throws Exception {
        sort(klass, input());
    }

    public abstract int[] input();

    private void sortLength(final Class<?> klass, final int[] array, final int length) throws Exception {
        klass.getMethod("sort", int[].class, int.class).invoke(null, array, length);
    }

    private void sort(final Class<?> klass, final int[] array) throws Exception {
        sortLength(klass, array, array.length);
    }

}
