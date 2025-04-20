package com.example.demo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StageStoryConfig {
    // 境界等级 -> 剧情文本映射（可自由修改内容）
    public static final Map<Integer, List<String>> STAGE_STORIES = new HashMap<>();

    static {
        // 凡人 -> 炼气
        STAGE_STORIES.put(1, List.of(
                "【初入炼气】\n丹田中一缕灵气凝而不散，你正式踏入炼气期！",
                "【炼气要诀】\n需每日吐纳，巩固根基。前方有灵草秘境可探索..."
        ));

        // 炼气 -> 筑基
        STAGE_STORIES.put(2, List.of(
                "【筑基大成】\n灵气化液，筑成道基！",
                "【天道感应】\n识海中浮现《太虚筑基经》，需寻五行灵物..."
        ));

        // 筑基 -> 金丹
        STAGE_STORIES.put(3, List.of(
                "【金丹初成】\n丹田结出金丹，寿元增至五百载！",
                "【丹劫警示】\n三年后将有雷劫，需炼制渡厄丹..."
        ));

        // 其他境界依此类推...
    }
}
