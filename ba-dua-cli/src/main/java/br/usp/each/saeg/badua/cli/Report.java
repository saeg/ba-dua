/**
 * Copyright (c) 2014, 2016 University of Sao Paulo and Contributors.
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
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;

import org.jacoco.core.internal.ContentTypeDetector;
import org.jacoco.core.internal.data.CRC64;
import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import br.usp.each.saeg.badua.core.data.ExecutionData;
import br.usp.each.saeg.badua.core.data.ExecutionDataReader;
import br.usp.each.saeg.badua.core.data.ExecutionDataStore;
import br.usp.each.saeg.badua.core.internal.instr.CoverageMethodTransformer;
import br.usp.each.saeg.badua.core.internal.instr.IdGenerator;
import br.usp.each.saeg.badua.core.internal.instr.MethodInstrumenter;
import br.usp.each.saeg.commons.BitSetUtils;
import br.usp.each.saeg.commons.io.Files;

public class Report implements IdGenerator {

    private static MethodVisitor NOP = new MethodVisitor(Opcodes.ASM5) {
        // Use default implementation
    };

    private final File inputFile;

    private final File classes;

    private final boolean showClasses;

    private final boolean showMethods;

    private ExecutionDataStore store;

    private long classId;

    private String className;

    private int methodProbeCount;

    private int classProbeCount;

    private int classCoveredDuas;

    private int classTotalDuas;

    public Report(final ReportOptions options) {
        this.inputFile = options.getInput();
        this.classes = options.getClasses();
        showClasses = options.showClasses();
        showMethods = options.showMethods();
    }

    public void run() throws IOException, ClassNotFoundException {

        readExecutionData();

        final List<File> files = Files.listRecursive(classes, new FilenameFilter() {

            @Override
            public boolean accept(final File dir, final String name) {
                return new File(dir, name).isFile();
            }

        });

        for (final File file : files) {
            final InputStream input = new FileInputStream(file);
            final ContentTypeDetector detector = new ContentTypeDetector(input);
            try {
                if (detector.getType() == ContentTypeDetector.CLASSFILE) {
                    final ClassReader cr = new ClassReader(detector.getInputStream());
                    final ClassNode cn = new ClassNode(Opcodes.ASM5);
                    cr.accept(cn, ClassReader.EXPAND_FRAMES);
                    classId = CRC64.checksum(cr.b);
                    analyze(cn);
                }
            } finally {
                input.close();
            }
        }
    }

    private void analyze(final ClassNode cn) throws IOException {
        // do not analyze interfaces
        if ((cn.access & Opcodes.ACC_INTERFACE) != 0) {
            return;
        }

        // before analyze
        className = cn.name;
        classProbeCount = 0;
        classCoveredDuas = 0;
        classTotalDuas = 0;

        // iteration order is important!
        for (final MethodNode mn : cn.methods) {
            methodProbeCount = 0; // reset method probe counter
            analyze(mn);
        }

        if (showClasses) {
            System.out.println(String.format("%s\t(%d/%d)",
                    className, classCoveredDuas, classTotalDuas));
        }
    }

    private void analyze(final MethodNode mn) {
        // do not analyze abstract methods
        if ((mn.access & Opcodes.ACC_ABSTRACT) != 0) {
            return;
        }
        // do not analyze static class initialization
        else if (mn.name.equals("<clinit>")) {
            return;
        }

        // we instrument the method to get the probe count
        final CoverageMethodTransformer mt = new CoverageMethodTransformer(className, this);
        final MethodInstrumenter mi = new MethodInstrumenter(mn.access, mn.name, mn.desc,
                mn.signature, toArray(mn.exceptions), NOP, mt);

        mn.accept(mi);

        final ExecutionData execData = store.get(classId);
        final BitSet mnData;
        if (execData == null) {
            mnData = new BitSet();
        } else {
            mnData = BitSetUtils.valueOf(Arrays.copyOfRange(execData.getData(),
                    classProbeCount - methodProbeCount, classProbeCount));
        }

        if (showClasses) {
            classCoveredDuas = classCoveredDuas + mnData.cardinality();
            classTotalDuas = classTotalDuas + mt.getDefUseChains().length;
        }

        if (showMethods) {
            System.out.println(String.format("%s.%s%s\t(%d/%d)", className,
                    mn.name, mn.desc, mnData.cardinality(), mt.getDefUseChains().length));
        }
    }

    private String[] toArray(final List<String> l) {
        return l.toArray(new String[l.size()]);
    }

    @Override
    public int nextId() {
        methodProbeCount++;
        return classProbeCount++;
    }

    private void readExecutionData() throws IOException {
        store = new ExecutionDataStore();
        final FileInputStream input = new FileInputStream(inputFile);
        try {
            final ExecutionDataReader reader = new ExecutionDataReader(input);
            reader.setExecutionDataVisitor(store);
            reader.read();
        } finally {
            input.close();
        }
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
