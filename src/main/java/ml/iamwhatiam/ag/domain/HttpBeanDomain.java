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
 * 使用HTTP分发服务的实体
 * 
 * @author iMinusMinus
 * @since 2017-09-23
 */
@Getter
@Setter
@ToString(callSuper = true)
public class HttpBeanDomain extends RpcBeanDomain {

    private static final long serialVersionUID = -3199277225392588657L;

    public static final String HTTP = "http";

    /**
     * 系统名称
     */
    private String            applicationName;

    @Override
    public String getRpcType() {
        return HTTP;
    }

    @Override
    public String getBeanId() {
        return interfaceName;
    }

}
