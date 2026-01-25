module project {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires org.apache.logging.log4j;
    requires com.fasterxml.jackson.databind;
    requires java.desktop;
    requires org.json;
    requires spring.context;
    requires spring.beans;
    requires java.net.http;
    requires testFx;
    requires javafx.swing;

    opens project.gui to javafx.fxml;
    opens project.gui.minigame to javafx.fxml;
    opens project.game.player to com.fasterxml.jackson.databind;
    exports project.gui;
    exports project.gui.minigame;
    exports project.game;
    exports project.game.player;
    exports project.audio;
    exports project.thread;
}