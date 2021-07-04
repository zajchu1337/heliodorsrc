package space.heliodor.module.impl.render;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.EnumChatFormatting;
import org.lwjgl.opengl.GL11;
import space.heliodor.Heliodor;
import space.heliodor.event.Event;
import space.heliodor.event.OnEvent;
import space.heliodor.event.impl.Event2D;
import space.heliodor.module.Category;
import space.heliodor.module.Module;
import space.heliodor.settings.OptionBool;
import space.heliodor.settings.OptionMode;
import space.heliodor.settings.OptionNumber;
import space.heliodor.utils.Render2DUtil;
import space.heliodor.utils.movement.Utils;
import java.awt.*;
import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.concurrent.CopyOnWriteArrayList;

public class HUD extends Module {
    private static final OptionMode mode = new OptionMode("HUD Color", "Pulse", "Pulse", "Color");
    private static final OptionMode font = new OptionMode("Font", "Fancy", "Fancy", "Minecraft");
    private static final OptionMode watermark = new OptionMode("Watermark", "Default", "Default", "None");
    private static final OptionBool background = new OptionBool("Background" ,false);
    private static final OptionBool sidebar = new OptionBool("Sidebar" ,false);
    private static final OptionBool outline = new OptionBool("Outline" ,false);
    public static final OptionNumber red = new OptionNumber("Red",255,0,255,5);
    public static final OptionNumber green = new OptionNumber("Green",255,0,255,5);
    public static final OptionNumber blue = new OptionNumber("Blue",255,0,255,5);
    private static final OptionNumber opacity = new OptionNumber("Opacity",255,0,255,1);
    private double animY = 1;
    private int colorInstance;
    public HUD() {
        super("HUD", 0, Category.RENDER);
        options.add(mode);
        options.add(watermark);
        options.add(font);
        options.add(background);
        options.add(sidebar);
        options.add(outline);
        options.add(red);
        options.add(green);
        options.add(blue);
        options.add(opacity);
    }

    /*
    TODO: Better Animations
     */
    @OnEvent
    public void onEvent(Event event) {
        if(event instanceof Event2D) {
            int arrayColor1 = new Color((int)red.getVal(), (int)green.getVal(), (int)blue.getVal(),255).getRGB();
            int opacityBackground = (int) Math.round(opacity.getVal());
            Color backgroundColor = new Color(0, 0, 0, opacityBackground);
            ScaledResolution sr = new ScaledResolution(mc);

            GL11.glPushMatrix();
            CopyOnWriteArrayList<Module> modules = Heliodor.INSTANCE().moduleMgr.getModules();
            switch (font.name()) {
                case "Fancy": {
                    modules.sort(Comparator.comparingDouble(m -> (double) Heliodor.INSTANCE().google.getWidth(((Module)m).getDisplayName())).reversed());
                    break;
                }
                case "Minecraft": {
                    modules.sort(Comparator.comparingDouble(m -> (double) mc.fontRendererObj.getStringWidth(((Module)m).getDisplayName())).reversed());
                    break;
                }
            }

            switch (watermark.name()) {
                case "Default": {
                    Heliodor.INSTANCE().font1.drawStringWithShadow(Heliodor.INSTANCE().customname, 2, 4, arrayColor1);
                    break;
                }
            }
            
            int count = 0;
            for(Module m : modules) {
                int arrayListColor = -1;
                switch (mode.name()) {
                    case "Pulse": {
                        arrayListColor = fadeBetween(arrayColor1, darker(arrayColor1, 0.5f), ((System.currentTimeMillis() + (count * 100)) % 1000 / (1000 / 2.0F)));
                        colorInstance = fadeBetween(arrayColor1, darker(arrayColor1, 0.5f), ((System.currentTimeMillis() + (count * 100)) % 1000 / (1000 / 2.0F)));
                        break;
                    }

                    case "Color": {
                        arrayListColor = new Color((int)red.getVal(), (int)green.getVal(), (int)blue.getVal(), 255).getRGB();
                        colorInstance = new Color((int)red.getVal(), (int)green.getVal(), (int)blue.getVal(), 255).getRGB();
                        break;
                    }
                }
                if(!m.isToggled() || m.name.equalsIgnoreCase("HUD") || m.name.equalsIgnoreCase("ESP")) {
                    if(m.animationX < sr.getScaledWidth() + 10){
                        m.animationX += 3;
                        m.inProcess = true;
                    }
                    else {
                        m.animationX = sr.getScaledWidth() + 11;
                        m.inProcess = false;
                    }
                }
                switch (font.name()) {
                    case "Fancy": {
                        if(m.animationX >= sr.getScaledWidth() -  Heliodor.INSTANCE().google.getWidth(m.getDisplayName()) + -1.1f) {
                            m.animationX -= m.animationX / 1100;
                        }
                        break;
                    }

                    case "Minecraft": {
                        if(m.animationX >= sr.getScaledWidth() -  mc.fontRendererObj.getStringWidth(m.getDisplayName()) + -2.9f) {
                            m.animationX -= m.animationX / 1100;
                        }
                        break;
                    }
                }

                Gui.drawRect(0,0,0,0,0);
                if(background.isEnabled()) Gui.drawRect(m.animationX - 2, font.name().equalsIgnoreCase("Fancy") ? Heliodor.INSTANCE().google.getHeight(m.getDisplayName()) + count + 2 : mc.fontRendererObj.FONT_HEIGHT + count + 2,1920, count, backgroundColor.getRGB());

                if(sidebar.isEnabled()) Gui.drawRect(sr.getScaledWidth() - 1.2f, 0, sr.getScaledWidth(), count,arrayListColor);

                if(outline.isEnabled()) Gui.drawRect(m.animationX - 2f, Heliodor.INSTANCE().font1.getHeight(m.getDisplayName()) + count + 4, m.animationX - 2f + 1f, count, arrayListColor);

                Gui.drawRect(0,0,0,0, 0);
                switch (font.name()) {
                    case "Fancy": {
                        Heliodor.INSTANCE().google.drawStringWithShadow(m.getDisplayName(), m.animationX, count + 1, arrayListColor);
                        break;
                    }
                    case "Minecraft": {
                        mc.fontRendererObj.drawStringWithShadow(m.getDisplayName(), m.animationX, count + 1, arrayListColor);
                        break;
                    }
                }

                count += m.animationX < sr.getScaledWidth() ? 11 - m.animationY : 0;

                m.animationY += 0.3;
                if(m.isToggled()) m.animationY = 0;
            }
            GL11.glPopMatrix();
        }
    }
    @Override
    public void onEnable() {
        ScaledResolution sr = new ScaledResolution(mc);
        for(Module m : Heliodor.INSTANCE().moduleMgr.getModules()) {
            m.animationX = sr.getScaledWidth() + 11;
        }
    }

    /*
        Credit:
        Autumn Client for Color Utils
     */
    public int fadeBetween(int color1, int color2, float offset) {
        if (offset > 1)
            offset = 1 - offset % 1;

        double invert = 1 - offset;
        int r = (int) ((color1 >> 16 & 0xFF) * invert +
                (color2 >> 16 & 0xFF) * offset);
        int g = (int) ((color1 >> 8 & 0xFF) * invert +
                (color2 >> 8 & 0xFF) * offset);
        int b = (int) ((color1 & 0xFF) * invert +
                (color2 & 0xFF) * offset);
        int a = (int) ((color1 >> 24 & 0xFF) * invert +
                (color2 >> 24 & 0xFF) * offset);
        return ((a & 0xFF) << 24) |
                ((r & 0xFF) << 16) |
                ((g & 0xFF) << 8) |
                (b & 0xFF);
    }

    public static int darker(int color, float factor) {
        int r = (int) ((color >> 16 & 0xFF) * factor);
        int g = (int) ((color >> 8 & 0xFF) * factor);
        int b = (int) ((color & 0xFF) * factor);
        int a = color >> 24 & 0xFF;

        return ((r & 0xFF) << 16) |
                ((g & 0xFF) << 8) |
                (b & 0xFF) |
                ((a & 0xFF) << 24);
    }
}
