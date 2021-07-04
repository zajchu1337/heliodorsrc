package space.heliodor.module.impl.movement;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.C00PacketKeepAlive;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C0CPacketInput;
import net.minecraft.network.play.client.C0FPacketConfirmTransaction;
import net.minecraft.network.play.server.S08PacketPlayerPosLook;
import net.minecraft.network.play.server.S32PacketConfirmTransaction;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import space.heliodor.Heliodor;
import space.heliodor.event.Event;
import space.heliodor.event.OnEvent;
import space.heliodor.event.impl.*;
import space.heliodor.module.Category;
import space.heliodor.module.Module;
import space.heliodor.module.impl.combat.KillAura;
import space.heliodor.module.impl.player.AntiVoid;
import space.heliodor.module.impl.player.Disabler;
import space.heliodor.settings.OptionMode;
import space.heliodor.settings.OptionNumber;
import space.heliodor.utils.Stopwatch;
import space.heliodor.utils.movement.Utils;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class Speed extends Module {
    private static final OptionMode mode = new OptionMode("Speed Mode", "Vanilla", "Vanilla", "Watchdog", "NCP");
    private static final OptionNumber speed  = new OptionNumber("Speed Value", 0.5, 0.2, 5, 0);
    private int stage;
    public Speed() {
        super("Speed", Keyboard.KEY_X , Category.MOVEMENT);
        options.add(mode);
        options.add(speed);
    }

    @Override
    public void onEnable() {
        mc.timer.timerSpeed = 1f;
    }

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1f;
    }

    @OnEvent
    public void onEvent(Event event) {
        this.setDisplayName(this.name + " " + EnumChatFormatting.GRAY + mode.name());
        if (event instanceof EventUpdate) {
            switch (mode.name()) {
                case "Vanilla": {
                    if(mc.thePlayer.onGround && Utils.isMoving()) mc.thePlayer.jump();

                    Utils.setSpeed(speed.getVal());
                    break;
                }
                case "NCP": {
                    if (Utils.isMoving()) {
                        if(mc.thePlayer.onGround && Utils.isMoving()){
                            mc.thePlayer.motionY = 0.4;
                            mc.thePlayer.isAirBorne = true;
                            mc.timer.timerSpeed = 1.4f;
                        }
                        else {
                            mc.timer.timerSpeed = 1f;
                        }
                        switch (stage) {
                            case 0: {
                                Utils.strafe(0.2924f);
                                break;
                            }
                            case 1: {
                                Utils.strafe(0.31f);
                                break;
                            }
                            case 2: {
                                Utils.strafe(0.28f);
                                break;
                            }
                            case 3: {
                                Utils.strafe(0.25f);
                                break;
                            }
                            case 4: {
                                Utils.strafe(0.3f);
                                break;
                            }
                            case 5: {
                                Utils.strafe(0.2785f);
                                break;
                            }
                            case 6: {
                                stage = 0;
                                mc.thePlayer.setPosition(mc.thePlayer.posX, mc.thePlayer.posY - 1.0E-5, mc.thePlayer.posZ);
                                break;
                            }
                        }
                        break;
                    }
                }
            }
        }
    }
}