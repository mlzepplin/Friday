package com.example.rishabh.friday;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class CheckList extends AppCompatActivity {


    //speech
    private ImageButton speak_btn;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private int counter=0;
    private String flag="INACTIVE";
    private String from= "button";
    //speech
    ListView itemsListView;
    MyDBHandler myDB;
    private FloatingActionButton fab;
    private String text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Declaring speech

        speak_btn=(ImageButton) findViewById(R.id.speak_btn);
        speak_btn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                promptSpeechInput();
            }
        });

    //here

        myDB = new MyDBHandler(this);

        //FUNCTIONALITY FOR LIST ITEM CLICK
        ArrayList<CheckListItem> itemsList = myDB.getAllCheckListItems();

        final ArrayAdapter<CheckListItem> itemsAdapter = new CheckListAdapter(this,R.layout.check_list_item_view,itemsList);

        itemsListView = (ListView)findViewById(R.id.checkListListView);
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
                        launchAddNewItemIntent.putExtra("from",from);
                        startActivity(launchAddNewItemIntent);

                    }

                }


        );


    }

    //speech
    private void promptSpeechInput() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Speak!!");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
            intent.putExtra("counter", counter);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "speech_not_supported",
                    Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        System.out.println("111111");
        if(null != data) {
            switch (requestCode) {
                case REQ_CODE_SPEECH_INPUT: {
                    System.out.println("222222 "+resultCode+" "+data.toString());
                    if (resultCode == RESULT_OK && null != data) {
                        System.out.println("222222 "+resultCode+" "+data.toString());
                        ArrayList<String> result = data
                                .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                        float[] confidence = data
                                .getFloatArrayExtra(RecognizerIntent.EXTRA_CONFIDENCE_SCORES);
                        try {

                            Toast.makeText(getApplicationContext(),
                                    result.get(0),
                                    Toast.LENGTH_SHORT).show();
                            findCommand(result.get(0));
                        }
                        catch(NullPointerException ne) {
                            Toast.makeText(getApplicationContext(),
                                    "No detection !!!",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                    flag = "INACTIVE";
                    break;
                }
            }
        }
        System.out.println("333333");
    }

    public void findCommand(String com) {
        if (com.matches("(.*)Add(.*)") || com.matches("(.*)add(.*)") || com.matches("(.*)ADD(.*)")) {
            from="speech";
            fab.callOnClick();
        }
        else if (com.matches("(.*)Check(.*)") || com.matches("(.*)check(.*)") || com.matches("(.*)CHECK(.*)")) {
            String[] newString=com.split("check");
            myDB.updateCheckStatus(new CheckListItem(0,newString[1]));
            Intent refresh=new Intent(this, CheckList.class);
            startActivity(refresh);
            finish();
            //TextView s
           // myDB.checkIt(newString[1]);
           // itemsListView.ca
            //checkBox.onCheckedChanged();

        }
        else if (com.matches("(.*)Uncheck(.*)") || com.matches("(.*)uncheck(.*)") || com.matches("(.*)UNCHECK(.*)")) {
            String[] newString = com.split("uncheck");
            myDB.updateCheckStatus(new CheckListItem(1, newString[1]));
            Intent refresh = new Intent(this, CheckList.class);
            startActivity(refresh);
            finish();
        }
        /*else if (com.matches("delete checked tasks") || com.matches("Delete checked tasks") || com.matches("DELETE CHECKED TASKS") || com.matches("Delete Checked Tasks")) {
            myDB.deleteOnlyChecked();
            //refresh
        }
        else if (com.matches("Delete all") || com.matches("delete all") || com.matches("Delete All") || com.matches("DELETE ALL")) {
            //delete all
        }*/

        else if (com.matches("(.*)delete(.*)") || com.matches("(.*)Delete(.*)") || com.matches("(.*)DELETE(.*)")) {
            String[] newString=com.split("delete");
            myDB.deleteCheckListItem(newString[1]);
            Intent refresh=new Intent(this, CheckList.class);
            startActivity(refresh);
            finish();
        }


    }


}
