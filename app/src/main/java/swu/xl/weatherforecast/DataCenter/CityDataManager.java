package swu.xl.weatherforecast.DataCenter;

import android.content.Context;

import java.util.List;

import swu.xl.weatherforecast.Baen.CityBean;

public class CityDataManager {
    //数据
    private List<CityBean> beans;

    //单例模式
    private static CityDataManager instance = null;
    //私有化构造方法
    private CityDataManager(Context context){
        loadData(context);
    }
    public static synchronized CityDataManager getDataManager(Context context){
        if (instance == null){
            instance = new CityDataManager(context);
        }
        return instance;
    }

    //加载数据
    public void loadData(Context context) {
        beans = DataUtil.loadCityDateByFile(context);
    }

    public List<CityBean> getBeans() {
        return beans;
    }

    public void setBeans(List<CityBean> beans) {
        this.beans = beans;
    }
}
