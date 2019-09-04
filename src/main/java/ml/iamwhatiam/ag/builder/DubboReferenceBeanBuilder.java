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

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;

import ml.iamwhatiam.ag.dao.DubboReferenceConfigDao;
import ml.iamwhatiam.ag.domain.DubboReferenceBeanDomain;
import ml.iamwhatiam.ag.domain.RpcBeanDomain;

/**
 * Dubbo ReferenceBean
 *
 * @author iMinusMinus
 * @since 2017-11月-04
 * 
 * @see com.alibaba.dubbo.config.spring.schema.DubboBeanDefinitionParser
 * @see com.alibaba.dubbo.config.spring.ReferenceBean
 */
@Slf4j
public class DubboReferenceBeanBuilder extends StubBeanBuilder {
	
	@Resource
	private DubboReferenceConfigDao dubboConfig;
	
	private final String CLASS = "com.alibaba.dubbo.config.spring.ReferenceBean";
	private final String INTERFACE_ATTR = "interface";
	private final String VERSION_ATTR = "version";
	private final String GROUP_ATTR = "group";
	private final String CHECK_ATTR = "check";
	private final String ID = "id";
	private final String GENERIC = "generic";

	@Override
	protected List<? extends RpcBeanDomain> loadDefinition() {
		List<DubboReferenceBeanDomain> list = dubboConfig.load();
		log.debug("load Dubbo ReferenceBean definitions: {}", list.size());
		return list;
	}
	
	/**
	 * 构建Dubbo ReferenceBean
	 * @param beanDefintion
	 */
	@Override
	protected BeanDefinitionBuilder buildBeanDefintion(RpcBeanDomain beanDefintion) {
		DubboReferenceBeanDomain dubboBean = (DubboReferenceBeanDomain) beanDefintion;
		BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(CLASS);
		builder.setLazyInit(false);
		builder.addPropertyValue(INTERFACE_ATTR, dubboBean.getInterfaceName());
		builder.addPropertyValue(CHECK_ATTR, dubboBean.isCheck());
		builder.addPropertyValue(ID, dubboBean.getBeanId());
		if(dubboBean.getGroup() != null && dubboBean.getGroup().length() > 0) {
			builder.addPropertyValue(GROUP_ATTR, dubboBean.getGroup());
		}
		if(dubboBean.getVersion() != null && dubboBean.getVersion().length() > 0) {
			builder.addPropertyValue(VERSION_ATTR, dubboBean.getVersion());
		}
		if(dubboBean.getGeneric() != null) {
			builder.addPropertyValue(GENERIC, dubboBean.getGeneric());
		}
		//add other property here
		return builder;
	}

}
