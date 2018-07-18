package org.blogsite.youngsoft.piggybank.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 *
 * @author Administrator
 */
public class Compress {

    private Compress() {
    }

    /**
     *
     * @param str
     * @return
     * @throws Exception
     */
    public static byte[] Zip(String str) throws Exception {
        byte[] ret;

        try {
            byte[] src = str.getBytes();
            ret = compress(src);
            //System.out.println("압축률: " + String.valueOf(100 * (src.length - ret.length) / src.length) + " %");
        } catch (Exception e) {
            throw new Exception(e);
        }
        return ret;
    }

    public static byte[] Zip(byte[] src) throws Exception {
        byte[] ret;

        try {
            ret = compress(src);
            //System.out.println("압축률: " + String.valueOf(100 * (src.length - ret.length) / src.length) + " %");
        } catch (Exception e) {
            throw new Exception(e);
        }
        return ret;
    }

    /**
     *
     * @param str
     * @return
     * @throws Exception
     */
    public static String ZipBase64(String str) throws Exception {
        return ZipBase64(str, false);
    }
    
    public static String ZipBase64(String str, boolean wrap) throws Exception {
        String ret = "";

        try {
            byte[] src = str.getBytes();
            byte[] compressedArray = compress(src);
            ret = new String(Base64Utils.encode(compressedArray, wrap));
            //System.out.println("압축률: " + String.valueOf(100 * (src.length - ret.length()) / src.length) + " %");
        } catch (Exception e) {
            throw new Exception(e);
        }
        return ret;
    }

    public static String ZipBase64(byte[] src) throws Exception {
        return ZipBase64(src, false);
    }
    
    public static String ZipBase64(byte[] src, boolean wrap) throws Exception {
        String ret = "";

        try {
            byte[] compressedArray = compress(src);
            ret = new String(Base64Utils.encode(compressedArray, wrap));
            //System.out.println("압축률: " + String.valueOf(100 * (src.length - ret.length()) / src.length) + " %");
        } catch (Exception e) {
            throw new Exception(e);
        }
        return ret;
    }

    /**
     *
     * @param src
     * @return
     * @throws Exception
     */
    public static String Unzip(byte[] src) throws Exception {
        String ret = "";

        try {
            ret = new String(uncompress(src));
        } catch (Exception e) {
            throw new Exception(e);
        }

        return ret;
    }

    /**
    *
    * @param src
    * @return
    * @throws Exception
    */
   public static byte[] UnzipBytes(byte[] src) throws Exception {
	   byte[] ret = null;
       try {
           ret = uncompress(src);
       } catch (Exception e) {
           throw new Exception(e);
       }

       return ret;
   }
    
    /**
     *
     * @param src
     * @return
     * @throws Exception
     */
    public static String UnzipBase64(String src) throws Exception {
        String ret = "";

        try {
            ret = new String(UnzipBase64ToBytes(src));
        } catch (Exception e) {
            throw new Exception(e);
        }

        return ret;
    }
    
    public static byte[] UnzipBase64(byte[] src) throws Exception {
        byte[] ret = null;

        try {
            byte[] output = Base64Utils.decode(src);
            ret = uncompress(output);
        } catch (Exception e) {
            throw new Exception(e);
        }

        return ret;
    }

    public static byte[] UnzipBase64ToBytes(String src) throws Exception {
        byte[] ret = null;

        try {
            byte[] output = Base64Utils.decode(src.getBytes());
            ret = uncompress(output);
        } catch (Exception e) {
            throw new Exception(e);
        }

        return ret;
    }

    private static byte[] compress(byte[] src) throws IOException {
        int size = 1024;
        BufferedInputStream bis = new BufferedInputStream(new ByteArrayInputStream(src));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        GZIPOutputStream gzip = new GZIPOutputStream(baos);
        byte[] buffer = new byte[size];
        int len;
        while ((len = bis.read(buffer, 0, size)) != -1) {
            gzip.write(buffer, 0, len);
        }
        gzip.finish();
        bis.close();
        gzip.close();
        return baos.toByteArray();
    }

    public static byte[] uncompress(byte[] data) throws IOException {
        int size = 1024;
        GZIPInputStream gzip = new GZIPInputStream(new ByteArrayInputStream(data));
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[size];
        int len;
        while ((len = gzip.read(buffer, 0, size)) != -1) {
            baos.write(buffer, 0, len);
        }
        gzip.close();
        baos.close();
        return baos.toByteArray();
    }
}
