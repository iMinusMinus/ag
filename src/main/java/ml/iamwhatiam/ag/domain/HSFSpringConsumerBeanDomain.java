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
package ml.iamwhatiam.ag.domain;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * HSF Spring Bean数据库配置
 * 
 * @see com.taobao.hsf.app.spring.util.HSFSpringConsumerBean
 * @author liangming
 * @since 2017-09-07
 */
@Getter
@Setter
@ToString(callSuper = true)
public class HSFSpringConsumerBeanDomain extends RpcBeanDomain {

    private static final long serialVersionUID = 4074100574172854031L;

    public static final String HSF = "hsf";

    @Override
    public String getRpcType() {
        return HSF;
    }

    /**
     * 服务所属的组
     */
    private String group = "HSF";

    /**
     * 接口版本
     */
    private String version = "1.0.0";

    /**
     * 调用的服务的目标地址（ip:port），仅在本地测试时设置
     */
    private String target;

    /**
     * 指向一个用户定义的接口方法回调对象
     */
    @Deprecated
    private Object callbackHandler;

    /**
     * 需要异步调用的方法，格式为：{name:方法名;type:异步调用类型;listener:回调listener类名}<br>
     * 异步调用类型有oneway、future、callback、reliable四种类型，默认为oneway<br>
     * 如果设置了listener，type会被自动重置为callback
     */
    private List<String> asyncallMethods;

    /**
     * 如果某个接口方法在asyncallMethods属性中配置了type是callback或reliable，则callback时会调用该对象
     * 的相应方法， 方法名为：<tt>接口方法名_{@value callbackMethodSuffix}</tt>
     */
    @Deprecated
    private String callbackMethodSuffix = "_callback";

    /**
     * 在callback调用的时候，设置统一的回调函数，支持callback的context传递
     */
    private Object callbackInvoker;

    /**
     * 业务调用方法之前先调用{@code interfaceMethodToAttachContext}
     * 方法，将上下文传入，再调用业务的接口方法。HSF会将传入的context传送给服务端，在服务端完成处理后，
     * 在回调客户端的callback方法时，将context传入。<br>
     * 如果interfaceMethodToAttachContext和invokeContextThreadLocal同时设置了，
     * 以最后一次额外调用为准
     */
    @Deprecated
    private String interfaceMethodToAttachContext;

    /**
     * HSF通过这个ThreadLocal获得上下文并传递到服务端，在服务端完成处理后， 在回调客户端的callback方法时，将context传入
     */
    @Deprecated
    private ThreadLocal<Serializable> invokeContextThreadLocal;

    /**
     * 设置用户调用的线程池大小
     */
    private String maxThreadPool;

    /**
     * 设置方法单独超时时间：
     * 
     * <pre>
     * &ltproperty name="methodName" value="" /&gt 
     * &lt property name="clientTimeout" value="" /&gt
     * </pre>
     */
    //    private MethodSpecial[] methodSpecials;

    /**
     * 设置调用的方式，支持多种rpc方式，但只能设置成一种
     */
    private Map<String, Properties>   rpcProtocols;

}
