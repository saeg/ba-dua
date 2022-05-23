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

import java.io.IOException;

/**
 * BA-DUA Runtime API
 */
public interface IAgent {

    /**
     * Resets all coverage information.
     */
    void reset();

    /**
     * Triggers a dump of the current execution data.
     *
     * @param reset if <code>true</code> the current execution data is
     *              cleared afterwards
     * @throws IOException if the output can't write execution data
     */
    void dump(boolean reset) throws IOException;

    /**
     * Returns current execution data.
     *
     * @param reset if <code>true</code> the current execution data is
     *              cleared afterwards
     * @return dump of current execution data in BA-DUA binary format
     */
    byte[] getExecutionData(boolean reset);

}
