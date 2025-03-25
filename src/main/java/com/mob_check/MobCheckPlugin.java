package com.mob_check;

import lombok.Getter;
import net.runelite.api.Actor;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.Player;
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

/**
 * A RuneLite plugin that tracks NPC attack animations and shows a tick countdown
 * for when tracked NPCs will next attack.
 */
@PluginDescriptor(name = "Mob Check Plugin")
public class MobCheckPlugin extends Plugin
{
	// Injected overlay instance to display the attack tick countdown above NPCs
	@Inject
	private MobCheckOverlay overlay;

	// OverlayManager handles adding and removing overlays
	@Inject
	private OverlayManager overlayManager;

	@Inject
	private Client client;


	/**
	 * Map to track how many ticks remain until the NPC attacks again.
	 * Key = NPC index (unique identifier for each NPC instance in-game)
	 * Value = Ticks remaining until next attack
	 */
	@Getter
	private final Map<Integer, Integer> npcNextAttackTickMap = new HashMap<>();

	/**
	 * Set of animation IDs that correspond to known attack animations.
	 * These IDs trigger the start of an attack cooldown for the NPC.
	 */
	private static final Set<Integer> ATTACK_ANIMATIONS = Set.of(
			2309, // Abyssal demon
			1537, // Abyssal demon (alt)
			1538, // Abyssal demon (alt)
			1552  // Mutated Bloodveld
	);

	/**
	 * Called when the plugin starts. Initializes state and adds overlay.
	 */
	@Override
	protected void startUp()
	{
		npcNextAttackTickMap.clear();          // Clear previous tick data
		overlayManager.add(overlay);           // Add overlay to client
		System.out.println("✅ MobCheckPlugin Started");
	}

	/**
	 * Called when the plugin stops. Cleans up state and removes overlay.
	 */
	@Override
	protected void shutDown()
	{
		npcNextAttackTickMap.clear();          // Clear state
		overlayManager.remove(overlay);        // Remove overlay from client
		System.out.println("MobCheckPlugin Stopped");
	}

	/**
	 * Triggered whenever an animation changes for any actor (player or NPC).
	 * We're only interested in NPCs and specifically their attack animations.
	 */

	@Subscribe
	public void onAnimationChanged(AnimationChanged event)
	{
		if (!(event.getActor() instanceof NPC))
		{
			return;
		}

		NPC npc = (NPC) event.getActor();
		Actor target = npc.getInteracting();

		// Only log if it's attacking you
		if (target == client.getLocalPlayer())
		{
			System.out.println("🎯 Attacker Animation Update: " + npc.getName() +
					" | ID: " + npc.getId() +
					" | Animation: " + npc.getAnimation() +
					" | Index: " + npc.getIndex());
		}
	}



	@Subscribe
	public void onGameTick(GameTick tick)
	{
		NPC attacker = getAttackingNpc();

		if (attacker != null)
		{
			String name = attacker.getName();
			int id = attacker.getId();
			int anim = attacker.getAnimation();
			int index = attacker.getIndex();

			System.out.println("🔴 Attacking NPC: " + name + " | ID: " + id + " | Animation: " + anim + " | Index: " + index);
		}
	}

	private NPC getAttackingNpc()
	{
		Player localPlayer = client.getLocalPlayer();
		if (localPlayer == null)
		{
			return null;
		}

		for (NPC npc : client.getNpcs())
		{
			Actor target = npc.getInteracting();
			if (target == localPlayer)
			{
				return npc;
			}
		}

		return null;
	}
	/**
	 * Returns the known attack speed (in ticks) for specific NPC IDs.
	 * If the NPC ID isn't known, it returns a default value of 4.
	 */
	private int getAttackSpeedForNpc(int npcId)
	{
		switch (npcId)
		{
			case 2652:
				return 10;
			case 2655:
				return 10;
			case 7241: // Abyssal demon
				return 4;
			case 7276: // Mutated Bloodveld
				return 5;
			default:   // Fallback/default for unknown NPCs
				return 4;
		}
	}
}













//	@Subscribe
//	public void onGameTick(GameTick event) {
//		npcNextAttackTickMap.replaceAll((npcIndex, ticks) -> ticks > 0 ? ticks - 1 : 0);
//
//		// Optional: log countdowns
//		for (Map.Entry<Integer, Integer> entry : npcNextAttackTickMap.entrySet())
//		{
//			if (entry.getValue() > 0)
//			{
//				System.out.println("NPC Index " + entry.getKey() + " - Ticks until next attack: " + entry.getValue());
//			}
//		}
//	}

//	@Subscribe
//	public void onAnimationChanged(AnimationChanged event)
//	{
//		// Ignore if the actor isn't an NPC
//		if (!(event.getActor() instanceof NPC))
//		{
//			return;
//		}
//
//		NPC npc = (NPC) event.getActor();
//		int animationId = npc.getAnimation();
//		int npcIndex = npc.getIndex();
//
//		// If this is an attack animation we're tracking, reset its attack cooldown
//		if (animationId != -1 && ATTACK_ANIMATIONS.contains(animationId))
//		{
//			int attackSpeed = getAttackSpeedForNpc(npc.getId()); // Get known attack speed
//			npcNextAttackTickMap.put(npcIndex, attackSpeed);     // Start countdown for this NPC
//			System.out.println("💥 Tracking " + npc.getName() + " - Attack in " + attackSpeed + " ticks");
//		}
//	}


//@Subscribe
//public void onNpcAnimationChanged(NpcAnimationChanged event)
//{
//	NPC npc = event.getNpc();
//
//	if (npc.getInteracting() == client.getLocalPlayer())
//	{
//		int id = npc.getId();
//		int anim = npc.getAnimation();
//
//		System.out.println("🔥 NpcAnimationChanged: " + npc.getName()
//				+ " | ID: " + id
//				+ " | Animation: " + anim
//				+ " | Index: " + npc.getIndex());
//	}
//}