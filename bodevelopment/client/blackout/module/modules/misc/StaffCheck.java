package bodevelopment.client.blackout.module.modules.misc;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.PacketEvent;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.modules.client.Notifications;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.class_2596;
import net.minecraft.class_2664;
import net.minecraft.class_2703;
import net.minecraft.class_2743;
import net.minecraft.class_2703.class_2705;

public class StaffCheck extends Module {
   private final SettingGroup sgGeneral = this.addGroup("General");
   private final Setting<Boolean> kb;
   private final Setting<Boolean> nameCheck;
   private long prevTime;
   private boolean added;
   List<String> staff;

   public StaffCheck() {
      super("Staff Check", "Alerts about staff checks", SubCategory.MISC, true);
      this.kb = this.sgGeneral.b("Knockback", true, "Notifies about suspicious knockback");
      this.nameCheck = this.sgGeneral.b("Name Check", false, ".");
      this.prevTime = System.currentTimeMillis();
      this.added = false;
      this.staff = new ArrayList();
   }

   public void onEnable() {
      if (!this.added) {
         this.added = true;
         this.staff.add("Raksamies");
         this.staff.add("OLEPOSSU");
      }
   }

   @Event
   public void onVelocity(PacketEvent.Receive.Pre event) {
      class_2596 var3 = event.packet;
      if (var3 instanceof class_2743) {
         class_2743 packet = (class_2743)var3;
         if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1724.method_5628() != packet.method_11818()) {
            return;
         }

         this.checkVelocity((float)packet.method_11815(), (float)packet.method_11819(), (float)packet.method_11816());
      }

      var3 = event.packet;
      if (var3 instanceof class_2664) {
         class_2664 packet = (class_2664)var3;
         this.checkVelocity(packet.method_11472(), packet.method_11474(), packet.method_11473());
      }

   }

   private void checkVelocity(float x, float z, float y) {
      if (BlackOut.mc.field_1724 != null) {
         if (x == 0.0F && z == 0.0F && y > 0.0F && (Boolean)this.kb.get()) {
            Managers.NOTIFICATIONS.addNotification("Suspicious Knockback taken at tick " + BlackOut.mc.field_1724.field_6012, this.getDisplayName(), 2.0D, Notifications.Type.Alert);
         }

      }
   }

   @Event
   public void onSend(PacketEvent.Receive.Pre event) {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null) {
         if ((Boolean)this.nameCheck.get()) {
            class_2596 var3 = event.packet;
            if (var3 instanceof class_2703) {
               class_2703 packet = (class_2703)var3;
               List<class_2705> entries = packet.method_46330();
               entries.forEach((entry) -> {
                  if (entry.comp_1111() != null && this.staff.contains(entry.comp_1111().method_44746().toString()) && System.currentTimeMillis() - this.prevTime > 5000L) {
                     Managers.NOTIFICATIONS.addNotification("Detected Staff", this.getDisplayName(), 2.0D, Notifications.Type.Alert);
                     this.prevTime = System.currentTimeMillis();
                  }

               });
            }

         }
      }
   }
}
