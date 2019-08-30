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

import org.apache.ibatis.annotations.Many;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;

import ml.iamwhatiam.ag.domain.PartnerConfigDomain;

/**
 * 针对合作方的配置
 * 
 * @author iMinusMinus
 * @since 2017-11-06
 */
@Repository("partnerGatewayConfig")
public interface PartnerConfigDao {

    @Select("SELECT * FROM partner_config WHER app_id = #{appId}")
    @Results({ @Result(id = true, column = "id", property = "id"), @Result(column = "app_id", property = "appId"),
            @Result(column = "transformation", property = "transformation"),
            @Result(column = "serialize_format", property = "format"),
            @Result(column = "charset_name", property = "charset"),
            @Result(column = "public_key", property = "publicKey"),
            @Result(column = "sign_algorithm", property = "signAlgorithm"),
            @Result(column = "joint", property = "joint"), @Result(column = "private_key", property = "privateKey"),
            @Result(column = "app_id", property = "parameterMappings", many = @Many(select = "ml.iamwhatiam.ag.dao.GatewayParameterMappingDao.findByAppId")), })
    PartnerConfigDomain findByAppid(String appid);

}
