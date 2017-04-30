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

public abstract class AbstractTarget {

    public static void use(final int var) {
    }

    public static boolean t() {
        return true;
    }

    public static boolean f() {
        return false;
    }

    public static void ex() {
        throw new Ex();
    }

    public static class Ex extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }

}
