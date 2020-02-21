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

import org.junit.Test;

public class Sort8SourceTest extends AbstractSortSourceTest {

    @Override
    public int[] input() {
        return new int[] { 3, 1, 2 };
    }

    @Test
    public void verifyCoveredDU() {
        assertDU(17, 18, 19, "sortupto", true);
        assertDU(17, 18, 19, "n", true);
        assertDU(17, 19, "a", true);
        assertDU(17, 19, "sortupto", true);
        assertDU(17, 20, "sortupto", true);
        assertDU(17, 21, "sortupto", true);
        assertDU(21, 22, 23, "index", true);
        assertDU(17, 22, 23, "n", true);
        assertDU(17, 23, 27, "a", true);
        assertDU(21, 23, 27, "index", true);
        assertDU(19, 23, 27, "mymax", true);
        assertDU(21, 27, "index", true);
        assertDU(27, 22, 23, "index", true);
        assertDU(27, 23, 27, "index", true);
        assertDU(27, 27, "index", true);
        assertDU(27, 22, 29, "index", true);
        assertDU(17, 22, 29, "n", true);
        assertDU(17, 29, "a", true);
        assertDU(17, 29, "sortupto", true);
        assertDU(17, 30, "a", true);
        assertDU(17, 30, "sortupto", true);
        assertDU(19, 30, "mymax", true);
        assertDU(17, 31, "a", true);
        assertDU(20, 31, "maxpos", true);
        assertDU(17, 32, "sortupto", true);
        assertDU(32, 18, 19, "sortupto", true);
        assertDU(32, 19, "sortupto", true);
        assertDU(32, 20, "sortupto", true);
        assertDU(32, 21, "sortupto", true);
        assertDU(17, 23, 24, "a", true);
        assertDU(21, 23, 24, "index", true);
        assertDU(19, 23, 24, "mymax", true);
        assertDU(17, 24, "a", true);
        assertDU(21, 24, "index", true);
        assertDU(21, 25, "index", true);
        assertDU(32, 29, "sortupto", true);
        assertDU(32, 30, "sortupto", true);
        assertDU(24, 30, "mymax", true);
        assertDU(25, 31, "maxpos", true);
        assertDU(32, 32, "sortupto", true);
        assertDU(32, 18, 34, "sortupto", true);
        assertDU(17, 18, 34, "n", true);
    }

    @Test
    public void verifyTotal() {
        assertTotal(true, 42);
        assertTotal(false, 7);
    }

}
