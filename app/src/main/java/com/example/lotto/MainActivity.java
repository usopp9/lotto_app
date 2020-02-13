package com.example.lotto;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.xmlpull.v1.XmlPullParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private LinearLayout container;


    DBHelper dbHelper;

    final static String dbName = "numbers.db";
    final static int dbVersion = 2;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        container = findViewById(R.id.linearLayout7);

        dbHelper = new DBHelper(this, dbName, null, dbVersion);


        Button btn_number = findViewById(R.id.btn_num);
        btn_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                String sql = "SELECT * FROM numbers;";

                Cursor cursor = db.rawQuery(sql, null);

                if (cursor.getCount() > 0) {
                    while (cursor.moveToNext()) {
                       System.out.println(cursor.getString(0)+", "+cursor.getString(1)+", " + cursor.getString(2)+", "+ cursor.getString(3)+", " + cursor.getString(4)+", "+cursor.getString(5));
                    }
                }

                dbHelper.close();
            }
        });


        Button btn = findViewById(R.id.button);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                container.removeAllViews();


                for(int i = 0;i<8;i++){

                    List list = new ArrayList();
                    while(list.size() < 6){
                        int get_random = (int)(Math.random() * 45)+1;
                        list.add(get_random);
                        HashSet<String> distinctData =new HashSet<String>(list);
                        list =new ArrayList<String>(distinctData);

                    }
                    Collections.sort(list);
                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                    View subLayout = inflater.inflate(R.layout.sub, container,false);


                    TextView textView1 = subLayout.findViewById(R.id.textView2);
                    textView1.setText(list.get(0).toString());
                    GradientDrawable bgShape1 = (GradientDrawable) textView1.getBackground();
                    bgShape1.setColor(Color.parseColor(SetColor((int)list.get(0))));

                    TextView textView2 = subLayout.findViewById(R.id.textView3);
                    textView2.setText(list.get(1).toString());
                    GradientDrawable bgShape2 = (GradientDrawable) textView2.getBackground();
                    bgShape2.setColor(Color.parseColor(SetColor((int)list.get(1))));

                    TextView textView3 = subLayout.findViewById(R.id.textView4);
                    textView3.setText(list.get(2).toString());
                    GradientDrawable bgShape3 = (GradientDrawable) textView3.getBackground();
                    bgShape3.setColor(Color.parseColor(SetColor((int)list.get(2))));

                    TextView textView4 = subLayout.findViewById(R.id.textView5);
                    textView4.setText(list.get(3).toString());
                    GradientDrawable bgShape4 = (GradientDrawable) textView4.getBackground();
                    bgShape4.setColor(Color.parseColor(SetColor((int)list.get(3))));

                    TextView textView5 = subLayout.findViewById(R.id.textView6);
                    textView5.setText(list.get(4).toString());
                    GradientDrawable bgShape5 = (GradientDrawable) textView5.getBackground();
                    bgShape5.setColor(Color.parseColor(SetColor((int)list.get(4))));

                    TextView textView6 = subLayout.findViewById(R.id.textView7);
                    textView6.setText(list.get(5).toString());
                    GradientDrawable bgShape6 = (GradientDrawable) textView6.getBackground();
                    bgShape6.setColor(Color.parseColor(SetColor((int)list.get(5))));


                    subLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            v.setBackgroundColor(Color.parseColor("#26000000"));

                            TextView textView1 = v.findViewById(R.id.textView2);
                            TextView textView2 = v.findViewById(R.id.textView3);
                            TextView textView3 = v.findViewById(R.id.textView4);
                            TextView textView4 = v.findViewById(R.id.textView5);
                            TextView textView5 = v.findViewById(R.id.textView6);
                            TextView textView6 = v.findViewById(R.id.textView7);

                            SQLiteDatabase db = dbHelper.getWritableDatabase();
                            String sql = String.format("INSERT INTO numbers VALUES('" + textView1.getText() + "','" + textView2.getText() + "','" + textView3.getText() + "','" + textView4.getText() +  "','" + textView5.getText() +"','"+textView6.getText()+"');");

                            db.execSQL(sql);
                            dbHelper.close();
                        }
                    });
                    container.addView(subLayout);
                }
            }
        });



    }
    public String SetColor(int number){
        String color = "";
        if(number < 11){
            color = "#FCC43D";
        }else if(number < 21){
            color ="#8CC6E7";
        }else if(number < 31){
            color ="#F18D80";
        }else if(number <41){
            color ="#A7A2DE";
        }else{
            color ="#6BCE9E";
        }
        return color;
    }

//dbhelper

    static class DBHelper extends SQLiteOpenHelper {

        //database 파일을 생성
        public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
            super(context, name, factory, version);
        }

        //실행할 때 DB 최초 생성
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE numbers (no1 TEXT, no2 TEXT, no3 INTEGER, no4 TEXT, no5 TEXT, no6 TEXT);");
//result.append("\nt3 테이블 생성 완료.");
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS numbers");
            onCreate(db);
        }

    }

}

