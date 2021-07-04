package space.heliodor.module.impl.movement;

import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.C09PacketHeldItemChange;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.client.C0BPacketEntityAction;
import net.minecraft.potion.Potion;
import net.minecraft.util.*;
import org.lwjgl.input.Keyboard;
import space.heliodor.Heliodor;
import space.heliodor.event.Event;
import space.heliodor.event.OnEvent;
import space.heliodor.event.impl.EventPostMotionUpdate;
import space.heliodor.event.impl.EventPreMotionUpdate;
import space.heliodor.event.impl.EventSendPacket;
import space.heliodor.event.impl.EventUpdate;
import space.heliodor.module.Category;
import space.heliodor.module.Module;
import space.heliodor.settings.OptionBool;
import space.heliodor.settings.OptionNumber;
import space.heliodor.utils.BlockUtil;
import space.heliodor.utils.Stopwatch;
import space.heliodor.utils.movement.Utils;

import java.util.Random;
import java.util.SplittableRandom;

public class Scaffold extends Module {
    private static final OptionBool sprint = new OptionBool("Sprint", true);
    private static final OptionBool expand = new OptionBool("Expand", true);
    private static final OptionBool itemSpoof = new OptionBool("ItemSpoof", true);
    private static final OptionBool swing = new OptionBool("Swing", true);
    private static final OptionNumber expandsize = new OptionNumber("ExpandSize", 2f, 1f, 7f, 0.1f);
    public float yaw, pitch;
    public Stopwatch timer = new Stopwatch();
    public BlockUtil block;
    private ItemStack itemStack = null;
    private int startItem;
    BlockPos[] blockPosList = new BlockPos[]{new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0), new BlockPos(0, 0, 1), new BlockPos(0, 0, -1)};
    EnumFacing[] faceList = new EnumFacing[]{EnumFacing.WEST, EnumFacing.EAST, EnumFacing.NORTH, EnumFacing.SOUTH};

    public Scaffold() {
        super("Scaffold", Keyboard.KEY_B, Category.MOVEMENT);
        options.add(expand);
        options.add(expandsize);
        options.add(sprint);
        options.add(itemSpoof);
        options.add(swing);
    }

    @OnEvent
    public void onEvent(Event event) {
        if(event instanceof EventSendPacket) {
            EventSendPacket sendPacket = (EventSendPacket) event;
            /*
            Sprint bypass soon???
             */
        }
        if(event instanceof EventPreMotionUpdate) {
            mc.thePlayer.setSprinting(sprint.isEnabled() && Utils.isMoving());
            yaw = mc.thePlayer.rotationYaw;
            pitch = mc.thePlayer.rotationPitch;
            block = getBlock(new BlockPos(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ).offset(EnumFacing.DOWN));
            for(double i = 0; i < (expand.isEnabled() ? expandsize.getVal() : 1); i += 1) block = getBlock(getExpandBlock(i).add(0, 0, 0).offset(EnumFacing.DOWN));
            float[] rotations = getRotations(block);
            mc.thePlayer.rotationYaw = rotations[0];
            mc.thePlayer.rotationPitch = rotations[1];
            mc.thePlayer.rotationYawHead = rotations[0];
            mc.thePlayer.renderYawOffset = rotations[0];
            mc.thePlayer.rotationPitchHead = rotations[1];
            placeBlock();
        }
        if(event instanceof EventPostMotionUpdate) {
            Heliodor.INSTANCE().moduleMgr.getModuleByName("KillAura").isToggled = false;
            mc.thePlayer.rotationYaw = yaw;
            mc.thePlayer.rotationPitch = pitch;
        }
    }

    private boolean validBlockCheck(Block pos) {
        return (!(pos instanceof BlockLiquid)) && (pos.getMaterial() != Material.air);
    }

    private void placeBlock() {
        if(block != null) {
            startItem = mc.thePlayer.inventory.currentItem;
            for(int i = 0; i < 9; i++) {
                itemStack = mc.thePlayer.inventory.getStackInSlot(i);
                if(itemStack != null && itemStack.getItem() instanceof ItemBlock) {
                    ItemBlock itemBlock = (ItemBlock) itemStack.getItem();
                    if(!(itemBlock.getBlock() instanceof BlockChest) && !(itemBlock.getBlock() instanceof BlockEnchantmentTable) && !(itemBlock.getBlock() instanceof BlockSnow) && !(itemBlock.getBlock() instanceof BlockSnowBlock)) {
                        if(itemSpoof.isEnabled()) {
                            mc.thePlayer.inventory.currentItem = i;
                        }
                        if(mc.playerController.onPlayerRightClick(mc.thePlayer, mc.theWorld, mc.thePlayer.inventory.getStackInSlot(i), block.blockPos, block.facing, getVec3(block.blockPos, block.facing))) {
                          if(swing.isEnabled) mc.thePlayer.swingItem();
                          else mc.thePlayer.sendQueue.sendQueueBypass(new C0APacketAnimation());
                        }
                        if(itemSpoof.isEnabled()) {
                            mc.thePlayer.inventory.currentItem = startItem;
                        }
                        else if(!itemSpoof.isEnabled()) {
                            mc.thePlayer.inventory.setCurrentItem(mc.thePlayer.inventory.getStackInSlot(i).getItem(), 0, false, false);
                        }
                    }
                }
            }
        }
    }
    public BlockPos getExpandBlock(double expand) {
        return new BlockPos(mc.thePlayer.posX + (-Math.sin(Math.toRadians(mc.thePlayer.rotationYawHead)) * expand), mc.thePlayer.posY, mc.thePlayer.posZ + (Math.cos(Math.toRadians(mc.thePlayer.rotationYawHead)) * expand));
    }

    @Override
    public void onEnable() {
        if(!itemSpoof.isEnabled()) {
            startItem = mc.thePlayer.inventory.currentItem;
        }
    }

    @Override
    public void onDisable() {
        mc.timer.timerSpeed = 1f;
        mc.getNetHandler().addToSendQueue(new C09PacketHeldItemChange(mc.thePlayer.inventory.currentItem));
        if(!itemSpoof.isEnabled()) mc.thePlayer.inventory.currentItem = startItem;
    }


    public float[] getRotations(BlockUtil block){
        if(block != null) {
            if(block.blockPos != null && block.facing != null) {
                double x = block.blockPos.getX() + 0.5 - mc.thePlayer.posX + (double)block.facing.getFrontOffsetX()/2;
                double z = block.blockPos.getZ() + 0.5 - mc.thePlayer.posZ + (double)block.facing.getFrontOffsetZ()/2;
                double d1 = mc.thePlayer.posY + mc.thePlayer.getEyeHeight() -(block.blockPos.getY() + 0.5);
                double d3 = MathHelper.sqrt_double(x * x + z * z);
                float pitch = (float)(Math.atan2(d1, d3) * 180.0D / Math.PI);
                float yaw = (float)(Math.atan2(z, x) * 180.0D / Math.PI) - 45.0F;
                return  new float[]{yaw, pitch};
            }
        }
        return new float[]{mc.thePlayer.rotationYaw, mc.thePlayer.rotationPitch};
    }

    /*
    Credit: Radium for this
     */
    private BlockUtil getBlock(BlockPos pos) {
        for (int i = 0; i < blockPosList.length; i++) {
            BlockPos blockPos = pos.add(blockPosList[i]);
            if (validBlockCheck(mc.theWorld.getBlockState(blockPos).getBlock())) {
                return new BlockUtil(blockPos, faceList[i]);
            }
        }

        BlockPos posBelow = new BlockPos(0, -1, 0);
        if (validBlockCheck(mc.theWorld.getBlockState(pos.add(posBelow)).getBlock())) {
            return new BlockUtil(pos.add(posBelow), EnumFacing.UP);
        }

        for (BlockPos blockPosition : blockPosList) {
            BlockPos blockPos = pos.add(blockPosition);
            for (int i = 0; i < blockPosList.length; i++) {
                BlockPos blockPos1 = blockPos.add(blockPosList[i]);
                if (validBlockCheck(mc.theWorld.getBlockState(blockPos1).getBlock())) {
                    return new BlockUtil(blockPos1, faceList[i]);
                }
            }
        }

        for (BlockPos blockPosition : blockPosList) {
            BlockPos blockPos = pos.add(blockPosition);
            for (BlockPos position : blockPosList) {
                BlockPos blockPos1 = blockPos.add(position);
                for (int i = 0; i < blockPosList.length; i++) {
                    BlockPos blockPos2 = blockPos1.add(blockPosList[i]);
                    if (validBlockCheck(mc.theWorld.getBlockState(blockPos2).getBlock())) {
                        return new BlockUtil(blockPos2, faceList[i]);
                    }
                }
            }
        }
        return null;
    }

    private Vec3 getVec3(BlockPos pos, EnumFacing face) {
        Vec3i directionVec = face.getDirectionVec();
        double x = directionVec.getX() * 0.5D;
        double y = directionVec.getY() * 0.5D;
        double z = directionVec.getZ() * 0.5D;

        return new Vec3(pos)
                .addVector(0.5D, 0.5D, 0.5D)
                .addVector(x, y, z);
    }
}
