# Ahoy! Asynchronous SQS

Ahoy! is an asynchronous SQS adapter for [AWS's Java SQS library](http://aws.amazon.com/sdkforjava/). While the SDK provided by AWS does include a `AmazonSQSAsyncClient`, this implementation uses [Futures](http://nurkiewicz.blogspot.com/2013/02/javautilconcurrentfuture-basics.html). I think Java's Futures are slick, however, they put the onus on the caller to poll for an end state. That is, you don't _fire-and-forget_ -- you fire and poll. Then forget. 

Ahoy! is designed to be fire-and-forget. Naturally, you don't necessarily want to forget receiving a message; thus, Ahoy! leverages _callbacks_. Callbacks in Java can be implemented via anonymous classes like so:

```
SQSAdapter sqs = new SQSAdapter("key", "secret", "some queue");
sqs.receive(new MessageReceivedCallback() {
  public void onReceive(String messageId, String message) {
  	//do something w/the message!
  }
});
``` 

You can attach a callback on an SQS send as well:

```
SQSAdapter sqs = new SQSAdapter("key", "secret", "some queue");
sqs.send("Message", new MessageSentCallback() {
  public void onSend(String messageId) {
  	//if you need to do something w/the messageId from AWS...
  }
});
```

Notice something else? Yeah, that's right, you don't have to deal with the myriad AWS SDK classes required to receive or send a message. No dealing with `SendMessageResult`, `Message`, `SendMessageRequest`, `DeleteMessageRequest`, `ReceiveMessageRequest`, and the list goes on.

## Some more details

This is important so read up. 
  * `receive` will delete the message off of the SQS queue
  * `receive` will listen for 20 seconds and grab up to 10 messages *and* the `onReceive` callback will be invoked for _each_ message