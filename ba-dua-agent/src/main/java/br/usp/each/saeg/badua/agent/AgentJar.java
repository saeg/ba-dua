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
package br.usp.each.saeg.badua.agent;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

public final class AgentJar {

    private static final int BUFFER_SIZE = 4096;

    private static final int EOF = -1;

    private static final String RESOURCE = "/baduaagent.jar";

    private AgentJar() {
        // No instances
    }

    public static URL getResource() {
        final URL url = AgentJar.class.getResource(RESOURCE);
        if (url == null) {
            throw new AssertionError("The agent resource has not been found.");
        }
        return url;
    }

    public static InputStream getResourceAsStream() {
        final InputStream stream = AgentJar.class.getResourceAsStream(RESOURCE);
        if (stream == null) {
            throw new AssertionError("The agent resource has not been found.");
        }
        return stream;
    }

    public static File extractToTempLocation() throws IOException {
        final File agentJar = File.createTempFile("baduaagent", ".jar");
        agentJar.deleteOnExit();
        extractTo(agentJar);
        return agentJar;
    }

    public static void extractTo(final File destination) throws IOException {
        InputStream in = null;
        OutputStream out = null;
        try {
            in = getResourceAsStream();
            out = new FileOutputStream(destination);
            copy(in, out);
        } finally {
            safeClose(in);
            safeClose(out);
        }
    }

    private static void copy(final InputStream input, final OutputStream output)
            throws IOException {

        final byte[] buffer = new byte[BUFFER_SIZE];

        int n = input.read(buffer);
        while (EOF != n) {
            output.write(buffer, 0, n);
            n = input.read(buffer);
        }

        output.flush();
    }

    private static void safeClose(final Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (final IOException e) { // NOPMD
        }
    }

}
