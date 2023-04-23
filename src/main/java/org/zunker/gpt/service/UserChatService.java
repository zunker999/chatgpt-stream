package org.zunker.gpt.service;

import org.zunker.gpt.enmus.MessageType;
import org.zunker.gpt.listener.CompletedCallBack;
import org.zunker.gpt.dto.text.Message;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * @author : zunker
 * @Description : gpt
 * @date : 2023年04月23日 10:21
 * ----------------- ----------------- -----------------
 */
public interface UserChatService extends CompletedCallBack {

    /**
     * 发送消息
     *
     * @param type
     * @param content
     * @param sessionId
     */
    Flux<String> send(MessageType type, String content, String sessionId);

    /**
     * 消息历史
     *
     * @param sessionId
     * @return
     */
    List<Message> getHistory(String sessionId);

    /**
     * 语音转文字
     * @param filePath
     * @return
     */
    Flux<String> audioToTextTranscriptions(String filePath);


}
