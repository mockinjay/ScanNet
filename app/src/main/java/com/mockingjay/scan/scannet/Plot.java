package com.mockingjay.scan.scannet;

/**
 * Created by mockingjay on 5/2/17.
 */
import android.app.Activity;
import android.graphics.*;
import android.os.Bundle;

import com.androidplot.util.PixelUtils;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.*;

import java.text.FieldPosition;
import java.text.Format;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.*;

/**
 * A simple XYPlot
 */
public class Plot extends Activity {

    private XYPlot plot;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plot_activity);

        ArrayList<Point> data = (ArrayList<Point>) getIntent().getSerializableExtra("data");

        double[] xValues = new double[data.size()];
        double[] yValues = new double[data.size()];
        int contor = 0;
        for (Point dPoint : data) {
            xValues[contor] = dPoint.getElement(0);
            yValues[contor] = dPoint.getElement(1);
            contor++;
        }

        Number[] xNumbers = new Number[data.size()];
        Number[] yNumbers = new Number[data.size()];

        for (int i = 0; i < contor; i++) {
            xNumbers[i] = (Number) xValues[i];
            yNumbers[i] = (Number) yValues[i];
        }

        plot = (XYPlot) findViewById(R.id.plot);

        BubbleSeries series1 = new BubbleSeries(
                Arrays.asList(xNumbers),
                Arrays.asList(yNumbers), "s1"
        );

        plot.setDomainBoundaries(-1, 1, BoundaryMode.FIXED);
        plot.setRangeBoundaries(0, 1, BoundaryMode.FIXED);

        BubbleFormatter bf1 = new BubbleFormatter(this, R.xml.bubble_formatter1);
//        bf1.setPointLabelFormatter(new PointLabelFormatter(Color.WHITE));
//        bf1.getPointLabelFormatter().getTextPaint().setTextAlign(Paint.Align.CENTER);
//        bf1.getPointLabelFormatter().getTextPaint().setFakeBoldText(true);

        // add series to the xyplot:
        plot.addSeries(series1, bf1);
        PanZoom.attach(plot);
    }
}
