package com.example.rishabh.friday;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class CheckList extends AppCompatActivity {

    MyDBHandler myDB;
    private FloatingActionButton fab;
    private String text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        myDB = new MyDBHandler(this);

        //FUNCTIONALITY FOR LIST ITEM CLICK
        ArrayList<String> itemsList = myDB.getAllCheckListItems();

        final ArrayAdapter<String> itemsAdapter = new CheckListAdapter(this,R.layout.check_list_item_view,itemsList);

        final ListView itemsListView = (ListView)findViewById(R.id.checkListListView);
        itemsListView.setAdapter(itemsAdapter);

        //LONG CLICK LISTENER
        itemsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("hell---------------", "Enterd onlong click---------");
            }
        });
        itemsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("hell---------------", "Enterd onlong click---------");
                String item = String.valueOf(parent.getItemAtPosition(position));
                myDB.deleteCheckListItem(item);
                ArrayList<String> itemslist = myDB.getAllCheckListItems();
                CheckListAdapter adapter = new CheckListAdapter(CheckList.this,R.layout.check_list_item_view,itemslist);
                itemsListView.setAdapter(adapter);

//                itemsListView.
//                itemsAdapter.notifyDataSetChanged();
                return true;

            }
        });

//        itemsListView.setOnItemLongClickListener(
//
//                new AdapterView.OnItemLongClickListener() {
//
//                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
//
//
//
//
//                        return true;
//                    }
//
//                }
//
//        );

        fab = (FloatingActionButton)findViewById(R.id.checkListFab);
        fab.setOnClickListener(

                //Starting up an interface for an event
                new FloatingActionButton.OnClickListener() {

                    public void onClick(View v) {

                        Intent launchAddNewItemIntent = new Intent(CheckList.this, AddNewItem.class);
                        startActivity(launchAddNewItemIntent);

                    }

                }


        );


    }


}
