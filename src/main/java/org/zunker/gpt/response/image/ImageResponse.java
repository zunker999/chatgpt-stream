package org.zunker.gpt.response.image;

import lombok.Data;

import java.util.List;

/**
 * @author : zunker
 * @Description : gpt
 * @date : 2023年04月23日 10:21
 * ----------------- ----------------- -----------------
 */
@Data
public class ImageResponse {

    private Long created;

    private List<DataRes> data;

    @Data
    public static class DataRes {
        private String url;
    }

}
