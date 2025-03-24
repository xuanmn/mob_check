package com.mob_check;

import lombok.Getter;
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

@PluginDescriptor(name = "Mob Check Plugin")
public class MobCheckPlugin extends Plugin
{
	@Inject
	private MobCheckOverlay overlay;

	@Inject
	private OverlayManager overlayManager;

	@Getter
    private final Map<Integer, Integer> npcNextAttackTickMap = new HashMap<>();

	private static final Set<Integer> ATTACK_ANIMATIONS = Set.of(
			2309, 1537, 1538, 1552
	);

	@Override
	protected void startUp()
	{
		npcNextAttackTickMap.clear();
		overlayManager.add(overlay);
		System.out.println("✅ MobCheckPlugin Started");
	}

	@Override
	protected void shutDown()
	{
		npcNextAttackTickMap.clear();
		overlayManager.remove(overlay);
		System.out.println("MobCheckPlugin Stopped");
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

		if (animationId != -1 && ATTACK_ANIMATIONS.contains(animationId))
		{
			int attackSpeed = getAttackSpeedForNpc(npc.getId());
			npcNextAttackTickMap.put(npcIndex, attackSpeed);
			System.out.println("💥 Tracking " + npc.getName() + " - Attack in " + attackSpeed + " ticks");
		}
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		for (Map.Entry<Integer, Integer> entry : npcNextAttackTickMap.entrySet())
		{
			int ticks = entry.getValue();
			npcNextAttackTickMap.put(entry.getKey(), ticks > 0 ? ticks - 1 : 0);

			if (ticks > 0)
			{
				System.out.println("NPC Index " + entry.getKey() + " - Ticks until next attack: " + (ticks - 1));
			}
		}
	}

	private int getAttackSpeedForNpc(int npcId)
	{
		switch (npcId)
		{
			case 7241:
				return 4;
			case 7276:
				return 5;
			default:
				return 4;
		}
	}

}