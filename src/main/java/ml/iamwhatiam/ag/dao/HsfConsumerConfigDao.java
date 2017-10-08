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

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Update;

import ml.iamwhatiam.ag.domain.HSFSpringConsumerBeanDomain;

/**
 * HSFSpringCunsumerBean CRUD
 * 
 * @see HSFSpringConsumerBeanDomain
 * @author iMinusMinus
 * @since 2017-09-07
 */
public interface HsfConsumerConfigDao extends RpcConfigDao {

    @Insert("INSERT INTO hsf_spring_consumer_bean (interface_name, version, target) values (#{interfaceName}, #{version}, #{target})")
    @SelectKey(statement = {
            "SELECT LAST_INSERT_ID() AS ID" }, keyProperty = "id", before = false, resultType = long.class)
    long save(HSFSpringConsumerBeanDomain domain);
    
    @Update("<script>UPDATE hsf_spring_consumer_bean"
    		+ "<trim prefix=\"SET\" suffixOverrides=\",\"> <if test=\"group != null\">`group` = #{group}</if>"
    		+ "<if test=\"version != null\">`version` = #{version}</if>"
    		+ "<if test=\"target != null\">`target` = #{target}</if></trim>"
    		+ "WHERE id = #{id}</script>")
    int update(HSFSpringConsumerBeanDomain domain);
    
    @Delete("DELETE FROM hsf_spring_consumer_bean WHERE id = #{0}")
    int delete(long id);

    @Select("SELECT id, interface_name, `version`, `group`, target FROM hsf_spring_consumer_bean")
    @Results({ @Result(column = "id", property = "id"), @Result(column = "interface_name", property = "interfaceName"),
            @Result(column = "version", property = "version"), @Result(column = "group", property = "group"),
            @Result(column = "target", property = "target"), })
    List<HSFSpringConsumerBeanDomain> load();

    @Select("SELECT id, interface_name, `version`, `group`, target FROM hsf_spring_consumer_bean WHERE interface_name = #{0} AND version = #{1}")
    @Results({ @Result(column = "id", property = "id"), @Result(column = "interface_name", property = "interfaceName"),
            @Result(column = "version", property = "version"), @Result(column = "group", property = "group"),
            @Result(column = "target", property = "target"), })
    HSFSpringConsumerBeanDomain findByInterfaceNameAndVersion(String interfaceName, String version);

}
