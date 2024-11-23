package bodevelopment.client.blackout.addon;

public class BlackoutAddon {
   private final String name;
   public final String modulePath;
   private final String commandPath;

   protected BlackoutAddon(String name, String modulePath, String commandPath) {
      this.name = name;
      this.modulePath = modulePath;
      this.commandPath = commandPath;
   }
}
