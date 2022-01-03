package edu.maastricht.ginrummy.UI.Event;

import java.util.Collection;

public interface EventTriggerCallback {

    void callback(Event event, Collection<EventHolder> eventHolders);

}
