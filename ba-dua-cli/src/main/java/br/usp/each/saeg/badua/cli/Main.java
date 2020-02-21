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
package br.usp.each.saeg.badua.cli;

import java.util.Arrays;

public class Main {

    private enum Command {
        instrument, report
    }

    public static void main(final String[] args) {
        if (args.length == 0) {
            exit("no command specified");
        }

        final String command = args[0];
        final String[] commandArgs = Arrays.copyOfRange(args, 1, args.length);

        try {
            switch (Command.valueOf(command)) {
            case instrument:
                Instrument.main(commandArgs);
                break;
            case report:
                Report.main(commandArgs);
                break;
            }
        } catch (final IllegalArgumentException e) {
            exit("no such command: " + command);
        }
    }

    private static void exit(final String message) {
        System.err.println(message);
        System.exit(1);
    }

}
