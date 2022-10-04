package com.example.project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View.OnClickListener;


import java.util.Vector;

import com.example.project.DBUtils;

public class MainActivity extends AppCompatActivity{
    private EditText query;
    private Spinner spinner;
    private String selectDb;
    private Button btn;
    private RadioGroup radioGroup;
    private String select_sql_red;
    private String queryText;
    private TableLayout result;
    private Handler handler;
    private long startTime;
    private TextView time;
    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        // choose database
        myChooseDB();
        //choose mySQL or redshift
        mySetOnItem();
        // click button
        myOnClick();

        handler = new Handler(){
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (msg.what == 0x12){
                    Vector<Vector<String>> m = (Vector<Vector<String>>) msg.obj;
                    for(int j=0;j<m.size();j++){
                        TableRow row = new TableRow(MainActivity.this);
                        for (int i =0 ; i < m.get(j).size();i++){
                            TextView text = new TextView(MainActivity.this);
                            text.setText(m.get(j).get(i));
                            row.addView(text);
                        }
                        result.addView(row);
                        result.setStretchAllColumns(true);
                    }
                }
                long endTime = System.currentTimeMillis();
                String consume = (endTime-startTime)+" ms";
                time.setText(consume);
            }
        };
    }

    public void initView(){
        radioGroup = findViewById(R.id.RadioGroup);
        query = findViewById(R.id.query);
        spinner = findViewById(R.id.spinDb);
        btn = findViewById(R.id.btn);
        result = findViewById(R.id.result);
        time = findViewById(R.id.time);
    }


    public void myChooseDB(){
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                RadioButton radioButton = findViewById(i);
                select_sql_red = radioButton.getText().toString();
            }
        });
    }

    public void mySetOnItem(){
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selectDb = spinner.getSelectedItem().toString();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });
    }

    public void myOnClick(){
        btn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                result.removeAllViews();
                queryText = query.getText().toString();
                Toast.makeText(MainActivity.this, "Query Database:"+
                        selectDb+" "+select_sql_red, Toast.LENGTH_SHORT ).show();
                startTime = System.currentTimeMillis();
                Thread thread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        Vector<Vector<String>> map = DBUtils.getInfo(selectDb, select_sql_red, queryText);
                        Log.e(" ","ee");
                        if (map!=null){
                            Log.e(" ","xxx");
                            Message message = handler.obtainMessage();
                            message.what = 0x12;
                            message.obj = map;
                            handler.sendMessage(message);
                        }
                    }
                });
                thread.start();


            }
        });
    }


}