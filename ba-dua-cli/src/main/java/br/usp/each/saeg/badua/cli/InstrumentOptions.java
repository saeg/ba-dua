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
package br.usp.each.saeg.badua.cli;

import java.io.File;

import org.kohsuke.args4j.Option;

public class InstrumentOptions {

    @Option(name = "-src", required = true,
            usage = "path where files to be instrument are located "
                    + "(may be a .class file or a directory whith classes)")
    private File src;

    @Option(name = "-dest", required = true,
            usage = "destination path to place the instrumented class files")
    private File dest;

    public File getSource() {
        return src;
    }

    public File getDestination() {
        return dest;
    }

}
