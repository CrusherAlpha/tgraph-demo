package cn.edu.buaa.act.tgraphdemo.electricitysimulation;

import java.time.LocalDate;
import java.util.HashMap;

public class City {
    private final String id;
    // we keep this two for table presentation.
    private final HashMap<LocalDate, Double> consume;
    private final HashMap<LocalDate, Double> produce;

    public City(String id) {
        this.id = id;
        consume = new HashMap<>();
        produce = new HashMap<>();
    }

    public void addConsume(LocalDate time, Double value) {
        consume.put(time, value);
    }

    public void addProduce(LocalDate time, Double value) {
        produce.put(time, value);
    }

    public String getId() {
        return id;
    }

    public HashMap<LocalDate, Double> getConsume() {
        return consume;
    }

    public HashMap<LocalDate, Double> getProduce() {
        return produce;
    }
}
