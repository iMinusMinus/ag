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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import ml.iamwhatiam.ag.constants.SerializeFormat;
import ml.iamwhatiam.ag.domain.MethodDomain;
import ml.iamwhatiam.ag.exceptions.NoSuchPublisherException;
import ml.iamwhatiam.ag.exceptions.RpcInvokingException;
import ml.iamwhatiam.ag.support.Deserializer;
import ml.iamwhatiam.ag.support.DeserializerFactory;
import ml.iamwhatiam.ag.support.Serializer;
import ml.iamwhatiam.ag.support.SerializerFactory;
import ml.iamwhatiam.ag.support.Subscriber;
import ml.iamwhatiam.ag.vo.HttpRequestVO;

/**
 * HSF HTTP请求（需要使用Pandora容器，同时启动参数必需启用HTTP）
 * 
 * @author liangming
 * @since 2017-09-20
 * @version 2018-01-22
 */
public class HsfHttpDispatcherServiceImpl extends HttpDispatcherService {

    private Logger          log            = LoggerFactory.getLogger(HsfHttpDispatcherServiceImpl.class);

    private final String    QUERY_STRING   = "$1=%s&$2=%s";

    private final String    GENERIC_METHOD = "$invoke";

    private final String[]  ARG_TYPES      = { String.class.getName(), String[].class.getName(),
            Object[].class.getName() };

    private SerializeFormat format;

    @Resource
    private Subscriber      subscriber;

    /**
     * 请求格式：http://ip:port/interfaceName:version/methodName
     */
    private final String    URL_FORMAT     = "http://%s/%s:%s/%s";

    @Override
    public boolean support(String type) {
        return "HSF".equalsIgnoreCase(type) || super.support(type);
    }

    protected final String getRequestUrl(String serviceName, MethodDomain md, String version) {
        String target = subscriber.getNews(md.getInterfaceName() + ":" + version);
        if (target == null) {//请求地址和端口：ip:port
            log.error("找不到服务[{}]发布者，主题为[{}]", serviceName, md.getInterfaceName() + ":" + version);
            throw new NoSuchPublisherException("publisher cannot find");
        }
        return String.format(URL_FORMAT, target, md.getInterfaceName(), version,
                md.hasGenericParameters() ? GENERIC_METHOD : md.getMethodName());
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
     * <ul>
     * <li>HTTP POST请求头Content-Type = "application/x-www-form-urlencoded"时，格式为
     * &#60;any1&#62;=parameterTypes&&#60;any2&#62;=args，编码为UTF-8；如果参数是泛型，
     * 请求地址的methodName应该为"$invoke"，POST内容变为&#60;any1&#62;=[\
     * "java.lang.String\", \"[Ljava.lang.String;\",
     * \"[Ljava.lang.Object;\"]&&#60;any2&#62;=[\"\\\"methodName\\\"\",[
     * parameterTypesExcludeGeneric], args]
     * <li>HTTP POST请求头Content-Type = "application/json"时， 格式为:[[],
     * []]，编码为UTF-8。
     * <li>HTTP
     * GET时，格式为/[parameterTypes,]/[args,]，其中参数类型可选，类型为String[]，需要使用URLEncoder.
     * encode 进行处理，参数必须，类型为Object[]，需要使用URLEncoder.encode进行处理
     * 
     * @see com.taobao.hsf.remoting.netty.server.http.processor.
     *      RestfulHttpRequestProcessor#parseUrlEncodedContent
     * @see com.taobao.hsf.remoting.netty.server.http.processor.RestfulHttpRequestProcessor#parseJsonContent
     * @see com.taobao.hsf.remoting.netty.server.http.processor.UglyTypeHttpRequestProcessor#process
     * @see com.taobao.hsf.remoting.provider.ProviderProcessor#handleRequest0
     */
    protected final Object recombineBody(HttpRequestVO req, MethodDomain md) {
        Serializer serializer = SerializerFactory.getSerializer(SerializeFormat.JSON);
        Object data = null;
        handleArgTypes(req, md);
        handleArgObjects(req, md);
        if (format == SerializeFormat.JSON) {
            Object[][] from = { req.getParameterTypes(), req.getArgs() };
            data = from;
        } else {
            //如果请求参数（参数对象包含&或=，服务端处理时会失败）
            data = String.format(QUERY_STRING, serializer.serialize(req.getParameterTypes()),
                    serializer.serialize(req.getArgs()));
        }
        return data;
    }

    /**
     * 针对泛型做特殊处理
     * 
     * @param req
     * @param md
     */
    private void handleArgTypes(HttpRequestVO req, MethodDomain md) {
        if (md.hasGenericParameters()) {
            req.setParameterTypes(ARG_TYPES);
        }
    }

    /**
     * <ul>
     * <li>针对特殊类型进行处理，避免JSON反序列化异常
     * <li>针对泛型做特殊处理
     * 
     * @param req
     */
    private void handleArgObjects(HttpRequestVO req, MethodDomain md) {
        Object[] args = req.getArgs();
        for (int index = 0; index < args.length; index++) {
            if (args[index] instanceof String || args[index] instanceof Enum<?>) {
                args[index] = "\"" + args[index] + "\"";
            }
        }
        if (md.hasGenericParameters()) {
            args = new Object[3];
            args[0] = "\"" + md.getMethodName() + "\"";
            String[] parameterTypes = new String[md.getParameters().size()];
            for (int index = parameterTypes.length - 1; index > 0; index--) {
                parameterTypes[index] = md.getParameters().get(index).getType();
            }
            args[1] = parameterTypes;
            args[2] = req.getArgs();
        }
        req.setArgs(args);
    }

    /**
     * HSF返回的格式为text/plain，但正常返回其实是JSON
     */
    protected final Object handleResponseBody(HttpEntity<String> response, Type returnType) {
        try {
            Deserializer deserializer = DeserializerFactory.getDeserializer(SerializeFormat.JSON);
            return deserializer.deserializeObject(response.getBody(), returnType);
        } catch (Exception e) {//如果JSON反序列化异常，则服务端返回的就是异常信息
            log.error(e.getMessage(), e);
            throw new RpcInvokingException(response.getBody());
        }
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
