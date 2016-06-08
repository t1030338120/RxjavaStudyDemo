package com.app.rxjava.java_test;

import com.app.rxjava.dagger2.Container;
import com.app.rxjava.dagger2.DaggerFruitComponent;

import org.junit.Test;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;

/**
 * 描述：
 * 作者：tyc
 */
public class MainClassTest {

    @Test
    public void testInsertSort() throws Exception {
        System.out.println("=================");
    }

    @Test
    public void testDagger2() throws Exception {

        Container container = new Container();
        DaggerFruitComponent.create().inject(container);
        System.out.println(container.mApple.getName());
        System.out.println(container.mBanana.getName());


    }


    @Test
    public void testRxJava() {

        Observable.just("123")
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        System.out.println("====" + s);
                    }
                });
    }


    @Test
    public void testRxJava02() {
        Observable<String> observable = Observable.just("123")
                .doOnNext(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        System.out.println("doOnNext " + s);
                    }
                });

        observable.subscribe(s -> System.out.println(s));


    }


    String data = null;

    @Test
    public void testRetryWhen() {
//        retryWhen和retry类似，区别是，retryWhen将onError中的Throwable传递给一个函数，
//        这个函数产生另一个Observable，retryWhen观察它的结果再决定是不是要重新订阅原始的Observable。
//        如果这个是发射数据的Observable（正常Observable），它就重新订阅；如果这个是onError通知类型Observable，它就将这个通知传递给观察者然后终止。


        Observable.just("aaaa")
                .flatMap(new Func1<String, Observable<String>>() {
                    @Override
                    public Observable<String> call(String s) {

                        if (data == null) {
                            return Observable.error(new NullPointerException("data is null!"));
                        }

                        return Observable.just("before " + s);
                    }
                })
                .retryWhen(new Func1<Observable<? extends Throwable>, Observable<?>>() {
                    @Override
                    public Observable<?> call(Observable<? extends Throwable> observable) {


                        return observable.flatMap(new Func1<Throwable, Observable<?>>() {
                            @Override
                            public Observable<?> call(Throwable throwable) {
                                if (IllegalArgumentException.class.isInstance(throwable) || NullPointerException.class.isInstance(throwable)) {
                                    return Observable.just("")
                                            .doOnNext(new Action1<String>() {
                                                @Override
                                                public void call(String s) {
                                                    data = "123";
                                                }
                                            });
                                }

                                return Observable.error(throwable);
                            }
                        });
                    }
                })
                .subscribe(s -> System.out.println(s),
                        throwable -> System.out.println(throwable.getMessage()));
    }


    @Test
    public void testRetry() {

        Observable.just(data)
                .map(new Func1<String, String>() {
                    @Override
                    public String call(String s) {
                        if (data == null) {
                            System.out.println(s);
                            throw new NullPointerException("data is null");
                        }
                        return s;
                    }
                })
                .retry(new Func2<Integer, Throwable, Boolean>() {
                    @Override
                    public Boolean call(Integer integer, Throwable throwable) {

                        if (IllegalArgumentException.class.isInstance(throwable) || NullPointerException.class.isInstance(throwable)) {
                            System.out.println("retry 重试");
                            data = "retry data 123";
                            return true;
                        }

                        System.out.println("retry onError 通知发送该观察者");
                        return false;
                    }
                })
                .subscribe(s -> System.out.println("onNext " + s),
                        throwable -> System.out.println(throwable.getMessage()));
    }


    @Test
    public void testRxJava03() {

        Observable.just("1235")
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                        System.out.println("执行完毕 onCompleted");
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.out.println("执行错误 onError");
                    }

                    @Override
                    public void onNext(String s) {
                        System.out.println("执行中     onNext");
                    }
                });

    }


    static class A {
        public A(String name) {
            this.name = name;
        }

        public String name;
    }


}