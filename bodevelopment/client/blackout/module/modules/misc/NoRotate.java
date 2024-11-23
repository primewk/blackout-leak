package bodevelopment.client.blackout.module.modules.misc;

import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;

public class NoRotate extends Module {
   private static NoRotate INSTANCE;
   private final SettingGroup sgGeneral = this.addGroup("General");
   public final Setting<NoRotate.NoRotateMode> mode;

   public NoRotate() {
      super("No Rotate", "Doesn't set rotation on rubberband", SubCategory.MISC, false);
      this.mode = this.sgGeneral.e("Mode", NoRotate.NoRotateMode.Cancel, ".");
      INSTANCE = this;
   }

   public static NoRotate getInstance() {
      return INSTANCE;
   }

   public String getInfo() {
      return ((NoRotate.NoRotateMode)this.mode.get()).name();
   }

   public static enum NoRotateMode {
      Cancel,
      Set,
      Spoof;

      // $FF: synthetic method
      private static NoRotate.NoRotateMode[] $values() {
         return new NoRotate.NoRotateMode[]{Cancel, Set, Spoof};
      }
   }
}
