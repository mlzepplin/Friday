package com.example.rishabh.friday;


import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.TimeZone;

public class MainActivity extends AppCompatActivity {

    private ImageButton btnSpeak;
    private Button searchButton;
    private FloatingActionButton fab;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private String flag= "INACTIVE";
    private String from= "button";
    private EditText searchEditText;
    private int counter = 0;
    private String seema = "new";

    private TextToSpeech t1;

    //sidebar changes
    private ArrayAdapter<String> mAdapter;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mActivityTitle;
    //sidebar changes end

    private float lastTranslate = 0.0f;
    private FrameLayout frame;

    MyDBHandler myDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //sidebar changes
        mDrawerList = (ListView)findViewById(R.id.navList);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mActivityTitle = getTitle().toString();
        addDrawerItems();


        setupDrawer();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        //sidebar changes end

//mic changes begin
        //txtSpeechInput = (TextView) findViewById(R.id.txtSpeechInput);

        btnSpeak = (ImageButton) findViewById(R.id.btnSpeak);
        btnSpeak.setOnClickListener(new View.OnClickListener() {


            public void onClick(View v) {
                seema = "new";
                promptSpeechInput("Say command to add/search/edit notes..");
            }
        });
        counter = 0;
        Intent intent = getIntent();
//mic changes end
        counter = intent.getIntExtra("counter",0);
        System.out.println(counter);
        if(counter<1) {

            t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                public void onInit(int status) {
                    if (status != TextToSpeech.ERROR) {
                        t1.setLanguage(Locale.US);

                        t1.speak("Click on the mic button to speak? ", TextToSpeech.QUEUE_FLUSH, null);

                    } //here ^^
                }
            });
        }
        counter++;
        //THE SEARCH EDIT TEXT
        final String nullcheck = "";// A NULL STRING
        searchEditText = (EditText)findViewById(R.id.searchEditText);



        //SEARCH BUTTON
        searchButton = (Button)findViewById(R.id.searchButton);
        //EVENT HANDLING

        searchButton.setOnClickListener(

                //Starting up an interface for an event
                new Button.OnClickListener() {

                    public void onClick(View v) {

                        // WILL IMPLEMENT LATER
                        if (nullcheck.equals(searchEditText.getText().toString())) {

                            // TOAST
                            Toast.makeText(MainActivity.this, "enter a label to search for", Toast.LENGTH_LONG).show();

                        }
                        else{

                            boolean exists = myDB.searchLabel(searchEditText.getText().toString());

                            System.out.println(searchEditText.getText().toString());

                            if (exists) {

                                // TOAST
                                //Toast.makeText(MainActivity.this, "Label already exists" , Toast.LENGTH_LONG).show();
                                Intent launchLabelDisplayIntent = new Intent(MainActivity.this, LabelDisplay.class);
                                launchLabelDisplayIntent.putExtra("label", searchEditText.getText().toString());
                                launchLabelDisplayIntent.putExtra("counter",counter);
                                startActivity(launchLabelDisplayIntent);


                            }
                            else {
                                t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                                    public void onInit(int status) {
                                        if (status != TextToSpeech.ERROR) {
                                            t1.setLanguage(Locale.US);
                                            t1.speak("Note not found, use note command to create a new one", TextToSpeech.QUEUE_FLUSH, null);

                                        } //here ^^
                                    }
                                });
                                seema = "confirm";
                            }
                        }

                    }

                }
        );

        myDB = new MyDBHandler(this);

        //FUNCTIONALITY FOR LIST ITEM CLICK

        ArrayList<String> labelList = myDB.getAllLabels();

        ArrayAdapter<String> labelsAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1,labelList);


        ListView labelsListView = (ListView)findViewById(R.id.labelsListView);
        labelsListView.setAdapter(labelsAdapter);

        //LONG CLICK LISTENER
        labelsListView.setOnItemLongClickListener(

                new AdapterView.OnItemLongClickListener() {

                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                        String label = String.valueOf(parent.getItemAtPosition(position));
                        //Intent launchWordListIntent = new Intent(MainActivity.this, LabelDisplay.class);
                        //launchWordListIntent.putExtra("label", label);
                        //launchWordListIntent.putExtra("counter",counter);
                        //startActivity(launchWordListIntent);
                        myDB.deleteLabel(label) ;
                        Intent refresh = new Intent(MainActivity.this, MainActivity.class);
                        refresh.putExtra("counter",counter);
                        startActivity(refresh);
                        finish();

                        return true;
                    }

                }

        );

        //ON CLICK LISTENER
        labelsListView.setOnItemClickListener(

                new AdapterView.OnItemClickListener() {

                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        String label = String.valueOf(parent.getItemAtPosition(position));
                        Intent launchWordListIntent = new Intent(MainActivity.this, LabelDisplay.class);
                        launchWordListIntent.putExtra("label", label);
                        launchWordListIntent.putExtra("counter",counter);
                        startActivity(launchWordListIntent);

                    }

                }

        );


        fab = (FloatingActionButton)findViewById(R.id.fab);
        fab.setOnClickListener(

                //Starting up an interface for an event
                new FloatingActionButton.OnClickListener() {

                    public void onClick(View v) {


                        boolean exists = myDB.searchLabel(searchEditText.getText().toString());
                        System.out.println(searchEditText.getText().toString());
                        Intent launchAddNewLabelIntent = new Intent(MainActivity.this, AddNewLabel.class);
                        if (exists) {

                            String msg=searchEditText.getText().toString();
                            System.out.println("Sending this to the intent "+msg);
                            launchAddNewLabelIntent.putExtra("EDIT", msg);
                        }
                        if(exists || from.equals("new") || from.equals("button")) {
                            launchAddNewLabelIntent.putExtra("from",from);
                            launchAddNewLabelIntent.putExtra("counter",counter);
                            startActivity(launchAddNewLabelIntent);
                        }
                    }

                }


        );





    }


    //sidebar changes
    private void addDrawerItems() {
        String[] osArray = { "Home", "Reminders", "Checklist", "Exit" };
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, osArray);
        mDrawerList.setAdapter(mAdapter);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(position==0) {
                    Intent intent = new Intent(MainActivity.this, ToActivity.class);
                    intent.putExtra("counter",counter);
                    startActivity(intent);

                }
                else if(position==1){
                    Intent intent = new Intent(MainActivity.this, ReminderActivity.class);
                    intent.putExtra("counter",counter);
                    startActivity(intent);
                    //addReminderInCalendar();
                }
                else if(position==2){
                    Intent intent = new Intent(MainActivity.this, CheckList.class);
                    startActivity(intent);

                }
                else{
                    Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                    homeIntent.addCategory( Intent.CATEGORY_HOME );
                    homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(homeIntent);
                    finish();

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

    private void promptSpeechInput(String dialog) {

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                dialog);
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

                            switch (seema) {
                                case "new":
                                    findCommand(result.get(0));
                                    break;
                                case "confirm":
                                    if(result.get(0).equalsIgnoreCase("yes")) {
                                        Intent launchWordListIntent = new Intent(MainActivity.this, LabelDisplay.class);
                                        launchWordListIntent.putExtra("from", from);
                                        launchWordListIntent.putExtra("counter", counter);
                                        startActivity(launchWordListIntent);
                                    }
                                    break;
                            }
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

        if((com.matches("(.*)note(.*)") || com.matches("(.*)Note(.*)") || com.matches("(.*)NOTE(.*)"))
                || (com.matches("(.*)item(.*)") || com.matches("(.*)Item(.*)") || com.matches("(.*)ITEM(.*)"))) {
            from = "new";
            fab.callOnClick();
        }
        else if(com.matches("(.*)edit(.*)") || com.matches("(.*)Edit(.*)") || com.matches("(.*)EDIT(.*)")) {
            String[] newString= com.split("edit ");
            if(newString.length<2) {
                t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                    public void onInit(int status) {
                        if (status != TextToSpeech.ERROR) {
                            t1.setLanguage(Locale.US);
                            t1.speak("Please name the label you want to edit", TextToSpeech.QUEUE_FLUSH, null);

                        } //here ^^
                    }
                });
            }
            else {
                searchEditText.setText(newString[1]);
                from = "edit";
                fab.callOnClick();
            }
        }
        else if((com.matches("(.*)search(.*)") || com.matches("(.*)Search(.*)") || com.matches("(.*)SEARCH(.*)"))
                || (com.matches("(.*)open(.*)") || com.matches("(.*)Open(.*)") || com.matches("(.*)OPEN(.*)"))) {
            String[] newString;
            if(com.matches("(.*)search(.*)") || com.matches("(.*)Search(.*)") || com.matches("(.*)SEARCH(.*)"))
                newString= com.split("search ");
            else
                newString= com.split("open ");
            if(newString.length<2) {
                t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                    public void onInit(int status) {
                        if (status != TextToSpeech.ERROR) {
                            t1.setLanguage(Locale.US);
                            t1.speak("Please name the label you want to open", TextToSpeech.QUEUE_FLUSH, null);

                        } //here ^^
                    }
                });
            }
            else {
                System.out.println("point 1");
                searchEditText.setText(newString[1]);
                System.out.println("point 2");
                from = "new";
                searchButton.callOnClick();
            }
        }
        else if((com.matches("(.*)delete(.*)") || com.matches("(.*)Delete(.*)") || com.matches("(.*)DELETE(.*)"))
                || (com.matches("(.*)remove(.*)") || com.matches("(.*)Remove(.*)") || com.matches("(.*)REMOVE(.*)"))) {
            final String[] newString;
            if(com.matches("(.*)delete(.*)") || com.matches("(.*)Delete(.*)") || com.matches("(.*)DELETE(.*)"))
                newString = com.split("delete ");
            else
                newString = com.split("remove ");
            if(newString.length<2) {
                t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                    public void onInit(int status) {
                        if (status != TextToSpeech.ERROR) {
                            t1.setLanguage(Locale.US);
                            t1.speak("Please name the label you want to delete", TextToSpeech.QUEUE_FLUSH, null);

                        } //here ^^
                    }
                });
            }
            else {
                myDB.deleteLabel(newString[1]);
                t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                    public void onInit(int status) {
                        if (status != TextToSpeech.ERROR) {
                            t1.setLanguage(Locale.US);
                            t1.speak("Note with label "+newString[1]+" has been removed", TextToSpeech.QUEUE_FLUSH, null);

                        } //here ^^
                    }
                });
                Intent refresh = new Intent(this, MainActivity.class);
                refresh.putExtra("counter", counter);
                startActivity(refresh);
                finish();
            }
        }
        else {
            t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                public void onInit(int status) {
                    if (status != TextToSpeech.ERROR) {
                        t1.setLanguage(Locale.US);
                        t1.speak("Command not recognized", TextToSpeech.QUEUE_FLUSH, null);

                    } //here ^^
                }
            });

        }

    }
}

