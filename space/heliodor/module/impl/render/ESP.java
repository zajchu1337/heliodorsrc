package space.heliodor.module.impl.render;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;
import space.heliodor.event.Event;
import space.heliodor.event.OnEvent;
import space.heliodor.event.impl.Event3D;
import space.heliodor.module.Category;
import space.heliodor.module.Module;
import space.heliodor.settings.OptionMode;

import java.awt.*;

public class ESP extends Module {
    private static final OptionMode mode = new OptionMode("ESP Mode", "CSGO", "CSGO");

    public ESP() {
        super("ESP", 0, Category.RENDER);
    }

    /*
    TODO: Fix ESP
     */
    @OnEvent
    public void onEvent(Event event) {
        if(event instanceof Event3D) {
            Event3D event3D = (Event3D) event;
            for(Object e : mc.theWorld.loadedEntityList) {
                if(e instanceof EntityPlayer) {
                    EntityPlayer player = mc.thePlayer;
                    if(e == player && mc.gameSettings.thirdPersonView != 0 || e != player && !player.isSpectator()) {
                        EntityPlayer entityPlayer = (EntityPlayer) e;
                        switch (mode.name()) {
                            case "CSGO": {
                                double x = (entityPlayer.lastTickPosX + (entityPlayer.posX - entityPlayer.lastTickPosX) * event3D.partialTicks);
                                double y = (entityPlayer.lastTickPosY + (entityPlayer.posY - entityPlayer.lastTickPosY) * event3D.partialTicks);
                                double z = (entityPlayer.lastTickPosZ + (entityPlayer.posZ - entityPlayer.lastTickPosZ) * event3D.partialTicks);
                                drawBox(entityPlayer,x - RenderManager.renderPosX, y - RenderManager.renderPosY, z - RenderManager.renderPosZ);
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    public void drawBox(EntityLivingBase entity, double posX, double posY, double posZ) {
        GlStateManager.pushMatrix();
        GlStateManager.translate(posX, posY, posZ);
        GL11.glNormal3f(0.0f, 0.0f, 0.0f);
        GlStateManager.rotate(-RenderManager.playerViewY, 0.0f, 1.0f, 0.0f);
        GlStateManager.scale(-0.1, -0.1, 0.1);
        GL11.glDisable(2896);
        GL11.glDisable(2929);
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GlStateManager.disableTexture2D();
        GlStateManager.enableAlpha();
        GlStateManager.depthMask(true);
        GL11.glColor4f((float)HUD.red.getVal() / 255F,
                (float)HUD.green.getVal() / 255F,
                (float)HUD.blue.getVal() / 255F, 1.0f);

        GL11.glBegin(GL11.GL_LINE_LOOP);
        GL11.glVertex2f(-5.75f, 2.15f);
        GL11.glVertex2f(5.75f, 2.15f);
        GL11.glEnd();

        GL11.glBegin(GL11.GL_LINE_LOOP);
        GL11.glVertex2f(-5.75f, -20);
        GL11.glVertex2f(5.75f, -20);
        GL11.glEnd();

        GL11.glBegin(GL11.GL_LINE_LOOP);
        GL11.glVertex2f(-5.75f, -20);
        GL11.glVertex2f(-5.75f, 2.15f);
        GL11.glEnd();

        GL11.glBegin(GL11.GL_LINE_LOOP);
        GL11.glVertex2f(5.75f, -20);
        GL11.glVertex2f(5.75f, 2.15f);
        GL11.glVertex2f(5.75f, -20);
        GL11.glVertex2f(5.75f, 2.15f);
        GL11.glEnd();
        GlStateManager.enableTexture2D();
        GL11.glDisable(3042);
        GL11.glEnable(2929);
        GL11.glEnable(2896);
        GlStateManager.popMatrix();
    }
}
