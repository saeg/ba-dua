/**
 * Copyright (c) 2014, 2018 University of Sao Paulo and Contributors.
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
import br.usp.each.saeg.commons.io.Files;

public class Instrumenter {

    private static final int DEFAULT = 0;

    private final String runtime;

    private final boolean exceptionHandler;

    public Instrumenter(final Class<?> runtime) {
        this(runtime.getName());
    }

    public Instrumenter(final String runtime) {
        this(runtime, false);
    }

    public Instrumenter(final String runtime, final boolean exceptionHandler) {
        this.runtime = runtime;
        this.exceptionHandler = exceptionHandler;
    }

    public byte[] instrument(final ClassReader reader) {
        final long classId = CRC64.checksum(reader.b);
        final ClassWriter writer = new ClassWriter(reader, DEFAULT);
        final ClassVisitor ci = new ClassInstrumenter(classId, writer, runtime, exceptionHandler);
        reader.accept(ci, ClassReader.EXPAND_FRAMES);
        return writer.toByteArray();
    }

    public byte[] instrument(final byte[] buffer, final String name) throws IOException {
        try {
            return instrument(new ClassReader(buffer));
        } catch (final RuntimeException e) {
            throw instrumentError(name, e);
        }
    }

    public void instrument(final InputStream input, final OutputStream output, final String name)
            throws IOException {
        try {
            output.write(instrument(new ClassReader(input)));
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
