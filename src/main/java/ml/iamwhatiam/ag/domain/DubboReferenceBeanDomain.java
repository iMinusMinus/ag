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
package ml.iamwhatiam.ag.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * Dubbo ReferenceBean domain
 *
 * @author iMinusMinus
 * @since 2017-11-04
 * @see com.alibaba.dubbo.config.AbstractMethodConfig
 * @see com.alibaba.dubbo.config.AbstractInterfaceConfig
 * @see com.alibaba.dubbo.config.AbstractReferenceConfig
 * @see com.alibaba.dubbo.config.ReferenceConfig
 */
@Getter
@Setter
@ToString(callSuper = true)
public class DubboReferenceBeanDomain extends RpcBeanDomain {

	private static final long serialVersionUID = -3251566072377464696L;

	private static final String NATIVEJAVA = "nativejava";

	private static final String TRUE = "true";

	private static final String BEAN = "bean";

	public static final String DUBBO = "dubbo";

	@Override
	public String getRpcType() {
		return DUBBO;
	}

	@Override
	public boolean useGeneric() {
		return NATIVEJAVA.equals(generic) || BEAN.equals(generic) || TRUE.equals(generic);
	}

	@Override
	public String getBeanId() {
		return springBeanId != null ? springBeanId : interfaceName;
	}

	private String springBeanId;
	
	//AbstractMethodConfig
	/**
	 * 远程调用超时时间(毫秒)
	 */
	private long timeout;
	
	/**
	 * 重试次数
	 */
	private int retries;
	
	/**
	 * 最大并发调用
	 */
	private int actives;
	
	/**
	 * 负载均衡
	 */
	private String loadbalance;
	
	/**
	 * 是否异步
	 */
	private boolean async;
	
	/**
	 * 异步发送是否等待发送成功
	 */
	private boolean sent;
	
	/**
	 * 合并器
	 */
	private String merger;
	
	/**
	 * 是否校验
	 */
	private boolean validation;
	
	/**
	 * 缓存方式
	 */
	private String cache;
	
	//AbstractInterfaceConfig
	/**
	 * 服务接口的本地实现类名
	 */
	private String local;
	
	/**
	 * 代理类型
	 */
	private String proxy;
	
	/**
	 * 集群方式
	 */
	private String cluster;
	
	/**
	 * 过滤器
	 */
	private String filter;
	
	/**
	 * 监听器
	 */
	private String listener;
	
	/**
	 * 负责人
	 */
	private String owner;
	
	/**
	 * 应用信息
	 */
	private String application;
	
	/**
	 * 模块信息
	 */
	private String module;
	
	/**
	 * 注册中心
	 */
	private List<String> registries;
	
	/**
	 * 服务监控
	 */
	private String monitor;
	
	/**
	 * callback实例个数限制
	 */
	private int callbacks;
	
	/**
	 * 连接事件
	 */
	private String onConnect;
	
	/**
	 * 断开事件
	 */
	private String onDisconnect;
	
	//AbstractReferenceConfig
	/**
	 * 服务版本号
	 */
	private String version;
	
	/**
	 * 服务群组
	 */
	private String group;
	
	/**
	 * 检查服务提供者是否存在
	 */
	private boolean check = true;
	
	/**
	 * 是否加载时即刻初始化
	 */
	private boolean init;
	
	/**
	 * 是否使用泛型接口,dubbo泛型支持三种序列化方式：nativejava, bean, true
	 */
	private String generic;
	
	/**
	 * 启用或禁用集群session粘滞
	 */
	private String sticky;
	
	private boolean reconnect = true;
	
	/**
	 * lazy create connection
	 */
	private boolean lazy;
	
	//ReferenceConfig
	
	/**
	 * 方法配置
	 */
	private List<MethodConfigDomain> methods;
	
	/**
	 * 服务提供放列表
	 */
	private String url;
	
	/**
	 * 客户端类型
	 */
	private String client;
	
	/**
	 * 传输协议
	 */
	private String protocol;

	/**
	 * 
	 * 方法配置
	 *
	 * @author iMinusMinus
	 * @since 2017-11-04
	 * @see com.alibaba.dubbo.config.MethodConfig
	 */
	@Getter
	@Setter
	@ToString(callSuper = true)
	public class MethodConfigDomain extends BaseDomain {

		private static final long serialVersionUID = 2923337304942296974L;
		
		/**
		 * 方法名
		 */
		private String name;
		
		/**
		 * 统计参数
		 */
		private int stat = -1;
		
		/**
		 * 异步调用回调实例
		 */
	    private Object            onInvoke;
	    
		/**
		 * 异步调用回调方法
		 */
	    private String            onInvokeMethod;
		
	}

}
