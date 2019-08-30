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

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ml.iamwhatiam.ag.constants.Joint;
import ml.iamwhatiam.ag.constants.SerializeFormat;

/**
 * 针对合作方的个性化配置
 * 
 * @author iMinusMinus
 * @since 2017-10-30
 */
@Getter
@Setter
@ToString(callSuper = true)
public class PartnerConfigDomain extends BaseDomain {

    private static final long                   serialVersionUID = -6422814185044069652L;

    /**
     * 分配给合作方的标识
     * 
     * @see ml.iamwhatiam.ag.vo.FacadeVO#appId
     */
    private String appId;

    /**
     * 加密算法，默认为RSA
     */
    private String transformation;

    /**
     * 输出格式（如果请求时格式为空，考虑使用此输入格式），默认为JSON
     */
    private SerializeFormat format;

    /**
     * 输出字符编码，默认为UTF-8
     */
    private String charset;

    /**
     * 合作方提供的公钥
     */
    private String publicKey;

    /**
     * 签名算法（RSA加密时，默认为SHA1withRSA）或摘要算法：
     * <ul>
     * <li>DSA
     * <ul>
     * <li>RawDSA</li>
     * <li>SHA1withDSA</li>
     * </ul>
     * </li>
     * <li>RSA</li>
     * <ul>
     * <li>MD2withRSA</li>
     * <li>MD5withRSA</li>
     * <li>SHA1withRSA</li>
     * <li>SHA256withRSA</li>
     * <li>SHA384withRSA</li>
     * <li>SHA512withRSA</li>
     * </ul>
     * </ul>
     */
    private String signAlgorithm;

    /**
     * 验签时原参数的拼接方式
     */
    private Joint joint;

    /**
     * 针对合作方的私钥
     */
    private String privateKey;

    /**
     * 参数映射
     */
    private List<GateWayParameterMappingDomain> parameterMappings;

}
