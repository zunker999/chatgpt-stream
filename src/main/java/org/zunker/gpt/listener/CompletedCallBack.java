package org.zunker.gpt.listener;

import org.zunker.gpt.dto.text.Message;

/**
 * @author : zunker
 * @Description : gpt
 * @date : 2023年04月23日 10:21
 * ----------------- ----------------- -----------------
 */
public interface CompletedCallBack {

    /**
     * 完成回掉
     *
     * @param questions
     * @param sessionId
     * @param response
     */
    void completed(Message questions, String sessionId, String response);

    void fail(String sessionId);

}
