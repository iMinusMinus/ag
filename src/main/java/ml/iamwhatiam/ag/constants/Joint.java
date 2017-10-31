/*
 * Copyright 2017 Zhongan.com All right reserved. This software is the
 * confidential and proprietary information of Zhongan.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Zhongan.com.
 */
package ml.iamwhatiam.ag.constants;

/**
 * 构成签名的多个字段连接方式
 * 
 * @author iMinusMinus
 * @since 2017-10-30
 */
public enum Joint {

    NONE("", "", "", ""), //各字段转为String后直接拼接，即value1value2。不同的合作方入参命名可能不一致，此方式在排序一致情况下不影响计算签名
    URL("", "=", "&", ""), //各字段以URL形式拼接，即key1=value1&key2=value2
    JSON("{", ":", ",", "}"),//各字段以JSON形式拼接，即key1:value1,key2:value2
    ;

    /**
     * 开始字符
     */
    private String begin;

    /**
     * 结束字符
     */
    private String end;

    /**
     * 键值分隔符
     */
    private String separator;

    /**
     * 值分隔符
     */
    private String follow;

    private Joint(String begin, String separator, String follow, String end) {
        this.begin = begin;
        this.separator = separator;
        this.follow = follow;
        this.end = end;
    }

    public String getBegin() {
        return begin;
    }

    public String getEnd() {
        return end;
    }

    public String getSeparator() {
        return separator;
    }

    public String getFollow() {
        return follow;
    }

}
