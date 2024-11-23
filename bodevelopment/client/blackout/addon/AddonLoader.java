package bodevelopment.client.blackout.addon;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.entrypoint.EntrypointContainer;

public class AddonLoader {
   public static final List<BlackoutAddon> addons = new ArrayList();

   public static void load() {
      Stream var10000 = FabricLoader.getInstance().getEntrypointContainers("blackout", BlackoutAddon.class).stream().map(EntrypointContainer::getEntrypoint);
      List var10001 = addons;
      Objects.requireNonNull(var10001);
      var10000.forEach(var10001::add);
   }
}
