
package org.blogsite.youngsoft.piggybank.crypt;

public class SecureKey {
    private SecureKey() {
    }

    public static String EmbedKey(String Target, String src) {
        int tL, sL, m, i;
        String sret = "";

        tL = Target.length();
        sL = src.length();
        m = tL / sL;
        for (i = 1; i <= sL; i++) {
            sret = sret + Target.substring((i - 1) * m, i * m) + src.charAt(i - 1);
        }
        sret = sret + Target.substring(m * sL);
        return sret;
    }

    public static String[] ExtractKey(String Target, int Len) {
        String ret[] = new String[2];
        int i, m;

        m = Target.length() / Len;
        String Key = Target.substring(m - 1, m);
        String src = Target.substring(0, m - 1);
        
        for (i = 2; i <= Len; i++) {
            Key = Key + Target.substring(i * m - 1, i * m);
            src = src + Target.substring((i - 1) * m, i * m - 1);
        }
        src = src + Target.substring(m * Len);
        ret[0] = src;
        ret[1] = Key;
        return ret;
    }
}

