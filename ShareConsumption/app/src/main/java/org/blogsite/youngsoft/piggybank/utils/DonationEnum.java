package org.blogsite.youngsoft.piggybank.utils;

import org.blogsite.youngsoft.piggybank.R;

public enum DonationEnum implements IEnum {
    Etc(0, SmsUtils.getResource(R.string.Etc)),
    Catholic(1, SmsUtils.getResource(R.string.Catholic)),
    Christian(2, SmsUtils.getResource(R.string.Christian)),
    Buddhism(3, SmsUtils.getResource(R.string.Buddhism)),
    Religion(4, SmsUtils.getResource(R.string.Religion)),
    Politics(5, SmsUtils.getResource(R.string.Politics)),
    Social(6, SmsUtils.getResource(R.string.Social)),
    Welfare(7, SmsUtils.getResource(R.string.Welfare));

    private static final long serialVersionUID = SmsUtils.superSerialVersionUID;
    private final transient int value;
    private final transient String name;

    private DonationEnum(int value, String name){
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

    public static DonationEnum getByName(String name){
        return (DonationEnum)EnumUtil.getByName(values(), name);
    }

    public static DonationEnum getByValue(int value){
        return (DonationEnum)EnumUtil.getByValue(values(), value);
    }
}
