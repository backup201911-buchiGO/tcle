package org.blogsite.youngsoft.piggybank.crypt;

import android.support.annotation.Nullable;

import org.blogsite.youngsoft.piggybank.utils.StringUtils;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.text.DecimalFormat;

/**
 * RSA 알고리즘을 위한 키를 BigInteger로 생성하기 위한 클래스
 * @author klee
 */
public class Key {
    public static final int KEY_SIZE_2048 = 2048;
    public static final int KEY_SIZE_1024 = 1024;
    private static final int pow = 16;
    public static final int MODULUS = 0;
    public static final int PUBKICIEY = 1;
    public static final int PRIVATEKEY = 2;

    /**
     * nbit 키 길이로 RSA 키를 생성한다.
     * @param nbit
     * @return
     */
    public static BigInteger[] generateKey(int nbit) {
        BigInteger[] ret = new BigInteger[3];
        
        SecureRandom random = new SecureRandom();
        BigInteger one = new BigInteger("1");
        BigInteger p = BigInteger.probablePrime(nbit / 2, random);
        BigInteger q = BigInteger.probablePrime(nbit / 2, random);
        BigInteger phi = (p.subtract(one)).multiply(q.subtract(one));
        
        DecimalFormat df = new DecimalFormat("###0");
        String s = df.format(Math.pow(2, pow) + 1);
//        System.out.println(s);
        ret[Key.MODULUS] = p.multiply(q);
        ret[Key.PUBKICIEY] = new BigInteger(s); //new BigInteger("4294967297"); // common value in practice = 2^16 + 1
        ret[Key.PRIVATEKEY] = ret[Key.PUBKICIEY].modInverse(phi);
        
        return ret;
    }

    /**
     * BigInteger를 문자열로 변환
     * @param big
     * @return
     */
    public static String convertBigInteger2String(BigInteger big){
        DecimalFormat df = new DecimalFormat("###0");
        return df.format(big);
    
    }

    /**
     * 문자열을 BigInteger로 변환
     * @param val
     * @return
     */
    @Nullable
    public static BigInteger convertString2BigInteger(String val){
        if(StringUtils.isIntegerNumber(val)) {
            return new BigInteger(val);
        }else{
            return null;
        }
    }
    
}
