package bodevelopment.client.blackout.module.modules.misc;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.enums.SwitchMode;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.MoveEvent;
import bodevelopment.client.blackout.event.events.TickEvent;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.randomstuff.FindResult;
import bodevelopment.client.blackout.util.OLEPOSSUtils;
import java.util.Objects;
import net.minecraft.class_1268;
import net.minecraft.class_1802;
import net.minecraft.class_2848;
import net.minecraft.class_2886;
import net.minecraft.class_2848.class_2849;

public class Disabler extends Module {
   private final SettingGroup sgGeneral = this.addGroup("General");
   private final Setting<Boolean> grimMovement;
   private final Setting<Boolean> b1;
   private final Setting<Boolean> b2;
   private final Setting<Double> tridentDelay;
   private final Setting<SwitchMode> tridentSwitch;
   private final Setting<Boolean> vulcanOmni;
   private long prevRiptide;

   public Disabler() {
      super("Disabler", "Disables some parts of anticheats", SubCategory.MISC, true);
      this.grimMovement = this.sgGeneral.b("Grim Movement", false, ".");
      SettingGroup var10001 = this.sgGeneral;
      Setting var10005 = this.grimMovement;
      Objects.requireNonNull(var10005);
      this.b1 = var10001.b("Bol1", true, ".", var10005::get);
      var10001 = this.sgGeneral;
      var10005 = this.grimMovement;
      Objects.requireNonNull(var10005);
      this.b2 = var10001.b("Bol2", true, ".", var10005::get);
      var10001 = this.sgGeneral;
      Setting var10008 = this.grimMovement;
      Objects.requireNonNull(var10008);
      this.tridentDelay = var10001.d("Trident Delay", 0.5D, 0.0D, 1.0D, 0.01D, ".", var10008::get);
      var10001 = this.sgGeneral;
      SwitchMode var10003 = SwitchMode.Silent;
      var10005 = this.grimMovement;
      Objects.requireNonNull(var10005);
      this.tridentSwitch = var10001.e("Trident Switch", var10003, ".", var10005::get);
      this.vulcanOmni = this.sgGeneral.b("Vulcan Omni Sprint", false, ".");
      this.prevRiptide = 0L;
   }

   @Event
   public void onMove(MoveEvent.Post event) {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null) {
         if ((Boolean)this.vulcanOmni.get() && !BlackOut.mc.field_1690.field_1894.method_1434() && BlackOut.mc.field_1724.method_5624()) {
            this.sendPacket(new class_2848(BlackOut.mc.field_1724, class_2849.field_12981));
            this.sendPacket(new class_2848(BlackOut.mc.field_1724, class_2849.field_12985));
         }

      }
   }

   @Event
   public void onTickPre(TickEvent.Post event) {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null) {
         if ((Boolean)this.grimMovement.get()) {
            if ((double)(System.currentTimeMillis() - this.prevRiptide) < (Double)this.tridentDelay.get() * 1000.0D) {
               return;
            }

            class_1268 hand = OLEPOSSUtils.getHand(class_1802.field_8547);
            if (hand == null) {
               FindResult result = ((SwitchMode)this.tridentSwitch.get()).find(class_1802.field_8547);
               if (!result.wasFound() || !((SwitchMode)this.tridentSwitch.get()).swap(result.slot())) {
                  return;
               }
            }

            if ((Boolean)this.b1.get()) {
               this.sendPacket(new class_2886(hand == null ? class_1268.field_5808 : hand, 0));
            }

            if ((Boolean)this.b2.get()) {
               this.releaseUseItem();
            }

            this.prevRiptide = System.currentTimeMillis();
            if (hand == null) {
               ((SwitchMode)this.tridentSwitch.get()).swapBack();
            }
         }

      }
   }
}
