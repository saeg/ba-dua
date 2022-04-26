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

import org.kohsuke.args4j.Option;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ReportOptions {

    @Option(name = "-input", required = true, usage = "the ba-dua execution data")
    private File input;

    @Option(name = "-classes", required = true,
            usage = "directory containing the classes which were instrumented. "
                    + "NOTE: this must point at the original, non-instrumented class files")
    private File classes;

    @Option(name = "-show-classes", aliases = "--sc", usage = "show class coverage")
    private Boolean showClasses;

    @Option(name = "-show-methods", aliases = "--sm", usage = "show method coverage")
    private boolean showMethods;

    @Option(name = "-xml", usage = "write XML report")
    private File xmlFile;
    
    @Option(name = "-html", usage = "write HTML report in selected root directory")
    private File htmlRoot;

    @Option(name = "-sourcefiles", required = true, usage = "location of the source files", metaVar = "<path>")
    private List<File> sourceFiles = new ArrayList<File>();

    public File getInput() {
        return input;
    }

    public File getClasses() {
        return classes;
    }

    public boolean showClasses() {
        return showClasses == null ? !showMethods && xmlFile == null : showClasses;
    }

    public boolean showMethods() {
        return showMethods;
    }

    public File getXMLFile() {
        return xmlFile;
    }
    
    public File getHTMLRoot() {
    	return htmlRoot;
    }

    public List<File> getSourceFiles() { return  sourceFiles; }

}
