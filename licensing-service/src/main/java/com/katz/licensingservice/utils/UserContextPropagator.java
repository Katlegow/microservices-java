package com.katz.licensingservice.utils;

import io.github.resilience4j.core.ContextPropagator;

import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Supplier;
import com.katz.licensingservice.model.UserContext;

public class UserContextPropagator implements ContextPropagator<UserContext> {
    @Override
    public Supplier<Optional<UserContext>> retrieve() {
        return () -> Optional.ofNullable(UserContextHolder.get());
    }

    @Override
    public Consumer<Optional<UserContext>> copy() {
        return (userContext) -> {
            userContext.ifPresent( (context) -> {
                clear();
                UserContextHolder.set(context);
            });
        };
    }

    @Override
    public Consumer<Optional<UserContext>> clear() {
        return (userContext) -> {
            UserContextHolder.remove();
        };
    }
}
