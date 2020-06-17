package swu.xl.weatherforecast.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {
    //表名
    public static String INFO = "info";
    //列明
    public static String INFO_ID = "_id";
    public static String INFO_CITY = "city";


    public DBHelper(@Nullable Context context) {
        super(context, "city.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //创建表
        String sql = "create table " + INFO + "(" + INFO_ID + " integer primary key autoincrement," + INFO_CITY + " varchar(20) unique not null)";
        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
