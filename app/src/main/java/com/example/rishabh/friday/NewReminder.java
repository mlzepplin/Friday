package com.example.rishabh.friday;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class NewReminder extends AppCompatActivity {

    private EditText title;
    private EditText description;
    private EditText date;
    private EditText time;
    private ImageButton dateButton;
    private ImageButton timeButton;
    private Button save;
    private Switch alarm;
    private Boolean alarmCheck = false;
    private long completeTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_reminder);

        title = (EditText) findViewById(R.id.title);
        description = (EditText) findViewById(R.id.description);
        date = (EditText) findViewById(R.id.date);
        dateButton = (ImageButton) findViewById(R.id.dateButton);
        time = (EditText) findViewById(R.id.time);
        timeButton = (ImageButton) findViewById(R.id.timeButton);
        alarm = (Switch) findViewById(R.id.alarm);
        save = (Button) findViewById(R.id.save);

        Intent intent = getIntent();
        String from = intent.getStringExtra("from");
        if(from.equals("display")) {
            String label = intent.getStringExtra("event_id");
            Cursor cursor = getRemindersFromCalendar();
            for (int i = 0; i < cursor.getCount(); i++) {
                if(label.equals(cursor.getString(0)))
                    break;
                cursor.moveToNext();
            }
            title.setText(cursor.getString(1));
            description.setText(cursor.getString(2));
            Calendar cal = milliToDate(Long.parseLong(cursor.getString(3)));
            date.setText(getStringDate(cal));

            time.setText(getStringTime(cal));
            alarm.setChecked(getAlarmData(label));

            title.setEnabled(false);
            title.setFocusable(false);
            description.setFocusable(false);
            description.setEnabled(false);
            date.setFocusable(false);
            date.setEnabled(false);
            time.setFocusable(false);
            time.setEnabled(false);
            alarm.setFocusable(false);
            alarm.setEnabled(false);
            save.setVisibility(View.INVISIBLE);

        }
        else {

            dateButton.setOnClickListener(
                    new View.OnClickListener() {
                        public void onClick(View v) {
                            setDate(v);
                        }
                    }
            );


            time.setText("00:00");

            timeButton.setOnClickListener(
                    new View.OnClickListener() {
                        public void onClick(View v) {
                            setTime(v);
                        }
                    }
            );

            alarm.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    alarmCheck = isChecked;
                    System.out.println(alarmCheck);
                }
            });

            save.setOnClickListener(
                    new Button.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            System.out.println(title.getText() + " " + date.getText());


                            if (!title.getText().toString().equals("") && !date.getText().toString().equals("")) {

                                String longTime = date.getText().toString() + " " + time.getText().toString() + ":00";
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                                Date dt = null;
                                try {
                                    dt = sdf.parse(longTime);
                                    //dt.setMonth(dt.get);
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }
                                completeTime = dt.getTime();
                                if (completeTime > Calendar.getInstance().getTimeInMillis()) {

                                    addReminderInCalendar();
                                    Intent intent = new Intent(NewReminder.this, ReminderActivity.class);
                                    startActivity(intent);
                                } else
                                    Toast.makeText(getApplicationContext(), "Choose Future Time", Toast.LENGTH_SHORT).show();
                            } else if (title.getText().toString().equals("")) {
                                Toast.makeText(getApplicationContext(), "Enter Title", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Select Date", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
            );
        }
    }

    public void setTime(View v) {
        final Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,
                                          int minute) {

                        time.setText(hourOfDay + ":" + minute);
                        System.out.println(alarm.getText());
                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();
    }

    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(999);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == 999) {
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);
            return new DatePickerDialog(this,
                    myDateListener, year, month, day);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {

            date.setText(new StringBuilder().append(arg1).append("/")
                    .append(arg2+1).append("/").append(arg3));
        }
    };

    private void addReminderInCalendar() {
        Calendar cal = Calendar.getInstance();
        Uri EVENTS_URI = Uri.parse(getCalendarUriBase(true) + "events");
        ContentResolver cr = getContentResolver();
        TimeZone timeZone = TimeZone.getDefault();

        /** Inserting an event in calendar. */
        ContentValues values = new ContentValues();
        values.put(CalendarContract.Events.CALENDAR_ID, 1);
        values.put(CalendarContract.Events.TITLE, title.getText().toString());
        values.put(CalendarContract.Events.DESCRIPTION, description.getText().toString());
        values.put(CalendarContract.Events.ALL_DAY, 0);
        // event starts in 1 minute
        values.put(CalendarContract.Events.DTSTART, completeTime);
        // ends 60 minutes from now
        values.put(CalendarContract.Events.DTEND, completeTime + 2 * 60 * 1000);
        values.put(CalendarContract.Events.EVENT_TIMEZONE, timeZone.getID());
        if(alarmCheck)
            values.put(CalendarContract.Events.HAS_ALARM, 1);
        else
            values.put(CalendarContract.Events.HAS_ALARM, 0);
        Uri event = cr.insert(EVENTS_URI, values);

        ContentValues eventContent = values;

        // Display event id.
        //Toast.makeText(getApplicationContext(), "Event added :: ID :: " + event.getLastPathSegment(), Toast.LENGTH_SHORT).show();

        /** Adding reminder for event added. */
        if(alarmCheck) {
            Uri REMINDERS_URI = Uri.parse(getCalendarUriBase(true) + "reminders");
            values = new ContentValues();
            values.put(CalendarContract.Reminders.EVENT_ID, Long.parseLong(event.getLastPathSegment()));
            values.put(CalendarContract.Reminders.METHOD, CalendarContract.Reminders.METHOD_ALERT);
            values.put(CalendarContract.Reminders.MINUTES, 0);
            cr.insert(REMINDERS_URI, values);
        }
        Calendar calen = Calendar.getInstance();
        calen.setTimeInMillis(eventContent.getAsLong(CalendarContract.Events.DTSTART));
        if(alarmCheck)
            Toast.makeText(getApplicationContext(), "Event added, alarm will go off on : "
                +getStringDate(calen)
                +" at "+getStringTime(calen), Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(getApplicationContext(), "Event added for : "
                    +getStringDate(calen)
                    +" at "+getStringTime(calen)+" ,no ALARM", Toast.LENGTH_SHORT).show();
    }


    private Cursor getRemindersFromCalendar() {

        Context context = this.getApplicationContext();
        String uriCal = getCalendarUriBase(true);

        if(uriCal != null) {
            Uri EVENTS_URI = Uri.parse(uriCal + "events");

            Cursor cursor = context.getContentResolver()
                    .query(
                            EVENTS_URI,
                            new String[]{"_id", "title", "description",
                                    "dtstart"}, null, null, null);
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
                    }

                }
                cursor.moveToNext();
            }
            cursor.moveToFirst();
            return cursor;
        }
        else
            return null;
    }

    private Boolean getAlarmData(String id) {
        Context context = this.getApplicationContext();
        String uriCal = getCalendarUriBase(true);

        if(uriCal != null) {
            Uri REMINDERS_URI = Uri.parse(uriCal + "reminders");
            Cursor cursor = context.getContentResolver()
                    .query(
                            REMINDERS_URI,
                            new String[]{"event_id"}, null, null, null);
            cursor.moveToFirst();
            int i;
            for (i = 0; i < cursor.getCount(); i++) {
                if (cursor.getString(0).equals(id))
                    break;
                cursor.moveToNext();
            }
            if(i==cursor.getCount())
                return false;
            else
                return true;
        }
        return false;
    }

    /** Returns Calendar Base URI, supports both new and old OS. */
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
        return calendarURI.toString();
    }

    public Calendar milliToDate(long millis) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millis);
        return calendar;
    }

    public long dateToMilli(String longTime) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date dt = null;
        try {
            dt = sdf.parse(longTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dt.getTime();
    }

    public String getStringDate(Calendar calen) {
        String date = calen.get(Calendar.YEAR)+"/"+(calen.get(Calendar.MONTH)+1)+"/" +calen.get(Calendar.DAY_OF_MONTH);
        return date;
    }

    public String getStringTime(Calendar calen) {
        String time = calen.get(Calendar.HOUR)+":"+calen.get(Calendar.MINUTE);
        return time;
    }



}
