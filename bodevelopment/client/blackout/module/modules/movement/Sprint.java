package bodevelopment.client.blackout.module.modules.movement;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.TickEvent;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;

public class Sprint extends Module {
   private static Sprint INSTANCE;
   private final SettingGroup sgGeneral = this.addGroup("General");
   public final Setting<Sprint.SprintMode> sprintMode;
   public final Setting<Boolean> hungerCheck;

   public Sprint() {
      super("Sprint", "Makes you sprint", SubCategory.MOVEMENT, true);
      this.sprintMode = this.sgGeneral.e("Mode", Sprint.SprintMode.Vanilla, "How to sprint");
      this.hungerCheck = this.sgGeneral.b("HungerCheck", true, "Do we check if we have enough hunger to sprint");
      INSTANCE = this;
   }

   public static Sprint getInstance() {
      return INSTANCE;
   }

   public void onDisable() {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null) {
         BlackOut.mc.field_1724.method_5728(false);
      }

   }

   public String getInfo() {
      return ((Sprint.SprintMode)this.sprintMode.get()).name();
   }

   @Event
   public void onTick(TickEvent.Post event) {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null && !LongJump.getInstance().enabled) {
         if ((Boolean)this.hungerCheck.get() && BlackOut.mc.field_1724.method_7344().method_7586() < 6) {
            BlackOut.mc.field_1724.method_5728(false);
         } else {
            if (this.shouldSprint()) {
               BlackOut.mc.field_1724.method_5728(true);
            }

         }
      }
   }

   public boolean shouldSprint() {
      boolean var10000;
      switch((Sprint.SprintMode)this.sprintMode.get()) {
      case Vanilla:
         var10000 = BlackOut.mc.field_1724.field_3913.method_20622();
         break;
      case Omni:
         var10000 = BlackOut.mc.field_1724.method_18798().method_10216() != 0.0D || BlackOut.mc.field_1724.method_18798().method_10215() != 0.0D;
         break;
      case Rage:
         var10000 = true;
         break;
      default:
         throw new IncompatibleClassChangeError();
      }

      return var10000;
   }

   public static enum SprintMode {
      Vanilla,
      Omni,
      Rage;

      // $FF: synthetic method
      private static Sprint.SprintMode[] $values() {
         return new Sprint.SprintMode[]{Vanilla, Omni, Rage};
      }
   }
}
