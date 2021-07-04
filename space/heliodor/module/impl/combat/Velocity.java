package space.heliodor.module.impl.combat;

import net.minecraft.network.play.server.S12PacketEntityVelocity;
import net.minecraft.network.play.server.S27PacketExplosion;
import net.minecraft.util.EnumChatFormatting;
import space.heliodor.event.Event;
import space.heliodor.event.OnEvent;
import space.heliodor.event.impl.EventRecievePacket;
import space.heliodor.module.Category;
import space.heliodor.module.Module;
import space.heliodor.settings.OptionNumber;

public class Velocity extends Module {
    public Velocity() {
        super("Velocity",0, Category.COMBAT);
    }

    @OnEvent
    public void onEvent(Event event) {
        this.setDisplayName("Velocity " + EnumChatFormatting.GRAY + "Cancel");
        if(event instanceof EventRecievePacket) {
            EventRecievePacket recievePacket = (EventRecievePacket) event;
            if(recievePacket.getPacket() instanceof S12PacketEntityVelocity || recievePacket.getPacket() instanceof S27PacketExplosion) {
                event.setCancelled(true);
            }
        }
    }
}
