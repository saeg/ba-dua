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
package br.usp.each.saeg.badua.agent.rt.internal;

import java.io.IOException;

import br.usp.each.saeg.badua.agent.rt.internal.output.FileOutput;
import br.usp.each.saeg.badua.core.runtime.RuntimeData;

public final class Agent {

    // --- Static

    private static Agent singleton;

    public static synchronized Agent getInstance() {
        if (singleton == null) {
            final Agent agent = new Agent();
            ShutdownHook.register(agent);
            singleton = agent;
        }
        return singleton;
    }

    // --- Object

    private final RuntimeData data;

    private final FileOutput output;

    private Agent() {
        data = new RuntimeData();
        output = new FileOutput();
    }

    private void shutdown() {
        try {
            output.writeExecutionData(data, false);
        } catch (final IOException e) {
            System.err.println("error writing execution data");
            e.printStackTrace();
        }
    }

    public RuntimeData getData() {
        return data;
    }

    public void reset() {
        data.reset();
    }

    public void dump(final boolean reset) throws IOException {
        output.writeExecutionData(data, reset);
    }

    private static final class ShutdownHook extends Thread {

        private final Agent agent;

        public ShutdownHook(final Agent agent) {
            this.agent = agent;
        }

        public ShutdownHook register() {
            Runtime.getRuntime().addShutdownHook(this);
            return this;
        }

        @Override
        public void run() {
            agent.shutdown();
        }

        public static ShutdownHook register(final Agent agent) {
            return new ShutdownHook(agent).register();
        }

    }

}
