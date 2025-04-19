package com.example.demo;

/**
 * 数值格式化工具类
 * 功能：将长整型数值转换为带单位后缀的字符串
 * 规则：
 *   1000 = 1k
 *   1000k = 1m
 *   1000m = 1b
 *   1000b = 1t
 *   1000t = 1q
 */
public class NumberFormatter {
    // 单位定义表（单位符号 -> 对应数值）
    private static final String[] UNITS = {"", "k", "m", "b", "t", "q"};
    private static final long[] UNIT_VALUES = {
            1L,                  // 1
            1_000L,             // 1k
            1_000_000L,         // 1m
            1_000_000_000L,     // 1b
            1_000_000_000_000L, // 1t
            1_000_000_000_000_000L // 1q
    };

    /**
     * 格式化数值（默认保留1位小数）
     * @param value 需要格式化的原始数值
     * @return 带单位后缀的格式化字符串
     */
    public static String formatNumber(long value) {
        return formatNumber(value, 1);
    }

    /**
     * 带精度控制的格式化方法
     * @param value 需要格式化的原始数值
     * @param precision 小数位数（0-3）
     * @return 带单位后缀的格式化字符串
     */
    public static String formatNumber(long value, int precision) {
        if (value == 0) return "0";
        if (precision < 0 || precision > 3) {
            throw new IllegalArgumentException("Precision must be between 0-3");
        }

        // 确定符号
        String sign = value < 0 ? "-" : "";
        long absValue = Math.abs(value);

        // 查找适用的最大单位
        int unitIndex = 0;
        for (int i = UNIT_VALUES.length - 1; i >= 0; i--) {
            if (absValue >= UNIT_VALUES[i]) {
                unitIndex = i;
                break;
            }
        }

        // 计算格式化后的值
        double formattedValue = (double) absValue / UNIT_VALUES[unitIndex];

        // 构建格式字符串
        String formatPattern;
        if (formattedValue == (long) formattedValue) {
            // 整数值不显示小数
            formatPattern = "%s%d%s";
            return String.format(formatPattern, sign, (long) formattedValue, UNITS[unitIndex]);
        } else {
            // 动态精度控制
            formatPattern = "%s%." + precision + "f%s";
            return String.format(formatPattern, sign, formattedValue, UNITS[unitIndex]);
        }
    }
}