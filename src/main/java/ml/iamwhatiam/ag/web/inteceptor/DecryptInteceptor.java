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
package ml.iamwhatiam.ag.web.inteceptor;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import ml.iamwhatiam.ag.constants.Joint;
import ml.iamwhatiam.ag.constants.Rococo;
import ml.iamwhatiam.ag.constants.SerializeFormat;
import ml.iamwhatiam.ag.dao.PartnerConfigDao;
import ml.iamwhatiam.ag.dao.ServiceConfigDao;
import ml.iamwhatiam.ag.domain.MethodDomain;
import ml.iamwhatiam.ag.domain.PartnerConfigurationDomain;
import ml.iamwhatiam.ag.exceptions.InteceptException;
import ml.iamwhatiam.ag.support.SerializerFactory;
import ml.iamwhatiam.ag.util.SecurityUtils;
import ml.iamwhatiam.ag.vo.FacadeVO;
import ml.iamwhatiam.ag.web.ParameterInteceptor;

/**
 * 安全阻截: 验签和解密
 * 
 * @author iMinusMinus
 * @since 2017-11-06
 */
@Component
public class DecryptInteceptor implements ParameterInteceptor {

    @Resource
    private ServiceConfigDao serviceConfig;

    @Resource
    private PartnerConfigDao partnerConfig;

    public boolean preDispatch(FacadeVO param, Object dispatcher) throws InteceptException {
        return false;
    }

    public Object postDispatch(Object result, Object handler) throws InteceptException {
        return result;
    }

    /**
     * 验签解密一般在正式处理前
     */
    public int getOrder() {
        return 0x40000000;
    }

    public boolean canIntecept(FacadeVO param) {
        MethodDomain md = serviceConfig.findByServiceName(param.getService());
        return Rococo.SIGNATURE_VERIFICATION.shouldDecorate(md.getRococo())
                && Rococo.DECIPHERMENT.shouldDecorate(md.getRococo());
    }

    public void intecept(FacadeVO param) {
        PartnerConfigurationDomain config = partnerConfig.findByClient(param.getClient());
        verifyAndDecrypt(param, config);
    }

    /**
     * 验签与解密
     * 
     * @param vo
     * @param config
     * @return
     */
    private void verifyAndDecrypt(FacadeVO vo, PartnerConfigurationDomain config) {
        String signature = vo.getSign();
        String ciphered = vo.getParameters();
        Map<String, String> origin = new HashMap<String, String>();
        origin.put("client", vo.getClient());
        origin.put("parameters", vo.getParameters());
        origin.put("format", vo.getFormat().name().toLowerCase());
        origin.put("sign", vo.getSign());
        origin.put("signType", vo.getSignType());
        origin.put("service", vo.getService());
        origin.put("timestamp", String.valueOf(vo.getTimestamp()));
        origin.put("version", vo.getVersion());
        origin.put("charset", vo.getCharset());
        if (config.getMapping() != null && !config.getMapping().isEmpty()) {
            for (Map.Entry<String, String> entry : config.getMapping().entrySet()) {
                if (entry.getValue() == null) {
                    origin.remove(entry.getKey());
                } else {
                    origin.put(entry.getValue(), origin.get(entry.getKey()));
                }
            }
        }
        byte[] data = null;
        if (origin.size() == 0) {
            data = vo.getCharset() == null ? ciphered.getBytes() : ciphered.getBytes(Charset.forName(vo.getCharset()));
        } else {
            String content = beforeSign(origin, config.getJoint());
            data = vo.getCharset() == null ? content.getBytes() : content.getBytes(Charset.forName(vo.getCharset()));
        }
        if (!SecurityUtils.verify(signature, data, vo.getSignType(), config.getKey())) {
            throw new SecurityException("验证签名失败");
        }
        byte[] plain = SecurityUtils.decrypt(config.getTransformation(), ciphered, config.getPrivateKey());
        String palinText = new String(plain, Charset.forName(vo.getCharset()));
        vo.setParameters(palinText);
    }

    /**
     * 签名前组织参数为String
     * 
     * @param content 公共参数map
     * @param joint 参数内、外连接方式
     * @return 连接后的字符串
     */
    private String beforeSign(Map<String, String> content, Joint joint) {
        if (joint == Joint.JSON) {
            return SerializerFactory.getSerializer(SerializeFormat.JSON).serialize(content);
        }
        StringBuilder sb = new StringBuilder(joint.getBegin());
        for (Map.Entry<String, String> entry : content.entrySet()) {
            sb.append(entry.getKey()).append(joint.getSeparator());
            sb.append(entry.getValue()).append(joint.getFollow());
        }
        sb.setLength(sb.length() - 1);
        sb.append(joint.getEnd());
        return sb.toString();
    }

    public ServiceConfigDao getServiceConfig() {
        return serviceConfig;
    }

    public void setServiceConfig(ServiceConfigDao serviceConfig) {
        this.serviceConfig = serviceConfig;
    }

    public PartnerConfigDao getPartnerConfig() {
        return partnerConfig;
    }

    public void setPartnerConfig(PartnerConfigDao partnerConfig) {
        this.partnerConfig = partnerConfig;
    }

}
