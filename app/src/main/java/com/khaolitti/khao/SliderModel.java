package com.khaolitti.khao;

public class SliderModel {
    private  String banner;

  private  String backgroundcolr;

    public SliderModel(String banner, String backgroundcolr) {
        this.banner = banner;
        this.backgroundcolr = backgroundcolr;
    }

    public String getBanner() {
        return banner;
    }

    public void setBanner(String banner) {
        this.banner = banner;
    }

    public String getBackgroundcolr() {
        return backgroundcolr;
    }

    public void setBackgroundcolr(String backgroundcolr) {
        this.backgroundcolr = backgroundcolr;
    }
}
