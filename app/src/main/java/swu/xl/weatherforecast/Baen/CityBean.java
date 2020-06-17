package swu.xl.weatherforecast.Baen;

public class CityBean implements Comparable<CityBean> {
    /**
     * area_id : 33
     * name : 嘉峪关市
     */
    private String area_id;
    private String name;

    //添加属性：拼音
    private String pinyin;

    public String getArea_id() {
        return area_id;
    }

    public void setArea_id(String area_id) {
        this.area_id = area_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    @Override
    public String toString() {
        return "CityBean{" +
                "area_id='" + area_id + '\'' +
                ", name='" + name + '\'' +
                ", pinyin='" + pinyin + '\'' +
                '}';
    }

    @Override
    public int compareTo(CityBean o) {
        return this.pinyin.compareTo(o.pinyin);
    }
}
