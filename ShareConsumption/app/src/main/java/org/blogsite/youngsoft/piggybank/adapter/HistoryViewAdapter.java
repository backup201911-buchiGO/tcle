package org.blogsite.youngsoft.piggybank.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.blogsite.youngsoft.piggybank.R;
import org.blogsite.youngsoft.piggybank.setting.DonationHistory;
import org.blogsite.youngsoft.piggybank.setting.DonationInfo;
import org.blogsite.youngsoft.piggybank.utils.CardEnum;
import org.blogsite.youngsoft.piggybank.utils.SmsUtils;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by klee on 2018-02-02.
 */

public class HistoryViewAdapter extends BaseAdapter {
    private Context context;
    private static final NumberFormat PRICE_FORMATTER = NumberFormat.getNumberInstance();
    private ArrayList<HistoryListData> listData = new ArrayList<HistoryListData>();

    public HistoryViewAdapter(Context context){
        super();
        this.context = context;
    }

    @Override
    public int getCount() {
        return listData.size();
    }

    @Override
    public Object getItem(int position) {
        return listData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void addItem(DonationHistory history){
        HistoryListData data = new HistoryListData();
        data.year = history.getYear();
        data.month = history.getMonth();
        data.day = history.getDay();
        data.date = String.valueOf(data.year) + SmsUtils.getResource(R.string.year) + " " + String.valueOf(data.month)
                + SmsUtils.getResource(R.string.month) + " " + String.valueOf(data.day) + SmsUtils.getResource(R.string.day);
        data.amountValue = PRICE_FORMATTER.format(history.getAmount()) + SmsUtils.getResource(R.string.won);
        data.donationName = history.getDonationName();
        data.account = history.getAccount();
        data.bankname = history.getBankname();
        data.home = history.getHome();
        data.tel = history.getTel();
        data.address = history.getAddress();
        data.category = history.getCategory();

        listData.add(data);
    }

    public void remove(int position){
        listData.remove(position);
        dataChange();
    }

    public void sort(){
        Collections.sort(listData, HistoryListData.ALPHA_COMPARATOR);
        dataChange();
    }

    public void dataChange(){
        this.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        HistoryHolder holder;
        if (convertView == null) {
            holder = new HistoryHolder();

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.history_listview_item, null);

            holder.date = (TextView) convertView.findViewById(R.id.date);
            holder.date.setTypeface(SmsUtils.typefaceNaumGothicCoding);
            holder.donationName = (TextView)convertView.findViewById(R.id.donationName);
            holder.donationName.setTypeface(SmsUtils.typefaceNaumGothicCoding);
            holder.amountValue = (TextView) convertView.findViewById(R.id.amountValue);
            holder.amountValue.setTypeface(SmsUtils.typefaceNaumGothicCoding);

            convertView.setTag(holder);
        }else{
            holder = (HistoryHolder) convertView.getTag();
        }

        HistoryListData mData = listData.get(position);

        holder.date.setText(mData.date);
        holder.donationName.setText(mData.donationName);
        holder.amountValue.setText(mData.amountValue);

        return convertView;
    }
}
