package com.junbo.langur.core.promise;

import com.google.common.util.concurrent.ForwardingListenableFuture;
import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.Executor;

/**
 * Created by Shenhua on 7/16/2014.
 */
public class ListenableFutureWrapper<V> extends ForwardingListenableFuture.SimpleForwardingListenableFuture<V> {
    public ListenableFutureWrapper(ListenableFuture<V> delegate) {
        super(delegate);
    }

    @Override
    public void addListener(Runnable listener, Executor exec) {
        super.addListener(new RunnableWrapper(listener), exec);
    }
}
