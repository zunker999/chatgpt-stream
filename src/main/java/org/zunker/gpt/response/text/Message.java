package org.zunker.gpt.response.text;

import lombok.Data;

/**
 * @author : zunker
 * @Description : gpt
 * @date : 2023年04月23日 10:21
 * ----------------- ----------------- -----------------
 */
@Data
public class Message {

    private String role;

    private String content;

}
