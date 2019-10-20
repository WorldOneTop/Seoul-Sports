package com.example.httptest;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class Listplz extends Activity {
    ArrayList<String> items;
    ArrayAdapter adapter;
    ListView listview;
    JSONArray jsonArray;
    ArrayList arrayList;
    int [] plength;
    double[][] postn;
    String[] pName;
    int size;
    double[] nowLoc;
    Intent intent2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        arrayList = new ArrayList();
        nowLoc = new double[2];

        jsonArray = onJsonReady(jsonArray);

        // 빈 데이터 리스트 생성.
        items = new ArrayList<String>() ;
        // ArrayAdapter 생성. 아이템 View를 선택(single choice)가능하도록 만듦.
       adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_single_choice, items) ;

        // listview 생성 및 adapter 지정.
        listview  = (ListView) findViewById(R.id.listView);
        listview.setAdapter(adapter) ;

//        items.add("LIST" + Integer.toString(1));

        nowLoc[0]= getIntent().getDoubleExtra("lat",0.0);
        nowLoc[1]= getIntent().getDoubleExtra("long",0.0);


        getList(getIntent().getStringExtra("select"));


        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String  str = items.get(i).substring(0, items.get(i).indexOf(" "));
                str = str.replaceAll(" ", "");


                intent2 = new Intent(Listplz.this, HTTpURL.class);

                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        super.run();
                        try {
                            HttpURLConnection conn = (HttpURLConnection) new URL("http://caerang.esllee.com").openConnection();
                            conn.setDoInput(true); // 서버로 부터 응답 수신
                            conn.connect();

                            InputStream is = conn.getInputStream(); // InputStream 값 가져오기
                            Log.d("asd","asd : "+is.read());

                            byte[] s = new byte[is.available()];
                            is.read(s);
                            is.close();
                            String host = new String(s);

                            intent2.putExtra("host", host);
                            Log.d("asdd",host);
                        } catch (Exception e) {
                            Log.d("asdddd",e+"");
                            e.printStackTrace();
                        }
                    }
                };
                thread.start();
                try {
                    thread.join();

                    intent2.putExtra("list",str);
                    Log.d("ASDd",str);
                    startActivity(intent2);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }



            }
        });
        Log.d("asd",size+"");

        // listview 갱신
        getLength();
        for(int i=0;i<size;i++){
            items.add(pName[i]+"    "+plength[i]+"m");
        }

        adapter.notifyDataSetChanged();
    }
    public JSONArray onJsonReady(JSONArray j){
        try {
            j = (new JSONObject(loadJSONFromAsset())).getJSONArray("DATA");//asset파일에서 배열형식으로가져옴
        } catch (JSONException e) {
                e.printStackTrace();
        }
        return j;
    }
    //제일
    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getAssets().open("jso.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;

    }
    //제일
    public int getJsonToTag(String tag,String equi){
        arrayList.clear();

        for(int i=0;i<jsonArray.length();i++){
            try {   // equi라는 운동시설이 있는 속성(경도,위도,공원이름)을 tag로 받아서 저장
                if(jsonArray.getJSONObject(i).getString("main_equip").contains(equi))
                    arrayList.add(jsonArray.getJSONObject(i).get(tag));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return arrayList.size();
    }

    public void getList(String equi) {
        size = getJsonToTag("latitude",equi);
        postn = new double[2][size]; //[0][n] >> 위도 [1][n] >> 경도

        for(int i=0;i<size;i++)
            postn[0][i] =  Double.parseDouble((String)arrayList.get(i));//위도설정
        getJsonToTag("longitude",equi);
        for(int i=0;i<size;i++)
            postn[1][i] =  Double.parseDouble((String)arrayList.get(i));//경도설정

        getJsonToTag("p_park",equi);
        pName = new String[size];                  //공원이름
        for(int i=0;i<size;i++)
            pName[i] = (String)arrayList.get(i);
        plength = new int[size];
    }
    private static double distance(double lat1, double lon1, double lat2, double lon2, String unit) {

        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));

        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;

        if (unit == "kilometer") {
            dist = dist * 1.609344;
        } else if(unit == "meter"){
            dist = dist * 1609.344;
        }

        return (dist);
    }


    // This function converts decimal degrees to radians
    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    // This function converts radians to decimal degrees
    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }
    public void getLength(){//루트 (a1 -a2)** + (b1 -b2)**
        for(int i=0;i<size;i++){
           plength[i] = (int)(distance(nowLoc[0] , postn[0][i],nowLoc[1],postn[1][i],"meter")/100);
        }
        bubble_sort(plength,size);
    }


    //               남은거리배열    크기
    void bubble_sort(int list[], int n) {
        int i, j;
        int temp;
        String temp2;
        for (i=0;i<n-1;i++) {
            for (j = 0; j < i; j++) {
                if (list[j] > list[j + 1]) {
                    temp = list[j];
                    list[j] = list[j + 1];
                    list[j + 1] = temp;

                    temp2 = pName[j];
                    pName[j] = pName[i+1];
                    pName[j+1] = temp2;
                }
            }
        }
    }
}
