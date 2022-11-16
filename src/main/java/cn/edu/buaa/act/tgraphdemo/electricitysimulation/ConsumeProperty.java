package cn.edu.buaa.act.tgraphdemo.electricitysimulation;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.time.LocalDate;

public class ConsumeProperty {
    private final SimpleObjectProperty<LocalDate> time;
    private final SimpleDoubleProperty consume;

    public ConsumeProperty(LocalDate time, double consume) {
        this.time = new SimpleObjectProperty<>(time);
        this.consume = new SimpleDoubleProperty(consume);
    }

    public LocalDate getTime() {
        return time.get();
    }

    public void setTime(LocalDate time) {
        this.time.set(time);
    }

    public Double getConsume() {
        return consume.get();
    }

    public void setConsume(Double consume) {
        this.consume.set(consume);
    }
}
