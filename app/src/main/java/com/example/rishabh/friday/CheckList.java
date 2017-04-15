package com.example.rishabh.friday;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
        ArrayList<CheckListItem> itemsList = myDB.getAllCheckListItems();

        final ArrayAdapter<CheckListItem> itemsAdapter = new CheckListAdapter(this,R.layout.check_list_item_view,itemsList);

        final ListView itemsListView = (ListView)findViewById(R.id.checkListListView);
        itemsListView.setAdapter(itemsAdapter);

        //LONG CLICK LISTENER
        itemsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });
        itemsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                String item = String.valueOf(parent.getItemAtPosition(position));
                myDB.deleteCheckListItem(item);

                ArrayList<CheckListItem> itemslist = myDB.getAllCheckListItems();
                CheckListAdapter adapter = new CheckListAdapter(CheckList.this,R.layout.check_list_item_view,itemslist);
                itemsListView.setAdapter(adapter);

//                itemsListView.
//                itemsAdapter.notifyDataSetChanged();
                return true;

            }
        });



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
