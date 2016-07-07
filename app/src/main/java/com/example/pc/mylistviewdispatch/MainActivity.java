package com.example.pc.mylistviewdispatch;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.mylayout);
//        TextView tv = (TextView)findViewById(R.id.tv);
//        tv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Toast.makeText(MainActivity.this, "tv", Toast.LENGTH_SHORT).show();
//            }
//        });

        setContentView(R.layout.content_main);
        listView = (ListView) findViewById(R.id.listview);
        List<String> list = new ArrayList<>();
        for(int i = 0 ;i < 20 ; ++ i){
            list.add("测试"+i);
        }

        listView.setAdapter(new ArrayAdapter<String>(this,R.layout.text_layout ,list ));
    }


}
