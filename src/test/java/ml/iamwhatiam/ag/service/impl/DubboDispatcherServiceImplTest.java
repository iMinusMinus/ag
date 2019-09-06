package ml.iamwhatiam.ag.service.impl;

import ml.iamwhatiam.ag.constants.SerializeFormat;
import ml.iamwhatiam.ag.dao.DubboReferenceConfigDao;
import ml.iamwhatiam.ag.dao.ServiceConfigDao;
import ml.iamwhatiam.ag.domain.DubboReferenceBeanDomain;
import ml.iamwhatiam.ag.domain.MethodDomain;
import ml.iamwhatiam.ag.domain.ParameterTypeDomain;
import ml.iamwhatiam.ag.util.ReflectionUtils;
import ml.iamwhatiam.ag.vo.FacadeVO;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
public class DubboDispatcherServiceImplTest {

    @InjectMocks
    private DubboDispatcherServiceImpl dubboDispatcherServiceImpl;

    @Mock
    private DubboReferenceConfigDao dubboConfig;

    @Mock
    private ServiceConfigDao serviceConfig;

    @Mock
    private ApplicationContext applicationContext;

    @Test
    public void testDoDispatch泛化无参() {
        FacadeVO req = new FacadeVO();
        req.setService("parameter.void");
        req.setVersion("1.0.0");
        req.setFormat(SerializeFormat.JSON);
        MethodDomain mockApiConfig = new MethodDomain();
        mockApiConfig.setServiceName(req.getService());
        mockApiConfig.setParameters(Collections.emptyList());
        mockApiConfig.setInterfaceName("java.util.List");
        mockApiConfig.setMethodName("toString");
        mockApiConfig.setReturnType("void");
        Mockito.when(serviceConfig.findByServiceName(req.getService())).thenReturn(mockApiConfig);
        DubboReferenceBeanDomain mockRpcConfig = new DubboReferenceBeanDomain();
        mockRpcConfig.setGeneric("true");
        mockRpcConfig.setInterfaceName(mockApiConfig.getInterfaceName());
        Mockito.when(dubboConfig.findByInterfaceNameAndVersion(mockApiConfig.getInterfaceName(), req.getVersion())).thenReturn(mockRpcConfig);
        Class[] intfs = {ReflectionUtils.findClass("com.alibaba.dubbo.rpc.service.GenericService")};
        Object mockBean = Proxy.newProxyInstance(DubboDispatcherServiceImplTest.class.getClassLoader(), intfs, (proxy, method, args) -> "[]");
        Mockito.when(applicationContext.getBean(Mockito.anyString())).thenReturn(mockBean);

        dubboDispatcherServiceImpl.doDispatch(req);
        Assert.assertTrue(true);
    }

    @Test
    public void testDoDispatch泛化单参() {
        FacadeVO req = new FacadeVO();
        req.setService("parameter.primitive");
        req.setVersion("1.0.0");
        req.setParameters("1");
        req.setFormat(SerializeFormat.JSON);
        MethodDomain mockApiConfig = new MethodDomain();
        mockApiConfig.setServiceName(req.getService());
        mockApiConfig.setInterfaceName("java.util.List");
        mockApiConfig.setMethodName("add");
        mockApiConfig.setReturnType("boolean");
        ParameterTypeDomain type = new ParameterTypeDomain();
        type.setType("java.lang.Object");
        type.setInterfaceName(mockApiConfig.getInterfaceName());
        type.setMethodName(mockApiConfig.getMethodName());
        mockApiConfig.setParameters(Arrays.asList(type));
        Mockito.when(serviceConfig.findByServiceName(req.getService())).thenReturn(mockApiConfig);
        DubboReferenceBeanDomain mockRpcConfig = new DubboReferenceBeanDomain();
        mockRpcConfig.setGeneric("true");
        mockRpcConfig.setInterfaceName(mockApiConfig.getInterfaceName());
        Mockito.when(dubboConfig.findByInterfaceNameAndVersion(mockApiConfig.getInterfaceName(), req.getVersion())).thenReturn(mockRpcConfig);
        Class[] intfs = {ReflectionUtils.findClass("com.alibaba.dubbo.rpc.service.GenericService")};
        Object mockBean = Proxy.newProxyInstance(DubboDispatcherServiceImplTest.class.getClassLoader(), intfs, (proxy, method, args) -> true);
        Mockito.when(applicationContext.getBean(Mockito.anyString())).thenReturn(mockBean);

        dubboDispatcherServiceImpl.doDispatch(req);
    }

    @Test
    public void testDoDispatch泛化复杂参数() {
        FacadeVO req = new FacadeVO();
        req.setService("parameter.java.bean");
        req.setVersion("1.0.0");
        req.setParameters("[{\"appId\":\"<appId>\",\"signType\":\"sha1WithRSA\",\"format\":\"JSON\"},\"string\", [1,2,3], [{\"appId\":\"1\",\"format\":\"XML\"},{\"appId\":\"2\",\"format\":\"JSON\"}]]");
        req.setFormat(SerializeFormat.JSON);
        MethodDomain mockApiConfig = new MethodDomain();
        mockApiConfig.setServiceName(req.getService());
        mockApiConfig.setInterfaceName("f.q.c.n");
        mockApiConfig.setMethodName("f");
        mockApiConfig.setReturnType("r");
        ParameterTypeDomain type0 = new ParameterTypeDomain();
        type0.setType("ml.iamwhatiam.ag.vo.FacadeVO");
        type0.setInterfaceName(mockApiConfig.getInterfaceName());
        type0.setMethodName(mockApiConfig.getMethodName());
        ParameterTypeDomain type1 = new ParameterTypeDomain();
        type1.setType("java.lang.String");
        type1.setInterfaceName(mockApiConfig.getInterfaceName());
        type1.setMethodName(mockApiConfig.getMethodName());
        type1.setIndex(1);
        ParameterTypeDomain type2 = new ParameterTypeDomain();
        type2.setType("[I");
        type2.setInterfaceName(mockApiConfig.getInterfaceName());
        type2.setMethodName(mockApiConfig.getMethodName());
        type2.setIndex(2);
        ParameterTypeDomain type3 = new ParameterTypeDomain();
        type3.setType("java.util.List");
        type3.setInterfaceName(mockApiConfig.getInterfaceName());
        type3.setMethodName(mockApiConfig.getMethodName());
        type3.setIndex(3);
        mockApiConfig.setParameters(Arrays.asList(type0, type1, type2, type3));
        Mockito.when(serviceConfig.findByServiceName(req.getService())).thenReturn(mockApiConfig);
        DubboReferenceBeanDomain mockRpcConfig = new DubboReferenceBeanDomain();
        mockRpcConfig.setGeneric("true");
        mockRpcConfig.setInterfaceName(mockApiConfig.getInterfaceName());
        Mockito.when(dubboConfig.findByInterfaceNameAndVersion(mockApiConfig.getInterfaceName(), req.getVersion())).thenReturn(mockRpcConfig);
        Class[] intfs = {ReflectionUtils.findClass("com.alibaba.dubbo.rpc.service.GenericService")};
        Class[] des = {Object[].class, Class[].class, Type[].class};
        Class[] testArgTypes = {FacadeVO.class, String.class, int[].class, List.class};
        Type[] mtypes = {FacadeVO.class, String.class, int[].class, new ParameterizedType() {
            @Override
            public Type[] getActualTypeArguments() {
                return new Type[] {FacadeVO.class};
            }

            @Override
            public Type getRawType() {
                return List.class;
            }

            @Override
            public Type getOwnerType() {
                return null;
            }
        }};
        Object mockBean = Proxy.newProxyInstance(DubboDispatcherServiceImplTest.class.getClassLoader(), intfs,
                (proxy, method, args) -> ReflectionUtils.invoke("realize", "com.alibaba.dubbo.common.utils.PojoUtils", des, "<any>", new Object[] {args[2], testArgTypes, mtypes}));
        Mockito.when(applicationContext.getBean(Mockito.anyString())).thenReturn(mockBean);

        Object result = dubboDispatcherServiceImpl.doDispatch(req);
        Assert.assertTrue(((Object[]) result)[0] instanceof FacadeVO);
        Assert.assertEquals("string", ((Object[]) result)[1]);
        Assert.assertEquals(1, ((int[]) ((Object[]) result)[2])[0]);
    }

}
