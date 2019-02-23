package com.kbz.hms.powerofimplicitintents;

import android.Manifest;
import android.app.Activity;
import android.app.SearchManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;
import android.widget.VideoView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {


    Button btnTimer, btnCalender, btnVideo, btnContact, btnWebSearch;
    VideoView videoView;
    ArrayList<Integer> alarmDays;
    private static final int READ_CONTACTS = 4;
    static final int REQUEST_SELECT_CONTACT = 1;
    public static final int VIDEO_CAPTURE = 2;
    public static final int GET_CONTENT = 3;
    public String TAG = "tag";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        alarmDays = new ArrayList<>();



        btnTimer = findViewById(R.id.btn_timer);
        btnCalender = findViewById(R.id.btn_calender);
        btnVideo = findViewById(R.id.btn_video);
        btnContact = findViewById(R.id.btn_contact);
        btnWebSearch = findViewById(R.id.btn_webSearch);


        btnTimer.setOnClickListener(this);
        btnContact.setOnClickListener(this);
        btnCalender.setOnClickListener(this);
        btnVideo.setOnClickListener(this);
        btnWebSearch.setOnClickListener(this);

        videoView = findViewById(R.id.vv_video);




        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

        if (v == findViewById(R.id.btn_timer)) {

            Intent i = new Intent(AlarmClock.ACTION_SET_ALARM);
            i.putExtra(AlarmClock.EXTRA_MESSAGE, "Alarm Title");

            alarmDays.add(Calendar.SATURDAY);
            alarmDays.add(Calendar.SUNDAY);
            alarmDays.add(Calendar.MONDAY);
            alarmDays.add(Calendar.TUESDAY);
            alarmDays.add(Calendar.WEDNESDAY);
            alarmDays.add(Calendar.THURSDAY);
            alarmDays.add(Calendar.FRIDAY);
            i.putExtra(AlarmClock.EXTRA_DAYS, alarmDays);
            i.putExtra(AlarmClock.EXTRA_VIBRATE, true);
            i.putExtra(AlarmClock.EXTRA_HOUR, 10);
            i.putExtra(AlarmClock.EXTRA_MINUTES, 21);
            startActivity(i);
        }

        if (v == findViewById(R.id.btn_calender)) {

            Calendar cal = new GregorianCalendar();
            cal.setTime(new Date());
            cal.add(Calendar.MONTH, 2);
            long time = cal.getTime().getTime();
            Uri.Builder builder =
                    CalendarContract.CONTENT_URI.buildUpon();
            builder.appendPath("time");
            builder.appendPath(Long.toString(time));
            Intent intent =
                    new Intent(Intent.ACTION_VIEW, builder.build());
            startActivity(intent);
        }


        if (v == findViewById(R.id.btn_video)) {

            Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
            if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(takeVideoIntent, VIDEO_CAPTURE);


            }

        }

        if (v == findViewById(R.id.btn_contact)) {

            contactPermissionRequeset();
        }

        if (v == findViewById(R.id.btn_webSearch)) {

            Uri webpage = Uri.parse("http://www.google.com/chrome");
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(webpage);
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivity(intent);
            }


        }


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == VIDEO_CAPTURE && resultCode == RESULT_OK) {

            Toast.makeText(this, "Video", Toast.LENGTH_LONG).show();
            videoView.setVideoURI(data.getData());
            videoView.start();

        } else if (requestCode == GET_CONTENT && resultCode == RESULT_OK) {

            getContact(data);

        }


    }

    private void getContact(Intent data)
    {
        Uri uri = data.getData();
        String[] projection = new String[]{ContactsContract.CommonDataKinds.Phone.NUMBER};


        Cursor cursor = getContentResolver().query(uri,
                null,
                null,
                null, null);
        if (cursor != null && cursor.moveToFirst()) {


            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));


            String myContact = "Selected name...  " + name + " \n";
            Toast.makeText(this, myContact, Toast.LENGTH_LONG).show();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Intent intent1 = new Intent(Intent.ACTION_PICK);
            intent1.setType(ContactsContract.Contacts.CONTENT_TYPE);
            startActivityForResult(intent1, GET_CONTENT);
        }
    }

    private void contactPermissionRequeset()
    {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALENDAR)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS}, READ_CONTACTS);
        }
    }

}

