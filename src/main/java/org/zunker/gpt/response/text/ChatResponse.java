package org.zunker.gpt.response.text;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author : zunker
 * @Description : gpt
 * @date : 2023年04月23日 10:21
 * ----------------- ----------------- -----------------
 */
@Data
public class ChatResponse implements Serializable {

    private String id;

    private String object;

    private long created;

    private String model;

    private TokenUsage tokenUsage;

    private List<ChatChoice> choices;


    @Data
    public static class ChatChoice implements Serializable {

        private long index;

        @JsonProperty("delta")
        private Message delta;

        @JsonProperty("message")
        private Message message;

        @JsonProperty("finish_reason")
        private String finishReason;

    }

}
