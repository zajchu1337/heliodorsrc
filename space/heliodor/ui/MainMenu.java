package space.heliodor.ui;

import net.minecraft.client.gui.*;
import net.minecraft.util.ResourceLocation;
import space.heliodor.Heliodor;
import space.heliodor.login.GuiAltLogin;

import java.awt.*;
import java.util.ArrayList;

public class MainMenu extends GuiScreen {
    public static ArrayList<String> buttonList;

    public MainMenu() {
        buttonList = new ArrayList<>();
        buttonList.add("Singleplayer");
        buttonList.add("Multiplayer");
        buttonList.add("Options");
        buttonList.add("Exit");
        buttonList.add("Alt Login");
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        this.drawRect(0,0,0,0,0);
        this.drawRect(0,0,1920,1080, new Color(36, 36, 36, 255).getRGB());
        mc.fontRendererObj.drawStringWithShadow("Heliodor",width / 2 - mc.fontRendererObj.getStringWidth("Heliodor") / 2, height / 2 - 100, -1);
        this.drawSkeetRect(width / 2, height / 2);
        this.drawSkeetButton(width / 2, height / 2 + 1);
        this.drawSkeetButton(width / 2, height / 2 + 16);
        this.drawSkeetButton(width / 2, height / 2 + 31);
        this.drawSkeetButton(width / 2, height / 2 + 46);
        this.drawSkeetButton(width / 2, height / 2 + 61);
        Heliodor.INSTANCE().verdana.drawCenteredString("Singleplayer", width / 2, height / 2 - 36f - 1.1f, new Color(255,255,255,255).getRGB());
        Heliodor.INSTANCE().verdana.drawCenteredString("Multiplayer", width / 2, height / 2 - 36f + 15 - 1.1f, new Color(255,255,255,255).getRGB());
        Heliodor.INSTANCE().verdana.drawCenteredString("Options", width / 2, height / 2 - 36f + 30 - 1.1f, new Color(255,255,255,255).getRGB());
        Heliodor.INSTANCE().verdana.drawCenteredString("Exit", width / 2, height / 2 - 36f + 45 - 1.1f, new Color(255,255,255,255).getRGB());
        Heliodor.INSTANCE().verdana.drawCenteredString("Alt Login", width / 2, height / 2 - 31f + 55 - 1.1f, new Color(255,255,255,255).getRGB());
        Heliodor.INSTANCE().verdana.drawStringWithShadow("Credits:", 5, 7, new Color(0,255,0,255).getRGB());
        Heliodor.INSTANCE().verdana.drawStringWithShadow("Sigma - Bed Breaker Rotations", 5, 12, -1);
        Heliodor.INSTANCE().verdana.drawStringWithShadow("Radium - Scaffold findBlock function and Vec3 finder for scaffold", 5 , 17, -1);
        Heliodor.INSTANCE().verdana.drawStringWithShadow("Astro - Render Utils", 5 , 22, -1);
        Heliodor.INSTANCE().verdana.drawStringWithShadow("Autumn - Pulse Color Util", 5 , 27, -1);
    }
    public void mouseClicked(int mouseX, int mouseY, int button) {
        if (mouseX > width / 2 - 30 && mouseX < width / 2 + 30 && mouseY > height / 2 - 40 && mouseY < height / 2 - 30 + Heliodor.INSTANCE().font1.getHeight("Singleplayer")) {
            mc.displayGuiScreen(new GuiSelectWorld(this));
        }
        if (mouseX > width / 2 - 30 && mouseX < width / 2 + 30 && mouseY > height / 2 - 25 && mouseY < height / 2 - 30 + 10 + Heliodor.INSTANCE().font1.getHeight("Multiplayer")) {
            mc.displayGuiScreen(new GuiMultiplayer(this));
        }
        if (mouseX > width / 2 - 30 && mouseX < width / 2 + 30 && mouseY > height / 2 - 10 && mouseY < height / 2 - 30 + 20 + Heliodor.INSTANCE().font1.getHeight("Options")) {
            mc.displayGuiScreen(new GuiOptions(this, mc.gameSettings));
        }
        if (mouseX > width / 2 - 30 && mouseX < width / 2 + 30 && mouseY > height / 2 + 5 && mouseY < height / 2 - 30 + 30 + Heliodor.INSTANCE().font1.getHeight("Exit")) {
            System.exit(1337);
        }
        if (mouseX > width / 2 - 30 && mouseX < width / 2 + 30 && mouseY > height / 2 + 5 && mouseY < height / 2 - 30 + 50 + Heliodor.INSTANCE().font1.getHeight("Alt Login")) {
            this.mc.displayGuiScreen(new GuiAltLogin(this));
        }
    }

    @Override
    public void initGui() {
        /*
        Why is it here?
         */
//        mc.entityRenderer.loadShader(new ResourceLocation("shaders/post/blur.json"));
    }

    public void drawSkeetRect(float width1, float height1) {
        this.drawRect(width1 - 46.5f, height1 - 66.5f, width1 + 46.5f, height1 + 66.5f, new Color(0, 0, 0,255).getRGB());
        this.drawRect(width1 - 46, height1 - 66, width1 + 46, height1 + 66, new Color(48, 48, 48,255).getRGB());
        this.drawRect(width1 - 44.5f, height1 - 64.5f, width1 + 44.5f, height1 + 64.5f, new Color(33, 33, 33,255).getRGB());
        this.drawRect(width1 - 43.5f, height1 - 63.5f, width1 + 43.5f, height1 + 63.5f, new Color(0, 0, 0,255).getRGB());
        this.drawRect(width1 - 43, height1 - 63, width1 + 43, height1 + 63, new Color(9, 9, 9,255).getRGB());
        this.drawRect(width1 - 40.5f, height1 - 60.5f, width1 + 40.5f, height1 + 60.5f, new Color(48, 48, 48,255).getRGB());
        this.drawRect(width1 - 40, height1 - 60, width1 + 40, height1 + 60, new Color(17, 17, 17,255).getRGB());
    }
    
    public void drawSkeetButton(int width1, int height1) {
        this.drawRect(width1 - 31f, height1 - 43f, width1 + 31f, height1 - 30f, new Color(0, 0, 0,255).getRGB());
        this.drawRect(width1 - 30.5f, height1 - 42.5f, width1 + 30.5f, height1 - 30.5f, new Color(45, 45, 45,255).getRGB());
        this.drawGradientRect(width1 - 30, height1 - 42, width1 + 30, height1 - 31, new Color(28, 28, 28,255).getRGB(), new Color(19, 19, 19, 255).getRGB());
    }
}
