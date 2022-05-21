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

import java.lang.reflect.InvocationTargetException;

import org.junit.Test;

import br.usp.each.saeg.badua.test.validation.targets.JumpToFirstInstruction;

public class JumpToFirstInstructionSourceTest extends ValidationTargetsTest {

    public JumpToFirstInstructionSourceTest() {
        super(JumpToFirstInstruction.class);
    }

    @Override
    public final void run(final Class<?> klass) throws Exception {
        klass.getMethod("run1").invoke(null);
        klass.getMethod("run2").invoke(null);
        try {
            klass.getMethod("run3").invoke(null);
        } catch (final InvocationTargetException ignore) {
        }
        try {
            klass.getMethod("run4").invoke(null);
        } catch (final InvocationTargetException ignore) {
        }

    }

    @Test
    public void test() {
        assertTotal(true, 2);
        assertTotal(false, 2);
        assertDU(19, 21, "var", true);
        assertDU(28, 30, "var", false);
        assertDU(37, 39, "var", true);
        assertDU(47, 49, "var", false);
    }

}
