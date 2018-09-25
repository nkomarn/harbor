package techtoolbox.Harbor.Actionbar;

import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_8_R2.IChatBaseComponent;
import net.minecraft.server.v1_8_R2.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R2.PacketPlayOutChat;

public class Actionbar_1_8_R2 implements Actionbar {

	@Override
	public void sendActionbar(Player player, String message) {
	    IChatBaseComponent titleComponent = ChatSerializer.a("{\"text\": \"" + ChatColor.translateAlternateColorCodes('&', message) + "\"}");
	    PacketPlayOutChat titlePacket = new PacketPlayOutChat(titleComponent, (byte)2);
	    ((CraftPlayer) player).getHandle().playerConnection.sendPacket(titlePacket);
	}
}
