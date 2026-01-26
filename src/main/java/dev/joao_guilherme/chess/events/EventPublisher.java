package dev.joao_guilherme.chess.events;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class EventPublisher {

    private final Map<Class<?>, List<Consumer<?>>> listeners = new HashMap<>();

    public <T extends GameEvent> void subscribe(Class<T> eventType, Consumer<T> listener) {
        listeners.computeIfAbsent(eventType, _ -> new ArrayList<>()).add(listener);
    }

    @SuppressWarnings("unchecked")
    public <T extends GameEvent> void publish(T event) {
        listeners.getOrDefault(event.getClass(), List.of()).forEach(listener -> ((Consumer<T>) listener).accept(event));
    }
}

