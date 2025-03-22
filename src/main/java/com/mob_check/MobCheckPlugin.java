package com.mob_check;

import lombok.Getter;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.events.AnimationChanged;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import javax.inject.Inject;
import java.util.HashMap;
import java.util.Map;

@PluginDescriptor(
		name = "Mob Check Plugin"
)
public class MobCheckPlugin extends Plugin
{
	@Inject
	private Client client;

    // This method retrieves the npcNextAttackTickMap
    @Getter
    private final Map<NPC, Integer> npcNextAttackTickMap = new HashMap<>();

	@Override
	protected void startUp() throws Exception
	{
		System.out.println("MobCheckPlugin Started");
	}

	@Override
	protected void shutDown() throws Exception
	{
		System.out.println("MobCheckPlugin Stopped");
	}

	// Listen for animation changes
	@Subscribe
	public void onAnimationChanged(AnimationChanged event)
	{
		if (event.getActor() instanceof NPC)
		{
			NPC npc = (NPC) event.getActor();
			int animationId = npc.getAnimation();
			int npcState = npc.getState();

			// Only log NPCs that are active (not idle) and have a meaningful animation
			if (npcState == 108 || animationId != -1)
			{
				System.out.println("NPC Name: " + npc.getName() + " (ID: " + npc.getId() + ") - Animation: " + animationId + " State: " + npcState);
			}
		}
	}

}