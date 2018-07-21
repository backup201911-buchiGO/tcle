package org.blogsite.youngsoft.piggybank.parser;

import java.io.Serializable;

import org.blogsite.youngsoft.piggybank.R;
import org.blogsite.youngsoft.piggybank.logs.PGLog;
import org.blogsite.youngsoft.piggybank.logs.StackTrace;
import org.blogsite.youngsoft.piggybank.utils.SmsUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import org.blogsite.youngsoft.piggybank.analyzer.Data;
import org.blogsite.youngsoft.piggybank.utils.CardEnum;
import org.blogsite.youngsoft.piggybank.utils.CategoryEnum;
import org.blogsite.youngsoft.piggybank.utils.StringUtils;

public class SamsungCardParser implements IParser, Serializable {
    private static final long serialVersionUID = SmsUtils.superSerialVersionUID;
    private static final String TAG = "SmasungCardParser";

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
            web = StringUtils.replaceAll(web, "[", "");
            web = StringUtils.replaceAll(web, "]", "");
            if (SmsUtils.getResource(R.string.send_web).equals(web)) {
                cardname = CardEnum.SAMSUNG_CARD;
                //System.out.println("len = " + len + " >> " + body);
                if (body.size() > 2 && isNum(body.get(2))) {
                    accept = true;
                    amount = body.get(2);
                    ussage = body.get(3);
                    if (body.get(1).endsWith(SmsUtils.getResource(R.string.accept))) {
                        approval = true;
                    } else if (body.get(1).endsWith(SmsUtils.getResource(R.string.reject)) || body.get(1).endsWith(SmsUtils.getResource(R.string.reject1))) {
                        approval = false;
                    }
                } else if (body.size() > 4 && isNum(body.get(4))) {
                    accept = true;
                    approval = true;
                    amount = body.get(4);
                    ussage = body.get(3);
                } else {
                    accept = false;
                }
            } else {
                cardname = CardEnum.SAMSUNG_CARD;
                //TODO: 삼카의 경우 body.get(1)에서 outof index 예외 발생 대응 임시 조취
                if ( body.size() > 1 ) {
                    if (isNum(body.get(1))) {
                        accept = true;
                        approval = true;
                        amount = body.get(1);
                        String a = body.get(2);
                        ussage = a.substring(a.lastIndexOf(" ")).trim();
                    } else {
                        accept = false;
                    }
                } else {
                    log();
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
    public void convertAmount() {
        String tamount =  SmsUtils.replaceAmount(amount);
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

    @Override
    public void log() {
        String flag = approval == true ? SmsUtils.getResource(R.string.accept) : SmsUtils.getResource(R.string.reject);
        String msg = cardname.getName() + ",\t" + flag + ",\t" + getDate()
                + ",\t" + amountValue + ",\t" + category.getName() + ",\t" + ussage;

        PGLog.d(TAG, msg);
    }
}
