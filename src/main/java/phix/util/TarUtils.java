/*
Copyright (c) 2017 Faculty of Mathematics and Informatics - Sofia University

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/
package phix.util;

import org.kamranzafar.jtar.TarConstants;
import org.kamranzafar.jtar.TarEntry;
import org.kamranzafar.jtar.TarHeader;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;

public final class TarUtils {

    private TarUtils() {
    }

    public static InputStream wrapString(String content, String fileName, long modificationTime, int fileMode)
    {
        byte[] contentBytes = content.getBytes(StandardCharsets.UTF_8);

        return wrapInputStream(new ByteArrayInputStream(contentBytes),
                fileName, contentBytes.length, modificationTime, fileMode);
    }

    public static InputStream wrapInputStream(InputStream inputStream, String fileName,
                                              long size, long modificationTime, int fileMode)
    {
        TarHeader header = TarHeader.createHeader(fileName, size, modificationTime, false, fileMode);
        TarEntry entry = new TarEntry(header);
        byte[] headerBytes = new byte[TarConstants.HEADER_BLOCK];
        entry.writeEntryHeader(headerBytes);
        InputStream headerInputStream = new ByteArrayInputStream(headerBytes);

        byte[] eofBytes = getPaddingAndEOF(size);
        InputStream eofInputStream = new ByteArrayInputStream(eofBytes);

        return new SequenceInputStream(Collections.enumeration(
                Arrays.asList(headerInputStream, inputStream, eofInputStream)
        ));
    }

    private static byte[] getPaddingAndEOF(long entrySize) {
        int paddingLen = TarConstants.DATA_BLOCK - ((int)(entrySize % TarConstants.DATA_BLOCK));

        return new byte[paddingLen + TarConstants.EOF_BLOCK];
    }

}
