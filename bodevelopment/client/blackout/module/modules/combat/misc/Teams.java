package bodevelopment.client.blackout.module.modules.combat.misc;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import net.minecraft.class_1657;
import net.minecraft.class_5251;

public class Teams extends Module {
   private static Teams INSTANCE;
   private final SettingGroup sgGeneral = this.addGroup("General");
   public final Setting<Boolean> colorCheck;

   public Teams() {
      super("Teams", "Tries to prevent hitting teammates", SubCategory.MISC_COMBAT, true);
      this.colorCheck = this.sgGeneral.b("Color Check", true, "Checks if the players tab color is the same as yours");
      INSTANCE = this;
   }

   public static Teams getInstance() {
      return INSTANCE;
   }

   public boolean isTeammate(class_1657 player) {
      if ((Boolean)this.colorCheck.get()) {
         class_5251 localColor = BlackOut.mc.field_1724.method_5476().method_10866().method_10973();
         class_5251 playerColor = player.method_5476().method_10866().method_10973();
         return localColor == playerColor;
      } else {
         return false;
      }
   }
}
