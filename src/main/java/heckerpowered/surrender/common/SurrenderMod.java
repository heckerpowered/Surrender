package heckerpowered.surrender.common;

import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.slf4j.Logger;
import com.mojang.logging.LogUtils;
import heckerpowered.surrender.common.content.SurrenderEnchantments;
import heckerpowered.surrender.common.content.SurrenderItems;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(SurrenderMod.MODID)
public class SurrenderMod {

    /**
     * Define mod id in a common place for everything to reference
     */
    public static final String MODID = "surrender";

    /**
     * Directly reference a slf4j logger
     */
    public static final Logger LOGGER = LogUtils.getLogger();

    /**
     * Set our version number to match the mods.toml file, which matches the one in our build.gradle
     */
    public static final ArtifactVersion VERSION = ModLoadingContext.get().getActiveContainer().getModInfo()
            .getVersion();

    public SurrenderMod() {
        LOGGER.info("Loading {}, version {}", VERSION);

        //
        // Register game contents.
        //
        setupContent(FMLJavaModLoadingContext.get().getModEventBus());
    }

    private void setupContent(final IEventBus eventBus) {
        SurrenderEnchantments.DEFERRED_REGISTER.register(eventBus);
        SurrenderItems.DEFERRED_REGISTER.register(eventBus);
    }
}
