package com.b50.sqs;

/**
 * Created with IntelliJ IDEA.
 * User: aglover
 * Date: 8/29/13
 * Time: 12:25 PM
 */
public interface MessageSentCallback {
    public void onSend(String messageId);
}
