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
package ml.iamwhatiam.ag.domain;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ml.iamwhatiam.ag.constants.GatewayField;
import ml.iamwhatiam.ag.constants.HttpParamLocation;

/**
 * 网关个性化配置信息
 * 
 * @author iMinusMinus
 * @since 2018-02-09
 */
@Getter
@Setter
@ToString
public class GateWayParameterMappingDomain extends BaseDomain {

    private static final long serialVersionUID = -5315293004766521859L;

    /**
     * 合作方标识
     */
    private String appId;

    /**
     * 字段名称（当参数在HTTP路径时，此值类型为int）
     */
    @NotNull
    private String filedName;

    /**
     * 1: 请求我方入参; 0: 我方响应出参
     */
    @NotNull
    private int io;

    /**
     * 字段类型
     */
    private String fieldType;

    /**
     * 字段值类型转换器
     */
    private String typeConverter;

    /**
     * 字段格式
     */
    private String fieldFormat;

    /**
     * 映射字段名称
     */
    private GatewayField mappingName;

    /**
     * 字段默认值
     */
    private String defaultValue;

    /**
     * 是否作为签名部分
     */
    private boolean signPart;

    /**
     * 参数位置
     */
    private HttpParamLocation location;

}
