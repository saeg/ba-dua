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

import org.junit.Assert;
import org.junit.Test;

public class MaxException1SourceTest extends AbstractMaxSourceTest {

    private Throwable exception;

    @Override
    public int[] input() {
        return new int[] { };
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
        assertTotal(true, 3);
        assertTotal(false, 24);
    }

}
