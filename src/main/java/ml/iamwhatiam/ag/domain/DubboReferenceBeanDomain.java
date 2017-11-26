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

public class DubboReferenceBeanDomain extends RpcBeanDomain {

	private static final long serialVersionUID = -3251566072377464696L;
	
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
	private String beanId;
	
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
	private String version = "0.0.0";
	
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
	 * 是否使用泛型接口
	 */
	private boolean generic;
	
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

	public String getRpcType() {
		return "Dubbo";
	}
	
	/**
	 * 
	 * 方法配置
	 *
	 * @author iMinusMinus
	 * @since 2017-11-04
	 * @see com.alibaba.dubbo.config.MethodConfig
	 */
	public class MethodConfigDomain extends BaseDomain {

		private static final long serialVersionUID = 2923337304942296974L;
		
		/**
		 * 方法名
		 */
		private String name;
		
		/**
		 * 统计参数
		 */
		private Integer stat;
		
		/**
		 * 异步调用回调实例
		 */
	    private Object            onInvoke;
	    
		/**
		 * 异步调用回调方法
		 */
	    private String            onInvokeMethod;
		
	}

	public String getBeanId() {
		return beanId;
	}

	public void setBeanId(String beanId) {
		this.beanId = beanId;
	}

	public String getLocal() {
		return local;
	}

	public void setLocal(String local) {
		this.local = local;
	}

	public String getProxy() {
		return proxy;
	}

	public void setProxy(String proxy) {
		this.proxy = proxy;
	}

	public String getCluster() {
		return cluster;
	}

	public void setCluster(String cluster) {
		this.cluster = cluster;
	}

	public String getFilter() {
		return filter;
	}

	public void setFilter(String filter) {
		this.filter = filter;
	}

	public String getListener() {
		return listener;
	}

	public void setListener(String listener) {
		this.listener = listener;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public String getModule() {
		return module;
	}

	public void setModule(String module) {
		this.module = module;
	}

	public List<String> getRegistries() {
		return registries;
	}

	public void setRegistries(List<String> registries) {
		this.registries = registries;
	}

	public String getMonitor() {
		return monitor;
	}

	public void setMonitor(String monitor) {
		this.monitor = monitor;
	}

	public int getCallbacks() {
		return callbacks;
	}

	public void setCallbacks(int callbacks) {
		this.callbacks = callbacks;
	}

	public String getOnConnect() {
		return onConnect;
	}

	public void setOnConnect(String onConnect) {
		this.onConnect = onConnect;
	}

	public String getOnDisconnect() {
		return onDisconnect;
	}

	public void setOnDisconnect(String onDisconnect) {
		this.onDisconnect = onDisconnect;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public boolean isCheck() {
		return check;
	}

	public void setCheck(boolean check) {
		this.check = check;
	}

	public boolean isInit() {
		return init;
	}

	public void setInit(boolean init) {
		this.init = init;
	}

	public boolean isGeneric() {
		return generic;
	}

	public void setGeneric(boolean generic) {
		this.generic = generic;
	}

	public String getSticky() {
		return sticky;
	}

	public void setSticky(String sticky) {
		this.sticky = sticky;
	}

	public boolean isReconnect() {
		return reconnect;
	}

	public void setReconnect(boolean reconnect) {
		this.reconnect = reconnect;
	}

	public boolean isLazy() {
		return lazy;
	}

	public void setLazy(boolean lazy) {
		this.lazy = lazy;
	}

	public List<MethodConfigDomain> getMethods() {
		return methods;
	}

	public void setMethods(List<MethodConfigDomain> methods) {
		this.methods = methods;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getClient() {
		return client;
	}

	public void setClient(String client) {
		this.client = client;
	}

	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

}
