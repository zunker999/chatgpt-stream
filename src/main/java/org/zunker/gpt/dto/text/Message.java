package org.zunker.gpt.dto.text;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.zunker.gpt.enmus.MessageType;
import org.zunker.gpt.enmus.UserType;

import java.util.Date;

/**
 * @author : zunker
 * @Description : gpt
 * @date : 2023年04月23日 10:21
 * ----------------- ----------------- -----------------
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Message {

    private MessageType messageType;

    private UserType userType;

    private String message;

    private Date date;

    public Message(MessageType messageType, UserType userType, String message) {
        this.messageType = messageType;
        this.userType = userType;
        this.message = message;
        this.date = new Date();
    }

    public Message(MessageType messageType, UserType userType) {
        this.messageType = messageType;
        this.userType = userType;
        this.date = new Date();
    }
}
