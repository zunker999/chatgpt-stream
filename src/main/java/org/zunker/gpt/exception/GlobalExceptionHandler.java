package org.zunker.gpt.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author : zunker
 * @Description : gpt
 * @date : 2023年04月23日 10:21
 * ----------------- ----------------- -----------------
 */
@ControllerAdvice
@ResponseBody
@Slf4j
public class GlobalExceptionHandler {


    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<String> exceptionHandler(Exception e) {
        log.error("exceptionHandler", e);
        return ResponseEntity.status(500).body(e.getMessage());
    }
}
