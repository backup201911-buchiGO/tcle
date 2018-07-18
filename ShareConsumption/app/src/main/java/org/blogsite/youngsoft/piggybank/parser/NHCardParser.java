package org.blogsite.youngsoft.piggybank.parser;

import com.google.firebase.crash.FirebaseCrash;
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

public class NHCardParser implements IParser, Serializable {

    private static final long serialVersionUID = SmsUtils.superSerialVersionUID;

    private static final String TAG = "NHCardParser";

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
            accept = false;
            len = body.size();
            if (len == 5) {
                accept = true;
                String flag = body.get(0);
                flag = StringUtils.replaceAll(flag, "[", "");
                flag = StringUtils.replaceAll(flag, "]", "");
                if (flag.startsWith(SmsUtils.getResource(R.string.nhcard))) {
                    cardname = CardEnum.NH_CARD;
                } else if (flag.startsWith(SmsUtils.getResource(R.string.nhcorpcard))) {
                    cardname = CardEnum.NH_CORPCARD;
                } else if (flag.startsWith(SmsUtils.getResource(R.string.nhcheckcard))) {
                    cardname = CardEnum.NH_CHECKCARD;
                } else {
                    accept = false;
                }
                amount = body.get(2);
                ussage = body.get(4);
            } else if (len == 6) {
                String web = body.get(0);
                web = StringUtils.replaceAll(web, "[", "");
                web = StringUtils.replaceAll(web, "]", "");
                if (SmsUtils.getResource(R.string.send_web).equals(web)) {
                    accept = true;
                    String flag = body.get(1);
                    flag = StringUtils.replaceAll(flag, "[", "");
                    flag = StringUtils.replaceAll(flag, "]", "");
                    amount = body.get(3);
                    ussage = body.get(5);
                    if (flag.startsWith(SmsUtils.getResource(R.string.nhcard))) {
                        cardname = CardEnum.NH_CARD;
                    } else if (flag.startsWith(SmsUtils.getResource(R.string.nhcorpcard))) {
                        cardname = CardEnum.NH_CORPCARD;
                    } else if (flag.startsWith(SmsUtils.getResource(R.string.nhcheckcard))) {
                        cardname = CardEnum.NH_CHECKCARD;
                    } else {
                        accept = false;
                    }

                    if (flag.endsWith(SmsUtils.getResource(R.string.accept))) {
                        approval = true;
                    }
                    if (flag.endsWith(SmsUtils.getResource(R.string.reject)) || flag.endsWith(SmsUtils.getResource(R.string.reject1))) {
                        approval = false;
                    }
                } else if (SmsUtils.getResource(R.string.cancellation).equals(web)) {
                    approval = false;
                    accept = true;
                    String flag = body.get(2);
                    flag = StringUtils.replaceAll(flag, "[", "");
                    flag = StringUtils.replaceAll(flag, "]", "");
                    amount = body.get(1);
                    ussage = body.get(5);
                    if (flag.startsWith(SmsUtils.getResource(R.string.nhcard))) {
                        cardname = CardEnum.NH_CARD;
                    } else if (flag.startsWith(SmsUtils.getResource(R.string.nhcorpcard))) {
                        cardname = CardEnum.NH_CORPCARD;
                    } else if (flag.startsWith(SmsUtils.getResource(R.string.nhcheckcard))) {
                        cardname = CardEnum.NH_CHECKCARD;
                    } else {
                        accept = false;
                    }
                } else {
                    accept = false;
                }
            } else if (len == 7) {
                String web = body.get(0);
                String flag = body.get(1);
                web = StringUtils.replaceAll(web, "[", "");
                web = StringUtils.replaceAll(web, "]", "");
                flag = StringUtils.replaceAll(flag, "[", "");
                flag = StringUtils.replaceAll(flag, "]", "");
                if (SmsUtils.getResource(R.string.send_web).equals(web)) {
                    if (SmsUtils.getResource(R.string.cancellation).equals(flag)) {
                        accept = true;
                        approval = false;
                        amount = body.get(2);
                        String cn = body.get(3);
                        cn = StringUtils.replaceAll(cn, "[", "");
                        cn = StringUtils.replaceAll(cn, "]", "");
                        ussage = body.get(6);
                        if (cn.startsWith(SmsUtils.getResource(R.string.nhcard))) {
                            cardname = CardEnum.NH_CARD;
                        } else if (flag.startsWith(SmsUtils.getResource(R.string.nhcorpcard))) {
                            cardname = CardEnum.NH_CORPCARD;
                        } else if (cn.startsWith(SmsUtils.getResource(R.string.nhcheckcard))) {
                            cardname = CardEnum.NH_CHECKCARD;
                        } else {
                            accept = false;
                        }
                    } else if (SmsUtils.getResource(R.string.approve_check).equals(flag)) {
                        accept = true;
                        approval = true;
                        amount = body.get(2);
                        String cn = body.get(3);
                        cn = StringUtils.replaceAll(cn, "[", "");
                        cn = StringUtils.replaceAll(cn, "]", "");
                        ussage = body.get(6);
                        if (cn.startsWith(SmsUtils.getResource(R.string.nhcard))) {
                            cardname = CardEnum.NH_CARD;
                        } else if (flag.startsWith(SmsUtils.getResource(R.string.nhcorpcard))) {
                            cardname = CardEnum.NH_CORPCARD;
                        } else if (cn.startsWith(SmsUtils.getResource(R.string.nhcheckcard))) {
                            cardname = CardEnum.NH_CHECKCARD;
                        } else {
                            accept = false;
                        }
                    } else if (flag.endsWith(SmsUtils.getResource(R.string.reject)) || flag.endsWith(SmsUtils.getResource(R.string.reject1))) {
                        approval = false;
                        accept = true;
                        String cn = body.get(3);
                        cn = StringUtils.replaceAll(cn, "[", "");
                        cn = StringUtils.replaceAll(cn, "]", "");
                        amount = body.get(4);
                        ussage = body.get(6);

                        if (cn.startsWith(SmsUtils.getResource(R.string.nhcard))) {
                            cardname = CardEnum.NH_CARD;
                        } else if (cn.startsWith(SmsUtils.getResource(R.string.nhcorpcard))) {
                            cardname = CardEnum.NH_CORPCARD;
                        } else if (cn.startsWith(SmsUtils.getResource(R.string.nhcheckcard))) {
                            cardname = CardEnum.NH_CHECKCARD;
                        } else {
                            cn = body.get(1);
                            cn = StringUtils.replaceAll(cn, "[", "");
                            cn = StringUtils.replaceAll(cn, "]", "");
                            amount = body.get(4);
                            if (cn.startsWith(SmsUtils.getResource(R.string.nhcorpcard))) {
                                cardname = CardEnum.NH_CARD;
                            } else if (cn.startsWith(SmsUtils.getResource(R.string.nhcorpcard))) {
                                cardname = CardEnum.NH_CORPCARD;
                            } else if (cn.startsWith(SmsUtils.getResource(R.string.nhcheckcard))) {
                                cardname = CardEnum.NH_CHECKCARD;
                            } else {
                                accept = false;
                            }
                        }
                    } else {
                        accept = false;
                    }
                } else {
                    accept = false;
                }
            } else if (len == 8) {
                String web = body.get(0);
                web = StringUtils.replaceAll(web, "[", "");
                web = StringUtils.replaceAll(web, "]", "");
                if (SmsUtils.getResource(R.string.send_web).equals(web)) {
                    accept = true;
                    String flag = body.get(1);
                    flag = StringUtils.replaceAll(flag, "[", "");
                    flag = StringUtils.replaceAll(flag, "]", "");
                    if (flag.endsWith(SmsUtils.getResource(R.string.reject)) || flag.endsWith(SmsUtils.getResource(R.string.reject1))) {
                        approval = false;
                    }
                    amount = body.get(3);
                    ussage = body.get(7);
                    String cn = body.get(4);
                    cn = StringUtils.replaceAll(cn, "[", "");
                    cn = StringUtils.replaceAll(cn, "]", "");

                    if (cn.startsWith(SmsUtils.getResource(R.string.nhcard))) {
                        cardname = CardEnum.NH_CARD;
                    } else if (flag.startsWith(SmsUtils.getResource(R.string.nhcorpcard))) {
                        cardname = CardEnum.NH_CORPCARD;
                    } else if (cn.startsWith(SmsUtils.getResource(R.string.nhcheckcard))) {
                        cardname = CardEnum.NH_CHECKCARD;
                    } else {
                        accept = false;
                    }
                } else {
                    accept = false;
                }
            } else if (len > 8) {
                accept = false;
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
    public void log() {
        String flag = approval == true ? SmsUtils.getResource(R.string.accept) : SmsUtils.getResource(R.string.reject1);
        String msg = cardname.getName() + ",\t" + flag + ",\t" + getDate()
                + ",\t" + amountValue + ",\t" + category.getName() + ",\t" + ussage;

        PGLog.d(TAG, msg);

        FirebaseCrash.logcat(4, TAG, msg);
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
