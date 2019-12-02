package com.shemy.phone;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity
{
    EditText txt_name,txt_phone,txt_hour,txt_min;
    ImageView call_image;
    TextView TimerStatus;
    Button btn_start,btn_stop;
    Uri uri;
    Timer timer;
    TimerTask task;
    String name,phone,path="";
    private int PICK_IMAGE_REQUEST = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        name = getIntent().getStringExtra("name");
        phone = getIntent().getStringExtra("phone");
        txt_name = findViewById(R.id.txt_name);
        txt_phone = findViewById(R.id.txt_phone);
        txt_hour = findViewById(R.id.timer_hour);
        txt_min = findViewById(R.id.timer_minute);
        call_image = findViewById(R.id.call_image);
        TimerStatus = findViewById(R.id.timer_count);
        btn_start = findViewById(R.id.timer_start);
        btn_stop = findViewById(R.id.timer_stop);

        txt_name.setText(name);
        txt_phone.setText(phone);
    }

    public void SelectPicture(View view)
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null)
        {
            uri = data.getData();
            path = uri.toString();
            //Toast.makeText(getApplicationContext(),uri.getPath(),Toast.LENGTH_LONG).show();
            try
            {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                call_image.setImageBitmap(bitmap);
            }
            catch (IOException e)
            {
                Toast.makeText(getApplicationContext(),e.getMessage() ,Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void StartTimer(View view)
    {
        String hours = txt_hour.getText().toString();
        String minutes = txt_min.getText().toString();

        if(!minutes.isEmpty())
        {
            try
            {
                long sec = Long.parseLong(minutes) * 60;
                if (!hours.isEmpty())
                    sec += Long.parseLong(hours) * 3600;
                RunTimer(sec);
                DisableControl();
            }
            catch (Exception ex)
            {
                Toast.makeText(getApplicationContext(), "Please Set Correct Time!!!", Toast.LENGTH_SHORT).show();
            }
        }
        else
            Toast.makeText(getApplicationContext(), "Please Set Timer Values!!!", Toast.LENGTH_SHORT).show();
    }

    private void DisableControl()
    {
        txt_hour.setEnabled(false);
        txt_min.setEnabled(false);
        txt_phone.setEnabled(false);
        txt_name.setEnabled(false);
        btn_start.setEnabled(false);
        call_image.setEnabled(false);
    }

    private void EnableControl()
    {
        txt_hour.setEnabled(true);
        txt_min.setEnabled(true);
        txt_phone.setEnabled(true);
        txt_name.setEnabled(true);
        btn_start.setEnabled(true);
        call_image.setEnabled(true);
    }

    public void RunTimer(long seconds)
    {
        timer = new Timer();
        final long[] sec = {seconds};
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
                        TimerStatus.setText(ParsSec(sec[0]));
                        sec[0]--;
                        if(sec[0] < 0)
                        {
                            StopTimer(null);
                            GoToCall();
                        }
                    }
                });
            }
        };

        timer.scheduleAtFixedRate(task, 0, 1000);
    }

    public void GoToCall()
    {
        Intent intent = new Intent(getApplicationContext(), CallActivity.class);
        intent.putExtra("name",txt_name.getText().toString());
        intent.putExtra("phone",txt_phone.getText().toString());
        intent.putExtra("img",path);
        startActivity(intent);

//        Intent alarmIntent = new Intent("android.intent.action.MAIN");
//        alarmIntent.setClass(getApplicationContext(), CallActivity.class);
//        alarmIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        alarmIntent.addFlags(
//                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
//                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
//                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
//                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
//        startActivity(alarmIntent);


    }

    public void StopTimer(View view)
    {
        if(timer!=null)
            timer.cancel();
        TimerStatus.setText("00:00:00");
        EnableControl();
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
