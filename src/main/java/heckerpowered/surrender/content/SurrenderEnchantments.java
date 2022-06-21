package heckerpowered.surrender.content;

import heckerpowered.surrender.SurrenderMod;
import heckerpowered.surrender.content.enchantment.GuardianEnchantment;
import heckerpowered.surrender.content.enchantment.RegeneratorEnchantment;
import heckerpowered.surrender.content.enchantment.SeekerEnchantment;
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
    public static final RegistryObject<RegeneratorEnchantment> REGENERATOR = DEFERRED_REGISTER.register("regenerator",
            RegeneratorEnchantment::new);
}
