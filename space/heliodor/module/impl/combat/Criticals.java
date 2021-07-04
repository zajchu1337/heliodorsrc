package space.heliodor.module.impl.combat;

import net.minecraft.network.play.client.C02PacketUseEntity;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.util.EnumChatFormatting;
import space.heliodor.Heliodor;
import space.heliodor.event.Event;
import space.heliodor.event.OnEvent;
import space.heliodor.event.impl.EventSendPacket;
import space.heliodor.module.Category;
import space.heliodor.module.Module;
import space.heliodor.settings.OptionMode;
import space.heliodor.settings.OptionNumber;
import space.heliodor.utils.Stopwatch;

import java.util.Spliterator;
import java.util.SplittableRandom;
import java.util.concurrent.CopyOnWriteArrayList;

public class Criticals extends Module {
    private static final OptionMode mode = new OptionMode("Criticals Mode", "Vanilla", "Vanilla", "AAC", "NoGround");
    private static final OptionNumber hurttime = new OptionNumber("Hurttime", 8, 1, 20, 1);
    private static final Stopwatch timer = new Stopwatch();
    private static final SplittableRandom splittableRandom = new SplittableRandom();
    private static final CopyOnWriteArrayList<Double> offsets = new CopyOnWriteArrayList<>();
    public Criticals() {
        super("Criticals",0, Category.COMBAT);
        options.add(mode);
        options.add(hurttime);
    }

    @OnEvent
    public void onEvent(Event event) {
        this.setDisplayName("Criticals " + EnumChatFormatting.GRAY + mode.name() + " " + Math.round(hurttime.getVal()));
        if(event instanceof EventSendPacket) {
            EventSendPacket sendPacket = (EventSendPacket) event;
            if(sendPacket.getPacket() instanceof C02PacketUseEntity) {
                if(((C02PacketUseEntity) sendPacket.getPacket()).getAction() == C02PacketUseEntity.Action.ATTACK) {
                    switch (mode.name()) {
                        case "AAC": {
                            if(timer.elapsed(400) && mc.thePlayer.hurtTime < hurttime.getVal() && mc.thePlayer.onGround && !Heliodor.INSTANCE().moduleMgr.getModuleByName("Flight").isToggled() && !Heliodor.INSTANCE().moduleMgr.getModuleByName("Speed").isToggled()) {
                                mc.thePlayer.sendQueue.sendQueueBypass(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + splittableRandom.nextDouble() / 32, mc.thePlayer.posZ,false));
                                timer.reset();
                            }
                            break;
                        }
                        case "Vanilla": {
                            if(mc.thePlayer.hurtTime < hurttime.getVal() && mc.thePlayer.onGround && !Heliodor.INSTANCE().moduleMgr.getModuleByName("Flight").isToggled() ) {
                                mc.thePlayer.sendQueue.sendQueueBypass(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.1, mc.thePlayer.posZ,false));
                            }
                            break;
                        }
                        case "NoGround": {
                            mc.thePlayer.sendQueue.sendQueueBypass(new C03PacketPlayer(false));
                        }
                    }
                }
            }
        }
    }
}