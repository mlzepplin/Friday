package com.example.rishabh.friday;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
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
    private TextToSpeech t1;


    //sidebar changes
    private ArrayAdapter<String> mAdapter;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mActivityTitle;
    //sidebar changes end

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
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //sidebar changes
        mDrawerList = (ListView)findViewById(R.id.navList3);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();
        addDrawerItems();


        setupDrawer();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        //sidebar changes end



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

                //(CheckListItem)parent.getItemAtPosition(position);
                CheckListItem checkListItem = (CheckListItem)view.getTag();
                myDB.deleteCheckListItem(checkListItem.getItemString());
                ArrayList<CheckListItem> itemslist = myDB.getAllCheckListItems();
                CheckListAdapter adapter = new CheckListAdapter(CheckList.this,R.layout.check_list_item_view,itemslist);
                itemsListView.setAdapter(adapter);

//                itemsListView.
                myDB.close();
//                itemsAdapter.notifyDataSetChanged();
                return true;

            }
        });

        fab = (FloatingActionButton)findViewById(R.id.checkListFab);
        from="button";
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

    //sidebar changes
    private void addDrawerItems() {
        String[] osArray = { "Home", "Notes", "Reminders", "Exit" };
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, osArray);
        mDrawerList.setAdapter(mAdapter);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(position==0) {
                    Intent intent = new Intent(CheckList.this, ToActivity.class);
                    intent.putExtra("counter",counter);
                    startActivity(intent);

                }
                else if(position==1){
                    Intent intent = new Intent(CheckList.this, MainActivity.class);
                    intent.putExtra("counter",counter);
                    startActivity(intent);
                    //addReminderInCalendar();
                }
                else if(position==2){

                    Intent intent = new Intent(CheckList.this, ReminderActivity.class);
                    startActivity(intent);

                }
                else{
                    Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                    homeIntent.addCategory( Intent.CATEGORY_HOME );
                    homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(homeIntent);

                }


                //Toast.makeText(MainActivity.this, "Note yet done!", Toast.LENGTH_SHORT).show();
            }
        });
    }



    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {


           /*
            @SuppressLint("NewApi")
            public void onDrawerSlide(View drawerView, float slideOffset)
            {
                super.onDrawerSlide(drawerView, slideOffset);
                float moveFactor = (mDrawerList.getWidth() * slideOffset);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
                {
                    frame.setTranslationX(moveFactor);
                }
                else
                {
                    TranslateAnimation anim = new TranslateAnimation(lastTranslate, moveFactor, 0.0f, 0.0f);
                    anim.setDuration(0);
                    anim.setFillAfter(true);
                    frame.startAnimation(anim);

                    lastTranslate = moveFactor;
                }


            }

            */
            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("Menu");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                mDrawerToggle.syncState();
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mActivityTitle);

                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
                mDrawerToggle.syncState();
            }
        };

        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.addDrawerListener(mDrawerToggle);

    }
    //sidebar changes ends




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_home_screen, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }


        //sidebar changes
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        //sidebar changes ends


        return super.onOptionsItemSelected(item);



    }



    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
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
        boolean present;

        if (com.matches("(.*)Add(.*)") || com.matches("(.*)add(.*)") || com.matches("(.*)ADD(.*)") || com.matches("(.*)Item(.*)") || com.matches("(.*)item(.*)") || com.matches("(.*)ITEM(.*)")) {
            from="speech";
            fab.callOnClick();
        }
        //unchecking an element
        else if (com.matches("(.*)Uncheck(.*)") || com.matches("(.*)uncheck(.*)") || com.matches("(.*)UNCHECK(.*)")) {
            String[] newString = com.split("uncheck ");
            if (newString.length<2) {
                final String toSpeak;
                toSpeak="Please name the label you want to uncheck" ;
                //here
                t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                    public void onInit(int status) {
                        if (status != TextToSpeech.ERROR) {
                            t1.setLanguage(Locale.US);

                            t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);

                        }


                    }
                }
                );
            }
            else {
                present = isPresent(newString[1]);
                if (present) {
                    myDB.updateCheckStatus(new CheckListItem(0, newString[1]));
                    Intent refresh = new Intent(this, CheckList.class);
                    startActivity(refresh);
                    finish();
                } else {
                    final String toSpeak;
                    toSpeak = "Your item with label" + newString[1] + "does not exist";
                    //here
                    t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                        public void onInit(int status) {
                            if (status != TextToSpeech.ERROR) {
                                t1.setLanguage(Locale.US);

                                t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);

                            }


                        }
                    }
                    );
                }
            }
        }
        //checking an element
        else if (com.matches("(.*)Check(.*)") || com.matches("(.*)check(.*)") || com.matches("(.*)CHECK(.*)")) {
            String[] newString=com.split("check ");
            if (newString.length<2) {
                final String toSpeak;
                toSpeak="Please name the label you want to check" ;
                //here
                t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                    public void onInit(int status) {
                        if (status != TextToSpeech.ERROR) {
                            t1.setLanguage(Locale.US);

                            t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);

                        }


                    }
                }
                );
            }
            else {
                present = isPresent(newString[1]);
                if (present) {
                    myDB.updateCheckStatus(new CheckListItem(1, newString[1]));
                    Intent refresh = new Intent(this, CheckList.class);
                    startActivity(refresh);
                    finish();
                } else {
                    final String toSpeak;
                    toSpeak = "Your item with label" + newString[1] + "does not exist";
                    //here
                    t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                        public void onInit(int status) {
                            if (status != TextToSpeech.ERROR) {
                                t1.setLanguage(Locale.US);

                                t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);

                            }


                        }
                    }
                    );
                }
            }
        }
        //delete
        else if (com.matches("(.*)delete(.*)") || com.matches("(.*)Delete(.*)") || com.matches("(.*)DELETE(.*)")) {
            String[] newString = com.split("delete ");
            if (newString.length<2) {
                final String toSpeak;
                toSpeak = "Please name the label you want to delete";
                //here
                t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                    public void onInit(int status) {
                        if (status != TextToSpeech.ERROR) {
                            t1.setLanguage(Locale.US);

                            t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);

                        }


                    }
                }
                );
            } else {
                present = isPresent(newString[1]);
                if (present) {
                    myDB.deleteCheckListItem(newString[1]);
                    final String toSpeak;
                    toSpeak = "Your item with label" + newString[1] + "has been deleted";
                    //here
                    t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                        public void onInit(int status) {
                            if (status != TextToSpeech.ERROR) {
                                t1.setLanguage(Locale.US);

                                t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);

                            }


                        }
                    }
                    );
                    //till here
                    Intent refresh = new Intent(this, CheckList.class);
                    startActivity(refresh);
                    finish();
                } else {
                    final String toSpeak;
                    toSpeak = "Your item with label" + newString[1] + "does not exist";
                    //here
                    t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                        public void onInit(int status) {
                            if (status != TextToSpeech.ERROR) {
                                t1.setLanguage(Locale.US);

                                t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);

                            }


                        }
                    }
                    );
                }
            }

        }
        else {
            final String toSpeak;
            toSpeak = "Command not recognized!";
            //here
            t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                public void onInit(int status) {
                    if (status != TextToSpeech.ERROR) {
                        t1.setLanguage(Locale.US);

                        t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);

                    }


                }
            }
            );
        }
    }

    public boolean isPresent(String label) {
        boolean find= myDB.searchCheckListLabel(label);
        System.out.println(find);
        return find;
    }
}
