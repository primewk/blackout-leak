package bodevelopment.client.blackout.module.modules.movement;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.TickEvent;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import net.minecraft.class_1313;
import net.minecraft.class_243;

public class SafeWalk extends Module {
   private final SettingGroup sgGeneral = this.addGroup("General");
   public final Setting<Boolean> sneak;
   private static SafeWalk INSTANCE;

   public SafeWalk() {
      super("Safe Walk", "Doesn't let you die (i would).", SubCategory.MOVEMENT, true);
      this.sneak = this.sgGeneral.b("Sneak", false, ".");
      INSTANCE = this;
   }

   public boolean shouldSkipListeners() {
      return !this.active();
   }

   @Event
   public void onTick(TickEvent.Post event) {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null && (Boolean)this.sneak.get()) {
         class_243 movement = BlackOut.mc.field_1724.method_18798();
         class_243 newMovement = BlackOut.mc.field_1724.method_18796(movement, class_1313.field_6308);
         if (!movement.equals(newMovement)) {
            BlackOut.mc.field_1724.method_5660(true);
            BlackOut.mc.field_1690.field_1832.method_23481(true);
         } else {
            BlackOut.mc.field_1724.method_5660(false);
            BlackOut.mc.field_1690.field_1832.method_23481(false);
         }

      }
   }

   public static boolean shouldSafeWalk() {
      return INSTANCE.active();
   }

   private boolean active() {
      if (this.enabled) {
         return true;
      } else {
         Scaffold scaffold = Scaffold.getInstance();
         return scaffold.enabled && (Boolean)scaffold.safeWalk.get();
      }
   }
}
