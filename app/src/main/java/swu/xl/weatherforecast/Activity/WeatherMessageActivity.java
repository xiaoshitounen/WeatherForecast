package swu.xl.weatherforecast.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

import swu.xl.weatherforecast.Baen.LocationWeather;
import swu.xl.weatherforecast.DataCenter.DataManager;
import swu.xl.weatherforecast.R;
import swu.xl.weatherforecast.Utils.NowTemperatureUtil;
import swu.xl.xltoolbar.XLToolBar;

public class WeatherMessageActivity extends AppCompatActivity {
    //视图
    private LinearLayout index_layout;
    private LinearLayout day_layout;
    private ImageView wea_pic;
    private TextView wea_temperature;
    private TextView wea_wind;
    private TextView date;
    private TextView wea_wea;
    private TextView res_city;
    private TextView wea_data;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather_message);

        //获取模型数据
        Intent intent = getIntent();
        int position = intent.getIntExtra("position", 0);
        LocationWeather locationWeather = DataManager.getDataManager().getBeans().get(position);

        //绑定视图
        initView();

        //绑定数据
        bindView(locationWeather);
    }

    /**
     * 绑定视图
     */
    private void initView() {
        //层次1
        wea_data = findViewById(R.id.wea_data);
        res_city = findViewById(R.id.res_city);
        wea_wea = findViewById(R.id.wea_wea);

        date = findViewById(R.id.date);
        wea_wind = findViewById(R.id.wea_wind);
        wea_temperature = findViewById(R.id.wea_temperature);

        wea_pic = findViewById(R.id.wea_pic);

        //层次2
        day_layout = findViewById(R.id.day_message);

        //层次3
        index_layout = findViewById(R.id.index_message);

        //返回
        XLToolBar toolBar = findViewById(R.id.tool_bar);
        toolBar.getLeft_btn().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    /**
     * 绑定数据
     */
    private void bindView(LocationWeather bean) {
        //1.层次1
        {
            //1.1获取相关数据
            LocationWeather.ResultsBean resultsBean = bean.getResults().get(0);
            LocationWeather.ResultsBean.WeatherDataBean weatherDataBean = resultsBean.getWeather_data().get(0);

            //1.2设置相关数据
            wea_data.setText(NowTemperatureUtil.getNowTemperature(weatherDataBean.getDate()));
            res_city.setText(resultsBean.getCurrentCity());
            wea_wea.setText(weatherDataBean.getWeather());

            date.setText(bean.getDate());
            wea_wind.setText(weatherDataBean.getWind());
            wea_temperature.setText(weatherDataBean.getTemperature());

            Glide.with(this).load(weatherDataBean.getDayPictureUrl()).into(wea_pic);
        }

        //层次2
        {
            //1.1获取相关数据
            LocationWeather.ResultsBean resultsBean = bean.getResults().get(0);
            List<LocationWeather.ResultsBean.WeatherDataBean> weatherDataBeans = new ArrayList<>();
            for (int i = 1; i < resultsBean.getWeather_data().size(); i++) {
                weatherDataBeans.add(resultsBean.getWeather_data().get(i));
            }

            //1.2设置相关数据
            for (int i = 0; i < weatherDataBeans.size(); i++) {
                //1.2.0获取数据
                LocationWeather.ResultsBean.WeatherDataBean weatherDataBean = weatherDataBeans.get(i);

                //1.2.1加载布局
                View inflate = LayoutInflater.from(this).inflate(R.layout.weather_future_layout, null);

                //1.2.2找到控件
                TextView day_date = inflate.findViewById(R.id.day_date);
                TextView day_wea = inflate.findViewById(R.id.day_wea);
                ImageView day_pic = inflate.findViewById(R.id.day_pic);
                TextView day_temperature = inflate.findViewById(R.id.day_temperature);

                //1.2.3绑定数据
                day_date.setText(weatherDataBean.getDate());
                day_wea.setText(weatherDataBean.getWeather());
                Glide.with(this).load(weatherDataBean.getDayPictureUrl()).into(day_pic);
                day_temperature.setText(weatherDataBean.getTemperature());

                //1.2.4加入布局
                day_layout.addView(inflate);
            }
        }

        //层次3
        {
            //1.1获取相关数据
            LocationWeather.ResultsBean resultsBean = bean.getResults().get(0);
            List<LocationWeather.ResultsBean.IndexBean> indexBeans = resultsBean.getIndex();

            //1.2设置相关数据
            for (int i = 0; i < indexBeans.size(); i++) {
                //1.2.0获取数据
                LocationWeather.ResultsBean.IndexBean indexBean = indexBeans.get(i);

                //1.2.1加载布局
                View inflate = LayoutInflater.from(this).inflate(R.layout.weather_index_layout, null);

                //1.2.2找到控件
                TextView index_tipt = inflate.findViewById(R.id.index_tipt);
                TextView index_zs = inflate.findViewById(R.id.index_zs);
                TextView index_des = inflate.findViewById(R.id.index_des);

                //1.2.3绑定数据
                index_tipt.setText(indexBean.getTipt()+"：");
                index_zs.setText(indexBean.getZs());
                index_des.setText(indexBean.getDes());

                //1.2.4加入布局
                index_layout.addView(inflate);
            }
        }
    }
}