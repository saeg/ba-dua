/**
 * Copyright (c) 2014 University of Sao Paulo and Contributors.
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
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.util.BitSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import br.usp.each.saeg.badua.core.internal.ContentTypeDetector;
import br.usp.each.saeg.badua.core.internal.instr.IdGenerator;
import br.usp.each.saeg.badua.core.internal.instr.MethodInstrumenter;
import br.usp.each.saeg.badua.core.internal.instr.df.CoverageMethodTransformer;
import br.usp.each.saeg.bytecode.analysis.graph.defuse.VariableRef;
import br.usp.each.saeg.commons.BitSetUtils;
import br.usp.each.saeg.commons.io.Files;
import br.usp.each.saeg.opal.requirement.Dua;
import br.usp.each.saeg.opal.requirement.Use.Type;

public class Report implements IdGenerator {

    private static MethodVisitor NOP = new MethodVisitor(Opcodes.ASM4) {
        // Use default implementation
    };

    private final File inputFile;

    private final File classes;

    private final boolean showClasses;

    private final boolean showMethods;

    private final boolean showAll;

    private Map<String, long[]> data;

    private String className;

    private int methodProbeCount;

    private int classProbeCount;

    private int classCoveredDuas;

    private int classTotalDuas;

    public Report(final ReportOptions options) {
        this.inputFile = options.getInput();
        this.classes = options.getClasses();
        showClasses = options.showClasses() | options.showAll();
        showMethods = options.showMethods() | options.showAll();
        showAll = options.showAll();
    }

    public void run() throws IOException, ClassNotFoundException {

        data = read(inputFile);

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
                    final ClassNode cn = new ClassNode(Opcodes.ASM4);
                    cr.accept(cn, ClassReader.EXPAND_FRAMES);
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
        methodProbeCount = 0;
        classProbeCount = 0;
        classCoveredDuas = 0;
        classTotalDuas = 0;

        // iteration order is important!
        for (final MethodNode mn : cn.methods) {
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

        // we need to instrument the method to verify if a size overflow occurs
        final CoverageMethodTransformer mt = new CoverageMethodTransformer(className, this);
        final MethodInstrumenter mi = new MethodInstrumenter(mn.access, mn.name, mn.desc,
                mn.signature, toArray(mn.exceptions), NOP, mt) {

            @Override
            public void sizeOverflow() {
                classProbeCount = classProbeCount - methodProbeCount;
                methodProbeCount = 0;
            }

        };
        mn.accept(mi);

        final BitSet mnData = BitSetUtils.valueOf(Arrays.copyOfRange(
                data.get(className), classProbeCount - methodProbeCount, classProbeCount));

        if (showClasses) {
            classCoveredDuas = classCoveredDuas + mnData.cardinality();
            classTotalDuas = classTotalDuas + mt.getDuas().length;
        }

        if (showMethods) {
            System.out.println(String.format("%s.%s%s\t(%d/%d)", className,
                    mn.name, mn.desc, mnData.cardinality(), mt.getDuas().length));
        }

        if (showAll) {
            final Dua[] duas = mt.getDuas();
            for (int i = 0; i < duas.length; i++) {
                final Dua dua = duas[i];
                if (mnData.get(i))
                    dua.cover();

                final Set<Integer> defLines = mt.getGraph().get(dua.def).lines();
                final Set<Integer> useLines = new TreeSet<Integer>();
                if (dua.use.type == Type.P_USE) {
                    useLines.addAll(mt.getGraph().get(dua.use.puse().from).lines());
                }
                useLines.addAll(mt.getGraph().get(dua.use.id()).lines());
                final VariableRef var = mt.getVariables().get(dua.var);

                System.out.println(String.format("-> %s, %s, %s, %s",
                        defLines, useLines, var, dua.isCovered()));
            }
        }

        // after analyze we could reset methodProbeCount
        methodProbeCount = 0;
    }

    private String[] toArray(final List<String> l) {
        final String[] array = new String[l.size()];
        int i = 0;
        for (final String string : l) {
            array[i++] = string;
        }
        return array;
    }

    @Override
    public int nextId() {
        methodProbeCount++;
        return classProbeCount++;
    }

    @SuppressWarnings("unchecked")
    private Map<String, long[]> read(final File file) throws IOException, ClassNotFoundException {
        final ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
        try {
            return (Map<String, long[]>) ois.readObject();
        } finally {
            ois.close();
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
