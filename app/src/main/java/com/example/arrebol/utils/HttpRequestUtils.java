package com.example.arrebol.utils;

import android.util.Log;
import android.widget.Toast;

import com.example.arrebol.entity.SearchResult;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class HttpRequestUtils {

    /**
     * 搜索小说、漫画、影视
     * @param url api地址
     * @param params 参数
     * @return Call对象
     */
    public static Call getSearchCall(String url, Map<String, String> params){
        //创建OkHttpClient对象
        OkHttpClient okHttpClient = new OkHttpClient();

        //拼接参数
        String concatUrl = appendParams(url, params);

        //创建request
        Request.Builder builder = new Request.Builder();
        Request request = builder.url(concatUrl)
                .get()
                .build();

        //返回一个Call对象
        //Log.d("zcc", "getSearchCall: " + concatUrl);
        return okHttpClient.newCall(request);
    }

    /**
     * 拼接get的url参数
     * @param url 请求网址
     * @param params 参数
     * @return 拼接后的结果
     */
    private static String appendParams(String url, Map<String, String> params){
        StringBuilder sb = new StringBuilder();
        sb.append(url).append("?");
        if(params != null &&  !params.isEmpty()){
            for(String key : params.keySet()){
                sb.append(key).append("=").append(params.get(key)).append("&");
            }
        }
        sb = sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    /**
     * 解析小说搜索的请求结果
     * @param content JSON请求结果
     * @param searchResults 解析后的结果
     * @return 0：成功获取 1：服务器异常 2：未找到相关内容
     */
    public static int parseNovelJson(String content, ArrayList<SearchResult> searchResults){
        //清空数据
        searchResults.clear();

        JSONObject object;
        try {
            object = new JSONObject(content);
            //服务器状态
            int code = object.optInt("code", -1);
            //查询结果
            String message = object.optString("message", "");
            Log.d("zcc", "parseNovelJson: " + code + " " + message);
            if(code == 1){
                //服务器错误
                return 1;
            }
            if(!message.contains("成功")){
                //未找到内容
                return 2;
            }

            JSONArray jsonArray = object.getJSONArray("list");

            for (int i = 0; i < jsonArray.length(); i++) {
                // JSON数组里面的具体-JSON对象
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if("...".equals(jsonObject.optString("introduce", "..."))
                        || jsonObject.optString("introduce", "...").length() < 5){
                    continue;
                }
                SearchResult result = new SearchResult();
                result.setAuthor(jsonObject.optString("author", "unknown"));
                result.setTag(jsonObject.optString("tag", "unknown"));
                result.setIntroduce(jsonObject.optString("introduce", "..."));
                result.setCover(jsonObject.optString("cover", ""));
                result.setName(jsonObject.optString("name", ""));
                result.setUrl(jsonObject.optString("url", ""));

                searchResults.add(result);
            }


        } catch (JSONException e) {
            e.printStackTrace();
            //Log.d("zcc", "error:" + e.getMessage());
        }
        return 0;
    }

}
