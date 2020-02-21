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

public class CatchException extends AbstractTarget {

    public static void run() {
        catchException1();
        catchException2();
        catchException3();
    }

    private static void catchException1() {
        var = 0;
        if (t()) {
            try {
                use(var);
                use(var);
                rtex();
            } catch (final RTEx e) {
            }
        }
    }

    private static void catchException2() {
        var = 0;
        if (t()) {
            try {
                use(var);
                rtex();
                use(var);
            } catch (final RTEx e) {
            }
        }
    }

    private static void catchException3() {
        var = 0;
        if (t()) {
            try {
                rtex();
                use(var);
                use(var);
            } catch (final RTEx e) {
            }
        }
    }

    private static int var;

}
