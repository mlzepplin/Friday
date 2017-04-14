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

        ListView itemsListView = (ListView)findViewById(R.id.checkListListView);
        itemsListView.setAdapter(itemsAdapter);

        //LONG CLICK LISTENER

        itemsListView.setOnItemLongClickListener(

                new AdapterView.OnItemLongClickListener() {

                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                        String item = String.valueOf(parent.getItemAtPosition(position));
                        Log.i("hell---------------", "Enterd onlong click---------");
                        myDB.deleteCheckListItem(item);
                        itemsAdapter.notifyDataSetChanged();
                        return true;
                    }

                }

        );

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


     /*
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
      */
    }






}
