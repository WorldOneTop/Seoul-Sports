package com.example.httptest;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

public class FirstActivity extends AppCompatActivity {

    EditText editText;
    ImageButton img_soccer,img_basketball,img_badminton,img_gateball,img_volleyball;
    ImageButton btn_search;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);

        editText = (EditText)findViewById(R.id.editText);
        img_badminton = (ImageButton)findViewById(R.id.img_badminton);
        img_soccer = (ImageButton)findViewById(R.id.img_soccer);
        img_basketball = (ImageButton)findViewById(R.id.img_basketball);
        img_gateball = (ImageButton)findViewById(R.id.img_gateball);
        img_volleyball = (ImageButton)findViewById(R.id.img_volleyball);
        btn_search = (ImageButton)findViewById(R.id.button_search);

        View.OnClickListener onclickListener = new View.OnClickListener() {

            Intent intent = new Intent(FirstActivity.this, SecondActivity.class);

            public void onClick(View view) {
                switch (view.getId()) {
                    case R.id.editText :
                        editText.setText("");
                        break;
                    case R.id.img_badminton :
                        intent.putExtra("select", "배드민턴");
                        break;
                    case R.id.img_basketball :
                        intent.putExtra("select", "농구");
                        break;
                    case R.id.img_gateball :
                        intent.putExtra("select", "게이트볼");
                        break;
                    case R.id.img_soccer :
                        intent.putExtra("select", "축구");
                        break;
                    case R.id.img_volleyball :
                        intent.putExtra("select", "족구");
                        break;
                    case R.id.button_search :
                        if(editText.getText().toString() == "") {
                            editText.setText("입력하시고 버튼 눌러주세요 ^^ -->");
                            break;
                        }
                        intent.putExtra("select", editText.getText().toString());
                        break;
                }
                startActivity(intent);
            }
        };
        img_volleyball.setOnClickListener(onclickListener);
        img_gateball.setOnClickListener(onclickListener);
        img_basketball.setOnClickListener(onclickListener);
        img_soccer.setOnClickListener(onclickListener);
        img_badminton.setOnClickListener(onclickListener);
        btn_search.setOnClickListener(onclickListener);
    }
}