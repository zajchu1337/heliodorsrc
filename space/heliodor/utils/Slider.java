package space.heliodor.utils;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.MathHelper;
import net.minecraft.util.MouseFilter;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.input.Mouse;
import space.heliodor.module.impl.render.ClickGUI;
import space.heliodor.settings.OptionNumber;

import java.awt.*;

public class Slider {
    int min;
    int max;
    int x;
    int y;
    int width;
    int mouseX;
    int mouseY;
    public boolean dragging = isSliderHovered(mouseX, mouseY) ? Mouse.isButtonDown(0) ? true : false : false;
    OptionNumber option;

    public Slider(int min, int max, int x, int y, int width, OptionNumber option) {
        this.min = min;
        this.max = max;
        this.x = x;
        this.y = y;
        this.width = width;
        this.option = option;
    }

    public Slider(int min, int max, int x, int y, int width, OptionNumber option, Color color) {
        this.min = min;
        this.max = max;
        this.x = x;
        this.y = y;
        this.width = width;
        this.option = option;
    }

    public void draw(int mouseX, int mouseY) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
        double percentBar = (option.getVal() - min)/(max - min);
        Gui.drawRect(x, y - 0.5f, x + width, (float) (y + 0.5), new Color(30,30,30,150).getRGB());
        switch (ClickGUI.mode.name()) {
            case "Moon": {
                Gui.drawRect(x, y - 0.5f, (float)(x + (percentBar * width)), y + 0.5f, new Color(252, 77, 77,255).getRGB());
                break;
            }
            case "Remix": {
                Gui.drawRect(x, y - 0.5f, (float)(x + (percentBar * width)), y + 0.5f, -1);
                break;
            }
        }
        boolean dragging1 = isSliderHovered(mouseX, mouseY) && Mouse.isButtonDown(0);
        if(dragging1) {
            option.value = option.getMinimum() + ((mouseX - ((float)x)) / ((float) width) * (option.getMaximum() - option.getMinimum()));
        }
    }

    public boolean isSliderHovered(int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x + width && mouseY >= y - 2 && mouseY <= y + 3;
    }
    public void mouseClicked(int mouseX, int mouseY, int mouseButton) {

    }

    public void mouseReleased() {
        this.dragging = false;
    }
}
