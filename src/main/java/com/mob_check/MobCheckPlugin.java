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
			7582, // Jal-MejRah
			7593, // Jal-Ak
			7604, // Jal-Xil
			7610, // Jal-Zek
			7618  // TzKal-Zuk
	);

	@Override
	protected void startUp() throws Exception
	{
		System.out.println("MobCheckPlugin Started");
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

		if (ATTACK_ANIMATIONS.contains(animationId))
		{
			int attackSpeed = getAttackSpeedForNpc(npc.getId());

			npcNextAttackTickMap.put(npcIndex, attackSpeed);

			System.out.println("NPC Attacking: " + npc.getName() + " (ID: " + npc.getId() + ") - Animation: " + animationId + ", Next attack in " + attackSpeed + " ticks.");
		}
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
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
			case 7706: // Jal-MejRah
				return 6;
			case 7707: // Jal-Ak
				return 4;
			case 7708: // Jal-Xil
				return 4;
			case 7709: // Jal-Zek
				return 6;
			case 7710: // TzKal-Zuk
				return 10;
			default:
				return 4; // Default fallback
		}
	}

	public Map<Integer, Integer> getNpcNextAttackTickMap()
	{
		return npcNextAttackTickMap;
	}
}