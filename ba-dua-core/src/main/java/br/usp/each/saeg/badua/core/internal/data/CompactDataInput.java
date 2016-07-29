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
package br.usp.each.saeg.badua.core.internal.data;

import java.io.IOException;
import java.io.InputStream;

public class CompactDataInput extends org.jacoco.core.internal.data.CompactDataInput {

    public CompactDataInput(final InputStream in) {
        super(in);
    }

    public long[] readLongArray() throws IOException {
        final long[] value = new long[readVarInt()];
        for (int i = 0; i < value.length; i++) {
            value[i] = readLong();
        }
        return value;
    }

}
