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

public class Max3SourceTest extends AbstractMaxSourceTest {

    @Override
    public int[] input() {
        return new int[] { 2, 1 };
    }

    @Test
    public void verifyCoveredDU() {
        assertDU(17, 18, 19, "i", true);
        assertDU(16, 18, 19, "length", true);
        assertDU(16, 19, 22, "array", true);
        assertDU(17, 19, 22, "i", true);
        assertDU(17, 19, 22, "max", true);
        assertDU(17, 22, "i", true);
        assertDU(22, 18, 24, "i", true);
        assertDU(16, 18, 24, "length", true);
        assertDU(17, 24, "max", true);
    }

    @Test
    public void verifyTotal() {
        assertTotal(true, 9);
        assertTotal(false, 14);
    }

}
