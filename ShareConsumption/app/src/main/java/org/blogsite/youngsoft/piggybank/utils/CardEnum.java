package org.blogsite.youngsoft.piggybank.utils;

import org.blogsite.youngsoft.piggybank.R;

/**
 * Created by klee on 2017-12-21.
 */

public enum CardEnum implements IEnum {

    UNKNOWN_CARD(			1, 	    SmsUtils.getResource(R.string.UNKNOWN_CARD)),
    KB_CARD(			    0, 	    SmsUtils.getResource(R.string.KB_CARD)),
    SHINHAN_CARD(	        22, 	SmsUtils.getResource(R.string.SHINHAN_CARD)),
    SHINHAN_CHECKCARD(	1, 	    SmsUtils.getResource(R.string.SHINHAN_CHECKCARD)),
    SHINHAN_CORPCARD(		2, 	    SmsUtils.getResource(R.string.SHINHAN_CORPCARD)),
    KB_CHECKCARD(		    3, 	    SmsUtils.getResource(R.string.KB_CHECKCARD)),
    SHINHAN_WELFARECARD(	4, 	    SmsUtils.getResource(R.string.SHINHAN_WELFARECARD)),
    SAMSUNG_CARD(		    5, 	    SmsUtils.getResource(R.string.SAMSUNG_CARD)),
    SAMSUNG_CHECKCARD(	6, 	    SmsUtils.getResource(R.string.SAMSUNG_CHECKCARD)),
    SAMSUNG_CORPARD(		7, 	    SmsUtils.getResource(R.string.SAMSUNG_CORPARD)),
    HYUNDAE_CARD(		    8, 	    SmsUtils.getResource(R.string.HYUNDAE_CARD)),
    HYUNDAE_CHECKCARD(	9, 	    SmsUtils.getResource(R.string.HYUNDAE_CHECKCARD)),
    HYUNDAE_CORPCARD(	    10, 	SmsUtils.getResource(R.string.HYUNDAE_CORPCARD)),
    HYUNDAE_WELFARECARD(	11, 	SmsUtils.getResource(R.string.HYUNDAE_WELFARECARD)),
    EMART_CARD(	        12, 	SmsUtils.getResource(R.string.EMART_CARD)),
    HANA_CARD(	            13, 	SmsUtils.getResource(R.string.HANA_CARD)),
    HANA_CHECKCARD(	    14, 	SmsUtils.getResource(R.string.HANA_CHECKCARD)),
    LOTTE_CARD(	        15, 	SmsUtils.getResource(R.string.LOTTE_CARD)),
    LOTTE_CHECKCARD(	    16, 	SmsUtils.getResource(R.string.LOTTE_CHECKCARD)),
    LOTTE_CORPCARD(	    17, 	SmsUtils.getResource(R.string.LOTTE_CORPCARD)),
    NH_CARD(	            18, 	SmsUtils.getResource(R.string.NH_CARD)),
    NH_CHECKCARD(	        19, 	SmsUtils.getResource(R.string.NH_CHECKCARD)),
    NH_CORPCARD(	        20, 	SmsUtils.getResource(R.string.NH_CORPCARD)),
    NH_WELFARECARD(	    21, 	SmsUtils.getResource(R.string.NH_WELFARECARD));

    private static final long serialVersionUID = 20170515L;
    private final transient int value;
    private final transient String name;

    /**
     * 열거형 클래스 카드 목록 분류 생성자
     * @param value	값
     * @param name		목록 이름
     */
    private CardEnum(int value, String name){
        this.value = value;
        this.name = name;
    }

    /**
     * 목록 이름
     * @return
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * 목록 값
     * @return
     */
    @Override
    public int getValue() {
        return value;
    }

    /**
     * 목록 이름으로 목록 획득
     * @param name		목록 이름
     * @return
     */
    public static CardEnum getByName(String name)
    {
        return (CardEnum) EnumUtil.getByName(values(), name);
    }

    /**
     * 목록 값으로 목록 획득
     * @param value	목록 값
     * @return
     */
    public static CardEnum getByValue(int value)
    {
        return (CardEnum) EnumUtil.getByValue(values(), value);
    }
}

