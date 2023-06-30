package vn.trendgpt.core.pojo.chatgpt;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ChatGPTRequest {
    private final int max_tokens;
    private final String model;
    private List<Message> messages;

    public ChatGPTRequest(String prompt, String model, String role) {
        this.max_tokens = 100;
        this.model = model;
        this.messages = new ArrayList<>();
        Message message = new Message();
        message.setRole(role);
        message.setContent(prompt);
        this.messages.add(message);
    }
}
