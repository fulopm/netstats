package hu.fulopmark.netstats.controller;

import hu.fulopmark.netstats.repository.SpeedtestRepository;
import hu.fulopmark.netstats.service.ChartService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;

@Controller
@AllArgsConstructor
public class MainController {

    private SpeedtestRepository speedtestRepository;
    private ChartService chartService;

    @GetMapping("/")
    public String index(Model model) {

        return "stats";
    }

    @GetMapping(path = "/chart.png", produces = "image/png")
    public ResponseEntity<byte[]> getLastFifteenSpeedtestsChart() throws IOException {

        return ResponseEntity.status(HttpStatus.OK)
                .header(HttpHeaders.CONTENT_DISPOSITION, "filename=\"chart.png\"")
                .contentType(MediaType.IMAGE_PNG)
                .body(chartService.getLastNSpeedTestsChart(20));

    }
}
