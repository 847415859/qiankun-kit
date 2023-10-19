package com.qiankun.common;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import okhttp3.*;

import java.io.IOException;
import java.util.Objects;

/**
 * @Description:  Http请求工具类，借助 okhttp3
 * @Date : 2023/01/14 15:20
 * @Auther : tiankun
 */
@Slf4j
public class HttpUtils {

    /**
     * POST
     * @param url
     * @param body
     * @return
     */
    public static boolean doPost(String url,String body){
        return doPost(url, body,200);
    }


    public static boolean doPost(String url,String body,int expectCode){
        try {
            OkHttpClient client = new OkHttpClient().newBuilder().build();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody requestBody = RequestBody.create(mediaType,body);
            Request request = new Request.Builder()
                .url(url)
                .method("POST", requestBody)
                .addHeader("Content-Type", "application/json")
                .build();
            log.info("请求体为：{}",JSON.toJSON(body));
            Response response = client.newCall(request).execute();
            log.info("Respnse: {}",response);
            return response.code() == expectCode;
        } catch (IOException e) {
            log.error("请求失败,url:{}, 失败原因：{}",url,e);
            // throw new RuntimeException("请求失败：{}",e);
            return false;
        }
    }

	public static Object doPostRes(String url,String body){
		try {
			OkHttpClient client = new OkHttpClient().newBuilder().build();
			MediaType mediaType = MediaType.parse("application/json");
			RequestBody requestBody = RequestBody.create(mediaType,body);
			Request request = new Request.Builder()
					.url(url)
					.method("POST", requestBody)
					.addHeader("Content-Type", "application/json")
					.build();
			log.info("请求体为：{}",JSON.toJSON(body));
			Response response = client.newCall(request).execute();
			log.info("Respnse: {}",response);
			return JSONObject.parseObject(response.body().string(), Object.class);
		} catch (IOException e) {
			log.error("请求失败,url:{}, 失败原因：{}",url,e);
			// throw new RuntimeException("请求失败：{}",e);
			return null;
		}
	}


    public static boolean doPostBusinessCode(String url,String body,String businessCode){
        try {
            OkHttpClient client = new OkHttpClient().newBuilder().build();
            MediaType mediaType = MediaType.parse("application/json");
            RequestBody requestBody = RequestBody.create(mediaType,body);
            Request request = new Request.Builder()
                    .url(url)
                    .method("POST", requestBody)
                    .addHeader("Content-Type", "application/json")
                    .build();
            log.info("请求体为：{}",JSON.toJSON(body));
            Response response = client.newCall(request).execute();
            log.info("Respnse: {}",response);
            ResponseBody responseBody = response.body();
            byte[] responseBytes = null;
            if (responseBody != null && (responseBytes = responseBody.bytes()).length > 0) {
                Object basicResult = JSON.parseObject(new String(responseBytes), Object.class);
                log.info("result :{}",JSON.toJSONString(basicResult));
                // TODO
                // return response.code() == 200 && Objects.equals(basicResult.getCode(),businessCode);
            }
            return response.code() == 200;
        } catch (IOException e) {
            log.error("请求失败,url:{}, 失败原因：{}",url,e);
            return false;
        }
    }


    public static boolean doGet(String url){
        return doGet(url,200);
    }


    public static boolean doGet(String url,int expectCode){
        try {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            Request request = new Request.Builder()
                    .url(url)
                    .build();
            Response response = client.newCall(request).execute();
            log.info("Respnse: {}",response);
            return response.code() == expectCode;
        } catch (IOException e) {
            log.error("请求失败,url:{}, 失败原因：{}",url,e);
            throw new RuntimeException("请求失败：{}",e);
        }
    }

	public static Object doGetRes(String url){
		try {
			OkHttpClient client = new OkHttpClient().newBuilder()
					.build();
			Request request = new Request.Builder()
					.url(url)
					.build();
			Response response = client.newCall(request).execute();
			log.info("Respnse: {}",response);
			return JSONObject.parseObject(response.body().string(), Object.class);
		} catch (IOException e) {
			log.error("请求失败,url:{}, 失败原因：{}",url,e);
			throw new RuntimeException("请求失败：{}",e);
		}
	}
}
