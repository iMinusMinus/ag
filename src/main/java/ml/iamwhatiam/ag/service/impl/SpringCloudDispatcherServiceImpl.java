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
import java.util.Map;

import javax.annotation.Resource;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonRootName;

import ml.iamwhatiam.ag.constants.SerializeFormat;
import ml.iamwhatiam.ag.dao.HttpConfigDao;
import ml.iamwhatiam.ag.domain.HttpBeanDomain;
import ml.iamwhatiam.ag.domain.MethodDomain;
import ml.iamwhatiam.ag.exceptions.InsufficientConfigurationException;
import ml.iamwhatiam.ag.support.Deserializer;
import ml.iamwhatiam.ag.support.DeserializerFactory;
import ml.iamwhatiam.ag.vo.HttpRequestVO;

/**
 * Spring Cloud
 * 
 * @author iMinusMinus
 * @since 2017-12-07
 */
public class SpringCloudDispatcherServiceImpl extends HttpDispatcherService {

    private static final String EUREKA_URL_FORMAT = "%s/apps/%s";

    private static final String URL_FORMAT        = "%s://%s:%s/%s";

    private static final String HTTPS             = "https";

    private static final String HTTP              = "http";

    @Value("${eureka.client.service-url.defaultZone}")
    private String              eurekaServerUrl;

    @Resource
    private HttpConfigDao       httpConfigDao;

    /**
     * Eureka获取服务实例信息请求地址：
     * ${eureka.client.service-url.defaultZone}/apps/${providerName}
     */
    @Override
    protected String getRequestUrl(String service, MethodDomain md, String version) {
        HttpBeanDomain domain = httpConfigDao.findByInterfaceNameAndVersion(md.getInterfaceName(), version);
        if (domain == null || domain.getApplicationName() == null) {
            throw new InsufficientConfigurationException();
        }
        String providerName = domain.getApplicationName();
        String path = md.getMethodName();
        SpringCloudDispatcherServiceImpl.EurekaApplication cluster = rest.getForObject(
                String.format(EUREKA_URL_FORMAT, eurekaServerUrl, providerName),
                SpringCloudDispatcherServiceImpl.EurekaApplication.class);
        for (SpringCloudDispatcherServiceImpl.EurekaInstance instance : cluster.getInstance()) {
            if ("UP".equals(instance.status)) {
                return instance.securePort.enabled
                        ? String.format(URL_FORMAT, HTTPS, instance.ipAddr, instance.securePort.port, path)
                        : String.format(URL_FORMAT, HTTP, instance.ipAddr, instance.port.port, path);
            }
        }
        return null;
    }

    @Override
    protected HttpHeaders getRequestHeader() {
        HttpHeaders header = new HttpHeaders();
        //        header.setContentType();
        //        header.setAcceptCharset();
        return header;
    }

    @Override
    protected Object recombineBody(HttpRequestVO req, MethodDomain md) {
        return req.getArgs();
    }

    @Override
    protected Object handleResponseBody(HttpEntity<String> response, Type returnType) {
        SerializeFormat format = convert(response.getHeaders().getContentType());
        Deserializer deserializer = DeserializerFactory.getDeserializer(format);
        return deserializer.deserializeObject(response.getBody(), returnType);
    }

    private SerializeFormat convert(MediaType mediaType) {
        if (MediaType.APPLICATION_JSON_VALUE.equals(mediaType.getType())) {
            return SerializeFormat.JSON;
        } else if (MediaType.APPLICATION_XML_VALUE.equals(mediaType.getType())) {
            return SerializeFormat.XML;
        }
        return null;
    }

    @JsonRootName("application")
    @XmlRootElement(name = "application")
    @Getter
    @Setter
    static class EurekaApplication {

        /**
         * 不区分大小写
         */
        private String           name;

        private EurekaInstance[] instance;

    }

    @Getter
    @Setter
    static class EurekaInstance {

        private String              hostName;

        //@see EurekaApplication#name
        private String              app;

        private String              ipAddr;

        /**
         * @see com.netflix.appinfo.InstanceInfo.InstanceStatus
         */
        private String              status;

        private PortWrapper         port;

        //HTTPS
        private PortWrapper         securePort;

        /**
         * @see com.netflix.appinfo.InstanceInfo.InstanceStatus
         */
        private String              overriddenstatus;

        private int                 countryId;

        private DataCenterInfo      dataCenterInfo;

        private LeaseInfo           leaseInfo;

        private Map<String, String> metadata;

        private String              homePageUrl;

        private String              statusPageUrl;

        private String              healthCheckUrl;

        private String              vipAddress;

        private String              secureVipAddress;

        private boolean             isCoordinatingDiscoveryServer;

        private long                lastUpdatedTimestamp;

        private long                lastDirtyTimestamp;

        /**
         * @see com.netflix.appinfo.InstanceInfo.ActionType
         */
        private String              actionType;

    }

    @Getter
    class PortWrapper {

        @XmlAttribute
        private final boolean enabled;

        private final int port;

        @JsonCreator
        public PortWrapper(@JsonProperty("@enabled") boolean enabled, @JsonProperty("$") int port) {
            this.enabled = enabled;
            this.port = port;
        }

    }

    /**
     * @see com.netflix.appinfo.DataCenterInfo
     */
    @Getter
    @Setter
    class DataCenterInfo {

        /**
         * Netflix, Amazon, MyOwn
         * 
         * @see com.netflix.appinfo.DataCenterInfo.Name
         */
        private String name;

    }

    /**
     * @see com.netflix.appinfo.LeaseInfo
     */
    @Getter
    @Setter
    class LeaseInfo {

        private int renewalIntervalInSecs;

        private int durationInSecs;

        private long registrationTimestamp;

        private long lastRenewalTimestamp;

        private long evictionTimestamp;

        private long serviceUpTimestamp;

    }

}
