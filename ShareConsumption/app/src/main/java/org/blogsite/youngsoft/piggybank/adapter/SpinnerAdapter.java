package org.blogsite.youngsoft.piggybank.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.blogsite.youngsoft.piggybank.R;
import org.blogsite.youngsoft.piggybank.utils.SmsUtils;

import java.util.List;

public class SpinnerAdapter extends BaseAdapter {
    Context context;
    List<String> data;
    LayoutInflater inflater;

    public SpinnerAdapter(Context context, List<String> data){
        this.context = context;
        this.data = data;
        inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount() {
        if(data!=null) return data.size();
        else return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null) {
            convertView = inflater.inflate(R.layout.spinner_spinner_normal, parent, false);
        }

        if(data!=null){
            //데이터세팅
            String text = data.get(position);
            TextView textView = ((TextView)convertView.findViewById(R.id.spinnerText));
            textView.setTypeface(SmsUtils.typefaceNaumGothic);
            textView.setText(text);
        }

        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        if(convertView==null){
            convertView = inflater.inflate(R.layout.spinner_spinner_normal, parent, false);
        }

        //데이터세팅
        String text = data.get(position);
        TextView textView = ((TextView)convertView.findViewById(R.id.spinnerText));
        textView.setTypeface(SmsUtils.typefaceNaumGothic);
        textView.setText(text);
        //((TextView)convertView.findViewById(R.id.spinnerText)).setText(text);

        return convertView;
    }

    @Override
    public Object getItem(int position) {
        return data.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
