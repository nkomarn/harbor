package mykyta.Harbor.NMS;

import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_13_R2.IChatBaseComponent;
import net.minecraft.server.v1_13_R2.PacketPlayOutChat;
import net.minecraft.server.v1_13_R2.PacketPlayOutTitle;
import net.minecraft.server.v1_13_R2.IChatBaseComponent.ChatSerializer;

public class NMS_1_13_R2 implements NMS {
	@Override
	public void sendActionbar(Player player, String message) {
	    IChatBaseComponent titleComponent = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + ChatColor.translateAlternateColorCodes('&', message) + "\"}");
	    PacketPlayOutTitle titlePacket = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.ACTIONBAR, titleComponent);
	    ((CraftPlayer) player).getHandle().playerConnection.sendPacket(titlePacket);  
	}

	@Override
	public void sendJSONMessage(Player player, String json) {
		IChatBaseComponent component = ChatSerializer.a(json);
		PacketPlayOutChat packet = new PacketPlayOutChat(component);
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
	}
}
