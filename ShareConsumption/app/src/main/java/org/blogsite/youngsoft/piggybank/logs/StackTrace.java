package org.blogsite.youngsoft.piggybank.logs;

public class StackTrace {
	public static String getStackTrace(Throwable e) {
		java.io.ByteArrayOutputStream bos = new java.io.ByteArrayOutputStream();
		java.io.PrintWriter writer = new java.io.PrintWriter(bos);
		e.printStackTrace(writer);
		writer.flush();
		String ret = bos.toString();
		return ret;
	}
}
