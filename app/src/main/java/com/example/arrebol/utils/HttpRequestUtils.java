package com.example.arrebol.utils;

import android.util.Log;
import android.widget.Toast;

import com.example.arrebol.entity.Chapter;
import com.example.arrebol.entity.SearchResult;
import com.example.arrebol.entity.Top;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class HttpRequestUtils {

    /**
     * 获取请求call
     * @param url api地址
     * @param params 参数
     * @return Call对象
     */
    public static Call getCall(String url, Map<String, String> params){
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


    public static void getUrls(final String url, Map<String, String> params){
        Call call = getCall(url, params);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                EventBus.getDefault().post("null");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                JSONObject object;

                //查询结果的url
                ArrayList<String> urls = new ArrayList<>();

                try {
                    object = new JSONObject(response.body().string());
                    //服务器状态
                    int code = object.optInt("code", -1);
                    //查询结果
                    String message = object.optString("message", "");
                    //Log.d("zcc", "parseNovelJson: " + code + " " + message);
                    if(code == 1 || !message.contains("成功")){
                        EventBus.getDefault().post(urls);
                    }else{
                        JSONArray jsonArray = object.getJSONArray("list");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            // JSON数组里面的具体-JSON对象
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            if(jsonObject.optString("url", "").length() != 0){
                                //添加到urls
                                urls.add(jsonObject.optString("url", ""));
                                //Log.d("rainm", jsonObject.optString("url", ""));
                            }
                        }
                        EventBus.getDefault().post(urls);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
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
     * 解析漫画热门搜索的JSON数据
     * @param content JSON请求结果
     * @param tops 解析后的结果
     * @return 0：成功获取 1：服务器异常 2：未找到相关内容
     */
    public static int parseHotCartoonJson(String content, ArrayList<Top> tops){

        JSONObject object;
        try {
            object = new JSONObject(content);
            //服务器状态
            int code = object.optInt("code", -1);
            //查询结果
            String message = object.optString("message", "");
            //Log.d("zcc", "parseNovelJson: " + code + " " + message);
            if(code == 1){
                //服务器错误
                return 1;
            }
            if(!message.contains("成功")){
                //未找到内容
                return 2;
            }

            JSONArray jsonArray = object.getJSONArray("list");

            //加载前10条
            for (int i = 0; i < jsonArray.length(); i++) {
                // JSON数组里面的具体-JSON对象
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if("...".equals(jsonObject.optString("name", "..."))){
                    continue;
                }
                Top top = new Top();
                top.setName(jsonObject.optString("name", ""));
                top.setRank((tops.size() + 1) + "");
                top.setTopStatus(true);
                tops.add(top);

                if(tops.size() == 10) break;
            }


        } catch (JSONException e) {
            e.printStackTrace();
            //Log.d("zcc", "error:" + e.getMessage());
        }
        return 0;
    }


    /**
     * 解析小说、动漫、影视搜索的请求结果
     * @param content JSON请求结果
     * @param searchResults 解析后的结果
     * @return 0：成功获取 1：服务器异常 2：未找到相关内容
     */
    public static int parseSearchUrlsJson(String content, String url, ArrayList<SearchResult> searchResults){

        JSONObject object;
        try {
            object = new JSONObject(content);
            //服务器状态
            int code = object.optInt("code", -1);
            //查询结果
            String message = object.optString("message", "");

            SearchResult result;
            //Log.d("zcc", "parseNovelJson: " + code + " " + message);
            if(code == 1){
                //服务器错误
                result = new SearchResult();
                result.setDisable(true);
                searchResults.add(result);
                return 1;
            }
            if(!message.contains("成功")){
                //未找到内容
                result = new SearchResult();
                result.setDisable(true);
                searchResults.add(result);
                return 2;
            }

            String data = object.optString("data", "");
            if(data.length() != 0){
                JSONObject dataObject = new JSONObject(data);

                result = new SearchResult();

                result.setAuthor(dataObject.optString("author", "unknown"));

                if("film".equals(dataObject.optString("genre", "film"))){
                    //说明是tag
                    result.setTag(dataObject.optString("tag", "unknown"));
                }else{
                    result.setTag(dataObject.optString("genre", "unknown"));
                }
                result.setIntroduce(dataObject.optString("introduce", "..."));
                result.setCover(dataObject.optString("cover", ""));
                result.setName(dataObject.optString("name", ""));
                result.setTime(dataObject.optString("time", "unknown"));
                result.setUrl(url);

//                Log.d("rainm", result.getUrl() + "\n"
//                        + result.getName() + "\n"
//                        + result.getTag() + "\n"
//                        + result.getAuthor()+ "\n"
//                        + result.getIntroduce() + "\n"
//                        + result.getCover() + "\n"
//                        + result.getTime() + "\n");

                searchResults.add(result);
            }

            //Log.d("rainm", "parseCartoonJson: " + searchResults.size());
        } catch (JSONException e) {
            e.printStackTrace();
            //Log.d("zcc", "error:" + e.getMessage());
        }
        return 0;
    }

    /**
     * 解析小说和漫画的详细数据
     * @param content
     * @param chapters
     * @return
     */
    public static int parseDetailUrlsJson(String content, ArrayList<Chapter> chapters){
        Log.d("zcc", "parseDetailUrlsJson: " + content);
        JSONObject object;
        try {
            object = new JSONObject(content);
            //服务器状态
            int code = object.optInt("code", -1);
            //查询结果
            String message = object.optString("message", "");

            SearchResult result;
            //Log.d("zcc", "parseNovelJson: " + code + " " + message);
            if(code == 1){
                //服务器错误
                return 1;
            }
            if(!message.contains("成功")){
                //未找到内容
                return 2;
            }
            JSONArray jsonArray = object.getJSONArray("list");

            Log.d("zcc", jsonArray.length()+"");
            for (int i = 0; i < jsonArray.length(); i++) {
                // JSON数组里面的具体-JSON对象
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Chapter chapter = new Chapter();
                chapter.setCurrentChapter(jsonObject.optString("num", "null"));
                chapter.setUrl(jsonObject.optString("url", "null"));
                chapters.add(chapter);


            }

            //Log.d("rainm", "parseCartoonJson: " + searchResults.size());
        } catch (JSONException e) {
            e.printStackTrace();
            //Log.d("zcc", "error:" + e.getMessage());
        }
        return 0;
    }

}
