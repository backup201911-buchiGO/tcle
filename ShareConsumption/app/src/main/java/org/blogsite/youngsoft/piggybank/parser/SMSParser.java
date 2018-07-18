package org.blogsite.youngsoft.piggybank.parser;

import java.util.ArrayList;

import org.blogsite.youngsoft.piggybank.R;
import org.blogsite.youngsoft.piggybank.analyzer.Data;
import org.blogsite.youngsoft.piggybank.logs.PGLog;
import org.blogsite.youngsoft.piggybank.logs.StackTrace;
import org.blogsite.youngsoft.piggybank.utils.SmsUtils;
import org.blogsite.youngsoft.piggybank.utils.StringUtils;

/**
 * Created by klee on 2018-01-10.
 */
public class SMSParser {

    private static final String TAG = "SMSParser";


    private Data data = null;
    private final long timestamp;
    private final String address;
    private final String contents;
    private Categorizer categorizer;

    public SMSParser(final long timestamp, final String address, final String contents){
        this.timestamp = timestamp;
        this.address = address;
        this.contents = contents;
    }

    public void setCategorizer(Categorizer categorizer){
        this.categorizer = categorizer;
    }

    public void parse() {
        data = null;
        try {
            IParser parser = null;
            boolean cardMsg = false;

            // KB 국민카드
            if (SmsUtils.getResource(R.string.kbnum1).equals(address) || SmsUtils.getResource(R.string.kbnum2).equals(address)) {
                cardMsg = true;
                SMS sms = new SMS(contents);
                ArrayList<String> body = sms.getBody();
                int len = body.size();
                parser = new KBCardParser();
                parser.setAddress(address);
                parser.setTimestamp(timestamp);
                parser.setContents(contents);
                parser.setCategory(categorizer);
                parser.parse(body);
                data = parser.getData();
            }

            // 신한카드
            else if (SmsUtils.getResource(R.string.shinhannum).equals(address)) {
                cardMsg = true;
                SMS sms = new SMS(contents);
                ArrayList<String> body = sms.getBody();
                int len = body.size();
                parser = new ShinhanParser();
                parser.setAddress(address);
                parser.setTimestamp(timestamp);
                parser.setContents(contents);
                parser.setCategory(categorizer);
                parser.parse(body);
                data = parser.getData();
                if (data == null) {
                    StringBuilder sb = new StringBuilder();
                    sb.append(address).append("|").append(timestamp).append("|").append(StringUtils.replaceAll(contents, "\n", "|"));
                }
            }
            // 농협 카드
            else if (SmsUtils.getResource(R.string.nhnum).equals(address)) {
                cardMsg = true;
                SMS sms = new SMS(contents, "\n");
                ArrayList<String> body = sms.getBody();
                int len = body.size();
                parser = new NHCardParser();
                parser.setAddress(address);
                parser.setTimestamp(timestamp);
                parser.setContents(contents);
                parser.setCategory(categorizer);
                parser.parse(body);
                data = parser.getData();
            }
            // 삼성 카드
            else if (SmsUtils.getResource(R.string.samsungnum).equals(address)) {
                cardMsg = true;
                SMS sms = new SMS(contents, "\n");
                ArrayList<String> body = sms.getBody();
                int len = body.size();
                parser = new SamsungCardParser();
                parser.setAddress(address);
                parser.setTimestamp(timestamp);
                parser.setContents(contents);
                parser.setCategory(categorizer);
                parser.parse(body);
                data = parser.getData();
            }
            // 현대 카드
            else if (SmsUtils.getResource(R.string.hdnum).equals(address)) {
                cardMsg = true;
                SMS sms = new SMS(contents, "\n");
                ArrayList<String> body = sms.getBody();
                int len = body.size();
                parser = new HyundaiCardParser();
                parser.setAddress(address);
                parser.setTimestamp(timestamp);
                parser.setContents(contents);
                parser.setCategory(categorizer);
                parser.parse(body);
                data = parser.getData();
            }
        }catch (Exception e){
            PGLog.e(TAG, StackTrace.getStackTrace(e));
        }
    }

    public Data getData() {
        return data;
    }
}
