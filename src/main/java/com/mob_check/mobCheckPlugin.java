package com.mob_check;

import com.google.inject.Provides;
import net.runelite.client.config.ConfigManager;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.events.*;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@PluginDescriptor(
	name = "mobCheckPlugin"
)
public class mobCheckPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private OverlayManager overlayManager;

	@Inject
	private mobCheckOverlay overlay;

	// Tracks next attack tick for each NPC
	private final Map<NPC, Integer> npcNextAttackTickMap = new HashMap<>();

	// Jal-Xil Constants
	private static final int JAL_XIL_ID = 7604; // Ranger NPC ID
	private static final int JAL_XIL_ATTACK_ANIMATION = 7566;
	private static final int JAL_XIL_ATTACK_SPEED = 4; // 4 ticks per attack

	@Override
	protected void startUp()
	{
		overlayManager.add(overlay);
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(overlay);
		npcNextAttackTickMap.clear();
	}

	@Subscribe
	public void onAnimationChanged(AnimationChanged event)
	{
		if (!(event.getActor() instanceof NPC)) {
			return;
		}

		NPC npc = (NPC) event.getActor();

		if (npc.getId() != JAL_XIL_ID) {
			return;
		}

		if (npc.getAnimation() == JAL_XIL_ATTACK_ANIMATION) {
			int nextAttackTick = client.getTickCount() + JAL_XIL_ATTACK_SPEED;
			npcNextAttackTickMap.put(npc, nextAttackTick);
		}
	}

	@Subscribe
	public void onNpcDespawned(NpcDespawned event)
	{
		npcNextAttackTickMap.remove(event.getNpc());
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		// Clean up expired NPCs
		npcNextAttackTickMap.entrySet().removeIf(entry -> entry.getKey().isDead());
	}

	public Map<NPC, Integer> getNpcNextAttackTickMap()
	{
		return npcNextAttackTickMap;
	}
}


