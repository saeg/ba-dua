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

public class Sort1SourceTest extends AbstractSortSourceTest {

    @Override
    public int[] input() {
        return new int[] { 1 };
    }

    @Test
    public void verifyCoveredDU() {
        assertDU(17, 18, 34, "sortupto", true);
        assertDU(17, 18, 34, "n", true);
    }

    @Test
    public void verifyTotal() {
        assertTotal(true, 2);
        assertTotal(false, 47);
    }

}
