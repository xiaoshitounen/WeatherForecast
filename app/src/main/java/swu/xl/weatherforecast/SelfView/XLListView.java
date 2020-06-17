package swu.xl.weatherforecast.SelfView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import swu.xl.weatherforecast.Baen.LocationWeather;
import swu.xl.weatherforecast.DataCenter.DataManager;
import swu.xl.weatherforecast.DataCenter.LocationWeatherItem;

public class XLListView extends ListView {
    //适配器
    private MyAdapter adapter;

    /**
     * 构造方法：Java代码初始化
     * @param context
     */
    public XLListView(Context context) {
        super(context);

        //初始化操作
        initData();
    }

    /**
     * 构造方法：Xml代码初始化
     * @param context
     * @param attrs
     */
    public XLListView(Context context, AttributeSet attrs) {
        super(context, attrs);

        //初始化操作
        initData();
    }

    /**
     * 初始化操作-设置适配器
     */
    private void initData() {
        adapter = new MyAdapter();
        setAdapter(adapter);
    }

    /**
     * 自定义的适配器
     */
    public class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return DataManager.getDataManager().getBeans().size();
        }

        @Override
        public Object getItem(int position) {
            return DataManager.getDataManager().getBeans().get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            //获取模型
            LocationWeather bean = DataManager.getDataManager().getBeans().get(position);
            //绑定视图和模型
            LocationWeatherItem item = new LocationWeatherItem(bean, getContext(), convertView);

            return item.getItem_view();
        }
    }

    //set,get方法
    @Override
    public MyAdapter getAdapter() {
        return adapter;
    }
}
