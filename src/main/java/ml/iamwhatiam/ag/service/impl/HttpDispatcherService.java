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
package ml.iamwhatiam.ag.service.impl;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import ml.iamwhatiam.ag.constants.SerializeFormat;
import ml.iamwhatiam.ag.dao.ServiceConfigDao;
import ml.iamwhatiam.ag.domain.MethodDomain;
import ml.iamwhatiam.ag.domain.ParameterTypeDomain;
import ml.iamwhatiam.ag.exceptions.InsufficientConfigurationException;
import ml.iamwhatiam.ag.exceptions.NoSuchPublisherException;
import ml.iamwhatiam.ag.service.DispatcherService;
import ml.iamwhatiam.ag.support.Deserializer;
import ml.iamwhatiam.ag.support.DeserializerFactory;
import ml.iamwhatiam.ag.util.ReflectionUtils;
import ml.iamwhatiam.ag.vo.FacadeVO;
import ml.iamwhatiam.ag.vo.HttpRequestVO;

/**
 * 如果RPC支持通过HTTP调用，且服务配置的调用方式为HTTP调用，则使用HTTP调用。
 * 
 * @author iMinusMinus
 * @since 2017-09-15
 */
public abstract class HttpDispatcherService implements DispatcherService {

    private Logger             log = LoggerFactory.getLogger(HttpDispatcherService.class);

    @Resource
    protected ServiceConfigDao serviceConfig;

    @Resource
    protected RestTemplate     rest;

    public boolean support(String type) {
        return "HTTP".equalsIgnoreCase(type);
    }

    /**
     * 组装HTTP请求URL
     * 
     * @param service 服务名称
     * @param method 方法配置信息
     * @param version 服务版本
     * @return 受理URL
     */
    protected abstract String getRequestUrl(String service, MethodDomain method, String version);

    /**
     * 组装HTTP请求头
     * 
     * @return
     */
    protected abstract HttpHeaders getRequestHeader();

    /**
     * 将原始请求重新组装成新的请求
     * 
     * @param req 请求参数类型和请求数据
     * @param md 方法配置信息
     * @return
     */
    protected abstract Object recombineBody(HttpRequestVO req, MethodDomain md);

    /**
     * 组装HTTP请求参数
     * 
     * @param md 方法配置信息
     * @param parameters 参数信息
     * @param format 参数格式：json、xml、protobuffer
     * @return 组装结果
     */
    protected Object getRequestBody(MethodDomain md, String parameters, SerializeFormat format) {
        List<ParameterTypeDomain> parameterTypes = md.getParameters();
        HttpRequestVO req = new HttpRequestVO();
        String[] argsTypes = new String[0];
        Object[] argsObjects = new Object[0];
        if (parameterTypes != null && parameterTypes.size() > 0) {
            argsTypes = new String[parameterTypes.size()];
            argsObjects = new Object[parameterTypes.size()];
            Class<?>[] classes = new Class<?>[parameterTypes.size()];
            Deserializer deserializer = DeserializerFactory.getDeserializer(format);
            for (int i = 0; i < parameterTypes.size(); i++) {
                argsTypes[i] = parameterTypes.get(i).getType();
                classes[i] = ReflectionUtils.findClass(argsTypes[i]);
            }
            if (parameterTypes.size() == 1) {
                argsObjects[0] = deserializer.deserializeObject(parameters, classes[0]);
            } else {
                List<Object> data = deserializer.deserializeArray(parameters, classes);
                for (int i = 0; i < data.size(); i++) {
                    argsObjects[i] = data.get(i);
                }
            }
        }
        req.setParameterTypes(argsTypes);
        req.setArgs(argsObjects);
        return recombineBody(req, md);
    }

    /**
     * 处理返回结果
     * 
     * @param response
     * @param returnType 方法返回类型，可能是泛型！
     * @return
     */
    protected abstract Object handleResponseBody(HttpEntity<String> response, Type returnType);

    protected Type extractReturnType(MethodDomain md) {
        if (!md.hasGenericReturn()) {
            return ReflectionUtils.findClass(md.getReturnType());
        }
        try {
            Class<?> clazz = ReflectionUtils.findClass(md.getInterfaceName());
            Class<?>[] parameterTypes = new Class<?>[md.getParameters().size()];
            for (int index = 0; index < md.getParameters().size(); index++) {
                parameterTypes[index] = ReflectionUtils.findClass(md.getParameters().get(index).getType());
            }
            Method method = ReflectionUtils.findAccessibleMethod(md.getMethodName(), clazz, parameterTypes);
            return method.getGenericReturnType();
        } catch (Exception e) {
            log.error(e.getMessage());
            return ReflectionUtils.findClass(md.getReturnType());
        }
    }

    /**
     * 使用HTTP(s)转发处理请求
     * 
     * @param request
     * @return result
     * @throws InsufficientConfigurationException
     * @throws NoSuchPublisherException
     * @throws RestClientException
     */
    public final Object doDispatch(FacadeVO request) {
        MethodDomain method = serviceConfig.findByServiceName(request.getService());
        if (method == null) {
            log.error("服务[{}]未配置", request.getService());
            throw new InsufficientConfigurationException();
        }
        String url = getRequestUrl(request.getService(), method, request.getVersion());
        log.info("服务[{}]调用地址[{}]", request.getService(), url);
        HttpHeaders header = getRequestHeader();
        Object body = getRequestBody(method, request.getParameters(), request.getFormat());
        HttpEntity<Object> req = new HttpEntity<Object>(body, header);
        log.debug("服务[{}]HTTP请求参数：{}", request.getService(), req);
        ResponseEntity<String> response = rest.postForEntity(url, req, String.class);
        log.debug("服务[{}]HTTP返回结果：{}", request.getService(), response);
        Object result = handleResponseBody(response, extractReturnType(method));
        log.info("服务[{}]调用返回结果：{}", request.getService(), result);
        return result;
    }

    public ServiceConfigDao getServiceConfig() {
        return serviceConfig;
    }

    public void setServiceConfig(ServiceConfigDao serviceConfig) {
        this.serviceConfig = serviceConfig;
    }

    public RestTemplate getRest() {
        return rest;
    }

    public void setRest(RestTemplate rest) {
        this.rest = rest;
    }

}
