package mykyta.Harbor.NMS;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.inventivetalent.bossbar.BossBarAPI;

import net.md_5.bungee.api.chat.TextComponent;
import net.minecraft.server.v1_7_R1.ChatSerializer;
import net.minecraft.server.v1_7_R1.IChatBaseComponent;
import net.minecraft.server.v1_7_R1.PacketPlayOutChat;

public class NMS_1_7_R1 implements NMS {
	@Override
	public void sendActionbar(Player player, String message) {
		if (Bukkit.getServer().getPluginManager().getPlugin("BossBarAPI") == null) System.out.println("API missing");
		else BossBarAPI.addBar(player, new TextComponent(ChatColor.translateAlternateColorCodes('&', message)), BossBarAPI.Color.RED, BossBarAPI.Style.PROGRESS, 1.0f, 20, 5); 
	}

	@Override
	public void sendJSONMessage(Player player, String json) {
		IChatBaseComponent component = ChatSerializer.a(json);
		PacketPlayOutChat packet = new PacketPlayOutChat(component);
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
	}

	@Override
	public void sendTitle(Player player, String top, String bottom) {
		/*IChatBaseComponent titleTop = ChatSerializer.a("{\"text\": \"" + ChatColor.translateAlternateColorCodes('&', top) + "\"}");
		IChatBaseComponent titleBottom = ChatSerializer.a("{\"text\": \"" + ChatColor.translateAlternateColorCodes('&', bottom) + "\"}");
		PacketPlayOutTitle title = new PacketPlayOutTitle(EnumTitleAction.TITLE, titleTop);
		PacketPlayOutTitle subtitle = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, titleBottom);
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(title);
		((CraftPlayer) player).getHandle().playerConnection.sendPacket(subtitle);*/
	}

	@Override
	public void enterBed(Player player) {
		 
	}
}
