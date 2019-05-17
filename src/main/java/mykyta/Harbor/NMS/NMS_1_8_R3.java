package mykyta.Harbor.NMS;

import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.IChatBaseComponent.ChatSerializer;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle;
import net.minecraft.server.v1_8_R3.PacketPlayOutTitle.EnumTitleAction;

public class NMS_1_8_R3 implements NMS {
	@Override
	public void sendActionbar(Player player, String message) {
		IChatBaseComponent titleComponent = ChatSerializer.a("{\"text\": \"" + ChatColor.translateAlternateColorCodes('&', message) + "\"}");
	    PacketPlayOutChat titlePacket = new PacketPlayOutChat(titleComponent, (byte)2);
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(titlePacket);
	}

	@Override
	public void sendJSONMessage(Player player, String json) {
		IChatBaseComponent component = ChatSerializer.a(json);
		PacketPlayOutChat packet = new PacketPlayOutChat(component);
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
	}

	@Override
	public void sendTitle(Player player, String top, String bottom) {
		IChatBaseComponent titleTop = ChatSerializer.a("{\"text\": \"" + ChatColor.translateAlternateColorCodes('&', top) + "\"}");
		IChatBaseComponent titleBottom = ChatSerializer.a("{\"text\": \"" + ChatColor.translateAlternateColorCodes('&', bottom) + "\"}");
		PacketPlayOutTitle title = new PacketPlayOutTitle(EnumTitleAction.TITLE, titleTop);
		PacketPlayOutTitle subtitle = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, titleBottom);
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(title);
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(subtitle);
	}

	@Override
	public void enterBed(Player player) {
		 
	}
}
