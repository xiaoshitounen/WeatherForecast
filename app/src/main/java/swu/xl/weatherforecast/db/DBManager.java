package swu.xl.weatherforecast.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import swu.xl.weatherforecast.Baen.DataBaseBean;

public class DBManager {
    //数据库database
    public static SQLiteDatabase database;

    //单例模式
    private static DBManager instance = null;
    //私有化构造方法
    private DBManager(Context context){
        initDB(context);
    }
    public static synchronized DBManager getDataManager(Context context){
        if (instance == null){
            instance = new DBManager(context);
        }
        return instance;
    }

    //初始化数据库信息
    public static void initDB(Context context){
        DBHelper dbHelper = new DBHelper(context);
        database = dbHelper.getWritableDatabase();
    }

    /**
     * 查询数据库中的所有城市信息
     */
    public List<DataBaseBean> queryAllCity(){
        //1.查找出所有数据的集合
        Cursor cursor = database.query(DBHelper.INFO, null, null, null, null, null, null);

        //2.创建集合用于存储数据
        List<DataBaseBean> dataBeans = new ArrayList<>();

        //3.装入集合中
        while (cursor.moveToNext()){
            int id = cursor.getInt(cursor.getColumnIndex(DBHelper.INFO_ID));
            String city = cursor.getString(cursor.getColumnIndex(DBHelper.INFO_CITY));

            dataBeans.add(new DataBaseBean(id,city));
        }

        //释放
        cursor.close();

        return dataBeans;
    }

    /**
     * 新增一条城市记录
     */
    public long updateCity(String city){
        //1.创建数据集合
        ContentValues values = new ContentValues();
        values.put(DBHelper.INFO_CITY,city);

        //2.插入数据
        return database.insert(DBHelper.INFO, null,values);
    }
}
