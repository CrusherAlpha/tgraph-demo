package cn.edu.buaa.act.tgraphdemo.electricitysimulation;

import java.util.Objects;

// we do not care edge in this demo.
public class PowerLine {
    private final String id;

    public PowerLine(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PowerLine powerLine = (PowerLine) o;
        return Objects.equals(id, powerLine.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
