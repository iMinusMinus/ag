/**
 * MIT License
 * 
 * Copyright (c) 2017 iMinusMinus
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package ml.iamwhatiam.ag.vo;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ml.iamwhatiam.ag.constants.SerializeFormat;

/**
 * 请求抽象
 * 
 * @author iMinusMinus
 * @since 2017-09-11
 */
@Getter
@Setter
@ToString
public class FacadeVO implements Serializable {

    private static final long serialVersionUID = 7671044275686455797L;

    /**
     * 调用者标识
     */
    private String            appId;

    /**
     * 参数
     */
    private String            parameters;

    /**
     * 参数格式：JSON、XML
     */
    @NotNull
    private SerializeFormat   format;

    /**
     * 签名
     */
    private String            sign;

    /**
     * 签名算法: sha1WithRSA, sha1WithDSA
     */
    private String            signType;

    /**
     * API标识
     */
    @NotNull
    private String            service;

    /**
     * 调用时间
     */
    private long              timestamp;

    /**
     * 调用版本
     */
    @NotNull
    private String            version;

    /**
     * 字符编码（仅针对参数部分）:UTF-8
     */
    private String            charset;

}
