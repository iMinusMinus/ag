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

import javax.annotation.Resource;

import ml.iamwhatiam.ag.dao.DubboReferenceConfigDao;
import ml.iamwhatiam.ag.domain.DubboReferenceBeanDomain;

/**
 * 将请求处理转发到Dubbbo提供方
 *
 * @author iMinusMinus
 * @since 2017-12-02
 */

public class DubboDispatcherServiceImpl extends RpcDispatcherService {
	
	@Resource
	private DubboReferenceConfigDao dubboConfig;

	/**
	 * 仅支持Dubbo
	 * 
	 * @param type
	 */
	@Override
	public boolean support(String type) {
		return DubboReferenceBeanDomain.DUBBO.equalsIgnoreCase(type);
	}
	
	/**
	 * @see ml.iamwhatiam.ag.builder.DubboReferenceBeanBuilder#getBeanName
	 */
	@Override
	protected String getBeanName(String interfaceName, String version) {
		DubboReferenceBeanDomain domain = dubboConfig.findByInterfaceNameAndVersion(interfaceName, version);
		if(domain != null && domain.getBeanId() != null && domain.getBeanId().length() > 0) {
			return domain.getBeanId();
		}
    	return interfaceName;
    }

	public DubboReferenceConfigDao getDubboConfig() {
		return dubboConfig;
	}

	public void setDubboConfig(DubboReferenceConfigDao dubboConfig) {
		this.dubboConfig = dubboConfig;
	}

}
