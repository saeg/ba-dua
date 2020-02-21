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

public class Max {

    public static int max(final int[] array, final int length) {
        int i = 0;
        int max = array[i++];
        while (i < length) {
            if (array[i] > max) {
                max = array[i];
            }
            i++;
        }
        return max;
    }

}
