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
package br.usp.each.saeg.badua.agent.rt.internal;

import br.usp.each.saeg.badua.core.runtime.IRuntime;
import br.usp.each.saeg.badua.core.runtime.RuntimeData;
import br.usp.each.saeg.badua.core.runtime.StaticAccessGenerator;

@Deprecated
public final class RT extends StaticAccessGenerator implements IRuntime {

    private static RuntimeData DATA;

    public RT() {
        super(RT.class.getName());
    }

    @Override
    public void startup(final RuntimeData data) {
        init(data);
    }

    public static void init(final RuntimeData data) {
        DATA = data;
    }

    public static long[] getData(final long classId, final String className, final int size) {
        return DATA.getExecutionData(classId, className, size).getData();
    }

}
