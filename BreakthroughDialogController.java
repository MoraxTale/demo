package com.example.demo1;

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
            "【天劫降临】\n九重雷劫轰然而至，需闭关百年抵御...",
            "【太上忘情】\n百年闭关后，你已彻底忘却人间情感，眼中只有天道法则",
            "【法则领悟】\n参悟天地法则时，发现一处上古仙府遗迹",
            "【仙府试炼】\n遗迹中考验的不是实力，而是对天道的理解",
            "【大道之争】\n其他无情道修士视你为竞争者，暗中设下杀局",
            "【无情巅峰】\n击败所有竞争者后，你站在了无情道的顶峰，却感到无尽的空虚"
    );

    private static final List<String> PATH_2 = List.of(
            "【人道轮回】\n入世历练，感悟众生悲欢...",
            "【因果纠缠】\n救下一名凡人，却卷入王朝纷争...",
            "【红尘炼心】\n你在凡间开设医馆，治病救人积累功德",
            "【王朝更迭】\n辅佐的皇子登基，你被奉为国师",
            "【信仰之力】\n百姓的香火信仰让你的修为有了新的突破",
            "【情劫考验】\n与一位凡人产生情愫，面临道心考验",
            "【超脱轮回】\n看破红尘后，你创出独门功法融合人道与仙道"
    );
    private static final List<String> PATH_3 = List.of(
            "【魔道噬心】\n吞噬同阶修士精血，修为突飞猛进...",
            "【正道围剿】\n三大宗门联手追杀，需遁入幽冥之地...",
            "【血海深仇】\n在幽冥之地发现血魔传承，但需献祭至亲",
            "【魔尊降世】\n炼成血魔大法后，你成为新一代魔尊",
            "【正邪大战】\n率领魔修攻上正道山门，血流成河",
            "【心魔反噬】\n过度使用魔功导致心魔失控，意识逐渐被吞噬",
            "【魔极生道】\n在彻底入魔前，你意外领悟魔道极致反生清净的奥秘"
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
