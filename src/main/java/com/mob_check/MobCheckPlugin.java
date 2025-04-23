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
	//	@Getter
	//
 	//
 	//
 	//
 	//
 	//
	//	private final Map<Integer, Integer> npcNextAttackTickMap = new HashMap<>();
	public Map<Integer, Integer> getNpcNextAttackTickMap()
	{
		return npcNextAttackTickMap;
	}
}






//package com.mob_check;
//
//import lombok.Getter;
//import net.runelite.api.Actor;
//import net.runelite.api.Client;
//import net.runelite.api.NPC;
//import net.runelite.api.Player;
//import net.runelite.api.events.AnimationChanged;
//import net.runelite.api.events.GameTick;
//import net.runelite.client.eventbus.Subscribe;
//import net.runelite.client.plugins.Plugin;
//import net.runelite.client.plugins.PluginDescriptor;
//import net.runelite.client.ui.overlay.OverlayManager;
//
//import javax.inject.Inject;
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Set;
//
///**
// * RuneLite plugin that tracks NPCs attacking the local player and logs
// * their animations and time until next attack.
// */
//@PluginDescriptor(name = "Mob Check Plugin")
//public class MobCheckPlugin extends Plugin
//{
//	@Inject private MobCheckOverlay overlay;
//	@Inject private OverlayManager overlayManager;
//	@Inject private Client client;
//
//	private final Map<Integer, Integer> lastAttackTick = new HashMap<>();
//	private int currentTick = 0;
//
//	/**
//	 * Map to track NPC index to ticks left until next attack.
//	 */
//	@Getter
//	private final Map<Integer, Integer> npcNextAttackTickMap = new HashMap<>();
//
//	/**
//	 * Set of known attack animations.
//	 */
//	private static final Set<Integer> ATTACK_ANIMATIONS = Set.of(
//			2309, // Abyssal demon
//			1537, 1538, // Alt animations
//			7590, 7591, // JalTok-Jad (Magic, Ranged)
//			1552  // Mutated Bloodveld
//	);
//
//	@Override
//	protected void startUp()
//	{
//		npcNextAttackTickMap.clear();
//		overlayManager.add(overlay);
//		System.out.println("✅ MobCheckPlugin Started");
//	}
//
//	@Override
//	protected void shutDown()
//	{
//		npcNextAttackTickMap.clear();
//		overlayManager.remove(overlay);
//		System.out.println("🛑 MobCheckPlugin Stopped");
//	}
//
//	/**
//	 * Called when any actor's animation changes.
//	 * Filters to only NPCs attacking the player.
//	 */
//
//	@Subscribe
//	public void onAnimationChanged(AnimationChanged event)
//	{
//		if (!(event.getActor() instanceof NPC)) return;
//
//		NPC npc = (NPC) event.getActor();
//		if (npc.getInteracting() != client.getLocalPlayer()) return;
//
//		int animation = npc.getAnimation();
//		int index = npc.getIndex();
//
//		if (ATTACK_ANIMATIONS.contains(animation))
//		{
//			Integer lastTick = lastAttackTick.get(index);
//			if (lastTick != null)
//			{
//				int delay = currentTick - lastTick;
//				System.out.println("⏱️ " + npc.getName() + " attacked again after " + delay + " ticks.");
//			}
//			lastAttackTick.put(index, currentTick);
//			System.out.println("🎯 Attacker Animation Update: " + npc.getName()
//					+ " | ID: " + npc.getId()
//					+ " | Animation: " + animation
//					+ " | Index: " + index);
//		}
//	}
//	/**
//	 * Every tick, check who is attacking us and print info.
//	 */
//	@Subscribe
//	public void onGameTick(GameTick tick)
//	{
//		currentTick++;
//
//		NPC attacker = getAttackingNpc();
//		if (attacker != null)
//		{
//			int id = attacker.getId();
//			int index = attacker.getIndex();
//			int anim = attacker.getAnimation();
//
//			System.out.println("🔴 Attacking NPC: " + attacker.getName() + " | ID: " + id + " | Animation: " + anim + " | Index: " + index);
//		}
//	}
//
//	/**
//	 * Finds the NPC currently attacking the local player.
//	 */
//	private NPC getAttackingNpc()
//	{
//		Player localPlayer = client.getLocalPlayer();
//		if (localPlayer == null)
//		{
//			return null;
//		}
//
//		for (NPC npc : client.getNpcs())
//		{
//			Actor target = npc.getInteracting();
//			if (target == localPlayer)
//			{
//				return npc;
//			}
//		}
//		return null;
//	}
//
//	/**
//	 * Returns known attack speed in ticks for specific NPCs.
//	 */
//	private int getAttackSpeedForNpc(int npcId)
//	{
//		switch (npcId)
//		{
//			case 10623: return 10; // JalTok-Jad
//			case 7241: return 4;   // Abyssal demon
//			case 7276: return 5;   // Mutated Bloodveld
//			default: return 4;
//		}
//	}
//}












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