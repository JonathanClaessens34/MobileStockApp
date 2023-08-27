package com.pxl.stockify.domain;

import com.anychart.AnyChart;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.HighLowDataEntry;
import com.anychart.charts.Stock;
import com.anychart.core.axes.StockDateTime;
import com.anychart.core.stock.Plot;
import com.anychart.core.stock.series.OHLC;
import com.anychart.core.ui.LabelsFactory;
import com.anychart.data.Table;
import com.anychart.data.TableMapping;
import com.anychart.enums.StockSeriesType;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class LoadChartClass {
    public LoadChartClass(){}

    public Stock loadAnyChart(Company equity, String equityName){

        ArrayList<DailyPrice> indexDailyPrices = equity.getCompanyStockPrices();

        Table table = Table.instantiate("x");
        List<DataEntry> data = new ArrayList<>();
        try {
            for (int j = 0; j < indexDailyPrices.size(); j++) {
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                Date date = formatter.parse(indexDailyPrices.get(j).getDailyDate());
                long dateLong = date.getTime();
                if(indexDailyPrices.get(j).getDailyClose() != 0){  //avoid false data
                    data.add(new OHCLDataEntry(
                            dateLong,
                            indexDailyPrices.get(j).getDailyOpen(),
                            indexDailyPrices.get(j).getDailyHigh(),
                            indexDailyPrices.get(j).getDailyLow(),
                            indexDailyPrices.get(j).getDailyClose(),
                            indexDailyPrices.get(j).getDailyVolume()));
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }

        table.addData(data);
        TableMapping mapping = table.mapAs("{open: 'open', high: 'high', low: 'low', close: 'close'}");
        Stock stock = AnyChart.stock();
        Plot plot = stock.plot(0);

        plot.yGrid(true);
        plot.xGrid(true);

        plot.xMinorGrid(true);
        plot.yMinorGrid(true);

        StockDateTime xAxis = plot.xAxis();
        xAxis.labels().fontColor("#000000");
        xAxis.labels().fontSize(12);
        xAxis.labels().format("${%value}");

        LabelsFactory yAxis = plot.yAxis(0).labels();
        yAxis.fontSize(12);
        yAxis.fontColor("#000000");
        yAxis.textDirection();

        plot.title(equityName);
        plot.title().fontColor("#000000");
        plot.title().fontSize(20);

        plot.legend().fontColor("#000000");
        plot.legend().fontSize(16);
        plot.legend().fontWeight(600);
        plot.legend().enabled(false);

        OHLC series = plot.ohlc(mapping).name(equity.getCompanySymbol());
        series.fallingStroke("#FF0000", 6, "6 3", "miter", "butt");
        series.risingStroke("#008000",6, "6 3", "miter", "butt");
        stock.scroller().ohlc(mapping);
        series.enabled(true);

        plot.ema(table.mapAs("{value: 'close'}"), 20d, StockSeriesType.LINE);

        return stock;
    }

    private class OHCLDataEntry extends HighLowDataEntry {
        OHCLDataEntry(long x, double open, double high, double low, double close, int volume){
            super(x, high, low);
            setValue("open", open);
            setValue("high", high);
            setValue("low", low);
            setValue("close", close);
            setValue("volume", volume);
        }
    }
}
