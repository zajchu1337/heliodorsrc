package space.heliodor.event.impl;

import net.minecraft.client.gui.ScaledResolution;
import space.heliodor.event.Event;

public class EventCrosshair extends Event {
    private final ScaledResolution sr;

    public EventCrosshair(ScaledResolution sr) {
        this.sr = sr;
    }

    public ScaledResolution getScaledRes() {
        return this.sr;
    }
}
