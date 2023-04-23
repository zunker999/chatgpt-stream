package org.zunker.gpt.response.text;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zunker.gpt.enmus.MessageType;

/**
 * @author : zunker
 * @Description : gpt
 * @date : 2023年04月23日 10:21
 * ----------------- ----------------- -----------------
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MessageResponse {

    private MessageType messageType;

    private String message;

    private Boolean end;

}
