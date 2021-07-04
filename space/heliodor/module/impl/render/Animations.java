package space.heliodor.module.impl.render;

import space.heliodor.module.Category;
import space.heliodor.module.Module;
import space.heliodor.settings.OptionMode;
import space.heliodor.settings.OptionNumber;

public class Animations extends Module {
    public static final OptionMode mode = new OptionMode("Animation", "Exhi", "Exhi", "1.7", "Slide", "Lucky", "Normal");
    public static final OptionNumber handspeed = new OptionNumber("Hand Speed", 1, 1, 10, 1);
    public static final OptionNumber itemsize = new OptionNumber("Item Size", 50, 1, 100, 1);
    public Animations() {
        super("Animations", 0, Category.RENDER);
        options.add(mode);
        options.add(handspeed);
        options.add(itemsize);
    }
}
