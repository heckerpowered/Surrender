package heckerpowered.surrender.common.content;

import heckerpowered.surrender.common.SurrenderMod;
import heckerpowered.surrender.common.content.enchantment.BlinkEnchantment;
import heckerpowered.surrender.common.content.enchantment.DecisiveStrikeEnchantment;
import heckerpowered.surrender.common.content.enchantment.ExperienceEnchantment;
import heckerpowered.surrender.common.content.enchantment.ExplosionEnchantment;
import heckerpowered.surrender.common.content.enchantment.FastBowEnchantment;
import heckerpowered.surrender.common.content.enchantment.GuardianEnchantment;
import heckerpowered.surrender.common.content.enchantment.HasteEnchantment;
import heckerpowered.surrender.common.content.enchantment.LastStandEnchantment;
import heckerpowered.surrender.common.content.enchantment.MagicReductionEnchantment;
import heckerpowered.surrender.common.content.enchantment.PredatorEnchantment;
import heckerpowered.surrender.common.content.enchantment.RegeneratorEnchantment;
import heckerpowered.surrender.common.content.enchantment.RipperEnchantment;
import heckerpowered.surrender.common.content.enchantment.SeekerEnchantment;
import heckerpowered.surrender.common.content.enchantment.ShieldRaiderEnchantment;
import heckerpowered.surrender.common.content.enchantment.UndyingEnchantment;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class SurrenderEnchantments {
        private SurrenderEnchantments() {
        }

        public static final DeferredRegister<Enchantment> DEFERRED_REGISTER = DeferredRegister
                        .create(ForgeRegistries.ENCHANTMENTS, SurrenderMod.MODID);
        public static final RegistryObject<SeekerEnchantment> SEEKER = DEFERRED_REGISTER.register("seeker",
                        SeekerEnchantment::new);
        public static final RegistryObject<GuardianEnchantment> GUARDIAN = DEFERRED_REGISTER.register("guardian",
                        GuardianEnchantment::new);
        public static final RegistryObject<RegeneratorEnchantment> REGENERATOR = DEFERRED_REGISTER.register(
                        "regenerator", RegeneratorEnchantment::new);
        public static final RegistryObject<ShieldRaiderEnchantment> SHIELD_RAIDER = DEFERRED_REGISTER
                        .register("shield_radier", ShieldRaiderEnchantment::new);
        public static final RegistryObject<PredatorEnchantment> PREDATOR = DEFERRED_REGISTER.register("predator",
                        PredatorEnchantment::new);
        public static final RegistryObject<BlinkEnchantment> BLINK = DEFERRED_REGISTER.register("blink",
                        BlinkEnchantment::new);
        public static final RegistryObject<DecisiveStrikeEnchantment> DECISIVE_STRIKE = DEFERRED_REGISTER
                        .register("decisive_strike", DecisiveStrikeEnchantment::new);
        public static final RegistryObject<LastStandEnchantment> LAST_STAND = DEFERRED_REGISTER.register("last_stand",
                        LastStandEnchantment::new);
        public static final RegistryObject<RipperEnchantment> RIPPER = DEFERRED_REGISTER.register("ripper",
                        RipperEnchantment::new);
        public static final RegistryObject<MagicReductionEnchantment> MAGIC_REDUCTION = DEFERRED_REGISTER
                        .register("magic_reduction", MagicReductionEnchantment::new);
        public static final RegistryObject<UndyingEnchantment> UNDYING = DEFERRED_REGISTER.register("undying",
                        UndyingEnchantment::new);
        public static final RegistryObject<ExplosionEnchantment> EXPLOSION = DEFERRED_REGISTER.register("explosion",
                        ExplosionEnchantment::new);
        public static final RegistryObject<ExperienceEnchantment> EXPERIENCE = DEFERRED_REGISTER.register("experience",
                        ExperienceEnchantment::new);
        public static final RegistryObject<HasteEnchantment> HASTE = DEFERRED_REGISTER.register("haste",
                        HasteEnchantment::new);
        public static final RegistryObject<FastBowEnchantment> FAST_BOW = DEFERRED_REGISTER.register("fast_bow",
                        FastBowEnchantment::new);
}
