package heckerpowered.surrender.common;

import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import heckerpowered.surrender.common.content.block.SurrenderBlocks;
import heckerpowered.surrender.common.content.enchantment.SurrenderEnchantments;
import heckerpowered.surrender.common.content.entity.SurrenderEntityType;
import heckerpowered.surrender.common.content.item.SurrenderItems;
import heckerpowered.surrender.common.content.tab.SurrenderCreativeModeTab;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.versions.mcp.MCPVersion;

@Mod(SurrenderMod.MODID)
public class SurrenderMod {

    /**
     * Define mod id in a common place for everything to reference
     */
    public static final String MODID = "surrender";

    /**
     * Define a user-friendly name.
     */
    public static final String MOD_NAME = "Surrender";

    /**
     * Directly reference a slf4j logger
     */
    public static final Logger LOGGER = LoggerFactory.getLogger("Surrender");

    /**
     * Directly reference a slf4j marker
     */
    public static final Marker MARKER = MarkerFactory.getMarker("SURRENDERMOD");
    /**
     * Set our version number to match the mods.toml file, which matches the one in our build.gradle
     */
    public static final ArtifactVersion VERSION = ModLoadingContext.get().getActiveContainer().getModInfo()
            .getVersion();

    /**
     * Create a new creative mode tab in a common place for everything to reference.
     */
    public static final SurrenderCreativeModeTab CREATIVE_MODE_TAB = new SurrenderCreativeModeTab();

    public SurrenderMod() {
        LOGGER.info(MARKER, "{} mod loading, version {}, for MC {} with MCP {}", MOD_NAME, VERSION,
                MCPVersion.getMCPVersion(), MCPVersion.getMCPVersion());

        //
        // Register game contents.
        //
        setupContent(FMLJavaModLoadingContext.get().getModEventBus());
    }

    private void setupContent(final IEventBus eventBus) {
        SurrenderEnchantments.DEFERRED_REGISTER.register(eventBus);
        SurrenderItems.DEFERRED_REGISTER.register(eventBus);
        SurrenderBlocks.DEFERRED_REGISTER.register(eventBus);
        SurrenderEntityType.DEFERRED_REGISTER.register(eventBus);
    }
}
