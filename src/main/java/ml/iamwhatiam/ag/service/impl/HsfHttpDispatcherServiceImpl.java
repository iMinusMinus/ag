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

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import ml.iamwhatiam.ag.constants.SerializeFormat;
import ml.iamwhatiam.ag.exceptions.NoSuchPublisherException;
import ml.iamwhatiam.ag.support.Serializer;
import ml.iamwhatiam.ag.support.SerializerFactory;
import ml.iamwhatiam.ag.support.Subscriber;
import ml.iamwhatiam.ag.vo.HttpRequestVO;

/**
 * HSF HTTP请求（需要使用Pandora容器，同时启动参数必需启用HTTP）
 * 
 * @author liangming
 * @since 2017-09-20
 */
public class HsfHttpDispatcherServiceImpl extends HttpDispatcherService {

    private Logger          log          = LoggerFactory.getLogger(HsfHttpDispatcherServiceImpl.class);

    private final String    QUERY_STRING = "$1=%s&$2=%s";

    private SerializeFormat format;

    @Resource
    private Subscriber      subscriber;

    /**
     * 请求格式：http://ip:port/interfaceName:version/methodName
     */
    private final String    URL_FORMAT   = "http://%s/%s:%s/%s";

    public boolean support(String type) {
        return "HSF".equalsIgnoreCase(type) || super.support(type);
    }

    protected final String getRequestUrl(String serviceName, String interfaceName, String methodName, String version) {
        String target = subscriber.getNews(interfaceName + "/" + version);
        if (target == null) {//请求地址和端口：ip:port
            log.error("找不到服务[{}]发布者，主题为[{}]", serviceName, interfaceName + "/" + version);
            throw new NoSuchPublisherException("publisher cannot find");
        }
        return String.format(URL_FORMAT, target, interfaceName, version, methodName);
    }

    private HttpHeaders getDefaultHeader() {
        HttpHeaders header = new HttpHeaders();
        List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();
        acceptableMediaTypes.add(MediaType.ALL);
        header.setAccept(acceptableMediaTypes);
        header.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        return header;
    }

    private HttpHeaders getJsonHeader() {
        HttpHeaders header = new HttpHeaders();
        List<MediaType> acceptableMediaTypes = new ArrayList<MediaType>();
        acceptableMediaTypes.add(MediaType.ALL);
        header.setAccept(acceptableMediaTypes);
        header.setContentType(MediaType.APPLICATION_JSON);
        return header;
    }

    protected final HttpHeaders getRequestHeader() {
        return format == SerializeFormat.JSON ? getJsonHeader() : getDefaultHeader();
    }

    /**
     * @see com.taobao.hsf.remoting.netty.server.http.processor.
     *      RestfulHttpRequestProcessor#parseUrlEncodedContent HTTP
     *      请求头Content-Type = "application/x-www-form-urlencoded"时，格式为
     *      &#60;any1&#62;=parameterTypes&&#60;any2&#62;=args，编码为UTF-8；
     * @see com.taobao.hsf.remoting.netty.server.http.processor.RestfulHttpRequestProcessor#parseJsonContent
     *      否则为JSON， 格式为:[[], []]，编码为UTF-8；
     * @see com.taobao.hsf.remoting.netty.server.http.processor.UglyTypeHttpRequestProcessor#process
     *      格式为/[parameterTypes,]/[args,]，其中参数类型可选，类型为String[]，需要使用URLEncoder.
     *      encode 进行处理，参数必须，类型为Object[]，需要使用URLEncoder.encode进行处理，只能使用GET
     */
    protected final Object recombineBody(HttpRequestVO req) {
        Serializer serializer = SerializerFactory.getSerializer(SerializeFormat.JSON);
        Object data = null;
        String parameterTypes = serializer.serialize(req.getParameterTypes());
        String args = serializer.serialize(req.getArgs());
        if (format == SerializeFormat.JSON) {
            Object[][] from = { req.getParameterTypes(), req.getArgs() };
            data = from;
        } else {
            data = String.format(QUERY_STRING, parameterTypes, args);
        }
        return data;
    }

    protected final Object handleResponseBody(HttpEntity<String> response) {
        return response.getBody();
    }

    public Subscriber getSubscriber() {
        return subscriber;
    }

    public void setSubscriber(Subscriber subscriber) {
        this.subscriber = subscriber;
    }

    public SerializeFormat getFormat() {
        return format;
    }

    public void setFormat(SerializeFormat format) {
        this.format = format;
    }

}
