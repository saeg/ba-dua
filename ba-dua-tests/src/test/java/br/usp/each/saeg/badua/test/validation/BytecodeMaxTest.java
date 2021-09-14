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
package br.usp.each.saeg.badua.test.validation;

import java.lang.reflect.Method;
import java.util.BitSet;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import br.usp.each.saeg.badua.core.internal.data.CRC64;
import br.usp.each.saeg.commons.BitSetUtils;

public class BytecodeMaxTest extends ValidationTest {

    private Method method;

    private long classId;

    private String className;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

        className = "Max";
        final int classVersion = Opcodes.V1_6;
        final int classAccessor = Opcodes.ACC_PUBLIC | Opcodes.ACC_SUPER;
        final String superName = "java/lang/Object";

        final int methodAccessor = Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC;
        final String methodName = "max";
        final String methodDesc = "([II)I";

        final ClassWriter cw = new ClassWriter(0);
        final MethodVisitor mw;

        cw.visit(classVersion, classAccessor, className, null, superName, null);
        mw = cw.visitMethod(methodAccessor, methodName, methodDesc, null, null);
        mw.visitCode();
        // block 0 (definitions {0, 1, 2, 3})
        mw.visitInsn(Opcodes.ICONST_0);
        mw.visitVarInsn(Opcodes.ISTORE, 2);
        mw.visitVarInsn(Opcodes.ALOAD, 0);
        mw.visitVarInsn(Opcodes.ILOAD, 2);
        mw.visitIincInsn(2, 1);
        mw.visitInsn(Opcodes.IALOAD);
        mw.visitVarInsn(Opcodes.ISTORE, 3);
        // block 1 (p-uses {1, 2})
        final Label backLoop = new Label();
        mw.visitLabel(backLoop);
        mw.visitVarInsn(Opcodes.ILOAD, 2);
        mw.visitVarInsn(Opcodes.ILOAD, 1);
        final Label breakLoop = new Label();
        mw.visitJumpInsn(Opcodes.IF_ICMPGE, breakLoop);
        // block 3 (p-uses {0, 2, 3})
        mw.visitVarInsn(Opcodes.ALOAD, 0);
        mw.visitVarInsn(Opcodes.ILOAD, 2);
        mw.visitInsn(Opcodes.IALOAD);
        mw.visitVarInsn(Opcodes.ILOAD, 3);
        final Label jump = new Label();
        mw.visitJumpInsn(Opcodes.IF_ICMPLE, jump);
        // block 5 (definitions {3}, uses {0, 2})
        mw.visitVarInsn(Opcodes.ALOAD, 0);
        mw.visitVarInsn(Opcodes.ILOAD, 2);
        mw.visitInsn(Opcodes.IALOAD);
        mw.visitVarInsn(Opcodes.ISTORE, 3);
        // block 4 (definitions {2}, uses {2})
        mw.visitLabel(jump);
        mw.visitIincInsn(2, 1);
        mw.visitJumpInsn(Opcodes.GOTO, backLoop);
        // block 2 ( uses {3})
        mw.visitLabel(breakLoop);
        mw.visitVarInsn(Opcodes.ILOAD, 3);
        mw.visitInsn(Opcodes.IRETURN);
        mw.visitMaxs(2, 4);
        mw.visitEnd();
        cw.visitEnd();

        final byte[] bytes = cw.toByteArray();
        final Class<?> klass = addClass(className, bytes);
        method = klass.getMethod(methodName, int[].class, int.class);
        classId = CRC64.checksum(bytes);
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
    public void testException1() {
        final int[] array = new int[0];
        try {
            max(array);
        } catch (final Exception ignore) {
            /* ignore */
        }
        assertCoverage();
    }

    @Test
    public void testException2() {
        final int[] array = new int[] { 1, 2, 3 };
        try {
            maxLength(array, array.length + 1);
        } catch (final Exception ignore) {
            /* ignore */
        }
        assertCoverage(0, 2, 3, 5, 7, 9, 10, 12, 15, 17, 19, 21, 22);
    }

    @Test
    public void test1() {
        final int[] array = new int[] { 1 };

        Assert.assertEquals(1, max(array));
        assertCoverage(4, 6, 11);
    }

    @Test
    public void test2() {
        final int[] array = new int[] { 1, 2 };

        Assert.assertEquals(2, max(array));
        assertCoverage(0, 2, 3, 4, 5, 7, 9, 10, 12, 14, 18);
    }

    @Test
    public void test3() {
        final int[] array = new int[] { 2, 1 };

        Assert.assertEquals(2, max(array));
        assertCoverage(1, 3, 4, 5, 8, 9, 11, 13, 18);
    }

    @Test
    public void test4() {
        final int[] array = new int[] { 1, 2, 3 };

        Assert.assertEquals(3, max(array));
        assertCoverage(0, 2, 3, 4, 5, 7, 9, 10, 12, 14, 15, 17, 18, 19, 21, 22);
    }

    @Test
    public void test5() {
        final int[] array = new int[] { 3, 2, 1 };

        Assert.assertEquals(3, max(array));
        assertCoverage(1, 3, 4, 5, 8, 9, 11, 13, 17, 18, 20, 21);
    }

    @Test
    public void test6() {
        final int[] array = new int[] { 1, 3, 2 };

        Assert.assertEquals(3, max(array));
        assertCoverage(0, 1, 2, 3, 4, 5, 7, 9, 10, 12, 14, 16, 17, 18, 20, 21);
    }

    @Test
    public void test7() {
        final int[] array = new int[] { 1, 2, 3, 2 };

        Assert.assertEquals(3, max(array));
        assertCoverage(0, 1, 2, 3, 4, 5, 7, 9, 10, 12, 14, 15, 16, 17, 18, 19, 20, 21, 22);
    }

    @Test
    public void test8() {
        final int[] array = new int[] { 2, 1, 3, 2 };

        Assert.assertEquals(3, max(array));
        assertCoverage(0, 1, 2, 3, 4, 5, 8, 9, 12, 13, 14, 16, 17, 18, 19, 20, 21, 22);
    }

    private void assertCoverage(final int... expectedCoveredChains) {
        final BitSet covered = BitSetUtils.valueOf(getData(classId, className, 1));
        final BitSet expected = new BitSet();
        for (final int ecc : expectedCoveredChains) {
            expected.set(ecc);
        }
        Assert.assertEquals(expected, covered);
    }

    private int maxLength(final int[] array, final int length) {
        try {
            return (Integer) method.invoke(null, array, length);
        } catch (final Exception ignore) {
            throw new RuntimeException(ignore.getCause());
        }
    }

    private int max(final int[] array) {
        return maxLength(array, array.length);
    }

}
