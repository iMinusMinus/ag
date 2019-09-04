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

import javax.annotation.Resource;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

//import com.taobao.hsf.hsfunit.HSFEasyStarter;
//import com.taobao.itest.ITestSpringContextBaseCase;
//import com.taobao.itest.annotation.ITestSpringContext;


import ml.iamwhatiam.ag.service.DispatcherService;
import ml.iamwhatiam.ag.vo.FacadeVO;

/**
 * 使用HSF作为分发器测试
 * 
 * @author liangming
 * @since 2017-09-08
 */
/*@ITestSpringContext({ "file:src/main/webapp/WEB-INF/applicationContext.xml", "file:src/main/webapp/WEB-INF/springMvc-servlet.xml" })*/
@Slf4j
public class HsfTest /*extends ITestSpringContextBaseCase*/ {

//    static {
//        String path = System.getProperty("hsf.sar.path");
//        HSFEasyStarter.startFromPath(path);
//    }

    private FacadeVO          request;

//    @Resource
    private DispatcherService hsf;

    @Resource
    private DispatcherService hsfHttp;

    @Before
    public void setUp() {
        request = new FacadeVO();
        request.setService("test.getUserInfo");
        request.setVersion("1.0.0");
        request.setParameters("\"userId\":\"1013965707\",\"cooperator\":\"測試字符集\"");
        log.info("request: {}", request);
    }

    @Test
    @Ignore
    public void testQueryUserInfoByHsf() {
        Object result = hsf.doDispatch(request);
        log.info("hsf response: {}", result);
        Assert.assertNotNull(result);
    }

    @Test
    @Ignore
    public void testQueryUserInfoByHttp() {
        Object result = hsfHttp.doDispatch(request);
        log.info("http response: {}", result);
        Assert.assertNotNull(result);
    }

}
