package com.yr.ffmpegyr.util;


import com.google.gson.annotations.SerializedName;

import java.util.List;
/**
 *
 * @projectName: ffmpeg
 * @className: MediaInfo
 * @author: Mby
 * @date: 2024/6/3 10:51
 * @version: 1.0
 */
public class MediaInfo {
    public static class Format {
        @SerializedName("bit_rate")
        private String bitRate;
        public String getBitRate() {
            return bitRate;
        }
        public void setBitRate(String bitRate) {
            this.bitRate = bitRate;
        }
    }

    public static class Stream {
        @SerializedName("index")
        private int index;

        @SerializedName("codec_name")
        private String codecName;

        @SerializedName("codec_long_name")
        private String codecLongame;

        @SerializedName("profile")
        private String profile;
    }

    @SerializedName("streams")
    private List<Stream> streams;

    @SerializedName("format")
    private Format format;

    public List<Stream> getStreams() {
        return streams;
    }

    public void setStreams(List<Stream> streams) {
        this.streams = streams;
    }

    public Format getFormat() {
        return format;
    }

    public void setFormat(Format format) {
        this.format = format;
    }
}
