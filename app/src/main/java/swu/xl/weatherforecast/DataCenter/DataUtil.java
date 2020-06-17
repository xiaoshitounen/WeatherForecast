package swu.xl.weatherforecast.DataCenter;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import okhttp3.OkHttp;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import swu.xl.weatherforecast.Baen.CityBean;
import swu.xl.weatherforecast.Baen.LocationWeather;
import swu.xl.weatherforecast.Constant.Constant;
import swu.xl.weatherforecast.Utils.PinyinUtil;
import swu.xl.weatherforecast.db.DBManager;

/**
 * 数据加载工具类
 */
public class DataUtil {

    /**
     * 从网络上加载城市数据
     * @return
     */
    public static List<CityBean> loadCityDateByFile(Context context) {
        //存储数据
        final List<CityBean> cityBeans = new ArrayList<>();

        try {
            //获取JSON文件的输入流
            InputStream is = context.getResources().getAssets().open("json/citys.json");

            //解析JSON字符串
            String jsonString = getStringByInputStream(is);

            //解析
            JSONArray jsonArray = new JSONArray(jsonString);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject cityJson = (JSONObject) jsonArray.get(i);

                Gson gson = new Gson();
                CityBean cityBean = gson.fromJson(String.valueOf(cityJson), CityBean.class);

                cityBeans.add(cityBean);
            }

            Log.d(Constant.TAG,"城市的个数"+cityBeans.size());
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        //加上pinyin
        for (CityBean cityBean : cityBeans) {
            cityBean.setPinyin(PinyinUtil.getPingYin(cityBean.getName()));
        }

        //排序
        Collections.sort(cityBeans);

        return cityBeans;
        /*//OkHttpClient
        final OkHttpClient client = new OkHttpClient();

        //存储数据
        final List<CityBean> cityBeans = new ArrayList<>();

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                //异步Get请求经城市所在天气数据
                //url
                String url = "https://s5.aconvert.com/convert/p3r68-cdx67/wm72c-k074c.json";

                //请求
                Request.Builder builder = new Request.Builder();
                builder.url(url);
                Request request = builder.build();
                Log.d(Constant.TAG,url);

                try {
                    //请求的响应
                    Response response = client.newCall(request).execute();

                    //成功的情况下
                    if (response.isSuccessful()) {
                        //读取响应体
                        ResponseBody body = response.body();
                        String jsonString = body.string();

                        //解析
                        JSONArray jsonArray = new JSONArray(jsonString);
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject cityJson = (JSONObject) jsonArray.get(i);

                            Gson gson = new Gson();
                            CityBean cityBean = gson.fromJson(String.valueOf(cityJson), CityBean.class);

                            cityBeans.add(cityBean);
                        }

                        Log.d(Constant.TAG,"城市的个数"+cityBeans.size());
                    }else {

                        Log.d(Constant.TAG,"响应失败");
                    }
                } catch (IOException e) {
                    e.printStackTrace();

                    Log.d(Constant.TAG,"IO异常："+e.getMessage());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        thread.start();

        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return cityBeans;*/
    }

    /**
     * 将输入流->JSON字符串
     * @param is
     * @return
     */
    private static String getStringByInputStream(InputStream is) {
        String result;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        byte[] data = new byte[1024];
        int len = 0;

        try {
            //一直读取
            while ((len = is.read(data)) != -1){
                bos.write(data,0,len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        result = new String(bos.toByteArray());

        return result;
    }
}

