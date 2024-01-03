package ru.practicum.ewm.statistic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.StatsClient;

@Component
public class Statistic {

    public final StatsClient statsClient;

    @Autowired
    public Statistic(StatsClient statsClient) {
        this.statsClient = statsClient;
    }

}