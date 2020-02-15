package com.example.lotto;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private LinearLayout container;

    DBHelper dbHelper;

    final static String dbName = "numbers.db";
    final static int dbVersion = 3;

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
                container.removeAllViews();
                if (cursor.getCount() > 0) {

                    while (cursor.moveToNext()) {
                        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                        final View subLayout = inflater.inflate(R.layout.sub, container, false);

                        subLayout.setId(Integer.parseInt(cursor.getString(0)));

                        SetText((TextView) subLayout.findViewById(R.id.textView2), cursor.getString(1));
                        SetText((TextView) subLayout.findViewById(R.id.textView3), cursor.getString(2));
                        SetText((TextView) subLayout.findViewById(R.id.textView4), cursor.getString(3));
                        SetText((TextView) subLayout.findViewById(R.id.textView5), cursor.getString(4));
                        SetText((TextView) subLayout.findViewById(R.id.textView6), cursor.getString(5));
                        SetText((TextView) subLayout.findViewById(R.id.textView7), cursor.getString(6));

                        subLayout.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                SQLiteDatabase db = dbHelper.getWritableDatabase();
                                String sql = "DELETE FROM numbers WHERE _id = " + subLayout.getId() + "";

                                db.execSQL(sql);
                                dbHelper.close();
                                container.removeView(v);
                            }
                        });
                        container.addView(subLayout);
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

                for (int i = 0; i < 7; i++) {

                    List list = new ArrayList();
                    while (list.size() < 6) {
                        int get_random = (int) (Math.random() * 45) + 1;
                        list.add(get_random);
                        HashSet<String> distinctData = new HashSet<String>(list);
                        list = new ArrayList<String>(distinctData);

                    }
                    Collections.sort(list);
                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

                    View subLayout = inflater.inflate(R.layout.sub, container, false);

                    SetText((TextView) subLayout.findViewById(R.id.textView2), list.get(0).toString());
                    SetText((TextView) subLayout.findViewById(R.id.textView3), list.get(1).toString());
                    SetText((TextView) subLayout.findViewById(R.id.textView4), list.get(2).toString());
                    SetText((TextView) subLayout.findViewById(R.id.textView5), list.get(3).toString());
                    SetText((TextView) subLayout.findViewById(R.id.textView6), list.get(4).toString());
                    SetText((TextView) subLayout.findViewById(R.id.textView7), list.get(5).toString());

                    subLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            try {
                                ColorDrawable viewColor = (ColorDrawable) v.getBackground();
                                viewColor.getColor();

                            } catch (Exception e) {
                                v.setBackgroundColor(Color.parseColor("#26000000"));

                                TextView textView1 = v.findViewById(R.id.textView2);
                                TextView textView2 = v.findViewById(R.id.textView3);
                                TextView textView3 = v.findViewById(R.id.textView4);
                                TextView textView4 = v.findViewById(R.id.textView5);
                                TextView textView5 = v.findViewById(R.id.textView6);
                                TextView textView6 = v.findViewById(R.id.textView7);

                                SQLiteDatabase db = dbHelper.getWritableDatabase();
                                String sql = String.format("INSERT INTO numbers VALUES(" + null + ",'" + textView1.getText() + "','" + textView2.getText() + "','" + textView3.getText() + "','" + textView4.getText() + "','" + textView5.getText() + "','" + textView6.getText() + "');");

                                db.execSQL(sql);
                                dbHelper.close();
                            }
                        }
                    });
                    container.addView(subLayout);
                }
            }
        });

    }

    public String SetColor(int number) {
        String color = "";
        if (number < 11) {
            color = "#FCC43D";
        } else if (number < 21) {
            color = "#8CC6E7";
        } else if (number < 31) {
            color = "#F18D80";
        } else if (number < 41) {
            color = "#A7A2DE";
        } else {
            color = "#6BCE9E";
        }
        return color;
    }

    public void SetText(TextView v, String getNumber) {
        TextView textView1 = v;
        textView1.setText(getNumber);
        GradientDrawable bgShape1 = (GradientDrawable) textView1.getBackground();
        bgShape1.setColor(Color.parseColor(SetColor(Integer.parseInt(getNumber))));
    }

}

