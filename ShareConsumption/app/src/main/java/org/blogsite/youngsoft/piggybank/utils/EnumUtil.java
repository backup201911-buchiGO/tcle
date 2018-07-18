package org.blogsite.youngsoft.piggybank.utils;

public final class EnumUtil {
	/**
	 *
	 */
	public static IEnum getByValue(IEnum[] values, Integer value)
	{
		if (values != null && value != null)
		{
			for(IEnum e:values)
			{
				if (value.equals(e.getValue()))
				{
					return e;
				}
			}
		}
		return null;
	}
	
	/**
	 *
	 */
	public static IEnum getByName(IEnum[] values, String name)
	{
		return EnumUtil.<IEnum>getEnumByName(values, name);
	}

	public static <T extends IEnum> T getEnumByName(T[] values, String name)
	{
		if (values != null && name != null)
		{
			for(T e:values)
			{
				if (name.equals(e.getName()))
				{
					return e;
				}
			}
		}
		return null;
	}

	/**
	 *
	 */
	public static IEnum getByEnumConstantName(IEnum[] values, String name)
	{
		if (values != null && name != null)
		{
			for(IEnum e:values)
			{
				if (name.equals(((Enum<?>)e).name()))
				{
					return e;
				}
			}
		}
		return null;
	}
	
	/**
	 *
	 */
	private EnumUtil()
	{
	}
}
