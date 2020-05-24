package battleship.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.util.ResourceBundle;

public class ResultsController implements Initializable {
    public Label winnerNameLabel;
    public Label myShotsLabel;
    public Label partnerShotsLabel;
    private final String winnerName;
    private final int myShots;
    private final int partnerShots;

    public ResultsController(String winnerName, int myShots, int partnerShots) {
        this.winnerName = winnerName;
        this.myShots = myShots;
        this.partnerShots = partnerShots;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        winnerNameLabel.setText("Winner: " + winnerName);
        myShotsLabel.setText("My shots: " + myShots);
        partnerShotsLabel.setText("Partner shots: " + partnerShots);
    }

    @FXML
    private void onOkButtonAction(ActionEvent actionEvent) {
        Platform.exit();
    }
}
