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
package ml.iamwhatiam.ag.hsf;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import ml.iamwhatiam.ag.dao.HsfConsumerConfigDao;
import ml.iamwhatiam.ag.domain.HSFSpringConsumerBeanDomain;
import ml.iamwhatiam.ag.support.AddressChangeEventListener;
import ml.iamwhatiam.ag.support.Subscriber;

/**
 * 开发环境测试用订阅器
 * 
 * @author iMinusMinus
 * @since 2017-09-22
 */
@Component
public class MockSubscriber implements Subscriber, InitializingBean {
	
	private Logger log = LoggerFactory.getLogger(MockSubscriber.class);

    @Resource
    private HsfConsumerConfigDao hsfConfig;

    private Map<String, String>  map = new ConcurrentHashMap<String, String>();

    /*
     * (non-Javadoc)
     * @see
     * org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
     */
    public void afterPropertiesSet() throws Exception {
        List<HSFSpringConsumerBeanDomain> list = hsfConfig.load();
        for (HSFSpringConsumerBeanDomain domain : list) {
            subscribe(domain.getInterfaceName() + ":" + domain.getVersion(), null);
        }
    }

    /**
     * <ul>
     * 获取服务顺序：
     * <li>本机订阅后加载的配置</li>宕机消失，或内存不够可能消失
     * <li>缓存中心缓存的配置</li>缓存服务器宕机消失，或保存时超时未存储
     * <li>本地临时文件保存的配置</li>手动删除消失，或服务重启消失，或系统清理临时目录消失，可能已经失效
     * <li>注册中心重新获取</li>注册中心临时宕机，超时
     * </ul>
     * 
     * @param topic
     * @return
     */
    public String getNews(String topic) {
        String news = map.get(topic);
        if(news == null) {
        	log.error("No target found for '{}'", topic);
        	return news;
        }
        return news.split("[;,\n]")[0];
    }

    public boolean subscribe(String what, AddressChangeEventListener listener) {
    	HSFSpringConsumerBeanDomain hsfBean = hsfConfig.findByInterfaceNameAndVersion(what.split(":")[0], what.split(":")[1]);
        String old = map.put(what, hsfBean.getTarget());
        if (old != null) {
            return false;
        }
        return true;
    }

}
