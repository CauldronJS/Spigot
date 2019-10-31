package me.conji.cauldron;

import me.conji.cauldron.api.async.AsyncContext;
import me.conji.cauldron.api.js.CauldronPromise;
import org.graalvm.polyglot.Value;

import java.util.concurrent.ConcurrentLinkedQueue;

public class SpigotAsyncContext implements AsyncContext {
    private ConcurrentLinkedQueue<CauldronPromise> promises;
    private Thread promiseThread;

    public SpigotAsyncContext() {
        this.promises = new ConcurrentLinkedQueue<>();
        this.promiseThread = new Thread();
    }

    @Override
    public CauldronPromise register(Value promise) {
        CauldronPromise cauldronPromise = new CauldronPromise() {
            @Override
            public boolean isBlocking() {
                return false;
            }
        };
        return cauldronPromise;
    }
}
