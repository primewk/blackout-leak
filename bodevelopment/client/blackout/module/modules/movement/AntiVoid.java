package bodevelopment.client.blackout.module.modules.movement;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.MoveEvent;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.modules.client.Notifications;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.util.OLEPOSSUtils;

public class AntiVoid extends Module {
   private final SettingGroup sgGeneral = this.addGroup("General");
   public final Setting<AntiVoid.Mode> mode;
   private final Setting<Double> d;
   private final Setting<Boolean> voidCheck;
   private double prevOG;

   public AntiVoid() {
      super("Anti Void", "Helps people with braindamage not fall off bridges.", SubCategory.MOVEMENT, true);
      this.mode = this.sgGeneral.e("Mode", AntiVoid.Mode.Motion, ".", () -> {
         return true;
      });
      this.d = this.sgGeneral.d("Activation Distance", 2.5D, 0.0D, 10.0D, 0.5D, ".");
      this.voidCheck = this.sgGeneral.b("Void Check", true, ".");
      this.prevOG = 0.0D;
   }

   public String getInfo() {
      return ((AntiVoid.Mode)this.mode.get()).name();
   }

   @Event
   public void onMove(MoveEvent.Pre event) {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null) {
         if (BlackOut.mc.field_1724.method_24828()) {
            this.prevOG = BlackOut.mc.field_1724.method_23318();
         }

         if (!((double)BlackOut.mc.field_1724.field_6017 < (Double)this.d.get())) {
            if (!(Boolean)this.voidCheck.get() || this.aboveVoid()) {
               switch((AntiVoid.Mode)this.mode.get()) {
               case Motion:
                  event.setY(this, 0.42D);
                  break;
               case Freeze:
                  event.setY(this, 0.0D);
                  break;
               case Position:
                  BlackOut.mc.field_1724.method_5814(BlackOut.mc.field_1724.method_23317(), BlackOut.mc.field_1724.method_23318() + 1.0D, BlackOut.mc.field_1724.method_23321());
               }

               BlackOut.mc.field_1724.field_6017 = 0.0F;
               Managers.NOTIFICATIONS.addNotification("Attempted to save you from the void!", this.getDisplayName(), 2.0D, Notifications.Type.Info);
            }
         }
      }
   }

   public boolean aboveVoid() {
      for(int i = 1; (double)i < 30.0D - Math.ceil(this.prevOG) + (double)BlackOut.mc.field_1724.method_31478(); ++i) {
         if (OLEPOSSUtils.collidable(BlackOut.mc.field_1724.method_24515().method_10087(i))) {
            return false;
         }
      }

      return true;
   }

   public static enum Mode {
      Motion,
      Freeze,
      Position;

      // $FF: synthetic method
      private static AntiVoid.Mode[] $values() {
         return new AntiVoid.Mode[]{Motion, Freeze, Position};
      }
   }
}
