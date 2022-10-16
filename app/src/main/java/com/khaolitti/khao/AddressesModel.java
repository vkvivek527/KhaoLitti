package com.khaolitti.khao;

public class AddressesModel {

    private Boolean selected;
    private String city;
    private String locality;
    private String flatno;
    private String pincode;
    private String landMark;
    private String name;
    private String mobileNo;
    private String alternateMoNo;
    private String state;

    public AddressesModel(Boolean selected, String city, String locality, String flatno, String pincode, String landMark, String name, String mobileNo, String alternateMoNo, String state) {
        this.selected = selected;
        this.city = city;
        this.locality = locality;
        this.flatno = flatno;
        this.pincode = pincode;
        this.landMark = landMark;
        this.name = name;
        this.mobileNo = mobileNo;
        this.alternateMoNo = alternateMoNo;
        this.state = state;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getFlatno() {
        return flatno;
    }

    public void setFlatno(String flatno) {
        this.flatno = flatno;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getLandMark() {
        return landMark;
    }

    public void setLandMark(String landMark) {
        this.landMark = landMark;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobileNo() {
        return mobileNo;
    }

    public void setMobileNo(String mobileNo) {
        this.mobileNo = mobileNo;
    }

    public String getAlternateMoNo() {
        return alternateMoNo;
    }

    public void setAlternateMoNo(String alternateMoNo) {
        this.alternateMoNo = alternateMoNo;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
