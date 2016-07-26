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
package br.usp.each.saeg.badua.core.data;

import java.util.HashMap;
import java.util.Map;

public class ExecutionDataStore {

    // TODO: Change visibility
    public final Map<Long, ExecutionData> entries = new HashMap<Long, ExecutionData>();

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

}
