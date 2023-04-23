package org.zunker.gpt.controller;

import com.alibaba.fastjson.JSONObject;
import org.zunker.gpt.enmus.MessageType;
import org.zunker.gpt.service.UserChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.zunker.gpt.dto.text.Message;
import org.zunker.gpt.common.OpenAiWebClient;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author : zunker
 * @Description : gpt
 * @date : 2023年04月23日 10:21
 * ----------------- ----------------- -----------------
 */
@Slf4j
@RestController
@RequestMapping({"/openai"})
@RequiredArgsConstructor
public class OpenAiController {

    private final UserChatService userChatService;

    private final OpenAiWebClient openAiWebClient;

    /**
     * 发信息
     *
     * @param prompt 提示词
     * @param user   用户
     * @return
     */
    @GetMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> streamCompletions(String prompt, String user) {
        Assert.hasLength(user, "user不能为空");
        Assert.hasLength(prompt, "prompt不能为空");
        return userChatService.send(MessageType.TEXT, prompt, user);
    }

    /**
     * 上传音频文件进行asr解析
     *
     * @return
     */
    @PostMapping(value = "/audio/translate/upload", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Mono<String> translateUpload(@RequestPart("file") FilePart filePart, @RequestPart("user") String user) {
        Map<String,String> resMap = new HashMap<>(1);
        try {
            //生成临时的空文件，只是一个占位符
            Path tempFile = Files.createTempFile(user, filePart.filename());
            //同步写入文件
            filePart.transferTo(tempFile.toFile());
            //file内容写入完毕，开始调接口翻译文本
            log.info("file content write finish");
            String filePath = tempFile.toAbsolutePath().toString();
            log.info("file path={}", filePath);
            resMap.put("path",filePath);
        } catch (IOException e) {
            log.error("translateUpload failed", e);
        }
        return Mono.just(JSONObject.toJSONString(resMap));
    }

    /**
     * 监听上传的音频文件翻译后的返回值
     *
     * @param filePath
     * @return
     */
    @GetMapping(value = "/audio/translate", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> translate(String filePath, String user) {
        log.info("audio translate,filePath={},user={}", filePath, user);
        Flux<String> stringFlux = userChatService.audioToTextTranscriptions(filePath);
        //接收到转换完的数据之后，才能对文件和结果做一些事
        stringFlux.subscribe(res -> {
            log.info("file translate res:{}", res);
            //转换完成之后，才可以删除文件
            log.info("file delete");
            File file = new File(filePath);
            if (file.delete()) {
                System.out.println("文件已删除！");
            } else {
                System.out.println("文件删除失败。");
            }
        });
        return stringFlux;
    }

    /**
     * 内容检测
     *
     * @param content
     * @return
     */
    @GetMapping("/checkContent")
    public Mono<ServerResponse> checkContent(@RequestParam String content) {
        log.info("req:{}", content);
        return openAiWebClient.checkContent(content);
    }

    /**
     * 获取历史记录
     *
     * @param user
     */
    @GetMapping(value = "/history")
    public Mono<List<Message>> history(String user) {
        Assert.hasLength(user, "user不能为空");
        return Mono.just(userChatService.getHistory(user));
    }


}
