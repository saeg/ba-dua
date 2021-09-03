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
package br.usp.each.saeg.badua.core.runtime;

import br.usp.each.saeg.badua.core.data.ExecutionData;
import br.usp.each.saeg.badua.core.data.ExecutionDataStore;
import br.usp.each.saeg.badua.core.data.IExecutionDataVisitor;

public class RuntimeData {

    private final ExecutionDataStore store = new ExecutionDataStore();

    public ExecutionData getExecutionData(final long id, final String name, final int size) {
        synchronized (store) {
            return store.get(id, name, size);
        }
    }

    public void collect(final IExecutionDataVisitor visitor) {
        synchronized (store) {
            store.accept(visitor);
        }
    }

    public void collect(final IExecutionDataVisitor visitor, final boolean reset) {
        synchronized (store) {
            collect(visitor);
            if (reset) {
                reset();
            }
        }
    }

    public void reset() {
        synchronized (store) {
            store.reset();
        }
    }

    public long[] getData(final long id, final String name, final int size) {
        return getExecutionData(id, name, size).getData();
    }

}
