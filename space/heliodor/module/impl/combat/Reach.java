package space.heliodor.module.impl.combat;

import net.minecraft.network.play.client.C02PacketUseEntity;
import optifine.MathUtils;
import org.lwjgl.input.Mouse;
import space.heliodor.event.Event;
import space.heliodor.event.OnEvent;
import space.heliodor.event.impl.EventPreMotionUpdate;
import space.heliodor.event.impl.EventReach;
import space.heliodor.event.impl.EventUpdate;
import space.heliodor.module.Category;
import space.heliodor.module.Module;
import space.heliodor.settings.OptionBool;
import space.heliodor.settings.OptionMode;
import space.heliodor.settings.OptionNumber;
import space.heliodor.utils.Stopwatch;

import java.util.Random;
import java.util.SplittableRandom;
import java.util.concurrent.CopyOnWriteArrayList;

public class Reach extends Module {


    public static OptionNumber rangeSetting = new OptionNumber("Reach", 4, 3, 6, 1);


    public Reach() {
        super("Reach", 0, Category.COMBAT);
        options.add(rangeSetting);

    }

    @OnEvent
    public void onEvent(Event event) {
        if (event instanceof EventReach) {

        ((EventReach) event).setReach((float) rangeSetting.getVal());
        }
    }
}



