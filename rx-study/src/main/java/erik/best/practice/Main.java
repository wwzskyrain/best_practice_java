package erik.best.practice;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Observable;
import org.checkerframework.dataflow.qual.TerminatesExecution;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        hello("王卫振");
    }


    public static void hello(String... args) {
        Flowable.fromArray(args).subscribe(s -> System.out.println("Hello " + s + "!"));
    }

    @Test
    public void create_observable_from_an_existing_data_structure() {
        Observable<String> o = Observable.fromArray("a", "b", "c");


        List<Integer> list = Arrays.asList(5, 6, 7, 8);
        Observable<Integer> o2 = Observable.fromIterable(list);

        Observable<String> o3 = Observable.just("one object");
    }
}