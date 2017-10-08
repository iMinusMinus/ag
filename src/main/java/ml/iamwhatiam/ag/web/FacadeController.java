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

import java.util.List;

import javax.annotation.Resource;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import ml.iamwhatiam.ag.service.DispatcherService;
import ml.iamwhatiam.ag.vo.FacadeVO;
import ml.iamwhatiam.ag.vo.ResultWrapper;

/**
 * 网关门面
 * 
 * @author iMinusMinus
 * @since 2017-09-08
 */
@Controller
public class FacadeController {

    private static Logger     log = LoggerFactory.getLogger(FacadeController.class);

    @Resource
    private DispatcherAdapter adapter;

    @RequestMapping("/ag")
    @ResponseBody
    @ExceptionHandler
    public ResultWrapper<Object> invoke(@Valid FacadeVO req, BindingResult br) {
        log.debug("request parameters: {}", req);
        ResultWrapper<Object> result = new ResultWrapper<Object>();
        if(br.hasErrors()) {
        	result.setStatus(ResultWrapper.CLIENT_ERROR);
        	result.setMessage(br.toString());
        	return result;
        }
        DispatcherExecutionChain chain = adapter.getDispatcherExecutionChain(req);
        DispatcherService dispatcher = chain.getDispatcher();
        List<DispatcherInterceptor> inteceptors = chain.getInterceptors();
        for (DispatcherInterceptor inteceptor : inteceptors) {
            inteceptor.preDispatch(req, dispatcher);
            if(inteceptor instanceof ParameterInteceptor && ((ParameterInteceptor) inteceptor).canIntecept(req)) {
            	((ParameterInteceptor) inteceptor).intecept(req);
            }
        }
        log.debug("RPC invoke parameters: {}", req);
        Object remoteResult = dispatcher.doDispatch(req);
        log.debug("RPC invoke result: {}", remoteResult);
        for (DispatcherInterceptor inteceptor : inteceptors) {
            Object mappingResult = inteceptor.postDispatch(remoteResult, dispatcher);
            remoteResult = mappingResult;
        }
        log.debug("response: {}", remoteResult);
        result.setResult(remoteResult);
        return result;
    }

}
