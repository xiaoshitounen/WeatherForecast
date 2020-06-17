package swu.xl.weatherforecast.Other;

import android.app.Application;
import android.content.Context;

import com.baidu.mapapi.CoordType;
import com.baidu.mapapi.SDKInitializer;

import swu.xl.weatherforecast.Activity.MainActivity;
import swu.xl.weatherforecast.DataCenter.CityDataManager;
import swu.xl.weatherforecast.DataCenter.DataManager;
import swu.xl.weatherforecast.db.DBManager;

/**
 * 做全局的操作
 */
public class XLApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        //在使用SDK各组件之前初始化context信息，传入ApplicationContext
        SDKInitializer.initialize(this);
        //自4.3.0起，百度地图SDK所有接口均支持百度坐标和国测局坐标，用此方法设置您使用的坐标类型.
        //包括BD09LL和GCJ02两种坐标，默认是BD09LL坐标。
        SDKInitializer.setCoordType(CoordType.BD09LL);

        //初始化单例类
        DataManager.getDataManager();
        CityDataManager.getDataManager(this);
        DBManager.getDataManager(this);
    }
}
