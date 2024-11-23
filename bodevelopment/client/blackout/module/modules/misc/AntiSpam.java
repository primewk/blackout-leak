package bodevelopment.client.blackout.module.modules.misc;

import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.util.OLEPOSSUtils;

public class AntiSpam extends Module {
   private static AntiSpam INSTANCE;
   public final SettingGroup sgGeneral = this.addGroup("General");
   private final Setting<Double> similarity;

   public AntiSpam() {
      super("Anti Spam", "Stacks similar messages.", SubCategory.MISC, false);
      this.similarity = this.sgGeneral.d("Similarity", 0.9D, 0.0D, 1.0D, 0.01D, ".");
      INSTANCE = this;
   }

   public static AntiSpam getInstance() {
      return INSTANCE;
   }

   public boolean isSimilar(String string1, String string2) {
      return OLEPOSSUtils.similarity(string1, string2) >= (Double)this.similarity.get();
   }
}
