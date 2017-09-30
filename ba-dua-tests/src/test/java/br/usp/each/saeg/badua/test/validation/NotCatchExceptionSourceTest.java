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

    @Override
    public final void run(final Class<?> klass) throws Exception {

        /**
         * Runtime Exceptions (not checked)
         */

        RTEx rtex = null;
        try {
            klass.getMethod("notCatchRuntimeException1").invoke(null);
        } catch (final InvocationTargetException e) {
            rtex = (RTEx) e.getTargetException();
        }
        Assert.assertNotNull(rtex);

        rtex = null;
        try {
            klass.getMethod("notCatchRuntimeException2").invoke(null);
        } catch (final InvocationTargetException e) {
            rtex = (RTEx) e.getTargetException();
        }
        Assert.assertNotNull(rtex);

        rtex = null;
        try {
            klass.getMethod("notCatchRuntimeException3").invoke(null);
        } catch (final InvocationTargetException e) {
            rtex = (RTEx) e.getTargetException();
        }
        Assert.assertNotNull(rtex);

        /**
         * Exceptions (checked)
         */

        Ex ex = null;
        try {
            klass.getMethod("notCatchException1").invoke(null);
        } catch (final InvocationTargetException e) {
            ex = (Ex) e.getTargetException();
        }
        Assert.assertNotNull(ex);

        ex = null;
        try {
            klass.getMethod("notCatchException2").invoke(null);
        } catch (final InvocationTargetException e) {
            ex = (Ex) e.getTargetException();
        }
        Assert.assertNotNull(ex);

        ex = null;
        try {
            klass.getMethod("notCatchException3").invoke(null);
        } catch (final InvocationTargetException e) {
            ex = (Ex) e.getTargetException();
        }
        Assert.assertNotNull(ex);

        /**
         * Errors
         */

        Err err = null;
        try {
            klass.getMethod("notCatchError1").invoke(null);
        } catch (final InvocationTargetException e) {
            err = (Err) e.getTargetException();
        }
        Assert.assertNotNull(err);

        ex = null;
        try {
            klass.getMethod("notCatchError2").invoke(null);
        } catch (final InvocationTargetException e) {
            err = (Err) e.getTargetException();
        }
        Assert.assertNotNull(err);

        ex = null;
        try {
            klass.getMethod("notCatchError3").invoke(null);
        } catch (final InvocationTargetException e) {
            err = (Err) e.getTargetException();
        }
        Assert.assertNotNull(err);

        /**
         * Throwable
         */

        Thr thr = null;
        try {
            klass.getMethod("notCatchThrowable1").invoke(null);
        } catch (final InvocationTargetException e) {
            thr = (Thr) e.getTargetException();
        }
        Assert.assertNotNull(thr);

        ex = null;
        try {
            klass.getMethod("notCatchThrowable2").invoke(null);
        } catch (final InvocationTargetException e) {
            thr = (Thr) e.getTargetException();
        }
        Assert.assertNotNull(thr);

        ex = null;
        try {
            klass.getMethod("notCatchThrowable3").invoke(null);
        } catch (final InvocationTargetException e) {
            thr = (Thr) e.getTargetException();
        }
        Assert.assertNotNull(thr);
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
