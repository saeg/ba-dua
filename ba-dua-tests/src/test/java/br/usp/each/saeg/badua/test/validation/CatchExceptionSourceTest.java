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

import br.usp.each.saeg.badua.test.validation.targets.CatchException;

public class CatchExceptionSourceTest extends ValidationTargetsTest {

    public CatchExceptionSourceTest() {
        super(CatchException.class);
    }

    @Override
    public final void run(final Class<?> klass) throws Exception {
        klass.getMethod("run").invoke(null);
    }

    @Test
    public void test() {
        /**
         * This test includes some wrong assertions. Using the test only to
         * detect possible unexpected changes in the current behavior
         *
         * This is a known limitations due to exception flows
         *
         * In some future version we will address these issues
         */
        assertTotal(true, 6); // <--- The correct value is 3
        assertTotal(false, 0); // <--- The correct value is 3
        assertDU(22, 25, "var", true);
        assertDU(22, 26, "var", true);
        assertDU(34, 37, "var", true);
        assertDU(34, 39, "var", true); // <--- wrong here, exception before the use
        assertDU(46, 50, "var", true); // <--- wrong here, exception before the use
        assertDU(46, 51, "var", true); // <--- wrong here, exception before the use
    }

}
