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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;

import ml.iamwhatiam.ag.util.Id;
import ml.iamwhatiam.ag.util.ReflectionUtils;

/**
 * 将请求通过服务名找到实际的发布者进行调用
 * 
 * @author liangming
 * @since 2017-09-18
 */
public class HsfDispatcherServiceImpl extends RpcDispatcherService {

    private static Logger  log                = LoggerFactory.getLogger(HsfDispatcherServiceImpl.class);

    private final String   CONTEXT_MDC_METHOD = "setInvokeContextThreadLocal";

    private final String   CLASS              = "com.taobao.hsf.app.spring.util.HSFSpringConsumerBean";

    private final String[] PARAMETER_TYPES    = { "java.lang.ThreadLocal" };

    public boolean support(String type) {
        return "HSF".equalsIgnoreCase(type);
    }

    /**
     * 设置线程上下文
     */
    @Deprecated
    protected final void processBeforeInvoke(String beanName) {
        try {
            Object factoryBean = applicationContext.getBean(BeanFactory.FACTORY_BEAN_PREFIX + beanName);
            Object args[] = new Object[1];
            ThreadLocal<String> cid = new ThreadLocal<String>();
            cid.set(Id.next());
            args[0] = cid;
            ReflectionUtils.invoke(CONTEXT_MDC_METHOD, CLASS, PARAMETER_TYPES, factoryBean, args);// 未达预期
            log.debug("HSF调用前设置线程上下文为[{}]", cid.get());
        } catch (Exception ignore) {
            log.warn(ignore.getMessage(), ignore);
        }
    }

}
