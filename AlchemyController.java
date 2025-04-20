package com.example.demo;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;

public class AdventureController {
    @FXML private Label lblResult;
    private Controller mainController;
    private static final int BASE_COST = 300;

    public void setMainController(Controller mainController) {
        this.mainController = mainController;
    }

    private void handleAdventure(int areaNumber) {
        if (!mainController.deductQi(BASE_COST * areaNumber)) {
            showAlert("灵气不足！需要 " + (BASE_COST * areaNumber) + " 灵气");
            return;
        }

        double successRate = 0.7 - (areaNumber * 0.1);
        boolean success = Math.random() < successRate;

        if (success) {
            int reward = 1000 * areaNumber;
            mainController.updateQi(reward);
            lblResult.setText(String.format("区域%d探索成功！\n获得%d灵气", areaNumber, reward));
        } else {
            lblResult.setText(String.format("区域%d遭遇危机！\n损失%d灵气", areaNumber, BASE_COST * areaNumber));
        }
    }

    @FXML private void handleArea1() { handleAdventure(1); }
    @FXML private void handleArea2() { handleAdventure(2); }
    @FXML private void handleArea3() { handleAdventure(3); }
    @FXML private void handleArea4() { handleAdventure(4); }
    @FXML private void handleArea5() { handleAdventure(5); }

    private void showAlert(String message) {
        new Alert(Alert.AlertType.WARNING, message).showAndWait();
    }
}
