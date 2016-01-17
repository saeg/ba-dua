/**
 * Copyright (c) 2014, 2016 University of Sao Paulo and Contributors.
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

public final class LongProbe extends Probe {

    public LongProbe(final MethodNode methodNode, final int window) {
        super(methodNode, window);
    }

    @Override
    public int getType() {
        return BA_LONG_PROBE;
    }

    @Override
    public void accept(final MethodVisitor mv) {

        // update covered
        if (potcov != 0) {
            mv.visitVarInsn(Opcodes.LLOAD, vAlive);

            if (!singlePredecessor && potcovpuse != 0) {
                mv.visitVarInsn(Opcodes.LLOAD, vSleepy);
                mv.visitInsn(Opcodes.LAND);
            }

            mv.visitLdcInsn(potcov);
            mv.visitInsn(Opcodes.LAND);
            mv.visitVarInsn(Opcodes.LLOAD, vCovered);
            mv.visitInsn(Opcodes.LOR);
            mv.visitVarInsn(Opcodes.LSTORE, vCovered);
        }

        // update alive
        if (disabled != 0) {
            mv.visitLdcInsn(~disabled);
            mv.visitVarInsn(Opcodes.LLOAD, vAlive);
            mv.visitInsn(Opcodes.LAND);
        }
        if (born != 0) {
            if (disabled == 0) {
                mv.visitVarInsn(Opcodes.LLOAD, vAlive);
            }
            mv.visitLdcInsn(born);
            mv.visitInsn(Opcodes.LOR);
        }
        if (disabled != 0 || born != 0) {
            mv.visitVarInsn(Opcodes.LSTORE, vAlive);
        }

        // update sleepy
        if (sleepy != 0) {
            mv.visitLdcInsn(~sleepy);
        } else {
            mv.visitLdcInsn(-1L);
        }
        mv.visitVarInsn(Opcodes.LSTORE, vSleepy);
    }

}
