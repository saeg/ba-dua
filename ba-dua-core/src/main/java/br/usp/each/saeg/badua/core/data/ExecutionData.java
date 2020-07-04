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

import java.util.Arrays;

public class ExecutionData {

    private final long id;

    private final String name;

    private final long[] data;

    public ExecutionData(final long id, final String name, final int length) {
        this(id, name, new long[length]);
    }

    public ExecutionData(final long id, final String name, final long[] data) {
        this.id = id;
        this.name = name;
        this.data = data;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public long[] getData() {
        return data;
    }

    public void reset() {
        Arrays.fill(data, 0L);
    }

    public void merge(final ExecutionData other) {
        assertCompatibility(other.id, other.name, other.data.length);
        for (int i = 0; i < data.length; i++) {
            data[i] |= other.data[i];
        }
    }

    public void assertCompatibility(final long id, final String name, final int length)
            throws IllegalStateException {

        if (this.id != id) {
            throw new IllegalStateException(String.format(
                    "Different ids (%016x and %016x).", this.id, id));
        }
        if (!this.name.equals(name)) {
            throw new IllegalStateException(String.format(
                    "Different class names %s and %s for id %016x.", this.name, name, id));
        }
        if (this.data.length != length) {
            throw new IllegalStateException(String.format(
                    "Incompatible execution data for class %s with id %016x.", name, id));
        }
    }

}
