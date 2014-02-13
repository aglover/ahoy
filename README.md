# Ahoy! Asynchronous SQS

Ahoy! is an asynchronous SQS adapter for [AWS's Java SQS library](http://aws.amazon.com/sdkforjava/). While the SDK provided by AWS does include a `AmazonSQSAsyncClient`, this implementation uses [Futures](http://nurkiewicz.blogspot.com/2013/02/javautilconcurrentfuture-basics.html). I think Java's Futures are slick, however, they put the onus on the caller to poll for an end state. That is, you don't _fire-and-forget_ -- you fire and poll. Then forget. 

Ahoy! is designed to be fire-and-forget. Naturally, you don't necessarily want to forget receiving a message; thus, Ahoy! leverages _callbacks_. Callbacks in Java can be implemented via anonymous classes like so:

```java
SQSAdapter sqs = new SQSAdapter("key", "secret", "some queue");
sqs.receive(new MessageReceivedCallback() {
  public void onReceive(String messageId, String message) {
  	//do something w/the message!
  }
});
``` 

In the code above, `onReceive` will be invoked once a message is popped off of an SQS queue named "some queue" -- note, the callback receives the message body as well as the messages ID (all provided via the SDK `Message` object). 

You can attach a callback on a `send` as well:

```java
SQSAdapter sqs = new SQSAdapter("key", "secret", "some queue");
sqs.send("Message", new MessageSentCallback() {
  public void onSend(String messageId) {
  	//if you need to do something w/the messageId from AWS...
  }
});
```

In this case, you can get a receipt in the form of the AWS ID attached to the SQS message. `send` is overridden; thus, you do not have to pass in an implementation of `MessageSentCallback`.

Notice something else? Yeah, that's right, you don't have to deal with the myriad AWS SDK classes required to receive or send a message. No dealing with `SendMessageResult`, `Message`, `SendMessageRequest`, `DeleteMessageRequest`, `ReceiveMessageRequest`, and the list goes on.

## Maven, Gradle, etc

To build with Maven, add the dependencies listed below to your `pom.xml` file:

```xml
<dependency>
  <groupId>com.github.aglover</groupId>
  <artifactId>ahoy</artifactId>
  <version>1.0.1</version>
</dependency>
```

Alternatively, if you want to use Gradle, add this to your `build.gradle` file:

```groovy
compile 'com.github.aglover:ahoy:1.0.1'
```

For other build tools like SBT, etc, see Ahoy's [mvnrepository.com](http://mvnrepository.com/artifact/com.github.aglover/ahoy) page. 

## Some more details

This is important so read up. 
  * `receive` will delete the message off of the SQS queue
  * `receive` will listen for 20 seconds and grab up to 10 messages and the `onReceive` callback will be invoked for _each_ message
  * reread that last point, please

## Helpful resources

Check out these handy-dandy resources:
  * [Java development 2.0: Cloud-based messaging with Amazon SQS](http://www.ibm.com/developerworks/library/j-javadev2-17/)
  * [Amazon Simple Queue Service (Amazon SQS)](http://aws.amazon.com/sqs/)
  * [AWS SDK for Java](http://aws.amazon.com/sdkforjava/)
