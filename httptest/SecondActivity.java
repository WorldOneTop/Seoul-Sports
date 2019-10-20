package com.example.httptest;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class SecondActivity extends AppCompatActivity {

    TextView textView;
    ImageButton img_blog, img_map, img_list;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_second);

        textView = (TextView)findViewById(R.id.textView);
        img_blog = (ImageButton)findViewById(R.id.img_blog);
        img_map = (ImageButton)findViewById(R.id.img_map);
        img_list = (ImageButton)findViewById(R.id.img_list);

        Intent intent = getIntent();
        final String select = intent.getStringExtra("select");

        View.OnClickListener listener = new View.OnClickListener() {

            Intent intent2;

            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.img_blog :

                        intent2 = new Intent(SecondActivity.this, HTTpURL.class);

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
                                } catch (Exception e) {
                                    Log.d("asdddd",e+"");
                                    e.printStackTrace();
                                }
                            }
                        };
                        thread.start();
                        try {
                            thread.join();

                            intent2.putExtra("list",select);

                            startActivity(intent2);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        break;

                    case R.id.img_list :
                        intent2 = new Intent(SecondActivity.this, MainActivity.class);
                        intent2.putExtra("fromlist", true);
                        intent2.putExtra("select", select);
                        break;

                    case R.id.img_map :
                        intent2 = new Intent(SecondActivity.this, MainActivity.class);
                        intent2.putExtra("fromlist", false);
                        intent2.putExtra("select", select);
                        break;
                }
                startActivity(intent2);
            }
        };

        textView.setText(select);
        img_blog.setOnClickListener(listener);
        img_list.setOnClickListener(listener);
        img_map.setOnClickListener(listener);
    }
}