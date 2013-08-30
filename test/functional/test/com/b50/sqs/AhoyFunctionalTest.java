package test.com.b50.sqs;

import com.b50.sqs.MessageReceivedCallback;
import com.b50.sqs.MessageSentCallback;
import com.b50.sqs.SQSAdapter;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created with IntelliJ IDEA.
 * User: aglover
 * Date: 8/30/13
 * Time: 10:56 AM
 */
public class AhoyFunctionalTest {

    @Test
    public void testSendAndReceiveAMsg() throws Exception {

        for (String value : new ArrayList<String>(Arrays.asList(new String[]{"key", "secret", "queue"}))) {
            assertNotNull("value: " + value + " cannot be null or functional test will not work!", System.getProperty(value));
        }

        SQSAdapter ahoy = new SQSAdapter(System.getProperty("key"), System.getProperty("secret"), System.getProperty("queue"));
        assertNotNull(ahoy);

        final String origMessage = "this is a test message";
        final boolean[] wasSent = {false};
        ahoy.send(origMessage, new MessageSentCallback() {
            @Override
            public void onSend(String messageId) {
                wasSent[0] = true;
                assertNotNull("message ID from AWS was null!", messageId);
            }
        });

        Thread.sleep(2000);
        assertEquals("callback for sent wasn't invoked", true, wasSent[0]);
        Thread.sleep(20000); //30 sec


        final boolean[] wasReceived = {false};
        ahoy.receive(new MessageReceivedCallback() {
            @Override
            public void onReceive(String messageId, String message) {
                wasReceived[0] = true;
                assertNotNull("message id was null", messageId);
                assertEquals("message wasn't " + origMessage, origMessage, message);
            }
        });

        Thread.sleep(30000);
        assertEquals("callback for received wasn't invoked", true, wasReceived[0]);
    }
}
