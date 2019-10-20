package com.example.httptest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class InfoDialog extends Dialog {
    private View.OnClickListener mPositiveListener;
    ImageView close;        //닫기버튼
    ImageView imageView;    //공원이미지
    Bitmap bitmap;
    TextView pName;//공원이름
    String pUrl;//안내사진주소
    TextView pEquip;//주요 시설
    TextView pHelp;//설명
    TextView pAddr;//주소
    Thread mThread;//이미지 로딩
    ImageView tobord;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //다이얼로그 밖의 화면은 흐리게 만들어줌
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
        layoutParams.dimAmount = 0.8f;
        getWindow().setAttributes(layoutParams);

        setCancelable(true);//바깥클릭시 닫는게 true

        close = findViewById(R.id.close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        tobord = findViewById(R.id.tobord);
        tobord.setOnClickListener(mPositiveListener);


    }

    public void init(String a,String b,String c,String d,String e){
        setContentView(R.layout.activity_info_dialog);

        pUrl=b;

        imageView = findViewById(R.id.imageView);
        pName =findViewById(R.id.pName);
        pEquip= findViewById(R.id.pEquip);
        pHelp=findViewById(R.id.pHelp);
        pAddr=findViewById(R.id.pAddr);

        pName.setText(Html.fromHtml("<h3 style=\"display:inline;\">"+a+"</h3>"));
        pEquip.setText(Html.fromHtml("<h5>"+c+"</h5>"));
        Log.d("asdasd",d);
        pHelp.setText(d);
        pAddr.setText(e);


        mThread = new Thread() {
            @Override
            public void run() {
                try {
                    URL url = new URL(pUrl);

                    // Web에서 이미지를 가져온 뒤
                    // ImageView에 지정할 Bitmap을 만든다
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setDoInput(true); // 서버로 부터 응답 수신
                    conn.connect();

                    InputStream is = conn.getInputStream(); // InputStream 값 가져오기
                    bitmap = BitmapFactory.decodeStream(is); // Bitmap으로 변환

                } catch (MalformedURLException e) {
                    e.printStackTrace();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        mThread.start(); // Thread 실행
        try {
            mThread.join();
            imageView.setImageBitmap(bitmap);
        } catch (InterruptedException ee) {
            ee.printStackTrace();
        }
    }

    //생성자 생성
    public InfoDialog(@NonNull Context context, View.OnClickListener positiveListener, View.OnClickListener negativeListener) {
        super(context);
        this.mPositiveListener = positiveListener;
    }
}