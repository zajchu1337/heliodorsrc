package space.heliodor.module.impl.render;

import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.S03PacketTimeUpdate;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.EnumChatFormatting;
import space.heliodor.Heliodor;
import space.heliodor.event.Event;
import space.heliodor.event.OnEvent;
import space.heliodor.event.impl.EventPreMotionUpdate;
import space.heliodor.event.impl.EventRecievePacket;
import space.heliodor.event.impl.EventSendPacket;
import space.heliodor.event.impl.EventUpdate;
import space.heliodor.module.Category;
import space.heliodor.module.Module;
import space.heliodor.settings.OptionMode;
import space.heliodor.settings.OptionNumber;
import space.heliodor.utils.SendMessage;
import space.heliodor.utils.movement.Utils;

import java.util.SplittableRandom;
import java.util.concurrent.CopyOnWriteArrayList;

public class Atmosphere extends Module {

    public OptionNumber time = new OptionNumber("Time", 10, 1, 20, 1);
    public Atmosphere() {
        super("Atmosphere", 0, Category.RENDER);
        options.add(time);
    }


    @OnEvent
    public void onEvent(Event event) {
        this.setDisplayName(this.name + " " + EnumChatFormatting.GRAY + Math.round(time.getVal()));
        if (event instanceof EventRecievePacket) {
            EventRecievePacket recievePacket = (EventRecievePacket) event;
            if (recievePacket.getPacket() instanceof S03PacketTimeUpdate) {
                event.setCancelled(true);
            }
        }
        if(event instanceof EventUpdate){
            mc.theWorld.setWorldTime((long) time.getVal() * 1000);
        }
    }
}