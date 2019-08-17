package me.conji.cauldron.factories;

import me.conji.cauldron.Cauldron;
import me.conji.cauldron.api.CauldronInjector;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.reflections.Reflections;

import java.util.Set;

public class InjectionFactory {
    public static void initialize(Reflections reflections) {
        Context context = Cauldron.getInstance().getContext();
        context.eval("js", "global.__injectors__ = Object.create(null)");
        Set<Class<? extends CauldronInjector>> injectors = reflections.getSubTypesOf(CauldronInjector.class);
        injectors.forEach(InjectionFactory::put);
    }

    public static void put(Class<?> clazz) {
        Context context = Cauldron.getInstance().getContext();
        String className = clazz.getSimpleName();
        context.getBindings("js").getMember("__injectors__").putMember(className, clazz);
    }
}
