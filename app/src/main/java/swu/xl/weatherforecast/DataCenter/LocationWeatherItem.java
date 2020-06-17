package swu.xl.weatherforecast.DataCenter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import swu.xl.weatherforecast.Baen.LocationWeather;
import swu.xl.weatherforecast.R;
import swu.xl.weatherforecast.Utils.NowTemperatureUtil;

public class LocationWeatherItem {
    //item对应的模型
    private LocationWeather bean;
    //item对应的视图
    private int layout;

    //存储关联好的视图
    private View item_view;

    /**
     * 构造方法
     */
    public LocationWeatherItem(LocationWeather bean, Context context, View convertView) {
        this.bean = bean;
        this.layout = R.layout.weather_main_layout;

        //关联
        initView(context,convertView);
    }

    //将模型和视图关联
    private void initView(Context context,View convertView){
        //1.获取布局
        ViewHolder viewHolder;
        if (convertView == null){
            //2.获取子视图

            //加载布局
            convertView = LayoutInflater.from(context).inflate(layout, null);
            //完善ViewHolder
            viewHolder = new ViewHolder();
            viewHolder.city = convertView.findViewWithTag(context.getResources().getString(R.string.ItemCity));
            viewHolder.weather = convertView.findViewWithTag(context.getResources().getString(R.string.ItemWeather));
            viewHolder.temperature = convertView.findViewWithTag(context.getResources().getString(R.string.ItemTemperature));
            viewHolder.now_temperature = convertView.findViewWithTag(context.getResources().getString(R.string.ItemNowTemperature));
            viewHolder.location = convertView.findViewWithTag(context.getResources().getString(R.string.location));

            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (bean != null){
            //3.设置数据
            LocationWeather.ResultsBean resultsBean = bean.getResults().get(0);
            viewHolder.city.setText(resultsBean.getCurrentCity());

            LocationWeather.ResultsBean.WeatherDataBean weatherDataBean = resultsBean.getWeather_data().get(0);
            viewHolder.weather.setText(weatherDataBean.getWeather());
            viewHolder.temperature.setText(weatherDataBean.getTemperature());
            viewHolder.now_temperature.setText(NowTemperatureUtil.getNowTemperature(weatherDataBean.getDate()));
            if (bean.isLocation()){
                viewHolder.location.setVisibility(View.VISIBLE);
            }else {
                viewHolder.location.setVisibility(View.INVISIBLE);
            }
        }

        //4.保存视图
        item_view = convertView;
    }

    /**
     * 内部类：ViewHolder
     */
    private class ViewHolder{
        public TextView city;
        public TextView weather;
        public TextView temperature;
        public TextView now_temperature;

        public ImageView location;
    }

    //get方法
    public View getItem_view() {
        return item_view;
    }
}
