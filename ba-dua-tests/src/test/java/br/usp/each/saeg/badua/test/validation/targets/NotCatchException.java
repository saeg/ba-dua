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
package br.usp.each.saeg.badua.test.validation.targets;

public class NotCatchException extends AbstractTarget {

    public static void notCatchException1() {
        var = 0;
        if (t()) {
            use(var);
            use(var);
            ex();
        }
    }

    public static void notCatchException2() {
        var = 0;
        if (t()) {
            use(var);
            ex();
            use(var);
        }
    }

    public static void notCatchException3() {
        var = 0;
        if (t()) {
            ex();
            use(var);
            use(var);
        }
    }

    private static int var;

}
