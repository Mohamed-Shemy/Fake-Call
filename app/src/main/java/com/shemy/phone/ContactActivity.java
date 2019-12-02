package com.shemy.phone;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class ContactActivity extends AppCompatActivity
{
    ListView clist;
    String[] nameArr;
    String[] numberArr;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        clist = findViewById(R.id.contact_list);

        LoadContacts();

        clist.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                Toast.makeText(ContactActivity.this, nameArr[position], Toast.LENGTH_LONG).show();
            }
        });
    }

    private void LoadContacts()
    {
        ArrayList<String> name = new ArrayList<String>();
        ArrayList<String> number = new ArrayList<String>();

        ContentResolver cr = getContentResolver();
        Cursor cur = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null,
                "upper (" + ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + ") ASC");

        String na, nu;
        while (cur.moveToNext())
        {
            na = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            nu = cur.getString(cur.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            name.add(na);
            number.add(nu);
        }

        nameArr = name.toArray(new String[name.size()]);
        numberArr = number.toArray(new String[number.size()]);

        Custom_List custom_List = new Custom_List(ContactActivity.this, nameArr, numberArr);
        clist.setAdapter(custom_List);
    }

    public void GoToTimer(View view)
    {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("name","");
        intent.putExtra("phone","");
        startActivity(intent);
    }
}


class Custom_List extends ArrayAdapter<String>
{
    private final Activity context;
    private final String[] names;
    private final String[] phones;


    public Custom_List(Activity context, String[] title, String[] descr)
    {
        super(context, R.layout.custom_list, title);
        this.context = context;
        this.names = title;
        this.phones = descr;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent)
    {
        LayoutInflater layoutInflater = context.getLayoutInflater();
        View rowView = layoutInflater.inflate(R.layout.custom_list, null, true);
        TextView txt_name = rowView.findViewById(R.id.contact_name);
        final TextView txt_number = rowView.findViewById(R.id.contact_phone);
        ImageButton call = rowView.findViewById(R.id.btn_call);
        txt_name.setText(names[position]);
        txt_number.setText(phones[position]);

        txt_name.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                ContactClick(names[position],phones[position]);
            }
        });
        txt_number.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                ContactClick(names[position],phones[position]);
            }
        });

        call.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"+ phones[position].toString()));
                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
                {
                    Toast.makeText(context, "App Need Permission To Make Call", Toast.LENGTH_LONG).show();
                    return;
                }
                context.startActivity(i);
            }
        });

        return rowView;
    }

    private void ContactClick(String name, String phone)
    {
        //Toast.makeText(context, name + "\n" + phone, Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(context, MainActivity.class);
        intent.putExtra("name",name);
        intent.putExtra("phone",phone);
        context.startActivity(intent);
    }

}
