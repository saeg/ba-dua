/**
 * Copyright (c) 2014, 2015 University of Sao Paulo and Contributors.
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
import java.io.ObjectOutputStream;

import br.usp.each.saeg.badua.core.runtime.RuntimeData;

public class FileOutput {

    public void writeExecutionData(final RuntimeData data) throws IOException {
        String filename = System.getProperty("output.file");
        if (filename == null)
            filename = "coverage.ser";

        final ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename));
        try {
            oos.writeObject(data.getData());
        } finally {
            oos.close();
        }
    }

}
