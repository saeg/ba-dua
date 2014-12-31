/**
 * Copyright (c) 2014, 2015 University of Sao Paulo and Contributors.
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

public final class IntegerProbe extends Probe {

    public IntegerProbe(final MethodNode methodNode) {
        super(methodNode);
    }

    @Override
    public int getType() {
        return BA_INT_PROBE;
    }

    @Override
    public void accept(final MethodVisitor mv) {

        // update covered
        if (potcov != 0) {
            mv.visitVarInsn(Opcodes.ILOAD, vAlive);

            if (!singlePredecessor && potcovpuse != 0) {
                mv.visitVarInsn(Opcodes.ILOAD, vSleepy);
                mv.visitInsn(Opcodes.IAND);
            }

            InstrSupport.push(mv, (int) potcov);
            mv.visitInsn(Opcodes.IAND);
            mv.visitVarInsn(Opcodes.ILOAD, vCovered);
            mv.visitInsn(Opcodes.IOR);
            mv.visitVarInsn(Opcodes.ISTORE, vCovered);
        }

        // update alive
        if (disabled != 0) {
            InstrSupport.push(mv, ~(int) disabled);
            mv.visitVarInsn(Opcodes.ILOAD, vAlive);
            mv.visitInsn(Opcodes.IAND);
        }
        if (born != 0) {
            if (disabled == 0) {
                mv.visitVarInsn(Opcodes.ILOAD, vAlive);
            }
            InstrSupport.push(mv, (int) born);
            mv.visitInsn(Opcodes.IOR);
        }
        if (disabled != 0 || born != 0) {
            mv.visitVarInsn(Opcodes.ISTORE, vAlive);
        }

        // update sleepy
        InstrSupport.push(mv, ~(int) sleepy);
        mv.visitVarInsn(Opcodes.ISTORE, vSleepy);

    }

}
