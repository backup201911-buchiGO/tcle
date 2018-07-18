package org.blogsite.youngsoft.piggybank.parser;

import java.util.ArrayList;
import org.blogsite.youngsoft.piggybank.analyzer.Data;

public interface IParser {
    public void parse(ArrayList<String> body);
    public String getUssage();
    public String getAmount();
    public String getDate();
    public boolean getApproval();
    public void log();
    public void convertAmount();
    public boolean isNum(String value);
    public Data getData();
    public void setAddress(String address);
    public void setTimestamp(long timestamp);
    public void setContents(String contents);
    public void setCategory(Categorizer categorizer);
    public long getVersion();
}
