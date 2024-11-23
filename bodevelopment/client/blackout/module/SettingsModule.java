package bodevelopment.client.blackout.module;

public class SettingsModule extends Module {
   public SettingsModule(String name, boolean client, boolean subscribe) {
      String var10002 = name.toLowerCase();
      super(name, "Global " + var10002 + " settings for all BlackOut modules.", client ? SubCategory.CLIENT : SubCategory.SETTINGS, subscribe);
   }

   public boolean toggleable() {
      return false;
   }

   public void enable(String msg) {
   }

   public void disable(String message) {
   }

   public boolean shouldSkipListeners() {
      return false;
   }
}
