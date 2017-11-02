/**
 * Copyright (c) 2014, 2017 University of Sao Paulo and Contributors.
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

public class Max7SourceTest extends AbstractMaxSourceTest {

    @Override
    public int[] input() {
        return new int[] { 1, 2, 3, 2 };
    }

    @Test
    public void verifyCoveredDU() {
        assertDU(17, 18, 19, "i", true);
        assertDU(16, 18, 19, "length", true);
        assertDU(16, 19, 20, "array", true);
        assertDU(17, 19, 20, "i", true);
        assertDU(17, 19, 20, "max", true);
        assertDU(16, 20, "array", true);
        assertDU(17, 20, "i", true);
        assertDU(17, 22, "i", true);
        assertDU(22, 18, 19, "i", true);
        assertDU(22, 19, 20, "i", true);
        assertDU(20, 19, 20, "max", true);
        assertDU(22, 20, "i", true);
        assertDU(22, 22, "i", true);
        assertDU(16, 19, 22, "array", true);
        assertDU(22, 19, 22, "i", true);
        assertDU(20, 19, 22, "max", true);
        assertDU(22, 18, 24, "i", true);
        assertDU(16, 18, 24, "length", true);
        assertDU(20, 24, "max", true);
        assertDU(16, 17, "array", true);
        assertDU(16, 17, "i", true);
        assertDU(17, 17, "i", true);
    }

    @Test
    public void verifyNotCoveredDU() {
        assertDU(13, 13, "this", false);
    }

    @Test
    public void verifyTotal() {
        assertTotal(true, 22);
        assertTotal(false, 5);
    }

}
