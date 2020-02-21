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

import java.io.IOException;
import java.io.OutputStream;

import br.usp.each.saeg.badua.core.internal.data.CompactDataOutput;

public class ExecutionDataWriter implements IExecutionDataVisitor {

    public static final char FORMAT_VERSION = 0x1001;

    public static final char MAGIC_NUMBER = 0xBABA;

    public static final byte BLOCK_HEADER = 0x01;

    public static final byte BLOCK_EXECUTIONDATA = 0x10;

    private final CompactDataOutput out;

    public ExecutionDataWriter(final OutputStream output) throws IOException {
        out = new CompactDataOutput(output);
        writeHeader();
    }

    private void writeHeader() throws IOException {
        out.writeByte(BLOCK_HEADER);
        out.writeChar(MAGIC_NUMBER);
        out.writeChar(FORMAT_VERSION);
    }

    @Override
    public void visitClassExecution(final ExecutionData data) {
        try {
            out.writeByte(BLOCK_EXECUTIONDATA);
            out.writeLong(data.getId());
            out.writeUTF(data.getName());
            out.writeLongArray(data.getData());
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

}
