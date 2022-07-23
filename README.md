# Surrender Mod

## Introduction

Surrender Mod is a open-source Minecraft Mod with a focus on giving players greater attackability
and more countermeasures. Mainly to add special abilities to the player's weapons, more possibilities

Follow **Surrender Mod** on **[Discord](https://discord.gg/cgNQPWExqj)** to get notified about new released.

Moreover, if you have any questions about Surrender Mod, Minecraft Mod developing or anything else, you can ask on the **[Discord](https://discord.gg/cgNQPWExqj)**.

## Features

* Allow players to avoid damage
* Allow players to teleport short distances under certain conditions.
* Allow player's death to be delayed.
* Provides players with the ability to heal themselves.

## Content

### Enchantment

___

#### BLINK

Dashing 8 blocks in 3 ticks, during the dashing, you are **immune to damage and invisible**.

Upon arrive, deal damage (0.4 \* *lvl* \* *damage*) to all entities within 5 blocks of you.
However, this attack **doesn't trigger** *non-damaging* enchantments, e.g. *Fire aspect* and *Knockback*.
Each dash costs 1 *durability*, and for every damage dealt to an entity, *1* more durability is consumed.
However, if the entity cannot be attacked at this time, or if damage cannot be applied to the entity, this attack will not consume durability.
If the attack results in the breaking of the item, it will not interrupt the reminder of the attack.

The maximum level of this enchantment is 5, and it can only be enchanted on weapons.

*Blink* has a **cooldown time** of *1* - *5* seconds (based on *lvl*)

When the *Entity* attacked by *Blink* dies (whether caused by you or not), it will reset the cooldown of *Blink*

___

#### DECISIVE STRIKE

Deals additional (5% \* *lvl*) damage to entities with less than *40%* of their current health.

___

#### EXPERIENCE

When you kill an *Entity*, it grants you the same experience as the *lvl*, and the main hand and off hand items are repaired with the same durability as the *lvl*.

___

#### EXPLOSION

This enchantment take effect when either of your main hand or off hand items has this enchantment.
When you fire a projectile in any way, this projectile will explode on impact. The explosion will not destroy blocks, and will not cause damage to you. The explosion distance increases with the *lvl* (2 blocks per *lvl*).

___

#### GUARDIAN

Provides 2 armor value per level.

When you are about to take damage that can bring your *Health* below *30%* of your *Max Health*, provide a shield that blocks 10 \* *lvl* damages for you, within a 30 second cooldown after the shield triggers.

This enchantment will provide an 20% additional armor and toughness per level to your total armor and total toughness.

___

#### HASTE

When dealing damage to an *Entity*, gain a movement speed bonus effect equal to the *lvl* for the same duration as the *lvl*.

___

#### LAST STAND

Deals additional (5% \* *lvl*) when your *Health* is below *30%* of your *Max Health*

___

#### MAGIC REDUCTION

Reduce the amount of magic damage you take by *12%* \* *lvl*

___

#### PREDATOR

When dealing damage to an *Entity*, reduce your *lvl* equivalent hunger level and provide the same amont of saturation.

___

#### REGENERATOR

When dealing damage to an *Entity*, heal *2%* \* *lvl* of your *Damaged Health*.

If your *Health* is below *50%* of your *Max Health*, healing is doubled.

If your *Health* is below *25%* of your *Max Health*, healing is tripled.

___

#### RIPPER

Each *lvl* causes you deal 20% extra damage.

___

#### SEEKER

When you kill an entity, you can teleport to the location where that *Entity* died, provided that you are within 20 blocks of that *Entity*.

Upon arrival, deals *2* + *12%* \* *lvl* of target's *Max Health* to *Entities* within 5 *Blocks* of you and *Knockback* these *Entities*.
The closer these *Entities* are to you, the stronger *Knockback* effect is generated.

___

#### SHIELD RADIER

Each attack will reduce the target's shield by *10%* \* *lvl*.

___

#### UNDYING

When you take damage that can put you into dying state, heal you to *Max Health* and delay death for *3* - (*5* - *lvl*) / *2* seconds, while burning your saturation.

You can use the healing effect to absolve this death; this effect cannot be triggered repeatedly, but has no cooldown.

During this time, convert the damage you deal to true damage and deal an additional *10%* + *2%* \* *lvl* of the target's *Max Health*.

If you hold an *Totem of undying*, it will be used in preference to the *Totem of undying*.

> True damage ignores damage reduction from armor and potion effects, ignores damage immunity from creation mode, and does not trigger *Totem of undying*.

___

#### FAST BOW

Fires arrows at high speed during bow buildup, at intervals of 5-0 ticks, based on *lvl*.

___

#### Blistering

Dashes forward 8 blocks in 3 ticks, during the dashing, you are **immune to damage and invisible**.

Meanwhile, deal damage (0.4 \* *lvl* \* *damage*) to all entities in the dash path, if any *Entities* are hit, reset the **cooldown time**

Each dash costs 1 *durability*, and for every damage dealt to an entity, *1* more durability is consumed.
However, if the entity cannot be attacked at this time, or if damage cannot be applied to the entity, this attack will not consume durability.
If the attack results in the breaking of the item, it will not interrupt the reminder of the attack.

*Blistering* has a **cooldown time** of *4* - *16* seconds (based on *lvl*)
