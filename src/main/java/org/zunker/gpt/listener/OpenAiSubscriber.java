package org.zunker.gpt.listener;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.zunker.gpt.enmus.MessageType;
import org.zunker.gpt.dto.text.Message;
import org.zunker.gpt.response.text.MessageResponse;
import org.zunker.gpt.response.image.ImageResponse;
import org.zunker.gpt.response.text.ChatResponse;
import org.zunker.gpt.response.audio.AudioResponse;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.Disposable;
import reactor.core.publisher.FluxSink;

import java.util.stream.Collectors;

/**
 * @author : zunker
 * @Description : gpt
 * @date : 2023年04月23日 10:21
 * ----------------- ----------------- -----------------
 */
@Slf4j
public class OpenAiSubscriber implements Subscriber<String>, Disposable {
    private final FluxSink<String> emitter;
    private Subscription subscription;
    private final String sessionId;
    private final StringBuilder sb;
    private final Message questions;
    private final MessageType messageType;
    private final CompletedCallBack completedCallBack;

    public OpenAiSubscriber(FluxSink<String> emitter, String sessionId, CompletedCallBack completedCallBack, Message questions) {
        this.emitter = emitter;
        this.sessionId = sessionId;
        this.completedCallBack = completedCallBack;
        this.questions = questions;
        this.sb = new StringBuilder();
        this.messageType = questions.getMessageType();
    }

    @Override
    public void onSubscribe(Subscription subscription) {
        this.subscription = subscription;
        subscription.request(1);
    }

    @Override
    public void onNext(String data) {
        log.info("OpenAI返回数据：{}", data);
        if (messageType == MessageType.IMAGE) {
            subscription.request(1);
            sb.append(data);
            return;
        }
        if ("[DONE]".equals(data)) {
            log.info("OpenAI返回数据结束了");
            subscription.request(1);
            emitter.next(JSON.toJSONString(new MessageResponse(MessageType.TEXT, "", true)));
            completedCallBack.completed(questions, sessionId, sb.toString());
            emitter.complete();
        } else if (MessageType.AUDIO == messageType) {
            log.info("OpenAI返回数据结束了");
            AudioResponse openAiResponse = JSON.parseObject(data, AudioResponse.class);
            String content = openAiResponse.getText();
            emitter.next(JSON.toJSONString(new MessageResponse(MessageType.AUDIO, content, true)));
            completedCallBack.completed(questions, sessionId, sb.toString());
            emitter.complete();
        } else {
            ChatResponse chatResponse = JSON.parseObject(data, ChatResponse.class);
            String content = chatResponse.getChoices().get(0).getDelta().getContent();
            content = content == null ? "" : content;
            emitter.next(JSON.toJSONString(new MessageResponse(MessageType.TEXT, content, null)));
            sb.append(content);
            subscription.request(1);
        }

    }

    @Override
    public void onError(Throwable t) {
        log.error("OpenAI返回数据异常：{}", t.getMessage());
        emitter.error(t);
        completedCallBack.fail(sessionId);
    }

    @Override
    public void onComplete() {
        log.info("OpenAI返回数据完成");
        if (messageType == MessageType.IMAGE) {
            ImageResponse imgResp = JSON.parseObject(sb.toString(), ImageResponse.class);
            String url = imgResp.getData().stream().map(ImageResponse.DataRes::getUrl).collect(Collectors.joining(","));
            emitter.next(JSON.toJSONString(new MessageResponse(MessageType.IMAGE, url, true)));
        }
        emitter.complete();
    }

    @Override
    public void dispose() {
        log.warn("OpenAI返回数据关闭");
        emitter.complete();
    }
}