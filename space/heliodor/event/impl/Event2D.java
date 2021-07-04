package space.heliodor.event.impl;

import space.heliodor.event.Event;

public class Event2D extends Event {
    public double partialTicks;

    public Event2D() {}
    public Event2D(double partialTicks) { this.partialTicks = partialTicks; }
}
