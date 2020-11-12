package com.usoft.security.utils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * HMAC 加密工具类
 *
 * @author: wangcanyi
 * @date: 2018-08-22 10:32
 **/
public class HmacUtil {
	public static final String HMAC_ALGORITHM = "HmacSHA1";
	public static final String HMAC_SHA256_ALGORITHM = "HmacSHA256";
	private static final String CONTENT_CHARSET = "UTF-8";

	/**
	 * 签名
	 *
	 * @param src       待签名字符串
	 * @param key       密钥
	 * @param algorithm 算法
	 * @return
	 */
	public static String sign(String src, String key, String algorithm) throws NoSuchAlgorithmException, UnsupportedEncodingException, InvalidKeyException {
		if (src == null || src.trim().length() == 0) {
			throw new RuntimeException("src不能为空");
		}
		if (key == null || src.trim().length() == 0) {
			throw new RuntimeException("key不能为空");
		}
		if (algorithm == null || algorithm.trim().length() == 0) {
			throw new RuntimeException("algorithm不能为空");
		}
		if (!algorithm.equals(HMAC_ALGORITHM) && !algorithm.equals(HMAC_SHA256_ALGORITHM)) {
			throw new RuntimeException("不支持的algorithm");
		}
		Mac mac = Mac.getInstance(algorithm);
		SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(CONTENT_CHARSET), mac.getAlgorithm());
		mac.init(secretKey);
		byte[] digest = mac.doFinal(src.getBytes(CONTENT_CHARSET));
		//因需要给外部公司使用，需兼容Java7，故无法使用Java8特性的Base64，使用Java6的javax.xml.bind.DatatypeConverter
		//详情见：https://blog.csdn.net/u013476542/article/details/53213783
		return DatatypeConverter.printBase64Binary(digest);
	}
}
