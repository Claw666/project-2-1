package edu.maastricht.ginrummy.UI.Event;

import edu.maastricht.ginrummy.UI.Event.Events.PlayerPickedEvent;

import java.lang.reflect.Method;
import java.util.*;

public class EventHandler {

    private Map<Class<? extends Event>, ArrayList<EventHolder>> events = new HashMap<>();
    private List<EventListener> eventListeners = new ArrayList<>();

    public EventHandler() {}

    public boolean trigger(Event event) {
        if(!(this.events.containsKey(event.getClass()))) {
            return false;
        }

        for(EventHolder eventHandler : this.events.get(event.getClass())) {
            eventHandler.execute(event);
        }
        return true;

    }

    public boolean trigger(Event event, EventTriggerCallback eventTriggerCallback) {

        if(!(this.events.containsKey(event.getClass()))) {
            return false;
        }

        Collection<EventHolder> eventHolders = this.events.get(event.getClass());

        for(EventHolder eventHolder : eventHolders) {

            eventHolder.execute(event);

        }

        eventTriggerCallback.callback(
                event,
                eventHolders
        );

        return true;

    }

    public boolean registerListener(EventListener listener) {

        if(this.eventListeners.contains(listener)) {
            return false;
        }

        this.eventListeners.add(listener);

        Method[] methods = listener.getClass().getDeclaredMethods();
        for(Method method : methods) {

            Subscribe subscribe = method.getAnnotation(Subscribe.class);

            if (subscribe == null) {
                continue;
            }

            if (!(method.getParameterTypes().length >= 1)) {
                continue;
            }

            if (!(method.getReturnType().equals(void.class))) {
                continue;
            }

            {
                Class<?>[] parameters = method.getParameterTypes();
                boolean invalid = false;
                for (Class<?> clazz : parameters) {

                    if (!(Event.class.isAssignableFrom(clazz))) {
                        invalid = true;
                        break;
                    }
                }
                if (invalid) {
                    continue;
                }
            }

            Class<?>[] parameters = method.getParameterTypes();
            for (int i = 0; i < parameters.length; i++) {
                Class<? extends Event> argument = (Class<? extends Event>) parameters[i];

                if (!(this.events.containsKey(argument))) {
                    this.events.put(argument, new ArrayList<>());
                }

                if (!(this.events.get(argument).add(new EventHolder(
                        listener,
                        method,
                        subscribe,
                        i,
                        parameters.length
                )))) {
                    throw new IllegalStateException();
                }

            }



        }

        return true;

    }

}