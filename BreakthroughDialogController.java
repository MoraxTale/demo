package com.example.demo;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.util.List;

public class BreakthroughDialogController {
    // 定义三条独立剧情线（可自定义文本）
    private static final List<String> PATH_1 = List.of(
            "【天道无情】\n斩断七情六欲，修为暴涨但心魔滋生...",
            "【天劫降临】\n九重雷劫轰然而至，需闭关百年抵御..."
    );

    private static final List<String> PATH_2 = List.of(
            "【人道轮回】\n入世历练，感悟众生悲欢...",
            "【因果纠缠】\n救下一名凡人，却卷入王朝纷争..."
    );

    private static final List<String> PATH_3 = List.of(
            "【魔道噬心】\n吞噬同阶修士精血，修为突飞猛进...",
            "【正道围剿】\n三大宗门联手追杀，需遁入幽冥之地..."
    );

    @FXML private Button btnPath1;
    @FXML private Button btnPath2;
    @FXML private Button btnPath3;

    // 处理三条分支的选择
    @FXML
    private void handlePath1() {
        showStoryBranch(PATH_1);
    }

    @FXML
    private void handlePath2() {
        showStoryBranch(PATH_2);
    }

    @FXML
    private void handlePath3() {
        showStoryBranch(PATH_3);
    }

    // 显示分支剧情（复用原有剧情弹窗）
    private void showStoryBranch(List<String> pages) {
        try {
            // 关闭当前选择窗口
            ((Stage) btnPath1.getScene().getWindow()).close();

            // 加载通用剧情弹窗
            FXMLLoader loader = new FXMLLoader(getClass().getResource("StoryDialog.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.initStyle(StageStyle.UNDECORATED);

            // 传递剧情数据
            StoryDialogController controller = loader.getController();
            controller.initStory(pages);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}