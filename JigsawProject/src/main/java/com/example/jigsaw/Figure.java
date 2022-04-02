package com.example.jigsaw;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Pane;

public class Figure extends Node {
    Pane pane;

    public Figure(int blockSize) {
        pane = new Pane();
        pane.setPrefSize(blockSize, blockSize);
        pane.setStyle("-fx-background-color: blue");
        pane.setOnDragDetected(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                System.out.println("Event on Source: drag detected");
                Dragboard db = pane.startDragAndDrop(TransferMode.ANY);

                ClipboardContent cb = new ClipboardContent();
                cb.putString("e");

                db.setContent(cb);

                event.consume();
            }
        });
    }

    @Override
    public Node getStyleableNode() {
        return super.getStyleableNode();
    }
}
