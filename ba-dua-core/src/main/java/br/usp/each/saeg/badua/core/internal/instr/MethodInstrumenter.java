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

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.commons.CodeSizeEvaluator;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.MethodNode;

public class MethodInstrumenter extends MethodNode {

    private final String[] exceptions;

    private final MethodVisitor next;

    private final MethodTransformer mt;

    public MethodInstrumenter(final int access,
                              final String name,
                              final String desc,
                              final String signature,
                              final String[] exceptions,
                              final MethodVisitor next,
                              final MethodTransformer mt) {

        super(Opcodes.ASM9, access, name, desc, signature, exceptions);
        this.exceptions = exceptions;
        this.next = next;
        this.mt = mt;
    }

    @Override
    public void visitFrame(final int type,
                           final int nLocal, final Object[] local,
                           final int nStack, final Object[] stack) {

        super.visitFrame(type, nLocal, local, nStack, stack);

        final FrameNode frame = (FrameNode) instructions.getLast();
        for (int i = 0; i < nLocal; i++) {
            if (local[i] instanceof Label) {
                ((LabelFrameNode) ((Label) local[i]).info).addFrameNode(frame);
            }
        }
        for (int i = 0; i < nStack; i++) {
            if (stack[i] instanceof Label) {
                ((LabelFrameNode) ((Label) stack[i]).info).addFrameNode(frame);
            }
        }
    }

    @Override
    public void visitEnd() {
        if (next == null)
            return;

        final MethodNode original = new MethodNode(api, access, name, desc, signature, exceptions);
        final CodeSizeEvaluator sizeEval = new CodeSizeEvaluator(null);

        // 1. create a copy of the unmodified MethodNode
        accept(original);
        // 2. transform
        transform();
        // 3. evaluate new size
        accept(sizeEval);

        // size is fine (lower than 65535 bytes)
        if (sizeEval.getMaxSize() < 0xFFFF)
            accept(next);

        // size overflow
        else {
            sizeOverflow();
            original.accept(next);
        }
    }

    private void transform() {
        if (mt != null)
            mt.transform(this);
    }

    @Override
    protected LabelFrameNode getLabelNode(final Label l) {
        if (!(l.info instanceof LabelFrameNode)) {
            l.info = new LabelFrameNode();
        }
        return (LabelFrameNode) l.info;
    }

    public void sizeOverflow() {
    }

}
