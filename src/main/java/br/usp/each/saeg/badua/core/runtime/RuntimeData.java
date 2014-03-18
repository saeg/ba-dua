/**
 * Copyright (c) 2014 University of Sao Paulo and Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Roberto Araujo - initial API and implementation and/or initial documentation
 */
package br.usp.each.saeg.badua.core.runtime;

import java.util.HashMap;
import java.util.Map;

public class RuntimeData {

    private final Map<String, long[]> data = new HashMap<String, long[]>();

    public long[] getExecutionData(final String className, final int size) {
        synchronized (data) {
            long[] dataArray = data.get(className);
            if (dataArray == null) {
                dataArray = new long[size];
                data.put(className, dataArray);
            }
            return dataArray;
        }
    }

    public Object getData() {
        return new HashMap<String, long[]>(data);
    }

}
