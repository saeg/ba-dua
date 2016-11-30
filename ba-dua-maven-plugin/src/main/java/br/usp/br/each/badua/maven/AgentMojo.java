/**
 * Copyright (c) 2014, 2017 University of Sao Paulo and Contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Roberto Araujo - initial API and implementation and/or initial documentation
 */
package br.usp.br.each.badua.maven;

import static java.lang.String.format;
import static org.apache.maven.plugins.annotations.LifecyclePhase.INITIALIZE;
import static org.apache.maven.plugins.annotations.ResolutionScope.RUNTIME;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo(name = "prepare-agent", defaultPhase = INITIALIZE, requiresDependencyResolution = RUNTIME, threadSafe = true)
public class AgentMojo extends AbstractMojo {

    private static final String SUREFIRE_ARG_LINE = "argLine";

    // @Parameter(property = "project", readonly = true)
    // private MavenProject project;

    @Parameter(property = "badua.skip", defaultValue = "false")
    private boolean skip;

    @Override
    public void execute() {
        if (skip) {
            skipMojo();
        } else {
            executeMojo();
        }
    }

    private void skipMojo() {
        getLog().info("Skipping BA-DUA execution because property badua.skip is set.");
    }

    private void executeMojo() {
        // final Properties projectProperties = project.getProperties();
        // final String oldSurefireArgLine = projectProperties.getProperty(SUREFIRE_ARG_LINE);
        final String newSurefireArgLine = "";
        getLog().info(format("%s was set to %s", SUREFIRE_ARG_LINE, newSurefireArgLine));
    }

}
