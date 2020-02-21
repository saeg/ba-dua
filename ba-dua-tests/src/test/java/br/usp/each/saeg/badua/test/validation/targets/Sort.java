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

public class Sort {

    public static void sort(final int[] a, final int n) {
        int sortupto, maxpos, mymax, index;
        sortupto = 0;
        while (sortupto < n - 1) {
            mymax = a[sortupto];
            maxpos = sortupto;
            index = sortupto + 1;
            while (index < n) {
                if (a[index] > mymax) {
                    mymax = a[index];
                    maxpos = index;
                }
                index++;
            }
            index = a[sortupto];
            a[sortupto] = mymax;
            a[maxpos] = index;
            sortupto++;
        }
    }

}
