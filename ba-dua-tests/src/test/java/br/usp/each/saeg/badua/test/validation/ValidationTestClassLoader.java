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
package br.usp.each.saeg.badua.test.validation;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import br.usp.each.saeg.commons.io.Files;

public class ValidationTestClassLoader extends ClassLoader {

    private final Map<String, byte[]> classes = new HashMap<String, byte[]>();

    public ValidationTestClassLoader() {
        super(ValidationTestClassLoader.class.getClassLoader());
    }

    public Class<?> add(final String name, final byte[] bytes) {
        classes.put(name, bytes);
        return load(name);
    }

    public static InputStream getClassData(final Class<?> clazz) {
        final String resource = "/" + clazz.getName().replace('.', '/') + ".class";
        return clazz.getResourceAsStream(resource);
    }

    public static byte[] getClassDataAsBytes(final Class<?> clazz) throws IOException {
        final InputStream in = getClassData(clazz);
        try {
            return Files.toByteArray(in);
        } finally {
            in.close();
        }
    }

    @Override
    protected synchronized Class<?> loadClass(final String name, final boolean resolve)
            throws ClassNotFoundException {

        final byte[] bytes = classes.get(name);
        if (bytes != null) {
            final Class<?> klass = defineClass(name, bytes, 0, bytes.length);
            if (resolve) {
                resolveClass(klass);
            }
            return klass;
        }
        return super.loadClass(name, resolve);
    }

    private Class<?> load(final String name) {
        try {
            return loadClass(name);
        } catch (final ClassNotFoundException ignore) {
            /* never happens */
            throw new RuntimeException(ignore);
        }
    }

}
