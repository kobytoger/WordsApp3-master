package com.example.kotytoger.wordsapp;

import android.content.Intent;
import android.content.res.Configuration;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Arrays;
import java.util.Locale;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    DatabaseHelper myDB;
    Button btnAdd, btnView, btnStart, btnClear;
    EditText editText1;
    EditText editText2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        editText1 = findViewById(R.id.editText2);
        editText2 = findViewById(R.id.editText1);

        btnAdd = findViewById(R.id.btnAdd);
        btnView = findViewById(R.id.btnView);
        btnStart = findViewById(R.id.btnStart);
        btnClear = findViewById(R.id.btnClear);

        myDB = new DatabaseHelper(this);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String entry1 = editText1.getText().toString();
                String entry2 = editText2.getText().toString();

                if(editText1.length()!= 0 && editText2.length()!= 0){
                    AddData(entry1,entry2);
                    editText1.setText("");
                    editText2.setText("");
                }else{
                    Toast.makeText(MainActivity.this, "The field is empty", Toast.LENGTH_LONG).show();
                }
            }
        });

        btnView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ViewListContents.class);
                startActivity(intent);
            }
        });

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Simulation.class);
                startActivity(intent);
            }
        });

        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myDB.deleteData();
            }
        });



    }

    public void AddData(String entry1, String entry2) {

        boolean insertData = myDB.addData(entry1,entry2);

        if(insertData==true){
            Toast.makeText(this, "Data Successfully Inserted!", Toast.LENGTH_LONG).show();
        }else{
            Toast.makeText(this, "Something went wrong :(.", Toast.LENGTH_LONG).show();
        }
    }
}