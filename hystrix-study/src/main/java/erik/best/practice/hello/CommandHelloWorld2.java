package erik.best.practice.hello;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixObservableCommand;
import rx.Observable;
import rx.Subscriber;

public class CommandHelloWorld2 extends HystrixObservableCommand<String> {

    private final String name;

    public CommandHelloWorld2( String name) {
        super(HystrixCommandGroupKey.Factory.asKey("ExampleGroup"));
        this.name = name;
    }

    public CommandHelloWorld2(Setter setter, String name) {
        super(setter);
        this.name = name;
    }

    @Override
    protected Observable<String> construct() {
        return Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> observer) {
                try {
                    if(!observer.isUnsubscribed()){
                        observer.onNext("Hello");
                        observer.onNext(name + "!");
                        observer.onCompleted();
                    }
                }catch (Exception e){
                    observer.onError(e);
                }
            }
        });
    }
}
