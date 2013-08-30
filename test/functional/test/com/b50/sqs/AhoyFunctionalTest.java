package test.com.b50.sqs;

import com.b50.sqs.SQSAdapter;
import org.junit.Test;

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
        String awsKey = System.getProperty("key");
        String awsSecret = System.getProperty("secret");
        String awsQueue = System.getProperty("queue");
        System.out.println("using key: " + awsKey + " and secret: " + awsSecret + " and queue name: " + awsQueue);
        SQSAdapter ahoy = new SQSAdapter(awsKey, awsSecret, awsQueue);
        assertNotNull(ahoy);
    }
}
