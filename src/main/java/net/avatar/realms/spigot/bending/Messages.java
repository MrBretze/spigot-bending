package net.avatar.realms.spigot.bending;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.avatar.realms.spigot.bending.abilities.Abilities;
import net.avatar.realms.spigot.bending.controller.LanguageParameter;

public class Messages {
	
	@LanguageParameter ("command.not_console")
	public static String NOT_CONSOLE_COMMAND = "This command cannot be executed by the console.";
	
	@LanguageParameter ("command.no_permission")
	public static String NO_PERMISSION = "You do not have the permission to execute that command.";
	
	@LanguageParameter ("command.invalid_ability")
	public static String INVALID_ABILITY = "This is an invalid ability.";

	@LanguageParameter ("command.not_element")
	public static String NOT_HAVE_ELEMENT = "You do not have the proper element : ";
	
	@LanguageParameter ("command.ability_bound")
	public static String ABILITY_BOUND = "{0} bound to slot {1}.";

	private static final String FILENAME = "messages.properties";

	private static Properties lines;
	private static File languageFile;
	private static InputStream input;

	public static void loadMessages () {
		lines = new Properties();

		File folder = Bending.plugin.getDataFolder();
		if (!folder.exists()) {
			folder.mkdir();
		}

		languageFile = new File(folder + File.separator + FILENAME);
		if (languageFile.exists()) {
			try {
				input = new FileInputStream(languageFile);
			}
			catch (Exception e) {
				input = Messages.class.getClassLoader().getResourceAsStream(FILENAME);
			}
		}
		else {
			input = Messages.class.getClassLoader().getResourceAsStream(FILENAME);
		}

		try {
			lines.load(input);
		}
		catch (IOException e) {
			e.printStackTrace();
			// Should never happen, hope so
		}

	}

	public static String getAbilityDescription (Abilities ability) {
		return lines.getProperty("abilities." + ability.getElement().name().toLowerCase() + "." + ability.name().toLowerCase());
	}

	public static String getString (String string) {
		return lines.getProperty(string);
	}

	public static void sendMessage (Player player, String key) {
		sendMessage(player, key, ChatColor.WHITE);
	}

	public static void sendMessage (Player player, String key, ChatColor color) {
		if (player == null) {
			Bending.plugin.getLogger().info(color + getString(key));
		}
		else {
			player.sendMessage(color + getString(key));
		}
	}

	// These one are not used yet, but we should.
	public static void sendMessage (Player player, String key, Map<String, String> params) {
		sendMessage(player, key, ChatColor.WHITE, params);
	}

	public static void sendMessage (Player player, String key, ChatColor color, Map<String, String> params) {
		String msg = getString(key);
		for (String k : params.keySet()) {
			msg.replaceAll("%" + k + "%", params.get(k));
		}
		if (player == null) {
			Bending.plugin.getLogger().info(color + msg);
		}
		else {
			player.sendMessage(color + msg);
		}
	}

}
