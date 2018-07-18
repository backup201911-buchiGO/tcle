package org.blogsite.youngsoft.piggybank.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.blogsite.youngsoft.piggybank.R;
import org.blogsite.youngsoft.piggybank.setting.DonationInfo;
import org.blogsite.youngsoft.piggybank.utils.CardEnum;
import org.blogsite.youngsoft.piggybank.utils.SmsUtils;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by klee on 2018-02-02.
 */

public class DonationListViewAdapter extends BaseAdapter {
    private Context context;
    private static final NumberFormat PRICE_FORMATTER = NumberFormat.getNumberInstance();
    private ArrayList<DonationListData> listData = new ArrayList<DonationListData>();

    public DonationListViewAdapter(Context context){
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

    public void addItem(DonationInfo info){
        DonationListData addInfo = null;
        CardEnum card = CardEnum.getByName(info.getCard());
        int resId = -1;
        if(card.equals(CardEnum.KB_CARD)) {
            resId = R.drawable.ic_kbcard;
        }else if(card.equals(CardEnum.KB_CHECKCARD)){
            resId = R.drawable.ic_kbcheck;
        }else if(card.equals(CardEnum.SHINHAN_CARD) || card.equals(CardEnum.SHINHAN_CHECKCARD) || card.equals(CardEnum.SHINHAN_CORPCARD)){
            resId = R.drawable.ic_shinhan;
        }else if(card.equals(CardEnum.NH_CARD) || card.equals(CardEnum.NH_CHECKCARD) || card.equals(CardEnum.NH_CORPCARD) || card.equals(CardEnum.NH_WELFARECARD)) {
            resId = R.drawable.ic_nhcard;
        }else if(card.equals(CardEnum.SAMSUNG_CARD)){
            resId = R.drawable.ic_samsung;
        }else if(card.equals(CardEnum.HYUNDAE_CARD)){
            resId = R.drawable.ic_hyundai;
        }else{
            resId = R.drawable.ic_unknown;
        }
        addInfo = new DonationListData();
        addInfo.resId = resId;
        addInfo.cardName = info.getCard();
        addInfo.categoryName = info.getCategory();
        int percent = info.getPercent();
        addInfo.amountValue = PRICE_FORMATTER.format(info.getAmount()) + "(" + PRICE_FORMATTER.format(info.getAmount() * percent / 100) + ")원";

        listData.add(addInfo);
    }

    public void remove(int position){
        listData.remove(position);
        dataChange();
    }

    public void sort(){
        Collections.sort(listData, DonationListData.ALPHA_COMPARATOR);
        dataChange();
    }

    public void dataChange(){
        this.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DonationViewHolder holder;
        if (convertView == null) {
            holder = new DonationViewHolder();

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.donation_listview_item, null);

            holder.mIcon = (ImageView) convertView.findViewById(R.id.card_image);
            holder.cardName = (TextView) convertView.findViewById(R.id.card_name);
            holder.cardName.setTypeface(SmsUtils.typefaceNaumGothic);
            holder.catagoryName = (TextView)convertView.findViewById(R.id.category_name);
            holder.catagoryName.setTypeface(SmsUtils.typefaceNaumGothic);
            holder.amountValue = (TextView) convertView.findViewById(R.id.card_value);
            holder.amountValue.setTypeface(SmsUtils.typefaceNaumGothicCoding);

            convertView.setTag(holder);
        }else{
            holder = (DonationViewHolder) convertView.getTag();
        }

        DonationListData mData = listData.get(position);

        holder.mIcon.setVisibility(View.VISIBLE);
        holder.mIcon.setImageResource(mData.resId);
        holder.cardName.setText(mData.cardName);
        holder.catagoryName.setText(mData.categoryName);
        holder.amountValue.setText(mData.amountValue);

        return convertView;
    }
}
