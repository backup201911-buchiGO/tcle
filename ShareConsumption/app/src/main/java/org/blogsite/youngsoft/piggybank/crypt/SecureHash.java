
package org.blogsite.youngsoft.piggybank.crypt;

import org.blogsite.youngsoft.piggybank.logs.PGLog;
import org.blogsite.youngsoft.piggybank.logs.StackTrace;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SecureHash {
    public static final String SHA256 = "SHA-256";

    private static final String TAG = "SecureHash";

    private static final String ALGORITHM = "SHA-256";
    
    public static byte[] getHash(byte[] input) throws Exception {
        try {
            MessageDigest md = MessageDigest.getInstance(ALGORITHM);
            return md.digest(input);
        } catch (NoSuchAlgorithmException e) {
            // 일어날 경우가 없다고 보지만 만약을 위해 Exception 발생
            throw new Exception(e);
        }
    }
    
    public static byte[] getHash(String hashAlg, byte[] input) throws Exception {
        try {
            MessageDigest md = MessageDigest.getInstance(hashAlg);
            return md.digest(input);
        } catch (NoSuchAlgorithmException e) {
            // 일어날 경우가 없다고 보지만 만약을 위해 Exception 발생
            throw new Exception(e);
        }
    }

    public static String getHashHex(String hashAlg, String input) {
        return getHashHex(hashAlg, input.getBytes());
    }

    public static String getHashHex(String hashAlg, byte[] input) {
        String ret = "";
        try {
            MessageDigest md = MessageDigest.getInstance(hashAlg);
            ret = byteArrayToHexString(md.digest(input));
        } catch (NoSuchAlgorithmException e) {
            // 일어날 경우가 없다고 보지만 만약을 위해 Exception 발생
            e.printStackTrace();
            PGLog.e(TAG, StackTrace.getStackTrace(e));
        }
        return ret;
    }
    
    private static String byteArrayToHexString(byte abyte0[]) {
        boolean flag = false;
        String as[] = {"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f"};
        StringBuffer stringbuffer = new StringBuffer(abyte0.length * 2);
        for (int i = 0; i < abyte0.length; i++) {
            byte byte0 = (byte) (abyte0[i] & 0xf0);
            byte0 >>>= 4;
            byte0 &= 0xf;
            stringbuffer.append(as[byte0]);
            byte0 = (byte) (abyte0[i] & 0xf);
            stringbuffer.append(as[byte0]);
        }

        String s = new String(stringbuffer);
        return s;
    }
}
