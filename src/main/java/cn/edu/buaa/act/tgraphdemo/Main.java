package cn.edu.buaa.act.tgraphdemo;

import cn.edu.buaa.act.tgraphdemo.electricitysimulation.City;
import cn.edu.buaa.act.tgraphdemo.electricitysimulation.PowerLine;
import cn.edu.buaa.act.tgraphdemo.graphvisualize.containers.CityTpInfoPane;
import cn.edu.buaa.act.tgraphdemo.graphvisualize.containers.SmartGraphDemoContainer;
import cn.edu.buaa.act.tgraphdemo.graphvisualize.graph.Digraph;
import cn.edu.buaa.act.tgraphdemo.graphvisualize.graph.DigraphEdgeList;
import cn.edu.buaa.act.tgraphdemo.graphvisualize.graph.Vertex;
import cn.edu.buaa.act.tgraphdemo.graphvisualize.graphview.SmartCircularSortedPlacementStrategy;
import cn.edu.buaa.act.tgraphdemo.graphvisualize.graphview.SmartGraphPanel;
import com.dlsc.formsfx.model.structure.DateField;
import com.dlsc.formsfx.model.structure.DoubleField;
import com.dlsc.formsfx.model.structure.SingleSelectionField;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Main extends Application {
    private static final Log log = LogFactory.getLog(Main.class);

    private int selectedVertexCnt = 0;
    // for add edge.
    private Vertex<City> startVertex = null;
    private Vertex<City> endVertex = null;

    private void restore(SmartGraphPanel<City, PowerLine> graphPanel) {
        if (startVertex != null) {
            graphPanel.getStylableVertex(startVertex).setStyleClass("vertex");
        }
        if (endVertex != null) {
            graphPanel.getStylableVertex(endVertex).setStyleClass("vertex");
        }
        selectedVertexCnt = 0;
        startVertex = null;
        endVertex = null;
    }


    @Override
    public void start(Stage ignored) throws Exception {
        Digraph<City, PowerLine> g = new DigraphEdgeList<>();
        HashMap<String, City> vertex = new HashMap<>();
        for (int i = 0; i < 6; ++i) {
            char c = (char) ('A' + i);
            vertex.put(String.valueOf(c), new City(String.valueOf(c)));

        }
        List<City> addedVertex = new ArrayList<>();
        for (int i = 6; i < 26; ++i) {
            char c = (char) ('A' + i);
            addedVertex.add(new City(String.valueOf(c)));
        }

        // initial graph
        // vertex
        for (var ver : vertex.values()) {
            g.insertVertex(ver);
        }
        // edge
        var A = vertex.get("A");
        var B = vertex.get("B");
        var C = vertex.get("C");
        var D = vertex.get("D");
        var E = vertex.get("E");
        var F = vertex.get("F");
        g.insertEdge(A, B, new PowerLine("AB"));
        g.insertEdge(B, A, new PowerLine("BA"));
        g.insertEdge(A, C, new PowerLine("AC"));
        g.insertEdge(A, D, new PowerLine("AD"));
        g.insertEdge(B, C, new PowerLine("BC"));
        g.insertEdge(C, D, new PowerLine("CD"));
        g.insertEdge(B, E, new PowerLine("BE"));
        g.insertEdge(F, D, new PowerLine("FD"));
        g.insertEdge(D, F, new PowerLine("DF"));

        // ui pane
        SmartGraphPanel<City, PowerLine> graphPanel = new SmartGraphPanel<>(g, new SmartCircularSortedPlacementStrategy());
        var demoPane = new SmartGraphDemoContainer(graphPanel);
        var controlPane = demoPane.getControl();
        var form = controlPane.getForm();

        // add vertex
        controlPane.setAddVertexButtonAction(actionEvent -> {
            if (addedVertex.size() <= 0) {
                log.info("no more cities.");
                return;
            }
            var n = addedVertex.remove(0);
            g.insertVertex(n);
            graphPanel.update();
            log.info(String.format("city %s has been added.", n.getId()));
        });

        // add edge
        controlPane.setAddEdgeButtonAction(actionEvent -> {
            if (startVertex == null || endVertex == null) {
                log.info("You should pick  two cities.");
                return;
            }
            g.insertEdge(startVertex, endVertex, new PowerLine(startVertex.toString() + endVertex.toString()));
            restore(graphPanel);
            graphPanel.update();
        });

        // mouse right single click event, pick a vertex
        graphPanel.setVertexSingleClickAction(graphVertex -> {
            switch (selectedVertexCnt) {
                case 0: {
                    startVertex = graphVertex.getUnderlyingVertex();
                    graphVertex.setStyleClass("selectedVertex");
                    ++selectedVertexCnt;
                    break;
                }
                case 1: {
                    if (!graphVertex.getUnderlyingVertex().equals(startVertex)) {
                        endVertex = graphVertex.getUnderlyingVertex();
                        graphVertex.setStyleClass("selectedVertex");
                        ++selectedVertexCnt;
                    }
                    break;
                }
                case 2: {
                    if (!graphVertex.getUnderlyingVertex().equals(startVertex) && !graphVertex.getUnderlyingVertex().equals(endVertex)) {
                        graphPanel.getStylableVertex(startVertex).setStyleClass("vertex");
                        startVertex = endVertex;
                        endVertex = graphVertex.getUnderlyingVertex();
                        graphVertex.setStyleClass("selectedVertex");
                    }
                    break;
                }
                default:
            }
        });

        // reset
        controlPane.setResetFormButtonButtonAction(actionEvent -> {
            // reset form
            form.reset();
            // reset select vertex for adding edge
            restore(graphPanel);
        });

        // mouse left double click event, display city info
        graphPanel.setVertexDoubleClickAction(graphVertex -> {
            var city = graphVertex.getUnderlyingVertex().element();
            CityTpInfoPane info = new CityTpInfoPane(city);
            var scene = new Scene(info);
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setTitle(String.format("City %s", city.getId()));
            stage.setScene(scene);
            stage.show();
        });

        // tp add button
        controlPane.setTpAddButtonAction(actionEvent -> {
            var formFields = form.getFields();
            var cityID = ((SingleSelectionField<String>)formFields.get(0)).getSelection();
            var tp = ((SingleSelectionField<String>)formFields.get(1)).getSelection();
            var time = ((DateField)formFields.get(2)).getValue();
            var value = ((DoubleField)formFields.get(3)).getValue();
            var cityVertex = vertex.get(cityID);
            if (cityVertex != null) {
                if (tp.equals("consume")) {
                    cityVertex.addConsume(time, value);
                } else {
                    cityVertex.addProduce(time, value);
                }
            }
            log.info("tp value was added successfully.");
            form.reset();
        });

        // tp query button
        controlPane.setTpQueryButton(actionEvent -> {
            var formFields = form.getFields();
            var cityID = ((SingleSelectionField<String>)formFields.get(0)).getSelection();
            var tp = ((SingleSelectionField<String>)formFields.get(1)).getSelection();
            var time = ((DateField)formFields.get(2)).getValue();
            var cityVertex = vertex.get(cityID);
            Double val = 0.;
            if (cityVertex != null) {
                if (tp.equals("consume")) {
                    var c = cityVertex.getConsume();
                    val = c.get(time);
                } else {
                    var p = cityVertex.getProduce();
                    val = p.get(time);
                }
            }
            log.info("tp value query successfully.");
            Alert info = new Alert(Alert.AlertType.INFORMATION);
            info.setContentText("The result is: " + val);
            info.show();
            form.reset();
        });


        Scene scene = new Scene(demoPane, 1500, 1500 * 0.618);
        Stage stage = new Stage(StageStyle.DECORATED);
        stage.setTitle("TGraph Demo");
        stage.setMinWidth(800);
        stage.setMinHeight(800 * 0.618);
        stage.setScene(scene);
        stage.show();

        graphPanel.init();



    }

    public static void main(String[] args) {
        launch(args);
    }
}
