package org.blogsite.youngsoft.piggybank.utils;

import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;

/**
 * 이 유틸리티 클래스는 객체를 직렬화시켜 디스크상에 저장하거나
 * 출력스트림을 통해 네트워크로 전송하기 위해 사용한다.
 */
@SuppressWarnings("resource")
public final class ObjSaver implements Serializable {
    private static final long serialVersionUID = 8710194862135552211L;

    /**
     *
     */
    public static void saveObject(Object obj, String fileName) throws Exception {
        saveObject(obj, new File(fileName));
    }

    /**
     *
     */
    public static void saveObject(Object obj, File file) throws Exception {
        FileOutputStream fos = null;
        ObjectOutputStream oos = null;

        try {
            fos = new FileOutputStream(file);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.flush();
            bos.flush();
            fos.flush();
        } catch (IOException e) {
            throw new Exception("파일 저장 오류 : " + file, e);
        } finally {
            if (oos != null) {
                try {
                    oos.close();
                } catch (IOException e) {
                }
            }

            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     *
     */
    public static void saveObject(Object obj, OutputStream os) throws Exception {
        ObjectOutputStream oos = null;

        try {
            oos = new ObjectOutputStream(os);
            oos.writeObject(obj);
            oos.flush();
        } catch (IOException e) {
            throw new Exception("OutputStream에 객체 저장 오류", e);
        }
    }

    public static void saveObject(Object obj, ByteArrayOutputStream os) throws Exception {
        ObjectOutputStream oos = null;

        try {
            oos = new ObjectOutputStream(os);
            oos.writeObject(obj);
            oos.flush();
        } catch (IOException e) {
            throw new Exception("OutputStream에 객체 저장 오류", e);
        }
    }

    public static byte[] ObjectToByte(Object obj) throws Exception {
        ObjectOutputStream oos = null;
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(os);
            oos.writeObject(obj);
            oos.flush();
            return os.toByteArray();
        } catch (IOException e) {
            throw new Exception("OutputStream에 객체 저장 오류", e);
        }
    }

    public static String ObjectToBase64(Object obj) throws Exception {
        ObjectOutputStream oos = null;
        try {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(os);
            oos.writeObject(obj);
            oos.flush();
            //return new String(Base64Utils.encode(os.toByteArray()));
            return Compress.ZipBase64(os.toByteArray(), false);
        } catch (IOException e) {
            throw new Exception("OutputStream에 객체 저장 오류", e);
        }
    }

    /**
     *
     */
    public static void saveClassSource(String source, File file) throws Exception {
        FileWriter fwriter = null;

        try {
            fwriter = new FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(fwriter);
            bufferedWriter.write(source);
            bufferedWriter.flush();
            fwriter.flush();
        } catch (IOException e) {
            throw new Exception("표현식 클래스 파일 저장 오류 : " + file, e);
        } finally {
            if (fwriter != null) {
                try {
                    fwriter.close();
                } catch (IOException e) {
                }
            }
        }
    }

    private ObjSaver() {
    }
}
