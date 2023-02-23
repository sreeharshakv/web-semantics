package Model;

public class CrimeDO {

    private String drNo; // Division of Records Number: Official file number made up of a 2 digit year, area ID, and 5 digits
    private String dateReported;
    private String dateOccurred;
    private String area;
    private String areaName;
    private String[] crimeCodes;
    private String primaryCrimeDesc;
    private String[] moCodes; // see link: https://data.lacity.org/api/views/y8tr-7khq/files/3a967fbd-f210-4857-bc52-60230efe256c?download=true&filename=MO%20CODES%20(numerical%20order).pdf
    private String premise;
    private String premiseDesc;
    private String weapon;
    private String weaponDesc;
    private String status;
    private String statusDesc;
    private String location;
    private String crossStreet;
    private Double HPI;

    public CrimeDO(String[] record) {
//        DateFormat formatter = new SimpleDateFormat("d/MM/yy,HH:mm");
        this.setDrNo(record[0]);
//        this.setDateReported(formatter.parse(record[1]));
//        this.setDateOccurred(formatter.parse(record[2]));
        this.setDateReported(record[1]);
        this.setDateOccurred(record[2]);
//        this.getDateOccurred().setTime(Long.parseLong(record[3]));
        this.setArea(record[4]);
        this.setAreaName(record[5]);
        this.setCrimeCodes(new String[]{record[14], record[15], record[16], record[17]});
        this.setPrimaryCrimeDesc(record[6]);
        this.setMoCodes(record[7].split(" "));
        this.setPremise(record[8]);
        this.setPremiseDesc(record[9]);
        this.setWeapon(record[10]);
        this.setWeaponDesc(record[11]);
        this.setStatus(record[12]);
        this.setStatusDesc(record[13]);
        this.setLocation(record[18]);
        this.setCrossStreet(record[19]);
    }

    public String getDrNo() {
        return drNo;
    }

    public void setDrNo(String drNo) {
        this.drNo = drNo;
    }

    public String getDateReported() {
        return dateReported;
    }

    public void setDateReported(String dateReported) {
        this.dateReported = dateReported;
    }

    public String getDateOccurred() {
        return dateOccurred;
    }

    public void setDateOccurred(String dateOccurred) {
        this.dateOccurred = dateOccurred;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) {
        this.area = area;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String[] getCrimeCodes() {
        return crimeCodes;
    }

    public void setCrimeCodes(String[] crimeCodes) {
        this.crimeCodes = crimeCodes;
    }

    public String getPrimaryCrimeDesc() {
        return primaryCrimeDesc;
    }

    public void setPrimaryCrimeDesc(String primaryCrimeDesc) {
        this.primaryCrimeDesc = primaryCrimeDesc;
    }

    public String[] getMoCodes() {
        return moCodes;
    }

    public void setMoCodes(String[] moCodes) {
        this.moCodes = moCodes;
    }

    public String getPremise() {
        return premise;
    }

    public void setPremise(String premise) {
        this.premise = premise;
    }

    public String getPremiseDesc() {
        return premiseDesc;
    }

    public void setPremiseDesc(String premiseDesc) {
        this.premiseDesc = premiseDesc;
    }

    public String getWeapon() {
        return weapon;
    }

    public void setWeapon(String weapon) {
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
}
