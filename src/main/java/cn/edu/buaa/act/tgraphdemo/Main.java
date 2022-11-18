package cn.edu.buaa.act.tgraphdemo;


import cn.edu.buaa.act.tgraph.api.dbms.DatabaseManagementService;
import cn.edu.buaa.act.tgraph.api.tgraphdb.Node;
import cn.edu.buaa.act.tgraph.api.tgraphdb.TGraphDatabaseService;
import cn.edu.buaa.act.tgraph.common.Pair;
import cn.edu.buaa.act.tgraph.impl.dbms.DatabaseManager;
import cn.edu.buaa.act.tgraph.txn.TransactionAbortException;
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
import com.google.common.base.Preconditions;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.RelationshipType;

import java.io.File;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

enum TEST_REL_TYPE implements RelationshipType {
    TEST_REL
}


public class Main extends Application {
    private static final Log log = LogFactory.getLog(Main.class);
    private DatabaseManagementService dbms = null;
    private TGraphDatabaseService tg = null;
    private static final Label testLabel = Label.label("test-node");

    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-d");

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

    boolean deleteDirectory(File directoryToBeDeleted) {
        File[] allContents = directoryToBeDeleted.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                deleteDirectory(file);
            }
        }
        return directoryToBeDeleted.delete();
    }


    @Override
    public void start(Stage ignored) {

        // init tgraph
        String dbmsPath = "/Users/crusher/test/tgraph-demo";
        deleteDirectory(new File(dbmsPath));
        dbms = DatabaseManager.of(dbmsPath);
        dbms.createDatabase("eu-demo");
        tg = dbms.database("eu-demo");

        // initial graph
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

        // vertex
        for (var ver : vertex.values()) {
            g.insertVertex(ver);
        }
        // edge
        List<Pair<City, City>> edges = new ArrayList<>();
        var A = vertex.get("A");
        var B = vertex.get("B");
        var C = vertex.get("C");
        var D = vertex.get("D");
        var E = vertex.get("E");
        var F = vertex.get("F");
        g.insertEdge(A, B, new PowerLine("AB"));
        edges.add(Pair.of(A, B));
        g.insertEdge(B, A, new PowerLine("BA"));
        edges.add(Pair.of(B, A));
        g.insertEdge(A, C, new PowerLine("AC"));
        edges.add(Pair.of(A, C));
        g.insertEdge(A, D, new PowerLine("AD"));
        edges.add(Pair.of(A, D));
        g.insertEdge(B, C, new PowerLine("BC"));
        edges.add(Pair.of(B, C));
        g.insertEdge(C, D, new PowerLine("CD"));
        edges.add(Pair.of(C, D));
        g.insertEdge(B, E, new PowerLine("BE"));
        edges.add(Pair.of(B, E));
        g.insertEdge(F, D, new PowerLine("FD"));
        edges.add(Pair.of(F, D));
        g.insertEdge(D, F, new PowerLine("DF"));
        edges.add(Pair.of(D, F));

        // add initial vertex and edge into tg
        try (var txn = tg.beginTx()) {
            HashMap<String, Node> mps = new HashMap<>();
            for (var v : vertex.values()) {
                var node = txn.createNode(testLabel);
                node.createTemporalProperty("consume");
                node.createTemporalProperty("produce");
                node.setProperty("ID", v.getId());
                mps.put(v.getId(), node);
            }
            for (var e : edges) {
                var st = mps.get(e.first().getId());
                var en = mps.get(e.second().getId());
                st.createRelationshipTo(en, TEST_REL_TYPE.TEST_REL);
            }
            txn.commit();
        }

        // add vertex temporal value
        // Node A.
        String d = "2022-10-%d";
        try (var txn = tg.beginTx()) {
            var a = txn.findNode(testLabel, "ID", "A");
            var am = vertex.get("A");
            for (int i = 5; i < 15; i += 2) {
                LocalDate localDate = LocalDate.parse(String.format(d, i), formatter);
                a.setTemporalPropertyValue("consume", Timestamp.valueOf(localDate.atStartOfDay()), (Double)(i * 1.0));
                am.addConsume(localDate, (Double)(i * 1.0));
                a.setTemporalPropertyValue("produce", Timestamp.valueOf(localDate.atStartOfDay()), (Double)(i * i + 1.0));
                am.addProduce(localDate, (Double)(i * i + 1.0));
            }
            txn.commit();
        } catch (TransactionAbortException e) {
            log.info("Transaction abort, set tp failed.");
        }
        // Node B.
        d = "2022-09-%d";
        try (var txn = tg.beginTx()) {
            var b = txn.findNode(testLabel, "ID", "B");
            var bm = vertex.get("B");
            for (int i = 10; i < 20; i += 3) {
                LocalDate localDate = LocalDate.parse(String.format(d, i), formatter);
                b.setTemporalPropertyValue("consume", Timestamp.valueOf(localDate.atStartOfDay()), (Double)(i * 1.0));
                bm.addConsume(localDate, (Double)(i * 1.0));
                b.setTemporalPropertyValue("produce", Timestamp.valueOf(localDate.atStartOfDay()), (Double)(i * i + 1.0));
                bm.addProduce(localDate, (Double) (i * i + 1.0));
            }
            txn.commit();
        } catch (TransactionAbortException e) {
            log.info("Transaction abort, set tp failed.");
        }


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
            vertex.put(n.getId(), n);
            g.insertVertex(n);
            graphPanel.update();
            log.info(String.format("city %s has been added.", n.getId()));
            try (var txn = tg.beginTx()) {
                var a = txn.createNode(testLabel);
                a.setProperty("ID", n.getId());
                a.createTemporalProperty("consume");
                a.createTemporalProperty("produce");
                txn.commit();
            }
        });

        // add edge
        controlPane.setAddEdgeButtonAction(actionEvent -> {
            if (startVertex == null || endVertex == null) {
                log.info("You should pick  two cities.");
                return;
            }
            var st = startVertex.element();
            var en = endVertex.element();
            g.insertEdge(startVertex, endVertex, new PowerLine(st.getId() + en.getId()));
            try (var txn = tg.beginTx()) {
                var a = txn.findNode(testLabel, "ID", st.getId());
                var b = txn.findNode(testLabel, "ID", en.getId());
                a.createRelationshipTo(b, TEST_REL_TYPE.TEST_REL);
                txn.commit();
            }
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
            String t;
            if (cityVertex != null) {
                if (tp.equals("consume")) {
                    cityVertex.addConsume(time, value);
                    t = "consume";
                } else {
                    cityVertex.addProduce(time, value);
                    t = "produce";
                }
                log.info("tp value was added successfully.");
                try (var txn = tg.beginTx()) {
                    var a = txn.findNode(testLabel, "ID", cityVertex.getId());
                    try {
                        a.setTemporalPropertyValue(t, Timestamp.valueOf(time.atStartOfDay()), value);
                    } catch (TransactionAbortException e) {
                        log.info("set tp property failed due to txn failed.");
                    }
                    txn.commit();
                }
            }
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
                String t;
                if (tp.equals("consume")) {
                    t = "consume";
                } else {
                    t = "produce";
                }
                try (var txn = tg.beginTx()) {
                    var a = txn.findNode(testLabel, "ID", cityID);
                    val = (Double) a.getTemporalPropertyValue(t, Timestamp.valueOf(time.atStartOfDay()));
                    txn.commit();
                } catch (TransactionAbortException e) {
                    log.info("get tp value failed due to txn failed.");
                }
                log.info("tp value query successfully.");
                Alert info = new Alert(Alert.AlertType.INFORMATION);
                info.setContentText("The result is: " + val);
                info.show();
            }
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
