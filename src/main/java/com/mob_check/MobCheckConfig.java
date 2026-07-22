package com.mob_check;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("mobcheck")
public interface MobCheckConfig extends Config
{
	@ConfigItem(
		keyName = "showOverhead",
		name = "Show Overhead",
		description = "Toggles rendering the prayer icon and countdown ticks above the player's head",
		position = 1
	)
	default boolean showOverhead()
	{
		return true;
	}

	@ConfigItem(
		keyName = "showInfoBox",
		name = "Show Info Box",
		description = "Toggles rendering the next prayer style overlay panel",
		position = 2
	)
	default boolean showInfoBox()
	{
		return true;
	}

	@ConfigItem(
		keyName = "playSoundAlert",
		name = "Play Sound Alert",
		description = "Plays a sound effect when the priority prayer style changes",
		position = 3
	)
	default boolean playSoundAlert()
	{
		return true;
	}

	@ConfigItem(
		keyName = "soundEffectId",
		name = "Sound Effect ID",
		description = "The RuneLite Sound Effect ID to play on priority style changes",
		position = 4
	)
	default int soundEffectId()
	{
		// Default to GE_INCREMENT_PLOP (2266)
		return 2266;
	}

	@ConfigItem(
		keyName = "warningThreshold",
		name = "Warning Threshold",
		description = "Number of remaining ticks before the overlay turns red to indicate urgency",
		position = 5
	)
	default int warningThreshold()
	{
		return 1;
	}

	@ConfigItem(
		keyName = "trackUnknownProjectiles",
		name = "Track Unknown Projectiles",
		description = "Fallback to Pray Magic for projectiles targeting you that are not in the database",
		position = 6
	)
	default boolean trackUnknownProjectiles()
	{
		return false;
	}

	@ConfigItem(
		keyName = "enableColosseum",
		name = "Enable Fortis Colosseum Tracking",
		description = "Enables tracking for Fortis Colosseum monsters (Manticore, Serpent Shaman, Javelinic Colossus, etc.)",
		position = 7
	)
	default boolean enableColosseum()
	{
		return true;
	}
}
