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

import org.junit.Assert;
import org.junit.Test;

import br.usp.each.saeg.badua.test.validation.targets.AbstractTarget.Err;
import br.usp.each.saeg.badua.test.validation.targets.AbstractTarget.Ex;
import br.usp.each.saeg.badua.test.validation.targets.AbstractTarget.RTEx;
import br.usp.each.saeg.badua.test.validation.targets.AbstractTarget.Thr;
import br.usp.each.saeg.badua.test.validation.targets.NotCatchException;

public class NotCatchExceptionSourceTest extends ValidationTargetsTest {

    public NotCatchExceptionSourceTest() {
        super(NotCatchException.class);
    }

    private static <E extends Throwable> void call(final Class<?> klass, final String name, final Class<E> exClass)
            throws IllegalArgumentException, SecurityException, IllegalAccessException, NoSuchMethodException {

        E ret = null;
        try {
            // Invoke a static method without arguments
            klass.getMethod(name).invoke(null);
        } catch (final InvocationTargetException e) {
            // We expect this method to throw an exception of E type
            ret = exClass.cast(e.getTargetException());
        }
        // Ensure an exception was thrown
        Assert.assertNotNull(ret);
        // And the type is the same
        Assert.assertEquals(exClass, ret.getClass());
    }

    @Override
    public final void run(final Class<?> klass) throws Exception {
        call(klass, "notCatchRuntimeException1", RTEx.class);
        call(klass, "notCatchRuntimeException2", RTEx.class);
        call(klass, "notCatchRuntimeException3", RTEx.class);
        call(klass, "notCatchException1", Ex.class);
        call(klass, "notCatchException2", Ex.class);
        call(klass, "notCatchException3", Ex.class);
        call(klass, "notCatchError1", Err.class);
        call(klass, "notCatchError2", Err.class);
        call(klass, "notCatchError3", Err.class);
        call(klass, "notCatchThrowable1", Thr.class);
        call(klass, "notCatchThrowable2", Thr.class);
        call(klass, "notCatchThrowable3", Thr.class);
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
        assertTotal(true, 24); // <--- The correct value is 12
        assertTotal(false, 0); // <--- The correct value is 12
        assertDU(16, 18, "var", true);
        assertDU(16, 19, "var", true);
        assertDU(25, 27, "var", true);
        assertDU(25, 29, "var", true); // <--- wrong here, exception before the use
        assertDU(34, 37, "var", true); // <--- wrong here, exception before the use
        assertDU(34, 38, "var", true); // <--- wrong here, exception before the use
        assertDU(43, 45, "var", true);
        assertDU(43, 46, "var", true);
        assertDU(52, 54, "var", true);
        assertDU(52, 56, "var", true); // <--- wrong here, exception before the use
        assertDU(61, 64, "var", true); // <--- wrong here, exception before the use
        assertDU(61, 65, "var", true); // <--- wrong here, exception before the use
        assertDU(70, 72, "var", true);
        assertDU(70, 73, "var", true);
        assertDU(79, 81, "var", true);
        assertDU(79, 83, "var", true); // <--- wrong here, exception before the use
        assertDU(88, 91, "var", true); // <--- wrong here, exception before the use
        assertDU(88, 92, "var", true); // <--- wrong here, exception before the use
        assertDU(97, 99, "var", true);
        assertDU(97, 100, "var", true);
        assertDU(106, 108, "var", true);
        assertDU(106, 110, "var", true); // <--- wrong here, exception before the use
        assertDU(115, 118, "var", true); // <--- wrong here, exception before the use
        assertDU(115, 119, "var", true); // <--- wrong here, exception before the use
    }

}
