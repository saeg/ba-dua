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
package br.usp.each.saeg.badua.core.instr;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.jacoco.core.internal.ContentTypeDetector;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import br.usp.each.saeg.badua.core.internal.data.CRC64;
import br.usp.each.saeg.badua.core.internal.instr.ClassInstrumenter;
import br.usp.each.saeg.badua.core.runtime.IExecutionDataAccessorGenerator;
import br.usp.each.saeg.commons.io.Files;

public class Instrumenter {

    private static final int DEFAULT = 0;

    private final IExecutionDataAccessorGenerator accessorGenerator;

    public Instrumenter(final IExecutionDataAccessorGenerator accessorGenerator) {
        this.accessorGenerator = accessorGenerator;
    }

    public byte[] instrument(final byte[] buffer) {
        final long classId = CRC64.checksum(buffer);
        final ClassReader reader = new ClassReader(buffer);
        final ClassWriter writer = new ClassWriter(reader, DEFAULT);
        final ClassVisitor ci = new ClassInstrumenter(classId, writer, accessorGenerator);
        reader.accept(ci, ClassReader.EXPAND_FRAMES);
        return writer.toByteArray();
    }

    public byte[] instrument(final byte[] buffer, final String name) throws IOException {
        try {
            return instrument(buffer);
        } catch (final RuntimeException e) {
            throw instrumentError(name, e);
        }
    }

    public void instrument(final InputStream input, final OutputStream output, final String name)
            throws IOException {
        try {
            output.write(instrument(Files.toByteArray(input)));
        } catch (final RuntimeException e) {
            throw instrumentError(name, e);
        }
    }

    private IOException instrumentError(final String name, final RuntimeException cause) {
        final String message = String.format("Error while instrumenting class %s.", name);
        final IOException ex = new IOException(message);
        ex.initCause(cause);
        return ex;
    }

    public int instrumentAll(final InputStream input, final OutputStream output, final String name)
            throws IOException {
        final ContentTypeDetector detector = new ContentTypeDetector(input);
        switch (detector.getType()) {
        case ContentTypeDetector.CLASSFILE:
            instrument(detector.getInputStream(), output, name);
            return 1;
        default:
            Files.copy(detector.getInputStream(), output);
            return 0;
        }
    }

}
