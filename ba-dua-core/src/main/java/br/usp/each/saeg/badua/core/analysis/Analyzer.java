/**
 * Copyright (c) 2014, 2017 University of Sao Paulo and Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Roberto Araujo - initial API and implementation and/or initial documentation
 */
package br.usp.each.saeg.badua.core.analysis;

import java.io.IOException;
import java.io.InputStream;

import org.jacoco.core.internal.ContentTypeDetector;
import org.jacoco.core.internal.analysis.StringPool;
import org.jacoco.core.internal.data.CRC64;
import org.objectweb.asm.ClassReader;

import br.usp.each.saeg.badua.core.data.ExecutionData;
import br.usp.each.saeg.badua.core.data.ExecutionDataStore;

public class Analyzer {

    private static final int DEFAULT = 0;

    private final StringPool stringPool = new StringPool();

    private final ExecutionDataStore store;

    private final ICoverageVisitor visitor;

    public Analyzer(final ExecutionDataStore store, final ICoverageVisitor visitor) {
        this.store = store;
        this.visitor = visitor;
    }

    private ExecutionData getData(final long classId, final String className) {
        final ExecutionData execData = store.get(classId);
        if (execData != null) {
            execData.assertCompatibility(classId, className, execData.getData().length);
            return execData;
        }
        return new ExecutionData(classId, className, null);
    }

    public void analyze(final ClassReader reader) {
        final long classId = CRC64.checksum(reader.b);
        final ClassAnalyzer ca = new ClassAnalyzer(getData(classId, reader.getClassName()), stringPool);
        reader.accept(ca, DEFAULT);
        final ClassCoverage coverage = ca.getCoverage();
        if (coverage.getDUCounter().getTotalCount() > 0) {
            visitor.visitCoverage(coverage);
        }
    }

    public void analyze(final InputStream input, final String location) throws IOException {
        try {
            analyze(new ClassReader(input));
        } catch (final RuntimeException e) {
            throw analyzeError(location, e);
        }
    }

    private IOException analyzeError(final String location, final Exception cause) {
        final String message = String.format("Error while analyzing %s.", location);
        final IOException ex = new IOException(message);
        ex.initCause(cause);
        return ex;
    }

    public int analyzeAll(final InputStream input, final String location) throws IOException {
        final ContentTypeDetector detector = new ContentTypeDetector(input);
        switch (detector.getType()) {
        case ContentTypeDetector.CLASSFILE:
            analyze(detector.getInputStream(), location);
            return 1;
        default:
            return 0;
        }

    }

}
