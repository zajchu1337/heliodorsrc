package space.heliodor.module.impl.player;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C0DPacketCloseWindow;
import org.lwjgl.Sys;
import org.lwjgl.input.Keyboard;
import space.heliodor.Heliodor;
import space.heliodor.event.Event;
import space.heliodor.event.OnEvent;
import space.heliodor.event.impl.EventInitGui;
import space.heliodor.event.impl.EventUpdate;
import space.heliodor.module.Category;
import space.heliodor.module.Module;
import space.heliodor.settings.OptionBool;
import space.heliodor.settings.OptionNumber;
import space.heliodor.utils.Stopwatch;

import java.util.Random;

public class ChestStealer extends Module {
    public OptionBool silent = new OptionBool("Silent", true);
    public OptionNumber delay = new OptionNumber("Delay", 150, 10, 300, 10);

    public ChestStealer() {
        super("ChestStealer", Keyboard.KEY_K, Category.PLAYER);
        options.add(delay);
        options.add(silent);
    }

    @OnEvent
    public void onEvent(Event event){
        if(event instanceof EventInitGui) {
            EventInitGui eventInitGui = (EventInitGui) event;
            if(silent.isEnabled()) {
                mc.displayGuiScreen(null);
                mc.thePlayer.inventory.setItemStack(null);
            }
            Thread chestThread = new Thread(() -> {
                try {
                    for(int i = 0; i < eventInitGui.getGui().inventoryRows * 9; i++) {
                        Slot slot = eventInitGui.getGui().inventorySlots.inventorySlots.get(i);
                        if(slot.getStack() != null) {
                            eventInitGui.getGui().handleMouseClick(slot, slot.slotNumber, 0, 1);
                            Thread.sleep((long) delay.getVal());
                        }
                    }
                    if(silent.isEnabled()) mc.thePlayer.sendQueue.addToSendQueue(new C0DPacketCloseWindow());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });

            chestThread.start();
        }
    }
}