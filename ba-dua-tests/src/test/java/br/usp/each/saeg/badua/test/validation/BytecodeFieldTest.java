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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.BitSet;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import br.usp.each.saeg.badua.core.internal.data.CRC64;
import br.usp.each.saeg.commons.BitSetUtils;

public class BytecodeFieldTest extends ValidationTest {

    private Method method;

    private Field field;

    private Object object;

    private long classId;

    private String className;

    @Override
    @Before
    public void setUp() throws Exception {
        super.setUp();

        className = "Field";

        // class
        final int classVersion = Opcodes.V1_6;
        final int classAccessor = Opcodes.ACC_PUBLIC | Opcodes.ACC_SUPER;
        final String superName = "java/lang/Object";

        // field
        final int fieldAccessor = Opcodes.ACC_PUBLIC;
        final String fieldName = "running";
        final String fieldDesc = "Z";

        // constructor
        final int constructorAccessor = Opcodes.ACC_PUBLIC;
        final String constructorName = "<init>";
        final String constructorDesc = "()V";

        // test method
        final int methodAccessor = Opcodes.ACC_PUBLIC;
        final String methodName = "start";
        final String methodDesc = "()V";

        final ClassWriter cw = new ClassWriter(0);
        final FieldVisitor fw;
        final MethodVisitor cmw;
        final MethodVisitor mw;

        // ---

        // class
        cw.visit(classVersion, classAccessor, className, null, superName, null);

        // field
        fw = cw.visitField(fieldAccessor, fieldName, fieldDesc, null, null);
        fw.visitEnd();

        // constructor
        cmw = cw.visitMethod(constructorAccessor, constructorName, constructorDesc, null, null);
        cmw.visitCode();
        cmw.visitVarInsn(Opcodes.ALOAD, 0);
        cmw.visitMethodInsn(Opcodes.INVOKESPECIAL, superName, constructorName, constructorDesc, false);
        cmw.visitInsn(Opcodes.RETURN);
        cmw.visitMaxs(1, 1);
        cmw.visitEnd();

        // test method
        mw = cw.visitMethod(methodAccessor, methodName, methodDesc, null, null);
        mw.visitCode();
        // block 0 (definitions {0, 1}, p-uses {0, 1})
        final Label target = new Label();
        mw.visitVarInsn(Opcodes.ALOAD, 0);
        mw.visitFieldInsn(Opcodes.GETFIELD, className, fieldName, fieldDesc);
        mw.visitJumpInsn(Opcodes.IFNE, target);
        // block 2 (uses {0}, definitions {1})
        mw.visitVarInsn(Opcodes.ALOAD, 0);
        mw.visitInsn(Opcodes.ICONST_1);
        mw.visitFieldInsn(Opcodes.PUTFIELD, className, fieldName, fieldDesc);
        // block 1 (none)
        mw.visitLabel(target);
        mw.visitInsn(Opcodes.RETURN);
        mw.visitMaxs(2, 1);
        mw.visitEnd();

        // finalize class
        cw.visitEnd();

        final byte[] bytes = cw.toByteArray();
        final Class<?> klass = addClass(className, bytes);
        method = klass.getMethod(methodName);
        field = klass.getField(fieldName);
        object = klass.newInstance();
        classId = CRC64.checksum(bytes);
    }

    /*
    0 = (0, (0,2), 0)
    1 = (0, (0,1), 0)
    2 = (0, 2, 0)
    3 = (0, (0,2), 1)
    4 = (0, (0,1), 1)
    */

    @Test
    public void test0() {
        final boolean running = get();
        Assert.assertEquals(false, running);
        assertCoverage();
    }

    @Test
    public void test1() {
        invoke();
        final boolean running = get();
        Assert.assertEquals(true, running);
        assertCoverage(0, 2, 3);
    }

    @Test
    public void test2() {
        set();
        invoke();
        final boolean running = get();
        Assert.assertEquals(true, running);
        assertCoverage(1, 4);
    }

    @Test
    public void test3() {
        invoke();
        invoke();
        final boolean running = get();
        Assert.assertEquals(true, running);
        assertCoverage(0, 1, 2, 3, 4);
    }

    private void assertCoverage(final int... expectedCoveredChains) {
        final BitSet covered = BitSetUtils.valueOf(getData(classId, className, 1));
        final BitSet expected = new BitSet();
        for (final int ecc : expectedCoveredChains) {
            expected.set(ecc);
        }
        Assert.assertEquals(expected, covered);
    }

    private void invoke() {
        try {
            method.invoke(object);
        } catch (final Exception ignore) {
            throw new RuntimeException(ignore.getCause());
        }
    }

    private boolean get() {
        try {
            return field.getBoolean(object);
        } catch (final Exception ignore) {
            throw new RuntimeException(ignore.getCause());
        }
    }

    private void set() {
        try {
            field.setBoolean(object, true);
        } catch (final Exception ignore) {
            throw new RuntimeException(ignore.getCause());
        }
    }

}
