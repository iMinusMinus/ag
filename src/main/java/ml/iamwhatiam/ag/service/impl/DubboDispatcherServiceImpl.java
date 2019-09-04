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
import ml.iamwhatiam.ag.domain.ParameterTypeDomain;
import ml.iamwhatiam.ag.domain.RpcBeanDomain;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 将请求处理转发到Dubbbo提供方
 *
 * @author iMinusMinus
 * @since 2017-12-02
 */

public class DubboDispatcherServiceImpl extends RpcDispatcherService {

	/**
	 * 泛化调用方法名
	 * @see com.alibaba.dubbo.rpc.service.GenericService#$invoke(java.lang.String, java.lang.String[], java.lang.Object[])
	 */
	private static final String GENERIC_METHOD_NAME = "$invoke";

	private static final String GENERIC_INTERFACE_NAME = "com.alibaba.dubbo.rpc.service.GenericService";

	private static final Class[] GENERIC_PARAMETER_TYPES = {String.class, String[].class, Object[].class};

	private static final String CLASS = "class";

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


	@Override
	protected RpcBeanDomain getRpcConfig(String interfaceName, String version) {
		return dubboConfig.findByInterfaceNameAndVersion(interfaceName, version);
	}

	@Override
	protected String getGenericMethodName() {
		return GENERIC_METHOD_NAME;
	}

	@Override
	protected String getGeneircInterfaceName() {
		return GENERIC_INTERFACE_NAME;
	}

	@Override
	protected Class[] getGenericParameterTypes() {
		return GENERIC_PARAMETER_TYPES;
	}

	/**
	 * dubbo泛化引用参数处理
	 * <ul>
	 *     <li>nativejava</li>byte[]
	 *     <li>true</li>
	 *     <li>bean</li>JavaBeanDescriptor
	 * </ul>
	 * @see com.alibaba.dubbo.rpc.filter.GenericFilter#invoke(com.alibaba.dubbo.rpc.Invoker, com.alibaba.dubbo.rpc.Invocation)
	 * @param methodName
	 * @param parameters
	 * @param parameterTypes
	 * @return
	 */
	@Override
	protected Object[] assembleGenericRequest(String methodName, String parameters, List<ParameterTypeDomain> parameterTypes) {
		Object[] args = new Object[3];
		args[0] = methodName;
		String[] types = new String[parameterTypes.size()];
		for(int i = 0; i < parameterTypes.size(); i++) {
			types[i] = parameterTypes.get(0).getType();
		}
		args[1] = types;
		Object[] params = new Object[types.length];
		//TODO parameters
		for(int i = 0; i < parameterTypes.size(); i++) {
			Map<String, Object> param = new HashMap<>();
			param.put(CLASS, parameterTypes.get(0).getType());
//			param.putAll();
			params[i] = param;
		}
		args[2] = params;
		return args;
	}
}
