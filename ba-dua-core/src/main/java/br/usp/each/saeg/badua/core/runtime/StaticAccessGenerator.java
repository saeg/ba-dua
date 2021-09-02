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

import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import br.usp.each.saeg.badua.core.internal.instr.InstrSupport;

public class StaticAccessGenerator implements IExecutionDataAccessorGenerator {

    private final String runtime;

    public StaticAccessGenerator(final String runtime) {
        this.runtime = runtime;
    }

    @Override
    public int generateDataAccessor(
            final long classId, final String className, final int size, final MethodVisitor mv) {

        mv.visitLdcInsn(classId);
        mv.visitLdcInsn(className);
        InstrSupport.push(mv, size);
        mv.visitMethodInsn(Opcodes.INVOKESTATIC,
                runtime.replace('.', '/'),
                InstrSupport.RUNTIME_NAME,
                InstrSupport.RUNTIME_DESC,
                false);

        return 4;
    }

}
