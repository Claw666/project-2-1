package edu.maastricht.ginrummy.UI.Event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;


public class EventHolder implements Comparable<EventHolder> {

    private EventListener eventListener;
    private Method method;
    private Subscribe subscribe;

    private int argumentIndex;
    private int argumentCount;

    public EventHolder(EventListener eventListener, Method method, Subscribe subscribe,
                       int argumentIndex, int argumentCount) {
        this.eventListener = eventListener;
        this.method = method;
        this.subscribe = subscribe;
        this.argumentIndex = argumentIndex;
        this.argumentCount = argumentCount;
    }

    public void execute(Event event) {
        try {
            Object[] parameters = new Object[argumentCount];
            parameters[argumentIndex] = event;

            this.method.invoke(this.eventListener, parameters);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public EventPriorities getPriority() {
        return this.subscribe.priority();
    }

    @Override
    public int compareTo(EventHolder o) {

        if(o.getPriority().getValue() == this.getPriority().getValue()) {
            return 0;
        }

        if(o.getPriority().getValue() > this.getPriority().getValue()) {
            return 1;
        }

        return -1;

    }
}
