package bodevelopment.client.blackout.module.modules.combat.misc;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.PacketEvent;
import bodevelopment.client.blackout.mixin.accessors.AccessorInteractEntityC2SPacket;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import net.minecraft.class_1309;
import net.minecraft.class_2596;
import net.minecraft.class_2848;
import net.minecraft.class_2824.class_5907;
import net.minecraft.class_2848.class_2849;

public class SuperKnockback extends Module {
   private final SettingGroup sgGeneral = this.addGroup("General");
   public final Setting<Boolean> check;

   public SuperKnockback() {
      super("Super Knockback", "Tries to give more KB", SubCategory.MISC_COMBAT, true);
      this.check = this.sgGeneral.b("Move Check", true, "Checks if you are moving to prevent sprinting in place.");
   }

   @Event
   public void onSend(PacketEvent.Send event) {
      if (BlackOut.mc.field_1724 != null) {
         if (!(Boolean)this.check.get() || BlackOut.mc.field_1724.method_18798().method_10216() != 0.0D && BlackOut.mc.field_1724.method_18798().method_10215() != 0.0D) {
            class_2596 var3 = event.packet;
            if (var3 instanceof AccessorInteractEntityC2SPacket) {
               AccessorInteractEntityC2SPacket packet = (AccessorInteractEntityC2SPacket)var3;
               if (packet.getType().method_34211() == class_5907.field_29172 && BlackOut.mc.field_1687.method_8469(packet.getId()) instanceof class_1309) {
                  if (!BlackOut.mc.field_1724.method_5624()) {
                     this.start();
                  }

                  this.stop();
                  this.start();
               }
            }

         }
      }
   }

   private void stop() {
      this.sendPacket(new class_2848(BlackOut.mc.field_1724, class_2849.field_12985));
   }

   private void start() {
      this.sendPacket(new class_2848(BlackOut.mc.field_1724, class_2849.field_12981));
   }
}
