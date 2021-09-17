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
package br.usp.each.saeg.badua.example;

import java.util.Arrays;

public class ArraysUtilTest {

    public void testMax() {
        final int[] array = new int[] { 1, 2, 3, 2 };
        assert 3 == ArraysUtil.max(array, array.length);
    }

    public void testSort() {
        final int[] array = new int[] { 2, 3, 1 };
        final int[] expected = new int[] { 3, 2, 1 };
        ArraysUtil.sort(array, array.length);
        assert Arrays.equals(expected, array);
    }

}
