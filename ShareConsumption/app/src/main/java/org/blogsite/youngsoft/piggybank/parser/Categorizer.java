package org.blogsite.youngsoft.piggybank.parser;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.blogsite.youngsoft.piggybank.R;
import org.blogsite.youngsoft.piggybank.crypt.Crypt;
import org.blogsite.youngsoft.piggybank.logs.PGLog;
import org.blogsite.youngsoft.piggybank.logs.StackTrace;
import org.blogsite.youngsoft.piggybank.utils.CategoryEnum;
import org.blogsite.youngsoft.piggybank.utils.SmsUtils;
import org.blogsite.youngsoft.piggybank.utils.StringUtils;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;

public class Categorizer  implements Serializable {
    private static final long serialVersionUID = SmsUtils.superSerialVersionUID;
    private static final String TAG = "Categorizer";

    private String meal = "";
    private String culture = "";
    private String medical = "";
    private String communication = "";
    private String dues = "";
    private String traffic = "";
    private String shopping = "";

    private HashMap<String, String> categoryMap;
    private String[][] cat = new String[7][];

    private long version = 1L;

    private boolean search(String location, String str){
        if(str!=null && !"".equals(str)){
            return location.indexOf(str)>-1;
        }else{
            return false;
        }
    }

    public CategoryEnum parseCategory(long timestamp, String location){
        init();
        CategoryEnum cat = parse(timestamp, location);
        if(cat==CategoryEnum.Unclassified){
            sendUnclassified(timestamp, location);
        }
        return cat;
    }

    private void sendUnclassified(long timestamp, String location){
        try {
            DateFormat df = new SimpleDateFormat(SmsUtils.getResource(R.string.date_format_kor));
            String s = df.format(new Date(timestamp));
            String cmsg = Crypt.encryptPiggyBank(location.getBytes());

            //파이어베이스 실시간 데이터베이스
            DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
            DatabaseReference mConditionRef = mDatabase.child("PiggyBank").child("Rules");
            mConditionRef.child("Unclassified").child(s).setValue(location);
        }catch (Exception e){
            PGLog.e(TAG, StackTrace.getStackTrace(e));
        }
    }

    private CategoryEnum parse(long timestamp, String location){
        CategoryEnum ret = CategoryEnum.Unclassified;
        try {
            if (!"".equals(location)) {
                for (String s : cat[CategoryEnum.Meal.getValue()]) {
                    if (search(location, s)) {
                        ret = CategoryEnum.Meal;
                    }
                }
                for (String s : cat[CategoryEnum.Culture.getValue()]) {
                    if (search(location, s)) {
                        ret = CategoryEnum.Culture;
                    }
                }
                for (String s : cat[CategoryEnum.Medical.getValue()]) {
                    if (search(location, s)) {
                        ret = CategoryEnum.Medical;
                    }
                }
                for (String s : cat[CategoryEnum.Communication.getValue()]) {
                    if (search(location, s)) {
                        ret = CategoryEnum.Communication;
                    }
                }
                for (String s : cat[CategoryEnum.Traffic.getValue()]) {
                    if (search(location, s)) {
                        ret = CategoryEnum.Traffic;
                    }
                }
                for (String s : cat[CategoryEnum.Dues.getValue()]) {
                    if (search(location, s)) {
                        ret = CategoryEnum.Dues;
                    }
                }
                for (String s : cat[CategoryEnum.Shopping.getValue()]) {
                    if (search(location, s)) {
                        ret = CategoryEnum.Shopping;
                    }
                }

                if (search(location, SmsUtils.getResource(R.string.expt_cat_1)) || search(location, SmsUtils.getResource(R.string.expt_cat_2))) {
                    ret = CategoryEnum.Traffic;
                }
            }
        }catch (Exception e){
            PGLog.e(TAG, StackTrace.getStackTrace(e));
        }
        return ret;
    }

    private void init(){
        cat[CategoryEnum.Meal.getValue()] = StringUtils.split(meal, "|");
        cat[CategoryEnum.Culture.getValue()] = StringUtils.split(culture, "|");
        cat[CategoryEnum.Medical.getValue()] = StringUtils.split(medical, "|");
        cat[CategoryEnum.Communication.getValue()] = StringUtils.split(communication, "|");
        cat[CategoryEnum.Dues.getValue()] = StringUtils.split(dues, "|");
        cat[CategoryEnum.Traffic.getValue()] = StringUtils.split(traffic, "|");
        cat[CategoryEnum.Shopping.getValue()] = StringUtils.split(shopping, "|");
    }

    public void setCategoryMap(HashMap<String, String> categoryMap){
        try {
            this.categoryMap = categoryMap;
            if (categoryMap != null && categoryMap.size() > 0) {
                Iterator<String> it = categoryMap.keySet().iterator();
                while (it.hasNext()) {
                    String key = it.next();
                    String cvalue = categoryMap.get(key);
                    String value = categoryMap.get(key);
                    if ("Meal".equals(key)) {
                        setMeal(value);
                    } else if ("Culture".equals(key)) {
                        setCommunication(value);
                    } else if ("Medical".equals(key)) {
                        setMedical(value);
                    } else if ("Communication".equals(key)) {
                        setCommunication(value);
                    } else if ("Traffic".equals(key)) {
                        setTraffic(value);
                    } else if ("Dues".equals(key)) {
                        setDues(value);
                    } else if ("Shopping".equals(key)) {
                        setShopping(value);
                    }
                }
            }
        }catch (Exception e){
            PGLog.e(TAG, StackTrace.getStackTrace(e));
        }
    }

    public void setMeal(String meal) {
        this.meal = meal;
    }

    public void setCulture(String culture) {
        this.culture = culture;
    }

    public void setMedical(String medical) {
        this.medical = medical;
    }

    public void setCommunication(String communication) {
        this.communication = communication;
    }

    public void setDues(String dues) {
        this.dues = dues;
    }

    public void setTraffic(String traffic) {
        this.traffic = traffic;
    }

    public void setShopping(String shopping) {
        this.shopping = shopping;
    }

    public long getVersion() {
        return version;
    }
}
