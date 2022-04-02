package com.example.jigsaw;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.ResourceBundle;
import java.util.Timer;


import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class MainController {

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;

    @FXML
    private AnchorPane mainAnchorPane;

    @FXML
    private Label roundCount;

    @FXML
    private Button endGame;

    @FXML
    private Label timeCount;

    @FXML
    private Button startButton;


    @FXML
    void endGameButton(ActionEvent event) {
        wantToContinue = false;
        startButton.setVisible(true);
        EndGame(gridPane);
    }

    @FXML
    void startGameButton(ActionEvent event) {
        System.out.println("new gane!");
        wantToContinue = true;
        StartGame(gridPane);

    }

    int blockSize = 40;

    boolean wantToContinue = true;

    int curInd = 0;

    String[] figuresDesc = new String[31];

    private Group curGroup;

    int curRound;

    private Timer timer;

    GridPane gridPane;

    long time;

    @FXML
    void initialize() {

        time = System.currentTimeMillis();

        curRound = 0;
        gridPane = CreateGridPane();

//        timer = new Timer(1000,  new ActionEvent() {
//            public void handler(ActionEvent e) {
//                System.out.println("HERE!");
//            }
//        });

        roundCount.setVisible(false);
        startButton.setVisible(false);
        timeCount.setVisible(false);

        FillFiguresDesc();

        StartNewRound();

        gridPane.setOnDragDropped(new EventHandler<DragEvent>() {
            public void handle(DragEvent event) {
                System.out.println("Event on Source: drag dropped");
            }
        });

        mainAnchorPane.getChildren().add(gridPane);

        mainAnchorPane.setStyle("-fx-background-color: black");
    }

    private void StartNewRound() {
        if (!wantToContinue) {
            return;
        }
        curRound += 1;
        //roundCount.setText("Количество ходов: " + String.valueOf(curRound));
        curInd = (int) (Math.random() * 31);
        Group group = CreateGroup(figuresDesc[curInd], 500, 100);
        mainAnchorPane.getChildren().add(group);
        curGroup = group;
        int finalCurInd = curInd;
        group.setOnDragDetected(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                System.out.println(group);
                System.out.println("Event on Source: drag detected");
                Dragboard db = group.startDragAndDrop(TransferMode.ANY);

                ClipboardContent cb = new ClipboardContent();
                cb.putString(figuresDesc[finalCurInd]);

                db.setContent(cb);

                event.consume();
            }
        });
    }

    private GridPane CreateGridPane() {
        GridPane gridPanel = new GridPane();
        gridPanel.setPadding(new Insets(40, 10, 10, 40));

        for (int i = 0; i < 9; i++) {
            ColumnConstraints col1 = new ColumnConstraints();
            gridPanel.getColumnConstraints().add(col1);
            col1.setPrefWidth(blockSize);
            RowConstraints row1 = new RowConstraints();
            gridPanel.getRowConstraints().add(row1);
            row1.setPrefHeight(blockSize);
            for (int j = 0; j < 9; j++) {
                Pane pane = new Pane();
                pane.setStyle("-fx-background-color: #5d626e");
                gridPanel.add(pane, i, j);
                pane.prefHeight(blockSize);
                pane.prefWidth(blockSize);
                String id = i + " " + j;
                pane.setId(id);
                System.out.println(id + " " + gridPanel.getChildren().size());

                pane.setOnDragExited(new EventHandler<DragEvent>() {
                    public void handle(DragEvent event) {
                        System.out.println("Exit");
                        event.acceptTransferModes(TransferMode.ANY);
                        String str = event.getDragboard().getString();
                        FillPaneWithFigure(pane, str, "-fx-background-color: #5d626e", "-fx-background-color: blue");
                    }
                });

                pane.setOnDragEntered(new EventHandler<DragEvent>() {
                    public void handle(DragEvent event) {
                        System.out.println("Enter");
                        event.acceptTransferModes(TransferMode.ANY);
                        String str = event.getDragboard().getString();
                        FillPaneWithFigure(pane, str, "-fx-background-color: #6ba2fa", "-fx-background-color: blue");
                    }
                });

                pane.setOnDragOver(new EventHandler<DragEvent>() {
                    public void handle(DragEvent event) {
                        if (event.getDragboard().hasString()) {
                            event.acceptTransferModes(TransferMode.ANY);
                        }
                    }
                });

                pane.setOnDragDropped(new EventHandler<DragEvent>() {
                    public void handle(DragEvent event) {
                        if (!wantToContinue) {
                            return;
                        }
                        System.out.println("Event on Source: drag dropped");

                        String str = event.getDragboard().getString();
                        boolean isCorrect = CheckFigure(pane, str);
                        if (isCorrect) {
                            FillPaneWithFigure(pane, str, "-fx-background-color: blue", "-fx-background-color: blue");
                            mainAnchorPane.getChildren().remove(curGroup);
                            StartNewRound();
                        }
                    }
                });
            }
        }
        gridPanel.setGridLinesVisible(true);

        gridPanel.setStyle("-fx-background-color: black");

        return gridPanel;
    }

    private Group CreateGroup(String desc, int startX, int startY) {
        String[] figure = desc.split(";");

        Group group = new Group();

        for (int i = 0; i < figure.length; i++) {
            String[] curIdStr = figure[i].split(" ");

            int curXPos = Integer.parseInt(curIdStr[0]);
            int curYPos = Integer.parseInt(curIdStr[1]);

            Rectangle rec = new Rectangle();

            rec.setX(startX + blockSize*curXPos);
            rec.setY(startY + blockSize*curYPos);
            rec.setWidth(blockSize);
            rec.setHeight(blockSize);

            rec.setFill(Color.rgb(107, 162, 250));

            group.getChildren().add(rec);
        }

        return group;
    }

    private void FillPaneWithFigure(Pane pane, String figureDesc, String color, String blockedColor) {
        if (!CheckFigure(pane, figureDesc)) {
            return;
        }
        String[] figure = figureDesc.split(";");
        String[] id_arr = pane.getId().split(" ");
        int xPos = Integer.parseInt(id_arr[0]);
        int yPos = Integer.parseInt(id_arr[1]);

        System.out.println("Pos: " + xPos + " " + yPos);
        GridPane parentGrid = (GridPane) pane.getParent();

        for (int i = 0; i < figure.length; i++) {
            String[] curIdStr = figure[i].split(" ");

            int curXPos = Integer.parseInt(curIdStr[0]);
            int curYPos = Integer.parseInt(curIdStr[1]);

            curXPos += xPos;
            curYPos += yPos;

            int pos = curXPos * 9 + curYPos;
            System.out.println(pos);
            if (!parentGrid.getChildren().get(pos).getStyle().equals("-fx-background-color: blue"))  {
                parentGrid.getChildren().get(pos).setStyle(color);
            }

        }
    }


    private boolean CheckFigure(Pane pane, String figureDesc) {

        boolean isCorrect = true;

        String[] figure = figureDesc.split(";");

        String[] id_arr = pane.getId().split(" ");
        int xPos = Integer.parseInt(id_arr[0]);
        int yPos = Integer.parseInt(id_arr[1]);

        System.out.println("Pos: " + xPos + " " + yPos);
        GridPane parentGrid = (GridPane) pane.getParent();

        for (int i = 0; i < figure.length; i++) {
            String[] curIdStr = figure[i].split(" ");

            int curXPos = Integer.parseInt(curIdStr[0]);
            int curYPos = Integer.parseInt(curIdStr[1]);

            curXPos += xPos;
            curYPos += yPos;
            if (curXPos >= 9 || curXPos < 0 || curYPos >= 9 || curYPos < 0) {
                return false;
            }

            int pos = curXPos * 9 + curYPos;
            System.out.println(pos);
            if (parentGrid.getChildren().get(pos).getStyle().equals("-fx-background-color: blue"))  {
                isCorrect = false;
            }

        }
        return isCorrect;
    }

    private void EndGame(GridPane gridPane) {
        endGame.setVisible(false);
        for (int i = 0; i < gridPane.getChildren().size(); i++) {
            gridPane.getChildren().get(i).setStyle("-fx-background-color: #5d626e");
        }
        //System.out.println(();
        timeCount.setVisible(true);
        timeCount.setText("Время игры: " + String.valueOf((System.currentTimeMillis() - time) * 1.0 / 1000));
        roundCount.setText("Количество ходов: " + String.valueOf(curRound));
        roundCount.setVisible(true);
        curGroup.setVisible(false);

    }

    private void StartGame(GridPane gridPane) {
        endGame.setVisible(true);
        startButton.setVisible(false);
        roundCount.setVisible(false);
        timeCount.setVisible(false);
        curRound = 0;
        curGroup.setVisible(true);
        mainAnchorPane.getChildren().remove(curGroup);
        StartNewRound();
    }

    private void FillFiguresDesc() {
        figuresDesc[0] = "0 0;1 0;0 1;0 2;";
        figuresDesc[1] = "0 0;0 1;1 1;2 1;";
        figuresDesc[2] = "0 2;1 2;1 1;1 0;";
        figuresDesc[3] = "0 0;1 0;2 0;2 1;";
        figuresDesc[4] = "0 0;1 0;1 1;1 2;";
        figuresDesc[5] = "0 1;1 1;2 1;2 0;";
        figuresDesc[6] = "0 0;0 1;0 2;1 2;";
        figuresDesc[7] = "0 0;0 1;1 0;2 0;";
        figuresDesc[8] = "0 0;0 1;1 1;1 2;";
        figuresDesc[9] = "0 1;1 1;1 0;2 0;";
        figuresDesc[10] = "0 2;0 1;1 1;1 0;";
        figuresDesc[11] = "0 0;1 0;1 1;2 1;";
        figuresDesc[12] = "0 2;1 2;2 0;2 1;2 2;";
        figuresDesc[13] = "0 0;0 1;0 2;1 2;2 2;";
        figuresDesc[14] = "0 2;0 1;0 0;1 0;2 0;";
        figuresDesc[15] = "0 0;1 0;2 0;2 1;2 2;";
        figuresDesc[16] = "0 2;1 2;1 1;1 0;2 2;";
        figuresDesc[17] = "0 0;1 0;1 1;1 2;2 0;";
        figuresDesc[18] = "0 0;0 1;0 2;1 1;2 1;";
        figuresDesc[19] = "0 1;1 1;2 0;2 1;2 2;";
        figuresDesc[20] = "0 0;1 0;2 0;";
        figuresDesc[21] = "0 0;0 1;0 2;";
        figuresDesc[22] = "0 0;";
        figuresDesc[23] = "0 0;0 1;1 0;";
        figuresDesc[24] = "0 0;1 1;1 0;";
        figuresDesc[25] = "0 1;1 1;1 0;";
        figuresDesc[26] = "0 0;0 1;1 1;";
        figuresDesc[27] = "0 0;0 1;0 2;1 1;";
        figuresDesc[28] = "0 0;1 0;1 1;2 0;";
        figuresDesc[29] = "0 1;1 0;1 1;1 2;";
        figuresDesc[30] = "0 1;1 0;1 1;2 1;";
    }

}
