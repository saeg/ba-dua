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
package br.usp.each.saeg.badua.agent.rt.internal.output;

import java.io.FileOutputStream;
import java.io.IOException;

import br.usp.each.saeg.badua.core.data.ExecutionDataWriter;
import br.usp.each.saeg.badua.core.runtime.RuntimeData;

public class FileOutput {

    public void writeExecutionData(final RuntimeData data, final boolean reset) throws IOException {
        String filename = System.getProperty("output.file");
        if (filename == null)
            filename = "coverage.ser";

        final FileOutputStream output = new FileOutputStream(filename, true);
        try {
            data.collect(new ExecutionDataWriter(output), reset);
        } finally {
            output.close();
        }
    }

}
