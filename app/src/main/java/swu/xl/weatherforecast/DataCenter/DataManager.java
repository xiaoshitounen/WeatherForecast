package swu.xl.weatherforecast.DataCenter;

import java.util.ArrayList;
import java.util.List;

import swu.xl.weatherforecast.Baen.LocationWeather;

/**
 * 数据管理者
 */
public class DataManager {
    //数据
    private List<LocationWeather> beans;

    //单例模式
    private static DataManager instance = null;
    //私有化构造方法
    private DataManager(){
        loadData();
    }
    public static synchronized DataManager getDataManager(){
        if (instance == null){
            instance = new DataManager();
        }
        return instance;
    }

    //加载数据
    public void loadData() {
        List<LocationWeather> lists = new ArrayList<>();

        beans = lists;
    }

    //get方法
    public List<LocationWeather> getBeans() {

        return beans;
    }
    //set方法
    public void setBeans(List<LocationWeather> beans) {
        this.beans = beans;
    }
}
