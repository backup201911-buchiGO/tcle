package org.blogsite.youngsoft.piggybank.parser;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.blogsite.youngsoft.piggybank.R;
import org.blogsite.youngsoft.piggybank.analyzer.Data;
import org.blogsite.youngsoft.piggybank.logs.PGLog;
import org.blogsite.youngsoft.piggybank.logs.StackTrace;
import org.blogsite.youngsoft.piggybank.utils.CardEnum;
import org.blogsite.youngsoft.piggybank.utils.CategoryEnum;
import org.blogsite.youngsoft.piggybank.utils.SmsUtils;
import org.blogsite.youngsoft.piggybank.utils.StringUtils;

public class ShinhanParser implements IParser, Serializable {

    private static final long serialVersionUID = SmsUtils.superSerialVersionUID;

    private static final String TAG = "ShinhanParser";

    private long version = 1L;

    private String address;
    private boolean accept = false;
    private String ussage = "";
    private String amount = "";
    private int amountValue = 0;

    private long timestamp = -1L;
    private int len = 0;
    private boolean approval = true;

    private Categorizer categorizer;
    private CardEnum cardname = CardEnum.UNKNOWN_CARD;
    private CategoryEnum category = CategoryEnum.Unclassified;

    private Data data = null;

    private String contents = null;

    @Override
    public long getVersion() {
        return version;
    }

    @Override
    public void setCategory(Categorizer categorizer){
        this.categorizer = categorizer;
    }

    @Override
    public void setAddress(String address) {
        this.address = address;
    }

    @Override
    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public void setContents(String contents) {
        this.contents = contents;
    }

    @Override
    public Data getData() {
        return data;
    }

    @Override
    public void parse(ArrayList<String> body) {
        try {
            len = body.size();
            String web = body.get(0);
            String flag = body.get(1);
            web = StringUtils.replaceAll(web, "[", "");
            web = StringUtils.replaceAll(web, "]", "");
            flag = StringUtils.replaceAll(flag, "[", "");
            flag = StringUtils.replaceAll(flag, "]", "");
            if (SmsUtils.getResource(R.string.send_web).equals(web)) {
                if (null != flag) {
                    if (SmsUtils.getResource(R.string.sh_approve_check).equals(flag)) {
                        approval = true;
                        parseShinhanWeb(body);
                    } else if (SmsUtils.getResource(R.string.shcorp_approve_check).equals(flag)) {
                        approval = true;
                        parseShinhanCorporationWeb(body);
                    } else if (SmsUtils.getResource(R.string.sh_approve_cancel).equals(flag)) {
                        approval = false;
                        parseShinhanWeb(body);
                    } else if (SmsUtils.getResource(R.string.shcorp_approve_cancel).equals(flag)) {
                        approval = false;
                        parseShinhanCorporationWeb(body);
                    } else {
                        accept = false;
                    }
                }
            } else {
                if (null != web) {
                    if (SmsUtils.getResource(R.string.sh_approve_check).equals(web)) {
                        approval = true;
                        parseShinhanOld(body);
                    } else if (SmsUtils.getResource(R.string.shcorp_approve_check).equals(web)) {
                        approval = true;
                        parseShinhanCorporationOld(body);
                    } else if (SmsUtils.getResource(R.string.sh_approve_cancel).equals(web)) {
                        approval = false;
                        parseShinhanOld(body);
                    } else if (SmsUtils.getResource(R.string.shcorp_approve_cancel).equals(web)) {
                        approval = false;
                        parseShinhanCorporationOld(body);
                    } else {
                        accept = false;
                    }
                }
            }
            if (accept) {
                category = categorizer.parseCategory(timestamp, ussage);
                convertAmount();

                data = new Data();
                data.setBody(contents);
                data.setAccept(accept);
                data.setApproval(approval);
                data.setAmount(amountValue);
                data.setCard(cardname);
                data.setCategory(category);
                data.setTimestamp(timestamp);

                log();
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append(address).append("|").append(timestamp).append("|").append(StringUtils.replaceAll(contents, "\n", "|"));
                PGLog.d(TAG, sb.toString());
            }
        }catch (Exception e){
            PGLog.e(TAG, StackTrace.getStackTrace(e));
        }
    }

    private void parseShinhanWeb(ArrayList<String> body) {
        cardname = CardEnum.SHINHAN_CHECKCARD;
        len = body.size();
        if (len == 6) {
            accept = true;
            amount = body.get(4); //사용금액
            ussage = body.get(5); //사용내역
        } else if (len >= 7) {
            String regex = "\\p{Digit}+/\\p{Digit}+\\s\\p{Digit}+:\\p{Digit}+";
            String tmp = body.get(2) + " " + body.get(3);
            if (tmp.matches(regex)) {
                amount = body.get(4);//사용금액
                if (isNum(amount)) {
                    accept = true;
                    String b3 = "";         //사용내역
                    for (int i = 5; i < len; i++) {
                        b3 += body.get(i) + " ";
                    }
                    ussage = b3.trim();
                }
            } else {
                amount = body.get(5);//사용금액
                if (isNum(amount)) {
                    accept = true;
                    String b3 = "";         //사용내역
                    for (int i = 6; i < len; i++) {
                        b3 += body.get(i) + " ";
                    }
                    ussage = b3.trim();
                }
            }
        }
    }

    private void parseShinhanCorporationWeb(ArrayList<String> body) {
        cardname = CardEnum.SHINHAN_CORPCARD;
        len = body.size();
        if (len == 6) {
            amount = body.get(4); //사용금액
            if (isNum(amount)) {
                accept = true;
                ussage = body.get(5); //사용내역
            }
        } else if (len >= 7) {
            String regex = "\\p{Digit}+\\s\\p{Digit}+/\\p{Digit}+\\s\\p{Digit}+:\\p{Digit}+";
            String tmp = body.get(2) + " " + body.get(3) + " " + body.get(4);
            if (tmp.matches(regex)) {
                amount = body.get(5);//사용금액
                if (isNum(amount)) {
                    accept = true;
                    String b3 = "";         //사용내역
                    for (int i = 6; i < len; i++) {
                        b3 += body.get(i) + " ";
                    }
                    ussage = b3.trim();
                }
            } else {
                amount = body.get(5);//사용금액
                if (isNum(amount)) {
                    accept = true;
                    String b3 = "";         //사용내역
                    for (int i = 6; i < len; i++) {
                        b3 += body.get(i) + " ";
                    }
                    ussage = b3.trim();
                }
            }
        }
    }

    private void parseShinhanOld(ArrayList<String> body) {
        cardname = CardEnum.SHINHAN_CHECKCARD;
        len = body.size();
        String regex = "\\p{Digit}+/\\p{Digit}+\\s\\p{Digit}+:\\p{Digit}+";
        String tmp = body.get(2) + " " + body.get(3);
        if (tmp.matches(regex)) {
            amount = body.get(4);//사용금액
            if (isNum(amount)) {
                accept = true;
                String b3 = "";         //사용내역
                for (int i = 5; i < len; i++) {
                    b3 += body.get(i) + " ";
                }
                ussage = b3.trim();
            }
        } else {
            amount = body.get(5);//사용금액
            if (isNum(amount)) {
                accept = true;
                String b3 = "";         //사용내역
                for (int i = 6; i < len; i++) {
                    b3 += body.get(i) + " ";
                }
                ussage = b3.trim();
            }
        }
    }

    private void parseShinhanCorporationOld(ArrayList<String> body) {
        cardname = CardEnum.SHINHAN_CORPCARD;
        len = body.size();
        String regex = "\\p{Digit}+\\s\\p{Digit}+/\\p{Digit}+\\s\\p{Digit}+:\\p{Digit}+";
        //String tmp = body.get(1) + " " + body.get(2) + " " + body.get(3);
        String tmp = body.get(2) + " " + body.get(3);
        if (tmp.matches(regex)) {
            amount = body.get(4);//사용금액
            if (isNum(amount)) {
                accept = true;
                String b3 = "";         //사용내역
                for (int i = 5; i < len; i++) {
                    b3 += body.get(i) + " ";
                }
                ussage = b3.trim();
            }
        } else {

            amount = body.get(5);//사용금액
            if (isNum(amount)) {
                accept = true;
                String b3 = "";         //사용내역
                for (int i = 6; i < len; i++) {
                    b3 += body.get(i) + " ";
                }
                ussage = b3.trim();
            }else if(isNum(body.get(4))){
                amount = body.get(4);//사용금액
                accept = true;
                String b3 = "";         //사용내역
                for (int i = 5; i < len; i++) {
                    b3 += body.get(i) + " ";
                }
                ussage = b3.trim();

            }
        }
    }

    @Override
    public String getUssage() {
        return ussage;
    }

    @Override
    public String getAmount() {
        return amount;
    }

    @Override
    public String getDate() {
        DateFormat df = new SimpleDateFormat("yy.MM.dd.EEEE");

        return df.format(new Date(timestamp));
    }

    @Override
    public boolean getApproval() {
        return approval;
    }

    @Override
    public void log() {
        String flag = approval == true ? SmsUtils.getResource(R.string.accept) : SmsUtils.getResource(R.string.reject);
        String msg = cardname.getName() + ",\t" + flag + ",\t" + getDate()
                + ",\t" + amountValue + ",\t" + category.getName() + ",\t" + ussage;

        PGLog.d(TAG, msg);
    }

    @Override
    public void convertAmount() {
        String tamount = SmsUtils.replaceAmount(amount);
        if (isNum(tamount)) {
            amountValue = Integer.parseInt(tamount);
            //amountValue = (int) Float.parseFloat(tamount);
            if (amountValue > 0) {
                amountValue = approval == true ? amountValue : -amountValue;
            }
        } else {
            amountValue = 0;
        }
    }

    @Override
    public boolean isNum(String value) {
        return SmsUtils.isNum(value);
    }
}
