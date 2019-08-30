package ml.iamwhatiam.ag.dao;

import ml.iamwhatiam.ag.BaseTest;
import ml.iamwhatiam.ag.constants.Generic;
import ml.iamwhatiam.ag.domain.MethodDomain;
import ml.iamwhatiam.ag.domain.ParameterTypeDomain;
import org.junit.Assert;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.Arrays;

/**
 * 服务测试
 * @author iMinusMinus
 * @date 2019-08-29
 */
public class ServiceConfigDaoTest extends BaseTest {

    private static final String ECHO_SERVICE = "dubbo.echo";

    @Resource
    private ServiceConfigDao serviceConfigDao;

    @Test
    public void testCrud() {
        MethodDomain md = new MethodDomain();
        md.setGeneric(Generic.NONE);
        md.setInterfaceName("com.alibaba.dubbo.rpc.service.EchoService");
        md.setMethodName("$echo");
        md.setReturnType(String.class.getName());
        md.setServiceName(ECHO_SERVICE);
        ParameterTypeDomain ptd = new ParameterTypeDomain();
        ptd.setIndex(0);
        ptd.setInterfaceName(md.getInterfaceName());
        ptd.setMethodName(md.getMethodName());
        ptd.setType(Object.class.getName());
        md.setParameters(Arrays.asList(ptd));
        int affect = serviceConfigDao.save(md);
        Assert.assertEquals(1, affect);
        long id = md.getId();

        MethodDomain found = serviceConfigDao.findByServiceName(ECHO_SERVICE);
        Assert.assertEquals(String.class.getName(), found.getReturnType());

        MethodDomain update = new MethodDomain();
        update.setId(id);
        update.setReturnType(Object.class.getName());
        int effect = serviceConfigDao.update(update);
        Assert.assertEquals(1, effect);

        Assert.assertEquals(1, serviceConfigDao.delete(id));
    }

}
