package com.example.httptest;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity
        implements OnMapReadyCallback, ActivityCompat.OnRequestPermissionsResultCallback {

    private boolean isfirst_temp,isfirst_temp2;
    private GoogleMap mMap;
    private Marker currentMarker = null;

    private static final String TAG = "googlemap_example";
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int UPDATE_INTERVAL_MS = 1000;  // 1초
    private static final int FASTEST_UPDATE_INTERVAL_MS = 500; // 0.5초


    // onRequestPermissionsResult에서 수신된 결과에서 ActivityCompat.requestPermissions를 사용한 퍼미션 요청을 구별하기 위해 사용됩니다.
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    boolean needRequest = false;


    // 앱을 실행하기 위해 필요한 퍼미션을 정의합니다.
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};  // 외부 저장소


    Location mCurrentLocatiion;
    LatLng currentPosition;


    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private Location location;


    private View mLayout;  // Snackbar 사용하기 위해서는 View가 필요합니다.
    // (참고로 Toast에서는 Context가 필요했습니다.)


    HashMap<String, Double> main_park_longitude;
    HashMap<String, Double> main_park_latitude;
    ArrayList<String> main_park_name;
    //제일
    JSONArray jsonArray;
    ArrayList arrayList;
    String []pName;//공원이름
    String []pUrl;//안내사진주소
    String []pEquip;//주요 시설
    String []pHelp;//설명
    String []pAddr;//주소
    InfoDialog infoDialog;//다이얼로그
    double[][] postn;
    String pickname;
    Intent intent2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        isfirst_temp2 = isfirst_temp = true;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //제일
        arrayList = new ArrayList();
        jsonArray = onJsonReady(jsonArray);



        //풀스크린으로 여백을 없앰
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);


        mLayout = findViewById(R.id.layout_main);

        locationRequest = new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL_MS)
                .setFastestInterval(FASTEST_UPDATE_INTERVAL_MS);


        LocationSettingsRequest.Builder builder =
                new LocationSettingsRequest.Builder();

        builder.addLocationRequest(locationRequest);


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        //레이아웃에 만든 fragment의 id를 참조하여 구글맵 호출
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }

    //제일
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

    @Override
    //구글맵 호출
    public void onMapReady(final GoogleMap googleMap) {
        Log.d(TAG, "onMapReady :");

        mMap = googleMap;

        //런타임 퍼미션 요청 대화상자나 GPS 활성 요청 대화상자 보이기전에
        //지도의 초기위치를 서울로 이동
        setDefaultLocation();

        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);


        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {

            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)
            startLocationUpdates(); // 3. 위치 업데이트 시작

        } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.

            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])) {

                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Snackbar.make(mLayout, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.",
                        Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {

                        // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                        ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS,
                                PERMISSIONS_REQUEST_CODE);
                    }
                }).show();
            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }

        }
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
     //제일
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                for(int i=0;i<arrayList.size();i++) {
                    if (marker.getTitle().equals(pName[i])) {
                        infoDialog = new InfoDialog(MainActivity.this,positiveListener,null);
                        infoDialog.init(pName[i],pUrl[i],pEquip[i],pHelp[i],pAddr[i]);
                        pickname = pName[i];
                        infoDialog.show();
                        break;
                    }
                }
            }
        });
    }
    Intent intent;
    private View.OnClickListener positiveListener = new View.OnClickListener() {
        public void onClick(View v) {
            intent = new Intent(MainActivity.this, HTTpURL.class);

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

                        intent.putExtra("host", host);
                    } catch (Exception e) {
                        Log.d("asdddd",e+"");
                        e.printStackTrace();
                    }
                }
            };
            thread.start();
            try {
                thread.join();

            intent.putExtra("list",pickname);

                startActivity(intent);
            infoDialog.dismiss();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };

    //위도 경도 등을 받아옴
    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(LocationResult locationResult) {
            super.onLocationResult(locationResult);

            List<Location> locationList = locationResult.getLocations();

            if (locationList.size() > 0) {
                location = locationList.get(locationList.size() - 1);
                //location = locationList.get(0);

                currentPosition
                        = new LatLng(location.getLatitude(), location.getLongitude());

//                String markerTitle = getCurrentAddress(currentPosition);
//                String markerSnippet = "위도:" + String.valueOf(location.getLatitude())
//                        + " 경도:" + String.valueOf(location.getLongitude());
//
//                Log.d(TAG, "onLocationResult : " + markerSnippet);

                if(getIntent().getBooleanExtra("fromlist",false)) {
                    Log.d("현재위치", location.getLatitude() + ",  " + location.getLongitude());
                    Intent intent3 = new Intent(MainActivity.this, Listplz.class);
                    intent3.putExtra("select",getIntent().getStringExtra("select"));
                    intent3.putExtra("lat",location.getLatitude());
                    intent3.putExtra("long",location.getLongitude());
                    startActivity(intent3);
                    finish();
                }//location.getLatitude(), location.getLongitude()



                //현재 위치에 마커 생성하고 이동
                setCurrentLocation(location);

                mCurrentLocatiion = location;
            }
        }
    };


    private void startLocationUpdates() {

        if (!checkLocationServicesStatus()) {

            Log.d(TAG, "startLocationUpdates : call showDialogForLocationServiceSetting");
            showDialogForLocationServiceSetting();
        } else {

            int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION);
            int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION);


            if (hasFineLocationPermission != PackageManager.PERMISSION_GRANTED ||
                    hasCoarseLocationPermission != PackageManager.PERMISSION_GRANTED) {

                Log.d(TAG, "startLocationUpdates : 퍼미션 안가지고 있음");
                return;
            }

            Log.d(TAG, "startLocationUpdates : call mFusedLocationClient.requestLocationUpdates");

            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

            if (checkPermission())
                mMap.setMyLocationEnabled(true);

        }

    }


    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");

        if (checkPermission()) {

            Log.d(TAG, "onStart : call mFusedLocationClient.requestLocationUpdates");
            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);

            if (mMap != null)
                mMap.setMyLocationEnabled(false);
        }
    }


    @Override
    protected void onStop() {

        super.onStop();

        if (mFusedLocationClient != null) {

            Log.d(TAG, "onStop : call stopLocationUpdates");
            mFusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }


    public String getCurrentAddress(LatLng latlng) {

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;

        try {

            addresses = geocoder.getFromLocation(
                    latlng.latitude,
                    latlng.longitude,
                    1);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";

        }


        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";

        } else {
            Address address = addresses.get(0);
            //마커찍기
            makeMarker(intent2.getStringExtra("select"));
            //현재 위치의 제목
            return address.getAddressLine(0).toString();
        }

    }


    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

//마커생성부분지움
    public void setCurrentLocation(Location location) {


        if (currentMarker != null) currentMarker.remove();


        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());//현재위치가져오기?
        //마커 속성들
//        MarkerOptions markerOptions = new MarkerOptions();//addMarker() 인자값
//        markerOptions.position(currentLatLng);          //좌표설정
//        markerOptions.title(markerTitle);               //타이틀
//        markerOptions.snippet(markerSnippet);           //서브타이틀
//        markerOptions.draggable(false);                 //드래그 불가능하도록
        //markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)); //색깔 초록색으루
        //markerOptions.alpha(0.75f);                   //투명도 설정

        // 마커찍기
//        currentMarker = mMap.addMarker(markerOptions);
//         현재 좌표 마커로하기
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(currentLatLng);
        //현재 위치로 카메라 옮김
        if(isfirst_temp) {
            isfirst_temp = false;
            mMap.moveCamera(cameraUpdate);
        }
        //제일
        intent2 = getIntent();
        if(isfirst_temp2) {
            isfirst_temp2 = false;
            makeMarker(intent2.getStringExtra("select"));
        }
    }

    //처음 위치 설정
    public void setDefaultLocation() {


        //디폴트 위치, Seoul
        LatLng DEFAULT_LOCATION = new LatLng(37.56, 126.97);
        String markerTitle = "위치정보 가져올 수 없음";
        String markerSnippet = "위치 퍼미션과 GPS 활성 여부 확인하세요";


        if (currentMarker != null) currentMarker.remove();


        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 15);
        //현재 위치로 카메라 옮깁
        mMap.moveCamera(cameraUpdate);

    }

    //여기부터는 런타임 퍼미션 처리을 위한 메소드들
    private boolean checkPermission() {

        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {
            return true;
        }
        return false;
    }

    /*
     * ActivityCompat.requestPermissions를 사용한 퍼미션 요청의 결과를 리턴받는 메소드입니다.
     */
    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {

        if (permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {

            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면

            boolean check_result = true;

            // 모든 퍼미션을 허용했는지 체크합니다.

            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }

            if (check_result) {
                // 퍼미션을 허용했다면 위치 업데이트를 시작합니다.
                startLocationUpdates();
            } else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.

                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {

                    // 사용자가 거부만 선택한 경우에는 앱을 다시 실행하여 허용을 선택하면 앱을 사용할 수 있습니다.
                    Snackbar.make(mLayout, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요. ",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {

                        @Override
                        public void onClick(View view) {

                            finish();
                        }
                    }).show();

                } else {
                    // "다시 묻지 않음"을 사용자가 체크하고 거부를 선택한 경우에는 설정(앱 정보)에서 퍼미션을 허용해야 앱을 사용할 수 있습니다.
                    Snackbar.make(mLayout, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인", new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            finish();
                        }
                    }).show();
                }
            }

        }
    }

    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                Intent callGPSSettingIntent
                        = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
            }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case GPS_ENABLE_REQUEST_CODE:
                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {
                        Log.d(TAG, "onActivityResult : GPS 활성화 되있음");
                        needRequest = true;
                        return;
                    }
                }
                break;
        }
    }
//제일                        검색할시설
//        게시판 안내사진 공원이름 주소 mainequip 설명
    public void makeMarker(String equi) {
        int size = getJsonToTag("latitude",equi);
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

        /*위에는 마커찍기용 밑에는 정보란 넘겨주기용*/

        getJsonToTag("p_addr",equi);                    //공원 주소
        pAddr = new String[size];
        for(int i=0;i<size;i++)
            pAddr[i] = (String)arrayList.get(i);


        getJsonToTag("guidance",equi);                    //안내사진주소
        pUrl = new String[size];
        for(int i=0;i<size;i++)
            pUrl[i] = ""+arrayList.get(i);


        getJsonToTag("main_equip",equi);                    //주 시설
        pEquip = new String[size];
        for(int i=0;i<size;i++)
            pEquip[i] = (String)arrayList.get(i);


        getJsonToTag("p_list_content",equi);                //공원설명
        pHelp = new String[size];
        for(int i=0;i<size;i++)
            pHelp[i] = (String)arrayList.get(i);


        for(int i=0;i<size;i++) {
            MarkerOptions markerOptions = new MarkerOptions();//addMarker() 인자값
            markerOptions.position(new LatLng(postn[0][i], postn[1][i]))          //좌표설정
            .title(pName[i])               //타이틀은 공원이름
            .snippet("상세정보 보기")           //서브타이틀은 공원주소
            .draggable(false)                //드래그 불가능하도록
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)) //색깔
            .alpha(0.7f);                   //투명도 설정
            mMap.addMarker(markerOptions);
        }
    }
}