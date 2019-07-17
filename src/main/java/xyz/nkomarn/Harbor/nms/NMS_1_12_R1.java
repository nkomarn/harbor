package xyz.nkomarn.Harbor.nms;

import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_12_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_12_R1.IChatBaseComponent;
import net.minecraft.server.v1_12_R1.PacketPlayOutChat;
import net.minecraft.server.v1_12_R1.PacketPlayOutTitle;
import net.minecraft.server.v1_12_R1.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_12_R1.PacketPlayOutTitle.EnumTitleAction;

class NMS_1_12_R1 implements NMS {
	public void sendActionbar(Player player, String message) {
	    IChatBaseComponent titleComponent = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + ChatColor.translateAlternateColorCodes('&', message) + "\"}");
	    PacketPlayOutTitle titlePacket = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.ACTIONBAR, titleComponent);
	    ((CraftPlayer) player).getHandle().playerConnection.sendPacket(titlePacket);  
	}

	public void sendJSONMessage(Player player, String json) {
		IChatBaseComponent component = ChatSerializer.a(json);
		PacketPlayOutChat packet = new PacketPlayOutChat(component);
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
	}

	public void sendTitle(Player player, String top, String bottom) {
		IChatBaseComponent titleComponentTop = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + ChatColor.translateAlternateColorCodes('&', top) + "\"}");
		IChatBaseComponent titleComponentBottom = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + ChatColor.translateAlternateColorCodes('&', bottom) + "\"}");
		PacketPlayOutTitle title = new PacketPlayOutTitle(EnumTitleAction.TITLE, titleComponentTop);
		PacketPlayOutTitle subtitle = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, titleComponentBottom);
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(title);
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(subtitle);
	}
}
