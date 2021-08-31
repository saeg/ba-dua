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
package br.usp.each.saeg.badua.test.validation.targets;

public class Finally extends AbstractTarget {
	
    public static void run() {
        finally1();
        finally2();
        finally3();
        finallyExceptionCatch1();
        finallyExceptionCatch2();
        finallyExceptionCatch3();
    }

    /**
     * This method does not include a DU
     * 
     * Since we do not consider exception flow,
     * the definition and use occurs in the same block
     */
    public static void finally1() {
        var = 0;
        try {
        	nop();
        } finally {
        	use(var);
        }
    }
    
    public static void finally2() {
        var = 0;
        if (t()) {
        	try {
            	nop();
            } finally {
            	use(var);
            }
        }
    }
    
    public static void finally3() {
        var = 0;
    	try {
    		if (f()) {
    			nop();
    		}
        } finally {
        	use(var);
        }
    }
    
    public static void finallyExceptionCatch1() {
        var = 0;
        try {
        	ex();
        } catch (Ex e) {
			nop();
		} finally {
        	use(var);
        }
    }
    
    public static void finallyExceptionCatch2() {
        var = 0;
        if (t()) {
        	try {
            	ex();
            } catch (Ex e) {
    			nop();
    		} finally {
            	use(var);
            }
        }
    }
    
    public static void finallyExceptionCatch3() {
        var = 0;
    	try {
    		if (t()) {
    			ex();
    		}
        } catch (Ex e) {
			nop();
		} finally {
        	use(var);
        }
    }

    private static int var;

}
