package org.blogsite.youngsoft.piggybank.utils;

import org.blogsite.youngsoft.piggybank.R;

/**
 * 카드 사용 내역에 대한 열거형 클래스
 */
public enum CategoryEnum implements IEnum {

	Unclassified(	-1, SmsUtils.getResource(R.string.Unclassified)),
	Meal(			0, 	SmsUtils.getResource(R.string.Meal)),
	Culture(		1, 	SmsUtils.getResource(R.string.Culture)),
	Medical(		2, 	SmsUtils.getResource(R.string.Medical)),
	Communication(3, 	SmsUtils.getResource(R.string.Communication)),
	Traffic(		4,	SmsUtils.getResource(R.string.Traffic)),
	Dues(			5, SmsUtils.getResource(R.string.Dues)),
	Shopping(		6,	SmsUtils.getResource(R.string.Shopping));

	private static final long serialVersionUID = SmsUtils.superSerialVersionUID;
	private final transient int value;
	private final transient String name;

	/**
	 * 열거형 클래스 카드 사용 목록 분류 생성자
	 * @param value	값
	 * @param name		목록 이름
	 */
	private CategoryEnum(int value, String name){
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
	public static CategoryEnum getByName(String name)
	{
		return (CategoryEnum) EnumUtil.getByName(values(), name);
	}

	/**
	 * 목록 값으로 목록 획득
	 * @param value	목록 값
	 * @return
	 */
	public static CategoryEnum getByValue(int value)
	{
		return (CategoryEnum) EnumUtil.getByValue(values(), value);
	}
}
