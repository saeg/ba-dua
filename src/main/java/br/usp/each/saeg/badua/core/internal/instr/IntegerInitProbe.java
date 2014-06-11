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
package br.usp.each.saeg.badua.core.internal.instr;

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.MethodNode;

public final class IntegerInitProbe extends Probe {

    public IntegerInitProbe(final MethodNode methodNode) {
        super(methodNode);
    }

    @Override
    public int getType() {
        return BA_INT_INIT_PROBE;
    }

    @Override
    public void accept(final MethodVisitor mv) {
        mv.visitInsn(Opcodes.ICONST_0);
        mv.visitVarInsn(Opcodes.ISTORE, vCovered);
        mv.visitInsn(Opcodes.ICONST_0);
        mv.visitVarInsn(Opcodes.ISTORE, vAlive);
        mv.visitInsn(Opcodes.ICONST_M1);
        mv.visitVarInsn(Opcodes.ISTORE, vSleepy);
    }

}
