package swu.xl.weatherforecast.Activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import swu.xl.weatherforecast.Baen.DataBaseBean;
import swu.xl.weatherforecast.Baen.LocationMessage;
import swu.xl.weatherforecast.Baen.LocationWeather;
import swu.xl.weatherforecast.Constant.Constant;
import swu.xl.weatherforecast.DataCenter.DataManager;
import swu.xl.weatherforecast.R;
import swu.xl.weatherforecast.SelfView.XLListView;
import swu.xl.weatherforecast.Utils.WeatherUtil;
import swu.xl.weatherforecast.db.DBManager;

public class MainActivity extends AppCompatActivity {
    //OkHttpClient
    private final OkHttpClient client = new OkHttpClient();

    //适配器
    private XLListView.MyAdapter adapter;

    //数据回调的代码标志
    private int CITY_RESULT_CODE = 10086;

    //刷新的控件
    private SwipeRefreshLayout refresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //权限申请
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            Toast.makeText(getApplicationContext(),"没有权限,请手动开启定位权限",Toast.LENGTH_SHORT).show();

            //申请一个（或多个）权限，并提供用于回调返回的获取码（用户定义）
            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        }

        //初始化操作
        init();

        //开始刷新
        refresh.setRefreshing(true);

        //定位
        initLocationOption();
    }

    /**
     * 初始化View
     */
    private void init() {
        //搜索更多的城市
        findViewById(R.id.main_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到搜索城市的界面
                Intent intent = new Intent(MainActivity.this, CitySearchActivity.class);
                startActivityForResult(intent, CITY_RESULT_CODE);
            }
        });

        //ListView
        ListView listView = findViewById(R.id.list);
        adapter = (XLListView.MyAdapter) listView.getAdapter();

        //具体天气情况
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //跳转到具体天气信息的界面
                Intent intent = new Intent(MainActivity.this, WeatherMessageActivity.class);
                intent.putExtra("position",position);
                startActivity(intent);
            }
        });

        //刷新控件
        refresh = findViewById(R.id.refresh);

        //监听下拉刷新
        refresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                //取消刷新
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (refresh.isRefreshing()) {
                            refresh.setRefreshing(false);
                        }
                    }
                }, 500);
            }
        });
    }

    /**
     * 添加城市天气信息
     * @param city
     */
    private void addCityWeatherMessage(final String city, final boolean location) {
        //线程
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                //同步Get请求经城市所在天气数据
                LocationWeather cityWeather = WeatherUtil.getCityWeatherMessage(city);
                cityWeather.setLocation(location);

                //添加数据
                //DataManager.getDataManager().getBeans().clear();
                DataManager.getDataManager().getBeans().add(cityWeather);

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //刷新数据
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });

        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * 回调事件
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //判断城市信息的回调
        if (requestCode == CITY_RESULT_CODE && resultCode == RESULT_OK) {
            //获取数据
            assert data != null;
            String city = data.getStringExtra("city");

            //测试数据
            Log.d(Constant.TAG, "想要查找的城市是："+city);

            //判断是否加载过
            List<LocationWeather> beans = DataManager.getDataManager().getBeans();

            for (LocationWeather bean : beans) {

                if (TextUtils.equals(bean.getResults().get(0).getCurrentCity(),city)){
                    Toast.makeText(this, "该城市数据已经显示在列表中了", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            //添加到首页
            addCityWeatherMessage(city,false);

            //添加到数据库中
            DBManager.getDataManager(this).updateCity(city);
        }
    }

    /**
     * 初始化定位参数配置
     */
    private void initLocationOption() {
        //定位服务的客户端。宿主程序在客户端声明此类，并调用，目前只支持在主线程中启动
        LocationClient locationClient = new LocationClient(getApplicationContext());
        //声明LocationClient类实例并配置定位参数
        LocationClientOption locationOption = new LocationClientOption();
        MyLocationListener myLocationListener = new MyLocationListener();
        //注册监听函数
        locationClient.registerLocationListener(myLocationListener);
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        locationOption.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);
        //可选，默认gcj02，设置返回的定位结果坐标系，如果配合百度地图使用，建议设置为bd09ll;
        locationOption.setCoorType("gcj02");
        //可选，默认0，即仅定位一次，设置发起连续定位请求的间隔需要大于等于1000ms才是有效的
        locationOption.setScanSpan(1000);
        //可选，设置是否需要地址信息，默认不需要
        locationOption.setIsNeedAddress(true);
        //可选，设置是否需要地址描述
        locationOption.setIsNeedLocationDescribe(true);
        //可选，设置是否需要设备方向结果
        locationOption.setNeedDeviceDirect(false);
        //可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        locationOption.setLocationNotify(true);
        //可选，默认true，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认不杀死
        locationOption.setIgnoreKillProcess(true);
        //可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        locationOption.setIsNeedLocationDescribe(true);
        //可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        locationOption.setIsNeedLocationPoiList(true);
        //可选，默认false，设置是否收集CRASH信息，默认收集
        locationOption.SetIgnoreCacheException(false);
        //可选，默认false，设置是否开启Gps定位
        locationOption.setOpenGps(true);
        //可选，默认false，设置定位时是否需要海拔信息，默认不需要，除基础定位版本都可用
        locationOption.setIsNeedAltitude(false);
        //设置打开自动回调位置模式，该开关打开后，期间只要定位SDK检测到位置变化就会主动回调给开发者，该模式下开发者无需再关心定位间隔是多少，定位SDK本身发现位置变化就会及时回调给开发者
        locationOption.setOpenAutoNotifyMode();
        //设置打开自动回调位置模式，该开关打开后，期间只要定位SDK检测到位置变化就会主动回调给开发者
        locationOption.setOpenAutoNotifyMode(3000,1, LocationClientOption.LOC_SENSITIVITY_HIGHT);
        //需将配置好的LocationClientOption对象，通过setLocOption方法传递给LocationClient对象使用
        locationClient.setLocOption(locationOption);
        //开始定位
        locationClient.start();
    }

    /**
     * 实现定位回调
     */
    public class MyLocationListener extends BDAbstractLocationListener{
        @Override
        public void onReceiveLocation(BDLocation location){
            //此处的BDLocation为定位结果信息类，通过它的各种get方法可获取定位相关的全部结果
            //以下只列举部分获取经纬度相关（常用）的结果信息
            //更多结果信息获取说明，请参照类参考中BDLocation类中的说明

            //获取纬度信息
            double latitude = location.getLatitude();
            //获取经度信息
            double longitude = location.getLongitude();
            //获取定位精度，默认值为0.0f
            float radius = location.getRadius();
            //获取经纬度坐标类型，以LocationClientOption中设置过的坐标类型为准
            String coorType = location.getCoorType();
            //获取定位类型、定位错误返回码，具体信息可参照类参考中BDLocation类中的说明
            int errorCode = location.getLocType();

            Log.d(Constant.TAG,"纬度："+latitude+" 经度："+longitude);

            //异步Get请求经纬度所在城市数据
            asyncRequestCityByGet(latitude,longitude);
        }

        /**
         * 异步Get请求
         * @param latitude
         * @param longitude
         */
        private void asyncRequestCityByGet(double latitude, double longitude) {
            //url
            String url = "https://api.map.baidu.com/geocoder?output=json&location=" +
                    latitude+","+longitude +
                    "&ak=esNPFDwwsXWtsQfw4NMNmur1";

            //请求
            Request.Builder builder = new Request.Builder();
            builder.url(url);
            Request request = builder.build();
            Log.d(Constant.TAG,url);

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    Log.d(Constant.TAG,"响应失败:Failure");
                    Log.d(Constant.TAG,e.getMessage());
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    //成功的情况下
                    if (response.isSuccessful()) {
                        //读取响应头
                        Headers headers = response.headers();
                        for (int i = 0; i < headers.size(); i++) {
                            Log.d(Constant.TAG,headers.name(i)+":"+headers.value(i));
                        }

                        //读取响应体
                        ResponseBody body = response.body();
                        String jsonString = body.string();

                        //解析
                        Gson gson = new Gson();
                        LocationMessage locationMessage = gson.fromJson(jsonString, LocationMessage.class);

                        //获取城市信息
                        String city = locationMessage.getResult().getAddressComponent().getCity();
                        Log.d(Constant.TAG, city);

                        //添加定位城市的数据
                        addCityWeatherMessage(city,true);

                        //查询数据库中的数据
                        List<DataBaseBean> city_sql = DBManager.getDataManager(MainActivity.this).queryAllCity();
                        for (DataBaseBean dataBaseBean : city_sql) {

                            //添加定位城市的数据
                            addCityWeatherMessage(dataBaseBean.getCity(),false);
                        }

                        //停止刷新
                        refresh.setRefreshing(false);

                    }else {

                        Log.d(Constant.TAG,"响应失败:Response Failure");
                    }
                }
            });
        }
    }
}