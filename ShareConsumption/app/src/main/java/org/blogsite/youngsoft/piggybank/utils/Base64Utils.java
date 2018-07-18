package org.blogsite.youngsoft.piggybank.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;


public class Base64Utils {

    public Base64Utils() {
    }

    public static void reset() {
        lineLength = 64;
        lineCut = false;
    }

    public static void setLineLength(int length) {
        lineLength = length;
    }

    public static void setLineCut(boolean _lineCut) {
        lineCut = _lineCut;
    }

    public static byte[] encode(byte in[], boolean wrap, int lineLength) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        encode(((InputStream) (new ByteArrayInputStream(in))), ((OutputStream) (baos)), wrap, lineLength);
        baos.close();
        return baos.toByteArray();
    }

    public static byte[] encode(byte in[]) throws IOException {
        return encode(in, lineCut, lineLength);
    }

    public static byte[] encode(byte in[], boolean wrap) throws IOException {
        return encode(in, wrap, lineLength);
    }

    public static void encode(InputStream is, OutputStream os, boolean wrap, int ll) throws IOException {
        byte inBuffer[] = new byte[3];
        byte outBuffer[] = new byte[4];
        int lineCount = 0;
        while (is.available() != 0) {
            int read = is.read(inBuffer);
            encodeBlock(inBuffer, read, outBuffer);
            os.write(outBuffer);
            lineCount += 4;
            if (wrap && ll <= lineCount) {
                os.write(10);
                lineCount = 0;
            }
        }
        if (wrap && lineCount != 0) {
            os.write(10);
        }
    }

    public static void encode(InputStream is, OutputStream os) throws IOException {
        encode(is, os, lineCut, lineLength);
    }

    public static void encode(InputStream is, OutputStream os, boolean wrap) throws IOException {
        encode(is, os, wrap, lineLength);
    }

    public static byte[] decode(byte in[]) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        decode(((InputStream) (new ByteArrayInputStream(in))), ((OutputStream) (baos)));
        baos.close();
        return baos.toByteArray();
    }

    public static void decode(InputStream is, OutputStream os) throws IOException {
        try {
            byte inBuffer[] = new byte[4];
            byte outBuffer[] = new byte[3];
            while (is.available() != 0) {
                int read = 0;
                while (is.available() != 0) {
                    byte current = (byte) is.read();
                    if (!isDecodeDomain(current)) {
                        continue;
                    }
                    inBuffer[read] = current;
                    if (++read == 4) {
                        break;
                    }
                }
                if (read != 4 && read != 0) {
                    throw new IOException("Base64Utils decode fail, last: " + read);
                }
                if (read != 0) {
                    int wrote = decodeBlock(inBuffer, outBuffer);
                    os.write(outBuffer, 0, wrote);
                }
            }
        } catch (ArrayIndexOutOfBoundsException ex) {
            throw new IOException("Base64Utils decode fail : illegal characters in data");
        }
    }

    private static boolean isDecodeDomain(byte current) {
        return B2I[current] >= 0;
    }

    @SuppressWarnings("unused")
    public static final int decodeBlock(byte in[], byte out[]) {
        int ret = 3;
        int idx[] = new int[4];
        boolean end = false;
        for (int i = 0; i < 4; i++) {
            byte current = in[i];
            if (current == 61) {
                ret = i != 2 ? 2 : 1;
                break;
            }
            idx[i] = B2I[current];
        }

        out[0] = (byte) ((idx[0] & 0xff) << 2 | (idx[1] & 0xff) >> 4);
        out[1] = (byte) ((idx[1] & 0xff) << 4 | (idx[2] & 0xff) >> 2);
        out[2] = (byte) ((idx[2] & 0xff) << 6 | idx[3] & 0xff);
        return ret;
    }

    public static final void encodeBlock(byte in[], int len, byte out[]) {
        if (out == null) {
            out = new byte[4];
        }
        for (int i = 3; len < i;) {
            in[--i] = 0;
        }

        out[0] = (byte) ((in[0] & 0xfc) >>> 2);
        out[1] = (byte) ((in[0] & 3) << 4 | (in[1] & 0xf0) >>> 4);
        out[2] = (byte) ((in[1] & 0xf) << 2 | (in[2] & 0xc0) >>> 6);
        out[3] = (byte) (in[2] & 0x3f);
        for (int i = 0; i < 4; i++) {
            out[i] = I2B[out[i]];
        }

        if (len < 3) {
            out[3] = 61;
        }
        if (len < 2) {
            out[2] = 61;
        }
    }

    private static int lineLength;
    private static boolean lineCut;
    public static final int RFC_MAX_LINE_LENGTH = 76;
    public static final int USUAL_LINE_LENGTH = 64;
    private static byte B2I[] = { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1,
            -1, -1, 62, -1, -1, -1, 63, 52, 53, 54, 55, 56, 57, 58, 59, 60, 61, -1, -1, -1, 0, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24,
            25, -1, -1, -1, -1, -1, -1, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 40, 41, 42, 43, 44, 45, 46, 47, 48, 49, 50, 51, -1, -1, -1, -1, -1 };
    private static byte I2B[] = { 65, 66, 67, 68, 69, 70, 71, 72, 73, 74, 75, 76, 77, 78, 79, 80, 81, 82, 83, 84, 85, 86, 87, 88, 89, 90, 97, 98, 99, 100, 101, 102, 103, 104, 105, 106, 107, 108, 109,
            110, 111, 112, 113, 114, 115, 116, 117, 118, 119, 120, 121, 122, 48, 49, 50, 51, 52, 53, 54, 55, 56, 57, 43, 47 };

    static {
        reset();
    }
}

