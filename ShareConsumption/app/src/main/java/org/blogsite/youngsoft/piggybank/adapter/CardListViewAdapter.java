package org.blogsite.youngsoft.piggybank.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.blogsite.youngsoft.piggybank.utils.SmsUtils;

import java.util.ArrayList;
import java.util.Collections;

import org.blogsite.youngsoft.piggybank.R;

/**
 * Created by klee on 2018-02-02.
 */

public class CardListViewAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<CardListData> listData = new ArrayList<CardListData>();

    public CardListViewAdapter(Context context){
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

    public void addItem(int icon, String cardName, String amountValue){
        CardListData addInfo = null;
        addInfo = new CardListData();
        addInfo.resId = icon;
        addInfo.cardName = cardName;
        addInfo.amountValue = amountValue;

        listData.add(addInfo);
    }

    public void remove(int position){
        listData.remove(position);
        dataChange();
    }

    public void sort(){
        Collections.sort(listData, CardListData.ALPHA_COMPARATOR);
        dataChange();
    }

    public void dataChange(){
        this.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        CardViewHolder holder;
        if (convertView == null) {
            holder = new CardViewHolder();

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.card_listview_item, null);

            holder.mIcon = (ImageView) convertView.findViewById(R.id.card_image);
            holder.cardName = (TextView) convertView.findViewById(R.id.card_name);
            holder.cardName.setTypeface(SmsUtils.typefaceNaumGothic);
            holder.amountValue = (TextView) convertView.findViewById(R.id.card_value);
            holder.amountValue.setTypeface(SmsUtils.typefaceNaumGothicCoding);

            convertView.setTag(holder);
        }else{
            holder = (CardViewHolder) convertView.getTag();
        }

        CardListData mData = listData.get(position);

        holder.mIcon.setVisibility(View.VISIBLE);
        holder.mIcon.setImageResource(mData.resId);
        holder.cardName.setText(mData.cardName);
        holder.amountValue.setText(mData.amountValue);

        return convertView;
    }
}
