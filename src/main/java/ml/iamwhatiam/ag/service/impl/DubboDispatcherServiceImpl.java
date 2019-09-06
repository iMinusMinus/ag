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
import ml.iamwhatiam.ag.support.Deserializer;
import ml.iamwhatiam.ag.util.ReflectionUtils;

import java.util.Collection;
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
	 *     <li>true</li>[{"class":"java.bean"},"ENUM", {"class":"enum", "name":"INSTANCE"}, primitive]
	 *     <li>bean</li>JavaBeanDescriptor
	 * </ul>
	 * @see com.alibaba.dubbo.rpc.filter.GenericFilter#invoke(com.alibaba.dubbo.rpc.Invoker, com.alibaba.dubbo.rpc.Invocation)
	 * @param methodName
	 * @param parameters
	 * @param parameterTypes
	 * @return
	 */
	@Override
	protected Object[] assembleGenericRequest(String methodName, String parameters, List<ParameterTypeDomain> parameterTypes, Deserializer deserializer) {
		Object[] args = new Object[3];
		args[0] = methodName;
		String[] types = new String[parameterTypes.size()];
		Class[] classes = new Class[parameterTypes.size()];
		for(int i = 0; i < parameterTypes.size(); i++) {
			types[i] = parameterTypes.get(i).getType();
			Class<?> klazz = ReflectionUtils.findClass(types[i]);
			if(klazz != null && (klazz.isArray() || Collection.class.isAssignableFrom(klazz))) {
				classes[i] = List.class;
			} else if(klazz != null && (!ReflectionUtils.isJavaBean(klazz)) || klazz == Object.class) {
				classes[i] = klazz;
			} else {
				classes[i] = Map.class;
			}
		}
		args[1] = types;
		if(parameterTypes.size() == 0) {
			args[2] = new Object[0];
			return args;
		}
		if(parameterTypes.size() == 1) {
			args[2] = new Object[]{deserializer.deserializeObject(parameters, classes[0])};
		} else {
			args[2] = deserializer.deserializeArray(parameters, classes);
		}
		for(int i = 0; i < parameterTypes.size(); i++) {
			Object param = ((Object[]) args[2])[i];
			Class<?> klazz = ReflectionUtils.findClass(parameterTypes.get(i).getType());
			if(param instanceof Map && (klazz == null || ReflectionUtils.isJavaBean(klazz))) {
				((Map) param).put(CLASS, parameterTypes.get(i).getType());
			}
		}
		return args;
	}
}
