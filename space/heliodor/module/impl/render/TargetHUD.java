package space.heliodor.module.impl.render;

import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.EntityLivingBase;
import org.lwjgl.opengl.GL11;
import space.heliodor.Heliodor;
import space.heliodor.event.Event;
import space.heliodor.event.OnEvent;
import space.heliodor.event.impl.Event2D;
import space.heliodor.module.Category;
import space.heliodor.module.Module;
import space.heliodor.module.impl.combat.KillAura;
import space.heliodor.settings.OptionMode;

import java.awt.*;
import java.text.DecimalFormat;

public class TargetHUD extends Module {
    public static final OptionMode mode = new OptionMode("TargetHUD Mode", "Astolfo", "Astolfo", "Heliodor");
    private double animHealth = 1;

    public TargetHUD() {
        super("TargetHUD", 0, Category.RENDER);
        options.add(mode);
    }

    /*
    TODO: Fix trash-code here
     */
    @OnEvent
    public void onEvent(Event event) {
        if (event instanceof Event2D) {
            KillAura aura = new KillAura();
            if (Heliodor.INSTANCE().moduleMgr.getModuleByName("KillAura").isToggled()) {
                if (aura.getTarget() instanceof EntityOtherPlayerMP) {
                    double width;
                    switch (mode.name()) {
                        case "Heliodor": {
                            EntityOtherPlayerMP target = (EntityOtherPlayerMP) aura.getTarget();
                            width = 75;
                            if (target != null) {
                                GL11.glPushMatrix();
                                GL11.glTranslated(GuiScreen.width / 2, GuiScreen.height / 2, GuiScreen.width / 2);
                                Gui.drawRect(-2, 34, (float) (2 + width), 60, new Color(40, 35, 35, 255).getRGB());
                                animHealth += ((target.getHealth() - animHealth) / 32) * 0.5;
                                if (animHealth < 0 || animHealth > target.getMaxHealth()) {
                                    animHealth = target.getHealth();
                                } else {
                                    mc.ingameGUI.drawGradientRect(0, 56, (int) ((animHealth / target.getMaxHealth()) * width), 58, new Color(255, 221, 0, 255).getRGB(), new Color(187, 163, 2, 255).getRGB());
                                }
                                DecimalFormat format = new DecimalFormat("#.#");
                                format.applyPattern("#.#");

                                String hp = format.format(target.getHealth());
                                mc.fontRendererObj.drawStringWithShadow(hp + " ❤", 2, 42, -1);
                                GL11.glPopMatrix();
                            } else {
                                animHealth = 1;
                            }
                            break;
                        }
                        case "Astolfo": {
                            EntityOtherPlayerMP target = (EntityOtherPlayerMP) aura.getTarget();
                            width = 140 - 32.5;
                            if (target != null) {
                                GL11.glPushMatrix();
                                GL11.glTranslated(GuiScreen.width / 2, GuiScreen.height / 2, GuiScreen.width / 2);
                                Gui.drawRect(-32.5f, 0, 145 - 32.5f, 50, new Color(0, 0, 0, 120).getRGB());
                                GL11.glScalef(2f, 2f, 2f);
                                DecimalFormat format = new DecimalFormat("#.#");
                                format.applyPattern("#.#");
                                String hp = format.format(target.getHealth() / 2);
                                mc.fontRendererObj.drawStringWithShadow(hp + " ❤", 0, 8.35f, -1);
                                GL11.glScalef(0.5f, 0.5f, 0.5f);
                                mc.fontRendererObj.drawStringWithShadow(target.getName(), 0, 4, -1);
                                GuiInventory.drawEntityOnScreen(-15, 45, 20, aura.getTarget().rotationYaw, aura.getTarget().rotationPitch, (EntityLivingBase) aura.getTarget());
                                Gui.drawRect(0f, 37.5f, 140 - 32.5f, 45.5f, new Color(66, 66, 66, 255).getRGB());
                                animHealth += ((target.getHealth() - animHealth) / 32) * 0.5;
                                if (animHealth < 0 || animHealth > target.getMaxHealth()) {
                                    animHealth = target.getHealth();
                                } else {
                                    Gui.drawRect(0f, 37.5f, (int) ((animHealth / target.getMaxHealth()) * width + 2), 45.5f, new Color(191, 190, 190, 255).getRGB());
                                    Gui.drawRect(0f, 37.5f, (int) ((animHealth / target.getMaxHealth()) * width), 45.5f, new Color(255, 255, 255, 255).getRGB());
                                }
                                GL11.glPopMatrix();
                                break;
                            }
                        }

                    }
                }
            }
        }
    }
}
