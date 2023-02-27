package Model;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CrimeDO {

    private String drNo; // Division of Records Number: Official file number made up of a 2 digit year, area ID, and 5 digits
    private Date dateReported;
    private Date dateOccurred;
    private int area;
    private String areaName;
    private List<Integer> crimeCodes;
    private String primaryCrimeDesc;
    private List<Integer> moCodes; // see link: https://data.lacity.org/api/views/y8tr-7khq/files/3a967fbd-f210-4857-bc52-60230efe256c?download=true&filename=MO%20CODES%20(numerical%20order).pdf
    private int premise;
    private String premiseDesc;
    private int weapon;
    private String weaponDesc;
    private String status;
    private String statusDesc;
    private String location;
    private String crossStreet;
    private Double HPI;
    boolean isValid = true;

    public CrimeDO(String[] record) {
        try {
            DateFormat formatter = new SimpleDateFormat("MM/d/yy hh:mm:ss");
            this.setDrNo(record[0]);
            this.setDateReported(formatter.parse(record[1]));
            this.setDateOccurred(formatter.parse(record[2]));
            this.setArea(record[4].equals("") ? 0 : Integer.parseInt(record[4]));
            this.setAreaName(record[5]);
            this.setCrimeCodes(new String[]{record[14], record[15], record[16], record[17]});
            this.setPrimaryCrimeDesc(record[6]);
            this.setMoCodes(record[7].split(" "));
            this.setPremise(record[8].equals("") ? 0 :Integer.parseInt(record[8]));
            this.setPremiseDesc(record[9]);
            this.setWeapon(record[10].equals("") ? 0 : Integer.parseInt(record[10]));
            this.setWeaponDesc(record[11]);
            this.setStatus(record[12]);
            this.setStatusDesc(record[13]);
            this.setLocation(record[18].replaceAll(" +", " "));
            this.setCrossStreet(record[19].replaceAll(" +", " "));
        } catch (Exception e) {
            this.setValid(false);
        }
    }

    public String getDrNo() {
        return drNo;
    }

    public void setDrNo(String drNo) {
        this.drNo = drNo;
    }

    public Date getDateReported() {
        return dateReported;
    }

    public void setDateReported(Date dateReported) {
        this.dateReported = dateReported;
    }

    public Date getDateOccurred() {
        return dateOccurred;
    }

    public void setDateOccurred(Date dateOccurred) {
        this.dateOccurred = dateOccurred;
    }

    public int getArea() {
        return area;
    }

    public void setArea(int area) {
        this.area = area;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public List<Integer> getCrimeCodes() {
        return crimeCodes;
    }

    public void setCrimeCodes(String[] crimeCodes) {
        List<Integer> temp = new ArrayList<>();
        for(String crimeCode: crimeCodes) {
            if(!crimeCode.trim().equals("")) {
                temp.add(Integer.parseInt(crimeCode));
            }
        }

        this.crimeCodes = temp;
    }

    public String getPrimaryCrimeDesc() {
        return primaryCrimeDesc;
    }

    public void setPrimaryCrimeDesc(String primaryCrimeDesc) {
        this.primaryCrimeDesc = primaryCrimeDesc;
    }

    public List<Integer> getMoCodes() {
        return moCodes;
    }

    public void setMoCodes(String[] moCodes) {
        List<Integer> temp = new ArrayList<>();
        for(String moCode: moCodes) {
            if(!moCode.trim().equals("")) {
                temp.add(Integer.parseInt(moCode));
            }
        }

        this.moCodes = temp;
    }

    public int getPremise() {
        return premise;
    }

    public void setPremise(int premise) {
        this.premise = premise;
    }

    public String getPremiseDesc() {
        return premiseDesc;
    }

    public void setPremiseDesc(String premiseDesc) {
        this.premiseDesc = premiseDesc;
    }

    public int getWeapon() {
        return weapon;
    }

    public void setWeapon(int weapon) {
        this.weapon = weapon;
    }

    public String getWeaponDesc() {
        return weaponDesc;
    }

    public void setWeaponDesc(String weaponDesc) {
        this.weaponDesc = weaponDesc;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatusDesc() {
        return statusDesc;
    }

    public void setStatusDesc(String statusDesc) {
        this.statusDesc = statusDesc;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getCrossStreet() {
        return crossStreet;
    }

    public void setCrossStreet(String crossStreet) {
        this.crossStreet = crossStreet;
    }

    public Double getHPI() {
        return HPI;
    }

    public void setHPI(Double HPI) {
        this.HPI = HPI;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean valid) {
        isValid = valid;
    }
}
