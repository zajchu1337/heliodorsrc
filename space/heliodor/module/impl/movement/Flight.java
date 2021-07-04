package space.heliodor.module.impl.movement;

import net.minecraft.block.BlockChest;
import net.minecraft.block.BlockEnchantmentTable;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.BlockSnowBlock;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.passive.EntityRabbit;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.*;
import net.minecraft.util.EnumChatFormatting;
import space.heliodor.event.Event;
import space.heliodor.event.OnEvent;
import space.heliodor.event.impl.EventPreMotionUpdate;
import space.heliodor.event.impl.EventSendPacket;
import space.heliodor.event.impl.EventUpdate;
import space.heliodor.module.Category;
import org.lwjgl.input.Keyboard;
import space.heliodor.module.Module;
import space.heliodor.settings.OptionBool;
import space.heliodor.settings.OptionMode;
import space.heliodor.settings.OptionNumber;
import space.heliodor.utils.SendMessage;
import space.heliodor.utils.Stopwatch;
import space.heliodor.utils.movement.Utils;

import java.util.Random;

public class Flight extends Module {
    private static final OptionMode mode = new OptionMode("Flight Mode", "Motion", "Verus", "Verus2", "Motion", "SurvivalDub", "AntiAC");
    private static final OptionBool damage = new OptionBool("Damage", true);
    private static final OptionBool antiKick = new OptionBool("AntiKick", true);
    private static final OptionNumber speed = new OptionNumber("Flight Speed", 2f, 1f, 7f, 0.1f);
    private static final OptionBool bobbing = new OptionBool("View bobbing", true);

    private boolean verusBool;
    private int stage;
    private double flightSpeed;

    /*
    Bow Flight Variables
    TODO: Better Bow flight
     */
    private ItemStack itemStack;
    private Stopwatch stopwatch = new Stopwatch();

    public Flight() {
        super("Flight", Keyboard.KEY_F, Category.MOVEMENT);
        options.add(mode);
        options.add(damage);
        options.add(speed);
        options.add(bobbing);
        options.add(antiKick);
    }

    public void onEnable() {
        if(mode.name().equalsIgnoreCase("AntiAC")) {
            mc.timer.timerSpeed = 0.325f;
        }
        if (damage.isEnabled && !mode.name().equalsIgnoreCase("Verus2") && !mode.name().equalsIgnoreCase("SurvivalDub")) {
            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY + 4.0001, mc.thePlayer.posZ, false));
            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, false));
            mc.thePlayer.sendQueue.addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, true));
        }
        stopwatch.reset();
        stage = 0;
    }

    public void onDisable() {
        Utils.setSpeed(0);
        mc.timer.timerSpeed = 1f;
    }

    @OnEvent
    public void onEvent(Event event) {
        setDisplayName(name + " " + EnumChatFormatting.GRAY + mode.name());
        if(event instanceof EventSendPacket) {
            EventSendPacket eventSendPacket = (EventSendPacket) event;
            if(eventSendPacket.getPacket() instanceof C13PacketPlayerAbilities && antiKick.isEnabled()) {
                C13PacketPlayerAbilities c13PacketPlayerAbilities = (C13PacketPlayerAbilities) eventSendPacket.getPacket();
                c13PacketPlayerAbilities.setAllowFlying(true);
            }
            if(mode.name().equalsIgnoreCase("AntiAC") && eventSendPacket.getPacket() instanceof C03PacketPlayer) {
                ((C03PacketPlayer) eventSendPacket.getPacket()).setMoving(false);
                ((C03PacketPlayer) eventSendPacket.getPacket()).x += 1;
            }
        }
        if (event instanceof EventUpdate) {
            if (bobbing.isEnabled() && Utils.isMoving())
                mc.thePlayer.cameraYaw = 0.2f;
            switch (mode.name()) {
                case "Motion": {
                    mc.thePlayer.motionY = 0;
                    Utils.setSpeed(speed.value);
                    if (mc.gameSettings.keyBindJump.isKeyDown()) mc.thePlayer.motionY += 1f;
                    if (mc.gameSettings.keyBindSneak.isKeyDown()) mc.thePlayer.motionY -= 1f;
                    break;
                }
                case "AntiAC": {
                    mc.thePlayer.motionY = 0.0;
                    mc.thePlayer.onGround = true;
                    if(stopwatch.elapsed(1000) && !stopwatch.elapsed(1200)) {
                        final double yaw = Utils.getDirection();
                        mc.timer.timerSpeed = 0.3f;
                        mc.thePlayer.setPosition(mc.thePlayer.posX + -Math.sin(yaw) * 0.525, mc.thePlayer.posY, mc.thePlayer.posZ + Math.cos(yaw) * 0.525);
                    }
                    else if(stopwatch.elapsed(1200)) {
                        mc.timer.timerSpeed = 0.325f;
                        stopwatch.reset();
                    }
                    SendMessage.log(stopwatch.getElapsedTime() + "ms");
                    break;
                }
                case "SurvivalDub": {
                    mc.thePlayer.motionY = 0;
                    mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 1.0E-5, mc.thePlayer.posZ);
                    switch (stage) {
                        case 0: {
                            for(int i = 0; i < 9; i++) {
                                itemStack = mc.thePlayer.inventory.getStackInSlot(i);
                                if (itemStack != null && itemStack.getItem() instanceof ItemBow) {
                                    mc.thePlayer.inventory.currentItem = i;
                                    mc.thePlayer.rotationPitch = -90;
                                    mc.gameSettings.keyBindUseItem.pressed = true;
                                    if(stopwatch.elapsed(150)) {
                                        mc.gameSettings.keyBindUseItem.pressed = false;
                                    }
                                    if(stopwatch.elapsed(550)) {
                                        mc.thePlayer.rotationPitch = 0;
                                    }
                                }
                            }
                            if(mc.thePlayer.hurtTime > 0) {
                                stage++;
                            }
                            else {
                                Utils.setSpeed(0);
                                mc.thePlayer.jumpMovementFactor = 0;
                            }
                            break;
                        }
                        case 1: {
                            mc.thePlayer.motionY = 0.42;
                            flightSpeed = 0.5;
                            stage++;
                        }
                        case 2: {
                            flightSpeed -= flightSpeed / 120;
                            if(mc.thePlayer.onGround) toggle();
                        }
                    }
                    if(mc.thePlayer.isCollidedHorizontally || !Utils.isMoving()) {
                        flightSpeed = 0;
                    }
                    if(stage > 0) {
                        Utils.setSpeed(Math.max(0.25, flightSpeed));
                    }
                    break;
                }
                case "Verus": {
                    if (this.isToggled() && !mc.thePlayer.isCollidedVertically) {
                        mc.thePlayer.onGround = true;
                        mc.timer.timerSpeed = 0.941f;
                        if (mc.thePlayer.ticksExisted % 8 == 0 && !mc.gameSettings.keyBindJump.isKeyDown() && Utils.isMoving()) {
                            if (!verusBool)
                                mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY + 0.41999998688697815, mc.thePlayer.posZ);
                            else {
                                mc.thePlayer.motionX = 0;
                                mc.thePlayer.motionZ = 0;
                                mc.getNetHandler().addToSendQueue(new C03PacketPlayer.C04PacketPlayerPosition(mc.thePlayer.posX, mc.thePlayer.posY - 0.0784000015258789, mc.thePlayer.posZ, false));
                                mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - (0.0784000015258789 * 2), mc.thePlayer.posZ);
                            }
                            verusBool = !verusBool;
                            mc.thePlayer.onGround = false;
                        } else mc.thePlayer.motionY = 0;

                        if (!mc.gameSettings.keyBindJump.isKeyDown())
                            Utils.strafe((float) 0.4);
                        mc.thePlayer.isCollidedHorizontally = false;

                        if (mc.gameSettings.keyBindJump.isKeyDown()) {
                            mc.thePlayer.motionY = 0.5;
                        }

                        if (mc.thePlayer.posY != Math.round(mc.thePlayer.posY)
                                && mc.gameSettings.keyBindJump.isKeyDown() || mc.gameSettings.keyBindSneak.isKeyDown()) {
                            mc.thePlayer.setPosition(mc.thePlayer.posX, Math.round(mc.thePlayer.posY),
                                    mc.thePlayer.posZ);
                        }
                        break;
                    }
                }
                case "Verus2": {
                    mc.thePlayer.motionY = 0;
                    switch (stage) {
                        case 0: {
                            mc.thePlayer.motionY = 0.42;
                            flightSpeed = speed.getVal();
                            stage++;
                        }
                        case 1: {
                            flightSpeed -= flightSpeed / 120;
                        }
                    }
                    if(mc.thePlayer.isCollidedHorizontally || !Utils.isMoving()) {
                        flightSpeed = 0;
                    }
                    Utils.setSpeed(Math.max(0.25, flightSpeed));
                    break;
                }
            }
        }
    }
}
