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
package ml.iamwhatiam.ag.util;

import java.lang.reflect.Method;

/**
 * JDK8有暴露Base64工具类，而低版本JDK没有暴露合适的base64编解码工具（可以使用sun.misc.BASE64Encoder和sun.
 * misc.BASE64Decoder等其他sun私有）。利用反射，调用未公开的java.util.prefs.Base64实现。
 * 
 * @author iMinusMinus
 * @since 2017-10-31
 */
public class Base64 {

    private static Class<?> delegate;

    private static Method   encode;

    private static Method   decode;

    static {
        delegate = ReflectionUtils.findClass("java.util.prefs.Base64");
        encode = ReflectionUtils.findAccessibleMethod("byteArrayToBase64", delegate, byte[].class);
        decode = ReflectionUtils.findAccessibleMethod("base64ToByteArray", delegate, String.class);
    }

    private Base64() {

    }

    /**
     * 将字符串按Base64编码
     * 
     * @param data
     * @return
     */
    public static String encode(byte[] data) {
        Object result = ReflectionUtils.invoke(encode, delegate, data);
        return (String) result;
    }

    /**
     * 将Base64编码的字节还原
     * 
     * @param data
     * @return
     */
    public static byte[] decode(String data) {
        Object result = ReflectionUtils.invoke(decode, delegate, data);
        return (byte[]) result;
    }
}
