package Model;

import java.util.HashMap;

public class HousingPriceDO {

    private final String[] timestamps = {"1/10", "2/10", "3/10", "4/10", "5/10", "6/10", "7/10", "8/10", "9/10", "10/10", "11/10", "12/10", "1/11", "2/11", "3/11", "4/11", "5/11", "6/11", "7/11", "8/11", "9/11", "10/11", "11/11", "12/11", "1/12", "2/12", "3/12", "4/12", "5/12", "6/12", "7/12", "8/12", "9/12", "10/12", "11/12", "12/12", "1/13", "2/13", "3/13", "4/13", "5/13", "6/13", "7/13", "8/13", "9/13", "10/13", "11/13", "12/13", "1/14", "2/14", "3/14", "4/14", "5/14", "6/14", "7/14", "8/14", "9/14", "10/14", "11/14", "12/14", "1/15", "2/15", "3/15", "4/15", "5/15", "6/15", "7/15", "8/15", "9/15", "10/15", "11/15", "12/15", "1/16", "2/16", "3/16", "4/16", "5/16", "6/16", "7/16", "8/16", "9/16", "10/16", "11/16", "12/16", "1/17", "2/17", "3/17", "4/17", "5/17", "6/17", "7/17", "8/17", "9/17", "10/17", "11/17", "12/17", "1/18", "2/18", "3/18", "4/18", "5/18", "6/18", "7/18", "8/18", "9/18", "10/18", "11/18", "12/18", "1/19", "2/19", "3/19", "4/19", "5/19", "6/19", "7/19", "8/19", "9/19", "10/19", "11/19", "12/19", "1/20", "2/20", "3/20", "4/20", "5/20", "6/20", "7/20", "8/20", "9/20", "10/20", "11/20", "12/20", "1/21", "2/21", "3/21", "4/21", "5/21", "6/21", "7/21", "8/21", "9/21", "10/21", "11/21", "12/21", "1/22", "2/22", "3/22", "4/22", "5/22", "6/22", "7/22", "8/22", "9/22", "10/22", "11/22", "12/22", "1/23"};

    public HousingPriceDO(String[] record) {
        this.setRegionID(Long.valueOf(record[0]));
        this.setRegionName(record[1]);
        this.setState(record[2]);
        this.setCity(record[3]);
        this.setHPI(record);
    }

    private Long regionID;
    private String regionName;
    private String state;
    private String city;

    private HashMap<String, Double> HPI;

    public Long getRegionID() {
        return regionID;
    }

    public void setRegionID(Long regionID) {
        this.regionID = regionID;
    }

    public String getRegionName() {
        return regionName;
    }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public HashMap<String, Double> getHPI() {
        return HPI;
    }

    public void setHPI(String[] HPIRecord) {
        HashMap<String, Double> data = new HashMap<>();
        for (int i = 0; i < timestamps.length; i++) {
            data.put(timestamps[i], Double.valueOf(HPIRecord[i+4]));
        }
        this.HPI = data;
    }
}
