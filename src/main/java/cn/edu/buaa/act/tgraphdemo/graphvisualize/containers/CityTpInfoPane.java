package cn.edu.buaa.act.tgraphdemo.graphvisualize.containers;

import cn.edu.buaa.act.tgraphdemo.electricitysimulation.City;
import cn.edu.buaa.act.tgraphdemo.electricitysimulation.ConsumeProperty;
import cn.edu.buaa.act.tgraphdemo.electricitysimulation.ProduceProperty;
import io.github.palexdev.materialfx.controls.MFXTableColumn;
import io.github.palexdev.materialfx.controls.MFXTableView;
import io.github.palexdev.materialfx.controls.cell.MFXTableRowCell;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.layout.GridPane;

import java.util.Comparator;

public class CityTpInfoPane extends GridPane {
    private final City city;

    private final MFXTableView<ConsumeProperty> consumeTable = new MFXTableView<>();
    private final MFXTableView<ProduceProperty> produceTable = new MFXTableView<>();

    public CityTpInfoPane(City city) {
        this.city = city;
        setConsumeTable();
        setProduceTable();
        add(consumeTable, 0, 0);
        setVgap(12);
        add(produceTable, 0, 1);
    }

    private ObservableList<ConsumeProperty> consumeData() {
        ObservableList<ConsumeProperty> ret = FXCollections.observableArrayList();
        for (var pr : city.getConsume().entrySet()) {
            ret.add(new ConsumeProperty(pr.getKey(), pr.getValue()));
        }
        return ret;
    }

    private ObservableList<ProduceProperty> produceData() {
        ObservableList<ProduceProperty> ret = FXCollections.observableArrayList();
        for (var pr : city.getProduce().entrySet()) {
            ret.add(new ProduceProperty(pr.getKey(), pr.getValue()));
        }
        return ret;
    }

    private void setConsumeTable() {
        // table consume
        MFXTableColumn<ConsumeProperty> timeColumn = new MFXTableColumn<>("Time", true, Comparator.comparing(ConsumeProperty::getTime));
        MFXTableColumn<ConsumeProperty> valueColumn = new MFXTableColumn<>("Consume", true, Comparator.comparing(ConsumeProperty::getConsume));
        timeColumn.setRowCellFactory(p -> new MFXTableRowCell<>(ConsumeProperty::getTime));
        valueColumn.setRowCellFactory(p -> new MFXTableRowCell<>(ConsumeProperty::getConsume));
        consumeTable.getTableColumns().addAll(timeColumn, valueColumn);
        consumeTable.setItems(consumeData());
        consumeTable.autosizeColumnsOnInitialization();
    }

    private void setProduceTable() {
        // table produce
        MFXTableColumn<ProduceProperty> tC = new MFXTableColumn<>("Time", true, Comparator.comparing(ProduceProperty::getTime));
        MFXTableColumn<ProduceProperty> vC = new MFXTableColumn<>("Produce", true, Comparator.comparing(ProduceProperty::getProduce));
        tC.setRowCellFactory(p -> new MFXTableRowCell<>(ProduceProperty::getTime));
        vC.setRowCellFactory(p -> new MFXTableRowCell<>(ProduceProperty::getProduce));
        produceTable.getTableColumns().addAll(tC, vC);
        produceTable.setItems(produceData());
        produceTable.autosizeColumnsOnInitialization();
    }


}
