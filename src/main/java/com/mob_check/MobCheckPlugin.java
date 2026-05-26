package com.mob_check;

import net.runelite.api.*;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ProjectileMoved;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;
import java.util.*;

@PluginDescriptor(name = "Mob Check Plugin", description = "Dynamic PvM Priority Prayer Helper", tags = { "pvm",
		"prayer", "dynamic", "projectiles", "sound" })
public class MobCheckPlugin extends Plugin {
	@Inject
	private Client client;

	@Inject
	private MobCheckOverlay overlay;

	@Inject
	private OverlayManager overlayManager;

	public static class AttackState {
		public int ticks;
		public String style;
		public String npcName;

		public AttackState(int ticks, String style, String npcName) {
			this.ticks = ticks;
			this.style = style;
			this.npcName = npcName;
		}
	}

	private final Map<Integer, AttackState> npcNextAttackTickMap = new HashMap<>();
	private String lastPriorityStyle = "";

	// Known projectile mapping for dynamic tracking
	private static final Map<Integer, String> PROJECTILE_STYLES = new HashMap<>();
	static {
		// Inferno
		PROJECTILE_STYLES.put(1374, "Pray Magic"); // Jal-Zek
		PROJECTILE_STYLES.put(1376, "Pray Range"); // Jal-Xil
		PROJECTILE_STYLES.put(448, "Pray Magic"); // Jad Mage
		PROJECTILE_STYLES.put(449, "Pray Range"); // Jad Range
		// General
		PROJECTILE_STYLES.put(160, "Pray Magic"); // Standard spells
		PROJECTILE_STYLES.put(15, "Pray Range"); // Standard arrows
	}

	@Override
	protected void startUp() throws Exception {
		npcNextAttackTickMap.clear();
		overlayManager.add(overlay);
	}

	@Override
	protected void shutDown() throws Exception {
		npcNextAttackTickMap.clear();
		overlayManager.remove(overlay);
	}

	@Subscribe
	public void onProjectileMoved(ProjectileMoved event) {
		Projectile projectile = event.getProjectile();

		// Only track projectiles targeting the player that are "new" (cycle 0)
		if (projectile.getInteracting() != client.getLocalPlayer()) {
			return;
		}
		// Calculate ticks until impact
		int ticksRemaining = (projectile.getRemainingCycles() / 30);
		String style = PROJECTILE_STYLES.getOrDefault(projectile.getId(), "Pray Magic");

		// Use hash of projectile to keep tracking unique
		npcNextAttackTickMap.put(projectile.hashCode(),
				new AttackState(ticksRemaining, style, "Incoming Projectile"));
	}

	@Subscribe
	public void onAnimationChanged(AnimationChanged event) {
		if (!(event.getActor() instanceof NPC))
			return;

		NPC npc = (NPC) event.getActor();
		int anim = npc.getAnimation();

		// We can still use animations for melee or instant attacks
		if (anim == 2309 || anim == 1552) { // Abyssal demon / Bloodveld
			npcNextAttackTickMap.put(npc.getIndex(),
					new AttackState(4, "Pray Melee", npc.getName()));
		}
	}

	@Subscribe
	public void onGameTick(GameTick event) {
		// Countdown ticks
		npcNextAttackTickMap.entrySet().removeIf(entry -> {
			entry.getValue().ticks--;
			return entry.getValue().ticks <= 0;
		});

		// Sound alert on priority change
		getPriorityAttack().ifPresent(priority -> {
			if (!priority.style.equals(lastPriorityStyle)) {
				client.playSoundEffect(SoundEffectID.GE_INCREMENT_PLOP);
				lastPriorityStyle = priority.style;
			}
		});

		if (npcNextAttackTickMap.isEmpty()) {
			lastPriorityStyle = "";
		}
	}

	public Optional<AttackState> getPriorityAttack() {
		return npcNextAttackTickMap.values().stream()
				.min(Comparator.comparingInt(a -> a.ticks));
	}

	public Map<Integer, AttackState> getNpcNextAttackTickMap() {
		return npcNextAttackTickMap;
	}
}
