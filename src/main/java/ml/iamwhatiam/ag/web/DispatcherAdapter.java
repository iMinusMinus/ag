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
package ml.iamwhatiam.ag.web;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import ml.iamwhatiam.ag.dao.RpcConfigDao;
import ml.iamwhatiam.ag.dao.ServiceConfigDao;
import ml.iamwhatiam.ag.domain.MethodDomain;
import ml.iamwhatiam.ag.domain.RpcBeanDomain;
import ml.iamwhatiam.ag.exceptions.InsufficientConfigurationException;
import ml.iamwhatiam.ag.service.DispatcherService;
import ml.iamwhatiam.ag.service.impl.HttpDispatcherService;
import ml.iamwhatiam.ag.service.impl.RpcDispatcherService;
import ml.iamwhatiam.ag.support.OrderedComparator;
import ml.iamwhatiam.ag.util.ReflectionUtils;
import ml.iamwhatiam.ag.vo.FacadeVO;

/**
 * 请求分发适配器
 * 
 * @author iMinuMinus
 * @since 2017-09-22
 */
@Component("adapter")
public class DispatcherAdapter implements ApplicationContextAware {

    private ApplicationContext                    applicationContext;

    private Map<String, DispatcherExecutionChain> cached    = new ConcurrentHashMap<String, DispatcherExecutionChain>();

    @Resource
    private ServiceConfigDao                      serviceConfig;

    /**
     * 优先使用RPC分发
     */
    private boolean                               preferRpc = true;

    /*
     * (non-Javadoc)
     * @see
     * org.springframework.context.ApplicationContextAware#setApplicationContext
     * (org.springframework.context.ApplicationContext)
     */
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    public DispatcherExecutionChain getDispatcherExecutionChain(FacadeVO request) {
        MethodDomain service = serviceConfig.findByServiceName(request.getService());
        if (service == null) {
            throw new InsufficientConfigurationException("No configuration for " + request.getService());
        }
        DispatcherExecutionChain chain = cached.get(service.getInterfaceName() + ":" + request.getVersion());
        if (chain != null) {
            return chain;
        }
        Map<String, RpcConfigDao> config = getBeansOfType(RpcConfigDao.class);
        RpcBeanDomain domain = null;
        for (Map.Entry<String, RpcConfigDao> entry : config.entrySet()) {
            RpcConfigDao dao = entry.getValue();
            domain = dao.findByInterfaceNameAndVersion(service.getInterfaceName(), request.getVersion());
            if (domain != null) {
                break;
            }
        }
        if (domain == null) {
            throw new InsufficientConfigurationException("No suitable RPC configuration for " + request.getService());
        }
        DispatcherService dispatcher = null;
        if (ReflectionUtils.findClass(domain.getInterfaceName()) != null && preferRpc) {
            dispatcher = getBeanOfType(RpcDispatcherService.class, domain);
        }
        if (dispatcher == null) {
            dispatcher = getBeanOfType(HttpDispatcherService.class, domain);
        }
        if (dispatcher == null) {
            throw new InsufficientConfigurationException("No dispatcher bean matches type: " + domain.getRpcType());
        }
        chain = new DispatcherExecutionChain(dispatcher);
        Map<String, DispatcherInterceptor> inteceptors = getBeansOfType(DispatcherInterceptor.class);
        List<DispatcherInterceptor> inteceptorList = new ArrayList<DispatcherInterceptor>(inteceptors.values());
        Collections.sort(inteceptorList, new OrderedComparator());
        for (DispatcherInterceptor inteceptor : inteceptorList) {
            chain.addInteceptors(inteceptor);
        }
        cached.put(service.getInterfaceName() + ":" + request.getVersion(), chain);
        return chain;
    }

    public void removeCache(String key) {
        if (cached == null) {
            return;
        }
        cached.remove(key);
    }

    public void clearCache() {
        if (cached == null) {
            return;
        }
        cached.clear();
    }

    private <T> Map<String, T> getBeansOfType(Class<T> type) {
        Map<String, T> beans = applicationContext.getBeansOfType(type);
        ApplicationContext current = applicationContext;
        while (current.getParent() != null) {
            beans.putAll(current.getParent().getBeansOfType(type));
            current = current.getParent();
        }
        return beans;
    }

    private DispatcherService getBeanOfType(Class<? extends DispatcherService> type, RpcBeanDomain filter) {
        Map<String, ? extends DispatcherService> dispatcherBeans = getBeansOfType(type);
        for (DispatcherService bean : dispatcherBeans.values()) {
            if (bean.support(filter.getRpcType())) {
                return bean;
            }
        }
        return null;
    }

    public ServiceConfigDao getServiceConfig() {
        return serviceConfig;
    }

    public void setServiceConfig(ServiceConfigDao serviceConfig) {
        this.serviceConfig = serviceConfig;
    }

    public boolean isPreferRpc() {
        return preferRpc;
    }

    public void setPreferRpc(boolean preferRpc) {
        this.preferRpc = preferRpc;
    }

}
