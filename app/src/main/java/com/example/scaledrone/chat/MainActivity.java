package com.example.scaledrone.chat;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);
        textView = (TextView) findViewById(R.id.text);

    }

    public void ButtonHandler(View View)
    {
        String button_text;
        button_text =((Button)View).getText().toString();
        if(button_text.equals("Enter the bool"))
        {
            Intent ganesh = new Intent(this,SecondActivity.class);
            startActivity(ganesh);
        }
        else if (button_text.equals("do something else"))
        {
            Intent mass = new Intent(this,ThirdActivity.class);
            startActivity(mass);

        }
    }
}
