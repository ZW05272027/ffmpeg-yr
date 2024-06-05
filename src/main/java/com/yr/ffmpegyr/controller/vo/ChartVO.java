package com.yr.ffmpegyr.controller.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * 类功能描述
 *
 * @projectName: ffmpeg-yr
 * @className: ChartVO
 * @author: Mby
 * @date: 2024/6/4 10:03
 * @version: 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChartVO {
    private List<BigDecimal> attentions;
}
