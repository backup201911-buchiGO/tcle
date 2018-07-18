package org.blogsite.youngsoft.piggybank.crypt;

import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

import org.blogsite.youngsoft.piggybank.setting.PBSettings;
import org.blogsite.youngsoft.piggybank.utils.Base64Utils;
import org.blogsite.youngsoft.piggybank.utils.ObjLoader;
import org.blogsite.youngsoft.piggybank.utils.ObjSaver;

/**
 * Created by klee on 2018-01-03.
 */

public class RSAUtils {
    public static final String RSA_ECB_PKCS1_PADDING = "RSA/ECB/PKCS1Padding";

    public static final int KEY_SIZE_2048 = 2048;
    public static final int KEY_SIZE_1024 = 1024;

    private RSAUtils() {
    }

    private static final String ALGORITHM = "RSA";

    public static void main(String[] args) throws Exception {

        //BigInteger[] keys = Key.generateKey(KEY_SIZE_2048);


        PublicKey publickey = RSAUtils.getPublicKey("MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEApgM2jC8Jov8xLb7dlVhd6xGD/prGb093RVUdBKsOcL1a0jq01j5J9/u3R3kuHvejXnF8OHMX4pIMjRb0L1sBKvooE/hjNUuCveoxoIQxxhN/V/qJ6VoTeXU4QInM+TZw2KmBTRPzrIZXPBZlVVs3K7JGxklVv0rstKv8kTaQvo3SJEM2avarupjGe8mvogsojyzygWBfSgSUdHH/816N1s6+JNaRaied5nWt2nCC/VUtk7LhnUQhgFr4+T/Dg3NeLmE1Il9sQI6Z6iMMzO/8SSBws8sO+SzAIwfP3yCZvSaNB3OlddkaQzIXyNK2sVewZcGYMgamnb8IFbEkBbH1nwIDAQAB");
        PrivateKey privatekey = RSAUtils.getPrivateKey("MIICOQIBADANBgkqhkiG9w0BAQEFAASCAiMwggIfAgEAAoIBAQCmAzaMLwmi/zEtvt2VWF3rEYP+msZvT3dFVR0Eqw5wvVrSOrTWPkn3+7dHeS4e96NecXw4cxfikgyNFvQvWwEq+igT+GM1S4K96jGghDHGE39X+onpWhN5dThAicz5NnDYqYFNE/Oshlc8FmVVWzcrskbGSVW/Suy0q/yRNpC+jdIkQzZq9qu6mMZ7ya+iCyiPLPKBYF9KBJR0cf/zXo3Wzr4k1pFqJ53mda3acIL9VS2TsuGdRCGAWvj5P8ODc14uYTUiX2xAjpnqIwzM7/xJIHCzyw75LMAjB8/fIJm9Jo0Hc6V12RpDMhfI0raxV7BlwZgyBqadvwgVsSQFsfWfAgEAAoIBAQCl7nY6HdXS/qFIKQtPd/i/uU0mUctffJ0pRo5vmQ4g75L0qEsSDUZfwFAL93p652h6QJI51ggcpYA4EaZWIu8C5vDiLRX0Mhpm+stJtngNlHVnGXt5K1hlwR1NnK6stZaLaLldskCPw+Mzour8KuT1HwcQsEItppT7bSBQgKqiWnkII2gpJV3bG2CXBtuVQHUYt0NPOLVnu/Iakwodh7E3lLbxCCQ3wb/u9z/HeAG/ME7bQQgh2OcTaTg+Gu8UEtC/wgDDYDlWZUIRNnwT1IsBMrPYoARzjS3loOFBAxVno0SEX+fow1gUwuMEz2mPGq4giqxc84vj4U+dGP3Q9lLpAgEAAgEAAgEAAgEAAgEA");

        //PublicKey publickey = RSAUtils.getPublicKey(keys[Key.MODULUS], keys[Key.PUBKICIEY]);
        //PrivateKey privatekey = RSAUtils.getPrivateKey(keys[Key.MODULUS], keys[Key.PRIVATEKEY]);

        System.out.println("publickey = " + RSAUtils.getBase64PublicKey(publickey));
        System.out.println("privatekey = " + RSAUtils.getBase64PrivateKey(privatekey));

        PBSettings set = new PBSettings();
        set.setUserName("KLEE");
        set.setUserEmail("test@gmail.com");
        set.setThreshold(100000);

        String sss = ObjSaver.ObjectToBase64(set);
        System.out.println("PBSettings = " + sss);

        PBSettings s1 = (PBSettings)ObjLoader.loadObjectFromBase64(sss);

        //KeyPair keypair = RSAUtils.generateKeyPair();
        //PublicKey publickey = keypair.getPublic();
        //PrivateKey privatekey = keypair.getPrivate();

        String a = "이통사, 보편요금제 막으며 '고가 마케팅'\n" +
                "저가요금제 차별하며 고가로 유도\n" +
                "정부 '2만원대 적정요금제' 추진엔\n" +
                "이통사들 \"과도한 시장개입\" 반발";

        String ae = "jKPzRlKKzfZs6vH70NH4yHng0W8LEwhPprNUx8u1iZ6x3obANTRqwPAU32foOKJ1JZcldJ3VzQLdzFd7ZoygZljxrb+NhhnkSoaCW5MBh7bkMxDF6OpC8qb8zQbv49ChR2YIR1Si3FWBn07WRWFK/64Gi+2N5zudLGhr8T5JrX9EQQEKBuHobZiOW5ETy7YAU5KBfFBlNx+6/+MV3p5UeNxdEuEsIlxoIUWxenFEiNJi8Hlk1NBBCjWoQ/P5G4fCPQ0nvDuMkAIvFwBXrU7ZjrMGFzPbyHfTtbaRAz1aOk4cgywhaOQOkSSExQ1uvsQ2qDqKOWmXLxmTpY1+8R3lxA==";
        //String a1 = RSAUtils.encryptAsString(a, publickey);
        String a2 = RSAUtils.decrypt(ae, privatekey);
//        System.out.println("a1 = " + a1);
        System.out.println(a2);
    }

    public static KeyPair generateKeyPair() {
        return generateKeyPair(KEY_SIZE_2048);
    }

    public static KeyPair generateKeyPair(int keySize) {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(ALGORITHM);
            keyPairGenerator.initialize(keySize);
            return keyPairGenerator.generateKeyPair();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException("Failed to generate key pair!", e);
        }
    }

    public static PublicKey getPublicKey(String base64PublicKey) {
        try {
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64Utils.decode(base64PublicKey.getBytes()));

            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            PublicKey publicKey = keyFactory.generatePublic(keySpec);
            return publicKey;
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to get public key!", e);
        }
    }

    public static PublicKey getPublicKey(BigInteger modulus, BigInteger exponent) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            RSAPublicKeySpec keySpec = new RSAPublicKeySpec(modulus, exponent);
            return keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to get public key!", e);
        }
    }

    public static String getBase64PublicKey(PublicKey publicKey) throws Exception {
        return new String(Base64Utils.encode(publicKey.getEncoded()));
    }

    public static PrivateKey getPrivateKey(String base64PrivateKey) {
        try {
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64Utils.decode(base64PrivateKey.getBytes()));
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            PrivateKey privateKey = keyFactory.generatePrivate(keySpec);
            return privateKey;
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to get private key!", e);
        }
    }

    public static PrivateKey getPrivateKey(BigInteger modulus, BigInteger exponent) {
        try {
            KeyFactory keyFactory = KeyFactory.getInstance(ALGORITHM);
            RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(modulus, exponent);
            return keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to get private key!", e);
        }
    }

    public static String getBase64PrivateKey(PrivateKey privateKey) throws Exception {
        return new String(Base64Utils.encode(privateKey.getEncoded()));
    }

    public static byte[] encryptAsByteArray(String data, PublicKey publicKey) {
        throwNullPointException(data);
        throwNullPointException(publicKey);
        try {
            Cipher cipher = Cipher.getInstance(RSA_ECB_PKCS1_PADDING);
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            return cipher.doFinal(data.getBytes());
        } catch (Exception e) {
            throw new IllegalArgumentException("Encrypt failed!", e);
        }
    }

    public static byte[] encryptAsByteArray(String data, String base64PublicKey) {
        return encryptAsByteArray(data, getPublicKey(base64PublicKey));
    }

    public static String encryptAsString(String data, PublicKey publicKey) throws Exception {
        return new String(Base64Utils.encode(encryptAsByteArray(data, publicKey)));
    }

    public static String encryptAsString(String data, String base64PublicKey) throws Exception {
        return new String(Base64Utils.encode(encryptAsByteArray(data, getPublicKey(base64PublicKey))));
    }

    public static String decrypt(byte[] data, PrivateKey privateKey) {
        throwNullPointException(data);
        throwNullPointException(privateKey);
        try {
            Cipher cipher = Cipher.getInstance(RSA_ECB_PKCS1_PADDING);
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            return new String(cipher.doFinal(data));
        } catch (Exception e) {
            throw new IllegalArgumentException("Decrypt failed!", e);
        }
    }

    public static String decrypt(byte[] data, String base64PrivateKey) {
        return decrypt(data, getPrivateKey(base64PrivateKey));
    }

    public static String decrypt(String data, PrivateKey privateKey) throws Exception {
        return decrypt(Base64Utils.decode(data.getBytes()), privateKey);
    }

    public static String decrypt(String data, String base64PrivateKey) throws Exception {
        return decrypt(Base64Utils.decode(data.getBytes()), getPrivateKey(base64PrivateKey));
    }

    private static void throwNullPointException(Object obj) {
        if (null == obj) {
            throw new NullPointerException();
        }
    }
}
