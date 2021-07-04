package space.heliodor;

import org.lwjgl.opengl.Display;
import space.heliodor.event.Event;
import space.heliodor.module.ModuleManager;
import space.heliodor.module.configs.ConfigSystem;
import space.heliodor.ui.RemixDropdown;

import space.heliodor.ui.MoonDropdown;
import space.heliodor.utils.font.TTFFontRenderer;


import java.awt.*;
import java.io.*;

public class Heliodor {
    public static String name = "Heliodor", customname = "Heliodor", version = "Alpha";
    public static TTFFontRenderer font1;
    public static TTFFontRenderer font2;
    public static TTFFontRenderer google;
    public static TTFFontRenderer fontSmall1;
    public static TTFFontRenderer icons;
    public static TTFFontRenderer icons_heliodor;
    public static TTFFontRenderer verdana;
    public static TTFFontRenderer clickGui1;
    public static TTFFontRenderer clickGui2;
    public static ModuleManager moduleMgr;
    public static MoonDropdown moonClickGui;
    public static RemixDropdown remixClickGui;
    public static ConfigSystem configSystem;

    public void onInitialize() {
        System.out.println("Initializing Heliodor...");
        try {
            String path = System.getProperty("user.dir") + "/versions/Heliodor/heliodor_assets/";
            font1 = new TTFFontRenderer(Font.createFont(Font.TRUETYPE_FONT, new File( path + "font.ttf")).deriveFont(Font.PLAIN, 9.0f));
            google = new TTFFontRenderer(Font.createFont(Font.TRUETYPE_FONT, new File(path + "fontCustom.ttf")).deriveFont(Font.PLAIN, 18.0f));
            fontSmall1 = new TTFFontRenderer(Font.createFont(Font.TRUETYPE_FONT, new File(path + "font.ttf")).deriveFont(Font.PLAIN, 7.0f));
            font2 = new TTFFontRenderer(Font.createFont(Font.TRUETYPE_FONT,new File(path + "font2.TTF")).deriveFont(Font.PLAIN, 18.5f));
            icons = new TTFFontRenderer(Font.createFont(Font.TRUETYPE_FONT, new File(path + "icons_heliodor.ttf")).deriveFont(Font.PLAIN, 18.0f));
            icons_heliodor = new TTFFontRenderer(Font.createFont(Font.TRUETYPE_FONT, new File(path + "icons.ttf")).deriveFont(Font.PLAIN, 18.0f));
            verdana = new TTFFontRenderer(Font.createFont(Font.TRUETYPE_FONT, new File(path + "Verdana.ttf")).deriveFont(Font.PLAIN, 9.0f));
            clickGui1 = new TTFFontRenderer(Font.createFont(Font.TRUETYPE_FONT, new File(path + "clickGui1.ttf")).deriveFont(Font.PLAIN, 18.0f));
            clickGui2 = new TTFFontRenderer(Font.createFont(Font.TRUETYPE_FONT, new File(path + "clickGui2.ttf")).deriveFont(Font.PLAIN, 18.0f));
            moduleMgr = new ModuleManager();
            moonClickGui = new MoonDropdown();
            remixClickGui = new RemixDropdown();
            configSystem = new ConfigSystem();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("There was a problem. Contact with client owner on discord for help.");
        }

        Display.setTitle("Heliodor " + version);
    }

    public static Heliodor INSTANCE() {
        return new Heliodor();
    }
    public static boolean canPass;
    public void onEvent(Event event) {
        moduleMgr.getModules().forEach(module -> {
            canPass = module.isToggled;
            if(canPass) module.onEvent(event);
        });
    }
}