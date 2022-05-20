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

public class JumpToFirstInstruction extends AbstractTarget {

    private static int var;

    public static void run1() {
        do {
            var = 0;
            if (t()) {
                use(var);
            }
        } while (f());
    }

    public static void run2() {
        do {
            var = 0;
            if (f()) {
                use(var);
            }
        } while (f());
    }

    public static void run3() {
        do {
            var = 0;
            if (t()) {
                use(var);
                rtex();
            }
        } while (f());
    }

    public static void run4() {
        do {
            var = 0;
            if (f()) {
                use(var);
                rtex();
            }
        } while (f());
    }

}
