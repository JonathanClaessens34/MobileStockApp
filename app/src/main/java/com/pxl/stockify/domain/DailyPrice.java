package com.pxl.stockify.domain;

public class DailyPrice {
    private String dailyDate;
    private double dailyOpen;
    private double dailyHigh;
    private double dailyLow;
    private double dailyClose;
    private int dailyVolume;

    public DailyPrice(String dailyDate, double dailyOpen, double dailyHigh, double dailyLow, double dailyClose, int dailyVolume) {
        this.dailyDate = dailyDate;
        this.dailyOpen = dailyOpen;
        this.dailyHigh = dailyHigh;
        this.dailyLow = dailyLow;
        this.dailyClose = dailyClose;
        this.dailyVolume = dailyVolume;
    }

    public String getDailyDate() {
        return dailyDate;
    }

    public double getDailyOpen() {
        return dailyOpen;
    }

    public double getDailyHigh() {
        return dailyHigh;
    }

    public double getDailyLow() {
        return dailyLow;
    }

    public double getDailyClose() {
        return dailyClose;
    }

    public int getDailyVolume() {
        return dailyVolume;
    }
}
