package com.oculus.sample;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class ContactsAdapter extends ArrayAdapter {

    List list = new ArrayList();

    public ContactsAdapter(Context context, int resource) {
        super(context, resource);
    }


    public void add(Contacts object) {
        super.add(object);
        list.add(object);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return list.get(position);
    }



    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
            View row;
            row = convertView;
        ContactHolder contactHolder;
        if(row==null){
            LayoutInflater layoutInflater=(LayoutInflater)this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                row = layoutInflater.inflate(R.layout.row_layout,parent,false);
                        contactHolder = new ContactHolder();
            contactHolder.Item=(TextView)row.findViewById(R.id.item);
                row.setTag(contactHolder);
        }else{

           contactHolder = (ContactHolder)row.getTag();
        }

        Contacts contacts =(Contacts)this.getItem(position);
        contactHolder.Item.setText(contacts.getItem());
        return row;
    }



    static class ContactHolder
    {
        TextView Item;

    }
}
