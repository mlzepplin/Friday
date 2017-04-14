package com.example.rishabh.friday;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by rishabh on 13/04/17.
 */
public class CheckListAdapter extends ArrayAdapter<String> {

    private List<String> checkList;
    private Context context;

    public CheckListAdapter(Context context, int resource, List<String> objects) {
        super(context, resource, objects);
        this.checkList = objects;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if(convertView==null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.check_list_item_view, parent, false);
        }
        TextView tv = (TextView) convertView.findViewById(R.id.tv);
        tv.setText(checkList.get(position));
        return convertView;
    }
}
