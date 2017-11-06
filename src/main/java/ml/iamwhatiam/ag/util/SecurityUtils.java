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
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.RSAKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 安全工具类
 * <ol>
 * <li>签名
 * <ul>
 * <li>私钥签名，公钥验证
 * <li>消息摘要
 * </ul>
 * <li>加密
 * <ul>
 * <li>公钥加密，私钥解密
 * <li>密钥加密，密钥解密
 * </ul>
 * </ol>
 * 
 * @author iMinusMinus
 * @since 2017-10-30
 */
public class SecurityUtils {

    private static Logger log = LoggerFactory.getLogger(SecurityUtils.class);

    private SecurityUtils() {

    }

    /**
     * 加密
     * 
     * @param transformation form: "algorithm/mode/padding" or "algorithm"
     *            <li>algorithm: AES, DES, DESede, RSA;
     *            <li>mode: CBC, ECB;
     *            <li>padding: NoPadding , PKCS5Padding, PKCS1Padding,
     *            OAEPWithSHA-1AndMGF1Padding, OAEPWithSHA-256AndMGF1Padding
     * @param plain 明文字节
     * @param keyt 密钥
     * @return 密文
     */
    public static String encrypt(String transformation, byte[] plain, String keyt) {
        int length = plain.length;
        byte[] ciphered = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            Key key = null;
            int blockSize = Integer.MAX_VALUE;
            if (isSymmetrical(transformation)) {
                key = getKey(transformation.split("/")[0], keyt);
            } else {
                key = getPublicKey(transformation.split("/")[0], keyt);
                blockSize = getMaxBlockSize((RSAKey) key) - 11;
            }
            Cipher cipher = Cipher.getInstance(transformation);
            cipher.init(Cipher.ENCRYPT_MODE, key);
            byte[] tmp = null;
            int offset = 0;
            while (length - offset > 0) {
                if (length - offset > blockSize) {
                    tmp = cipher.doFinal(plain, offset, blockSize);
                } else {
                    tmp = cipher.doFinal(plain, offset, length - offset);
                }
                baos.write(tmp, 0, tmp.length);
                offset += blockSize;
            }
            ciphered = baos.toByteArray();
        } catch (NoSuchAlgorithmException e) {
            log.error("加密算法有误", e);
            throw new SecurityException(e.getMessage(), e);
        } catch (NoSuchPaddingException e) {
            log.error("填充模式错误", e);
            throw new SecurityException(e.getMessage(), e);
        } catch (InvalidKeyException e) {
            log.error("密钥无效", e);
            throw new SecurityException(e.getMessage(), e);
        } catch (IllegalBlockSizeException e) {
            log.error("无效块大小", e);
            throw new SecurityException(e.getMessage(), e);
        } catch (BadPaddingException e) {
            log.error("数据填充模式有误", e);
            throw new SecurityException(e.getMessage(), e);
        } catch (InvalidKeySpecException e) {
            log.error("密钥算法错误", e);
            throw new SecurityException(e.getMessage(), e);
        } finally {
            try {
                baos.close();
            } catch (IOException ignore) {
                log.error(ignore.getMessage(), ignore);
            }
        }
        return Base64.encode(ciphered);
    }

    /**
     * 签名
     * <li>私钥签名，公钥验证
     * <li><del>密钥加密，验证消息摘要</del>
     * 
     * @param data 需要签名的内容
     * @param digestWithEncryption 签名算法(SHA1withDSA, SHA1withRSA,
     *            SHA256withRSA)，格式为：&#60;digest&#62;with&#60;encryption&#62;
     * @param privateKey PKCS8格式私钥，不含'BEGIN'行和'END'行信息，内容不含空格
     * @return 签名
     */
    public static String sign(byte[] data, String digestWithEncryption, String privateKey) {
        try {
            Signature signature = Signature.getInstance(digestWithEncryption);
            String encryption = digestWithEncryption.split("with")[1];
            signature.initSign(getPrivateKey(encryption, privateKey));
            signature.update(data);
            byte[] signed = signature.sign();
            return Base64.encode(signed);
        } catch (NoSuchAlgorithmException e) {
            log.error("签名算法有误", e);
            throw new SecurityException(e.getMessage(), e);
        } catch (InvalidKeyException e) {
            log.error("密钥错误", e);
            throw new SecurityException(e.getMessage(), e);
        } catch (InvalidKeySpecException e) {
            log.error("密钥算法错误", e);
            throw new SecurityException(e.getMessage(), e);
        } catch (SignatureException e) {
            log.error("签名异常", e);
            throw new SecurityException(e.getMessage(), e);
        }
    }

    /**
     * 验证签名
     * 
     * @param sign 签名
     * @param content 内容
     * @param digestWithEncryption 签名算法(SHA1withDSA, SHA1withRSA, SHA256withRSA)
     * @param publicKey 公钥，不含'BEGIN'行和'END'行信息
     * @return 签名和计算值是否一致
     */
    public static boolean verify(String sign, byte[] content, String digestWithEncryption, String publicKey) {
        try {
            Signature signature = Signature.getInstance(digestWithEncryption);
            signature.initVerify(getPublicKey(digestWithEncryption.split("with")[1], publicKey));
            signature.update(content);
            return signature.verify(Base64.decode(sign));
        } catch (NoSuchAlgorithmException e) {
            log.error("签名算法错误", e);
            throw new SecurityException(e.getMessage(), e);
        } catch (InvalidKeySpecException e) {
            log.error("密钥算法错误", e);
            throw new SecurityException(e.getMessage(), e);
        } catch (InvalidKeyException e) {
            log.error("公钥错误", e);
            throw new SecurityException(e.getMessage(), e);
        } catch (SignatureException e) {
            log.error("签名异常", e);
            throw new SecurityException(e.getMessage(), e);
        }
    }

    /**
     * 解密
     * 
     * @param transformation form: "algorithm/mode/padding" or "algorithm"
     *            <li>algorithm: AES, DES, DESede, RSA;
     *            <li>mode: CBC, ECB;
     *            <li>padding: NoPadding , PKCS5Padding, PKCS1Padding,
     *            OAEPWithSHA-1AndMGF1Padding, OAEPWithSHA-256AndMGF1Padding
     * @param ciphered base64格式密文
     * @return
     */
    public static byte[] decrypt(String transformation, String ciphertext, String keyt) {
        byte[] plainText = null;
        byte[] ciphered = Base64.decode(ciphertext);
        int length = ciphered.length;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            Key key = null;
            int blockSize = Integer.MAX_VALUE;
            if (isSymmetrical(transformation)) {
                key = getKey(transformation.split("/")[0], keyt);
            } else {
                key = getPrivateKey(transformation.split("/")[0], keyt);
                blockSize = getMaxBlockSize((RSAKey) key);
            }
            Cipher cipher = Cipher.getInstance(transformation);
            cipher.init(Cipher.DECRYPT_MODE, key);
            byte[] tmp = null;
            int offset = 0;
            while (length - offset > 0) {
                if (length - offset > blockSize) {
                    tmp = cipher.doFinal(ciphered, offset, blockSize);
                } else {
                    tmp = cipher.doFinal(ciphered, offset, length - offset);
                }
                baos.write(tmp, 0, tmp.length);
                offset += blockSize;
            }
            plainText = baos.toByteArray();
        } catch (InvalidKeyException e) {
            log.error("无效密钥", e);
            throw new SecurityException(e.getMessage(), e);
        } catch (NoSuchAlgorithmException e) {
            log.error("解密算法错误", e);
            throw new SecurityException(e.getMessage(), e);
        } catch (NoSuchPaddingException e) {
            log.error("填充方式错误", e);
            throw new SecurityException(e.getMessage(), e);
        } catch (IllegalBlockSizeException e) {
            log.error("块大小错误", e);
            throw new SecurityException(e.getMessage(), e);
        } catch (BadPaddingException e) {
            log.error(e.getMessage(), e);
            throw new SecurityException("配置错误", e);
        } catch (InvalidKeySpecException e) {
            log.error("密钥算法错误", e);
            throw new SecurityException(e.getMessage(), e);
        } finally {
            try {
                baos.close();
            } catch (IOException ignore) {
                log.error(ignore.getMessage(), ignore);
            }
        }
        return plainText;
    }

    /**
     * 生成消息摘要
     * 
     * @param algorithm 消息摘要算法：MD5（不建议使用）、SHA1、SHA-256
     * @param data 消息内容
     * @return 消息摘要（小写）
     * @throws NoSuchAlgorithmException
     */
    public static String digest(String algorithm, byte[] data) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance(algorithm);
        byte[] hash = md.digest(data);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < hash.length; i++) {
            sb.append(Integer.toHexString((hash[i] & 0xFF) + 0x100).substring(1));
        }
        return sb.toString();
    }

    /**
     * @param algorithm 算法：DiffieHellman、DSA、RSA
     * @param privateKey 密钥，Java和C#使用PKCS8，PHP使用pem，密钥中不要有空格！
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    private static PrivateKey getPrivateKey(String algorithm, String privateKey)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
        return keyFactory.generatePrivate(new PKCS8EncodedKeySpec(Base64.decode(privateKey)));
    }

    /**
     * @param algorithm 算法：DiffieHellman、DSA、RSA
     * @param publicKey 公钥
     * @return
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeySpecException
     */
    private static PublicKey getPublicKey(String algorithm, String publicKey)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
        return keyFactory.generatePublic(new X509EncodedKeySpec(Base64.decode(publicKey)));
    }

    /**
     * 获取（对称加密）密钥
     * 
     * @param algorithm 加密算法
     * @param keyt base64编码的密钥
     * @return 密钥
     * @throws NoSuchAlgorithmException
     */
    private static Key getKey(String algorithm, String keyt) {
        return new SecretKeySpec(Base64.decode(keyt), algorithm);
    }

    /**
     * RSA: 明文长度<= 密钥长度 - 11, 超出长度则需使用分片加密
     * 
     * @param key RSA公钥或私钥
     * @return 解密最大块长度，如果是加密则再减11
     */
    private static int getMaxBlockSize(RSAKey key) {
        return key.getModulus().bitLength() / 8;
    }

    /**
     * @param transformation 算法
     * @return 是否对称加密
     */
    private static boolean isSymmetrical(String transformation) {
        String algorithm = transformation.split("/")[0];
        return !"RSA".equals(algorithm) && !"DSA".equals(algorithm);
    }

}
