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
package ml.iamwhatiam.ag.web;

import org.springframework.core.Ordered;

import ml.iamwhatiam.ag.exceptions.InteceptException;
import ml.iamwhatiam.ag.vo.FacadeVO;

/**
 * 请求分发前置后置处理器
 * 
 * @author iMinusMinus
 * @since 2017-09-15
 */
public interface DispatcherInterceptor extends Ordered {

    /**
     * 请求前处理
     * 
     * @param param
     * @param dispatcher
     * @return
     * @throws InteceptException
     */
    boolean preDispatch(FacadeVO param, Object dispatcher) throws InteceptException;

    /**
     * 请求后处理
     * 
     * @param result
     * @param handler
     * @return mappingResult
     * @throws InteceptException
     */
    Object postDispatch(Object result, Object handler) throws InteceptException;

}
