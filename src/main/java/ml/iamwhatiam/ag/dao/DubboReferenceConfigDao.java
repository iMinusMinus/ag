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

import ml.iamwhatiam.ag.domain.DubboReferenceBeanDomain;

/**
 * Dubbo ReferenceBean config
 *
 * @author iMinusMinus
 * @since 2017-11-04
 */

public interface DubboReferenceConfigDao extends RpcConfigDao {
	
	@Select("SELECT * FROM dubbo_reference_bean")
	@Results({
		@Result(column = "id", property = "id"),
		@Result(column = "spring_bean_id", property = "springBeanId"),
		@Result(column = "interface_name", property = "interfaceName"),
		@Result(column = "provider_version", property = "version"),
		@Result(column = "check_provider_avaliable", property = "check"),
		@Result(column = "generic", property = "generic"),
	})
	@Override
	List<DubboReferenceBeanDomain> load();
	
	@Select("SELECT * FROM dubbo_reference_bean WHERE interface_name = #{0} AND provider_version = #{1}")
	@Results({
		@Result(column = "id", property = "id"),
		@Result(column = "spring_bean_id", property = "springBeanId"),
		@Result(column = "interface_name", property = "interfaceName"),
		@Result(column = "provider_version", property = "version"),
		@Result(column = "check_provider_avaliable", property = "check"),
		@Result(column = "generic", property = "generic"),
	})
	@Override
	DubboReferenceBeanDomain findByInterfaceNameAndVersion(String interfaceName, String version);

}
