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
import org.springframework.stereotype.Repository;

import ml.iamwhatiam.ag.domain.ParameterTypeDomain;

/**
 * 参数配置
 * 
 * @author iMinusMinus
 * @since 2017-09-19
 */
@Repository("paramConfig")
public interface ParamConfigDao {
	
	@Insert("INSERT INTO parameter_type (mid, position, type_name) "
			+ "VALUES (#{mid}, #{index}, #{type})")
	@SelectKey(statement = {
    "SELECT LAST_INSERT_ID() AS ID" }, keyProperty = "id", before = false, resultType = long.class)
	long save(ParameterTypeDomain domain);
	
	@Update("UPDATE parameter_type set position = #{index}, type_name = #{type} WHERE id = #{id}")
	int update(ParameterTypeDomain domain);
	
	@Delete("DELETE FROM parameter_type WHERE id = #{id}")
	int delete(long id);

    @Select("SELECT * FROM parameter_type p WHERE p.mid = #{id} ORDER BY p.position")
    @Results({ @Result(id = true, column = "id", property = "id"), @Result(column = "mid", property = "mid"),
            @Result(column = "position", property = "index"), @Result(column = "type_name", property = "type") })
    List<ParameterTypeDomain> findByMethodId(long mid);

}
