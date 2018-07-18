package org.blogsite.youngsoft.piggybank.analyzer;

import org.blogsite.youngsoft.piggybank.utils.CardEnum;
import org.blogsite.youngsoft.piggybank.utils.CategoryEnum;
import org.blogsite.youngsoft.piggybank.utils.StringUtils;

/**
 * Created by klee on 2018-01-29.
 */
public class Card {

    private final CardEnum cardName;
    private final Category category;
    private int totalAmount;

    public Card(CardEnum cardName) {
        this.cardName = cardName;
        this.category = new Category();
        totalAmount = 0;
    }

    public CardEnum getCardName() {
        return cardName;
    }

    public void addCategory(CategoryEnum cat, int value) {
        totalAmount += value;
        category.setCategotyValue(cat, value);
    }

    public void addCategory(int idx, int value) {
        totalAmount += value;
        category.setCategotyValue(idx, value);
    }

    public Category getCategories() {
        return category;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public String getFormattedTotalAmount() {
        return StringUtils.format(totalAmount, "#,###");
    }

}
