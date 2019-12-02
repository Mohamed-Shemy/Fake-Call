package com.shemy.phone;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Vibrator;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class CallActivity extends AppCompatActivity
{
    TextView txt_name,txt_phone,txt_time;
    Ringtone defaultRingtone = null;
    Vibrator vibrator = null;
    ImageView img;
    String name,phone,path;
    Timer timer = null;
    TimerTask task = null;
    long sec = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call);


        Window window = this.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);


        name = getIntent().getStringExtra("name");
        phone = getIntent().getStringExtra("phone");
        path = getIntent().getStringExtra("img");
        txt_name = findViewById(R.id.call_name);
        txt_phone = findViewById(R.id.call_phone);
        txt_time = findViewById(R.id.call_status);
        img = findViewById(R.id.contact_image);

        txt_name.setText(name);
        txt_phone.setText(phone + " (Egypt)");


        Fragment fragment = new answer_fragment();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();


        PlayRingtone();


        try
        {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), Uri.parse(path));

            //img.setScaleType(bitmap.getWidth() == bitmap.getHeight() ? ImageView.ScaleType.CENTER : ImageView.ScaleType.CENTER_CROP);

            img.setImageBitmap(bitmap);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN || keyCode == KeyEvent.KEYCODE_VOLUME_UP)
        {
            StopRingtone();
            return true;
        }
        else if(keyCode == KeyEvent.KEYCODE_POWER)
        {
            Cancel(null);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void Answer(View view)
    {
        StartTimer();
        Fragment fragment = new Call_Status();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction transaction = fm.beginTransaction();
        transaction.replace(R.id.fragment_container, fragment);
        transaction.commit();
        StopRingtone();
    }

    public void PlayRingtone()
    {
        defaultRingtone = RingtoneManager.getRingtone(getApplicationContext(),Settings.System.DEFAULT_RINGTONE_URI);
        defaultRingtone.play();

        vibrator = (Vibrator) getSystemService(getApplication().VIBRATOR_SERVICE);
        long[] pattern = {1500, 1000};

        vibrator.vibrate(pattern, 0);
    }

    public void StopRingtone()
    {
        if(defaultRingtone != null && defaultRingtone.isPlaying())
            defaultRingtone.stop();
        if(vibrator != null)
            vibrator.cancel();
    }

    public void Cancel(View view)
    {
        if(timer != null)
            timer.cancel();
        StopRingtone();
        finishAffinity();
    }

    private void StartTimer()
    {
        timer = new Timer();
        task = new TimerTask()
        {
            @Override
            public void run()
            {
                runOnUiThread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        sec++;
                        txt_time.setText(ParsSec(sec));
                    }
                });
            }
        };

        timer.scheduleAtFixedRate(task, 0, 1000);
    }

    private String ParsSec(long sec)
    {
        String time = "";
        int hours = (int) sec / 3600;
        int remainder = (int) sec - hours * 3600;
        int mins = remainder / 60;
        remainder = remainder - mins * 60;
        int secs = remainder;

        time = String.format("%02d:%02d:%02d",hours,mins,secs);

        return time;
    }

}
