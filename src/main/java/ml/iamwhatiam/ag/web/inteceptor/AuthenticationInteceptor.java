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

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import ml.iamwhatiam.ag.constants.Default;
import ml.iamwhatiam.ag.constants.Rococo;
import ml.iamwhatiam.ag.dao.ServiceConfigDao;
import ml.iamwhatiam.ag.domain.MethodDomain;
import ml.iamwhatiam.ag.exceptions.InteceptException;
import ml.iamwhatiam.ag.service.CacheService;
import ml.iamwhatiam.ag.support.Deserializer;
import ml.iamwhatiam.ag.support.DeserializerFactory;
import ml.iamwhatiam.ag.support.Serializer;
import ml.iamwhatiam.ag.support.SerializerFactory;
import ml.iamwhatiam.ag.vo.FacadeVO;
import ml.iamwhatiam.ag.web.DispatcherInterceptor;
import ml.iamwhatiam.ag.web.ParameterInteceptor;

/**
 * 登入验证及参数处理
 *
 * @author iMinusMinus
 * @since 2017-10-04
 */
@Component
public class AuthenticationInteceptor implements DispatcherInterceptor, ParameterInteceptor {

    @Resource
    private ServiceConfigDao serviceConfig;

    @Resource
    private CacheService     cache;

    public int getOrder() {
        return 0;
    }

    /**
     * 是否需要触发
     */
    public boolean canIntecept(FacadeVO param) {
        MethodDomain service = serviceConfig.findByServiceName(param.getService());
        int rococo = service.getRococo();
        return Rococo.AUTHENTICATION.shouldDecorate(rococo);
    }

    /**
     * 替换凭证为用户标识
     */
    public void intecept(FacadeVO param) {
        //使用token换取OpenId
        Deserializer deserializer = DeserializerFactory.getDeserializer(param.getFormat());
        Map<Object, Object> map = deserializer.deserializeObject(param.getParameters(), Map.class);
        String accessToken = (String) map.get(Default.OAUTH_TOKEN.get());
        map.put(Default.OPEN_ID.get(), cache.get(accessToken));
        Serializer serializer = SerializerFactory.getSerializer(param.getFormat());
        param.setParameters(serializer.serialize(map));
    }

    /**
     * 判断是否登入
     */
    public boolean preDispatch(FacadeVO param, Object dispatcher) throws InteceptException {
        Deserializer deserializer = DeserializerFactory.getDeserializer(param.getFormat());
        Map<Object, Object> map = deserializer.deserializeObject(param.getParameters(), Map.class);
        String accessToken = (String) map.get(Default.OAUTH_TOKEN.get());
        if (cache.get(accessToken) == null) {
            throw new InteceptException("Not Login");
        }
        return false;
    }

    public Object postDispatch(Object result, Object handler) throws InteceptException {
        // DO NOTHING
        return result;
    }

    public ServiceConfigDao getServiceConfig() {
        return serviceConfig;
    }

    public void setServiceConfig(ServiceConfigDao serviceConfig) {
        this.serviceConfig = serviceConfig;
    }

    public CacheService getCache() {
        return cache;
    }

    public void setCache(CacheService cache) {
        this.cache = cache;
    }

}
