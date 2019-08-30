package ml.iamwhatiam.ag;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

/**
 * 测试基类：启动spring容器，可避免重复启动容器，减少测试耗时一级内存消耗
 * @author iMinusMinus
 * @date 2019-08-30
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = {"file:src/main/webapp/WEB-INF/applicationContext.xml", "file:src/main/webapp/WEB-INF/springMvc-servlet.xml"})
public abstract class BaseTest {
}
