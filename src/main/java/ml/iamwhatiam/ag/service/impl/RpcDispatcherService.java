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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public abstract class RpcDispatcherService implements DispatcherService, ApplicationContextAware {

    private static Logger        log = LoggerFactory.getLogger(RpcDispatcherService.class);

    protected ApplicationContext applicationContext;

    @Resource
    protected ServiceConfigDao   serviceConfig;
    
    /**
     * 默认注册的bean id为接口类全名，如果不是，子类需要重写此方法
     * 
     * @param interfaceName 接口类全名
     * @param version 服务提供方版本
     * @return
     */
    protected String getBeanName(String interfaceName, String version) {
    	return interfaceName;
    }

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

        String beanName = getBeanName(method.getInterfaceName(), request.getVersion());
        Object bean = applicationContext.getBean(beanName);

        String[] parameterTypes = new String[0];
        Object[] args = new Object[0];
        if (method.getParameters() != null && method.getParameters().size() > 0) {
            parameterTypes = new String[method.getParameters().size()];
            args = new Object[method.getParameters().size()];
            Deserializer deserializer = DeserializerFactory.getDeserializer(request.getFormat());
            Class<?>[] classes = new Class<?>[method.getParameters().size()];
            for (int i = 0; i < method.getParameters().size(); i++) {
                parameterTypes[i] = method.getParameters().get(i).getType();
                classes[i] = ReflectionUtils.findClass(parameterTypes[i]);
            }
            if (parameterTypes.length == 1) {//parameter:{}
                args[0] = deserializer.deserializeObject(request.getParameters(), classes[0]);
            } else {//parameter:[]
                List<Object> data = deserializer.deserializeArray(request.getParameters(), classes);
                for (int i = 0; i < data.size(); i++) {
                    args[i] = data.get(i);
                }
            }
        }
        processBeforeInvoke(beanName);
        log.debug("RPC服务[{}]请求参数：{}", request.getService(), args);
        Object result;
        try {
            result = ReflectionUtils.invoke(method.getMethodName(), method.getInterfaceName(), parameterTypes, bean,
                    args);
        } catch (Exception e) {
            throw new RpcInvokingException(e);
        }
        log.debug("RPC服务[{}]返回结果：{}", request.getService(), result);
        processAfterInvoke(beanName);
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
