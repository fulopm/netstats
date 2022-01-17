package hu.fulopmark.netstats.repository;

import hu.fulopmark.netstats.model.Speedtest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface SpeedtestRepository extends JpaRepository<Speedtest, LocalDateTime> {


    @Query(nativeQuery = true, value = "select * from netstats s order by s.ts desc limit :n")
    List<Speedtest> getLastN(@Param("n") int n);
}
