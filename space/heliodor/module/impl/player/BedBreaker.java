package space.heliodor.module.impl.player;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBed;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.client.C03PacketPlayer;
import net.minecraft.network.play.client.C07PacketPlayerDigging;
import net.minecraft.network.play.client.C0APacketAnimation;
import net.minecraft.network.play.server.S02PacketChat;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.MathHelper;
import space.heliodor.Heliodor;
import space.heliodor.event.Event;
import space.heliodor.event.OnEvent;
import space.heliodor.event.impl.Event2D;
import space.heliodor.event.impl.EventRecievePacket;
import space.heliodor.event.impl.EventSendPacket;
import space.heliodor.event.impl.EventUpdate;
import space.heliodor.module.Category;
import space.heliodor.module.Module;
import space.heliodor.settings.OptionBool;
import space.heliodor.utils.BlockUtil;
import space.heliodor.utils.Render2DUtil;
import space.heliodor.utils.SendMessage;
import space.heliodor.utils.Stopwatch;

import java.awt.*;

public class BedBreaker extends Module {
    private static final OptionBool counter = new OptionBool("Counter", true);
    private Stopwatch timer = new Stopwatch();
    private boolean canBreakBed;

    public BedBreaker() {
        super("BedBreaker", 0, Category.PLAYER);
        options.add(counter);
    }

    @OnEvent
    public void onEvent(Event event) {
        if(event instanceof EventRecievePacket && mc.theWorld != null && mc.thePlayer.ticksExisted > 0 && counter.isEnabled()) {
            EventRecievePacket recievePacket = (EventRecievePacket) event;
            if(recievePacket.getPacket() instanceof S02PacketChat) {
                String message = ((S02PacketChat) recievePacket.getPacket()).getChatComponent().getUnformattedText();
                if(message.contains("zostali zniszczeni przez " + mc.thePlayer.getName())) {
                    timer.reset();
                    canBreakBed = false;
                }
            }
        }
        if (event instanceof EventUpdate) {
            if (timer.elapsed(15000) && counter.isEnabled()) canBreakBed = true;
            else if(!timer.elapsed(15000) && counter.isEnabled()) canBreakBed = false;
            else if(!counter.isEnabled()) canBreakBed = false;

            if(canBreakBed) {
                for (double xRange = -3; xRange < 3; xRange++) {
                    for (double yRange = -3; yRange < 3; yRange++) {
                        for (double zRange = -3; zRange < 5; zRange++) {
                            BlockPos blockPos = new BlockPos(mc.thePlayer.posX + xRange, mc.thePlayer.posY + yRange, mc.thePlayer.posZ + zRange);
                            Block blockBed = mc.theWorld.getBlockState(blockPos).getBlock();
                            if (blockBed instanceof BlockBed) {
                                float[] rotations = getRotations(blockPos, EnumFacing.UP);
                                mc.thePlayer.sendQueue.sendQueueBypass(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, rotations[0], rotations[1], mc.thePlayer.onGround));
                                mc.thePlayer.sendQueue.sendQueueBypass(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.START_DESTROY_BLOCK, blockPos, EnumFacing.NORTH));
                                mc.thePlayer.sendQueue.sendQueueBypass(new C03PacketPlayer.C06PacketPlayerPosLook(mc.thePlayer.posX, mc.thePlayer.posY, mc.thePlayer.posZ, rotations[0], rotations[1], mc.thePlayer.onGround));
                                mc.thePlayer.sendQueue.sendQueueBypass(new C07PacketPlayerDigging(C07PacketPlayerDigging.Action.STOP_DESTROY_BLOCK, blockPos, EnumFacing.NORTH));
                                mc.thePlayer.sendQueue.sendQueueBypass(new C0APacketAnimation());
                            }
                        }
                    }
                }
            }
        }
        if(event instanceof Event2D && counter.isEnabled()) {
            String text = Math.round(timer.getElapsedTime() / 1000) + " seconds";
            float width = Heliodor.INSTANCE().google.getWidth(text) + 5;
            int height = 20;
            int posX = 2;
            int posY = 100;
            Render2DUtil.drawRect(posX, posY, posX + width + 2, posY + height, new Color(5, 5, 5, 255).getRGB());
            Render2DUtil.drawBorderedRect(posX + .5, posY + .5, posX + width + 1.5, posY + height - .5, 0.5, new Color(40, 40, 40, 255).getRGB(), new Color(60, 60, 60, 255).getRGB(), true);
            Render2DUtil.drawBorderedRect(posX + 2, posY + 2, posX + width, posY + height - 2, 0.5, new Color(22, 22, 22, 255).getRGB(), new Color(60, 60, 60, 255).getRGB(), true);
            Render2DUtil.drawRect(posX + 2.5, posY + 2.5, posX + width - .5, posY + 4.5, new Color(9, 9, 9, 255).getRGB());
            Render2DUtil.drawGradientSideways(4, posY + 3, 4 + (width / 3), posY + 4, new Color(81, 149, 219, 255).getRGB(), new Color(180, 49, 218, 255).getRGB());
            Render2DUtil.drawGradientSideways(4 + (width / 3), posY + 3, 4 + ((width / 3) * 2), posY + 4, new Color(180, 49, 218, 255).getRGB(), new Color(236, 93, 128, 255).getRGB());
            Render2DUtil.drawGradientSideways(4 + ((width / 3) * 2), posY + 3, ((width / 3) * 3) + 1, posY + 4, new Color(236, 93, 128, 255).getRGB(), new Color(167, 171, 90, 255).getRGB());
            Heliodor.INSTANCE().google.drawString(text, 5, posY + 6, -1);
        }
    }

    /*
    Credit: Sigma for Rotations
     */
    public float[] getRotations(BlockPos block, EnumFacing face){
        double x = block.getX() + 0.5 - mc.thePlayer.posX + (double)face.getFrontOffsetX()/2;
        double z = block.getZ() + 0.5 - mc.thePlayer.posZ + (double)face.getFrontOffsetZ()/2;
        double d1 = mc.thePlayer.posY + mc.thePlayer.getEyeHeight() -(block.getY() + 0.5);
        double d3 = MathHelper.sqrt_double(x * x + z * z);
        float yaw = (float)(Math.atan2(z, x) * 180.0D / Math.PI) - 90.0F;
        float pitch = (float)(Math.atan2(d1, d3) * 180.0D / Math.PI);
        if(yaw < 0.0F){
            yaw += 360f;
        }
        return  new float[]{yaw, pitch};
    }
}
