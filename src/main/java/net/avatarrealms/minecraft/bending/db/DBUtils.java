package net.avatarrealms.minecraft.bending.db;

import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import net.avatarrealms.minecraft.bending.abilities.BendingPlayer;
import net.avatarrealms.minecraft.bending.abilities.BendingPlayerData;
import net.avatarrealms.minecraft.bending.db.impl.FlatFileDB;
import net.avatarrealms.minecraft.bending.db.impl.MongoDB;

public class DBUtils {
	public static IBendingDB choose(String key) {
		//TODO rework this awful statement
		if(key.equals("flatfile")) {
			return new FlatFileDB();
		} else if(key.equals("mongodb")) {
			return new MongoDB();
		}
		return null;
	}

	public static void convert(IBendingDB src, IBendingDB dest) {
		Map<UUID, BendingPlayerData> dump = src.dump();
		dest.clear();
		for(Entry<UUID, BendingPlayerData> entry : dump.entrySet()) {
			dest.set(entry.getKey(), new BendingPlayer(entry.getValue()));
		}
	}
}
