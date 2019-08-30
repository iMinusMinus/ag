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

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import lombok.extern.slf4j.Slf4j;
import ml.iamwhatiam.ag.vo.ChaosVO;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import ml.iamwhatiam.ag.vo.FacadeVO;
import ml.iamwhatiam.ag.vo.ResultWrapper;

/**
 * 网关门面
 * <ul>
 *     <li>合作方可能固定了请求路径，我们只能选择域名，那么可以将子域名appid.iamwhatiam.ml指向iamwhatiam.ml/appid</li>
 *     <li>合作方可能将参数放在HTTP请求头，或者放在路径，或者放在查询参数，或者放在请求体</li>
 *     <li>甚至某些参数由于是固定值，事先提供给我们</li>
 *     <li>charset和Content-Type也是需要考虑的</li>
 *     <li>是否需要验签，哪些参数需要作为签名部分，这些参数如何排序，如何合并，签名算法又是什么</li>
 *     <li>是否需要加解密，哪些参数需要解密，是对称加密还是非对称加密，加密算法是什么，密钥如何获取</li>
 * </ul>
 * 
 * @author iMinusMinus
 * @since 2017-09-08
 */
@Controller
@Slf4j
public class FacadeController {

    /**
     * 适配网关入口
     * @param appId
     * @param request
     * @param response
     * @return
     */
    @RequestMapping("/{appId}/**")
    public void invoke(@PathVariable("appId") String appId, HttpServletRequest request, HttpServletResponse response) throws Exception {
        String charset = request.getCharacterEncoding();
        String contentType = request.getContentType();
        ChaosVO vo = new ChaosVO();
        //查询配置，提取、转换参数
        Enumeration<String> headerNamess = request.getHeaderNames();
        Map<String, Object> headers = new HashMap<>();
        while(headerNamess.hasMoreElements()) {
            String header = headerNamess.nextElement();
            Enumeration<String> headerValues = request.getHeaders(header);
            List<String> tmp = new ArrayList<>();
            while(headerValues.hasMoreElements()) {
                tmp.add(headerValues.nextElement());
            }
            String[] hval = new String[tmp.size()];
            headers.put(header, tmp.size() == 1 ? tmp.get(0) : tmp.toArray(hval));
        }
        vo.setHeaders(headers);
        String path = request.getRequestURI().substring(request.getContextPath() == null ? 1 : request.getContextPath().length() + 1);
        String[] paths = path.split("/");
        vo.setPaths(paths);
        String qs = request.getQueryString();
        if(qs != null) {
            String[] pairs = qs.split("&");
            Map<String, Object> urlParams = new HashMap<>();
            for (int i = 0; i < pairs.length; i++) {
                String[] kv = pairs[i].split("=");
                urlParams.put(kv[0], kv[1]);
            }
            vo.setQueryString(urlParams);
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        InputStream is = request.getInputStream();
        int val = 0;
        while((val = is.read()) != -1) {
            baos.write(val);
        }
        vo.setBody(baos.toString(charset));

//        response.addHeader();
//        response.getOutputStream().write();
    }

    /**
     * 标准网关入口
     * @param req
     * @param br
     * @return
     */
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
//        DispatcherExecutionChain chain = adapter.getDispatcherExecutionChain(req);
//        DispatcherService dispatcher = chain.getDispatcher();
//        List<DispatcherInterceptor> inteceptors = chain.getInterceptors();
//        for (DispatcherInterceptor inteceptor : inteceptors) {
//            inteceptor.preDispatch(req, dispatcher);
//            if(inteceptor instanceof ParameterInteceptor && ((ParameterInteceptor) inteceptor).canIntecept(req)) {
//            	((ParameterInteceptor) inteceptor).intecept(req);
//            }
//        }
//        log.debug("RPC invoke parameters: {}", req);
//        Object remoteResult = dispatcher.doDispatch(req);
//        log.debug("RPC invoke result: {}", remoteResult);
//        for (DispatcherInterceptor inteceptor : inteceptors) {
//            Object mappingResult = inteceptor.postDispatch(remoteResult, dispatcher);
//            remoteResult = mappingResult;
//        }
//        log.debug("response: {}", remoteResult);
//        result.setResult(remoteResult);
        return result;
    }

}
