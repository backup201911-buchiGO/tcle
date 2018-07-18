package org.blogsite.youngsoft.piggybank.utils;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.MalformedURLException;
import java.net.URL;

public final class ObjLoader {
    /**
     *
     */
    //private static boolean wasWarning;
    /**
     *
     */
    public static Object loadObject(String fileName) throws Exception {
        return loadObject(new File(fileName));
    }

    /**
     *
     */
    public static Object loadObject(File file) throws Exception {
        if (!file.exists() || !file.isFile()) {
            throw new Exception(new FileNotFoundException(String.valueOf(file)));
        }

        Object obj = null;

        FileInputStream fis = null;
        ObjectInputStream ois = null;

        try {
            fis = new FileInputStream(file);
            BufferedInputStream bufferedIn = new BufferedInputStream(fis);
            ois = new ObjectInputStream(bufferedIn);
            obj = ois.readObject();
        } catch (IOException e) {
            throw new Exception("다음 파일로부터 객체 로딩 오류 : " + file, e);
        } catch (ClassNotFoundException e) {
            throw new Exception("다음 파일로부터 객체를 로딩하는 과정에서 클래스를 찾지 못함 : " + file, e);
        } finally {
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                }
            }

            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                }
            }
        }

        return obj;
    }

    /**
     *
     */
    public static Object loadObject(URL url) throws Exception {
        Object obj = null;

        InputStream is = null;
        ObjectInputStream ois = null;

        try {
            is = url.openStream();
            ois = new ObjectInputStream(is);
            obj = ois.readObject();
        } catch (IOException e) {
            throw new Exception("다음 URL로부터 객체 로딩 오류 : " + url, e);
        } catch (ClassNotFoundException e) {
            throw new Exception("다음 URL로부터 객체 로딩 중 클래스를 찾지 못함 : " + url, e);
        } finally {
            if (ois != null) {
                try {
                    ois.close();
                } catch (IOException e) {
                }
            }

            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        }

        return obj;
    }

    /**
     *
     */
    @SuppressWarnings("resource")
    public static Object loadObject(InputStream is) throws Exception {
        Object obj = null;

        ObjectInputStream ois = null;

        try {
            ois = new ObjectInputStream(is);
            obj = ois.readObject();
        } catch (IOException e) {
            throw new Exception("InputStream으로부터 객체 로딩 오류", e);
        } catch (ClassNotFoundException e) {
            throw new Exception("InputStream으로부터 객체 로딩중 클래스를 찾지 못함", e);
        }

        return obj;
    }

    /**
     *
     */
    @SuppressWarnings("resource")
    public static Object loadObject(byte[] data) throws Exception {
        Object obj = null;
        ObjectInputStream ois = null;

        try {
            ByteArrayInputStream is = new ByteArrayInputStream(data);
            ois = new ObjectInputStream(is);
            obj = ois.readObject();
        } catch (IOException e) {
            throw new Exception("InputStream으로부터 객체 로딩 오류", e);
        } catch (ClassNotFoundException e) {
            throw new Exception("InputStream으로부터 객체 로딩중 클래스를 찾지 못함", e);
        }

        return obj;
    }

    @SuppressWarnings("resource")
    public static Object loadObjectFromBase64(String s) throws Exception {
        Object obj = null;
        ObjectInputStream ois = null;

        try {
            //ByteArrayInputStream is = new ByteArrayInputStream(Base64Utils.decode(s.getBytes()));
            ByteArrayInputStream is = new ByteArrayInputStream(Compress.UnzipBase64ToBytes(s));
            ois = new ObjectInputStream(is);
            obj = ois.readObject();
        } catch (IOException e) {
            throw new Exception("InputStream으로부터 객체 로딩 오류", e);
        } catch (ClassNotFoundException e) {
            throw new Exception("InputStream으로부터 객체 로딩중 클래스를 찾지 못함", e);
        }

        return obj;
    }

    /**
     *
     */
    public static byte[] loadBytes(File file) throws Exception {
        ByteArrayOutputStream baos = null;
        FileInputStream fis = null;

        try {
            fis = new FileInputStream(file);
            baos = new ByteArrayOutputStream();

            byte[] bytes = new byte[10000];
            int ln = 0;
            while ((ln = fis.read(bytes)) > 0) {
                baos.write(bytes, 0, ln);
            }

            baos.flush();
        } catch (IOException e) {
            throw new Exception("다음 파일로부터 바이트 데이터 로딩 오류 : " + file, e);
        } finally {
            if (baos != null) {
                try {
                    baos.close();
                } catch (IOException e) {
                }
            }

            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                }
            }
        }

        return baos.toByteArray();
    }

    /**
     *
     */
    public static byte[] loadBytes(URL url) throws Exception {
        ByteArrayOutputStream baos = null;
        InputStream is = null;

        try {
            is = url.openStream();
            baos = new ByteArrayOutputStream();

            byte[] bytes = new byte[10000];
            int ln = 0;
            while ((ln = is.read(bytes)) > 0) {
                baos.write(bytes, 0, ln);
            }

            baos.flush();
        } catch (IOException e) {
            throw new Exception("다음 URL로부터 바이트 데이터 로딩 오류 : " + url, e);
        } finally {
            if (baos != null) {
                try {
                    baos.close();
                } catch (IOException e) {
                }
            }

            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        }

        return baos.toByteArray();
    }

    /**
     *
     */
    public static byte[] loadBytes(InputStream is) throws Exception {
        ByteArrayOutputStream baos = null;

        try {
            baos = new ByteArrayOutputStream();

            byte[] bytes = new byte[10000];
            int ln = 0;
            while ((ln = is.read(bytes)) > 0) {
                baos.write(bytes, 0, ln);
            }

            baos.flush();
        } catch (IOException e) {
            throw new Exception("입력 스트림으로부터 바이트 데이터 로딩 오류.", e);
        } finally {
            if (baos != null) {
                try {
                    baos.close();
                } catch (IOException e) {
                }
            }
        }

        return baos.toByteArray();
    }

    /**
     * 읽어들일 파일 열기를 시도한다.
     *
     * @param filename 파일 이름
     * @return 파일에 대한 입력스트림 또는 파일을 찾을 수 없는 경우 <code>null</code>
     * @throws Exception
     */
    public static InputStream getFileInputStream(String filename) throws Exception {
        InputStream is = null;

        File file = new File(filename);
        if (file.exists() && file.isFile()) {
            try {
                is = new FileInputStream(file);
            } catch (FileNotFoundException e) {
                throw new Exception("다음 파일 열기 오류 : " + filename, e);
            }
        }

        return is;
    }

    /**
     * 지정된 리소스 이름에 대한 리소스 URL을 리턴한다.
     *
     * @param resource 리소스 이름
     * @return 지정된 이름을 갖는 리소스의 URL이나 찾지 못하면 <code>null</code>
     * @see ClassLoader#getResource(String)
     */
    public static URL getResource(String resource) {
        URL location = null;

        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader != null) {
            location = classLoader.getResource(resource);
        }

        if (location == null) {
            classLoader = ObjLoader.class.getClassLoader();
            if (classLoader != null) {
                location = classLoader.getResource(resource);
            }
        }

        return location;
    }

    /**
     * URL에 대한 입력스트림 열기를 시도한다.
     *
     * @param spec URL로 파싱할 문자열
     * @return URL에 대한 입력스트림 또는 <code>spec</code>이 올바르지 않은 URL이라면 <code>null</code>
     * @throws Exception
     */
    public static InputStream getURLInputStream(String spec) throws Exception {
        InputStream is = null;

        try {
            URL url = new URL(spec);
            is = url.openStream();
        } catch (MalformedURLException e) {
        } catch (IOException e) {
            throw new Exception("다음 URL 열기 오류 : " + spec, e);
        }

        return is;
    }

    private ObjLoader() {
    }
}
