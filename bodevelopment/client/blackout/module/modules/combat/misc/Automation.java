package bodevelopment.client.blackout.module.modules.combat.misc;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.MoveEvent;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.modules.combat.defensive.Surround;
import bodevelopment.client.blackout.module.modules.movement.Blink;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.util.HoleUtils;
import net.minecraft.class_2338;

public class Automation extends Module {
   private final SettingGroup sgGeneral = this.addGroup("General");
   private final Setting<Boolean> holeSurround;
   private final Setting<Boolean> leaveHoleBlink;
   private final Setting<Boolean> enterHoleBlink;
   private final Setting<Boolean> safeHoleBlink;
   private class_2338 currentPos;
   private class_2338 blinkPos;

   public Automation() {
      super("Automation", "Automates enabling some modules.", SubCategory.MISC_COMBAT, true);
      this.holeSurround = this.sgGeneral.b("Hole Surround", true, "Enables surround when entering a hole.");
      this.leaveHoleBlink = this.sgGeneral.b("Leave Hole Blink", true, "Enables blink when leaving a hole.");
      this.enterHoleBlink = this.sgGeneral.b("Enter Hole Blink", true, "Disables blink when entering a hole.");
      this.safeHoleBlink = this.sgGeneral.b("Safe Hole Blink", true, "Disables blink if old hole is not valid.");
      this.currentPos = null;
      this.blinkPos = null;
   }

   @Event
   public void onMove(MoveEvent.Post event) {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null) {
         class_2338 prev = this.currentPos;
         this.currentPos = new class_2338(BlackOut.mc.field_1724.method_31477(), (int)Math.round(BlackOut.mc.field_1724.method_23318()), BlackOut.mc.field_1724.method_31479());
         class_2338 prevPos = prev == null ? this.currentPos : prev;
         Blink blink = Blink.getInstance();
         Surround surround = Surround.getInstance();
         String var10001;
         if ((Boolean)this.safeHoleBlink.get() && this.blinkPos != null && !HoleUtils.inHole(this.blinkPos)) {
            var10001 = blink.getDisplayName();
            blink.disable(var10001 + " was disabled by " + this.getDisplayName());
         }

         if (!this.currentPos.equals(prevPos)) {
            if (HoleUtils.inHole(this.currentPos) && !HoleUtils.inHole(prevPos)) {
               if ((Boolean)this.holeSurround.get()) {
                  var10001 = surround.getDisplayName();
                  surround.enable(var10001 + " was enabled by " + this.getDisplayName());
               }

               if ((Boolean)this.enterHoleBlink.get()) {
                  var10001 = blink.getDisplayName();
                  blink.disable(var10001 + " was disabled " + this.getDisplayName());
               }
            }

            if ((Boolean)this.leaveHoleBlink.get() && !HoleUtils.inHole(this.currentPos) && HoleUtils.inHole(prevPos)) {
               this.blinkPos = prevPos;
               var10001 = blink.getDisplayName();
               blink.enable(var10001 + " was enabled by " + this.getDisplayName());
            }
         }

      }
   }
}
