package org.blogsite.youngsoft.piggybank.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.blogsite.youngsoft.piggybank.R;
import org.blogsite.youngsoft.piggybank.utils.SmsUtils;

import java.util.ArrayList;

public class DonationAdapter  extends BaseAdapter {
    private Context context;
    private ArrayList<String> nameList;
    private ArrayList<Integer> imageList;
    private static LayoutInflater inflater=null;

    public DonationAdapter(Context context, ArrayList<String> nameList, ArrayList<Integer> imageList) {
        this.context=context;
        init();
        this.nameList = nameList;
        this.imageList = imageList;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);

    }

    private void  init(){
        if(nameList!=null) nameList.clear();
        if(imageList!=null) imageList.clear();
    }

    @Override
    public int getCount() {
        return nameList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder=new Holder();
        View rowView;

        rowView = inflater.inflate(R.layout.donation_gridlayout, null);
        holder.donation_name =(TextView) rowView.findViewById(R.id.donation_name);
        holder.donation_images =(ImageView) rowView.findViewById(R.id.donation_images);

        holder.donation_name.setTypeface(SmsUtils.typefaceNaumGothic);
        holder.donation_name.setText(nameList.get(position));
        holder.donation_images.setImageResource(imageList.get(position));

        return rowView;
    }

    public class Holder
    {
        TextView donation_name;
        ImageView donation_images;
    }
}
