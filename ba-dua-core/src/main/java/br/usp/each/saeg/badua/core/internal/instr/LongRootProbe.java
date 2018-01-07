/**
 * Copyright (c) 2014, 2018 University of Sao Paulo and Contributors.
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

public final class LongRootProbe extends Probe {

    public LongRootProbe(final MethodNode methodNode, final int window) {
        super(methodNode, window);
    }

    @Override
    public int getType() {
        return BA_LONG_ROOT_PROBE;
    }

    @Override
    public void accept(final MethodVisitor mv) {
        if (born != 0) {
            mv.visitLdcInsn(born);
            mv.visitVarInsn(Opcodes.LSTORE, vAlive);
        }
        if (sleepy != 0) {
            mv.visitLdcInsn(~sleepy);
            mv.visitVarInsn(Opcodes.LSTORE, vSleepy);
        }
    }

}
