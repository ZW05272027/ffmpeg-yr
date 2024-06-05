package com.yr.ffmpegyr.controller.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

/**
 * 返回值封装
 *
 * @projectName: ffmpeg-yr
 * @className: AiResult
 * @author: Mby
 * @date: 2024/6/4 11:55
 * @version: 1.0
 */
@Data
public class AiResult {
    private Source source;
    private Results results;
    @Data
    public static class Source{
        private String type;
        private List<String> file;
        private String url;
    }
    @Data
    public static class Results{
        private Attention attention;
        private List<String> emotion;
        private List<String> sitting_pose;
    }

    @Data
    public static class Attention{
        private BigDecimal attentive;

    }

    public Source getSource() {
        return source;
    }

    public void setSource(Source source) {
        this.source = source;
    }

    public Results getResults() {
        return results;
    }

    public void setResults(Results results) {
        this.results = results;
    }
}
