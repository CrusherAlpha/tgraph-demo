module cn.edu.buaa.act.tgraphdemo {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;
    requires commons.logging;


    opens cn.edu.buaa.act.tgraphdemo to javafx.fxml;
    exports cn.edu.buaa.act.tgraphdemo;

}