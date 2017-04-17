package com.example.rishabh.friday;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
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

    //speech
    private TextToSpeech t1;
    private static String flag;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    private String seema="display";
    public String from;
    private String speak;
    public int exceptionCount=-1;
    String newd, newm, newy;
    String dialog;
    int notFirst = 0;
    public String[] months={"January","February","March","April","May","June","July","August","September","October","November","December"};
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

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        from = intent.getStringExtra("from");
        if(from.equals("display")) {
            String label = intent.getStringExtra("event_id");
            Cursor cursor = getRemindersFromCalendar();
            for (int i = 0; i < cursor.getCount(); i++) {
                System.out.println(cursor.getString(0));
                if(label.equals(cursor.getString(0)))
                    break;
                cursor.moveToNext();
            }
            if(!cursor.isAfterLast()) {
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
                dateButton.setVisibility(View.INVISIBLE);
                timeButton.setVisibility(View.INVISIBLE);
                save.setVisibility(View.INVISIBLE);
            }
            else {
                finish();
            }


        }
        else if (from.equals("speech")){
            System.out.println("GOing here");
            System.out.print(from);
            seema = "title";
            promptSpeechInput("Enter your title");

            description.setOnClickListener(
                    new Button.OnClickListener() {
                        public void onClick(View v) {
                            seema = "description";
                            promptSpeechInput("Enter the description");
                        }
                    }
            );
            date.setOnClickListener(
                    new Button.OnClickListener() {
                        public void onClick(View v) {
                            seema = "month";
                            promptSpeechInput("Enter the Month");
                        }
                    }
            );

            time.setOnClickListener(
                    new Button.OnClickListener() {
                        public void onClick(View v) {
                            seema = "time";
                            promptSpeechInput("Enter the time");
                        }
                    }
            );
            //boundary
        }
        else {
            System.out.print("New here");
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

        }


        save.setOnClickListener(
                new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final String toSpeak;
                        System.out.println(title.getText() + " " + date.getText());


                        if (!title.getText().toString().equals("") && !date.getText().toString().equals("")) {

                            String longTime = date.getText().toString() + " " + time.getText().toString() + ":00";
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                            Date dt = null;
                            try {
                                dt = sdf.parse(longTime);
                                //dt.setMonth(dt.get);

                                completeTime = dt.getTime();
                                if (completeTime > Calendar.getInstance().getTimeInMillis()) {
                                    //speech
                                    toSpeak = "Your Reminder has been saved with title" + title.getText();

                                    t1 = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
                                        public void onInit(int status) {
                                            if (status != TextToSpeech.ERROR) {
                                                t1.setLanguage(Locale.US);

                                                t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);

                                            } //here ^^
                                        }
                                    });
                                    //speech
                                    addReminderInCalendar();
                                    Intent intent = new Intent(NewReminder.this, ReminderActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                                else
                                    Toast.makeText(getApplicationContext(), "Choose Future Time", Toast.LENGTH_SHORT).show();
                            }
                            catch (ParseException e) {
                                Toast.makeText(getApplicationContext(), "Date or time format Invalid! Enter again.", Toast.LENGTH_SHORT).show();
                                notFirst = 1;
                                date.callOnClick();
                            }
                        } else if (title.getText().toString().equals("")) {
                            Toast.makeText(getApplicationContext(), "Enter Title", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Select Date", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
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

    private void promptSpeechInput(String dialog) {
        this.dialog = dialog;
        flag = "ACTIVE";
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                dialog);
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    "speech_not_supported",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return true;
    }


    public void exitButton3(View view) {
        Toast.makeText(this, "Reminder not saved !", Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        StringBuilder exceptionDate=new StringBuilder();

        super.onActivityResult(requestCode, resultCode, data);
        ArrayList<String> result;
        System.out.println("Speech Result returned");
        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                if (resultCode == RESULT_OK && null != data) {

                    result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

                    try {
                        System.out.println(seema);

                        speak = result.get(0);
                        if(from.equals("speech")) {
                            switch (seema) {
                                case "title":
                                    title.setText(speak);
                                    description.callOnClick();
                                    break;
                                case "description":
                                    description.setText(speak);
                                    exceptionCount=2;
                                    date.callOnClick();
                                    break;
                                case "alarm":
                                    if(speak.equalsIgnoreCase("yes"))
                                        alarm.setChecked(true);
                                    else
                                        alarm.setChecked(false);
                                    alarmCheck = alarm.isChecked();
                                    save.callOnClick();
                                    break;
                                case "date":
                                    Calendar calendar=Calendar.getInstance();
                                    String string = speak;
                                    DateFormat format = new SimpleDateFormat("MMM d yyyy", Locale.ENGLISH);
                                    Date dateObj = null;
                                    try {
                                        dateObj = format.parse(string);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    System.out.println(dateObj);
                                    calendar.setTime(dateObj);
                                    int yr= calendar.get(Calendar.YEAR);
                                    int mon=calendar.get(Calendar.MONTH)+1;
                                    System.out.println(mon);
                                    int dt=calendar.get(Calendar.DATE);
                                    System.out.println(dt);
                                    System.out.println("UP");
                                  //  int yr= date.getYear();
                                   // System.out.println(yr);
                                   // int mon=date.getMonth()+1;
                                  //  int dt=date.getDate();
                                    StringBuilder sb= new StringBuilder();
                                    sb.append(yr);
                                    sb.append("/");
                                    sb.append(mon);
                                    sb.append("/");
                                    sb.append(dt);
                                    String newDate=sb.toString();
                                    System.out.println(newDate);
                                    date.setText(newDate);
                                    time.callOnClick();
                                    break;
                                case "time":
                                    SimpleDateFormat displayFormat = new SimpleDateFormat("HH:mm");
                                    SimpleDateFormat parseFormat = new SimpleDateFormat("hh:mm a");
                                    Date ddd = null;
                                    speak=speak.replace(".","");
                                    speak.toUpperCase();
                                    try {
                                        ddd = parseFormat.parse(speak);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    System.out.println(parseFormat.format(ddd) + " = " + displayFormat.format(ddd));
                                    time.setText(displayFormat.format(ddd).toString());
                                    if(notFirst == 0) {
                                        seema = "alarm";
                                        promptSpeechInput("Alarm, YES or NO?");
                                    }
                                    else
                                        save.callOnClick();
                                    break;

                                case "month":
                                   // exceptionDate.append(speak);

                                    System.out.println("Month "+speak);
                                    int i;
                                    for( i=0;i<12;i++)
                                        if (speak.equals(months[i]))
                                            break;
                                    i=i+1;
                                    StringBuilder temp=new StringBuilder();
                                    temp.append(i);
                                    newm=temp.toString();
                                    System.out.println("MONTH CONVERTED  "+newm);
                                    seema="dt";
                                    promptSpeechInput("Enter day of month");
                                    break;

                                case "dt":
                                   // exceptionDate.append(" ");
                                   // exceptionDate.append(speak);
                                    newd=speak;
                                    System.out.println("Date "+newd);
                                    seema="year";
                                    promptSpeechInput("Enter the year");
                                    break;

                                case "year":
                                    newy=speak;
                                    System.out.println("Year "+newy);
                                   // exceptionDate.append(" ");
                                    //exceptionDate.append(speak);
                                    /*
                                    String dateAppended=exceptionDate.toString();

                                    //copy paste
                                    Calendar calendarExp=Calendar.getInstance();
                                    String stringExp = dateAppended;
                                    DateFormat formatExp = new SimpleDateFormat("MMM d yyyy", Locale.ENGLISH);
                                    Date dateObjExp = null;
                                    try {
                                        dateObj = formatExp.parse(stringExp);
                                    } catch (ParseException e) {
                                        e.printStackTrace();
                                    }
                                    System.out.println(dateObjExp);
                                    calendarExp.setTime(dateObjExp);
                                    int yrEx= calendarExp.get(Calendar.YEAR);
                                    int monEx=calendarExp.get(Calendar.MONTH)+1;
                                    System.out.println(monEx);
                                    int dtExp=calendarExp.get(Calendar.DATE);
                                    System.out.println(dtExp);
                                    System.out.println("UP2222");
                                    */

                                    //final format
                                    StringBuilder sb2= new StringBuilder();
                                    sb2.append(newy);
                                    sb2.append("/");
                                    sb2.append(newm);
                                    sb2.append("/");
                                    sb2.append(newd);
                                    String newDateExp=sb2.toString();
                                    System.out.println(newDateExp);
                                    date.setText(newDateExp);
                                    time.callOnClick();
                                    break;



                            }
                        }

                    }
                    catch(NullPointerException ne) {
                        exceptionCount++;
                        System.out.println("Exception count"+exceptionCount);
                        if (exceptionCount>0 && seema.equals("date")) {
                            seema="month";
                            promptSpeechInput("Enter the month");
                        }

                        else {
                            Toast.makeText(getApplicationContext(),
                                    "No detection !!!",
                                    Toast.LENGTH_SHORT).show();
                            promptSpeechInput(dialog);
                        }
                    }
                }

                break;
            }
        }

    }

}
