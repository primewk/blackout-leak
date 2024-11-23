package bodevelopment.client.blackout.module.modules.combat.misc;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.TickEvent;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.util.InvUtils;
import java.util.Objects;
import net.minecraft.class_1802;
import net.minecraft.class_2561;
import net.minecraft.class_2661;

public class AutoLog extends Module {
   private final SettingGroup sgGeneral = this.addGroup("General");
   private final Setting<Boolean> disable;
   private final Setting<Double> health;
   private final Setting<Boolean> totems;
   private final Setting<Double> totemAmount;

   public AutoLog() {
      super("Auto Log", "Automatically logs off", SubCategory.MISC_COMBAT, true);
      this.disable = this.sgGeneral.b("Disable on Disconnect", true, "Automatically Disables the module when disconnecting");
      this.health = this.sgGeneral.d("Log Health", 16.0D, 0.0D, 36.0D, 1.0D, "At what health should we disconnect");
      this.totems = this.sgGeneral.b("Count Totems", true, "Counts Totems");
      SettingGroup var10001 = this.sgGeneral;
      Setting var10008 = this.totems;
      Objects.requireNonNull(var10008);
      this.totemAmount = var10001.d("Totem Amount", 3.0D, 0.0D, 36.0D, 1.0D, "How many totems left to disconnect", var10008::get);
   }

   @Event
   public void onTick(TickEvent.Pre event) {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null) {
         int tots = InvUtils.count(true, true, (stack) -> {
            return stack.method_31574(class_1802.field_8288);
         });
         if ((double)(BlackOut.mc.field_1724.method_6032() + BlackOut.mc.field_1724.method_6067()) <= (Double)this.health.get()) {
            if ((Boolean)this.totems.get() && (double)tots > (Double)this.totemAmount.get()) {
               return;
            }

            if ((Boolean)this.disable.get()) {
               this.disable();
            }

            BlackOut.mc.field_1724.field_3944.method_52781(new class_2661(class_2561.method_43470("AutoLog")));
         }

      }
   }
}
