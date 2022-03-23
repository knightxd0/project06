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
public class LineBotController extends health implements eventToText, logic {
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
    // double[] info = new double[2];

    // -------------------------------------------------------------- ส่วน code
    // --------------------------------------------------------------

    // method
    @Autowired
    private LineMessagingClient lineMessagingClient;

    @EventMapping // รับข้อมูลจาก user
    public void handleTextMessage(MessageEvent<TextMessageContent> event) { // จัดการข้อความข้อความ //Event = เหตุการณ์
        log.info(event.toString());
        TextMessageContent message = event.getMessage();
        getLogic(message.getText());// จัดการ true false
        if (logic) {
            inputTextContent(event.getReplyToken(), event, message); // จัดการเนื้อหาข้อความที่ user ป้อน
        } else {
            handleTextContent(event.getReplyToken(), event, message); // จัดการเนื้อหาข้อความส่วน หัวข้อ
        }
    }

    // ส่วนเช็คหัวข้อ
    // Interfce eventToText
    public String checktext(String text) {
        String t = text;
        String messagech;
        if ((t.equals("bmi")) || (t.equals("Bmi")) || (t.equals("BMi")) || (t.equals("BMI")) || (t.equals("bMi")) // กลุ่มคำที่
                                                                                                                  // user
                                                                                                                  // อาจพิมพ์มา
                || (t.equals("bMI"))) {
            messagech = "BMI"; // จะ set คำที่ user ป้อนมาให้ตรงกับหัวข้อ BMI เพื่อง่ายต่อกาารใช้งานของ user

        } else if (t.equals("calorie") || t.equals("Calorie") || t.equals("calories") || t.equals("Calories")
                || t.equals("Cal") || t.equals("cal")) {
            messagech = "แคลลอรี่";

        } else if (t.equals("y") || t.equals("yes") || t.equals("ใช่") || t.equals("Yes") || t.equals("ใช่แล้ว")
                || t.equals("ยืนยัน")) {
            messagech = "Y";

        } else if (t.equals("n") || t.equals("no") || t.equals("ไม่ใช่") || t.equals("NO") || t.equals("ไม่ยืนยัน")) {
            messagech = "N";

        } else {
            messagech = t; // ถ้ากลุ่มคำไม่ตรงก็จะคืนค่ากลุ่มคำนั้นไปใช้ต่อ

        }

        return messagech;
    }

    // set logic ให้เป็น false เพื่อให้ไปใช้งานส่วนหัวข้อได้
    // Interfce logic
    public void getLogic(String text) {
        String t = text;
        t = checktext(t);
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
        if (t.equals("N")) {
            logic = false;

        }
        if (t.equals("Y")) {
            logic = false;
        }
    }

    // ฟังก์ชันคำนวนแคลลอรี่ต่อวัน
    // Abstract health
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

    // ฟังก์ชันคำนวนBMI
    // Abstract health
    public double getBMI(double weight, double height) {
        setHeight(height);
        double h = Math.pow(getHeight(), 2);
        double sum = weight / h;
        return Double.parseDouble(String.format("%.2f", sum));
    }

    // ฟังก์ชันเกณฑ์BMI
    // Abstract health
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

    // behavior
    // ส่วนหัวข้อ
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

            case "N": {
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
            case "Y": {
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
                                    getCalories() + " กิโลแคลลอรี่")));
                }
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

    // behavior
    // ส่วน input ค่าจาก user
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
            if (logic) {
                this.count++;
                this.reply(replyToken, new TextMessage("ส่วนสูงเท่าไรครับ cm."));

            } else {
                this.reply(replyToken, Arrays.asList(
                        new TextMessage("ข้อมูลไม่ถูกต้องครับ"),
                        new TextMessage("น้ำหนักเท่าไรครับ kg.")

                ));
                logic = true;
            }

        } else if (this.count == 1) {
            for (int i = 0; i < n.length; i++) {

                try {
                    double num = Double.parseDouble(n[i]);
                    this.height = num;

                } catch (NumberFormatException e) {
                    logic = false;
                }
            }

            if (logic) {
                this.count++;
                this.reply(replyToken, new TextMessage("อายุเท่าไรครับ"));

            } else {
                this.reply(replyToken, Arrays.asList(
                        new TextMessage("ข้อมูลไม่ถูกต้องครับ"),
                        new TextMessage("ส่วนสูงเท่าไรครับ cm.")

                ));
                logic = true;
            }

        } else if (this.count == 2) {
            for (int i = 0; i < n.length; i++) {

                try {
                    int num = Integer.parseInt(n[i]);
                    this.age = num;

                } catch (NumberFormatException e) {
                    logic = false;
                }
            }

            if (logic) {
                this.count++;
                this.reply(replyToken, new TextMessage("เพศอะไรครับ"));

            } else {
                this.reply(replyToken, Arrays.asList(
                        new TextMessage("ข้อมูลไม่ถูกต้องครับ"),
                        new TextMessage("อายุเท่าไรครับ")

                ));
                logic = true;
            }

        } else if (this.count == 3) {

            if (text.equals("ชาย")) {
                this.gender = text;

            } else if (text.equals("หญิง")) {
                this.gender = text;

            } else {
                this.reply(replyToken, Arrays.asList(
                        new TextMessage("ข้อมูลไม่ถูกต้องครับ"),
                        new TextMessage("เพศอะไรครับ")

                ));
            }

            this.reply(replyToken, Arrays.asList(
                    new TextMessage("เพศ: " + this.gender),
                    new TextMessage("น้ำหนัก: " + this.weight + "\nส่วนสูง: " + this.height + "\nอายุ: " + this.age
                            + "\nเพศ: " + this.gender),
                    new TextMessage("ยืนยันข้อมูล\n(y/n)")));
        }

    }

    // ------------------------------------------------------------------------------------------------------------------------------------------

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
