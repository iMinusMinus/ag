package ml.iamwhatiam.ag.web;

import ml.iamwhatiam.ag.BaseTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.Resource;
import java.net.URI;

public class FacadeControllerTest extends BaseTest {

    @Resource
    private WebApplicationContext wac;

    private MockMvc mvc;

    @Before
    public void setUp() {
        mvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    public void testInvoke() throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post(new URI("/test/1qaz/2wsx?qa=测试&ed=rf"));
        builder.contextPath("/test");
        builder.characterEncoding("UTF-8");
        builder.contentType(MediaType.APPLICATION_JSON);
        builder.header("plm","okn");
        builder.header("ijb", "uhv", "字符集测试");
        builder.param("qwe", "字符", "uiop");
        builder.param("asd", "fgh");
        builder.content("{\"array\":[\"object\",\"number\",\"string\",\"true\",\"false\",\"null\"],\"object\":\"乱码测试\"}");
        mvc.perform(builder);
    }
}
