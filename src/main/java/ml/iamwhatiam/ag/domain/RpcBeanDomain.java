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

/**
 * 服务接口基础配置
 * 
 * @author iMinusMinus
 * @since 2017-09-22
 */
@Getter
@Setter
@ToString(callSuper = true)
public abstract class RpcBeanDomain extends BaseDomain {

    private static final long serialVersionUID = -3666474780900016010L;

    /**
     * 接口完整类名
     */
    protected String interfaceName;

    /**
     * 接口版本
     */
    protected String version;

    /**
     * 泛化调用，默认启用
     */
    public boolean useGeneric() {
        return true;
    }

    /**
     * 获取RPC类型
     * 
     * @see ml.iamwhatiam.ag.service.DispatcherService#support(String)
     * @return RPC类型
     */
    public abstract String getRpcType();

    /**
     * 获取beanId
     * @return
     */
    public abstract String getBeanId();

}
