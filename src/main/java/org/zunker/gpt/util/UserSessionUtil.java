package org.zunker.gpt.util;

import org.zunker.gpt.enmus.MessageType;
import org.zunker.gpt.dto.text.Message;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author : zunker
 * @Description : gpt
 * @date : 2023年04月23日 10:21
 * ----------------- ----------------- -----------------
 */
@Component
public class UserSessionUtil {

    private static final Map<String, List<Message>> MESSAGE_HISTORY = new ConcurrentHashMap<>();

    /**
     * 消息列表
     *
     * @param sessionId
     * @param message
     */
    public void addMessage(String sessionId, Message message) {
        List<Message> messageList = MESSAGE_HISTORY.getOrDefault(sessionId, new ArrayList<>());
        messageList.add(message);
        MESSAGE_HISTORY.put(sessionId,messageList);
    }

    /**
     * 消息历史
     *
     * @param sessionId
     * @param messageType
     * @param maxTokens
     * @return
     */
    public List<Message> getHistory(String sessionId, MessageType messageType, Integer maxTokens) {
        List<Message> history = MESSAGE_HISTORY.getOrDefault(sessionId, new ArrayList<>());
        List<Message> result = new ArrayList<>();
        int count = 0;
        for (int i = history.size() - 1; i >= 0; i--) {
            Message message = history.get(i);
            if (messageType == null) {
                result.add(message);
                continue;
            }
            if (message.getMessageType() == messageType) {
                count += message.getMessage().length();
                if (count >= maxTokens) {
                    break;
                }
                result.add(message);
            }
        }
        Collections.reverse(result);
        return result;
    }


}
