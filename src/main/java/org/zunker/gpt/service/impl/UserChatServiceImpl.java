package org.zunker.gpt.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.zunker.gpt.enmus.MessageType;
import org.zunker.gpt.enmus.UserType;
import org.zunker.gpt.listener.OpenAiSubscriber;
import org.zunker.gpt.service.UserChatService;
import org.zunker.gpt.dto.text.Message;
import org.zunker.gpt.common.OpenAiWebClient;
import org.zunker.gpt.util.UserSessionUtil;
import org.zunker.gpt.dto.translate.Transcriptions;
import org.zunker.gpt.dto.whisper.Whisper;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author : zunker
 * @Description : gpt
 * @date : 2023年04月23日 10:21
 * ----------------- ----------------- -----------------
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserChatServiceImpl implements UserChatService {

    private final UserSessionUtil userSessionUtil;

    private final OpenAiWebClient openAiWebClient;
    private static final float TOKEN_CONVERSION_RATE = 0.7f;
    private static final Integer MAX_TOKEN = 4096;
    private static final List<String> IMAGE_COMMAND_PREFIX = Arrays.asList("画", "找");

    @Override
    public Flux<String> send(MessageType type, String content, String sessionId) {
        if (IMAGE_COMMAND_PREFIX.contains(String.valueOf(content.charAt(0)))) {
            Message userMessage = new Message(MessageType.IMAGE, UserType.USER, content);
            String finalContent = content;
            return Flux.create(emitter -> {
                OpenAiSubscriber subscriber = new OpenAiSubscriber(emitter, sessionId, this, userMessage);
                Flux<String> openAiResponse = openAiWebClient.getImage(sessionId, finalContent);
                openAiResponse.subscribe(subscriber);
                emitter.onDispose(subscriber);
            });
        }

        Message userMessage = new Message(MessageType.TEXT, UserType.USER, content);
        int currentToken = (int) (content.length() / TOKEN_CONVERSION_RATE);
        List<Message> history = userSessionUtil.getHistory(sessionId, MessageType.TEXT, (int) ((MAX_TOKEN / TOKEN_CONVERSION_RATE) - currentToken));
        log.info("history:{}", history);
        String historyDialogue = history.stream().map(e -> String.format(e.getUserType().getCode(), e.getMessage())).collect(Collectors.joining());
        String prompt = StringUtils.hasLength(historyDialogue) ? String.format("%sQ:%s\nA: ", historyDialogue, content) : content;

        log.info("prompt:{}", prompt);
        return Flux.create(emitter -> {
            OpenAiSubscriber subscriber = new OpenAiSubscriber(emitter, sessionId, this, userMessage);
            Flux<String> openAiResponse = openAiWebClient.getChatResponse(sessionId, prompt, null, null, null);
            openAiResponse.subscribe(subscriber);
            emitter.onDispose(subscriber);
        });
    }

    @Override
    public List<Message> getHistory(String sessionId) {
        return userSessionUtil.getHistory(sessionId, null, Integer.MAX_VALUE);
    }

    @Override
    public void completed(Message questions, String sessionId, String response) {
        userSessionUtil.addMessage(sessionId, questions);
        userSessionUtil.addMessage(sessionId, new Message(questions.getMessageType(), UserType.BOT, response));
    }

    @Override
    public void fail(String sessionId) {
        log.error("completedCallBack error,sessionId={}", sessionId);
    }

    @Override
    public Flux<String> audioToTextTranscriptions(String filePath) {
        Transcriptions transcriptions = Transcriptions.builder()
                .model(Whisper.Model.WHISPER_1.getName())
                .build();
        Message userMessage = new Message(MessageType.AUDIO, UserType.USER);
        return Flux.create(emitter -> {
            OpenAiSubscriber subscriber = new OpenAiSubscriber(emitter, "", this, userMessage);
            Flux<String> openAiResponse = openAiWebClient.audioToTextTranscriptions(filePath, transcriptions);
            openAiResponse.subscribe(subscriber);
            emitter.onDispose(subscriber);
        });
    }


}
