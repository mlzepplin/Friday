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
        View view = null;
        LayoutInflater vi = (LayoutInflater)context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);
        view = vi.inflate(R.layout.check_list_item_view,null);
        TextView tv = (TextView)view.findViewById(R.id.tv);
        tv.setText(checkList.get(position));
        return view;
    }
}
