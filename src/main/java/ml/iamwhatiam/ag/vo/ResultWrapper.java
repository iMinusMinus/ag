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
package ml.iamwhatiam.ag.vo;

import java.io.Serializable;

/**
 * 结果封装
 *
 * @author iMinusMinus
 * @since 2017-10-08
 */

public class ResultWrapper<T> implements Serializable {

	private static final long serialVersionUID = -4698386627629278497L;
	
	/**
	 * 调用者异常：参数非法等
	 */
	public static final int CLIENT_ERROR = 1;
	
	/**
	 * 分发异常，抵达实际被调用者前本系统处理出现的异常
	 */
	public static final int DISPATCHING_ERROR = 2;
	
	/**
	 * 处理异常，被调用者抛出的异常
	 */
	public static final int PROCESSING_ERROR = 3;
	
	/**
	 * 状态：0，成功
	 */
	private int status;
	
	/**
	 * 描述信息
	 */
	private String message;
	
	/**
	 * 结果
	 */
	private T result;

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public T getResult() {
		return result;
	}

	public void setResult(T result) {
		this.result = result;
	}

	@Override
	public String toString() {
		return "{\"status\":" + status + ", \"message\":\"" + message + "\", \"result\":" + result + "}";
	}
	
	

}
