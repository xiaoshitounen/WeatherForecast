package swu.xl.weatherforecast.Utils;

import android.util.Log;

import com.google.gson.Gson;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import swu.xl.weatherforecast.Baen.LocationWeather;
import swu.xl.weatherforecast.Constant.Constant;

public class WeatherUtil {
    //OkHttpClient
    private final static OkHttpClient client = new OkHttpClient();

    /**
     * 添加城市天气信息
     * @param city
     */
    public static LocationWeather getCityWeatherMessage(String city) {
        //异步Get请求经城市所在天气数据
        //url
        String url = "https://api.map.baidu.com/telematics/v3/weather?location=" + city +
                "&output=json&ak=FkPhtMBK0HTIQNh7gG4cNUttSTyr0nzo";

        //请求
        Request.Builder builder = new Request.Builder();
        builder.url(url);
        Request request = builder.build();
        Log.d(Constant.TAG,url);

        //返回的数据
        LocationWeather locationWeather = null;

        try {
            //请求的响应
            Response response = client.newCall(request).execute();

            //成功的情况下
            if (response.isSuccessful()) {
                //读取响应体
                ResponseBody body = response.body();
                String jsonString = body.string();

                //解析
                Gson gson = new Gson();
                locationWeather = gson.fromJson(jsonString, LocationWeather.class);

                //获取城市信息
                String date = locationWeather.getDate();
                String city_name = locationWeather.getResults().get(0).getCurrentCity();

                Log.d(Constant.TAG,date+"-"+city_name);
            }else {

                Log.d(Constant.TAG,"响应失败");
            }
        } catch (IOException e) {
            e.printStackTrace();

            Log.d(Constant.TAG,"IO异常："+e.getMessage());
        }

        /*//异步请求转阻塞式同步请求
        final CountDownLatch countDownLatch = new CountDownLatch(1);

        //返回的数据
        final LocationWeather[] locationWeather = new LocationWeather[1];

        //回调
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
                    locationWeather[0] = gson.fromJson(jsonString, LocationWeather.class);

                    //获取城市信息
                    String date = locationWeather[0].getDate();
                    String city = locationWeather[0].getResults().get(0).getCurrentCity();

                    Log.d(Constant.TAG,date+"-"+city);

                    countDownLatch.countDown();
                }else {

                    Log.d(Constant.TAG,"响应失败:Response Failure");
                }
            }
        });*/

        return locationWeather;
    }
}
