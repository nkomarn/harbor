package xyz.nkomarn.Harbor.nms;

import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_8_R2.IChatBaseComponent;
import net.minecraft.server.v1_8_R2.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R2.PacketPlayOutChat;
import net.minecraft.server.v1_8_R2.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R2.PacketPlayOutTitle.EnumTitleAction;
import xyz.nkomarn.Harbor.nms.NMS;

class NMS_1_8_R2 implements NMS {
	public void sendActionbar(Player player, String message) {
		IChatBaseComponent titleComponent = ChatSerializer.a("{\"text\": \"" + ChatColor.translateAlternateColorCodes('&', message) + "\"}");
		PacketPlayOutChat titlePacket = new PacketPlayOutChat(titleComponent, (byte) 2);
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(titlePacket);
	}

	public void sendJSONMessage(Player player, String json) {
		IChatBaseComponent component = ChatSerializer.a(json);
		PacketPlayOutChat packet = new PacketPlayOutChat(component);
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
	}

	public void sendTitle(Player player, String top, String bottom) {
		IChatBaseComponent titleTop = ChatSerializer.a("{\"text\": \"" + ChatColor.translateAlternateColorCodes('&', top) + "\"}");
		IChatBaseComponent titleBottom = ChatSerializer.a("{\"text\": \"" + ChatColor.translateAlternateColorCodes('&', bottom) + "\"}");
		PacketPlayOutTitle title = new PacketPlayOutTitle(EnumTitleAction.TITLE, titleTop);
		PacketPlayOutTitle subtitle = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, titleBottom);
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(title);
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(subtitle);
	}
}
