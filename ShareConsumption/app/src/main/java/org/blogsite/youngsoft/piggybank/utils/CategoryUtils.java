package org.blogsite.youngsoft.piggybank.utils;

import org.blogsite.youngsoft.json.JSONObject;
import org.blogsite.youngsoft.piggybank.crypt.Crypt;

import java.util.HashMap;
import java.util.Iterator;

public class CategoryUtils {

    public JSONObject decryptJson(String str) throws Exception {
        String dmap = decrypt(str);
        JSONObject json = new JSONObject(dmap);
        return json;
    }

    public HashMap<String, String> getCategoryMap(String str) throws Exception {
        JSONObject json = decryptJson(str);
        HashMap<String, String> map = new HashMap<String, String>();
        Iterator<String> it = json.keys();
        while(it.hasNext()){
            String key = it.next();
            String cvalue = json.getString(key);
            map.put(key, cvalue);
        }
        return map;
    }

    private String crypt(String s) throws Exception{
        try{
            return Crypt.encryptPiggyBank(s.getBytes());

        }catch (Exception e){
            throw  e;
        }
    }

    private String decrypt(String s) throws Exception{
        try{
            return new String(Crypt.decryptPiggyBank(s));

        }catch (Exception e){
            throw  e;
        }
    }

}
