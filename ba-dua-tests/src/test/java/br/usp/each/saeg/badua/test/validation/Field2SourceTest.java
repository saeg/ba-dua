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

import br.usp.each.saeg.badua.test.validation.targets.Field;

public class Field2SourceTest extends ValidationTargetsTest {

    public Field2SourceTest() {
        super(Field.class);
    }

    @Override
    public final void run(final Class<?> klass) throws Exception {
        final Object object = klass.newInstance();
        klass.getField("running").setBoolean(object, true);
        klass.getMethod("start").invoke(object);
    }

    @Test
    public void verifyCoveredDU() {
        assertTotal(true, 2);
        assertDU(18, 18, 21, "this", true);
        assertDU(18, 18, 21, "this.running", true);
    }

    @Test
    public void verifyNotCoveredDU() {
        assertTotal(false, 3);
        assertDU(18, 18, 19, "this", false);
        assertDU(18, 18, 19, "this.running", false);
        assertDU(18, 19, "this", false);
    }

}
