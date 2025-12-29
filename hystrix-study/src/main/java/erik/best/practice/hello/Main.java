package erik.best.practice.hello;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixEventType;
import com.netflix.hystrix.HystrixRequestLog;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import org.junit.Assert;
import org.junit.Test;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.functions.Action1;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.junit.Assert.*;

public class Main {

    @Test
    public void testSynchronous() {
        assertEquals("Hello World!", new CommandHelloWorld("World").execute());
        assertEquals("Hello Bob!", new CommandHelloWorld("Bob").execute());
    }

    @Test
    public void testAsynchronous() throws ExecutionException, InterruptedException {
        assertEquals("Hello World!", new CommandHelloWorld("World").queue().get());
        assertEquals("Hello Bob!", new CommandHelloWorld("Bob").queue().get());
    }

    @Test
    public void testAsynchronous2() throws Exception {

        Future<String> fWorld = new CommandHelloWorld("World").queue();
        Future<String> fBob = new CommandHelloWorld("Bob").queue();

        assertEquals("Hello World!", fWorld.get());
        assertEquals("Hello Bob!", fBob.get());
    }


    @Test
    public void testObserve() {
        Observable<String> fWorld = new CommandHelloWorld("World").observe();
        Observable<String> fBob = new CommandHelloWorld("Bob").observe();

        // blocking
        assertEquals("Hello World!", fWorld.toBlocking().single());
        assertEquals("Hello Bob!", fBob.toBlocking().single());

        fWorld.subscribe(new Observer<String>() {
            @Override
            public void onCompleted() {
                //nothing need to do here.
            }

            @Override
            public void onError(Throwable throwable) {
                throwable.printStackTrace();
            }

            @Override
            public void onNext(String s) {
                System.out.println("onNext: " + s);
            }
        });

        // non-blocking
        // - also verbose anonymous inner-class
        // - ignore errors and onCompleted signal
        fBob.subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                System.out.println("onNext: " + s);
            }
        });
    }

    @Test
    public void test_fallback_synchronous() {
        assertEquals("Hello Failure World!", new CommandHelloFailure("World").execute());
        assertEquals("Hello Failure Bob!", new CommandHelloFailure("Bob").execute());
    }


    @Test
    public void testWithoutCacheHits() {
        HystrixRequestContext context = HystrixRequestContext.initializeContext();
        try {
            assertTrue(new CommandUsingRequestCache(2).execute());
            assertFalse(new CommandUsingRequestCache(1).execute());
            assertTrue(new CommandUsingRequestCache(0).execute());
            assertTrue(new CommandUsingRequestCache(58672).execute());
        } finally {
            context.shutdown();
        }
    }


    @Test
    public void testWithCacheHits() {
        HystrixRequestContext context = HystrixRequestContext.initializeContext();
        try {
            CommandUsingRequestCache command2a = new CommandUsingRequestCache(2);
            CommandUsingRequestCache command2b = new CommandUsingRequestCache(2);

            assertTrue(command2a.execute());
            // this is the first time we've executed this command with
            // the value of "2" so it should not be from cache
            assertFalse(command2a.isResponseFromCache());

            assertTrue(command2b.execute());
            // this is the second time we've executed this command with
            // the same value so it should return from cache
            assertTrue(command2b.isResponseFromCache());
        } finally {
            context.shutdown();
        }

        // start a new request context
        context = HystrixRequestContext.initializeContext();
        try {
            CommandUsingRequestCache command3b = new CommandUsingRequestCache(2);
            assertTrue(command3b.execute());
            // this is a new request context so this
            // should not come from cache
            assertFalse(command3b.isResponseFromCache());
        } finally {
            context.shutdown();
        }
    }


    @Test
    public void testCollapser() throws Exception {
        HystrixRequestContext context = HystrixRequestContext.initializeContext();
        try {
            Future<String> f1 = new CommandCollapserGetValueForKey(1).queue();
            Future<String> f2 = new CommandCollapserGetValueForKey(2).queue();
            Future<String> f3 = new CommandCollapserGetValueForKey(3).queue();
            Future<String> f4 = new CommandCollapserGetValueForKey(4).queue();

            assertEquals("ValueForKey: 1", f1.get());
            assertEquals("ValueForKey: 2", f2.get());
            assertEquals("ValueForKey: 3", f3.get());
            assertEquals("ValueForKey: 4", f4.get());

            // assert that the batch command 'GetValueForKey' was in fact
            // executed and that it executed only once
            assertEquals(1, HystrixRequestLog.getCurrentRequest().getExecutedCommands().size());
            HystrixCommand<?> command = HystrixRequestLog.getCurrentRequest()
                    .getExecutedCommands().toArray(new HystrixCommand<?>[1])[0];
            // assert the command is the one we're expecting
            assertEquals("GetValueForKey", command.getCommandKey().name());
            // confirm that it was a COLLAPSED command execution
            assertTrue(command.getExecutionEvents().contains(HystrixEventType.COLLAPSED));
            // and that it was successful
            assertTrue(command.getExecutionEvents().contains(HystrixEventType.SUCCESS));
        } finally {
            context.shutdown();
        }
    }

    @Test
    public void test_success_for_fail_fast_command() {
        CommandThatFailFast commandThatFailFast = new CommandThatFailFast(false);
        assertEquals("success", commandThatFailFast.execute());
    }

    @Test
    public void test_false_for_fail_fast_command() {
        CommandThatFailFast commandThatFailFast = new CommandThatFailFast(true);
        try {
            commandThatFailFast.execute();
        } catch (HystrixRuntimeException e) {
            assertEquals("failure from CommandThatFailsFast", e.getCause().getMessage());
            e.printStackTrace();
        }
    }

    @Test
    public void test_success_for_command_that_fail_silently() {
        assertEquals("success", new CommandThatFailsSilently(false).execute());
    }

    @Test
    public void test_fail_for_command_that_fail_silently() {
        try {
            assertEquals(null, new CommandThatFailsSilently(true).execute());
        } catch (HystrixRuntimeException e) {
            fail("we should not get an exception as we fail silently with a fallback");
        }
    }



    @Test
    public void test_stubbed_fallback() {
        CommandThatFailWithStubbedFallback command = new CommandThatFailWithStubbedFallback(1234, "ca");
        CommandThatFailWithStubbedFallback.UserAccount account = command.execute();
        assertTrue(command.isFailedExecution());
        assertTrue(command.isResponseFromFallback());
        assertEquals(1234, account.customerId);
        assertEquals("ca", account.countryCode);
        assertEquals(true, account.isFeatureXPermitted);
        assertEquals(true, account.isFeatureYPermitted);
        assertEquals(false, account.isFeatureZPermitted);
    }


}
