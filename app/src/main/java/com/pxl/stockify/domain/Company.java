package com.pxl.stockify.domain;

import java.util.ArrayList;

public class Company {
    private String companySymbol;
    private ArrayList<DailyPrice> companyStockPrices;

    public Company(String companySymbol, ArrayList<DailyPrice> companyStockPrices) {
        this.companySymbol = companySymbol;
        this.companyStockPrices = companyStockPrices;
    }

    public ArrayList<DailyPrice> getCompanyStockPrices() {
        return companyStockPrices;
    }

    public String getCompanySymbol() {
        return companySymbol;
    }
}
