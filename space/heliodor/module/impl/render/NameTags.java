package space.heliodor.module.impl.render;

import com.sun.security.ntlm.Client;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumChatFormatting;
import space.heliodor.Heliodor;
import space.heliodor.event.Event;
import space.heliodor.event.OnEvent;
import space.heliodor.event.impl.Event2D;
import space.heliodor.event.impl.Event3D;
import space.heliodor.event.impl.EventNameTag;
import space.heliodor.module.Category;
import space.heliodor.module.Module;
import org.lwjgl.opengl.GL11;
import space.heliodor.settings.OptionNumber;

import java.awt.*;
import java.text.DecimalFormat;

public class NameTags extends Module {
    public NameTags() {
        super("NameTags",0, Category.RENDER);

    }

    @OnEvent
    public void onEvent(Event event) {
        if (event instanceof EventNameTag) {
            event.setCancelled(true);
        }
        /*
            Credit:
            Autumn for GL's and positions
         */
        if (event instanceof Event3D) {
            for (EntityPlayer entity : mc.theWorld.playerEntities) {
                if (entity.isInvisible() || entity == mc.thePlayer)
                    continue;

                GL11.glPushMatrix();
                double x = entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * mc.timer.renderPartialTicks - mc.getRenderManager().renderPosX;
                double y = entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * mc.timer.renderPartialTicks - mc.getRenderManager().renderPosY;
                double z = entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * mc.timer.renderPartialTicks - mc.getRenderManager().renderPosZ;
                GL11.glTranslated(x, y + entity.getEyeHeight() + 1.4, z);
                if (mc.gameSettings.thirdPersonView == 2) {
                    GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0, 1, 0);
                    GlStateManager.rotate(-mc.getRenderManager().playerViewX, 1, 0, 0);
                } else {
                    GlStateManager.rotate(-mc.thePlayer.rotationYaw, 0, 1, 0);
                    GlStateManager.rotate(mc.thePlayer.rotationPitch, 1, 0, 0);
                }
                float distance = mc.thePlayer.getDistanceToEntity(entity), scaleVar1 = 0.017f, scaleVar2 = 0.07f;
                float scaleFactor = (float) (distance <= 7 ? 7 * scaleVar2 : (double) (distance * scaleVar2));
                scaleVar1 *= scaleFactor;
                scaleVar1 = Math.min(0.3f, scaleVar1);
                GL11.glScalef(-scaleVar1, -scaleVar1, .3f);

                GlStateManager.disableLighting();
                GL11.glDisable(GL11.GL_DEPTH_TEST);
                DecimalFormat decimalFormat = new DecimalFormat("#.#");
                String hp = decimalFormat.format(entity.getHealth());
                Gui.drawRect(-Heliodor.INSTANCE().google.getWidth(entity.getName() + EnumChatFormatting.GREEN + " " + hp) / 2 - 5, 38, Heliodor.INSTANCE().google.getWidth(entity.getName() + EnumChatFormatting.GREEN + " " + hp) / 2 + 5, 50, new Color(0, 0, 0, 80).getRGB());
                Gui.drawRect(0,0,0,0,0);
                Heliodor.INSTANCE().google.drawCenteredString(entity.getName() + EnumChatFormatting.GREEN + " " + hp, 0.5f, 40, new Color((int)HUD.red.getVal(), (int)HUD.green.getVal(), (int)HUD.blue.getVal(),255).getRGB());
                GL11.glEnable(GL11.GL_DEPTH_TEST);
                GL11.glPopMatrix();
            }
        }
    }
}
