package erik.best.practice.hello;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;

public class CommandThatFailsSilently extends HystrixCommand<String> {

    private final boolean throwException;

    public CommandThatFailsSilently(boolean throwException){
        super(HystrixCommandGroupKey.Factory.asKey("ExampleGroup"));
        this.throwException = throwException;
    }

    @Override
    protected String run() throws Exception {
        if (throwException) {
            throw new RuntimeException("failure from CommandThatFailsFast");
        } else {
            return "success";
        }
    }

    @Override
    protected String getFallback() {
        //返回null，或者空map、空list，都属于 fails silently
        return null;
    }
}
