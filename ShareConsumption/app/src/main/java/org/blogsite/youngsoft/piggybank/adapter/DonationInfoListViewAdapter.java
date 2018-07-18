package org.blogsite.youngsoft.piggybank.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import org.blogsite.youngsoft.piggybank.R;
import org.blogsite.youngsoft.piggybank.setting.DonationInfo;
import org.blogsite.youngsoft.piggybank.utils.CardEnum;
import org.blogsite.youngsoft.piggybank.utils.CategoryEnum;
import org.blogsite.youngsoft.piggybank.utils.SmsUtils;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by klee on 2018-02-02.
 */

public class DonationInfoListViewAdapter extends BaseAdapter {
    private Context context;
    private static final NumberFormat PRICE_FORMATTER = NumberFormat.getNumberInstance();
    private ArrayList<DonationInfoListData> listData = new ArrayList<DonationInfoListData>();

    public DonationInfoListViewAdapter(Context context){
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
        DonationInfoListData addInfo = null;
        CardEnum card = CardEnum.getByName(info.getCard());
        int cardResId = -1;
        if(card.equals(CardEnum.KB_CARD)) {
            cardResId = R.drawable.ic_kbcard;
        }else if(card.equals(CardEnum.KB_CHECKCARD)){
            cardResId = R.drawable.ic_kbcheck;
        }else if(card.equals(CardEnum.SHINHAN_CARD) || card.equals(CardEnum.SHINHAN_CHECKCARD) || card.equals(CardEnum.SHINHAN_CORPCARD)){
            cardResId = R.drawable.ic_shinhan;
        }else if(card.equals(CardEnum.NH_CARD) || card.equals(CardEnum.NH_CHECKCARD) || card.equals(CardEnum.NH_CORPCARD) || card.equals(CardEnum.NH_WELFARECARD)) {
            cardResId = R.drawable.ic_nhcard;
        }else if(card.equals(CardEnum.SAMSUNG_CARD)){
            cardResId = R.drawable.ic_samsung;
        }else if(card.equals(CardEnum.HYUNDAE_CARD)){
            cardResId = R.drawable.ic_hyundai;
        }else{
            cardResId = R.drawable.ic_unknown;
        }

        String categoryName = info.getCategory();
        CategoryEnum category = CategoryEnum.getByName(categoryName);
        int categorydResId = -1;
        if(category.equals(CategoryEnum.Communication)){
            categorydResId = R.drawable.communication;
        }else if(category.equals(CategoryEnum.Culture)){
            categorydResId = R.drawable.culture;
        }else if(category.equals(CategoryEnum.Dues)){
            categorydResId = R.drawable.dues;
        }else if(category.equals(CategoryEnum.Meal)){
            categorydResId = R.drawable.meal;
        }else if(category.equals(CategoryEnum.Medical)){
            categorydResId = R.drawable.medical;
        }else if(category.equals(CategoryEnum.Shopping)){
            categorydResId = R.drawable.shopping;
        }else if(category.equals(CategoryEnum.Traffic)){
            categorydResId = R.drawable.traffic;
        }else{
            categorydResId = R.drawable.unclassified;
        }

        addInfo = new DonationInfoListData();
        addInfo.cardResId = cardResId;
        addInfo.cardName = info.getCard();
        addInfo.categorydResId = categorydResId;
        addInfo.categoryName = categoryName;
        addInfo.percent = info.getPercent();
        addInfo.threshold = info.getThreshold();
        addInfo.overall = info.isThresholdOverall();

        listData.add(addInfo);
    }

    public void remove(int position){
        listData.remove(position);
        dataChange();
    }

    public void sort(){
        Collections.sort(listData, DonationInfoListData.ALPHA_COMPARATOR);
        dataChange();
    }

    public void dataChange(){
        this.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        DonationInfoListViewHolder holder;
        if (convertView == null) {
            holder = new DonationInfoListViewHolder();

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.donation_listview_item, null);

            holder.cardIcon = (ImageView) convertView.findViewById(R.id.card_image);
            holder.cardName = (TextView) convertView.findViewById(R.id.card_name);
            holder.cardName.setTypeface(SmsUtils.typefaceNaumGothic);
            //holder.cardName.setTextColor(Color.WHITE);

            holder.catagoryName = (TextView)convertView.findViewById(R.id.category_name);
            holder.catagoryName.setTypeface(SmsUtils.typefaceNaumGothic);
            //holder.catagoryName.setTextColor(Color.WHITE);

            holder.theshold = (TextView)convertView.findViewById(R.id.card_value);
            holder.theshold.setTypeface(SmsUtils.typefaceNaumGothicCoding);
            //holder.theshold.setTextColor(Color.WHITE);

            convertView.setTag(holder);
        }else{
            holder = (DonationInfoListViewHolder) convertView.getTag();
        }

        DonationInfoListData mData = listData.get(position);

        holder.cardIcon.setVisibility(View.VISIBLE);
        holder.cardIcon.setImageResource(mData.cardResId);
        holder.cardName.setText(mData.cardName);
        holder.catagoryName.setText(mData.categoryName);
        holder.theshold.setText(PRICE_FORMATTER.format(mData.threshold) + SmsUtils.getResource(R.string.won));

        return convertView;
    }
}
