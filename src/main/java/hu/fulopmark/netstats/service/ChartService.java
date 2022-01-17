package hu.fulopmark.netstats.service;

import hu.fulopmark.netstats.model.Speedtest;
import hu.fulopmark.netstats.repository.SpeedtestRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.labels.XYItemLabelGenerator;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.*;
import org.jfree.data.xy.XYDataset;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class ChartService {

    private SpeedtestRepository speedtestRepository;

    private static final double BYTES_TO_MBYTES_DIVISOR = 125000.0;
    private static final String CHART_TITLE = "DIGI Letöltési és feltöltési sebesség (utolsó 15)";
    private static final String TIME_AXIS_LABEL = "Idő";
    private static final String VALUE_AXIS_LABEL = "Sebesség";



    private static final XYItemLabelGenerator ITEM_LABEL_GENERATOR = (xyDataset, series, item) -> {
        var dataset = (TimeSeriesCollection) xyDataset;

        var value = dataset.getSeries(series).getDataItem(item).getValue();

        return String.valueOf((double)value);
    };

    public byte[] getLastNSpeedTestsChart(int n) throws IOException {
        List<Speedtest> lastSpeedtests = speedtestRepository.getLastN(n);

        JFreeChart chart = createChart(createTimeSeriesCollection(lastSpeedtests));


        BufferedImage bf = chart.createBufferedImage(1280, 600);

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(bf , "png", byteArrayOutputStream);

        return byteArrayOutputStream.toByteArray();

    }


    private TimeSeriesCollection createTimeSeriesCollection(List<Speedtest> speedtests) {
        TimeSeries downloadSeries = new TimeSeries("Letöltés (Mbps)");
        TimeSeries uploadSeries = new TimeSeries("Feltöltés (Mbps)");

        log.info("Default timezone: {}", ZoneId.systemDefault());
        for (Speedtest s : speedtests) {
            final Hour hour = new Hour(convertToDate(s.getTs()));
            final double downloadSpeed =  bytesToMegabytes(s.getDownload());
            final double uploadSpeed =  bytesToMegabytes(s.getUpload());

            log.trace("Adding series with hour {} and download {} upload {} speed ", hour, downloadSpeed, uploadSpeed);
            downloadSeries.add(hour, downloadSpeed);
            uploadSeries.add(hour, uploadSpeed);
        }

        var dataset = new TimeSeriesCollection();

        dataset.addSeries(downloadSeries);
        dataset.addSeries(uploadSeries);


        return dataset;
    }

    private JFreeChart createChart(XYDataset dataset) {

        JFreeChart chart = ChartFactory.createTimeSeriesChart(CHART_TITLE,
                TIME_AXIS_LABEL,
                VALUE_AXIS_LABEL,
                dataset,
                true,
                true,
                true);
        XYPlot plot = (XYPlot) chart.getPlot();
        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) plot.getRenderer();
        renderer.setSeriesShapesVisible(0,true);
        renderer.setSeriesShapesVisible(1,true);
        renderer.setSeriesItemLabelsVisible(0, true);
        renderer.setSeriesItemLabelsVisible(1, true);
        renderer.setSeriesItemLabelGenerator(0, ITEM_LABEL_GENERATOR);
        renderer.setSeriesItemLabelGenerator(1, ITEM_LABEL_GENERATOR);


        DateAxis axis = (DateAxis) plot.getDomainAxis();
        axis.setDateFormatOverride(new SimpleDateFormat("MM-dd HH:mm"));

        return chart;
    }

    private Date convertToDate(LocalDateTime dateToConvert) {
        return Date.from(dateToConvert
                .atZone(ZoneId.systemDefault())
                .toInstant());
    }

    private double bytesToMegabytes(BigDecimal bytes) {

        final var divideBy = new BigDecimal(BYTES_TO_MBYTES_DIVISOR);
        var megabytes = bytes.divide(divideBy, RoundingMode.DOWN).doubleValue();

        return Math.round(megabytes * 100.0) / 100.0;
    }
}
