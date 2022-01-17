package hu.fulopmark.netstats.model;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "netstats")
@Data
public class Speedtest {

    private String serverName;
    private Integer serverId;
    private Double latency;
    private Double jitter;
    private Double packetLoss;
    private BigDecimal download;
    private BigDecimal upload;
    private BigDecimal downloadBytes;
    private BigDecimal uploadBytes;
    private String shareUrl;
    private Integer downloadServerCount;
    @Id
    private LocalDateTime ts;
}
