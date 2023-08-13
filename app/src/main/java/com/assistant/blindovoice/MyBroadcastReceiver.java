package com.assistant.blindovoice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import static android.speech.tts.TextToSpeech.*;

public class MyBroadcastReceiver extends BroadcastReceiver
{
    MediaPlayer mp;
    TextToSpeech textToSpeech;
    @Override
    public void onReceive(Context context, Intent intent) {
       mp=MediaPlayer.create(context, R.raw.alarm);
       mp.start();
    }


}