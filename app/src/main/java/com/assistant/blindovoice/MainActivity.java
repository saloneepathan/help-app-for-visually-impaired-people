package com.assistant.blindovoice;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.telephony.SmsManager;
//import android.util.Log;
//import android.view.GestureDetector;
//import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
//import java.util.TimeZone;
public class MainActivity extends AppCompatActivity implements View.OnLongClickListener {
    //GestureDetector gestureDetector;
    TextToSpeech textToSpeech;
    Button b;
    String name;
    //String Phonenumber = "";
    String dial_number = "";
    String message="";
    String msgname="";
    String todolist,time;
    //String weekdays ="";
    boolean chkminutes=false;
    Calendar calendar;
    int i=10;
    public static final String ListName = "listname";
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "MyPrefs" ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        b = findViewById(R.id.button);
        b.setOnLongClickListener(this);
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        //gc=new BlobleClass();
       // startAlert();
        //calendar = Calendar.getInstance();
      //  calendar.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
            }
        });
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.READ_PHONE_STATE},
                1);
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.SEND_SMS},
                2);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1);
        }
    }
    @SuppressLint("MissingPermission")
    public void readContacts(String searchname, String action, String message){
        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        if (cur.getCount() > 0) {
            while (cur.moveToNext()) {
                String id = cur.getString(cur.getColumnIndex(ContactsContract.Contacts._ID));
                String name = cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                if (Integer.parseInt(cur.getString(cur.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER))) > 0) {
                    System.out.println("name : " + name + ", ID : " + id);
if(searchname.equalsIgnoreCase(name)){
    // get the phone number
    Cursor pCur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,null,
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = ?",
            new String[]{id}, null);
    while (pCur.moveToNext()) {
        String phone = pCur.getString(
                pCur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        System.out.println("phone" + phone);
if(action.equals("call")) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        textToSpeech.speak("Calling "+searchname+" !", TextToSpeech.QUEUE_FLUSH, null, null);
    }
    try {
        Intent intent = new Intent(Intent.ACTION_CALL);
        intent.setData(Uri.parse("tel:" + phone));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && checkSelfPermission(Manifest.permission.CALL_PHONE)
                != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1);
        }else {
            startActivity(intent);
        }
    } catch (Exception e) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textToSpeech.speak("Error While making Call!", TextToSpeech.QUEUE_FLUSH, null, null);
        }

        Toast.makeText(getApplicationContext(), "Error in your phone call" + e.getMessage(), Toast.LENGTH_LONG).show();
    }
}else if(action.equals("message")){
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
        textToSpeech.speak("Sending Message to "+searchname+"!", TextToSpeech.QUEUE_FLUSH, null, null);
    }

    if(message.isEmpty() || message.length()<=0){
        message="Hello!";
    }
    try {
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phone, null, message, null, null);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textToSpeech.speak("Message Sent Successfully!", TextToSpeech.QUEUE_FLUSH, null, null);
        }

        Toast.makeText(getApplicationContext(), "Message Sent",
                Toast.LENGTH_LONG).show();
    } catch (Exception ex) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            textToSpeech.speak("Error While Sending Message!", TextToSpeech.QUEUE_FLUSH, null, null);
        }

        Toast.makeText(getApplicationContext(),ex.getMessage().toString(),
                Toast.LENGTH_LONG).show();
        ex.printStackTrace();
    }
}
        break;
    }
    pCur.close();
}
                }
            }
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_PHONE_STATE, Manifest.permission.SEND_SMS, Manifest.permission.READ_CONTACTS, Manifest.permission.CALL_PHONE}, 101);
                    // Toast.makeText(MainActivity.this, "Permission denied to read phone state", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
    //call Shweta
    @SuppressLint("NewApi")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case 10:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String str = result.get(0);
                    Toast.makeText(getApplicationContext(), str, Toast.LENGTH_SHORT).show();
                    String[] arr = str.split(" ");
                    String action = arr[0];
                    Toast.makeText(getApplicationContext(), action, Toast.LENGTH_SHORT).show();

                    //call vedika
                    if (action.equalsIgnoreCase("call")) {
                        if (arr.length > 2) {
                            try {
                                int arraylength = arr.length;
                                for (int i = 1; i <= arraylength; i++) {
                                    if (i == 1) {
                                        name = arr[i];
                                    } else {
                                        name = name + " " + arr[i];
                                    }
                                }
                            } catch (Exception e) {
                            }
                            Toast.makeText(getApplicationContext(), name, Toast.LENGTH_SHORT).show();
                        } else if (arr.length == 2) {
                            name = arr[1];
                        } else {
                        }
                        if (name.length() <= 0 || name.isEmpty()) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                textToSpeech.speak("Name Not Found!", TextToSpeech.QUEUE_FLUSH, null, null);
                            }
                            Toast.makeText(getApplicationContext(), "Empty Name!", Toast.LENGTH_SHORT).show();
                        } else {
                            //search name in contact
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                textToSpeech.speak("Searching for "+name+" in your contact list!", TextToSpeech.QUEUE_FLUSH, null, null);
                            }
                            Toast.makeText(getApplicationContext(), "Searching...", Toast.LENGTH_SHORT).show();
                            readContacts(name,"call",null);
                        }
                    }


                    //dial 1234567890
                    else if (action.equalsIgnoreCase("dial")) {
                        if (arr.length > 2) {
                            try {
                                int arraylength = arr.length;
                                for (int i = 1; i <= arraylength; i++) {
                                    if (i == 1) {
                                        dial_number = arr[i];
                                    } else {
                                        dial_number = dial_number + arr[i];
                                    }
                                }
                            } catch (Exception e) {
                            }
                            Toast.makeText(getApplicationContext(), dial_number, Toast.LENGTH_SHORT).show();
                        } else if (arr.length == 2) {
                            dial_number = arr[1];
                        }
                        if (dial_number.length() <= 0 || dial_number.isEmpty()) {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                textToSpeech.speak("Incorrect Number!", TextToSpeech.QUEUE_FLUSH, null, null);
                            }
                            Toast.makeText(getApplicationContext(), "Empty Number!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), dial_number, Toast.LENGTH_SHORT).show();
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                textToSpeech.speak("Calling "+dial_number+"!", TextToSpeech.QUEUE_FLUSH, null, null);
                            }
                            dial_number = "+91" + dial_number;
                            try {
                                Intent intent = new Intent(Intent.ACTION_CALL);
                                intent.setData(Uri.parse("tel:" + dial_number));
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                                        && checkSelfPermission(Manifest.permission.CALL_PHONE)
                                        != PackageManager.PERMISSION_GRANTED){
                                    requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, 1);
                                }else {
                                    startActivity(intent);
                                }
                            }catch(Exception e){
                                textToSpeech.speak("Error While Calling "+dial_number+"!", TextToSpeech.QUEUE_FLUSH, null, null);
                                Toast.makeText(getApplicationContext(), "Error in your phone call"+e.getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }



                    }


                    //Text 1234567890 send how are you
                    else if (action.equalsIgnoreCase("text")) {
                        if (arr.length > 2) {
                            try {
                                int arraylength = arr.length;
                                int x=0;
                                for (int i = 1; i <= arraylength; i++) {
                                    if (i == 1) {
                                        dial_number = arr[i];
                                    } else {
                                        if(arr[i].equalsIgnoreCase("send")){
                                            x=i;
                                        }else{
                                            if(i>x && x>0){
                                                if(message.length()<=0 || message.isEmpty()){
                                                    message=arr[i];
                                                }else{
                                                    message=message +" "+arr[i];
                                                }
                                            }else{
                                                dial_number = dial_number + arr[i];
                                            }
                                        }
                                    }
                                }
                            } catch (Exception e) {
                            }
                            Toast.makeText(getApplicationContext(), dial_number, Toast.LENGTH_SHORT).show();
                        }

                        else if (arr.length == 2) {
                            dial_number = arr[1];
                        }
                        textToSpeech.speak("Sending Message to "+dial_number+"!", TextToSpeech.QUEUE_FLUSH, null, null);
                        dial_number = "+91" + dial_number;
                        try {
                            SmsManager smsManager = SmsManager.getDefault();
                            smsManager.sendTextMessage(dial_number, null, message, null, null);
                            textToSpeech.speak("Message Sent Successfully!", TextToSpeech.QUEUE_FLUSH, null, null);
                            Toast.makeText(getApplicationContext(), "Message Sent",
                                    Toast.LENGTH_LONG).show();
                        } catch (Exception ex) {
                            textToSpeech.speak("Error While Sending Message!", TextToSpeech.QUEUE_FLUSH, null, null);
                            Toast.makeText(getApplicationContext(),ex.getMessage().toString(),
                                    Toast.LENGTH_LONG).show();
                            ex.printStackTrace();
                        }

                        //message vedika
                        //message vedika send where are you?
                    }else if (action.equalsIgnoreCase("message")) {
                        if (arr.length > 2) {
                            try {
                                int arraylength = arr.length;
                                int x=0;
                                for (int i = 1; i <= arraylength; i++) {
                                    if (i == 1) {
                                        msgname = arr[i];
                                    } else {
                                        if(arr[i].equalsIgnoreCase("send")){
                                            x=i;
                                        }else{
                                            if(i>x && x>0){
                                                if(message.length()<=0 || message.isEmpty()){
                                                    message=arr[i];
                                                }else{
                                                    message=message+" "+arr[i];
                                                }
                                            }else{
                                                msgname = msgname +" "+ arr[i];
                                            }
                                        }
                                    }
                                }
                            } catch (Exception e) {
                            }
                            readContacts(msgname,"message",message);
                        } else if (arr.length == 2) {
                            dial_number = arr[1];
                        }
                        textToSpeech.speak("Sending Message to "+dial_number+"!", TextToSpeech.QUEUE_FLUSH, null, null);
                        dial_number = "+91" + dial_number;
                        try {
                            SmsManager smsManager = SmsManager.getDefault();
                            smsManager.sendTextMessage(dial_number, null, message, null, null);
                            textToSpeech.speak("Message Sent Successfully!", TextToSpeech.QUEUE_FLUSH, null, null);
                            Toast.makeText(getApplicationContext(), "Message Sent",
                                    Toast.LENGTH_LONG).show();
                        } catch (Exception ex) {
                            Toast.makeText(getApplicationContext(),ex.getMessage().toString(),
                                    Toast.LENGTH_LONG).show();
                            ex.printStackTrace();
                        }
                    }

                    //Make list add apple orange banana
                    else if (action.equalsIgnoreCase("make")) {
                        if (arr.length > 3) {
                            try {
                                int arraylength = arr.length;
                                for (int i = 3; i <= arraylength; i++) {
                                    //boolean b=false;
                                    //String newval="";
                                    if (i == 3) {
                                        todolist = arr[i];
                                    } else {
                                            todolist = todolist +", "+ arr[i];
                                    }
                                }
                            } catch (Exception e) {
                            }
                        }
                        if (todolist.length() <= 0 || todolist.isEmpty()) {
                            textToSpeech.speak("No List Item Found!", TextToSpeech.QUEUE_FLUSH, null, null);
                            Toast.makeText(getApplicationContext(), "Empty List!", Toast.LENGTH_SHORT).show();
                        } else {
                            SharedPreferences.Editor editor = sharedpreferences.edit();
                            editor.putString(ListName, todolist);
                            editor.commit();
                           // gc.setTODOLIST(todolist);
                            textToSpeech.speak("List Created Successfully!", TextToSpeech.QUEUE_FLUSH, null, null);
                            Toast.makeText(getApplicationContext(), "To Do List Created!", Toast.LENGTH_SHORT).show();
                        }

                        //set alarm after xyz minutes
                    }else if (action.equalsIgnoreCase("set")) {
                        if (arr.length >= 4) {
                            try {
                                int arraylength = arr.length;
                                int x=0;
                                int y=0;
                                for (int i = 3; i <= arraylength; i++) {
                                    if (i == 3) {
                                        time = arr[i];
                                        chkminutes=true;
                                    }
                                }
                            } catch (Exception e) {
                            }
                            if(chkminutes){
                                i = Integer.parseInt(time);
                            }else{
                               i=10;
                            }
                            Intent intent = new Intent(this, MyBroadcastReceiver.class);
                            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                                    this.getApplicationContext(), 0, intent, 0);
                            AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                            try{
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
                                                + (i * 60000), pendingIntent);
                                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
                                                + (i * 60000), pendingIntent);
                                    } else {
                                        alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
                                                + (i * 60000), pendingIntent);
                                    }
                                    textToSpeech.speak("Alarm Set Successfully!", TextToSpeech.QUEUE_FLUSH, null, null);
                                    Toast.makeText(getApplicationContext(),"Alarm Set at : "+calendar.getTime(),Toast.LENGTH_SHORT).show();
                            }catch (Exception e){
                            }
                        }
                        //read list
                    }else if (action.equalsIgnoreCase("read")) {
                        String tdlist = sharedpreferences.getString(ListName,"");
                       //String tdlist=gc.getTODOLIST();
                       textToSpeech.speak(tdlist, TextToSpeech.QUEUE_FLUSH, null, null);
                    }else{
                        Toast.makeText(getApplicationContext(),"No list detected",Toast.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }
    @Override
    public boolean onLongClick(View view) {
        if(view==b){
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                textToSpeech.speak("how can I help you?", TextToSpeech.QUEUE_FLUSH, null, null);
            }
            try {
                Thread.sleep(2000);
            } catch (InterruptedException es) {
                es.printStackTrace();
            }
            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
            if (intent.resolveActivity(getPackageManager()) != null) {
                startActivityForResult(intent, 10);
            } else {
                Toast.makeText(getApplicationContext(), "Your Device Don't Support Speech Input", Toast.LENGTH_SHORT).show();
            }
        }
        return false;
    }
}