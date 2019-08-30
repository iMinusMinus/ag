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
package ml.iamwhatiam.ag.constants;

import lombok.Getter;

import java.io.Serializable;

/**
 * 网关字段
 * 
 * @author iMinusMinus
 * @since 2018-03-27
 */
@Getter
public enum GatewayField implements Serializable {

    APP_ID("appId"), //move to URL path
    SIGN("sign"), //may be part of HTTP header
    SIGN_TYPE("signType"), //may be configuration
    API("api"), //may be in URL path
    CHARSET("charset"), //may be configuration or as part of HTTP header
    FORMAT("format"), //may be configuration or as part of HTTP header
    PARAMETER("parameter"),
    TRANSFORMATION("transformation"), //may be configuration or as part of HTTP header
    VERSION("version"), //may be in URL path or no version
    TIMESTAMP("timestamp"), //may be part of HTTP header
    SESSION_ID("sessionId"), //secret key difference in different session, keep the same on same session, null if not 1st time
    SECRET_KEY("secretKey"), //client user server RSA public key encrypt this secret key, then use this secret key encrypt content
    ALGORITHM_PARAMETER("algorithmParameter"),//initial vector for symmetric algorithm
    //
    ;

    private String fieldName;

    GatewayField(String fieldName) {
        this.fieldName = fieldName;
    }

}
