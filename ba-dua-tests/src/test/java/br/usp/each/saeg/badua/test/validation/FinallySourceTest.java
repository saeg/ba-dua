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

import br.usp.each.saeg.badua.test.validation.targets.Finally;

public class FinallySourceTest extends ValidationTargetsTest {

    public FinallySourceTest() {
        super(Finally.class);
    }

    @Override
    public final void run(final Class<?> klass) throws Exception {
        klass.getMethod("run").invoke(null);
    }

    @Test
    public void test() {
        assertTotal(true, 3);
        assertTotal(false, 1);
        assertDU(40, 45, "var", true);
        assertDU(51, 57, "var", true);
        assertDU(73, 80, "var", true);
        assertDU(86, 94, "var", false);
        
    }

}
