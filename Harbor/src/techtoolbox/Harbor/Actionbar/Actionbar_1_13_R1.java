package techtoolbox.Harbor.Actionbar;

import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.v1_13_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import net.minecraft.server.v1_13_R1.IChatBaseComponent;
import net.minecraft.server.v1_13_R1.PacketPlayOutTitle;

public class Actionbar_1_13_R1 implements Actionbar {

	@Override
	public void sendActionbar(Player player, String message) {
	    IChatBaseComponent titleComponent = IChatBaseComponent.ChatSerializer.a("{\"text\": \"" + ChatColor.translateAlternateColorCodes('&', message) + "\"}");
	    PacketPlayOutTitle titlePacket = new PacketPlayOutTitle(PacketPlayOutTitle.EnumTitleAction.ACTIONBAR, titleComponent);
	    ((CraftPlayer) player).getHandle().playerConnection.sendPacket(titlePacket);  
	}
}
