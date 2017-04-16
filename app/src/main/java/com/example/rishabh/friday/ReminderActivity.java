package com.example.rishabh.friday;

import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.speech.RecognizerIntent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class ReminderActivity extends AppCompatActivity {

    //speech
    private ImageButton speak;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private int counter=0;
    private String flag="INACTIVE";
    private String from="button";
    //speech

    private ArrayAdapter<String> mAdapter;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mActivityTitle;
    private ImageButton add;
    ListView reminderList;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder);

        mDrawerList = (ListView)findViewById(R.id.navList1);
        mDrawerLayout = (DrawerLayout)findViewById(R.id.d_layout);
        mActivityTitle = getTitle().toString();
        addDrawerItems();

        setupDrawer();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //mic button changes
        speak=(ImageButton) findViewById(R.id.speak);
        speak.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                promptSpeechInput();
            }
        });

        add = (ImageButton) findViewById(R.id.add);
        from="new";
        add.setOnClickListener(
            new View.OnClickListener() {
                public void onClick(View view) {
                    System.out.println("Clicked");
                    Intent intent = new Intent(ReminderActivity.this, NewReminder.class);
                    intent.putExtra("from", from);
                    startActivity(intent);

                }
            }
        );

        ArrayList<String> reminders = getRemindersFromCalendar();
        ArrayAdapter<String> reminderAdapter = new ArrayAdapter(this,android.R.layout.simple_list_item_1, reminders);

        reminderList = (ListView)findViewById(R.id.reminderList);
        reminderList.setAdapter(reminderAdapter);

        reminderList.setOnItemClickListener(

                new AdapterView.OnItemClickListener() {

                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        String label = String.valueOf(parent.getItemAtPosition(position));
                        String[] split = label.split(" : ");
                        label = split[0];
                        Intent launchReminderIntent = new Intent(ReminderActivity.this, NewReminder.class);
                        launchReminderIntent.putExtra("event_id", label);
                        launchReminderIntent.putExtra("from", "display");
                        startActivity(launchReminderIntent);

                    }

                }

        );

        reminderList.setOnItemLongClickListener(

                new AdapterView.OnItemLongClickListener() {

                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                        String label = String.valueOf(parent.getItemAtPosition(position));
                        String[] split = label.split(" : ");
                        label = split[0];
                        deleteReminder(label);
                        Intent refresh = new Intent(ReminderActivity.this, ReminderActivity.class);
                        startActivity(refresh);
                        finish();
                        return true;
                    }

                }

        );


    }

    private void addDrawerItems() {
        String[] osArray = { "Home", "Notes", "Checklist" };
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, osArray);
        mDrawerList.setAdapter(mAdapter);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(position==0) {
                    Intent intent = new Intent(ReminderActivity.this, ToActivity.class);
                    intent.putExtra("counter",counter);
                    startActivity(intent);

                }
                else if(position==1){
                    Intent intent = new Intent(ReminderActivity.this, MainActivity.class);
                    intent.putExtra("counter",counter);
                    startActivity(intent);
                    //addReminderInCalendar();
                }
                else{

                    Intent intent = new Intent(ReminderActivity.this, CheckList.class);
                    startActivity(intent);

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





    private void deleteReminder(String event_id) {
        Context context = this.getApplicationContext();
        Calendar cal = Calendar.getInstance();
        String uriCal = getCalendarUriBase(true);
        int rows =0;
        if(uriCal != null) {
            Uri EVENTS_URI = Uri.parse(uriCal + "events");
            Uri deleteURI = ContentUris.withAppendedId(EVENTS_URI,Long.parseLong(event_id));
            rows = getContentResolver().delete(deleteURI,null,null);
        }
        System.out.println(event_id+" "+rows);
    }

    private ArrayList<String> getRemindersFromCalendar() {

        Context context = this.getApplicationContext();
        Calendar cal = Calendar.getInstance();
        ArrayList<String> reminders = new ArrayList<>();
        String uriCal = getCalendarUriBase(true);

        if(uriCal != null) {
            Uri EVENTS_URI = Uri.parse(uriCal + "events");
            ContentResolver cr = getContentResolver();
            TimeZone timeZone = TimeZone.getDefault();

            Cursor cursor = context.getContentResolver()
                    .query(
                            EVENTS_URI,
                            new String[]{"_id", "title", "description",
                                    "dtstart", "dtend", "rdate", "rrule"}, null, null, null);
            cursor.moveToFirst();

            int count = cursor.getCount();
            for (int i = 0; i < count; i++) {
                String temp = cursor.getString(0) +" : "+ cursor.getString(1);
                if(temp!= null) {
                    temp.trim();
                    if (!temp.equals("")) {

                        if(cal.getTimeInMillis() < Long.parseLong(cursor.getString(3))) {
                            System.out.println(cursor.getString(0));
                            System.out.println(temp);
                            /*System.out.println(cursor.getString(2));
                            System.out.println(cursor.getString(3));
                            System.out.println(cursor.getString(4));
                            System.out.println(cursor.getString(5));
                            System.out.println(cursor.getString(6));*/

                            reminders.add(temp);
                        }
                    }

                }
                cursor.moveToNext();
            }
        }
        return reminders;
    }

    private String getCalendarUriBase(boolean eventUri) {
        Uri calendarURI = null;
        try {
            if (android.os.Build.VERSION.SDK_INT <= 7) {
                calendarURI = (eventUri) ? Uri.parse("content://calendar/") : Uri.parse("content://calendar/calendars");
            } else {
                calendarURI = (eventUri) ? Uri.parse("content://com.android.calendar/") : Uri
                        .parse("content://com.android.calendar/calendars");
            }
        } catch (Exception e) {

            e.printStackTrace();
        }

        if(calendarURI != null)
            return calendarURI.toString();
        else
            return null;
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
        String temp = com;
        temp = temp.toUpperCase();
        if (temp.matches("(.*)REMINDER(.*)")) {
            from="speech";
            add.callOnClick();
        }

        else if (com.matches("(.*)Open(.*)") || com.matches("(.*)open(.*)") || com.matches("(.*)OPEN(.*)")) {
            String[] split=com.split("open ");
            System.out.println(split[1]);
            String label = split[1];
            Intent launchReminderIntent = new Intent(ReminderActivity.this, NewReminder.class);
            launchReminderIntent.putExtra("event_id", label);
            launchReminderIntent.putExtra("from", "display");
            startActivity(launchReminderIntent);
        }

        else if (com.matches("(.*)delete(.*)") || com.matches("(.*)Delete(.*)") || com.matches("(.*)DELETE(.*)")) {
            String[] split = com.split("delete ");
            deleteReminder(split[1]);
            Intent refresh = new Intent(ReminderActivity.this, ReminderActivity.class);
            startActivity(refresh);
            finish();

        }

    }
}
