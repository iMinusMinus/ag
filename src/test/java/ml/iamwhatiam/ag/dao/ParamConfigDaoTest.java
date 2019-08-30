package ml.iamwhatiam.ag.dao;

import ml.iamwhatiam.ag.BaseTest;
import ml.iamwhatiam.ag.domain.ParameterTypeDomain;
import org.junit.Assert;
import org.junit.Test;

import javax.annotation.Resource;
import java.util.List;

public class ParamConfigDaoTest extends BaseTest {

    @Resource
    private ParamConfigDao paramConfigDao;

    @Test
    public void testCrud() {
        ParameterTypeDomain ptd = new ParameterTypeDomain();
        ptd.setIndex(0);
        ptd.setInterfaceName("com.alibaba.dubbo.rpc.service.EchoService");
        ptd.setMethodName("$echo");
        ptd.setType(String.class.getName());
        Assert.assertEquals(1, paramConfigDao.save(ptd));
        long id = ptd.getId();

        ParameterTypeDomain toUpdate = new ParameterTypeDomain();
        toUpdate.setId(id);
        toUpdate.setType(Object.class.getName());
        Assert.assertEquals(1, paramConfigDao.update(toUpdate));

        List<ParameterTypeDomain> qr = paramConfigDao.findByMethod(ptd.getInterfaceName(), ptd.getMethodName());
        Assert.assertTrue(qr != null && qr.size() == 1);
        Assert.assertEquals(Object.class.getName(), qr.get(0).getType());

        Assert.assertEquals(1, paramConfigDao.delete(id));
    }
}
