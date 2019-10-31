package me.conji.cauldron.api.async;

import me.conji.cauldron.api.js.CauldronPromise;
import org.graalvm.polyglot.Value;

public interface AsyncContext {
    public CauldronPromise register(Value promise);
}