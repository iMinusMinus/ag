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

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.security.NoSuchAlgorithmException;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

/**
 * 安全工具测试类
 * 
 * @author iMinusMinus
 * @since 2017-11-3
 */
public class SecurityUtilsTest {

    @Test
    public void testSha1() {
        String alg = "SHA1";//SHA1 also acceptable
        try {
            Assert.assertEquals("a9993e364706816aba3e25717850c26c9cd0d89d",
                    SecurityUtils.digest(alg, "abc".getBytes()));
            String large = "很长的字符串，长度大于哈希后字符长度，我也编不下去了，你们懂的，这就好了";
            Assert.assertEquals("62c001c5eea559d9a660d0c3aee2b01fda657cb5",
                    SecurityUtils.digest(alg, large.getBytes()));
        } catch (NoSuchAlgorithmException e) {
            Assert.fail();
        }
    }

    @Test
    public void testMd5() {
        String alg = "MD5";
        try {
            Assert.assertEquals("900150983cd24fb0d6963f7d28e17f72", SecurityUtils.digest(alg, "abc".getBytes()));
            String large = "很长的字符串，长度大于哈希后字符长度，我也编不下去了，你们懂的，这就好了";
            Assert.assertEquals("a7341a62c6b905ea8e58d6b46db312b4", SecurityUtils.digest(alg, large.getBytes()));
        } catch (NoSuchAlgorithmException e) {
            Assert.fail();
        }
    }

    @Test
    public void testSha256() {
        String alg = "SHA-256";
        try {
            Assert.assertEquals("ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad",
                    SecurityUtils.digest(alg, "abc".getBytes()));
            String large = "很长的字符串，长度大于哈希后字符长度，我也编不下去了，你们懂的，这就好了";
            Assert.assertEquals("c6ea22291c31fe6b1dfa9ee29e49144f8ecbcabac51f5f6efdda9f2535da3af3",
                    SecurityUtils.digest(alg, large.getBytes()));
        } catch (NoSuchAlgorithmException e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    @Ignore
    public void testSha1File() {
        String hash = "ef1ba96278ad0e774f9171fc36ab330e2ef5ab4e";// 获取地址为https://archive.apache.org/dist/tomcat/tomcat-7/v7.0.69/bin/apache-tomcat-7.0.69-windows-x64.zip.sha1
        String alg = "SHA-1";
        String file = "D:\\Downloads\\apache-tomcat-7.0.69-windows-x64.zip";//获取地址为https://archive.apache.org/dist/tomcat/tomcat-7/v7.0.69/bin/apache-tomcat-7.0.69-windows-x64.zip
        try {
            FileInputStream fis = new FileInputStream(file);
            byte[] data = new byte[1024];
            int read = 0;
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            while ((read = fis.read(data)) != -1) {
                baos.write(data, 0, read);
            }
            Assert.assertEquals(hash, SecurityUtils.digest(alg, baos.toByteArray()));
            fis.close();
            baos.close();
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void testDes() {
        //密钥大于7byte部分被忽略
        String keyt = "MTIzNDU2Nzg=";
        String plainText = "test很大的数据量，DES密钥长度为56bit，DESede可为112bit、168bit，默认为168bit，AES可为128，无权限限制可为192、256bit，默认为128bit，该字符串长度大于密钥长度，加密时不需要强制分组";
        String ciphered = SecurityUtils.encrypt("DES/ECB/PKCS5Padding", plainText.getBytes(), keyt);
        Assert.assertEquals(
                "8TP68exFNme7ZuTGq/mWyzA0iRc5Pz5erSV2r0e4yxM3teOo166L83V2UHsjHm5mVMkf2yecE6+5n+AfdU555IkEriKLa1+PaE5AKWFdjmhXW+tcSB7qR4hIYtA4SzsmebvLZXZ97+qbNyyYV/nrz3jAS4KGpRv3QHvzVtYV1uFtAP9AtBKQguXfLAfDWwPjMLAAhvRM6kqRXq2FWdpVHSKBNT5dYdQsFYH3MQVtIoYMGrF1eMqzcadMyjSTaWQOSjs2RS4x1HTmpDUZXqnBUrakB+w4tKufyxifcuThUW5Ju5izQ/wh0I709clF5FK6",
                ciphered);
        byte[] plain = SecurityUtils.decrypt("DES", ciphered, keyt);
        Assert.assertEquals(plainText, new String(plain));
    }

    @Test
    public void testRsa() {
        String privateKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAOw1W6xaGLll7OtAUEbFlCSDfTd+nqxVWXyllmiV+YjiBzkov86O2GvOI9lOu8XlEy45cWk9e/CZCdH0V92IMi3tyLl8ycb4jC0fta0TWNihe6CyhsVUj1kPzYy+AH+Loz5mcgF6sz//md9MOID1/Qd/YF19gcw6n/o4V482Rve5AgMBAAECgYBZM0k8TAXcNaaDrJTkNQbdxx4JT/LB57VUgf/L3R8P1zOdHUtZyM3n4D/fd4EnmXtl0GGIuaRNVq3DsL9htGYmAkOsggQnBPdTFbe7Q6KhXZLusyoNUFvlIAbQSVdCgAWCA9V6iZ6vrmNW2urj1h1KQFvAbDVllRDZ4vaejF4xVQJBAPyYSq8RsXgEAiZvk01+7rAiKvgsPpxf2wG2UHGSvatizDQZIgfME9191hz3b2yOUqi0Hha2MttLi4LWfZbF9BMCQQDvZIQ39DZiSxOYn24sxg26+qUx48Yc2Yes9m6UEzAuDuDCMapyJ+v62rjnPi9jn9X+hxWfhdpJHfj6KVO1xuaDAkBkzL1Y+b2RgD//aJ0m2tWTkj8FhFqD+riiCUg22nE4OJf23mS3KdhvlizgqFldv7n6us4bECBhZNdKoh/CEELjAkEA7PtXfECweZuSuZrSKVaijv/C+uFd5H9fNVT64HEiV+X4j6U08y8cB0fwlVJU/U1kPUSinjmWfp1CNPsmWCOfWwJBAKzoNFbjXGCnoXveyaKxzuo4Xb/otN+4pSCtikMxamASEfFkoxqLWMQFJHXGoDEheycBLFfVvodBQjh1LH1XJkU=";
        String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDsNVusWhi5ZezrQFBGxZQkg303fp6sVVl8pZZolfmI4gc5KL/OjthrziPZTrvF5RMuOXFpPXvwmQnR9FfdiDIt7ci5fMnG+IwtH7WtE1jYoXugsobFVI9ZD82MvgB/i6M+ZnIBerM//5nfTDiA9f0Hf2BdfYHMOp/6OFePNkb3uQIDAQAB";
        String plainText = "test测试数据data";
        String ciphered = SecurityUtils.encrypt("RSA", plainText.getBytes(), publicKey);
        byte[] plain = SecurityUtils.decrypt("RSA", ciphered, privateKey.replace(" ", ""));
        Assert.assertEquals(plainText, new String(plain));
        String test = SecurityUtils.sign("test".getBytes(), "SHA1withRSA", privateKey.replace(" ", ""));
        Assert.assertTrue(SecurityUtils.verify(test, "test".getBytes(), "SHA1withRSA", publicKey));
        String hashed = "xncSRgyqrICvoYGDS+YMVMJ92ZyWpEFsXVKoj5XtabtTeYeW9yHNwftxeOalavh8b4yXgb18sXt+rUc6jEQUfMHS7WNbQ+dIR1kUe2Xettxm0McSHiyGVdLhT8CJXozjSvxyrPJUWLP5xOUu7dr1UT1dxPQTUYFCVi/vGnVAJGs=";
        Assert.assertEquals(hashed, test);
        StringBuilder large = new StringBuilder();
        large.append("{\"key\":[");
        for (int i = 0; i < 100; i++) {
            large.append("\"index" + i).append("\",");
        }
        large.append("],{");
        for (int i = 0; i < 100; i++) {
            large.append("\"index" + i).append("\":\"").append("value" + i).append("\",");
        }
        large.append("}");
        String c = SecurityUtils.encrypt("RSA", large.toString().getBytes(), publicKey);
        byte[] p = SecurityUtils.decrypt("RSA", c, privateKey.replace(" ", ""));
        Assert.assertEquals(large.toString(), new String(p));
    }

    @Test
    public void testDesede() {
        //密钥最小24byte？
        String keyt = "IyFpTWludXNNaW51c0BnaXRodWIuY29t";
        String plainText = "test data";
        String ciphered = SecurityUtils.encrypt("DESede", plainText.getBytes(), keyt);
        Assert.assertEquals("F1SXc/OyQaXfPrhLx36hzw==", ciphered);
        byte[] plain = SecurityUtils.decrypt("TripleDES/ECB/PKCS5Padding", ciphered, keyt);
        Assert.assertEquals(plainText, new String(plain));
    }

    @Test
    public void testAes() {
        String keyt = "MTIzNDU2Nzg5YWJjZGVmZw==";
        String plainText = "test data";
        String ciphered = SecurityUtils.encrypt("AES", plainText.getBytes(), keyt);
        Assert.assertEquals("Wj2vpJBNRg7eNyf6jVjQKQ==", ciphered);
        byte[] plain = SecurityUtils.decrypt("AES/ECB/PKCS5Padding", ciphered, keyt);
        Assert.assertEquals(plainText, new String(plain));
    }

}
