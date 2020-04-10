/*
 * Copyright (c) 2020, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

package nsk.share;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;



public final class MainWrapper {

    public MainWrapper() {
    }

    public static void main(String[] args) throws Exception {
        String wrapperName = args[0];
        String className = args[1];
        String[] classArgs = new String[args.length - 2];
        System.arraycopy(args, 2, classArgs, 0, args.length - 2);
        Class c = Class.forName(className);
        Method mainMethod = c.getMethod("main", new Class[] { String[].class });

        // It is needed to register finalizer thread in default thread group
        // So FinalizerThread thread can't be in virtual threads group
        Finalizer finalizer = new Finalizer(new FinalizableObject());
        finalizer.activate();

        Thread.Builder tb = Thread.builder().task(() -> {
                try {
                    mainMethod.invoke(null, new Object[] { classArgs });
                } catch (IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            });
        if (wrapperName.equals("Virtual")) {
            tb = tb.virtual();
        }
        Thread t = tb.name("main").build();
        Thread.currentThread().setName("old-m-a-i-n");
        t.start();
        t.join();
    }
}
