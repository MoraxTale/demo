package com.example.demo1;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Window;

import java.util.List;

public class StoryDialogController {
    @FXML private Label lblText;
    @FXML private Button btnNext;
    @FXML private VBox root;

    private List<String> storyPages; // 存储所有剧情文本
    private int currentPage = 0;     // 当前显示的页面索引

    /**
     * 初始化剧情数据
     * @param pages 自定义的剧情文本列表（按顺序）
     */
    public void initStory(List<String> pages) {
        this.storyPages = pages;
        showPage(0); // 显示第一页
    }
    // 在 StoryDialogController 类中添加：
    public void setCustomButtonText(String text, int targetPage) {
        if (currentPage == targetPage) {
            btnNext.setText(text);
        }
    }

    // 显示指定页面的文本
    private void showPage(int pageIndex) {
        if (pageIndex >= storyPages.size()) {
            // 安全关闭窗口（通过已存在的组件获取窗口）
            Window window = lblText.getScene().getWindow();
            if (window != null) {
                window.hide();
            }
            return;
        }
        lblText.setText(storyPages.get(pageIndex));
        currentPage = pageIndex;

        // 动态更新按钮文本
        if (pageIndex == storyPages.size() - 1) {
            btnNext.setText("关闭");
        } else {
            btnNext.setText("继续");
        }
    }

    // 点击按钮切换到下一页
    @FXML
    private void handleNext() {
        showPage(currentPage + 1);
    }
}