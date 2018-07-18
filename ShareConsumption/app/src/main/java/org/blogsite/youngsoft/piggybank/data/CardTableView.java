package org.blogsite.youngsoft.piggybank.data;

/**
 * Created by klee on 2018-01-08.
 */

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.TextView;

import org.blogsite.youngsoft.piggybank.utils.SmsUtils;
import org.blogsite.youngsoft.tableview.SortableTableView;
import org.blogsite.youngsoft.tableview.model.TableColumnWeightModel;
import org.blogsite.youngsoft.tableview.toolkit.SimpleTableHeaderAdapter;
import org.blogsite.youngsoft.tableview.toolkit.SortStateViewProviders;
import org.blogsite.youngsoft.tableview.toolkit.TableDataRowBackgroundProviders;

import org.blogsite.youngsoft.piggybank.R;


public class CardTableView extends SortableTableView<CardData> {
    public CardTableView(final Context context) {
        this(context, null);
    }

    public CardTableView(final Context context, final AttributeSet attributes) {
        this(context, attributes, android.R.attr.listViewStyle);
    }

    public CardTableView(final Context context, final AttributeSet attributes, final int styleAttributes) {
        super(context, attributes, styleAttributes);

        final SimpleTableHeaderAdapter simpleTableHeaderAdapter = new SimpleTableHeaderAdapter(context,
                R.string.cardName, R.string.approval, R.string.useDate, R.string.amount, R.string.category);

        //simpleTableHeaderAdapter.setTextColor(ContextCompat.getColor(context, R.color.title_color));
        ((TextView)simpleTableHeaderAdapter.getHeaderView(0, this)).setTypeface(SmsUtils.typefaceNaumGothic);
        ((TextView)simpleTableHeaderAdapter.getHeaderView(1, this)).setTypeface(SmsUtils.typefaceNaumGothic);
        ((TextView)simpleTableHeaderAdapter.getHeaderView(2, this)).setTypeface(SmsUtils.typefaceNaumGothic);
        ((TextView)simpleTableHeaderAdapter.getHeaderView(3, this)).setTypeface(SmsUtils.typefaceNaumGothic);
        simpleTableHeaderAdapter.setTextSize(11);
        simpleTableHeaderAdapter.setPaddingBottom(5);
        simpleTableHeaderAdapter.setPaddingTop(5);
        simpleTableHeaderAdapter.setPaddingLeft(5);
        simpleTableHeaderAdapter.setPaddingRight(5);

        setHeaderAdapter(simpleTableHeaderAdapter);

        final int rowColorEven = ContextCompat.getColor(context, R.color.table_data_row_even);
        final int rowColorOdd = ContextCompat.getColor(context, R.color.table_data_row_odd);
        setDataRowBackgroundProvider(TableDataRowBackgroundProviders.alternatingRowColors(rowColorEven, rowColorOdd));
        setHeaderSortStateViewProvider(SortStateViewProviders.brightArrows());

        final TableColumnWeightModel tableColumnWeightModel = new TableColumnWeightModel(5);
        tableColumnWeightModel.setColumnWeight(0, 3);   //카드
        tableColumnWeightModel.setColumnWeight(1, 2);   //승인/취소
        tableColumnWeightModel.setColumnWeight(2, 5);   //날짜
        tableColumnWeightModel.setColumnWeight(3, 4);   //금액
        tableColumnWeightModel.setColumnWeight(4, 2);   //종류

        setColumnModel(tableColumnWeightModel);

        //setColumnComparator(2, CardComparators.getCardUseDateComparator(), true);
        //setColumnComparator(3, CardComparators.getCardAmountComparator(), true);

/*
        setColumnComparator(0, CardComparators., false);
        setColumnComparator(1, CardComparators.getCarNameComparator(), false);
        setColumnComparator(2, CardComparators.getCarPowerComparator(), false);
        setColumnComparator(3, CardComparators.getCarPriceComparator(), false);
        setColumnComparator(4, CardComparators.getCarPriceComparator(), false);
*/
    }
}
