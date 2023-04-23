package org.zunker.gpt.common;

/**
 * @author : zunker
 * @Description : gpt
 * @date : 2023年04月23日 10:21
 * ----------------- ----------------- -----------------
 */
public interface ApiConstant {

    String HOST = "https://api.openai.com";

    /**
     * 对话接口
     */
    String CHAT_API = HOST + "/v1/chat/completions";

    /**
     * 内容检测
     */
    String CONTENT_AUDIT = HOST + "/v1/moderations";

    /**
     * 生成图片
     */
    String IMAGE_API = HOST + "/v1/images/generations";

    /**
     * 语音转文字
     */
    String TRANSCRIPTIONS_API = HOST + "/v1/audio/transcriptions";


}
