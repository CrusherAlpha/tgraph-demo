package cn.edu.buaa.act.tgraphdemo;

import cn.edu.buaa.act.tgraphdemo.graphvisualize.containers.SmartGraphDemoContainer;
import cn.edu.buaa.act.tgraphdemo.graphvisualize.graph.Digraph;
import cn.edu.buaa.act.tgraphdemo.graphvisualize.graph.DigraphEdgeList;
import cn.edu.buaa.act.tgraphdemo.graphvisualize.graphview.SmartCircularSortedPlacementStrategy;
import cn.edu.buaa.act.tgraphdemo.graphvisualize.graphview.SmartGraphPanel;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;



public class Main extends Application {
    private static final Log log = LogFactory.getLog(Main.class);


    @Override
    public void start(Stage ignored) throws Exception {
        Digraph<String, String> g = new DigraphEdgeList<>();

        // vertex
        g.insertVertex("A");
        g.insertVertex("B");
        g.insertVertex("C");
        g.insertVertex("D");
        g.insertVertex("E");
        g.insertVertex("F");

        // edge
        g.insertEdge("A", "B", "AB");
        g.insertEdge("B", "A", "BA");
        g.insertEdge("A", "C", "AC");
        g.insertEdge("A", "D", "AD");
        g.insertEdge("B", "C", "BC");
        g.insertEdge("C", "D", "CD");
        g.insertEdge("B", "E", "BE");
        g.insertEdge("F", "D", "FD");
        g.insertEdge("D", "F", "DF");

        SmartGraphPanel<String, String> graphView = new SmartGraphPanel<>(g, new SmartCircularSortedPlacementStrategy());

        Scene scene = new Scene(new SmartGraphDemoContainer(graphView), 1024, 768);
        Stage stage = new Stage(StageStyle.DECORATED);
        stage.setTitle("TGraph Demo");
        stage.setMinHeight(500);
        stage.setMinWidth(800);
        stage.setScene(scene);
        stage.show();

        graphView.init();

        graphView.setVertexSingleClickAction(stringSmartGraphVertex -> log.info("single click: " + stringSmartGraphVertex.getUnderlyingVertex().element()));
        graphView.setEdgeSingleClickAction(stringStringSmartGraphEdge -> log.info("single click: " + stringStringSmartGraphEdge.getUnderlyingEdge().element()));

        graphView.setVertexDoubleClickAction(stringSmartGraphVertex -> log.info("double click: " +  stringSmartGraphVertex.getUnderlyingVertex().element()));
        graphView.setEdgeDoubleClickAction(stringStringSmartGraphEdge -> log.info("double click: " + stringStringSmartGraphEdge.getUnderlyingEdge().element()));

    }

    public static void main(String[] args) {
        launch(args);
    }
}
