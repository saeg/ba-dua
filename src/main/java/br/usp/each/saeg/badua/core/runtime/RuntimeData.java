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

    private final Map<Long, long[]> data = new HashMap<Long, long[]>();

    public long[] getExecutionData(final long classId, final int size) {
        synchronized (data) {
            long[] dataArray = data.get(classId);
            if (dataArray == null) {
                dataArray = new long[size];
                data.put(classId, dataArray);
            }
            return dataArray;
        }
    }

    public Object getData() {
        return new HashMap<Long, long[]>(data);
    }

}
