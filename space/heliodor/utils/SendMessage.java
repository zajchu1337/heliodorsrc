package space.heliodor.utils;

import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;

public class SendMessage {
    static Minecraft mc = Minecraft.getMinecraft();

    public static void log(String msg) {
        if (mc.thePlayer != null && mc.theWorld != null) {
            mc.thePlayer.addChatMessage(new ChatComponentText("§6" + "Heliodor" + "§7: " + msg));
        }

    }
}
