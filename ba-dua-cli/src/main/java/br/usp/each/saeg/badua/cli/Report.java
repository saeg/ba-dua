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

import static java.lang.String.format;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

import br.usp.each.saeg.badua.core.analysis.Analyzer;
import br.usp.each.saeg.badua.core.analysis.ClassCoverage;
import br.usp.each.saeg.badua.core.analysis.ICoverageVisitor;
import br.usp.each.saeg.badua.core.analysis.MethodCoverage;
import br.usp.each.saeg.badua.core.data.ExecutionDataReader;
import br.usp.each.saeg.badua.core.data.ExecutionDataStore;
import br.usp.each.saeg.commons.io.Files;

public class Report {

    private static class PrintCoverage implements ICoverageVisitor {

        private final PrintStream out;

        private final boolean showClasses;

        private final boolean showMethods;

        public PrintCoverage(final PrintStream out, final boolean showClasses, final boolean showMethods) {
            this.out = out;
            this.showClasses = showClasses;
            this.showMethods = showMethods;
        }

        @Override
        public void visitCoverage(final ClassCoverage coverage) {
            if (showMethods) {
                for (final MethodCoverage methodCoverage : coverage.getMethods()) {
                    print(coverage.getName(), methodCoverage);
                }
            }
            if (showClasses) {
                print(coverage);
            }
        }

        private void print(final String className, final MethodCoverage coverage) {
            out.println(format("%s.%s%s\t(%d/%d)", className,
                    coverage.getName(), coverage.getDesc(),
                    coverage.getDUCounter().getCoveredCount(),
                    coverage.getDUCounter().getTotalCount()));
        }

        private void print(final ClassCoverage coverage) {
            out.println(format("%s\t(%d/%d)", coverage.getName(),
                    coverage.getDUCounter().getCoveredCount(),
                    coverage.getDUCounter().getTotalCount()));
        }

    }

    private static class CoverageVisitor implements ICoverageVisitor {

        private final List<ClassCoverage> classes = new ArrayList<ClassCoverage>();

        private final ICoverageVisitor next;

        public CoverageVisitor(final ICoverageVisitor next) {
            this.next = next;
        }

        @Override
        public void visitCoverage(final ClassCoverage coverage) {
            classes.add(coverage);
            if (next != null) {
                next.visitCoverage(coverage);
            }
        }

    }

    private final File classes;

    private final File xmlFile;

    private final CoverageVisitor visitor;

    private final Analyzer analyzer;

    public Report(final ReportOptions options) throws IOException {
        classes = options.getClasses();
        xmlFile = options.getXMLFile();

        visitor = new CoverageVisitor(new PrintCoverage(
                System.out, options.showClasses(), options.showMethods()));

        analyzer = new Analyzer(readExecutionData(options.getInput()), visitor);
    }

    public void run() throws IOException {

        final List<File> files = Files.listRecursive(classes, new FilenameFilter() {

            @Override
            public boolean accept(final File dir, final String name) {
                return new File(dir, name).isFile();
            }

        });

        for (final File file : files) {
            final InputStream input = new FileInputStream(file);
            try {
                analyzer.analyzeAll(input, file.getPath());
            } finally {
                input.close();
            }
        }

        if (xmlFile != null) {
            final FileOutputStream output = new FileOutputStream(xmlFile);
            try {
                XMLCoverageWriter.write(visitor.classes, output);
            } finally {
                output.close();
            }
        }
    }

    private static ExecutionDataStore readExecutionData(final File inputFile) throws IOException {
        final ExecutionDataStore store = new ExecutionDataStore();
        final FileInputStream input = new FileInputStream(inputFile);
        try {
            final ExecutionDataReader reader = new ExecutionDataReader(input);
            reader.setExecutionDataVisitor(store);
            reader.read();
        } finally {
            input.close();
        }
        return store;
    }

    public static void main(final String[] args) {
        final ReportOptions options = new ReportOptions();
        final CmdLineParser parser = new CmdLineParser(options);

        try {
            parser.parseArgument(args);
        } catch (final CmdLineException e) {
            System.err.println(e.getLocalizedMessage());
            parser.printUsage(System.err);
            System.exit(1);
        }

        try {
            new Report(options).run();
        } catch (final Exception e) {
            System.err.println("Failed: " + e.getLocalizedMessage());
            System.exit(1);
        }
    }

}
