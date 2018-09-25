package techtoolbox.Harbor.Actionbar;

import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_12_R1.ChatMessageType;
import net.minecraft.server.v1_12_R1.IChatBaseComponent;
import net.minecraft.server.v1_12_R1.PacketPlayOutChat;

public class Actionbar_1_12_R1 implements Actionbar {

	@Override
	public void sendActionbar(Player player, String message) {
	    
	    IChatBaseComponent titleComponent = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + ChatColor.translateAlternateColorCodes('&', message) + "\"}");
	    PacketPlayOutChat titlePacket = new PacketPlayOutChat(titleComponent, ChatMessageType.GAME_INFO);
	    ((CraftPlayer) player).getHandle().playerConnection.sendPacket(titlePacket);
	}
}
