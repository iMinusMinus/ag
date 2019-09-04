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

import java.util.List;

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;
import ml.iamwhatiam.ag.domain.ParameterTypeDomain;
import ml.iamwhatiam.ag.domain.RpcBeanDomain;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import ml.iamwhatiam.ag.dao.ServiceConfigDao;
import ml.iamwhatiam.ag.domain.MethodDomain;
import ml.iamwhatiam.ag.exceptions.InsufficientConfigurationException;
import ml.iamwhatiam.ag.exceptions.RpcInvokingException;
import ml.iamwhatiam.ag.service.DispatcherService;
import ml.iamwhatiam.ag.support.Deserializer;
import ml.iamwhatiam.ag.support.DeserializerFactory;
import ml.iamwhatiam.ag.util.ReflectionUtils;
import ml.iamwhatiam.ag.vo.FacadeVO;

/**
 * 使用RPC分发调用（依赖服务提供者接口、入参、返回类）
 * 
 * @author iMinusMinus
 * @since 2017-09-20
 */
@Slf4j
public abstract class RpcDispatcherService implements DispatcherService, ApplicationContextAware {

    protected ApplicationContext applicationContext;

    @Resource
    protected ServiceConfigDao   serviceConfig;

    /**
     * 获取服务配置
     * @param interfaceName
     * @param version
     * @return
     */
    protected abstract RpcBeanDomain getRpcConfig(String interfaceName, String version);

    /**
     * 泛化调用方法名
     * @return
     */
    protected abstract String getGenericMethodName();

    /**
     * 泛化调用接口名
     * @return
     */
    protected abstract String getGeneircInterfaceName();

    /**
     * 泛化调用参数类型
     * @return
     */
    protected abstract Class[] getGenericParameterTypes();

    /**
     * 生成泛化调用入参
     * @param methodName
     * @param parameters
     * @param parameterTypes
     * @return
     */
    protected abstract Object[] assembleGenericRequest(String methodName, String parameters, List<ParameterTypeDomain> parameterTypes);
    
    /**
     * 获取配置-->寻找bean-->组装参数-->调用
     * 
     * @param request
     * @return
     * @throws InsufficientConfigurationException
     * @throws BeansException
     * @throws RpcInvokingException
     */
    @Override
    public final Object doDispatch(FacadeVO request) {
        MethodDomain method = serviceConfig.findByServiceName(request.getService());
        if (method == null) {
            log.error("服务[{}]未配置", request.getService());
            throw new InsufficientConfigurationException();
        }
        String methodName = method.getMethodName();
        String interfaceName = method.getInterfaceName();
        Class[] parameterTypes = new Class[0];
        Object[] args = new Object[0];

        RpcBeanDomain rpcConfig = getRpcConfig(method.getInterfaceName(), request.getVersion());
        if(rpcConfig == null) {
            log.error("接口[{}:{}]未配置", method.getInterfaceName(), request.getVersion());
            throw new InsufficientConfigurationException();
        }

        Object bean = applicationContext.getBean(rpcConfig.getBeanId());
        if(request.getParameters() != null && (method.getParameters() == null || method.getParameters().size() == 0)) {
            log.warn("服务[{}]有入参，但未配置参数类型", request.getService());
        }
        if(request.getParameters() == null && method.getParameters() != null && method.getParameters().size() != 0) {
            throw new RuntimeException("服务[" + request.getService() + "]请求参数确实");
        }
        //使用泛化调用，接口、入参、出参class可以不需要在classpath
        if(rpcConfig.useGeneric()) {
            methodName = getGenericMethodName();
            interfaceName = getGeneircInterfaceName();
            parameterTypes = getGenericParameterTypes();
            args = assembleGenericRequest(method.getMethodName(), request.getParameters(), method.getParameters());
        } else if(method.getParameters() != null && method.getParameters().size() != 0){
            String[] parameTypes = new String[method.getParameters().size()];
            args = new Object[method.getParameters().size()];
            Deserializer deserializer = DeserializerFactory.getDeserializer(request.getFormat());
            parameterTypes = new Class<?>[method.getParameters().size()];
            for (int i = 0; i < method.getParameters().size(); i++) {
                parameTypes[i] = method.getParameters().get(i).getType();
                parameterTypes[i] = ReflectionUtils.findClass(parameTypes[i]);
            }
            if (parameTypes.length == 1) {//parameter:{}
                args[0] = deserializer.deserializeObject(request.getParameters(), parameterTypes[0]);
            } else {//parameter:[]
                List<Object> data = deserializer.deserializeArray(request.getParameters(), parameterTypes);
                for (int i = 0; i < data.size(); i++) {
                    args[i] = data.get(i);
                }
            }
        }

        processBeforeInvoke(rpcConfig.getBeanId());
        log.debug("RPC服务[{}]请求参数：{}", request.getService(), args);
        Object result;
        try {
            result = ReflectionUtils.invoke(methodName, interfaceName, parameterTypes, bean, args);
        } catch (Exception e) {
            throw new RpcInvokingException(e);
        }
        log.debug("RPC服务[{}]返回结果：{}", request.getService(), result);
        processAfterInvoke(rpcConfig.getBeanId());
        return result;
    }

    /**
     * 请求前处理bean、factoryBean
     */
    protected void processBeforeInvoke(String beanName) {
        //DO NOTHING, let sub class do what they want
    }

    /**
     * 请求后处理bean、factoryBean
     * 
     * @param beanName
     */
    protected void processAfterInvoke(String beanName) {
        //DO NOTHING, let sub class do what they want
    }

    /*
     * (non-Javadoc)
     * @see
     * org.springframework.context.ApplicationContextAware#setApplicationContext
     * (org.springframework.context.ApplicationContext)
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}
