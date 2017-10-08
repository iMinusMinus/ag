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

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Many;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Update;
import org.apache.ibatis.mapping.FetchType;
import org.springframework.stereotype.Repository;

import ml.iamwhatiam.ag.domain.MethodDomain;

/**
 * 服务配置
 * 
 * @author iMinusMinus
 * @since 2017-09-15
 */
@Repository("serviceConfig")
public interface ServiceConfigDao {

    @Insert("INSERT INTO service (service_name, interface_name, method_name, rococo, return_type "
    		+ "VALUES (#{serviceName}, #{interfaceName}, #{methodName}, #{rococo}, #{returnType})")
    /* oracle before, mysql after */
    @SelectKey(statement = {
            "SELECT LAST_INSERT_ID() AS ID" }, keyProperty = "id", before = false, resultType = long.class)
    long save(MethodDomain method);
    
    @Update("<script>UPDATE service <trim prefix = \"SET\" suffixOverrides = \",\">"
    		+ "<if test=\"serviceName != null\">service_name = #{serviceName}</if>"
    		+ "<if test=\"interfaceName != null\">interface_name = interfaceName</if>"
    		+ "<if test=\"methodName != null\">method_name = #{methodName}</if>"
    		+ "<if test=\"rococo != null\">rococo = rococo</if>"
    		+ "<if test=\"returnType != null\">return_type = returnType</if></trim> "
    		+ "WHERE id = #{id}")
    int update(MethodDomain domain);
    
    @Delete("DELETE FROM service WHERE id = #{id}")
    int delete(long id);

    @Results({ @Result(id = true, column = "id", property = "id"),
            @Result(column = "service_name", property = "serviceName"),
            @Result(column = "interface_name", property = "interfaceName"),
            @Result(column = "method_name", property = "methodName"),
            @Result(column = "rococo", property = "rococo"),
            @Result(column = "return_type", property = "returnType"),
            @Result(column = "id", property = "parameters", many = @Many(select = "ml.iamwhatiam.ag.dao.ParamConfigDao.findByMethodId", fetchType = FetchType.LAZY)) })
    @Select("SELECT * FROM service s WHERE s.service_name = #{serviceName}")
    MethodDomain findByServiceName(String serviceName);

}
