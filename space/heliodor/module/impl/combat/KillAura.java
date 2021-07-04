package space.heliodor.module.impl.combat;

import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.projectile.EntityLargeFireball;
import net.minecraft.item.ItemSword;
import net.minecraft.network.play.client.*;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.EnumFacing;
import org.lwjgl.input.Keyboard;
import space.heliodor.event.Event;
import space.heliodor.event.OnEvent;
import space.heliodor.event.impl.EventPostMotionUpdate;
import space.heliodor.event.impl.EventPreMotionUpdate;
import space.heliodor.module.Category;
import space.heliodor.module.Module;
import space.heliodor.settings.OptionBool;
import space.heliodor.settings.OptionMode;
import space.heliodor.settings.OptionNumber;
import space.heliodor.utils.SendMessage;
import space.heliodor.utils.Stopwatch;

import java.util.Comparator;
import java.util.SplittableRandom;

public class KillAura extends Module {
    public static OptionMode mode = new OptionMode("KillAura Mode", "Single", "Single", "Switch");
    public static OptionMode rotationMode = new OptionMode("Rotation Mode", "NCP", "NCP", "AAC");
    public static OptionMode autoblockMode = new OptionMode("Autoblock Mode", "NCP", "NCP", "Vanilla", "None", "Fake");
    public static OptionNumber reachSetting = new OptionNumber("Reach", 4.5f, 3, 8, 0.1f);
    public static OptionNumber apsSetting = new OptionNumber("APS", 15, 5, 20, 1);
    public static OptionBool players = new OptionBool("Players", true);
    public static OptionBool others = new OptionBool("Others", false);
    public EntityLivingBase target;
    public Stopwatch timer = new Stopwatch();
    public float yaw, pitch;
    private static final Stopwatch switchTimer = new Stopwatch();
    private static final SplittableRandom random = new SplittableRandom();
    int targetNumber;
    public KillAura() {
        super("KillAura", Keyboard.KEY_R, Category.COMBAT);
        options.add(mode);
        options.add(rotationMode);
        options.add(autoblockMode);
        options.add(reachSetting);
        options.add(apsSetting);
        options.add(players);
        options.add(others);
    }

    @OnEvent
    public void onEvent(Event event) {
        if(event instanceof EventPreMotionUpdate) {
            this.setDisplayName("KillAura " + EnumChatFormatting.GRAY + mode.name());
            boolean canAutoblock = mc.thePlayer.getHeldItem() != null && mc.thePlayer.getHeldItem().getItem() instanceof ItemSword && mc.thePlayer.getHeldItem().getItem() != null;
            yaw = mc.thePlayer.rotationYaw;
            pitch = mc.thePlayer.prevRotationPitch;

            Object[] possibleTargets = mc.theWorld.loadedEntityList.stream().filter(this::isValid).toArray();
            if(possibleTargets.length > 0 && !(possibleTargets[0] instanceof EntityLargeFireball)) {
                if((target == null || !isValid(target))) {
                    target = (EntityLivingBase) possibleTargets[0];
                }
            }

            if (target == null) return;
            float[] facing = rotations(target);

            if(isValid(target)) {
                if(!rotationMode.name().equalsIgnoreCase("AAC")) {
                    mc.thePlayer.rotationYaw = facing[0];
                    mc.thePlayer.rotationPitch = facing[1];
                    mc.thePlayer.rotationYawHead = facing[0];
                    mc.thePlayer.rotationPitchHead = facing[1];
                    mc.thePlayer.renderYawOffset = facing[0];
                    mc.thePlayer.prevRenderYawOffset = facing[0];
                }

                if(canAutoblock) {
                    switch (autoblockMode.name()) {
                        case "Fake": {
                            mc.playerController.sendFakeUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.getCurrentEquippedItem());
                            break;
                        }

                        case "NCP": {
                            mc.playerController.sendFakeUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.getCurrentEquippedItem());
                            mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, mc.thePlayer.getCurrentEquippedItem(), 0, 0, 0));
                            break;
                        }

                        case "Vanilla": {
                            mc.thePlayer.setItemInUse(mc.thePlayer.getHeldItem(), 71626);
                            break;
                        }
                    }
                }

                if(!timer.elapsed((long) (1000 / apsSetting.getVal()))) return;

                if(canAutoblock) {
                    switch (autoblockMode.name()) {
                        case "NCP": {
                            mc.getNetHandler().addToSendQueue(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.RELEASE_USE_ITEM, new BlockPos(1, 1, 1), EnumFacing.DOWN));
                            break;
                        }
                    }
                }
                mc.thePlayer.swingItem();
                mc.thePlayer.sendQueue.addToSendQueue(new C02PacketUseEntity(target, C02PacketUseEntity.Action.ATTACK));

                if(mode.name().equalsIgnoreCase("Switch") && possibleTargets.length > 0) {
                    targetNumber = random.nextInt(possibleTargets.length);
                    target = (EntityLivingBase) possibleTargets[targetNumber];
                }

                if(canAutoblock) {
                    switch (autoblockMode.name()) {
                        case "NCP": {
                            mc.playerController.sendFakeUseItem(mc.thePlayer, mc.theWorld, mc.thePlayer.getCurrentEquippedItem());
                            mc.thePlayer.sendQueue.addToSendQueue(new C08PacketPlayerBlockPlacement(new BlockPos(-1, -1, -1), 255, mc.thePlayer.getCurrentEquippedItem(), 0, 0, 0));
                            break;
                        }
                    }
                }
            }
            timer.reset();
            switchTimer.reset();
        }
        if(event instanceof EventPostMotionUpdate) {
            mc.thePlayer.rotationYaw = yaw;
            mc.thePlayer.rotationPitch = pitch;

            if (target == null)
                return;

            if(!timer.elapsed((long) (1000 / apsSetting.getVal()))) return;
        }
    }

    public float[] rotations(Entity e) {
        double deltaX = e.boundingBox.minX + (e.boundingBox.maxX - e.boundingBox.minX + 0.1) - mc.thePlayer.posX, deltaY = e.posY - 4.25 + e.getEyeHeight() - mc.thePlayer.posY + mc.thePlayer.getEyeHeight(), deltaZ = e.boundingBox.minZ + (e.boundingBox.maxX - e.boundingBox.minX) - mc.thePlayer.posZ, distance = Math.sqrt(Math.pow(deltaX, 2) + Math.pow(deltaZ, 2));
        float yaw = (float) Math.toDegrees(-Math.atan(deltaX / deltaZ)), pitch = (float) -Math.toDegrees(Math.atan(deltaY / distance));
        final double v = Math.toDegrees(Math.atan(deltaZ / deltaX));
        if (deltaX < 0 && deltaZ < 0) yaw = (float) (90 + v);
        else if (deltaX > 0 && deltaZ < 0) yaw = (float) (-90 + v);
        return new float[] {yaw, pitch};
    }

    public EntityLivingBase getTarget() {
        EntityLivingBase target = null;
        for(Entity entity : mc.theWorld.loadedEntityList) {
            if(entity instanceof EntityLivingBase) {
                EntityLivingBase entity2 = (EntityLivingBase) entity;
                if(mc.thePlayer.getDistanceToEntity(entity) <= reachSetting.getVal() && isValid(entity2)) {
                    target = entity2;
                }
            }
        }
        return target;
    }
    public boolean isValid(Entity entity) {
        if (entity != null) {
            if (entity.getDistanceToEntity(mc.thePlayer) <= reachSetting.getVal()) {
                if (entity instanceof EntityLivingBase) {
                    //&& !entity.isDead && ((EntityLivingBase)entity).getHealth() > 0
                    if (entity != mc.thePlayer && !entity.isDead) {
                        if (entity instanceof EntityVillager) {
                            return false;
                        }
                        if (players.isEnabled() && (entity instanceof EntityOtherPlayerMP)) {
                            return true;
                        }
                        if (others.isEnabled() && (entity instanceof EntityAnimal || entity instanceof EntityMob)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
