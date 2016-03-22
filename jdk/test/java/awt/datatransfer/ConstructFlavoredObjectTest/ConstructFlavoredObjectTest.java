/*
 * Copyright (c) 2015, Oracle and/or its affiliates. All rights reserved.
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
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.SystemFlavorMap;
import java.io.IOException;
import java.io.Reader;
import javax.swing.JLabel;
import javax.swing.TransferHandler;
/*
 * @test
 * @bug 8130329
 * @summary  Audit Core Reflection in module java.desktop AWT/Miscellaneous area
 *           for places that will require changes to work with modules
 * @author Alexander Scherbatiy
 */
public class ConstructFlavoredObjectTest {

    private static final String TEST_MIME_TYPE = "text/plain;class="
            + MyStringReader.class.getName();

    public static void main(String[] args) throws Exception {

        final DataFlavor dataFlavor = new DataFlavor(TEST_MIME_TYPE);
        SystemFlavorMap systemFlavorMap = (SystemFlavorMap) SystemFlavorMap.
                getDefaultFlavorMap();
        systemFlavorMap.addUnencodedNativeForFlavor(dataFlavor, "TEXT");
        systemFlavorMap.addFlavorForUnencodedNative("TEXT", dataFlavor);

        TransferHandler transferHandler = new TransferHandler("Test Handler");

        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        transferHandler.exportToClipboard(new JLabel("Test"), clipboard,
                TransferHandler.COPY);

        Object clipboardData = clipboard.getData(dataFlavor);

        if (!(clipboardData instanceof MyStringReader)) {
            throw new RuntimeException("Wrong clipboard data!");
        }
    }

    public static class MyStringReader extends Reader {

        public MyStringReader(Reader reader) {
        }

        @Override
        public int read(char[] cbuf, int off, int len) throws IOException {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public void close() throws IOException {
            throw new UnsupportedOperationException("Not supported yet.");
        }
    }
}
