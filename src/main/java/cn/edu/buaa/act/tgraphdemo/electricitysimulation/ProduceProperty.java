
package cn.edu.buaa.act.tgraphdemo.electricitysimulation;

import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.time.LocalDate;

public class ProduceProperty {
    private final SimpleObjectProperty<LocalDate> time;
    private final SimpleDoubleProperty produce;

    public ProduceProperty(LocalDate time, double produce) {
        this.time = new SimpleObjectProperty<>(time);
        this.produce = new SimpleDoubleProperty(produce);
    }

    public LocalDate getTime() {
        return time.get();
    }

    public void setTime(LocalDate time) {
        this.time.set(time);
    }

    public Double getProduce() {
        return produce.get();
    }

    public void setProduce(Double produce) {
        this.produce.set(produce);
    }
}

