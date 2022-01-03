package edu.maastricht.ginrummy.UI.Event;

public class CancellableEvent extends Event {

    private boolean cancelled = false;

    public CancellableEvent() {}

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public boolean isCancelled() {
        return this.cancelled;
    }

}
