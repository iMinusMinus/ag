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

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;

import ml.iamwhatiam.ag.dao.HsfConsumerConfigDao;
import ml.iamwhatiam.ag.domain.HSFSpringConsumerBeanDomain;
import ml.iamwhatiam.ag.domain.RpcBeanDomain;

/**
 * 从数据库获取HSFSpringConsumerBean配置信息，进行构建spring bean
 * 
 * @author liangming
 * @since 2017-09-07
 */
public class HsfConsumerBeanBuilder extends StubBeanBuilder {

    private Logger               log            = LoggerFactory.getLogger(HsfConsumerBeanBuilder.class);

    @Resource
    private HsfConsumerConfigDao hsfConfig;

    /**
     * HSFSpringConsumerBean属性
     */
    private final String         CLASS          = "com.taobao.hsf.app.spring.util.HSFSpringConsumerBean";
    private final String         INIT_METHOD    = "init";
    private final String         INTERFACE_NAME = "interfaceName";
    private final String         VERSION        = "version";
    private final String         GROUP          = "group";

    /**
     * 加载HSF配置
     */
    @Override
    protected final List<HSFSpringConsumerBeanDomain> loadDefinition() {
        List<HSFSpringConsumerBeanDomain> list = hsfConfig.load();
        log.debug("load HSF consumer bean definitions: {}", list.size());
        return list;
    }

    /**
     * 构建HSFSpringConsumerBean
     */
    @Override
    protected final BeanDefinitionBuilder buildBeanDefintion(RpcBeanDomain beanDefintion) {
        HSFSpringConsumerBeanDomain hsfBean = (HSFSpringConsumerBeanDomain) beanDefintion;
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(CLASS);
        builder.setInitMethodName(INIT_METHOD);
        builder.addPropertyValue(INTERFACE_NAME, hsfBean.getInterfaceName());
        if (hsfBean.getVersion() != null && hsfBean.getVersion().trim().length() > 0) {
            builder.addPropertyValue(VERSION, hsfBean.getVersion());
        }
        if (hsfBean.getGroup() != null && hsfBean.getGroup().trim().length() > 0) {
            builder.addPropertyValue(GROUP, hsfBean.getGroup());
        }
        //add other property here
        return builder;
    }

    public HsfConsumerConfigDao getHsfConfig() {
        return hsfConfig;
    }

    public void setHsfConfig(HsfConsumerConfigDao hsfConfig) {
        this.hsfConfig = hsfConfig;
    }

    /**
     * Spring 在创建时会调用init-method，HSFSpringConsumerBean本身也会判断是否初始化
     */
    //    @Override
    //    @Deprecated
    //    protected final void processFactoryBean(String beanName) {
    //        Object hsf = applicationContext.getBean(beanName);
    //        ReflectionUtils.invoke(INIT_METHOD, CLASS, new String[0], hsf);
    //    }

}
