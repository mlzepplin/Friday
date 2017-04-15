package com.example.rishabh.friday;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import java.util.TimeZone;

public class ReminderActivity extends AppCompatActivity {

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

        add = (ImageButton) findViewById(R.id.add);
        add.setOnClickListener(
            new View.OnClickListener() {
                public void onClick(View view) {
                    System.out.println("Clicked");
                    Intent intent = new Intent(ReminderActivity.this, NewReminder.class);
                    intent.putExtra("from", "new");
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
        String[] osArray = { "Notes", "Reminders", "Checklist" };
        mAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, osArray);
        mDrawerList.setAdapter(mAdapter);

        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int counter = getIntent().getIntExtra("counter",0);
                if(position==0) {
                    Intent intent = new Intent(ReminderActivity.this, MainActivity.class);
                    intent.putExtra("counter",counter);
                    startActivity(intent);

                }
                else if(position==1){
                    Intent intent = new Intent(ReminderActivity.this, ReminderActivity.class);
                    intent.putExtra("counter",counter);
                    startActivity(intent);
                }
                else{

                    Toast.makeText(ReminderActivity.this, "You have Not Created anything for this!Remember!!!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ReminderActivity.this, MainActivity.class);
                    intent.putExtra("counter",counter);
                    startActivity(intent);
                }
            }
        });
    }

    private void setupDrawer() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.drawer_open, R.string.drawer_close) {


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

    private void deleteReminder(String event_id) {
        Context context = this.getApplicationContext();
        Calendar cal = Calendar.getInstance();
        String uriCal = getCalendarUriBase(false);
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
                        System.out.println(cursor.getString(0));
                        System.out.println(temp);
                        System.out.println(cursor.getString(2));
                        System.out.println(cursor.getString(3));
                        System.out.println(cursor.getString(4));
                        System.out.println(cursor.getString(5));
                        System.out.println(cursor.getString(6));
                        if(cal.getTimeInMillis() < Long.parseLong(cursor.getString(3)))
                            reminders.add(temp);
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
}
