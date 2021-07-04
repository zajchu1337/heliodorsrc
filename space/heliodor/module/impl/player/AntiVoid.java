package space.heliodor.module.impl.player;

import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import org.apache.commons.lang3.RandomUtils;
import space.heliodor.event.Event;
import space.heliodor.event.OnEvent;
import space.heliodor.event.impl.EventUpdate;
import space.heliodor.module.Category;
import space.heliodor.module.Module;
import space.heliodor.settings.OptionMode;

public class AntiVoid extends Module {
    private static final OptionMode mode = new OptionMode("AntiVoid Mode", "NCP", "NCP", "Watchdog");
    public AntiVoid() {
        super("AntiVoid",0, Category.PLAYER);
        options.add(mode);
    }

    @OnEvent
    public void onEvent(Event event) {
        if(event instanceof EventUpdate) {
            if (mc.thePlayer.fallDistance > 2.5 && mc.theWorld.isAirBlock(new BlockPos(0, -1, 0))) {
                switch (mode.name()) {
                    case "NCP": {
                        if (mc.thePlayer.fallDistance > 4 && mc.thePlayer.ticksExisted % 40 == 0) mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 3, mc.thePlayer.posZ);
                        break;
                    }
                }
            }
        }
    }
}
