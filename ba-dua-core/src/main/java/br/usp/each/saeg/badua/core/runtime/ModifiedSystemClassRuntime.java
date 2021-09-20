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
package br.usp.each.saeg.badua.core.runtime;

import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.lang.instrument.Instrumentation;
import java.security.ProtectionDomain;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.Type;

import br.usp.each.saeg.badua.core.internal.instr.InstrSupport;

public class ModifiedSystemClassRuntime extends StaticAccessGenerator implements IRuntime {

    private final Class<?> systemClass;

    public ModifiedSystemClassRuntime(final Class<?> systemClass) {
        super(systemClass.getName());
        this.systemClass = systemClass;
    }

    @Override
    public void startup(final RuntimeData data) throws Exception {
        systemClass.getField(InstrSupport.RUNTIME_FIELD_NAME).set(null, data);
    }

    public static IRuntime createFor(final Instrumentation inst, final String forClassName)
            throws ClassNotFoundException {

        final ClassFileTransformer transformer = new ClassFileTransformer() {

            @Override
            public byte[] transform(final ClassLoader loader,
                                    final String className,
                                    final Class<?> classBeingRedefined,
                                    final ProtectionDomain protectionDomain,
                                    final byte[] classfileBuffer) throws IllegalClassFormatException {

                if (className.equals(forClassName)) {
                    return instrument(classfileBuffer);
                }
                return null;
            }

        };
        inst.addTransformer(transformer);
        final Class<?> clazz = Class.forName(forClassName.replace('/', '.'));
        inst.removeTransformer(transformer);
        try {
            clazz.getField(InstrSupport.RUNTIME_FIELD_NAME);
        } catch (final NoSuchFieldException e) {
            throw new RuntimeException(String.format(
                    "Class %s could not be instrumented.", forClassName), e);
        }
        return new ModifiedSystemClassRuntime(clazz);
    }

    private static byte[] instrument(final byte[] buffer) {
        final ClassReader reader = new ClassReader(buffer);
        final ClassWriter writer = new ClassWriter(reader, 0);
        reader.accept(new ClassVisitor(Opcodes.ASM9, writer) {
            @Override
            public void visitEnd() {

                final FieldVisitor fv = cv.visitField(
                        InstrSupport.RUNTIME_FIELD_ACC,
                        InstrSupport.RUNTIME_FIELD_NAME,
                        InstrSupport.RUNTIME_FIELD_DESC,
                        null, null);
                fv.visitEnd();

                final MethodVisitor mv = cv.visitMethod(
                        InstrSupport.RUNTIME_ACC,
                        InstrSupport.RUNTIME_NAME,
                        InstrSupport.RUNTIME_DESC,
                        null, null);
                mv.visitCode();

                mv.visitFieldInsn(Opcodes.GETSTATIC, reader.getClassName(),
                        InstrSupport.RUNTIME_FIELD_NAME, InstrSupport.RUNTIME_FIELD_DESC);
                mv.visitVarInsn(Opcodes.LLOAD, 0);
                mv.visitVarInsn(Opcodes.ALOAD, 2);
                mv.visitVarInsn(Opcodes.ILOAD, 3);
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL,
                        Type.getInternalName(RuntimeData.class),
                        InstrSupport.RUNTIME_NAME,
                        InstrSupport.RUNTIME_DESC,
                        false);
                mv.visitInsn(Opcodes.ARETURN);

                mv.visitMaxs(5, 4);
                mv.visitEnd();

                super.visitEnd();
            }
        }, ClassReader.EXPAND_FRAMES);
        return writer.toByteArray();
    }

}
