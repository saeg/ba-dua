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

import static br.usp.each.saeg.badua.core.analysis.SourceLineDefUseChain.NONE;
import static org.jacoco.report.internal.xml.XMLCoverageWriter.createChild;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.jacoco.core.analysis.ICounter;
import org.jacoco.report.internal.xml.XMLDocument;
import org.jacoco.report.internal.xml.XMLElement;

import br.usp.each.saeg.badua.core.analysis.ClassCoverage;
import br.usp.each.saeg.badua.core.analysis.CoverageNode;
import br.usp.each.saeg.badua.core.analysis.MethodCoverage;
import br.usp.each.saeg.badua.core.analysis.SourceLineDefUseChain;

public class XMLCoverageWriter {

    private XMLCoverageWriter() {
        // No instances
    }

    public static void write(final List<ClassCoverage> classes, final FileOutputStream output)
            throws IOException {
        final CoverageNode sum = new CoverageNode("");
        final XMLElement root = new XMLDocument("report", null, null, "UTF-8", true, output);
        for (final ClassCoverage c : classes) {
            writeClass(c, root);
            sum.increment(c);
        }
        writeCounters(sum, root);
        root.close();
    }

    private static void writeClass(final ClassCoverage c, final XMLElement parent) throws IOException {
        final XMLElement element = createChild(parent, "class", c.getName());
        for (final MethodCoverage m : c.getMethods()) {
            writeMethod(m, element);
        }
        writeCounters(c, element);
    }

    private static void writeMethod(final MethodCoverage m, final XMLElement parent) throws IOException {
        final XMLElement element = createChild(parent, "method", m.getName());
        element.attr("desc", m.getDesc());
        for (final SourceLineDefUseChain du : m.getDefUses()) {
            writeDU(du, element);
        }
        writeCounters(m, element);
    }

    private static void writeDU(final SourceLineDefUseChain du, final XMLElement parent) throws IOException {
        final XMLElement element = parent.element("du");
        element.attr("var", du.var);
        element.attr("def", du.def);
        element.attr("use", du.use);
        if (du.target != NONE) {
            element.attr("target", du.target);
        }
        element.attr("covered", du.covered ? 1 : 0);
    }

    private static void writeCounters(final CoverageNode node, final XMLElement parent) throws IOException {
        writeCounter(node.getDUCounter(), "DU", parent);
        writeCounter(node.getMethodCounter(), "METHOD", parent);
        writeCounter(node.getClassCounter(), "CLASS", parent);
    }

    private static void writeCounter(final ICounter counter, final String type, final XMLElement parent)
            throws IOException {
        if (counter.getTotalCount() > 0) {
            final XMLElement element = parent.element("counter");
            element.attr("type", type);
            element.attr("missed", counter.getMissedCount());
            element.attr("covered", counter.getCoveredCount());
        }
    }

}
