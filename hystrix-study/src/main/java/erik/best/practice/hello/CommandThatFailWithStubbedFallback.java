package erik.best.practice.hello;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;

public class CommandThatFailWithStubbedFallback extends HystrixCommand<CommandThatFailWithStubbedFallback.UserAccount> {

    private final int customerId;
    private final String countryCodeFromGeoLookup; //用作stub


    public CommandThatFailWithStubbedFallback(int customerId, String countryCodeFromGeoLookup) {
        super(HystrixCommandGroupKey.Factory.asKey("ExampleGroup"));
        this.customerId = customerId;
        this.countryCodeFromGeoLookup = countryCodeFromGeoLookup;
    }

    @Override
    protected UserAccount run() throws Exception {
        // fetch UserAccount from remote service
        //        return UserAccountClient.getAccount(customerId);
        throw new RuntimeException("forcing failure for example");
    }

    @Override
    protected UserAccount getFallback() {
        /**
         * Return stubbed fallback with some static defaults, placeholders,
         * and an injected value 'countryCodeFromGeoLookup' that we'll use
         * instead of what we would have retrieved from the remote service.
         */
        return new UserAccount(customerId, "Unknown Name",
                countryCodeFromGeoLookup, true, true, false);
    }

    public static class UserAccount {
        public final int customerId;
        public final String name;
        public final String countryCode;
        public final boolean isFeatureXPermitted;
        public final boolean isFeatureYPermitted;
        public final boolean isFeatureZPermitted;

        UserAccount(int customerId, String name, String countryCode,
                    boolean isFeatureXPermitted,
                    boolean isFeatureYPermitted,
                    boolean isFeatureZPermitted) {
            this.customerId = customerId;
            this.name = name;
            this.countryCode = countryCode;
            this.isFeatureXPermitted = isFeatureXPermitted;
            this.isFeatureYPermitted = isFeatureYPermitted;
            this.isFeatureZPermitted = isFeatureZPermitted;
        }
    }
}
