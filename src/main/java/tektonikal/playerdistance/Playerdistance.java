package tektonikal.playerdistance;

import eu.midnightdust.lib.config.MidnightConfig;
import net.fabricmc.api.ModInitializer;
import tektonikal.playerdistance.config.PDcfg;

public class Playerdistance implements ModInitializer {
    /**
     * Runs the mod initializer.
     */
    @Override
    public void onInitialize() {
        MidnightConfig.init("playerdistance",PDcfg.class);
    }
}
