/**
 * Copyright (c) 2014, 2017 University of Sao Paulo and Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Roberto Araujo - initial API and implementation and/or initial documentation
 */
package br.usp.each.saeg.badua.core.internal.instr;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.MethodNode;

public class CatchAndThrowMethodVisitor extends MethodVisitor {

    private final String type;

    private Label start;

    private Label end;

    private Label handler;

    public CatchAndThrowMethodVisitor(final String type, final MethodNode mn) {
        super(Opcodes.ASM5, mn);
        this.type = type;
    }

    @Override
    public void visitCode() {
        start = new Label();
        end = new Label();
        handler = new Label();
        visitLabel(start);
    }

    @Override
    public void visitEnd() {
        visitLabel(end);
        visitLabel(handler);
        visitInsn(Opcodes.ATHROW);
        visitTryCatchBlock(start, end, handler, type);
        super.visitEnd();
    }

    @Override
    public void visitMaxs(final int maxStack, final int maxLocals) {
        super.visitMaxs(Math.max(maxStack, 1), maxLocals);
    }

}
