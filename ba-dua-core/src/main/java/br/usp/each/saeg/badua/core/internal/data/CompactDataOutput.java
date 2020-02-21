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
package br.usp.each.saeg.badua.core.internal.data;

import java.io.IOException;
import java.io.OutputStream;

public class CompactDataOutput extends org.jacoco.core.internal.data.CompactDataOutput {

    public CompactDataOutput(final OutputStream out) {
        super(out);
    }

    public void writeLongArray(final long[] value) throws IOException {
        writeVarInt(value.length);
        for (final long l : value) {
            writeLong(l);
        }
    }

}
