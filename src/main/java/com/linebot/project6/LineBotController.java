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
    // field
    private int count;
    private double weight;
    private double height;
    private int age;
    private String gender;
    private String standard;
    private double sum;
    private int type;

    boolean logic = false;
    double[] info = new double[2];

    @Autowired
    private LineMessagingClient lineMessagingClient;

    @EventMapping //
    public void handleTextMessage(MessageEvent<TextMessageContent> event) { // จัดการข้อความข้อความ //Event = เหตุการณ์
        log.info(event.toString());
        TextMessageContent message = event.getMessage();
        getLogic(message.getText());
        if (logic == true) {
            inputTextContent(event.getReplyToken(), event, message); // จัดการเนื้อหาข้อความ
        } else {
            handleTextContent(event.getReplyToken(), event, message); // จัดการเนื้อหาข้อความ
        }
    }

    public String checktext(String text) {
        String t = text;
        String messagech;
        if ((t.equals("bmi")) || (t.equals("Bmi")) || (t.equals("BMi")) || (t.equals("BMI")) || (t.equals("bMi"))
                || (t.equals("bMI"))) {
            messagech = "BMI";
        } else if (t.equals("calorie") || t.equals("Calories") || t.equals("Cal")) {
            messagech = "แคลลอรี่";
        } else {
            messagech = t;
        }

        return messagech;
    }

    public void getLogic(String text) {
        String t = text;
        if (t.equals("ยกเลิก")) {
            logic = false;
        }
        if (t.equals("ต้องการแก้ไข")) {
            logic = false;
        }
        if (t.equals("ต้องการ")) {
            logic = false;
        }
        if (t.equals("ไม่ต้องการ")) {
            logic = false;
        }
        if (t.equals("ไม่")) {
            logic = false;
        }
        if (t.equals("ไม่ต้อง")) {
            logic = false;
        }
    }

    public double getCalories() {
        String g = this.gender;
        double calories;
        if (g.equals("ชาย")) {
            this.sum = this.weight * 31.0;
        } else if (g.equals("หญิง")) {
            this.sum = this.weight * 27.0;
        }
        calories = this.sum;
        return calories;
    }

    public double getBMI(double weight, double height) {

        double h = ((height / 100.00) * (height / 100.00));
        double sum = weight / h;
        return Double.parseDouble(String.format("%.2f", sum));
    }

    public String getStandard(double bmi) {
        double b = bmi;
        String standard;
        if (b < 18.50) {
            this.standard = "น้ำหนักต่ำกว่าเกณฑ์";
        } else if ((b > 18.59) && (b < 22.90)) {
            this.standard = "สุขภาพดี";
        } else if ((b > 23.0) && (b < 24.90)) {
            this.standard = "ท้วม";
        } else if ((b > 25.0) && (b < 29.90)) {
            this.standard = "อ้วน";
        } else if (b > 30.0) {
            this.standard = "อ้วนมาก";
        }
        standard = this.standard;
        return standard;
    }

    private void handleTextContent(String replyToken, Event event, TextMessageContent content) { // เนื้อหา
        String text = content.getText();

        log.info("Got text message from %s : %s", replyToken, text);

        switch (checktext(text)) {
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
                this.reply(replyToken, new TextMessage("น้ำหนักเท่าไรครับ kg."));
                logic = true;
                this.type = 0;
                break;
            }
            case "แคลลอรี่": {
                this.reply(replyToken, new TextMessage("น้ำหนักเท่าไรครับ kg."));
                logic = true;
                this.type = 1;
                break;
            }

            case "ต้องการแก้ไข": {
                this.reply(replyToken, new TextMessage("ช่วยบอกน้ำหนัก kg."));
                logic = true;
                this.count = 0;
                break;
            }
            case "ต้องการ": {
                this.reply(replyToken, new TextMessage("ช่วยบอกน้ำหนัก kg."));
                logic = true;
                this.count = 0;
                break;
            }
            case "ไม่ต้องการ": {
                if (this.type == 0) {
                    logic = false;
                    this.reply(replyToken, Arrays.asList(
                            new TextMessage("กำลังประมวลผลครับ"),
                            new TextMessage("BMI: " +
                                    getBMI(this.weight, this.height) + "\nคุณอยู่ในเกณฑ์: "
                                    + getStandard(getBMI(this.weight, this.height)))));
                } else if (this.type == 1) {
                    logic = false;
                    this.reply(replyToken, Arrays.asList(
                            new TextMessage("กำลังประมวลผลครับ"),
                            new TextMessage("แคลลอรี่ต่อวัน: " +
                                    getCalories())));
                }
                // this.weight = 0;
                this.count = 0;

                break;
            }
            case "ไม่": {

                if (this.type == 0) {
                    logic = false;
                    this.reply(replyToken, Arrays.asList(
                            new TextMessage("กำลังประมวลผลครับ"),
                            new TextMessage("BMI: " +
                                    getBMI(this.weight, this.height) + "\nคุณอยู่ในเกณฑ์: "
                                    + getStandard(getBMI(this.weight, this.height)))));
                } else if (this.type == 1) {
                    logic = false;
                    this.reply(replyToken, Arrays.asList(
                            new TextMessage("กำลังประมวลผลครับ"),
                            new TextMessage("แคลลอรี่ต่อวัน: " +
                                    getCalories())));
                }
                // this.weight = 0;
                this.count = 0;

                break;
            }
            case "ไม่ต้อง": {
                if (this.type == 0) {
                    logic = false;
                    this.reply(replyToken, Arrays.asList(
                            new TextMessage("กำลังประมวลผลครับ"),
                            new TextMessage("BMI: " +
                                    getBMI(this.weight, this.height) + "\nคุณอยู่ในเกณฑ์: "
                                    + getStandard(getBMI(this.weight, this.height)))));
                } else if (this.type == 1) {
                    logic = false;
                    this.reply(replyToken, Arrays.asList(
                            new TextMessage("กำลังประมวลผลครับ"),
                            new TextMessage("แคลลอรี่ต่อวัน: " +
                                    getCalories() + "กิโลแคลอรี่")));
                }
                // this.weight = 0;
                this.count = 0;

                break;
            }
            case "โมโม่": {
                this.reply(replyToken, new TextMessage(
                        "เริ่มใช้งานง่ายๆ ตามนี้เลย\nหา BMI\n1.พิมพ์ BMI\n2.กรอกข้อมูล\n3.ยืนยันข้อมูล\nหา Calories ต่อวัน\n1.พิมพ์ แคลลอรี่\n2.กรอกข้อมูล\n3.ยืนยันข้อมูล"));
                break;
            }
            default: {
                this.reply(replyToken, Arrays.asList(
                        new TextMessage("โมโม่ไม่เข้าใจครับ"),
                        new TextMessage(
                                "เริ่มใช้งานง่ายๆ ตามนี้เลย\nหา BMI\n1.พิมพ์ BMI\n2.กรอกข้อมูล\n3.ยืนยันข้อมูล\nหา Calories ต่อวัน\n1.พิมพ์ แคลลอรี่\n2.กรอกข้อมูล\n3.ยืนยันข้อมูล")));
            }

        }

    }

    private void inputTextContent(String replyToken, Event event, TextMessageContent content) {
        String text = content.getText();

        log.info("Got text message from %s : %s", replyToken, text);
        String[] n = text.split(" ");

        if (this.count == 0) {
            for (int i = 0; i < n.length; i++) {

                try {
                    double num = Double.parseDouble(n[i]);
                    this.weight = num;

                } catch (NumberFormatException e) {
                    logic = false;
                }
            }
            this.count++;
            this.reply(replyToken, new TextMessage("ส่วนสูงเท่าไรครับ cm."));

        } else if (this.count == 1) {
            for (int i = 0; i < n.length; i++) {

                try {
                    double num = Double.parseDouble(n[i]);
                    this.height = num;

                } catch (NumberFormatException e) {
                    logic = false;
                }
            }
            this.count++;
            this.reply(replyToken, new TextMessage("อายุเท่าไรครับ"));

        } else if (this.count == 2) {
            for (int i = 0; i < n.length; i++) {

                try {
                    int num = Integer.parseInt(n[i]);
                    this.age = num;

                } catch (NumberFormatException e) {
                    logic = false;
                }
            }
            this.count++;
            this.reply(replyToken, new TextMessage("เพศอะไรครับ"));

        } else if (this.count == 3) {

            if (text.equals("ชาย")) {
                this.gender = text;

            } else if (text.equals("หญิง")) {
                this.gender = text;

            } else {
                this.reply(replyToken, new TextMessage("ข้อมูลไม่ถูกต้องครับ\nเพศอะไรครับ"));
            }

            this.reply(replyToken, Arrays.asList(
                    new TextMessage("เพศ: " + this.gender),
                    new TextMessage("น้ำหนัก: " + this.weight + "\nส่วนสูง: " + this.height + "\nอายุ: " + this.age
                            + "\nเพศ: " + this.gender),
                    new TextMessage("ต้องการแก้ไขหรือไม่ครับ\n(ต้องการ/ไม่ต้องการ)")));
        }

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
