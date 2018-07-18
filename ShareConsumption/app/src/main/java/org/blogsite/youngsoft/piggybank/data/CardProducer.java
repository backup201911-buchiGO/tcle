package org.blogsite.youngsoft.piggybank.data;

import org.blogsite.youngsoft.piggybank.utils.CardEnum;

/**
 * Created by klee on 2018-01-06.
 */

/**
 * 카드 Procedure
 */
public class CardProducer {
    private final String name;
    private final CardEnum cardName;

    public CardProducer(final CardEnum cardName) {
        this.cardName = cardName;
        this.name = cardName.getName();
    }

    public CardEnum getCardName(){
        return cardName;
    }

    public String getName() {
        return name;
    }
}
