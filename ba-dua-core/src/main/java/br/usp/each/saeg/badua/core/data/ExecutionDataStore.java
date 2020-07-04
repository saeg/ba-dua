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
package br.usp.each.saeg.badua.core.data;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ExecutionDataStore implements IExecutionDataVisitor {

    private final Map<Long, ExecutionData> entries = new HashMap<Long, ExecutionData>();

    public ExecutionData get(final Long id) {
        return entries.get(id);
    }

    public ExecutionData get(final Long id, final String name, final int size) {
        ExecutionData entry = entries.get(id);
        if (entry == null) {
            entry = new ExecutionData(id, name, size);
            entries.put(id, entry);
        } else {
            entry.assertCompatibility(id, name, size);
        }
        return entry;
    }

    public Collection<ExecutionData> getContents() {
        return new ArrayList<ExecutionData>(entries.values());
    }

    public void accept(final IExecutionDataVisitor visitor) {
        for (final ExecutionData data : getContents()) {
            visitor.visitClassExecution(data);
        }
    }

    public void reset() {
        for (final ExecutionData data : getContents()) {
            data.reset();
        }
    }

    @Override
    public void visitClassExecution(final ExecutionData data) {
        final Long id = data.getId();
        final ExecutionData entry = entries.get(id);
        if (entry == null) {
            entries.put(id, data);
        } else {
            entry.merge(data);
        }
    }

}
