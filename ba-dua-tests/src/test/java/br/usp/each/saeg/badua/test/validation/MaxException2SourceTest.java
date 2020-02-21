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

import org.junit.Assert;
import org.junit.Test;

public class MaxException2SourceTest extends AbstractMaxSourceTest {

    private Throwable exception;

    @Override
    public int[] input() {
        return new int[] { 1, 2, 3 };
    }

    @Override
    protected int size(final int[] array) {
        // wrong size
        return super.size(array) + 1;
    }

    @Override
    protected void handle(final Throwable e) throws Exception {
        // Ignore exception, but assert was thrown
        exception = e;
    }

    @Test
    public void exceptionWasThrown() {
        Assert.assertNotNull(exception);
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
    }

    @Test
    public void verifyTotal() {
        assertTotal(true, 13);
        assertTotal(false, 10);
    }

}
