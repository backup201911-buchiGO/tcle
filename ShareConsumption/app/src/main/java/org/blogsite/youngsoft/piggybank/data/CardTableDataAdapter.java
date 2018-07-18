package org.blogsite.youngsoft.piggybank.data;

/**
 * Created by klee on 2018-01-08.
 */

/**
 * 카드 테이블 뷰어를 위한 어뎁터
 */

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import org.blogsite.youngsoft.piggybank.utils.CardEnum;
import org.blogsite.youngsoft.piggybank.utils.CategoryEnum;
import org.blogsite.youngsoft.piggybank.utils.SmsUtils;
import org.blogsite.youngsoft.tableview.TableView;
import org.blogsite.youngsoft.tableview.toolkit.LongPressAwareTableDataAdapter;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.blogsite.youngsoft.piggybank.R;

public class CardTableDataAdapter extends LongPressAwareTableDataAdapter<CardData> {
    private static final int TEXT_SIZE = 10;
    private static final NumberFormat PRICE_FORMATTER = NumberFormat.getNumberInstance();
    private Typeface myTypeface = null;

    public CardTableDataAdapter(final Context context, final List<CardData> data, final TableView<CardData> tableView) {
        super(context, data, tableView);
    }

    @Override
    public View getDefaultCellView(int rowIndex, int columnIndex, ViewGroup parentView) {
        final CardData cardData = getRowData(rowIndex);
        View renderedView = null;

        /**
         *  컬럼      이름
         *  -----------------------------
         *  0           카드이름
         *  1           승인/취소
         *  2           사용날짜
         *  3           사용금액
         *  4           분류
         */
        switch (columnIndex) {
            case 0:
                // 카드이름
                renderedView = renderCardName(cardData, parentView);
                break;
            case 1:
                // 카드 승인/취소
                renderedView = renderApproval(cardData);
                break;
            case 2:
                // 카드 사용일시
                renderedView = renderUseDate(cardData);
                break;
            case 3:
                //카드 사용금액
                renderedView = renderAmound(cardData);
                break;
            case 4:
                // 카드 사용 분류
                // 분류를 이미지로
                //renderedView = renderCategoryLogo(cardData, parentView);
                // 분류 이름으로
                renderedView = renderCategoryName(cardData);
                break;
        }
        return renderedView;
    }

    public void setTypeface(Typeface myTypeface){
        this.myTypeface = myTypeface;
    }

    @Override
    public View getLongPressCellView(int rowIndex, int columnIndex, ViewGroup parentView) {
        final CardData cardData = getRowData(rowIndex);
        View renderedView = getDefaultCellView(rowIndex, columnIndex, parentView);;
/**********************************
        View renderedView = null;

        switch (columnIndex) {
            case 1:
                renderedView = renderEditableCatName(cardData);
                break;
            default:
                renderedView = getDefaultCellView(rowIndex, columnIndex, parentView);
        }
 **********************************/
        return renderedView;
    }

    /**
     * 카드이름 렌더
     * @param cardData
     * @return
     */
    private View renderCardName(final CardData cardData, final ViewGroup parentView){
        CardEnum card = cardData.getProducer().getCardName();
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

        return renderCardLogo(resId, parentView);

        //return renderString(cardData.getName());
    }

    private View renderCardLogo(final int resId, final ViewGroup parentView) {
        final View view = getLayoutInflater().inflate(R.layout.table_cell_image, parentView, false);
        final ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
        imageView.setImageResource(resId);

        return view;
    }

    /**
     * 카드 승인/취소 렌더
     * @param cardData
     * @return
     */
    private View renderApproval(final CardData cardData){
        String s = cardData.isApproval() ? SmsUtils.getResource(R.string.accept) : SmsUtils.getResource(R.string.reject);

        final TextView textView = new TextView(getContext());
        textView.setText(s);
        if(!cardData.isApproval()){
            textView.setTextColor(ContextCompat.getColor(getContext(), R.color.strikethru));
        }
        textView.setPadding(5, 5, 5, 5);
        textView.setTextSize(TEXT_SIZE);
        textView.setTypeface(SmsUtils.typefaceNaumGothic);
        return textView;
    }

    /**
     * 카드 사용일자 렌더
     * @param cardData
     * @return
     */
    private View renderUseDate(final CardData cardData){
        final  long timestamp = cardData.getTimestamp();
        DateFormat df = new SimpleDateFormat("yy.MM.dd.EEEE");
        String s = df.format(new Date(timestamp));
        View v = renderString(s);
        ((TextView)v).setTypeface(SmsUtils.typefaceNaumGothic);
        return v;
    }

    /**
     * 카드 사용금액 렌더
     * @param cardData
     * @return
     */
    private View renderAmound(final CardData cardData) {
        final String amountString = PRICE_FORMATTER.format(cardData.getAmount());

        final TextView textView = new TextView(getContext());
        textView.setText(amountString);
        textView.setPadding(5, 5, 5, 5);
        textView.setGravity(Gravity.RIGHT);
        if(!cardData.isApproval()){
            textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            textView.setTextColor(ContextCompat.getColor(getContext(), R.color.strikethru));
        }else {
            if (cardData.getAmount() >= 100000) {
                textView.setTextColor(ContextCompat.getColor(getContext(), R.color.table_price_high));
            }
        }
/*
        if (cardData.getAmount() < 50000) {
            textView.setTextColor(ContextCompat.getColor(getContext(), R.color.table_price_low));
        } else if (cardData.getAmount() > 100000) {
            textView.setTextColor(ContextCompat.getColor(getContext(), R.color.table_price_high));
        }
*/
        textView.setTextSize(TEXT_SIZE + 2);
        textView.setTypeface(SmsUtils.typefaceNaumGothicCoding);
        return textView;
    }

    /**
     * 카드 사용내역 분류 이름 렌더
     * @param cardData
     * @return
     */
    private View renderCategoryName(final CardData cardData) {
        return renderCategoryString(cardData.getCategory());
    }

    private View renderEditableCatName(final CardData cardData) {
        final EditText editText = new EditText(getContext());
        editText.setText(cardData.getName());
        editText.setPadding(5, 5, 5, 5);
        editText.setTextSize(TEXT_SIZE);
        editText.setSingleLine();
        editText.addTextChangedListener(new CarNameUpdater(cardData));
        return editText;
    }

    /**
     * 텍스트 렌더
     * @param value
     * @return
     */
    private View renderString(final String value) {
        final TextView textView = new TextView(getContext());
        textView.setText(value);
        textView.setPadding(5, 5, 5, 5);
        textView.setTextSize(TEXT_SIZE);
        return textView;
    }

    private View renderCategoryString(final CategoryEnum category) {
        final TextView textView = new TextView(getContext());
        textView.setTypeface(myTypeface);
        textView.setPadding(5, 5, 5, 5);
        CharSequence cs = category.getName();
        textView.setHint(cs);
        textView.setTextSize(TEXT_SIZE + 4);
        textView.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
        switch(category.getValue()){
            case -1:
                textView.setTextColor(Color.GRAY);
                textView.setText("\uF3CA"); // 미분류
                break;
            case 0:
                textView.setTextColor(Color.RED);
                textView.setText("\uF595"); // 식사
                break;
            case 1:
                textView.setTextColor(Color.parseColor("#ff8c00"));
                textView.setText("\uF1F2"); // 문화
                break;
            case 2:
                textView.setTextColor(Color.parseColor("#FF2E7D32"));
                textView.setText("\uF12E"); // 의료
                break;
            case 3:
                textView.setTextColor(Color.parseColor("#33FFFF"));
                textView.setText("\uF4E5"); // 통신
                break;
            case 4:
                textView.setTextColor(Color.parseColor("#0000ff"));
                textView.setText("\uF5CD"); // 교통
                break;
            case 5:
                textView.setTextColor(Color.parseColor("#4b0082"));
                textView.setText("\uF167"); // 공과금
                break;
            case 6:
                textView.setTextColor(Color.parseColor("#800080"));
                textView.setText("\uF448"); // 생활
                break;
            default:
                textView.setTextColor(Color.GRAY);
                textView.setText("\uF3CA"); // 미분류
                break;
        }
        return textView;
    }

    /**
     * 수정 가능한 컬럼을 위한 내부 클래스
     */
    private static class CarNameUpdater implements TextWatcher {
        private CardData cardDataToUpdate;

        public CarNameUpdater(CardData carToUpdate) {
            this.cardDataToUpdate = carToUpdate;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            // no used
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // not used
        }

        @Override
        public void afterTextChanged(Editable s) {
            cardDataToUpdate.setName(s.toString());
        }
    }
}
