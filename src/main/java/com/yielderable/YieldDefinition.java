package com.yielderable;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.atomic.AtomicReference;

import static com.yielderable.Completed.completed;
import static com.yielderable.Exceptions.unchecked;
import static com.yielderable.FlowControl.youMayProceed;
import static com.yielderable.IfAbsent.ifAbsent;
import static com.yielderable.Message.message;

public class YieldDefinition<T> implements Iterable<T>, ClosableIterator<T> {
    private final SynchronousQueue<Message<T>> dataChannel = new SynchronousQueue<>();
    private final SynchronousQueue<FlowControl> flowChannel = new SynchronousQueue<>();
    private final AtomicReference<Optional<T>> currentValue = new AtomicReference<>(Optional.empty());
    private List<Runnable> toRunOnClose = new CopyOnWriteArrayList<>();

    public void returning(T value) {
        publish(value);
        waitUntilNextValueRequested();
    }

    public void breaking() {
        throw new BreakException();
    }

    @Override
    public ClosableIterator<T> iterator() {
        return this;
    }

    @Override
    public boolean hasNext() {
        calculateNextValue();
        Message<T> message = unchecked(() -> dataChannel.take());
        if (message instanceof Completed) return false;
        currentValue.set(message.value());
        return true;
    }

    @Override
    public T next() {
        try {
            ifAbsent(currentValue.get()).then(this::hasNext);
            return currentValue.get().get();
        } finally {
            currentValue.set(Optional.empty());
        }
    }

    public void signalComplete() {
        unchecked(() -> this.dataChannel.put(completed()));
    }

    public void waitUntilFirstValueRequested() {
        waitUntilNextValueRequested();
    }

    private void waitUntilNextValueRequested() {
        unchecked(() -> flowChannel.take());
    }

    private void publish(T value) {
        unchecked(() -> dataChannel.put(message(value)));
    }

    private void calculateNextValue() {
        unchecked(() -> flowChannel.put(youMayProceed));
    }

    @Override
    public void close() {
        toRunOnClose.forEach(Runnable::run);
    }

    public void onClose(Runnable onClose) {
        this.toRunOnClose.add(onClose);
    }

    @Override
    protected void finalize() throws Throwable {
        close();
        super.finalize();
    }
}
