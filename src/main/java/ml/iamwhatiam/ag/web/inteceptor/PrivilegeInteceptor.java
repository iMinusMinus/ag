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
package ml.iamwhatiam.ag.web.inteceptor;

import javax.annotation.Resource;

import org.springframework.stereotype.Component;

import ml.iamwhatiam.ag.constants.Default;
import ml.iamwhatiam.ag.exceptions.InteceptException;
import ml.iamwhatiam.ag.service.CacheService;
import ml.iamwhatiam.ag.vo.FacadeVO;
import ml.iamwhatiam.ag.web.DispatcherInterceptor;

/**
 * 权限校验
 * 
 * @author iMinusMinus
 * @since 2017-10-31
 */
@Component
public class PrivilegeInteceptor implements DispatcherInterceptor {

    @Resource
    private CacheService cache;

    /**
     * 权限验证一般作为最先的处理
     */
    public int getOrder() {
        return 0x70000000;
    }

    @Override
    public boolean preDispatch(FacadeVO param, Object dispatcher) throws InteceptException {
        Object value = cache.get(String.format(Default.PRIVILEGE_KEY.get(), param.getClient(), param.getService()));
        if (value == null) {
            throw new InteceptException("未授权");
        }
        return false;
    }

    public Object postDispatch(Object result, Object handler) throws InteceptException {
        // DO NOTHING
        return result;
    }

    public CacheService getCache() {
        return cache;
    }

    public void setCache(CacheService cache) {
        this.cache = cache;
    }

}
