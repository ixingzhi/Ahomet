package com.shichuang.open.tool;

import java.math.BigDecimal;

public class RxBigDecimalTool {
    /**
     * 保留两位小数
     *
     * @param value
     * @param point
     * @return
     */
    public static String toDecimal(double value, int point) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
        return bd.toString();
    }

    /**
     * 保留两位小数
     *
     * @param value
     * @param point
     * @return
     */
    public static String toDecimal(String value, int point) {
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
        return bd.toString();
    }

    /**
     * 保留两位小数
     *
     * @param value
     * @param point
     * @return
     */
    public static String toDecimal(float value, int point) {
        double d = Double.parseDouble(String.valueOf(value));  // 保证精度不丢失
        BigDecimal bd = new BigDecimal(d);
        bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
        return bd.toString();
    }

    /**
     * 加法
     */
    public static BigDecimal add(double var1, double var2) {
        BigDecimal bigDecimal1 = new BigDecimal(var1);
        bigDecimal1 = bigDecimal1.setScale(2, BigDecimal.ROUND_HALF_UP);

        BigDecimal bigDecimal2 = new BigDecimal(var2);
        bigDecimal2 = bigDecimal2.setScale(2, BigDecimal.ROUND_HALF_UP);

        BigDecimal bigDecimal3 = bigDecimal1.add(bigDecimal2);
        return bigDecimal3;
    }
}
