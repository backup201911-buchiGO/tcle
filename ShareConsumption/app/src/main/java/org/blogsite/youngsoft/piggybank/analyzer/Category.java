package org.blogsite.youngsoft.piggybank.analyzer;

import org.blogsite.youngsoft.piggybank.utils.CategoryEnum;
import org.blogsite.youngsoft.piggybank.utils.StringUtils;

import java.util.HashMap;

public class Category {

    public static final int MAX_CATEGORY = 7;
    private HashMap<CategoryEnum, Integer> catMap = new HashMap<CategoryEnum, Integer>();

    public Category() {
        for (int i = 0; i < MAX_CATEGORY + 1; i++) {
            CategoryEnum cat = CategoryEnum.getByValue(i);
            catMap.put(cat, 0);
        }
    }

    public void setCategotyValue(CategoryEnum cat, int value) {
        if (catMap.containsKey(cat)) {
            int amount = catMap.get(cat);
            catMap.remove(cat);
            catMap.put(cat, amount + value);
        } else {
            catMap.put(cat, value);
        }
    }

    public void setCategotyValue(int idx, int value) {
        CategoryEnum cat = CategoryEnum.getByValue(idx);
        setCategotyValue(cat, value);
    }

    public int getCategotyValue(CategoryEnum cat) {
        return catMap.get(cat);
    }

    public int getCategotyValue(int idx) {
        CategoryEnum cat = CategoryEnum.getByValue(idx);
        return catMap.get(cat);
    }

    public String getCategotyFormattedValue(CategoryEnum cat) {
        int value = getCategotyValue(cat);
        return StringUtils.format(value, "#,###");
    }

    public String getCategotyFormattedValue(int idx) {
        int value = getCategotyValue(idx);
        return StringUtils.format(value, "#,###");
    }
}
