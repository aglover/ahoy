package com.b50.sqs;

/**
 * Created with IntelliJ IDEA.
 * User: aglover
 * Date: 8/27/13
 * Time: 2:04 PM
 */
public interface MessageReceivedCallback {
    public void onReceive(String messageId, String message);
}
