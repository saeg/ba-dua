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

import static br.usp.each.saeg.badua.core.data.ExecutionDataWriter.BLOCK_EXECUTIONDATA;
import static br.usp.each.saeg.badua.core.data.ExecutionDataWriter.BLOCK_HEADER;
import static br.usp.each.saeg.badua.core.data.ExecutionDataWriter.FORMAT_VERSION;
import static br.usp.each.saeg.badua.core.data.ExecutionDataWriter.MAGIC_NUMBER;

import java.io.IOException;
import java.io.InputStream;

import br.usp.each.saeg.badua.core.internal.data.CompactDataInput;

public class ExecutionDataReader {

    private static final int EOF = -1;

    private final CompactDataInput in;

    private IExecutionDataVisitor executionDataVisitor;

    private boolean firstBlock = true;

    public ExecutionDataReader(final InputStream input) {
        in = new CompactDataInput(input);
    }

    public void setExecutionDataVisitor(final IExecutionDataVisitor visitor) {
        executionDataVisitor = visitor;
    }

    public void read() throws IOException {
        int b = in.read();
        while (EOF != b) {
            final byte type = (byte) b;
            if (firstBlock) {
                assertValue(BLOCK_HEADER, type);
            }
            readBlock(type);
            b = in.read();
        }
    }

    private void readBlock(final byte type) throws IOException {
        switch (type) {
        case BLOCK_HEADER:
            readHeader();
            break;
        case BLOCK_EXECUTIONDATA:
            readExecutionData();
            break;
        default:
            throw new IOException(String.format("Unknown block type 0x%x.", type));
        }
    }

    private void readHeader() throws IOException {
        assertValue(MAGIC_NUMBER, in.readChar());
        assertValue(FORMAT_VERSION, in.readChar());
        firstBlock = false;
    }

    private void readExecutionData() throws IOException {
        if (executionDataVisitor == null) {
            throw new IOException("No execution data visitor.");
        }
        final long id = in.readLong();
        final String name = in.readUTF();
        final long[] data = in.readLongArray();
        executionDataVisitor.visitClassExecution(new ExecutionData(id, name, data));
    }

    private void assertValue(final byte expected, final byte actual) throws IOException {
        if (expected != actual) {
            throw new IOException("Invalid execution data file.");
        }
    }

    private void assertValue(final char expected, final char actual) throws IOException {
        if (expected != actual) {
            throw new IOException("Invalid execution data file.");
        }
    }

}
