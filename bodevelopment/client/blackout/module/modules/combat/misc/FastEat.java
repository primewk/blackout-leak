package bodevelopment.client.blackout.module.modules.combat.misc;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.PacketEvent;
import bodevelopment.client.blackout.event.events.TickEvent;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.mixin.accessors.AccessorEntityStatusC2SPacket;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.util.OLEPOSSUtils;
import net.minecraft.class_1268;
import net.minecraft.class_243;
import net.minecraft.class_2596;
import net.minecraft.class_2663;
import net.minecraft.class_2846;
import net.minecraft.class_2886;
import net.minecraft.class_2828.class_2830;
import net.minecraft.class_2846.class_2847;

public class FastEat extends Module {
   private final SettingGroup sgGeneral = this.addGroup("General");
   private final Setting<Boolean> antiStop;
   private final Setting<Double> packets;
   private static FastEat INSTANCE;
   private double toSend;

   public FastEat() {
      super("Fast Eat", "Eats golden apples faster.", SubCategory.MISC_COMBAT, true);
      this.antiStop = this.sgGeneral.b("Anti Stop", false, "Doesn't allow you to stop eating.");
      this.packets = this.sgGeneral.d("Packets", 0.0D, 0.0D, 10.0D, 1.0D, ".");
      this.toSend = 0.0D;
      INSTANCE = this;
   }

   public static boolean eating() {
      if (INSTANCE != null && INSTANCE.enabled && (Boolean)INSTANCE.antiStop.get()) {
         return getHand() != null;
      } else {
         return false;
      }
   }

   @Event
   public void onSend(PacketEvent.Send event) {
      if (getHand() != null) {
         class_2596 var3 = event.packet;
         if (var3 instanceof class_2846) {
            class_2846 packet = (class_2846)var3;
            if (packet.method_12363() == class_2847.field_12974 && (Boolean)this.antiStop.get()) {
               event.setCancelled(true);
            }
         }

      }
   }

   @Event
   public void onReceive(PacketEvent.Receive.Pre event) {
      class_2596 var3 = event.packet;
      if (var3 instanceof class_2663) {
         class_2663 packet = (class_2663)var3;
         if (BlackOut.mc.field_1724 != null && ((AccessorEntityStatusC2SPacket)packet).getId() == BlackOut.mc.field_1724.method_5628()) {
            class_1268 hand = getHand();
            if (hand != null && BlackOut.mc.field_1690.field_1904.method_1434()) {
               this.sendSequenced((s) -> {
                  return new class_2886(hand, s);
               });
            }
         }
      }

   }

   @Event
   public void onTick(TickEvent.Pre event) {
      class_1268 hand = getHand();
      if (hand != null && BlackOut.mc.field_1690.field_1904.method_1434()) {
         this.sendSequenced((s) -> {
            return new class_2886(hand, s);
         });

         for(this.toSend += (Double)this.packets.get(); this.toSend > 0.0D; --this.toSend) {
            class_243 pos = Managers.PACKET.pos;
            this.sendInstantly(new class_2830(pos.method_10216(), pos.method_10214(), pos.method_10215(), Managers.ROTATION.prevYaw, Managers.ROTATION.prevPitch, Managers.PACKET.isOnGround()));
         }

      }
   }

   private static class_1268 getHand() {
      return BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null ? OLEPOSSUtils.getHand(OLEPOSSUtils::isGapple) : null;
   }
}
