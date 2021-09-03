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
package br.usp.each.saeg.badua.core.internal.instr;

import static java.lang.String.format;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import br.usp.each.saeg.badua.core.runtime.RuntimeData;

public final class InstrSupport {

    private InstrSupport() {
        // No instances
    }

    public static final String RUNTIME_FIELD_NAME = "DATA";

    public static final String RUNTIME_FIELD_DESC = Type.getDescriptor(RuntimeData.class);

    public static final int RUNTIME_FIELD_ACC = Opcodes.ACC_SYNTHETIC |
                                                Opcodes.ACC_PUBLIC |
                                                Opcodes.ACC_STATIC |
                                                Opcodes.ACC_TRANSIENT;

    public static final String RUNTIME_NAME = "getData";

    public static final String RUNTIME_DESC = "(JLjava/lang/String;I)[J";

    public static final int RUNTIME_ACC = Opcodes.ACC_SYNTHETIC |
                                          Opcodes.ACC_PUBLIC |
                                          Opcodes.ACC_STATIC |
                                          Opcodes.ACC_FINAL;

    // --- Data Field

    /**
     * Name of the field that stores data-flow coverage of a class.
     */
    public static final String DATAFIELD_NAME = "$data";

    /**
     * Data type of the field that stores data-flow coverage for a class (
     * <code>long[]</code>).
     */
    public static final String DATAFIELD_DESC = "[J";

    /**
     * Access modifiers of the field that stores data-flow coverage of a class.
     */
    public static final int DATAFIELD_ACC = Opcodes.ACC_SYNTHETIC |
                                            Opcodes.ACC_PRIVATE |
                                            Opcodes.ACC_STATIC |
                                            Opcodes.ACC_TRANSIENT;

    // --- Initialization Method

    /**
     * Name of the method that returns data-flow coverage field of a class.
     */
    public static final String DATAMETHOD_NAME = "$getData";

    /**
     * Descriptor of the method that returns data-flow coverage field of a class.
     */
    public static final String DATAMETHOD_DESC = "()[J";

    /**
     * Access modifiers of the method that returns data-flow coverage field of a class.
     */
    public static final int DATAMETHOD_ACC = Opcodes.ACC_SYNTHETIC |
                                             Opcodes.ACC_PRIVATE |
                                             Opcodes.ACC_STATIC |
                                             Opcodes.ACC_FINAL;

    /**
     * Ensures that the given member does not correspond to a internal member
     * created by the instrumentation process. This would mean that the class is
     * already instrumented.
     *
     * @param member
     *            name of the member to check
     * @param owner
     *            name of the class owning the member
     * @throws IllegalStateException
     *             thrown if the member has the same name than the
     *             instrumentation member
     */
    public static void assertNotInstrumented(final String member, final String owner)
            throws IllegalStateException {
        if (member.equals(DATAFIELD_NAME) || member.equals(DATAMETHOD_NAME)) {
            throw new IllegalStateException(format("Class %s is already instrumented.", owner));
        }
    }

    /**
     * Generates the instruction to push the given int value on the stack.
     * Implementation taken from
     * {@link org.objectweb.asm.commons.GeneratorAdapter#push(int)}.
     *
     * @param mv
     *            visitor to emit the instruction
     * @param value
     *            the value to be pushed on the stack.
     */
    public static void push(final MethodVisitor mv, final int value) {
        if (value >= -1 && value <= 5) {
            mv.visitInsn(Opcodes.ICONST_0 + value);
        } else if (value >= Byte.MIN_VALUE && value <= Byte.MAX_VALUE) {
            mv.visitIntInsn(Opcodes.BIPUSH, value);
        } else if (value >= Short.MIN_VALUE && value <= Short.MAX_VALUE) {
            mv.visitIntInsn(Opcodes.SIPUSH, value);
        } else {
            mv.visitLdcInsn(Integer.valueOf(value));
        }
    }

}
