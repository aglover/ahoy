package test.com.b50.sqs;

import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.*;
import com.b50.sqs.MessageReceivedCallback;
import com.b50.sqs.MessageSentCallback;
import com.b50.sqs.SQSAdapter;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created with IntelliJ IDEA.
 * User: aglover
 * Date: 8/27/13
 * Time: 1:54 PM
 */
public class SQSAdapterTest {

    @Test
    public void testAdapterReceive() throws Exception {

        AmazonSQSClient mockClient = mock(AmazonSQSClient.class);
        CreateQueueResult mockQueueResult = mock(CreateQueueResult.class);
        when(mockClient.createQueue(any(CreateQueueRequest.class))).thenReturn(mockQueueResult);
        when(mockQueueResult.getQueueUrl()).thenReturn("URL");
        ReceiveMessageResult receiveMessageResult = mock(ReceiveMessageResult.class);
        when(mockClient.receiveMessage(any(ReceiveMessageRequest.class))).thenReturn(receiveMessageResult);

        Message simpleMessage = new Message();
        simpleMessage.setBody("TESTING 1,2,3");
        simpleMessage.setMessageId("1");
        List<Message> mockedMsgs = new LinkedList<Message>();
        mockedMsgs.add(simpleMessage);
        when(receiveMessageResult.getMessages()).thenReturn(mockedMsgs);

        SQSAdapter sqs = new SQSAdapter(mockClient, "some queue");
        assertNotNull("sqs object was null", sqs);

        final boolean[] wasReceived = {false};
        sqs.receive(new MessageReceivedCallback() {
            @Override
            public void onReceive(String id, String message) {
                wasReceived[0] = true;
                assertEquals("id should be 1", "1", id);
                assertEquals("message should be TESTING 1,2,3", "TESTING 1,2,3", message);
            }
        }
        );

        Thread.sleep(2000);
        assertEquals("wasReceived was not true", true, wasReceived[0]);

    }

    @Test
    public void testAdapterSend() throws Exception {
        AmazonSQSClient mockClient = mock(AmazonSQSClient.class);
        SendMessageResult mockResult = mock(SendMessageResult.class);
        when(mockClient.sendMessage(any(SendMessageRequest.class))).thenReturn(mockResult);
        when(mockResult.getMessageId()).thenReturn("TEST");

        SQSAdapter sqs = new SQSAdapter(mockClient, "some queue");
        assertNotNull("sqs object was null", sqs);

        final boolean[] wasReceived = {false};
        sqs.send("Message", new MessageSentCallback() {
            @Override
            public void onSend(String messageId) {
                wasReceived[0] = true;
                assertEquals("should be TEST", "TEST", messageId);
            }
        });

        Thread.sleep(2000);
        assertEquals("wasReceived was not true", true, wasReceived[0]);
    }
}
