package erik.best.practice.hello;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;

public class CommandUsingRequestCache extends HystrixCommand<Boolean> {

    private int value;

    public CommandUsingRequestCache(int value) {
        super(HystrixCommandGroupKey.Factory.asKey("ExampleGroupKey"));
        this.value = value;
    }

    @Override
    protected Boolean run() throws Exception {
        return value == 0 || value % 2 == 0;
    }

    @Override
    protected String getCacheKey() {
        return String.valueOf(value);
    }
}
