package com.usoft.security.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * Open Api 签名 工具类
 *
 * @author: wangcanyi
 * @date: 2018-08-22 11:18
 **/
public class OpenApiSignUtil {
	public static final String SIGNATURE_KEY = "signature";
	private static final String HTTP_GET = "GET";
	private static final String ENCODE = "UTF-8";
	private static final Logger LOGGER = LoggerFactory.getLogger(OpenApiSignUtil.class);

	/**
	 * 验证签名
	 *
	 * @param request   请求参数
	 * @param secretKey 密钥
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 * @throws IOException
	 */
	public static boolean verifySignForHttpGet(HttpServletRequest request, String secretKey) throws NoSuchAlgorithmException, InvalidKeyException, IOException {
		if (request == null) {
			throw new RuntimeException("request不能为空");
		}

		String oldSignatureValue = request.getParameter(SIGNATURE_KEY);
		if (oldSignatureValue == null || oldSignatureValue.trim().length() == 0) {
			throw new RuntimeException("signature为空");
		}
		String newSignatureValue = signForHttpGet(request, secretKey);
		LOGGER.debug("[OpenApiSignUtil.verifySignForHttpGet]旧签名{}：新签名：{}", oldSignatureValue, newSignatureValue);
		return oldSignatureValue.equals(newSignatureValue);
	}

	/**
	 * 验证签名
	 *
	 * @param jsonBody  json内容
	 * @param secretKey 密钥
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 * @throws UnsupportedEncodingException
	 */
	public static boolean verifySignForJson(String jsonBody, String secretKey) throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {
		if (jsonBody == null || jsonBody.trim().length() == 0) {
			throw new RuntimeException("jsonBody不能为空");
		}
		String oldSignatureValue = getValueByJson(jsonBody, SIGNATURE_KEY);
		if (oldSignatureValue == null || oldSignatureValue.trim().length() == 0) {
			throw new RuntimeException("signature为空");
		}
		String newSignatureValue = signForJson(jsonBody, secretKey);
		LOGGER.debug("[OpenApiSignUtil.verifySignForJson]旧签名{}：新签名：{}", oldSignatureValue, newSignatureValue);
		return oldSignatureValue.equals(newSignatureValue);
	}

	/**
	 * 签名 For Http Get
	 *
	 * @param request   Http Request
	 * @param secretKey 密钥
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 * @throws UnsupportedEncodingException
	 */
	public static String signForHttpGet(HttpServletRequest request, String secretKey) throws NoSuchAlgorithmException, InvalidKeyException, IOException {
		if (request == null) {
			throw new RuntimeException("request不能为空");
		}
		if (!HTTP_GET.equals(request.getMethod())) {
			throw new RuntimeException("只支持GET请求");
		}
		String src = getSrcByHttpGet(request);
		return sign(src, secretKey);
	}

	/**
	 * 签名 For Json
	 *
	 * @param jsonBody  json内容
	 * @param secretKey 密钥
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 * @throws UnsupportedEncodingException
	 */
	public static String signForJson(String jsonBody, String secretKey) throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {
		if (jsonBody == null || jsonBody.trim().length() == 0) {
			throw new RuntimeException("jsonBody不能为空");
		}
		String src = getSrcByJson(jsonBody);
		return sign(src, secretKey);
	}

	/**
	 * 签名
	 *
	 * @param src       待签名字符串
	 * @param secretKey 密钥
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws InvalidKeyException
	 * @throws UnsupportedEncodingException
	 */
	public static String sign(String src, String secretKey) throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {
		if (src == null || src.trim().length() == 0) {
			throw new RuntimeException("待签名字符串为空");
		}
		if (secretKey == null || secretKey.trim().length() == 0) {
			throw new RuntimeException("密钥为空");
		}
		return HmacUtil.sign(src, secretKey, HmacUtil.HMAC_SHA256_ALGORITHM);
	}

	/**
	 * 获取 Http Get 请求参数，排除签名键值对，注意：排除签名键值对，非签名值
	 *
	 * @param request HTTP 请求头
	 * @return
	 */
	private static String getSrcByHttpGet(HttpServletRequest request) throws UnsupportedEncodingException {
		String signatureValue = request.getParameter(SIGNATURE_KEY);
		String queryString = URLDecoder.decode(request.getQueryString(), ENCODE);
		if (queryString == null || queryString.trim().length() == 0) {
			throw new RuntimeException("请求参数为空");
		}
		if (signatureValue == null || signatureValue.trim().length() == 0) {
			throw new RuntimeException("signature为空");
		}
		LOGGER.debug("[OpenApiSignUtil.getSrcByHttpGet]请求参数：{}", queryString);
		//获取签名键值对
		String signatureKV = SIGNATURE_KEY + "=" + signatureValue;
		LOGGER.debug("[OpenApiSignUtil.getSrcByHttpGet]签名键值对:{}", signatureKV);
		//获取签名键值对位置
		int signatureIndex = queryString.indexOf(signatureKV);
		if (signatureIndex == -1) {
			throw new RuntimeException("不存在signature键值对");
		}
		//排队签名键值对
		String src;
		if (signatureIndex == 0) {
			//排第一位
			src = queryString.replace(signatureKV + "&", "");
		} else {
			//不排在第一位
			src = queryString.replace("&" + signatureKV, "");
		}
		LOGGER.debug("[OpenApiSignUtil.getSrcByHttpGet]待签名串：{}", src);
		return src;
	}

	/**
	 * 获取 Json 请求参数，排除签名值,注意：排除签名值，非签名键值对
	 *
	 * @param jsonBody
	 * @return
	 * @throws IOException
	 */
	private static String getSrcByJson(String jsonBody) {
		if (jsonBody == null || jsonBody.trim().length() == 0) {
			throw new RuntimeException("请求参数为空");
		}
		LOGGER.debug("[OpenApiSignUtil.getSrcByJson]请求参数：{}", jsonBody);
		String signatureValue = getValueByJson(jsonBody, SIGNATURE_KEY);
		if (signatureValue == null || signatureValue.trim().length() == 0) {
			throw new RuntimeException("不存在signature值");
		}
		LOGGER.debug("[OpenApiSignUtil.getSrcByJson]签名值:{}", signatureValue);
		//排除签名值，注意：json只排除签名值，而HTTP GET则排除签名键值对
		String src = jsonBody.replace(signatureValue, "");
		LOGGER.debug("[OpenApiSignUtil.getSrcByJson]待签名串：{}", src);
		return src;
	}

	/**
	 * 获取Json里面的特定值
	 *
	 * @param jsonBody json内容
	 * @param key      Json里的属性名
	 * @return
	 */
	private static String getValueByJson(String jsonBody, String key) {
		if (jsonBody == null || jsonBody.trim().length() == 0) {
			throw new RuntimeException("jsonBody不能为空");
		}
		if (key == null || key.trim().length() == 0) {
			throw new RuntimeException("Key不能为空");
		}
		JSONObject jsonObject = JSON.parseObject(jsonBody);
		if (jsonObject.containsKey(key)) {
			return jsonObject.getString(key);
		} else {
			return "";
		}
	}
}
