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
package ml.iamwhatiam.ag.dao;

import java.util.List;

import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import ml.iamwhatiam.ag.domain.HttpBeanDomain;

/**
 * HTTP分发服务配置
 * 
 * @author iMinusMinus
 * @since 2017-09-23
 */
@Repository
public interface HttpConfigDao extends RpcConfigDao {

    /**
     * 保存服务配置信息
     * 
     * @param domain
     * @return
     */
    long save(HttpBeanDomain domain);

    /**
     * 按接口名和版本获取唯一的服务配置信息
     * 
     * @param interfaceName
     * @param version
     * @return
     */
    @Select("SELECT id, interface_name, `version` FROM hsf_spring_consumer_bean WHERE interface_name = #{param1} AND version = #{param2}")
    @Results({ @Result(column = "id", property = "id"), @Result(column = "interface_name", property = "interfaceName"),
            @Result(column = "version", property = "version"), })
    HttpBeanDomain findByInterfaceNameAndVersion(String interfaceName, String version);

}
