package space.heliodor.module.impl.player;

import space.heliodor.event.Event;
import space.heliodor.event.OnEvent;
import space.heliodor.event.impl.EventUpdate;
import space.heliodor.module.Category;
import space.heliodor.module.Module;
import space.heliodor.settings.OptionNumber;

public class Step extends Module {
    /*
    TODO: NCP Step
     */
    private static final OptionNumber stepheight = new OptionNumber("Step Height", 1f, 1f, 2f, 0.1f);
    public Step() {
        super("Step", 0, Category.PLAYER);
        options.add(stepheight);
    }

    public void onDisable() {
        mc.thePlayer.stepHeight = 0.625f;
    }

    @OnEvent
    public void onEvent(Event event) {
        if(event instanceof EventUpdate) {
            mc.thePlayer.stepHeight = (float) stepheight.getVal();
        }
    }
}

