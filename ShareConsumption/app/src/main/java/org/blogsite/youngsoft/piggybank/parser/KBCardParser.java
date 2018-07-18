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

import static org.blogsite.youngsoft.piggybank.utils.StringUtils.replaceAll;

public class KBCardParser implements IParser, Serializable {

    private static final long serialVersionUID = SmsUtils.superSerialVersionUID;

    private static final String TAG = "KBCardParser";

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
                cardname = CardEnum.KB_CARD;
                switch (len) {
                    case 6:
                        amount = body.get(5);
                        if (amount.startsWith(SmsUtils.getResource(R.string.cancel_complete)) && isNum(amount)) {
                            accept = true;
                            approval = false;
                            ussage = body.get(2);
                        } else if (isNum(amount)) {
                            accept = true;
                            approval = true;
                            ussage = body.get(2);
                        }
                        break;
                    case 7:
                        String cname = body.get(1);
                        cname = StringUtils.replaceAll(cname, "[", "");
                        cname = StringUtils.replaceAll(cname, "]", "");
                        if (cname.startsWith(SmsUtils.getResource(R.string.kbcard))) {
                            cardname = CardEnum.KB_CARD;
                        } else if (cname.startsWith(SmsUtils.getResource(R.string.kbcheckcard))) {
                            cardname = CardEnum.KB_CHECKCARD;
                        } else {
                            accept = false;
                        }
                        amount = body.get(4);
                        if (isNum(amount)) {
                            accept = true;
                            ussage = body.get(6);
                        } else {
                            amount = body.get(5);
                            if (isNum(amount)) {
                                accept = true;
                                String a1 = body.get(4);
                                String regexH = "^[ㄱ-ㅎ가-힣]*$";
                                a1 = replaceAll(a1, "(", "");
                                a1 = replaceAll(a1, ")", "");
                                if (a1.matches(regexH)) {
                                    ussage = body.get(4);
                                } else {
                                    a1 = body.get(6);
                                    regexH = "^[a-zA-Zㄱ-ㅎ가-힣]*$";
                                    String a4 = body.get(4);
                                    if (a4.matches(regexH)) {
                                        ussage = a4;
                                    } else {
                                        ussage = a1;
                                    }
                                }
                            }
                        }
                        break;
                    case 8:
                        cname = body.get(1);
                        cname = StringUtils.replaceAll(cname, "[", "");
                        cname = StringUtils.replaceAll(cname, "]", "");
                        if (cname.startsWith(SmsUtils.getResource(R.string.kbcard))) {
                            cardname = CardEnum.KB_CARD;
                            if (isNum(body.get(5))) {
                                amount = body.get(5);
                                String a1 = "";
                                for (int i = 6; i < len; i++) {
                                    a1 += body.get(i) + " ";
                                }
                                ussage = a1.trim();
                                accept = true;
                            } else {
                                amount = body.get(4);
                                amount = amount.substring(0, amount.indexOf(SmsUtils.getResource(R.string.won)));
                                if (isNum(amount)) {
                                    String a1 = "";
                                    for (int i = 7; i < len; i++) {
                                        a1 += body.get(i) + " ";
                                    }
                                    ussage = a1.trim();
                                    accept = true;
                                }
                            }

                        } else if (cname.startsWith(SmsUtils.getResource(R.string.kbcheckcard))) {
                            cardname = CardEnum.KB_CHECKCARD;
                            amount = body.get(5);
                            if (isNum(amount)) {
                                accept = true;
                                String a1 = "";
                                for (int i = 6; i < len; i++) {
                                    a1 += body.get(i) + " ";
                                }
                                ussage = a1.trim();
                            }

                        } else {
                            accept = false;
                        }
                        break;
                    case 9:
                        cname = body.get(1);
                        cname = StringUtils.replaceAll(cname, "[", "");
                        cname = StringUtils.replaceAll(cname, "]", "");
                        if (cname.startsWith(SmsUtils.getResource(R.string.kbcard))) {
                            cardname = CardEnum.KB_CARD;
                        } else if (cname.startsWith(SmsUtils.getResource(R.string.kbcheckcard))) {
                            cardname = CardEnum.KB_CHECKCARD;
                        } else {
                            accept = false;
                            System.out.println(body);
                        }

                        amount = body.get(3);
                        if (isNum(amount)) {
                            accept = true;
                            String a1 = "";
                            for (int i = 7; i < len; i++) {
                                a1 += body.get(i) + " ";
                            }
                            ussage = a1.trim();
                        } else {
                            amount = body.get(4);
                            if (isNum(amount)) {
                                accept = true;
                                String a1 = "";
                                for (int i = 7; i < len; i++) {
                                    a1 += body.get(i) + " ";
                                }
                                ussage = a1.trim();
                            } else if (isNum(body.get(5))) {
                                accept = true;
                                amount = body.get(5);
                                String a1 = "";
                                for (int i = 6; i < body.size(); i++) {
                                    a1 += body.get(i) + " ";
                                }
                                ussage = a1.trim();
                            } else {
                                /**
                                 * 해외구매인 경우(금액 실수)
                                 */
                                accept = false;
                            }
                        }
                        break;
                    case 10:
                        cname = body.get(1);
                        cname = StringUtils.replaceAll(cname, "[", "");
                        cname = StringUtils.replaceAll(cname, "]", "");
                        if (cname.startsWith(SmsUtils.getResource(R.string.kbcard))) {
                            cardname = CardEnum.KB_CARD;
                        } else if (cname.startsWith(SmsUtils.getResource(R.string.kbcheckcard))) {
                            cardname = CardEnum.KB_CHECKCARD;
                        } else {
                            accept = false;
                        }
                        amount = body.get(6);
                        if (isNum(amount)) {
                            accept = true;
                            String a1 = "";
                            for (int i = 7; i < len; i++) {
                                a1 += body.get(i) + " ";
                            }
                            ussage = a1.trim();
                        } else if (isNum(body.get(3))) {
                            accept = true;
                            amount = body.get(3);
                            String a1 = "";
                            for (int i = 7; i < len; i++) {
                                a1 += body.get(i) + " ";
                            }
                            ussage = a1.trim();
                        } else {
                            amount = body.get(5);
                            if (isNum(amount)) {
                                accept = true;
                                String a1 = "";
                                for (int i = 6; i < len; i++) {
                                    a1 += body.get(i) + " ";
                                }
                                ussage = a1.trim();
                            } else {
                                /**
                                 * 해외구매인 경우(금액 실수)
                                 */
                                accept = false;
                            }
                        }
                        if (!accept) {
                            if (isNum(body.get(4))) {
                                accept = true;
                                amount = body.get(4);
                                String a1 = "";
                                for (int i = 7; i < len; i++) {
                                    a1 += body.get(i) + " ";
                                }
                                ussage = a1.trim();
                                if (SmsUtils.getResource(R.string.reject).equals(body.get(2)) || SmsUtils.getResource(R.string.reject1).equals(body.get(2))) {
                                    approval = false;
                                } else {
                                    approval = true;
                                }
                            }
                        }
                        break;
                    case 11:
                        cname = body.get(1);
                        cname = StringUtils.replaceAll(cname, "[", "");
                        cname = StringUtils.replaceAll(cname, "]", "");
                        if (cname.startsWith(SmsUtils.getResource(R.string.kbcard))) {
                            cardname = CardEnum.KB_CARD;
                        } else if (cname.startsWith(SmsUtils.getResource(R.string.kbcheckcard))) {
                            cardname = CardEnum.KB_CHECKCARD;
                        } else {
                            accept = false;
                        }
                        amount = body.get(4);
                        if (isNum(amount)) {
                            accept = true;
                            String a1 = "";
                            for (int i = 6; i < len; i++) {
                                a1 += body.get(i) + " ";
                            }
                            ussage = a1.trim();
                        } else {
                            amount = body.get(6);
                            if (isNum(amount)) {
                                accept = true;
                                String a1 = "";
                                for (int i = 7; i < len; i++) {
                                    a1 += body.get(i);
                                }
                                ussage = a1.trim();
                            }
                        }
                        break;
                    default:
                        accept = false;
                        break;
                }
            } else {
                cardname = CardEnum.KB_CARD;
                String flag = body.get(0);
                flag = StringUtils.replaceAll(flag, "[", "");
                flag = StringUtils.replaceAll(flag, "]", "");
                if (flag.startsWith(SmsUtils.getResource(R.string.kbcard))) {
                    cardname = CardEnum.KB_CARD;
                } else if (flag.startsWith(SmsUtils.getResource(R.string.kbcheckcard))) {
                    cardname = CardEnum.KB_CHECKCARD;
                } else {
                    accept = false;
                }
                approval = true;
                if (flag.equals(SmsUtils.getResource(R.string.reject)) || flag.equals(SmsUtils.getResource(R.string.reject1))) {
                    approval = false;
                }
                amount = body.get(2);
                if (isNum(amount)) {
                    accept = true;
                    String a1 = "";
                    for (int i = 6; i < len; i++) {
                        a1 += body.get(i);
                    }
                    ussage = a1.trim();
                } else if (isNum(body.get(3))) {
                    accept = true;
                    amount = body.get(3);
                    String a1 = "";
                    for (int i = 5; i < len; i++) {
                        a1 += body.get(i);
                    }
                    ussage = a1.trim();
                } else if (isNum(body.get(4))) {
                    accept = true;
                    amount = body.get(4);
                    String a1 = "";
                    for (int i = 5; i < body.size(); i++) {
                        a1 += body.get(i) + " ";
                    }
                    ussage = a1.trim();
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

    @Override
    public void log() {
        String flag = approval == true ? SmsUtils.getResource(R.string.accept) : SmsUtils.getResource(R.string.reject);
        String msg = cardname.getName() + ",\t" + flag + ",\t" + getDate()
                + ",\t" + amountValue + ",\t" + category.getName() + ",\t" + ussage;

        PGLog.d(TAG, msg);
    }
}