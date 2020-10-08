package com.oculus.sample;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by Prashant on 24-03-2017.
 */

public class Custom extends ArrayAdapter<String> {

    String[] url;
    String[] name;
    Context context ;

    public Custom (Context context, String[] url , String[] name){

        super(context, R.layout.customadapter,url);

        this.context = context;
        this.url= url;
        this.name= name;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        convertView =layoutInflater.inflate(R.layout.row_layout,null);

        TextView textView = (TextView)convertView.findViewById(R.id.item);
      //  ImageView imageView =(ImageView)convertView.findViewById(R.id.imageView);
        textView.setText(name[position]);
       // imageView.setBackgroundResource(pics[position]);
        return convertView;
    }
}
