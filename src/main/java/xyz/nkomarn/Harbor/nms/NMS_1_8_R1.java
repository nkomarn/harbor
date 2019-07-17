package xyz.nkomarn.Harbor.nms;

import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_8_R1.ChatSerializer;
import net.minecraft.server.v1_8_R1.EnumTitleAction;
import net.minecraft.server.v1_8_R1.IChatBaseComponent;
import net.minecraft.server.v1_8_R1.PacketPlayOutChat;
import net.minecraft.server.v1_8_R1.PacketPlayOutTitle;
import xyz.nkomarn.Harbor.nms.NMS;

class NMS_1_8_R1 implements NMS {
	public void sendActionbar(Player p, String m) {
		IChatBaseComponent tc = ChatSerializer.a("{\"text\": \"" + ChatColor.translateAlternateColorCodes('&', m) + "\"}");
	    PacketPlayOutChat tp = new PacketPlayOutChat(tc, (byte) 2);
		((CraftPlayer) p).getHandle().playerConnection.sendPacket(tp);
	}

	public void sendJSONMessage(Player p, String j) {
		IChatBaseComponent c = ChatSerializer.a(j);
		PacketPlayOutChat packet = new PacketPlayOutChat(c);
		((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
	}

	public void sendTitle(Player p, String t, String b) {
		IChatBaseComponent tt = ChatSerializer.a("{\"text\": \"" + ChatColor.translateAlternateColorCodes('&', t) + "\"}");
		IChatBaseComponent tm = ChatSerializer.a("{\"text\": \"" + ChatColor.translateAlternateColorCodes('&', b) + "\"}");
		PacketPlayOutTitle x = new PacketPlayOutTitle(EnumTitleAction.TITLE, tt);
		PacketPlayOutTitle y = new PacketPlayOutTitle(EnumTitleAction.SUBTITLE, tm);
		((CraftPlayer) p).getHandle().playerConnection.sendPacket(x);
		((CraftPlayer) p).getHandle().playerConnection.sendPacket(y);
	}
}
