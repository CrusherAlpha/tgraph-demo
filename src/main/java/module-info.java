module cn.edu.buaa.act.tgraphdemo {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;
    requires commons.logging;
    requires com.dlsc.formsfx;


    opens cn.edu.buaa.act.tgraphdemo to javafx.fxml;
    exports cn.edu.buaa.act.tgraphdemo;

}