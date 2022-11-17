package cn.edu.buaa.act.tgraphdemo.graphvisualize.containers;

import cn.edu.buaa.act.tgraphdemo.graphvisualize.graphview.SmartGraphPanel;
import com.dlsc.formsfx.model.structure.Field;
import com.dlsc.formsfx.model.structure.Form;
import com.dlsc.formsfx.model.structure.Group;
import com.dlsc.formsfx.model.structure.Section;
import com.dlsc.formsfx.view.renderer.FormRenderer;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import java.io.File;
import java.net.MalformedURLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ControlPane extends VBox {
    private Button addVertexButton;
    private Button addEdgeButton;
    private Button tpAddButton;
    private Button tpQueryButton;
    private Button resetFormButton;
    private Form form;

    public ControlPane() {
        setSpacing(52);
        setAlignment(Pos.TOP_CENTER);
        setPrefWidth(336);
        createButton();
        createForm();
        loadStylesheet();
    }

    private void loadStylesheet() {
        try {
            getStylesheets().add(new File("control.css").toURI().toURL().toExternalForm());
            this.getStyleClass().add("control");
        } catch (MalformedURLException ex) {
            Logger.getLogger(SmartGraphPanel.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void createButton() {
        addVertexButton = new Button("ADD VERTEX");
        addVertexButton.getStyleClass().add("entity-add-button");
        getChildren().add(addVertexButton);

        addEdgeButton = new Button("ADD EDGE");
        addEdgeButton.getStyleClass().add("entity-add-button");
        getChildren().add(addEdgeButton);

        tpAddButton = new Button("ADD TP TIMEPOINT");
        tpAddButton.getStyleClass().add("entity-tp-function-button");
        getChildren().add(tpAddButton);

        tpQueryButton = new Button("QUERY TP TIMEPOINT");
        tpQueryButton.getStyleClass().add("entity-tp-function-button");
        getChildren().add(tpQueryButton);

        resetFormButton = new Button("RESET");
        resetFormButton.getStyleClass().add("reset-button");
        getChildren().add(resetFormButton);

    }

    private void createForm() {
        List<String> citys = new ArrayList<>(26);
        for (int i = 0; i < 26; ++i) {
            char v = (char) ('A' + i);
            citys.add(String.valueOf(v));
        }
        List<String> tps = new ArrayList<>(2);
        tps.add("consume");
        tps.add("produce");
        form = Form.of(
                Group.of(
                        Field.ofSingleSelectionType(citys).label("City"),
                        Field.ofSingleSelectionType(tps).label("Tp"),
                        Field.ofDate(LocalDate.now()).label("Date")
                ),
                Section.of(
                        Field.ofDoubleType(0.).label("Val")
                )
        ).title("TpForm");
        var n = new FormRenderer(form);
        getChildren().add(n);
    }

    public void setAddVertexButtonAction(EventHandler<ActionEvent> actionEvent) {
        addVertexButton.setOnAction(actionEvent);
    }


    public void setAddEdgeButtonAction(EventHandler<ActionEvent> actionEvent) {
        addEdgeButton.setOnAction(actionEvent);
    }

    public void setTpAddButtonAction(EventHandler<ActionEvent> actionEvent) {
        tpAddButton.setOnAction(actionEvent);
    }

    public void setTpQueryButton(EventHandler<ActionEvent> actionEvent) {
        tpQueryButton.setOnAction(actionEvent);
    }

    public void setResetFormButtonButtonAction(EventHandler<ActionEvent> actionEvent) {
        resetFormButton.setOnAction(actionEvent);
    }

    public Form getForm() {
        return form;
    }
}
