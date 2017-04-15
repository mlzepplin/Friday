package com.example.rishabh.friday;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import java.util.List;

/**
 * Created by rishabh on 13/04/17.
 */
public class CheckListAdapter extends ArrayAdapter<CheckListItem> {

    private List<CheckListItem> checkList;
    private Context context;

    public CheckListAdapter(Context context, int resource, List<CheckListItem> objects) {
        super(context, resource, objects);
        this.checkList = objects;
        this.context = context;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if(convertView==null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.check_list_item_view, parent, false);
        }
        TextView tv = (TextView) convertView.findViewById(R.id.tv);
        tv.setText(checkList.get(position).getItemString());


        final CheckBox checkBox = (CheckBox) convertView.findViewById(R.id.checkBox);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                                                @Override
                                                public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                                                    MyDBHandler myDBHandler = new MyDBHandler(context);
                                                    if(checkBox.isChecked())
                                                        myDBHandler.updateCheckStatus(new CheckListItem(1,checkList.get(position).getItemString()));
                                                    else
                                                        myDBHandler.updateCheckStatus(new CheckListItem(0,checkList.get(position).getItemString()));
                                                }
                                            }
        );
        if(checkList.get(position).getCheckStatus() == 1)
            checkBox.setChecked(true);
        else
            checkBox.setChecked(false);





        return convertView;
    }
}
