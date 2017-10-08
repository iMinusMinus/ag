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

/**
 * 服务个性化前置后置处理
 * 
 * @author IminusMinus
 * @since 2017-09-07
 */
public enum Rococo {

    NONE(0),
    AUTHENTICATION(1), //登入验证
    AUTHORIZATION(2), //权限验证
    RESTRICTION(4), //接口限制：时间、次数
    SIGNATURE_VERIFICATION(8), //验签
    DECIPHERMENT(16), //解密
    PARAMETER_KEY_MAPPING(32), //参数key转换，例如：oldKey to outer.newKey --> {"oldKey":""} to {"outer":{"newKey":""}}
    RESULT_KEY_MAPPING(64), //结果key转换
    ENCRYPTION(128), //加密
    SIGNATURE(256),//签名
    ;

    /**
     * 调用前需要的处理，必须是0或2的n次幂
     */
    private int decoration;

    private Rococo(int decoration) {
        this.decoration = decoration;
    }

    public int getDecoration() {
        return decoration;
    }

    public boolean shouldDecorate(int decoration) {
        return (this.decoration & decoration) != 0;
    }

}
