package techtoolbox.Harbor;

import org.bukkit.Bukkit;

public class ActionBarMessage implements Runnable {
	@Override
	public void run() { 
<<<<<<< HEAD
		for (int i = 0; Main.worlds.size() > i; i++) {
			if (((Main.worlds.get(Bukkit.getServer().getWorlds().get(i))).intValue() > 0) && ((Main.worlds.get(Bukkit.getServer().getWorlds().get(i))).intValue() < (Bukkit.getServer().getWorlds().get(i)).getPlayers().size() - Main.bypassers.size())) {
				Main.sendActionbar("playersInBed", Bukkit.getServer().getWorlds().get(i));
			}
			else if (((Main.worlds.get(Bukkit.getServer().getWorlds().get(i))).intValue() == (Bukkit.getServer().getWorlds().get(i)).getPlayers().size() - Main.bypassers.size()) && ((Bukkit.getServer().getWorlds().get(i)).getPlayers().size() - Main.bypassers.size() > 1)) {
				Main.sendActionbar("everyoneSleeping", Bukkit.getServer().getWorlds().get(i));
=======
		try {
			for (int i = 0; Main.worlds.size() > i; i++) {
				if (((Main.worlds.get(Bukkit.getServer().getWorlds().get(i))).intValue() > 0) && ((Main.worlds.get(Bukkit.getServer().getWorlds().get(i))).intValue() < (Bukkit.getServer().getWorlds().get(i)).getPlayers().size() - Main.bypassers.size())) {
					Main.sendActionbar("playersInBed", Bukkit.getServer().getWorlds().get(i), null);
				}
				else if (((Main.worlds.get(Bukkit.getServer().getWorlds().get(i))).intValue() == (Bukkit.getServer().getWorlds().get(i)).getPlayers().size() - Main.bypassers.size()) && ((Bukkit.getServer().getWorlds().get(i)).getPlayers().size() - Main.bypassers.size() > 1)) {
					Main.sendActionbar("everyoneSleeping", Bukkit.getServer().getWorlds().get(i), null);
				}
>>>>>>> Update 1.4.4, I guess.
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
}
