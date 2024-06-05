package com.yr.ffmpegyr.util;

import cn.hutool.json.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.lang.reflect.Type;
import java.net.http.HttpResponse;
import java.util.List;

/**
 * 文件上传
 *
 * @projectName: ffmpeg-yr
 * @className: UploadUtil
 * @author: Mby
 * @date: 2024/6/3 17:34
 * @version: 1.0
 */
@Slf4j
public class UploadUtil {
    public static <T> List<T> analyse(String url,String dir, List<String> files, Class<T> clazz) {
        RestTemplate restTemplate = new RestTemplate();

        // 创建请求头
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);

        // 创建请求体
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

        for (String file : files){
            body.add("frames", new FileSystemResource(new File(dir+"/"+file)));
        }
        body.add("model", "attention");
        // 创建 HttpEntity
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

        // 发送请求
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);

        ///

//        Unirest.setTimeouts(0, 0);
//        HttpResponse<String> response = Unirest.post("https://detect.91jzx.cn/camera_vision_api/camera_vision_v2/predict")
//                .header("accept", "application/json")
//                .field("model", "attention")
//                .field("file", new File("/D:/worker_project/ai-camera-vision/data/t1.mp4/frame_0001.png"))
//                .field("file", new File("/D:/worker_project/ai-camera-vision/data/t1.mp4/frame_0002.png"))
//                .asString();


        if (response.getStatusCode() == HttpStatus.OK) {
            String responseBody = response.getBody();
            log.info("responseBody:{}",responseBody);
            return JSONArray.parseArray(responseBody, clazz);
        }
        throw new RuntimeException("File upload failed: " + response.getStatusCode());
    }
}
