package swu.xl.weatherforecast.Baen;

public class DataBaseBean {
    private int id;
    private String city;

    public DataBaseBean() {
    }

    public DataBaseBean(int id, String city) {
        this.id = id;
        this.city = city;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}
