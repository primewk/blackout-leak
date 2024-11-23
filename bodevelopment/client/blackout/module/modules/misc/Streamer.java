package bodevelopment.client.blackout.module.modules.misc;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;

public class Streamer extends Module {
   private static Streamer INSTANCE;
   private final SettingGroup sgGeneral = this.addGroup("General");
   public final Setting<String> spoofedName;
   public final Setting<Boolean> skin;

   public Streamer() {
      super("Streamer", "Spoofs stuff to not reveal your account.", SubCategory.MISC, true);
      this.spoofedName = this.sgGeneral.s("Spoofed Name", "Luhposu", "");
      this.skin = this.sgGeneral.b("Skin", true, ".");
      INSTANCE = this;
   }

   public static Streamer getInstance() {
      return INSTANCE;
   }

   public String replace(String string) {
      return string.replace(BlackOut.mc.method_1548().method_1676(), (CharSequence)this.spoofedName.get());
   }
}
