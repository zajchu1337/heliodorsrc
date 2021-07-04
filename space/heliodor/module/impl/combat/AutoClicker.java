package space.heliodor.module.impl.combat;

import net.minecraft.network.play.client.C02PacketUseEntity;
import optifine.MathUtils;
import org.lwjgl.input.Mouse;
import space.heliodor.event.Event;
import space.heliodor.event.OnEvent;
import space.heliodor.event.impl.EventPreMotionUpdate;
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

public class AutoClicker extends Module {

    public static OptionNumber jitterstrength = new OptionNumber("Jitter Strength", 0.5, 0, 20, 1);
    public static OptionNumber cpsSetting = new OptionNumber("cps", 15, 5, 20, 1);
    public static OptionBool jitter = new OptionBool("Jitter", false);
    private static final Stopwatch timer = new Stopwatch();
    private static final SplittableRandom random = new SplittableRandom();

    public AutoClicker() {
        super("AutoClicker", 0, Category.COMBAT);
        options.add(cpsSetting);
        options.add(jitterstrength);
        options.add(jitter);
    }

    @OnEvent
    public void onEvent(Event event) {
        if (event instanceof EventUpdate) {
            if (timer.elapsed((long) (1000 / cpsSetting.getVal())) && mc.currentScreen == null && Mouse.isButtonDown(0)) {
                mc.thePlayer.swingItem();
                if(jitter.isEnabled){
                    mc.thePlayer.rotationYaw += random.nextInt(0, 2) * jitterstrength.getVal();
                    mc.thePlayer.rotationPitch += random.nextInt(0, 2) * jitterstrength.getVal();
                }
                if (mc.pointedEntity != null && mc.thePlayer.getDistanceToEntity(mc.pointedEntity) <= 3) {
                    mc.thePlayer.sendQueue.sendQueueBypass(new C02PacketUseEntity(mc.pointedEntity, C02PacketUseEntity.Action.ATTACK));
                }
                timer.reset();
            }

        }
    }
}

