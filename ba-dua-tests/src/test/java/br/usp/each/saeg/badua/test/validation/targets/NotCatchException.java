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
package br.usp.each.saeg.badua.test.validation.targets;

public class NotCatchException extends AbstractTarget {

    public static void notCatchRuntimeException1() {
        var = 0;
        if (t()) {
            use(var);
            use(var);
            rtex();
        }
    }

    public static void notCatchRuntimeException2() {
        var = 0;
        if (t()) {
            use(var);
            rtex();
            use(var);
        }
    }

    public static void notCatchRuntimeException3() {
        var = 0;
        if (t()) {
            rtex();
            use(var);
            use(var);
        }
    }

    public static void notCatchException1() throws Ex {
        var = 0;
        if (t()) {
            use(var);
            use(var);
            ex();
        }
    }

    public static void notCatchException2() throws Ex {
        var = 0;
        if (t()) {
            use(var);
            ex();
            use(var);
        }
    }

    public static void notCatchException3() throws Ex {
        var = 0;
        if (t()) {
            ex();
            use(var);
            use(var);
        }
    }

    public static void notCatchError1() throws Err {
        var = 0;
        if (t()) {
            use(var);
            use(var);
            err();
        }
    }

    public static void notCatchError2() throws Err {
        var = 0;
        if (t()) {
            use(var);
            err();
            use(var);
        }
    }

    public static void notCatchError3() throws Err {
        var = 0;
        if (t()) {
            err();
            use(var);
            use(var);
        }
    }

    public static void notCatchThrowable1() throws Thr {
        var = 0;
        if (t()) {
            use(var);
            use(var);
            thr();
        }
    }

    public static void notCatchThrowable2() throws Thr {
        var = 0;
        if (t()) {
            use(var);
            thr();
            use(var);
        }
    }

    public static void notCatchThrowable3() throws Thr {
        var = 0;
        if (t()) {
            thr();
            use(var);
            use(var);
        }
    }

    private static int var;

}
