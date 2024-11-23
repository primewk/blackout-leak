package bodevelopment.client.blackout.module.modules.visual.world;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.TickEvent;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import net.minecraft.class_1293;
import net.minecraft.class_1294;

public class Brightness extends Module {
   private static Brightness INSTANCE;
   private final SettingGroup sgGeneral = this.addGroup("General");
   public final Setting<Brightness.Mode> mode;

   public Brightness() {
      super("Brightness", "Makes the world bright", SubCategory.WORLD, true);
      this.mode = this.sgGeneral.e("Mode", Brightness.Mode.Gamma, ".", () -> {
         return true;
      });
      INSTANCE = this;
   }

   public static Brightness getInstance() {
      return INSTANCE;
   }

   public void onDisable() {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null) {
         if (this.mode.get() == Brightness.Mode.Effect && BlackOut.mc.field_1724.method_6059(class_1294.field_5925)) {
            BlackOut.mc.field_1724.method_6016(class_1294.field_5925);
         }

      }
   }

   public String getInfo() {
      return ((Brightness.Mode)this.mode.get()).name();
   }

   @Event
   public void onTick(TickEvent.Pre event) {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null) {
         if (this.mode.get() == Brightness.Mode.Effect) {
            BlackOut.mc.field_1724.method_6092(new class_1293(class_1294.field_5925, 69, 0));
         }

      }
   }

   public static enum Mode {
      Effect,
      Gamma;

      // $FF: synthetic method
      private static Brightness.Mode[] $values() {
         return new Brightness.Mode[]{Effect, Gamma};
      }
   }
}
