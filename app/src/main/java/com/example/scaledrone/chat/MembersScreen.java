package com.example.scaledrone.chat;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MembersScreen extends AppCompatActivity {
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_members);
        textView = (TextView) findViewById(R.id.text);

    }

    /* Floating action button handler */
    public void ButtonHandler(View View){

        /*FloatingActionButton floatingActionButton = View.findViewById(R.id.floatingActionButton2);
        floatingActionButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View View){
                startActivity(new Intent(View.getContext(), MembersScreen.class));
            }
        });*/
    }
}