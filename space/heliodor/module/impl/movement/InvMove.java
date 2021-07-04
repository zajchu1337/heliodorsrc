package space.heliodor.module.impl.movement;

import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.settings.KeyBinding;
import org.lwjgl.input.Keyboard;
import space.heliodor.event.Event;
import space.heliodor.event.OnEvent;
import space.heliodor.event.impl.EventUpdate;
import space.heliodor.module.Category;
import space.heliodor.module.Module;

public class InvMove extends Module {
    public InvMove() {
        super("InvMove",0, Category.MOVEMENT);
    }

    private final KeyBinding[] moveKeys = { mc.gameSettings.keyBindRight, mc.gameSettings.keyBindLeft, mc.gameSettings.keyBindBack, mc.gameSettings.keyBindForward, mc.gameSettings.keyBindJump, mc.gameSettings.keyBindSprint };

    @OnEvent
    public void onEvent(Event event) {
        if(event instanceof EventUpdate) {
            if(!(mc.currentScreen instanceof GuiChat)) {
                for (KeyBinding moveKey : moveKeys) {
                    moveKey.pressed = Keyboard.isKeyDown(moveKey.getKeyCode());
                }
            }
        }
    }
}

