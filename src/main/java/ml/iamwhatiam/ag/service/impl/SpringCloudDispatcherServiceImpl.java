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

    protected HttpHeaders getRequestHeader() {
        HttpHeaders header = new HttpHeaders();
        //        header.setContentType();
        //        header.setAcceptCharset();
        return header;
    }

    protected Object recombineBody(HttpRequestVO req, MethodDomain md) {
        return req.getArgs();
    }

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
    static class EurekaApplication {

        /**
         * 不区分大小写
         */
        private String           name;

        private EurekaInstance[] instance;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public EurekaInstance[] getInstance() {
            return instance;
        }

        public void setInstance(EurekaInstance[] instance) {
            this.instance = instance;
        }

    }

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

        public String getHostName() {
            return hostName;
        }

        public void setHostName(String hostName) {
            this.hostName = hostName;
        }

        public String getApp() {
            return app;
        }

        public void setApp(String app) {
            this.app = app;
        }

        public String getIpAddr() {
            return ipAddr;
        }

        public void setIpAddr(String ipAddr) {
            this.ipAddr = ipAddr;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public PortWrapper getPort() {
            return port;
        }

        public void setPort(PortWrapper port) {
            this.port = port;
        }

        public PortWrapper getSecurePort() {
            return securePort;
        }

        public void setSecurePort(PortWrapper securePort) {
            this.securePort = securePort;
        }

        public String getOverriddenstatus() {
            return overriddenstatus;
        }

        public void setOverriddenstatus(String overriddenstatus) {
            this.overriddenstatus = overriddenstatus;
        }

        public int getCountryId() {
            return countryId;
        }

        public void setCountryId(int countryId) {
            this.countryId = countryId;
        }

        public DataCenterInfo getDataCenterInfo() {
            return dataCenterInfo;
        }

        public void setDataCenterInfo(DataCenterInfo dataCenterInfo) {
            this.dataCenterInfo = dataCenterInfo;
        }

        public LeaseInfo getLeaseInfo() {
            return leaseInfo;
        }

        public void setLeaseInfo(LeaseInfo leaseInfo) {
            this.leaseInfo = leaseInfo;
        }

        public Map<String, String> getMetadata() {
            return metadata;
        }

        public void setMetadata(Map<String, String> metadata) {
            this.metadata = metadata;
        }

        public String getHomePageUrl() {
            return homePageUrl;
        }

        public void setHomePageUrl(String homePageUrl) {
            this.homePageUrl = homePageUrl;
        }

        public String getStatusPageUrl() {
            return statusPageUrl;
        }

        public void setStatusPageUrl(String statusPageUrl) {
            this.statusPageUrl = statusPageUrl;
        }

        public String getHealthCheckUrl() {
            return healthCheckUrl;
        }

        public void setHealthCheckUrl(String healthCheckUrl) {
            this.healthCheckUrl = healthCheckUrl;
        }

        public String getVipAddress() {
            return vipAddress;
        }

        public void setVipAddress(String vipAddress) {
            this.vipAddress = vipAddress;
        }

        public String getSecureVipAddress() {
            return secureVipAddress;
        }

        public void setSecureVipAddress(String secureVipAddress) {
            this.secureVipAddress = secureVipAddress;
        }

        public boolean isCoordinatingDiscoveryServer() {
            return isCoordinatingDiscoveryServer;
        }

        public void setCoordinatingDiscoveryServer(boolean isCoordinatingDiscoveryServer) {
            this.isCoordinatingDiscoveryServer = isCoordinatingDiscoveryServer;
        }

        public long getLastUpdatedTimestamp() {
            return lastUpdatedTimestamp;
        }

        public void setLastUpdatedTimestamp(long lastUpdatedTimestamp) {
            this.lastUpdatedTimestamp = lastUpdatedTimestamp;
        }

        public long getLastDirtyTimestamp() {
            return lastDirtyTimestamp;
        }

        public void setLastDirtyTimestamp(long lastDirtyTimestamp) {
            this.lastDirtyTimestamp = lastDirtyTimestamp;
        }

        public String getActionType() {
            return actionType;
        }

        public void setActionType(String actionType) {
            this.actionType = actionType;
        }

    }

    class PortWrapper {

        @XmlAttribute
        private boolean enabled;

        private int     port;

        @JsonCreator
        public PortWrapper(@JsonProperty("@enabled") boolean enabled, @JsonProperty("$") int port) {
            this.enabled = enabled;
            this.port = port;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public int getPort() {
            return port;
        }

    }

    /**
     * @see com.netflix.appinfo.DataCenterInfo
     */
    class DataCenterInfo {

        /**
         * Netflix, Amazon, MyOwn
         * 
         * @see com.netflix.appinfo.DataCenterInfo.Name
         */
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

    }

    /**
     * @see com.netflix.appinfo.LeaseInfo
     */
    class LeaseInfo {

        private int  renewalIntervalInSecs;

        private int  durationInSecs;

        private long registrationTimestamp;

        private long lastRenewalTimestamp;

        private long evictionTimestamp;

        private long serviceUpTimestamp;

        public int getRenewalIntervalInSecs() {
            return renewalIntervalInSecs;
        }

        public void setRenewalIntervalInSecs(int renewalIntervalInSecs) {
            this.renewalIntervalInSecs = renewalIntervalInSecs;
        }

        public int getDurationInSecs() {
            return durationInSecs;
        }

        public void setDurationInSecs(int durationInSecs) {
            this.durationInSecs = durationInSecs;
        }

        public long getRegistrationTimestamp() {
            return registrationTimestamp;
        }

        public void setRegistrationTimestamp(long registrationTimestamp) {
            this.registrationTimestamp = registrationTimestamp;
        }

        public long getLastRenewalTimestamp() {
            return lastRenewalTimestamp;
        }

        public void setLastRenewalTimestamp(long lastRenewalTimestamp) {
            this.lastRenewalTimestamp = lastRenewalTimestamp;
        }

        public long getEvictionTimestamp() {
            return evictionTimestamp;
        }

        public void setEvictionTimestamp(long evictionTimestamp) {
            this.evictionTimestamp = evictionTimestamp;
        }

        public long getServiceUpTimestamp() {
            return serviceUpTimestamp;
        }

        public void setServiceUpTimestamp(long serviceUpTimestamp) {
            this.serviceUpTimestamp = serviceUpTimestamp;
        }

    }

}
