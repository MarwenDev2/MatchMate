package entities;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;

public class sendSMS {

    public static final String ACCOUNT_SID = "AC3650f569e4740b82dbe2aaa4c50fcd60";
    public static final String AUTH_TOKEN = "3c7e621314ee3d649b09cf39b87b7903";

    public static void sms(String text, String number) {
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        Message message = Message.creator(
                new com.twilio.type.PhoneNumber(number),
                new com.twilio.type.PhoneNumber("+15108764320"),
                text).create();

        System.out.println(message.getSid());
    }

}
