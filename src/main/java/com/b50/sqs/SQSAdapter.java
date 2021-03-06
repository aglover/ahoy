package com.b50.sqs;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClient;
import com.amazonaws.services.sqs.model.*;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created with IntelliJ IDEA.
 * User: aglover
 * Date: 8/27/13
 * Time: 1:56 PM
 */
public class SQSAdapter {
    private final ExecutorService pool = Executors.newFixedThreadPool(10);
    private AmazonSQS sqs;
    private String queueURL;

    /**
     * Facilitates customizing AWS client connection, such as setting the region
     *
     * @param sqs
     * @param queueURL
     */
    public SQSAdapter(final AmazonSQS sqs, final String queueURL) {
        this.sqs = sqs;
        this.queueURL = queueURL;
    }

    /**
     *
     * @return instance of underlying AmazonSQS client
     */
    public AmazonSQS getAWSClient() {
        return this.sqs;
    }

    /**
     * Assumes east coast region! If you need to set an alternate region, see other
     * constructor as you can pass in an instance of AmazonSQS
     *
     * @param awsKey
     * @param awsSecret
     * @param queueName
     */
    public SQSAdapter(final String awsKey, final String awsSecret, final String queueName) {
        sqs = new AmazonSQSClient(new BasicAWSCredentials(awsKey, awsSecret));
        queueURL = sqs.createQueue(new CreateQueueRequest(queueName)).getQueueUrl();
    }

    public void receive(final MessageReceivedCallback callback) {
        receive(sqs, queueURL, callback);
    }

    public void send(final String message) {
        send(sqs, queueURL, message, null);
    }

    public void send(final String message, final MessageSentCallback callback) {
        send(sqs, queueURL, message, callback);
    }

    private void receive(final AmazonSQS sqs, final String queueURL, final MessageReceivedCallback callback) {
        pool.execute(new Runnable() {
            @Override
            public void run() {
                final List<Message> messages = sqs.receiveMessage(
                        new ReceiveMessageRequest(queueURL).withMaxNumberOfMessages(10).withWaitTimeSeconds(20)).getMessages();
                if (messages.size() > 0) {
                    for (final Message message : messages) {
                        callback.onReceive(message.getMessageId(), message.getBody());
                        sqs.deleteMessage(new DeleteMessageRequest(queueURL, message.getReceiptHandle()));
                    }
                }
            }
        });
    }

    private void send(final AmazonSQS sqs, final String queueURL, final String message, final MessageSentCallback callback) {
        pool.execute(new Runnable() {
            @Override
            public void run() {
                SendMessageResult res = sqs.sendMessage(new SendMessageRequest(queueURL, message));
                if (callback != null) {
                    callback.onSend(res.getMessageId());
                }
            }
        });
    }
}
