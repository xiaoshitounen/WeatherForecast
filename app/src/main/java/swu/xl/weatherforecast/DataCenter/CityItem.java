package swu.xl.weatherforecast.DataCenter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import swu.xl.weatherforecast.Baen.CityBean;
import swu.xl.weatherforecast.R;

public class CityItem {
    //item对应的模型
    private CityBean bean;
    //item对应的视图
    private int layout;

    //存储关联好的视图
    private View item_view;

    /**
     * 构造方法
     * @param bean
     * @param context
     * @param convertView
     */
    public CityItem(CityBean bean, Context context, View convertView) {
        this.bean = bean;
        this.layout = R.layout.city_show_layout;

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
            viewHolder.city = convertView.findViewWithTag(context.getResources().getString(R.string.CityName));

            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        if (bean != null){
            //3.设置数据
            viewHolder.city.setText(bean.getName());
        }

        //4.保存视图
        item_view = convertView;
    }

    /**
     * 内部类：ViewHolder
     */
    private class ViewHolder{
        public TextView city;
    }

    //get方法
    public View getItem_view() {
        return item_view;
    }
}
