package org.blogsite.youngsoft.piggybank.utils;

import java.text.DecimalFormat;

/**
 * Created by 이강구 on 2017-12-12.
 */
public class StringUtils {

    public static String replaceAll(String str, String pattern, String replace) {
        int s = 0;
        int e = 0;

        StringBuffer result = new StringBuffer();

        while ((e = str.indexOf(pattern, s)) >= 0) {
            result.append(str.substring(s, e));
            result.append(replace);
            s = e + pattern.length();
        }
        result.append(str.substring(s));
        return result.toString();
    }

    /**
     * <p>
     * 문자열을 구분자로 나누어서, 문자열 배열로 만든다.
     * </p>
     * <p>
     * 배열의 문자열 중에 <code>null</code>과 공백("")도 포함한다.
     * </p>
     *
     * <pre>
     * StringUtils.split("h-a-n", '-') = ["h", "a", "n"]
     * StringUtils.split("h--n", '-')  = ["h", "", "n"]
     * StringUtils.split(null, *)      = null
     * </pre>
     *
     * @param str 문자열
     * @param separator 구분자
     * @return 구분자로 나누어진 문자열 배열
     */
    public static String[] split(String str, char separator) {
        return split(str, new String(new char[]{separator}));
    }

    /**
     * <p>
     * 문자열을 구분자로 나누어서, 문자열 배열로 만든다.
     * </p>
     * <p>
     * 배열의 문자열 중에 <code>null</code>과 공백("")도 포함한다.
     * </p>
     *
     * <pre>
     * StringUtils.split("h-a-n", "-") = ["h", "a", "n"]
     * StringUtils.split("h--n", "-")  = ["h", "", "n"]
     * StringUtils.split(null, *)      = null
     * </pre>
     *
     * @param str 문자열
     * @param separator 구분자
     * @return 구분자로 나누어진 문자열 배열
     */
    public static String[] split(String str, String separator) {
        if (str == null) {
            return null;
        }
        StrTokenizer tokenizer = new StrTokenizer(str, separator);
        return tokenizer.toArray();
    }

    public static boolean isIntegerNumber(String num) {
        if (num == null || "".equals(num)) {
            return false;
        }
        num = num.toLowerCase();
        String RefString = "0123456789-";

        for (int i = 0, maxi = num.length(); i < maxi; i++) {
            String ch = num.substring(i, i + 1);
            if (RefString.indexOf(ch, 0) == -1) {
                return false;
            }
        }

        return true;
    }

    public static boolean isFloatNumber(String num) {
        if (num == null || "".equals(num)) {
            return false;
        }
        num = num.toLowerCase();
        String RefString = "0123456789.-";

        for (int i = 0, maxi = num.length(); i < maxi; i++) {
            String ch = num.substring(i, i + 1);
            if (RefString.indexOf(ch, 0) == -1) {
                return false;
            }
        }

        return true;
    }

    public static boolean search(String src, String search) {
        boolean ret = false;

        for (int i = 0; i < search.length(); i++) {
            char c = search.charAt(i);
            ret = src.indexOf(c) > -1;
            if (ret) {
                break;
            }
        }

        return ret;
    }

    public static String convertCardValue(String amount) {
        String tamount = replaceAll(amount, ",", "");
        tamount = replaceAll(tamount, "원", "");
        tamount = replaceAll(tamount, "(USD)", "");
        tamount = replaceAll(tamount, "(US$)", "");
        tamount = replaceAll(tamount, "(금액)", "");
        if (isIntegerNumber(tamount)) {
            return tamount;
        } else {
            return "";
        }
    }

    public static boolean cardAccepted(String amount) {
        String amt = replaceAll(amount, "(금액)", "");
        if (!"".equals(convertCardValue(amt)) && !search(amt, "(USD)") && !search(amt, "(US$)")) {
            return true;
        } else {
            return false;
        }
    }

    public static String removeCRLF(String s) {
        return replaceAll(s, "\n", "");
    }

    public static boolean isNull(String str) {
        if (str == null || "".equals(str)) {
            return true;
        } else {
            return false;
        }
    }

    public static String format(String str, String pattern) {
        if (isNull(pattern)) {
            return str;
        }
        StringBuffer result = new StringBuffer();

        if (pattern.indexOf("@") > -1) {
            if (str == null) {
                str = "";
            }
            // 문자열
            String[] strPattern = split(pattern, "@");

//			addlogs("strPattern.length:"+strPattern.length);
            int maxstrPattern = strPattern.length - 1;

            // 들어온값이 적으면 무조건 " " 로 처리
            if (str.length() < maxstrPattern) {
                str += "                                        ";
                str = str.substring(0, maxstrPattern);  //자리수 맞추기
            }
            for (int ii = 0; ii < maxstrPattern; ii++) {
                result.append(strPattern[ii].toString());
                result.append(str.substring(ii, ii + 1));
            }
            result.append(strPattern[maxstrPattern].toString());
            result.append(str.substring(maxstrPattern));
        } else if (pattern.indexOf("#") > -1) {
            // 숫자 포멧
            if (!isIntegerNumber(str)) {
                str = "0";
            }
            if (str.indexOf(",") > 0) {
                str = str.replaceAll(",", "");
            }
            return format(Double.valueOf(str), pattern);

        } else if (pattern.indexOf("0") > -1) {
            // 숫자포멧
            if (!isIntegerNumber(str)) {
                str = "0";
            }
            if (str.indexOf(",") > 0) {
                str = str.replaceAll(",", "");
            }
            return format(Double.valueOf(str), pattern);
        }

        return result.toString();
    }

    public static String format(String str, String pattern, boolean enablenull) {
        if (isNull(pattern)) {
            return str;
        }

        if (enablenull) {
            return null;
        } else {
            return format(null, pattern);
        }
    }

    public static String format(double dbl, String pattern) {
        if (isNull(pattern)) {
            return String.valueOf(dbl);
        }
        String ret = "";
        try {
            ret = new DecimalFormat(pattern).format(dbl);
        } catch (Exception e) {
            ret = String.valueOf(dbl);
        }
        return ret;
    }

    public static String convertHangul(int amount) {
        String money = String.valueOf(amount);
        return convertHangul(money);
    }

    public static String convertHangul(String money) {
        String[] han1 = {"", "일", "이", "삼", "사", "오", "육", "칠", "팔", "구"};
        String[] han2 = {"", "십", "백", "천"};
        String[] han3 = {"", "만", "억", "조", "경"};
        StringBuilder result = new StringBuilder();

        money = (money == null) ? "" : money.trim();
        if (money.equalsIgnoreCase("")) {
            return "";
        } else {
            int len = money.length();
            String temp = "";

            for (int i = len - 1; i >= 0; i--) {
                temp += han1[Integer.parseInt(money.substring(len - i - 1, len - i))];    // 값이있는지 임시 저장

                result.append(han1[Integer.parseInt(money.substring(len - i - 1, len - i))]);

                if (Integer.parseInt(money.substring(len - i - 1, len - i)) > 0) {
                    result.append(han2[i % 4]);
                }

                if (i % 4 == 0 && temp.length() > 0) {  // 4자리마다 han3 단위출력시 값이있을때만
                    result.append(han3[i / 4]).append(" ");
                    temp = "";                      // Temp초기화처리
                }
            }
            return result.toString().trim();
        }
    }

    public static String convertHanja(String money) {
        String[] han1 = {"", "壹", "貳", "參", "四", "伍", "陸", "柒", "捌", "玖"};
        String[] han2 = {"", "拾", "伯", "阡"};
        String[] han3 = {"", "萬", "億", "兆", "京"};
        StringBuilder result = new StringBuilder();

        money = (money == null) ? "" : money.trim();
        if (money.equalsIgnoreCase("")) {
            return "";
        } else {
            int len = money.length();
            String temp = "";

            for (int i = len - 1; i >= 0; i--) {
                temp += han1[Integer.parseInt(money.substring(len - i - 1, len - i))];    // 값이있는지 임시 저장

                result.append(han1[Integer.parseInt(money.substring(len - i - 1, len - i))]);

                if (Integer.parseInt(money.substring(len - i - 1, len - i)) > 0) {
                    result.append(han2[i % 4]);
                }

                if (i % 4 == 0 && temp.length() > 0) {  // 4자리마다 han3 단위출력시 값이있을때만
                    result.append(han3[i / 4]).append(" ");
                    temp = "";                      // Temp초기화처리
                }
            }
            return result.toString().trim();
        }
    }

    public static String replaceSpaces(String s) {
        String ret = "";
        String ss = s;
        while (ss.indexOf("  ") > 0) {
            ss = replaceAll(ss, "  ", " ");
        }

        return ss;
    }

/********************************************************************************************************/
    private static class StrTokenizer {

        private int currentPosition;
        private int maxPosition;
        private String str;
        private String delimiter;
        private int delimiterSize;
        private int countTokens = -1; // Token의 갯수

        /**
         * 대상 문자열에 대한 Tokenizer를 생성합니다. 구분자는 기본값(" ")으로 사용합니다.
         *
         * @param str 대상 문자열
         */
        public StrTokenizer(String str) {
            this(str, " ");
        }

        /**
         * 대상 문자열에 대한 Tokenizer를 생성합니다. delim는 구분자이니다.
         *
         *
         * @param str 대상 문자열
         * @param delim 구분자
         * @throws NullPointerException - str 가 null 경우
         */
        public StrTokenizer(String str, String delim) {
            this.str = str;
            this.delimiter = delim;
            currentPosition = 0;
            maxPosition = this.str.length();
            delimiterSize = this.delimiter.length();
        }

        /**
         * 이용할 수 있는 토큰이 더 존재하지를 판단합니다. 이 메서드가
         * <code>true</code<를 돌려주는 경우, nextToken 메소드를 호출해 다음 토큰을 가져갈 수 있습니다.
     *
         * @return 현 위치의 뒤로, 1개 이상의 토큰이 존재할 경우 true, 그렇지 않을 경우 false
         */
        public boolean hasMoreTokens() {
            return ((currentPosition <= maxPosition) || (str.indexOf(delimiter, currentPosition) > -1));
        }

        /**
         * 다음 토큰을 반환한다. 반환할 토큰이 없으면 null을 돌려준다.
         *
         * @return 다음 토큰
         */
        public String nextToken() {
            String result = null;
            int index = str.indexOf(delimiter, currentPosition);
            if (index > -1) {
                result = str.substring(currentPosition, index);
                currentPosition = index + delimiterSize;
            } else {
                result = str.substring(currentPosition);
                currentPosition = maxPosition + delimiterSize;
            }
            return result.trim();
        }

        /**
         * 토큰의 총 갯수를 반환한다.
         *
         * @return 토큰의 총 갯수
         */
        public int countTokens() {
            if (countTokens < 0) {
                int tCountTokens = 1;
                int cPos = 0;
                int index = 0;
                while ((index = str.indexOf(delimiter, cPos)) > -1) {
                    tCountTokens++;
                    cPos = index + delimiterSize;
                }
                countTokens = tCountTokens;
            }
            return countTokens;
        }

        /**
         * 배열 크기 만큼의 문자열배열을 만든후, 토큰 값을 채워넣은 후 반환한다. 토큰 갯수가 배열 크기보다 작을 경우는, 나머지
         * 값을 null로 설정한고, 토큰 갯수가 배열 크기보다 클 경우는, 배열 크기만큼만 값을 설정한다.
         *
         * @param arraySize 배열 크기
         * @return
         */
        public String[] toArray(int arraySize) {
            String[] array = new String[arraySize];
            for (int i = 0; i < arraySize && hasMoreTokens(); i++) {
                array[i] = nextToken();
            }
            return array;
        }

        /**
         * 토큰들을 문자열배열로 만든다. 문자열배열의 크기는 토큰의 크기로 만들어진다.
         *
         * @return 문자열배열
         */
        public String[] toArray() {
            return toArray(countTokens());
        }
    }
/********************************************************************************************************/
}
