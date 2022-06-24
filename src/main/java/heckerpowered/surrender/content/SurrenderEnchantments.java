package heckerpowered.surrender.content;

import heckerpowered.surrender.SurrenderMod;
import heckerpowered.surrender.content.enchantment.BlinkEnchantment;
import heckerpowered.surrender.content.enchantment.DecisiveStrikeEnchantment;
import heckerpowered.surrender.content.enchantment.GuardianEnchantment;
import heckerpowered.surrender.content.enchantment.LastStandEnchantment;
import heckerpowered.surrender.content.enchantment.MagicReductionEnchantment;
import heckerpowered.surrender.content.enchantment.PredatorEnchantment;
import heckerpowered.surrender.content.enchantment.RegeneratorEnchantment;
import heckerpowered.surrender.content.enchantment.RipperEnchantment;
import heckerpowered.surrender.content.enchantment.SeekerEnchantment;
import heckerpowered.surrender.content.enchantment.ShieldRaiderEnchantment;
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
}
