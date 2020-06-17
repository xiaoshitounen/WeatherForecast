package swu.xl.weatherforecast.Utils;

public class NowTemperatureUtil {
    /**
     * 分割字符串得到当前实时温度数据
     */
    public static String getNowTemperature(String data){
        //分割
        String[] split = data.split("：");

        //继续分割
        String result = split[1].substring(0,3);

        return result;
    }
}
