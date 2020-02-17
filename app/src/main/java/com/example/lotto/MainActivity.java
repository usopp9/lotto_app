package com.example.lotto;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private LinearLayout container;

    DBHelper dbHelper;

    final static String dbName = "numbers.db";
    final static int dbVersion = 3;

    private  JSONObject json = null;
    private  String lottoHtml;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        new Thread() {
            public void run() {

                //이번주 로또 회차구하기
                int drwNo = 895;
                String drwDate = "2020-01-25";

                SimpleDateFormat dateSet = new SimpleDateFormat ( "yyyy-MM-dd");

                try{
                    String date = dateSet.format(new Date());
                    Date startDate = dateSet.parse(date);

                    Date endDate = dateSet.parse(drwDate);
                    long diff = endDate.getTime() - startDate.getTime();

                    long calDateDays = diff / (24*60*60*1000);

                    calDateDays = Math.abs(calDateDays)/7;

                    Calendar cal = Calendar.getInstance();
                    cal.setTime(startDate);

                    int getDay =  cal.get(Calendar.DAY_OF_WEEK);

                    //토요일
                    if(getDay == 7){
                        SimpleDateFormat f = new SimpleDateFormat("HH:mm:ss",Locale.KOREA);
                        Date d1 = f.parse("20:54:00");

                        String today = f.format(new Date());
                        Date d2 = f.parse(today);
                        long timediff = d1.getTime() - d2.getTime();
                        long sec = timediff / 1000;

                        //-일경우 8시 54분 지남
                        if(sec > 0){
                            calDateDays -= 1;
                        }
                    }

                    drwNo += calDateDays;

                }catch (ParseException e){

                }

                lottoHtml = GetLotto(drwNo);

                Bundle bundle = new Bundle();
                bundle.putString("getDate",lottoHtml);
                Message msg = new Message();
                msg.setData(bundle);

                handler.sendMessage(msg);

            }
        }.start();


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

                        final View subLayout = inflater.inflate(R.layout.mynumbers, container, false);

                        subLayout.setId(Integer.parseInt(cursor.getString(0)));

                        List getThisWeek = new ArrayList();
                        List getCursor = new ArrayList();

                        try{
                            getThisWeek.add(json.getString("drwtNo1"));
                            getThisWeek.add(json.getString("drwtNo2"));
                            getThisWeek.add(json.getString("drwtNo3"));
                            getThisWeek.add(json.getString("drwtNo4"));
                            getThisWeek.add(json.getString("drwtNo5"));
                            getThisWeek.add(json.getString("drwtNo6"));
                            getThisWeek.add(json.getString("bnusNo"));

                            getCursor.add(cursor.getString(1));
                            getCursor.add(cursor.getString(2));
                            getCursor.add(cursor.getString(3));
                            getCursor.add(cursor.getString(4));
                            getCursor.add(cursor.getString(5));
                            getCursor.add(cursor.getString(6));

                            for(int i = 0; i < getCursor.size();i++){

                                String getNum = getCursor.get(i).toString();
                                boolean tf = false;

                                for(int j = 0; j < getThisWeek.size(); j++){

                                    if(getNum.equals(getThisWeek.get(j).toString())){
                                        tf = true;
                                        break;
                                    }
                                }

                                if(i ==0){
                                    SetText((TextView) subLayout.findViewById(R.id.textView2), cursor.getString(1),false, tf);
                                }else  if(i ==1){
                                    SetText((TextView) subLayout.findViewById(R.id.textView3), cursor.getString(2),false, tf);
                                }else  if(i ==2){
                                    SetText((TextView) subLayout.findViewById(R.id.textView4), cursor.getString(3),false, tf);
                                }else  if(i ==3){
                                    SetText((TextView) subLayout.findViewById(R.id.textView5), cursor.getString(4),false, tf);
                                }else  if(i ==4){
                                    SetText((TextView) subLayout.findViewById(R.id.textView6), cursor.getString(5),false, tf);
                                }else  if(i ==5){
                                    SetText((TextView) subLayout.findViewById(R.id.textView7), cursor.getString(6),false, tf);
                                }
                            }
                            SetText((TextView) subLayout.findViewById(R.id.delete_number), "X",false, true);
                        }catch (JSONException e){

                        }

                        TextView deleteView = subLayout.findViewById(R.id.delete_number);

                        deleteView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                LinearLayout getParent = (LinearLayout) v.getParent();

                                SQLiteDatabase db = dbHelper.getWritableDatabase();
                                String sql = "DELETE FROM numbers WHERE _id = " + getParent.getId() + "";

                                db.execSQL(sql);
                                dbHelper.close();
                                container.removeView(getParent);
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

                for (int i = 0; i < 6; i++) {

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

                    SetText((TextView) subLayout.findViewById(R.id.textView2), list.get(0).toString(),true,true);
                    SetText((TextView) subLayout.findViewById(R.id.textView3), list.get(1).toString(),true,true);
                    SetText((TextView) subLayout.findViewById(R.id.textView4), list.get(2).toString(),true,true);
                    SetText((TextView) subLayout.findViewById(R.id.textView5), list.get(3).toString(),true,true);
                    SetText((TextView) subLayout.findViewById(R.id.textView6), list.get(4).toString(),true,true);
                    SetText((TextView) subLayout.findViewById(R.id.textView7), list.get(5).toString(),true,true);

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

    public String SetColor(int number, boolean getType, boolean getCheck) {
        String color = "";
        if(getType){
            if (number < 11) {
                color = "#FCC43D";
            } else if (number < 21) {
                color = "#8CC6E7";
            } else if (number < 31) {
                color = "#F18D80";
            } else if (number < 41) {
                color = "#A7A2DE";
            } else if (number < 46) {
                color = "#6BCE9E";
            }
        }else{
            if(getCheck){
                color = "#FA5858";
            }else{
                color = "#CED8F6";
            }
        }
        return color;
    }

    public void SetText(TextView v, String getNumber, boolean getType, boolean getCheck) {
        TextView textView1 = v;
        textView1.setText(getNumber);

        if(getNumber.equals("X")){
            getNumber = "45";
            getType = true;
        }

        GradientDrawable bgShape1 = (GradientDrawable) textView1.getBackground();
        bgShape1.setColor(Color.parseColor(SetColor(Integer.parseInt(getNumber),getType,getCheck)));
    }

    public String GetLotto(int drwNo){
        String result;

        try {

            URL url = new URL("https://www.dhlottery.co.kr/common.do?method=getLottoNumber&drwNo="+drwNo);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();

            httpURLConnection.setReadTimeout(3000);
            httpURLConnection.setConnectTimeout(3000);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setUseCaches(false);
            httpURLConnection.connect();

            int responseStatusCode = httpURLConnection.getResponseCode();

            InputStream inputStream;
            if (responseStatusCode == HttpURLConnection.HTTP_OK) {
                inputStream = httpURLConnection.getInputStream();
            } else {
                inputStream = httpURLConnection.getErrorStream();
            }

            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                sb.append(line);
            }

            bufferedReader.close();
            httpURLConnection.disconnect();

            result = sb.toString().trim();


        } catch (Exception e) {
            result = e.toString();
        }

    return  result;
    }

    final Handler handler = new Handler()
    {

        public void handleMessage(Message msg)
        {
            try {

                json = new JSONObject(msg.getData().getString("getDate"));

                if(json.getString("returnValue").equals("success")){
                    TextView textView1 = findViewById(R.id.textView9);
                    textView1.setText(json.getString("drwNo")+"회차");

                    TextView textView2 = findViewById(R.id.textView8);
                    textView2.setText(json.getString("drwNoDate"));

                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View subLayout = inflater.inflate(R.layout.thisweek, container, false);
                    try{
                        LinearLayout topContainer = findViewById(R.id.linearLayout1);

                        SetText((TextView) subLayout.findViewById(R.id.textView2), json.getString("drwtNo1"),true,true);
                        SetText((TextView) subLayout.findViewById(R.id.textView3), json.getString("drwtNo2"),true,true);
                        SetText((TextView) subLayout.findViewById(R.id.textView4), json.getString("drwtNo3"),true,true);
                        SetText((TextView) subLayout.findViewById(R.id.textView5), json.getString("drwtNo4"),true,true);
                        SetText((TextView) subLayout.findViewById(R.id.textView6), json.getString("drwtNo5"),true,true);
                        SetText((TextView) subLayout.findViewById(R.id.textView7), json.getString("drwtNo6"),true,true);
                        SetText((TextView) subLayout.findViewById(R.id.textView8), json.getString("bnusNo"),true,true);

                        topContainer.addView(subLayout);

                    }catch (JSONException e){

                    }
                }
            });
        }
    };

}

