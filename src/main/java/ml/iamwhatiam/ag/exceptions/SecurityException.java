/*
 * Copyright 2017 Zhongan.com All right reserved. This software is the
 * confidential and proprietary information of Zhongan.com ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Zhongan.com.
 */
package ml.iamwhatiam.ag.exceptions;

/**
 * 安全异常
 * 
 * @author iMinusMinus
 * @since 2017-10-31
 */
public class SecurityException extends RuntimeException {

    private static final long serialVersionUID = 6431405472707338343L;

    public SecurityException() {
        super();
    }

    public SecurityException(String message) {
        super(message);
    }

    public SecurityException(String message, Exception cause) {
        super(message, cause);
    }

    public SecurityException(Exception cause) {
        super(cause);
    }

}
