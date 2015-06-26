package com.qdc.plugins.weixin;

import java.io.StringReader;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.apache.cordova.LOG;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;
import org.xmlpull.v1.XmlPullParser;

import android.os.AsyncTask;
import android.util.Log;
import android.util.Xml;

import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

/**
 * 微信支付插件
 * 
 * @author NCIT
 * 
 */
public class WeixinPay extends CordovaPlugin {
	
	/** 公众账号ID */
	public static final String APP_ID = "wx2d789e6ca2ded095";
	
	/** 商户号 */
	public static final String MCH_ID = "1232782102";
	
	/** API密钥 */
	public static final String API_KEY = "";
	
	/** 通知地址 */
	public static final String NOTIFY_URL = "http://127.0.0.1/";

	/** 预付订单请求URL */
	public static final String UNIFIEDORDER_URL = "https://api.mch.weixin.qq.com/pay/unifiedorder";

	/** JS回调接口对象 */
	public static CallbackContext cbContext = null;

	/** LOG TAG */
	private static final String LOG_TAG = WeixinPay.class.getSimpleName();
	
	/**
	 * 插件初始化
	 */
	@Override
	public void initialize(CordovaInterface cordova, CordovaWebView webView) {
		LOG.d(LOG_TAG, "WeixinPay#initialize");

		super.initialize(cordova, webView);
	}

	/**
	 * 插件主入口
	 */
	@Override
	public boolean execute(String action, final JSONArray args,
			CallbackContext callbackContext) throws JSONException {
		LOG.d(LOG_TAG, "WeixinPay#execute");

		boolean ret = false;

		if ("payment".equalsIgnoreCase(action)) {
			//////////////////////
			// 生成预付订单
			//////////////////////
			LOG.d(LOG_TAG, "WeixinPay#unifiedorder.start");

			cbContext = callbackContext;

			final String body = "测试body";//args.getString(0);
			final String outTradeNo = "010101010101010101";//args.getString(1);
			final String totalFee = "1";//args.getString(2);
			final String spbillCreateIp = getClientIp();

			PayReq payreq = new PayReq();
			IWXAPI msgApi = WXAPIFactory.createWXAPI(cordova.getActivity(), null);

			msgApi.registerApp(APP_ID);

			Map<String, String> unifiedorder = null;
			
			try {
				unifiedorder = new GetPrepayIdTask().execute(body, outTradeNo, totalFee, spbillCreateIp).get();
				
				String returnCode = unifiedorder.get("return_code");

				if ("FAIL".equalsIgnoreCase(returnCode)) {
					throw new IllegalAccessException(unifiedorder.toString());
				}
			} catch (Exception e) {
				LOG.e(LOG_TAG, e.getMessage(), e);
				throw new JSONException(e.getMessage());
			}
			
			LOG.d(LOG_TAG, "WeixinPay#unifiedorder.end");

			//////////////////////
			// 生成签名
			//////////////////////
			LOG.d(LOG_TAG, "WeixinPay#gensign.start");

			payreq.appId = APP_ID;
			payreq.partnerId = MCH_ID;
			payreq.prepayId = unifiedorder.get("prepay_id");
			payreq.packageValue = "Sign=WXPay";
			payreq.nonceStr = genNonceStr();
			payreq.timeStamp = genTimeStamp();

			Map<String, String> signParams = new HashMap<String, String>();
			signParams.put("appid", payreq.appId);
			signParams.put("noncestr", payreq.nonceStr);
			signParams.put("package", payreq.packageValue);
			signParams.put("partnerid", payreq.partnerId);
			signParams.put("prepayid", payreq.prepayId);
			signParams.put("timestamp", payreq.timeStamp);

			String[] keys = signParams.keySet().toArray(new String[0]);
			Collections.sort(Arrays.asList(keys));

			StringBuilder signStr = new StringBuilder();

			for (String key : keys) {
				String value = signParams.get(key);

				signStr.append(key);
				signStr.append("=");
				signStr.append(value);
				signStr.append("&");
			}
			signStr.append("key=");
			signStr.append(API_KEY);

			String sign = getMessageDigest(signStr.toString());

			payreq.sign = sign;
			
			LOG.d(LOG_TAG, "WeixinPay#gensign.end");

			//////////////////////
			// 请求微信支付
			//////////////////////
			LOG.d(LOG_TAG, "WeixinPay#payment.start");

			msgApi.registerApp(APP_ID);
			msgApi.sendReq(payreq);
			
			LOG.d(LOG_TAG, "WeixinPay#payment.end");
			
			PluginResult pluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
			pluginResult.setKeepCallback(true);
			callbackContext.sendPluginResult(pluginResult);

			cordova.getThreadPool().execute(new Runnable() {
				public void run() {
				}
			});
			ret = true;
		}

		return ret;
	}

	private String genProductArgs(String body, String outTradeNo, String totalFee, String spbillCreateIp) {

		try {
			String nonceStr = genNonceStr();
			
			Map<String, String> params = new HashMap<String, String>();
			params.put("appid", APP_ID);
			params.put("body", body);
			params.put("mch_id", MCH_ID);
			params.put("nonce_str", nonceStr);
			params.put("notify_url", NOTIFY_URL);
			params.put("out_trade_no", outTradeNo);
			params.put("spbill_create_ip", spbillCreateIp);
			params.put("total_fee", totalFee);
			params.put("trade_type", "APP");

			String[] keys = params.keySet().toArray(new String[0]);
			Collections.sort(Arrays.asList(keys));
			
			StringBuilder signStr = new StringBuilder();
			StringBuilder sbXml = new StringBuilder();

			sbXml.append("<xml>");

			for (String key : keys) {
				String value = params.get(key);
				
				signStr.append(key);
				signStr.append("=");
				signStr.append(value);
				signStr.append("&");
				
				sbXml.append("<").append(key).append(">");
				sbXml.append(value);
				sbXml.append("</").append(key).append(">");
			}
			signStr.append("key=");
			signStr.append(API_KEY);

			String sign = getMessageDigest(signStr.toString());

			sbXml.append("<sign>");
			sbXml.append(sign);
			sbXml.append("</sign></xml>");

			return sbXml.toString();

		} catch (Exception e) {
			Log.e(LOG_TAG, "genProductArgs fail, ex = " + e.getMessage(), e);
		}
		return null;
	}

	private String genNonceStr() {
		return UUID.randomUUID().toString().replaceAll("-", "");
	}

	private Map<String, String> decodeXml(String content) {
		try {
			Map<String, String> xml = new HashMap<String, String>();
			XmlPullParser parser = Xml.newPullParser();
			parser.setInput(new StringReader(content));
			int event = parser.getEventType();
			while (event != XmlPullParser.END_DOCUMENT) {
				String nodeName = parser.getName();
				switch (event) {
				case XmlPullParser.START_DOCUMENT:
					break;
				case XmlPullParser.START_TAG:
					if ("xml".equals(nodeName) == false) {
						xml.put(nodeName, parser.nextText());
					}
					break;
				case XmlPullParser.END_TAG:
					break;
				}
				event = parser.next();
			}
			return xml;
		} catch (Exception e) {
			Log.e(LOG_TAG, e.getMessage(), e);
		}
		return null;
	}
	
	private String genTimeStamp() {
		return String.valueOf(System.currentTimeMillis() / 1000);
	}
	
	private String getClientIp() {
		String ip = null;
		// try {
			ip = "8.8.8.8";//InetAddress.getLocalHost().getHostAddress().toString();
		// } catch (UnknownHostException e) {
		// 	LOG.e(LOG_TAG, e.getMessage(), e);
		// }
		return ip;
	}
	
	public final static String getMessageDigest(String src) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
		try {
			MessageDigest mdTemp = MessageDigest.getInstance("MD5");
			mdTemp.update(src.getBytes());
			byte[] md = mdTemp.digest();
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return String.valueOf(str).toUpperCase();
		} catch (Exception e) {
			Log.e(LOG_TAG, e.getMessage(), e);
		}
		return null;
	}
	
	/** 异步请求处理：预付订单生成 */
	private class GetPrepayIdTask extends AsyncTask<String, Void, Map<String,String>> {

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected void onPostExecute(Map<String,String> result) {

		}

		@Override
		protected void onCancelled() {
			super.onCancelled();
		}

		@Override
		protected Map<String,String> doInBackground(String ... params) {
			String body = params[0];
			String outTradeNo = params[1];
			String totalFee = params[2];
			String spbillCreateIp = params[3];
			String entity = genProductArgs(body, outTradeNo, totalFee, spbillCreateIp);
			Log.e(LOG_TAG, entity);
			byte[] buf = Util.httpPost(UNIFIEDORDER_URL, entity);
			String content = new String(buf);
			Log.e(LOG_TAG, content);
			return decodeXml(content);
		}
	}
}
