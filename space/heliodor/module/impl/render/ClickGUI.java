package space.heliodor.module.impl.render;

import org.lwjgl.input.Keyboard;
import space.heliodor.Heliodor;
import space.heliodor.event.OnEvent;
import space.heliodor.module.Category;
import space.heliodor.module.Module;
import space.heliodor.settings.OptionMode;

import java.util.Collections;

public class ClickGUI extends Module {

    public static OptionMode mode = new OptionMode("ClickGUI Mode", "Moon", "Moon", "Remix");
    public ClickGUI() {
        super("ClickGUI", Keyboard.KEY_RSHIFT, Category.RENDER);
        options.add(mode);
    }

    @Override
    public void onEnable() {
        switch (mode.name()) {
            case "Moon": {
                mc.displayGuiScreen(Heliodor.INSTANCE().moonClickGui);
                break;
            }
            case "Remix": {
                mc.displayGuiScreen(Heliodor.INSTANCE().remixClickGui);
                break;
            }
        }
        this.toggle();
    }
}