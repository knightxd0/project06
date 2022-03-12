package com.linebot.project6;

import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.event.Event;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.Message;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.model.response.BotApiResponse;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;

@Slf4j
@LineMessageHandler
public class LineBotController {
    boolean logic = false;
    double[] info = new double[2];

    @Autowired
    private LineMessagingClient lineMessagingClient;

    @EventMapping //
    public void handleTextMessage(MessageEvent<TextMessageContent> event) { // จัดการข้อความข้อความ //Event = เหตุการณ์
        log.info(event.toString());
        TextMessageContent message = event.getMessage();
        String t = message.getText();
        if (t.equals("ยกเลิก")) {
            logic = false;
        }
        if (t.equals("ต้องการแก้ไข")) {
            logic = false;
        }
        if (t.equals("ไม่ต้องการ")) {
            logic = false;
        }
        if (logic == true) {
            String cal = "Calculator";
            calTextContent(event.getReplyToken(), event, message, cal); // จัดการเนื้อหาข้อความ
        } else {
            handleTextContent(event.getReplyToken(), event, message); // จัดการเนื้อหาข้อความ
        }
    }

    private void handleTextContent(String replyToken, Event event, TextMessageContent content) { // เนื้อหา
        String text = content.getText();

        log.info("Got text message from %s : %s", replyToken, text);

        switch (text) {
            case "Profile": {
                String userId = event.getSource().getUserId();
                if (userId != null) {
                    lineMessagingClient.getProfile(userId)
                            .whenComplete((profile, throwable) -> {
                                if (throwable != null) {
                                    this.replyText(replyToken, throwable.getMessage());
                                    return;
                                }
                                this.reply(replyToken, Arrays.asList(
                                        new TextMessage("Display name: " +
                                                profile.getDisplayName()),
                                        new TextMessage("Status message: " +
                                                profile.getStatusMessage()),
                                        new TextMessage("User ID: " +
                                                profile.getUserId())));
                            });
                }
                break;
            }
            case "BMI": {
                this.reply(replyToken, new TextMessage("ช่วยบอกน้ำหนัก kg. และส่วนสูง cm. หน่อย "));
                logic = true;
                break;
            }
            case "bmi": {
                this.reply(replyToken, new TextMessage("ช่วยบอกน้ำหนัก kg. และส่วนสูง cm. หน่อย "));
                logic = true;
                break;
            }
            case "Bmi": {
                this.reply(replyToken, new TextMessage("ช่วยบอกน้ำหนัก kg. และส่วนสูง cm. หน่อย "));
                logic = true;
                break;
            }
            case "คำนวณBMI": {
                this.reply(replyToken, new TextMessage("ช่วยบอกน้ำหนัก kg. และส่วนสูง cm. หน่อย "));
                logic = true;
                break;
            }
            case "คำนวณbmi": {
                this.reply(replyToken, new TextMessage("ช่วยบอกน้ำหนัก kg. และส่วนสูง cm. หน่อย "));
                logic = true;
                break;
            }
            case "ต้องการแก้ไข": {
                this.reply(replyToken, new TextMessage("ช่วยบอกน้ำหนัก kg. และส่วนสูง cm. หน่อย "));
                logic = true;
                break;
            }
            case "ไม่ต้องการ": {
                this.reply(replyToken, new TextMessage("กำลังประมวลผลงั้บ"));
                double weight = info[0], height = info[1];

                break;
            }

        }

    }

    private void calTextContent(String replyToken, Event event, TextMessageContent content, String cal) {
        String message = cal;
        boolean logic = true;
        String text = content.getText();
        log.info("Got text message from %s : %s", replyToken, text);
        String[] n = text.split(" ");

        info[0] = 0;
        info[1] = 0;

        for (int i = 0; i < n.length; i++) {

            try {
                double num = Double.parseDouble(n[i]);

            } catch (NumberFormatException e) {
                logic = false;
            }

            if (logic) {
                info[i] += Double.parseDouble(n[i]);
            }

        }

        if (info[0] > info[1]) {
            double temp = 0;
            temp += info[0];
            info[0] = info[1];
            info[1] = temp;
        }

        switch (message) {
            case "Calculator": {
                this.reply(replyToken, Arrays.asList(
                        new TextMessage("น้ำหนัก: " +
                                info[0]),
                        new TextMessage("ส่วนสูง: " +
                                info[1]),
                        new TextMessage("ต้องการแก้ไขมั้ยงั้บ")));

                break;
            }
        }

        logic = false;

    }

    private void replyText(@NonNull String replyToken, @NonNull String message) {
        if (replyToken.isEmpty()) {
            throw new IllegalArgumentException("replyToken is not empty");
        }

        if (message.length() > 1000) {
            message = message.substring(0, 1000 - 2) + "...";
        }
        this.reply(replyToken, new TextMessage(message));
    }

    private void reply(@NonNull String replyToken, @NonNull Message message) {
        reply(replyToken, Collections.singletonList(message));
    }

    private void reply(@NonNull String replyToken, @NonNull List<Message> messages) {
        try {
            BotApiResponse response = lineMessagingClient.replyMessage(
                    new ReplyMessage(replyToken, messages)).get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

}
