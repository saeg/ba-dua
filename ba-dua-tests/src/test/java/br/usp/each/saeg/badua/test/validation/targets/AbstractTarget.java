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

public abstract class AbstractTarget {

    public static void use(final int var) {
    }

    public static boolean t() {
        return true;
    }

    public static boolean f() {
        return false;
    }

    public static void rtex() {
        throw new RTEx();
    }

    public static void ex() throws Ex {
        throw new Ex();
    }

    public static void err() throws Err {
        throw new Err();
    }

    public static void thr() throws Thr {
        throw new Thr();
    }

    public static class RTEx extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }

    public static class Ex extends Exception {
        private static final long serialVersionUID = 1L;
    }

    public static class Err extends Error {
        private static final long serialVersionUID = 1L;
    }

    public static class Thr extends Throwable {
        private static final long serialVersionUID = 1L;
    }

}
