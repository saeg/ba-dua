/**
 * Copyright (c) 2014, 2016 University of Sao Paulo and Contributors.
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

import br.usp.each.saeg.badua.core.data.ExecutionData;
import br.usp.each.saeg.badua.core.data.ExecutionDataStore;

public class RuntimeData {

    private final ExecutionDataStore store = new ExecutionDataStore();

    public ExecutionData getExecutionData(final long id, final String name, final int size) {
        synchronized (store) {
            return store.get(id, name, size);
        }
    }

    public Object getData() {
        final Map<Long, long[]> data = new HashMap<Long, long[]>(store.entries.size());
        for (final ExecutionData executionData : store.entries.values()) {
            data.put(executionData.getId(), executionData.getData());
        }
        return data;
    }

}
