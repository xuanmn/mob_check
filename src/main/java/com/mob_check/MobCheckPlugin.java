package com.mob_check;

import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@PluginDescriptor(
		name = "Mob Check Plugin"
)
public class MobCheckPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private MobCheckOverlay overlay;

	@Inject
	private OverlayManager overlayManager;

	// Maps NPC index to ticks until next attack
	private final Map<Integer, Integer> npcNextAttackTickMap = new HashMap<>();

	// Example: Inferno NPCs attack animation IDs (you can expand this list)
	private static final Set<Integer> ATTACK_ANIMATIONS = Set.of(
			2309, // Abyssal demon
			1537, // Abyssal demon
			1538, // Abyssal demon
			1552  // Mutated Bloodveld
	);

	@Override
	protected void startUp() throws Exception
	{
		System.out.println("✅ MobCheckPlugin Started");
		npcNextAttackTickMap.clear();
		overlayManager.add(overlay);
	}

	@Override
	protected void shutDown() throws Exception
	{
		System.out.println("MobCheckPlugin Stopped");
		npcNextAttackTickMap.clear();
		overlayManager.remove(overlay);
	}

	@Subscribe
	public void onAnimationChanged(AnimationChanged event)
	{
		if (!(event.getActor() instanceof NPC))
		{
			return;
		}

		NPC npc = (NPC) event.getActor();
		int animationId = npc.getAnimation();
		int npcIndex = npc.getIndex();
		String name = npc.getName();
		int npcId = npc.getId();

		System.out.println("🎯 NPC: " + name + " | ID: " + npcId + " | Index: " + npcIndex + " | Animation: " + animationId);

		// Log only if this is a new animation
		if (animationId != -1 && ATTACK_ANIMATIONS.contains(animationId))
		{
			int attackSpeed = getAttackSpeedForNpc(npcId);
			npcNextAttackTickMap.put(npcIndex, attackSpeed);

			System.out.println("💥 Tracking " + name + " (ID: " + npcId + ") - Attack in " + attackSpeed + " ticks");
		}
	}

	@Subscribe
	public void onGameTick(GameTick event) {
		npcNextAttackTickMap.replaceAll((npcIndex, ticks) -> ticks > 0 ? ticks - 1 : 0);

		// Optional: log countdowns
		for (Map.Entry<Integer, Integer> entry : npcNextAttackTickMap.entrySet())
		{
			if (entry.getValue() > 0)
			{
				System.out.println("NPC Index " + entry.getKey() + " - Ticks until next attack: " + entry.getValue());
			}
		}
	}

	/**
	 * Returns the attack speed in ticks for known NPCs.
	 * You can expand this with more accurate data.
	 */
	private int getAttackSpeedForNpc(int npcId)
	{
		switch (npcId)
		{
			case 7241: // Abyssal demon
				return 4;
			case 7276: // Mutated Bloodveld
				return 5;
			default:
				return 4; // Default
		}
	}

	public Map<Integer, Integer> getNpcNextAttackTickMap()
	{
		return npcNextAttackTickMap;
	}
}