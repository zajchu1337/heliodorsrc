package space.heliodor.module.impl.player;

import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.play.client.*;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.util.EnumChatFormatting;
import space.heliodor.Heliodor;
import space.heliodor.event.Event;
import space.heliodor.event.OnEvent;
import space.heliodor.event.impl.EventPreMotionUpdate;
import space.heliodor.event.impl.EventRecievePacket;
import space.heliodor.event.impl.EventSendPacket;
import space.heliodor.module.Category;
import space.heliodor.module.Module;
import space.heliodor.settings.OptionMode;
import space.heliodor.utils.SendMessage;
import space.heliodor.utils.movement.Utils;

import java.util.SplittableRandom;
import java.util.concurrent.CopyOnWriteArrayList;

public class Disabler extends Module {
    private static final OptionMode mode = new OptionMode("Disabler Mode", "Verus", "Verus", "Vehicle");
    public Disabler() {
        super("Disabler",0, Category.PLAYER);
        options.add(mode);
    }

    private static final CopyOnWriteArrayList<C0FPacketConfirmTransaction> bypassList = new  CopyOnWriteArrayList<>();
    private static final SplittableRandom random = new SplittableRandom();
    private int current;
    double x,y,z;

    @OnEvent
    public void onEvent(Event event) {
        this.setDisplayName(this.name + " " + EnumChatFormatting.GRAY + mode.name());
        if(event instanceof EventPreMotionUpdate) {
            switch (mode.name()) {
                case "Verus": {
                    if(mc.thePlayer.ticksExisted < 5 && mc.theWorld != null) {
                        current = 0;
                        bypassList.clear();
                    }

                    if(mc.thePlayer.ticksExisted % 100 == 0 && bypassList.size() > 0 && (bypassList.size() - 1) > current) {
                        mc.thePlayer.sendQueue.sendQueueBypass(bypassList.get(++current));
                    }

                    if(mc.thePlayer.ticksExisted % 1000 == 0) {
                        current = 0;
                        bypassList.clear();
                    }

                    /*
                    TODO: Remove teleport
                     */
                    mc.thePlayer.sendQueue.sendQueueBypass(new C00PacketKeepAlive(-1));
                    if(mc.thePlayer.ticksExisted % 60 == 0) {
                        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, mc.thePlayer.posY + 5997.7235, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, true));
                        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, mc.thePlayer.posY - 5997.7235, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, false));
                        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, mc.thePlayer.posY + 5997.7235, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, true));
                        mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, mc.thePlayer.posY - 5997.7235, mc.thePlayer.posZ, mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch, false));
                    }
                    if(mc.thePlayer.ticksExisted > 1200 && mc.thePlayer.ticksExisted < 1300) {
                        System.out.println("Verus crashed? You can fly now.");
                    }
                    break;
                }
            }
        }

        if(event instanceof EventSendPacket) {
            EventSendPacket sendPacket = (EventSendPacket) event;
            switch (mode.name()) {
                case "Verus": {
                    if(sendPacket.getPacket() instanceof C0FPacketConfirmTransaction) {
                        bypassList.add((C0FPacketConfirmTransaction) ((EventSendPacket) event).getPacket());
                        event.setCancelled(true);
                    }

                    if(sendPacket.getPacket() instanceof C00PacketKeepAlive) {
                        ((C00PacketKeepAlive) sendPacket.getPacket()).key = -2;
                    }

                    if(sendPacket.getPacket() instanceof C03PacketPlayer) {
                        /*
                        Half of ac is turned off when you are riding animal or vehicle LOL
                         */
                        mc.thePlayer.sendQueue.addToSendQueue(new C0CPacketInput());
                    }
                    break;
                }

                /*
                TODO: Fix on Ghostly.
                 */
                case "Vehicle": {
                    if(sendPacket.getPacket() instanceof C03PacketPlayer) {
                        mc.thePlayer.sendQueue.addToSendQueue(new C0CPacketInput());
                    }
                    break;
                }
            }
        }
    }
}
