# Mob Check Plugin

A dynamic PvM Priority Prayer Helper plugin for the RuneLite client. It displays the ticks remaining before incoming NPC projectile attacks and immediate melee attacks hit the player, helping you time your protection prayers perfectly.

## Features

- **Overhead Countdown Overlay**: Renders the style-specific protection prayer icon (Magic, Range, Melee) and remaining tick countdown directly above the player's character.
- **Sidebar Info Box**: Displays a clean panel showing the priority attack style and ticks remaining.
- **Dynamic Priority Engine**: Automatically compares all incoming threats and displays the one that will hit you first.
- **Audio Alerts**: Plays a configurable sound effect when the priority prayer style changes, keeping you focused on the action.
- **Broad Boss & Mob Support**: Built-in support for multiple PvM encounters:
  - **Inferno**: Jal-Zek, Jal-Xil, and JalTok-Jad
  - **Zulrah**: Magic and Range phases, plus Snakelings
  - **Vorkath**: Standard Magic, Range, and Dragonfire projectiles
  - **Cerberus**: Melee, Magic, and Range attacks
  - **Alchemical Hydra**: Magic and Range style swaps
  - **Gauntlet / Hunllef**: Magic and Range styles
  - **Demonic Gorillas**: Style transition detection
  - **God Wars Dungeon**: Commander Zilyana, General Graardor, and K'ril Tsutsaroth
  - **Fortis Colosseum**: Serpent Shaman, Javelinic Colossus, Manticore (tri-attack sequence), Jaguar Warrior, Minotaur, Shockwave Colossus, Fremennik Warband, and Sol Heredit
  - **General Combat**: Standard spell and projectile tracking for ordinary NPCs, as well as melee animations (Abyssal demons, Bloodvelds, etc.).

## Configuration Options

Inside the RuneLite Configuration Panel under **Mob Check**, you can configure:

1. **Show Overhead**: Toggles rendering the prayer icon and ticks above your character.
2. **Show Info Box**: Toggles rendering the sidebar panel.
3. **Play Sound Alert**: Enables/disables audio cues when priority attacks switch.
4. **Sound Effect ID**: Customize the sound effect played (default `2266` / GE Plop).
5. **Warning Threshold**: Configure the tick count at which the overlay indicator turns red to indicate immediate urgency (default: `1` tick remaining).
6. **Track Unknown Projectiles**: Toggles a fallback to Magic prayer for any projectile targeting you that is not recognized in the database.

## Building and Testing

### Prerequisites

The plugin build system utilizes Gradle 8.10, which supports **Java 11 through Java 23**. If your default system JDK is newer (e.g., JDK 24+), you will need to set `JAVA_HOME` to a compatible JDK version (such as JDK 11) to compile or run tests:

```bash
export JAVA_HOME=/path/to/compatible/jdk
```

### Compiling and Packaging

To compile and package the plugin locally:

```bash
./gradlew build
```

To build a shaded JAR containing all code and dependencies for local testing:

```bash
./gradlew shadowJar
```

### Running Tests

To run the unit test suite and verify threat logic:

```bash
./gradlew test
```

### Manual Testing / Local Run

You can run the built-in integration test runner class `com.mob_check.MobCheckPluginTest` inside your IDE to launch RuneLite in developer mode with the plugin preloaded.