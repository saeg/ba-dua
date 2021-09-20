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

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Handle;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.TypePath;
import org.objectweb.asm.tree.MethodNode;

public class CatchAndThrowMethodVisitor extends MethodVisitor {

    private final String type;

    private final boolean withFrames;

    private Label start;

    private Label end;

    private Label handler;

    private boolean started;

    public CatchAndThrowMethodVisitor(final String type, final MethodNode mn, final boolean withFrames) {
        super(Opcodes.ASM9, mn);
        this.type = type;
        this.withFrames = withFrames;
    }

    @Override
    public void visitCode() {
        start = new Label();
        end = new Label();
        handler = new Label();
        started = false;
        super.visitCode();
    }

    @Override
    public void visitMaxs(final int maxStack, final int maxLocals) {
        visitTryCatchBlockEnd();
        super.visitMaxs(Math.max(maxStack, 1), maxLocals);
    }

    @Override
    public void visitFrame(final int type,
            final int nLocal, final Object[] local,
            final int nStack, final Object[] stack) {
        visitTryCatchBlockStart();
        super.visitFrame(type, nLocal, local, nStack, stack);
    }

    @Override
    public void visitInsn(final int opcode) {
        visitTryCatchBlockStart();
        super.visitInsn(opcode);
    }

    @Override
    public void visitIntInsn(final int opcode, final int operand) {
        visitTryCatchBlockStart();
        super.visitIntInsn(opcode, operand);
    }

    @Override
    public void visitVarInsn(final int opcode, final int var) {
        visitTryCatchBlockStart();
        super.visitVarInsn(opcode, var);
    }

    @Override
    public void visitTypeInsn(final int opcode, final String type) {
        visitTryCatchBlockStart();
        super.visitTypeInsn(opcode, type);
    }

    @Override
    public void visitFieldInsn(final int opcode, final String owner, final String name, final String desc) {
        visitTryCatchBlockStart();
        super.visitFieldInsn(opcode, owner, name, desc);
    }

    @Override
    @Deprecated
    public void visitMethodInsn(final int opcode, final String owner, final String name, final String desc) {
        visitTryCatchBlockStart();
        super.visitMethodInsn(opcode, owner, name, desc);
    }

    @Override
    public void visitMethodInsn(final int opcode, final String owner, final String name, final String desc, final boolean itf) {
        visitTryCatchBlockStart();
        super.visitMethodInsn(opcode, owner, name, desc, itf);
    }

    @Override
    public void visitInvokeDynamicInsn(final String name, final String desc, final Handle bsm, final Object... bsmArgs) {
        visitTryCatchBlockStart();
        super.visitInvokeDynamicInsn(name, desc, bsm, bsmArgs);
    }

    @Override
    public void visitJumpInsn(final int opcode, final Label label) {
        visitTryCatchBlockStart();
        super.visitJumpInsn(opcode, label);
    }

    @Override
    public void visitLabel(final Label label) {
        visitTryCatchBlockStart();
        super.visitLabel(label);
    }

    @Override
    public void visitLdcInsn(final Object cst) {
        visitTryCatchBlockStart();
        super.visitLdcInsn(cst);
    }

    @Override
    public void visitIincInsn(final int var, final int increment) {
        visitTryCatchBlockStart();
        super.visitIincInsn(var, increment);
    }

    @Override
    public void visitMultiANewArrayInsn(final String desc, final int dims) {
        visitTryCatchBlockStart();
        super.visitMultiANewArrayInsn(desc, dims);
    }

    @Override
    public AnnotationVisitor visitInsnAnnotation(final int typeRef, final TypePath typePath, final String desc, final boolean visible) {
        visitTryCatchBlockStart();
        return super.visitInsnAnnotation(typeRef, typePath, desc, visible);
    }

    @Override
    public void visitLineNumber(final int line, final Label start) {
        visitTryCatchBlockStart();
        super.visitLineNumber(line, start);
    }

    private void visitTryCatchBlockStart() {
        if (!started) {
            started = true;
            visitTryCatchBlock(start, end, handler, type);
            visitLabel(start);
        }
    }

    private void visitTryCatchBlockEnd() {
        if (started) {
            if (withFrames) {
                mv.visitFrame(Opcodes.F_NEW,
                        0, new Object[] {},
                        1, new Object[] { "java/lang/Throwable" });
            }
            visitLabel(end);
            visitLabel(handler);
            visitInsn(Opcodes.ATHROW);
        }
    }

}
