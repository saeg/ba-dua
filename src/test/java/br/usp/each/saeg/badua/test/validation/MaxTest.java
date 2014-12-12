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
package br.usp.each.saeg.badua.test.validation;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.BitSet;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import br.usp.each.saeg.badua.agent.rt.internal.RT;
import br.usp.each.saeg.badua.core.internal.data.CRC64;
import br.usp.each.saeg.badua.core.runtime.RuntimeData;
import br.usp.each.saeg.commons.BitSetUtils;

public class MaxTest extends ValidationTest {

    private Class<?> klass;

    private Method method;

    private long classId;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

        final int classVersion = Opcodes.V1_6;
        final int classAccessor = Opcodes.ACC_PUBLIC | Opcodes.ACC_SUPER;
        final String className = "Max";
        final String superName = "java/lang/Object";

        final int methodAccessor = Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC;
        final String methodName = "max";
        final String methodDesc = "([II)I";

        final ClassWriter cw = new ClassWriter(0);
        final MethodVisitor mw;

        cw.visit(classVersion, classAccessor, className, null, superName, null);
        mw = cw.visitMethod(methodAccessor, methodName, methodDesc, null, null);
        mw.visitCode();
        // block 0 (definitions {array, length, i, max})
        mw.visitInsn(Opcodes.ICONST_0);
        mw.visitVarInsn(Opcodes.ISTORE, 2);
        mw.visitVarInsn(Opcodes.ALOAD, 0);
        mw.visitVarInsn(Opcodes.ILOAD, 2);
        mw.visitIincInsn(2, 1);
        mw.visitInsn(Opcodes.IALOAD);
        mw.visitVarInsn(Opcodes.ISTORE, 3);
        // block 1 (p-uses {i, length})
        final Label backLoop = new Label();
        mw.visitLabel(backLoop);
        mw.visitVarInsn(Opcodes.ILOAD, 2);
        mw.visitVarInsn(Opcodes.ILOAD, 1);
        final Label breakLoop = new Label();
        mw.visitJumpInsn(Opcodes.IF_ICMPGE, breakLoop);
        // block 3 (p-uses {array, i, max})
        mw.visitVarInsn(Opcodes.ALOAD, 0);
        mw.visitVarInsn(Opcodes.ILOAD, 2);
        mw.visitInsn(Opcodes.IALOAD);
        mw.visitVarInsn(Opcodes.ILOAD, 3);
        final Label jump = new Label();
        mw.visitJumpInsn(Opcodes.IF_ICMPLE, jump);
        // block 5 (definitions {max}, uses {array, i})
        mw.visitVarInsn(Opcodes.ALOAD, 0);
        mw.visitVarInsn(Opcodes.ILOAD, 2);
        mw.visitInsn(Opcodes.IALOAD);
        mw.visitVarInsn(Opcodes.ISTORE, 3);
        // block 4 (definitions {i}, uses {i})
        mw.visitLabel(jump);
        mw.visitIincInsn(2, 1);
        mw.visitJumpInsn(Opcodes.GOTO, backLoop);
        // block 2 ( uses {max})
        mw.visitLabel(breakLoop);
        mw.visitVarInsn(Opcodes.ILOAD, 3);
        mw.visitInsn(Opcodes.IRETURN);
        mw.visitMaxs(2, 4);
        mw.visitEnd();
        cw.visitEnd();

        final byte[] bytes = cw.toByteArray();
        klass = addClass(className, bytes);
        method = klass.getMethod(methodName, int[].class, int.class);
        classId = CRC64.checksum(bytes);

        RT.init(new RuntimeData());
    }

    /*
    0  = (0, (3,5), 0)
    1  = (0, (3,4), 0)
    2  = (0, 5, 0)
    3  = (0, (1,3), 1)
    4  = (0, (1,2), 1)
    5  = (0, (1,3), 2)
    6  = (0, (1,2), 2)
    7  = (0, (3,5), 2)
    8  = (0, (3,4), 2)
    9  = (0, 4, 2)
    10 = (0, 5, 2)
    11 = (0, 2, 3)
    12 = (0, (3,5), 3)
    13 = (0, (3,4), 3)
    14 = (5, 2, 3)
    15 = (5, (3,5), 3)
    16 = (5, (3,4), 3)
    17 = (4, (1,3), 2)
    18 = (4, (1,2), 2)
    19 = (4, (3,5), 2)
    20 = (4, (3,4), 2)
    21 = (4, 4, 2)
    22 = (4, 5, 2)
    */

    @Test
    public void testException1() throws Exception {
        Throwable e = null;
        try {
            method.invoke(null, new int[0], 0);
        } catch (final InvocationTargetException exception) {
            e = exception.getCause();
        }
        Assert.assertTrue(e instanceof IndexOutOfBoundsException);
        assertCoverage();
    }

    @Test
    public void testException2() throws Exception {
        Throwable e = null;
        try {
            method.invoke(null, new int[] { 1, 2, 3, 4, 5 }, 6);
        } catch (final InvocationTargetException exception) {
            e = exception.getCause();
        }
        Assert.assertTrue(e instanceof IndexOutOfBoundsException);
        assertCoverage();
    }

    @Test
    public void test1() throws Exception {
        final int max = (Integer) method.invoke(null, new int[] { 1, 2, 3, 4, 5 }, 5);
        Assert.assertEquals(5, max);
        assertCoverage(0, 2, 3, 4, 5, 7, 9, 10, 12, 14, 15, 17, 18, 19, 21, 22);
    }

    @Test
    public void test2() throws Exception {
        final int max = (Integer) method.invoke(null, new int[] { 5, 4, 3, 2, 1 }, 5);
        Assert.assertEquals(5, max);
        assertCoverage(1, 3, 4, 5, 8, 9, 11, 13, 17, 18, 20, 21);
    }

    @Test
    public void test3() throws Exception {
        final int max = (Integer) method.invoke(null, new int[] { 5 }, 1);
        Assert.assertEquals(5, max);
        assertCoverage(4, 6, 11);
    }

    @Test
    public void test4() throws Exception {
        final int max = (Integer) method.invoke(null, new int[] { 1, 2, 5, 3, 4 }, 5);
        Assert.assertEquals(5, max);
        assertCoverage(0, 1, 2, 3, 4, 5, 7, 9, 10, 12, 14, 15, 16, 17, 18, 19, 20, 21, 22);
    }

    @Test
    public void test5() throws Exception {
        method.invoke(null, new int[] { 5 }, 1);
        method.invoke(null, new int[] { 5, 4 }, 2);
        method.invoke(null, new int[] { 1, 2, 5, 3, 4 }, 5);
        assertCoverage(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22);
    }

    private void assertCoverage(final int... expectedCoveredChains) {
        final BitSet covered = BitSetUtils.valueOf(RT.getData(classId, 1));
        final BitSet expected = new BitSet();
        for (final int ecc : expectedCoveredChains) {
            expected.set(ecc);
        }
        Assert.assertEquals(expected, covered);
    }

}
