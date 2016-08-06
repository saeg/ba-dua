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
package br.usp.each.saeg.badua.test.validation;

import static br.usp.each.saeg.badua.core.analysis.SourceLineDefUseChain.NONE;

import java.util.ArrayList;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Before;
import org.objectweb.asm.ClassReader;

import br.usp.each.saeg.badua.agent.rt.internal.RT;
import br.usp.each.saeg.badua.core.analysis.Analyzer;
import br.usp.each.saeg.badua.core.analysis.ClassCoverage;
import br.usp.each.saeg.badua.core.analysis.ICoverageVisitor;
import br.usp.each.saeg.badua.core.analysis.MethodCoverage;
import br.usp.each.saeg.badua.core.analysis.SourceLineDefUseChain;
import br.usp.each.saeg.badua.core.data.ExecutionDataStore;
import br.usp.each.saeg.badua.core.runtime.RuntimeData;

public abstract class ValidationTargetsTest extends ValidationTest implements ICoverageVisitor {

    private final Class<?> target;

    private Collection<ClassCoverage> classes;

    private Collection<SourceLineDefUseChain> defUses;

    public ValidationTargetsTest(final Class<?> target) {
        this.target = target;
    }

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();
        classes = new ArrayList<ClassCoverage>();
        defUses = new ArrayList<SourceLineDefUseChain>();
        final byte[] bytes = ValidationTestClassLoader.getClassDataAsBytes(target);
        final ExecutionDataStore store = execute(bytes);
        final Analyzer analyzer = new Analyzer(store, this);
        analyzer.analyze(new ClassReader(bytes));
        Assert.assertEquals(1, classes.size());
        for (final MethodCoverage coverage : classes.iterator().next().getMethods()) {
            defUses.addAll(coverage.getDefUses());
        }
    }

    private ExecutionDataStore execute(final byte[] bytes) throws Exception {
        final RuntimeData data = new RuntimeData();
        RT.init(data);
        run(addClass(target.getName(), bytes));
        final ExecutionDataStore store = new ExecutionDataStore();
        data.collect(store);
        return store;
    }

    public abstract void run(final Class<?> klass) throws Exception;

    @Override
    public void visitCoverage(final ClassCoverage coverage) {
        classes.add(coverage);
    }

    public void assertDU(final int def, final int use, final String var, final boolean covered) {
        assertDU(def, use, NONE, var, covered);
    }

    public void assertDU(final int def, final int use, final int target,
            final String var, final boolean covered) {
        for (final SourceLineDefUseChain defUse : defUses) {
            if (defUse.def == def
                    && defUse.use == use
                    && defUse.target == target
                    && defUse.var.equals(var)) {

                if (defUse.covered != covered) {
                    Assert.fail("Invalid DU status");
                } else {
                    return; // Success!
                }
            }
        }
        Assert.fail("DU not found");
    }

    public void assertTotal(final boolean covered, final int total) {
        int count = 0;
        for (final SourceLineDefUseChain defUse : defUses) {
            if (defUse.covered == covered) {
                count++;
            }
        }
        Assert.assertEquals(total, count);
    }

}
