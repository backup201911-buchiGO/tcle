package org.blogsite.youngsoft.piggybank.parser;

import org.blogsite.youngsoft.piggybank.R;
import org.blogsite.youngsoft.piggybank.utils.SmsUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RulesUtils {
    private static RulesUtils instance = null;

    private HashMap<String, String> categoryMap = null;
    private final List<String> cardPhones;

    private RulesUtils(){
        categoryMap = new HashMap<String, String>();
        cardPhones = new ArrayList<String>();
        cardPhones.add(SmsUtils.getResource(R.string.kbnum1)); //KB 국민카드
        cardPhones.add(SmsUtils.getResource(R.string.kbnum2)); //KB 국민카드
        cardPhones.add(SmsUtils.getResource(R.string.shinhannum)); // 신한카드
        cardPhones.add(SmsUtils.getResource(R.string.nhnum)); // 농협 카드
        cardPhones.add(SmsUtils.getResource(R.string.samsungnum)); // 삼성 카드
        cardPhones.add(SmsUtils.getResource(R.string.hdnum)); // 현대 카드
    }

    public static synchronized RulesUtils getInstance(){
        if(instance==null){
            instance = new RulesUtils();
        }

        return instance;
    }

    public HashMap<String, String> getCategoryMap() {
        return categoryMap;
    }

    public void setCategoryMap(HashMap<String, String> categoryMap) {
        this.categoryMap = categoryMap;
    }

    public List<String> getCardPhones() {
        return cardPhones;
    }
}
