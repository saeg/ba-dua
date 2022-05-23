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
package br.usp.each.saeg.badua.agent.rt;

import br.usp.each.saeg.badua.agent.rt.internal.Agent;

/**
 * Entry point (API) to access the BA-DUA agent runtime.
 */
public final class BaDuaRuntime {

    private BaDuaRuntime() {
        // no instances
    }

    /**
     * Returns the agent instance of the BA-DUA runtime in this JVM.
     *
     * @return agent instance
     */
    public static IAgent getAgent() {
        return Agent.getInstance();
    }

}
