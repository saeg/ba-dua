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
    public void verifyTotal() {
        assertTotal(true, 0);
        assertTotal(false, 23);
    }

}
