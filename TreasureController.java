package com.example.demo;

import javafx.fxml.FXML;
import javafx.scene.control.ListView;

import java.util.HashMap;
import java.util.Map;

public class TreasureController {
    private Controller mainController;

    @FXML
    private ListView<String> treasureListView; // 修改：确保正确绑定

    private final Map<String, TreasureData> treasures = new HashMap<>();

    public void setMainController(Controller mainController) {
        this.mainController = mainController;
    }

    public Map<String, TreasureData> getTreasures() {
        return treasures;
    }

    public void setTreasures(Map<String, TreasureData> newTreasures) {
        treasures.clear();
        treasures.putAll(newTreasures);
        updateTreasureDisplay();
    }

    // 更新法宝显示
    public void updateTreasureDisplay() {
        treasureListView.getItems().clear();
        for (String name : treasures.keySet()) {
            treasureListView.getItems().add(name);
        }
    }
}
