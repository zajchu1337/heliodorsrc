package space.heliodor.event.impl;

import space.heliodor.event.Event;

public class EventReach extends Event {
    private float reach;

    public EventReach(float reach) {
        this.reach = reach;
    }

    public float getReach() {
        return this.reach;
    }

    public void setReach(float reach) {
        this.reach = reach;
    }
}
