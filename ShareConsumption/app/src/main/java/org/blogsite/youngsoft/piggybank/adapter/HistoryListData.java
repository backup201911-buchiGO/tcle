package org.blogsite.youngsoft.piggybank.adapter;

import java.text.Collator;
import java.util.Comparator;

/**
 * Created by klee on 2018-02-02.
 */

public class HistoryListData {
    public int year;
    public int month;
    public int day;
    public String date;
    public String amountValue;
    public String donationName;
    public String account;
    public String bankname;
    public String home;
    public String tel;
    public String address;
    public String category;

    /**
     * 알파벳 이름으로 정렬
     */
    public static final Comparator<HistoryListData> ALPHA_COMPARATOR = new Comparator<HistoryListData>() {
        private final Collator sCollator = Collator.getInstance();

        @Override
        public int compare(HistoryListData mListDate_1, HistoryListData mListDate_2) {
            return sCollator.compare(mListDate_1.date, mListDate_2.date);
        }
    };
}
