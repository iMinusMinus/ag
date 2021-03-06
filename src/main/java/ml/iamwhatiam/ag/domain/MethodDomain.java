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

import java.util.List;

import javax.validation.constraints.NotNull;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ml.iamwhatiam.ag.constants.Generic;

/**
 * 方法实体
 * 
 * @author iMinusMinus
 * @since 2017-09-07
 */
@Getter
@Setter
@ToString(callSuper = true)
public class MethodDomain extends BaseDomain {

    private static final long         serialVersionUID = -4672854286671661712L;

    /**
     * 对外暴露的服务名称（相当于interfaceName.methodName唯一确定），建议格式：行业.领域.行为
     */
    @NotNull
    private String                    serviceName;

    /**
     * @see HSFSpringConsumerBeanDomain#interfaceName
     */
    @NotNull
    private String                    interfaceName;

    /**
     * 方法名称
     */
    @NotNull
    private String                    methodName;

    /**
     * 方法需要的额外前置后置处理类
     * @see ml.iamwhatiam.ag.constants.Rococo
     */
    private int                       rococo;

    /**
     * 方法参数类型
     */
    private List<ParameterTypeDomain> parameters;

    /**
     * 方法返回类型：可能是数组、泛型或接口
     */
    @NotNull
    private String                    returnType;

    /**
     * 泛型标识
     */
    private Generic                   generic;

    public boolean hasGenericParameters() {
        return generic != null && (generic == Generic.IN || generic == Generic.BOTH);
    }

    public boolean hasGenericReturn() {
        return generic != null && (generic == Generic.OUT || generic == Generic.BOTH);
    }

}
