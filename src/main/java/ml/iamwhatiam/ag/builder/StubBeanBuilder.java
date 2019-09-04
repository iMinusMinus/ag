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
package ml.iamwhatiam.ag.builder;

import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;

import ml.iamwhatiam.ag.domain.RpcBeanDomain;

/**
 * local proxy bean builder
 * 
 * @author iMinusMinus
 * @since 2017-09-25
 */
@Slf4j
public abstract class StubBeanBuilder implements ApplicationContextAware, InitializingBean {

    protected ApplicationContext applicationContext;

    /**
     * 加载数据库配置（看做动态xml配置）
     * 
     * @return
     */
    protected abstract List<? extends RpcBeanDomain> loadDefinition();
    
    /**
     * 构建bean定义
     * 
     * @param beanDefintion
     * @return
     */
    protected abstract BeanDefinitionBuilder buildBeanDefintion(RpcBeanDomain beanDefintion);

    /**
     * 对beanFactory进行处理
     * 
     * @param facotyBeanName &beanName
     */
    protected void processFactoryBean(String facotyBeanName) {
        //let sub class override
    }

    /**
     * 对bean进行处理
     * 
     * @param beanName
     */
    protected void processBean(String beanName) {
        //let sub class override
    }

    /*
     * (non-Javadoc)
     * @see
     * org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        log.info("开始加载数据库RPC stub bean配置");
        //1. load bean definition
        List<? extends RpcBeanDomain> beanDefintions = loadDefinition();
        int added = 0;
        for (RpcBeanDomain beanDefintion : beanDefintions) {
            if (beanDefintion.getInterfaceName() == null || beanDefintion.getInterfaceName().trim().length() <= 0) {
                log.error("配置的RPC stub bean错误：{}", beanDefintion);
                continue;
            }
            String beanName = beanDefintion.getBeanId();
            if (applicationContext.containsBean(beanName)) {
                log.warn("bean [{}]已存在", beanName);
                continue;
            }
            //2. assemble bean definition
            BeanDefinitionBuilder builder = buildBeanDefintion(beanDefintion);
            //3. registry bean definition
            //applicationContext is XmlWebApplicationContext, which is ConfigurableApplicationContext
            ConfigurableApplicationContext context = (ConfigurableApplicationContext) applicationContext;
            //beanFactory is DefaultListableBeanFactory, which is BeanDefinitionRegistry
            BeanDefinitionRegistry registry = (BeanDefinitionRegistry) context.getBeanFactory();
            registry.registerBeanDefinition(beanName, builder.getBeanDefinition());
            added++;
            //4. process factory bean and bean
            processFactoryBean(BeanFactory.FACTORY_BEAN_PREFIX + beanName);
            processBean(beanName);
            log.info("bean [{}] 加载初始化完毕", beanName);
        }
        log.info("完成加载数据库RPC bean配置: {}", added);
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
