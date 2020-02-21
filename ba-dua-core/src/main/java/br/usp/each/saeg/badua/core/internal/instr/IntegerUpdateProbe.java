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

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.MethodNode;

public final class IntegerUpdateProbe extends Probe {

    public final String owner;

    public final int index;

    public IntegerUpdateProbe(final MethodNode methodNode, final String owner, final int index) {
        super(methodNode);
        this.owner = owner;
        this.index = index;
    }

    @Override
    public int getType() {
        return BA_INT_UPDATE_PROBE;
    }

    @Override
    public void accept(final MethodVisitor mv) {
        mv.visitVarInsn(Opcodes.ILOAD, vCovered);
        mv.visitInsn(Opcodes.I2L);
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, owner,
                InstrSupport.DATAMETHOD_NAME,
                InstrSupport.DATAMETHOD_DESC, false);
        InstrSupport.push(mv, index);
        mv.visitInsn(Opcodes.DUP2_X2);
        mv.visitInsn(Opcodes.LALOAD);
        mv.visitInsn(Opcodes.LOR);
        mv.visitInsn(Opcodes.LASTORE);
    }

}
