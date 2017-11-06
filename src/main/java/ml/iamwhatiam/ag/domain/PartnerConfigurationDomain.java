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

import java.util.Map;

import ml.iamwhatiam.ag.constants.Joint;
import ml.iamwhatiam.ag.constants.SerializeFormat;

/**
 * 针对合作方的个性化配置
 * 
 * @author iMinusMinus
 * @since 2017-10-30
 */
public class PartnerConfigurationDomain extends BaseDomain {

    private static final long   serialVersionUID = -6422814185044069652L;

    /**
     * 分配给合作方的标识
     * 
     * @see ml.iamwhatiam.ag.vo.FacadeVO#client
     */
    private String              appid;

    /**
     * 加密算法
     */
    private String              transformation   = "RSA";

    /**
     * 输出格式（如果请求时格式为空，考虑使用此输入格式）
     */
    private SerializeFormat     format;

    /**
     * 输出字符编码
     */
    private String              charset          = "UTF-8";

    /**
     * 合作方提供的公钥
     */
    private String              key;

    /**
     * 签名算法：
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
    private String              signAlgorithm    = "SHA1withRSA";

    /**
     * 验签时原参数的拼接方式
     */
    private Joint               joint            = Joint.NONE;

    /**
     * 针对合作方的私钥
     */
    private String              privateKey;

    /**
     * 参数映射：空Map，无映射；key不为null，value为null，从原参数移除出该key；若key全部移除，则只计算参数值
     */
    private Map<String, String> mapping;

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getTransformation() {
        return transformation;
    }

    public void setTransformation(String transformation) {
        this.transformation = transformation;
    }

    public SerializeFormat getFormat() {
        return format;
    }

    public void setFormat(SerializeFormat format) {
        this.format = format;
    }

    public String getCharset() {
        return charset;
    }

    public void setCharset(String charset) {
        this.charset = charset;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getSignAlgorithm() {
        return signAlgorithm;
    }

    public void setSignAlgorithm(String signAlgorithm) {
        this.signAlgorithm = signAlgorithm;
    }

    public Joint getJoint() {
        return joint;
    }

    public void setJoint(Joint joint) {
        this.joint = joint;
    }

    public String getPrivateKey() {
        return privateKey;
    }

    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
    }

    public Map<String, String> getMapping() {
        return mapping;
    }

    public void setMapping(Map<String, String> mapping) {
        this.mapping = mapping;
    }

    @Override
    public String toString() {
        return "PartnerConfigurationDomain [appid=" + appid + ", transformation=" + transformation + ", format="
                + format + ", charset=" + charset + ", key=" + key + ", signAlgorithm=" + signAlgorithm + ", joint="
                + joint + ", privateKey=" + privateKey + ", mapping=" + mapping + ", id=" + id + ", creator=" + creator
                + ", gmtCreated=" + gmtCreated + ", modifier=" + modifier + ", gmtModified=" + gmtModified
                + ", deleted=" + deleted + "]";
    }

}
