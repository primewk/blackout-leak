package bodevelopment.client.blackout.module.modules.movement;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.MoveEvent;
import bodevelopment.client.blackout.event.events.PacketEvent;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.modules.combat.offensive.Aura;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.util.OLEPOSSUtils;
import java.util.Objects;
import net.minecraft.class_1268;
import net.minecraft.class_1799;
import net.minecraft.class_1829;
import net.minecraft.class_2596;
import net.minecraft.class_2868;
import net.minecraft.class_2886;

public class NoSlow extends Module {
   private static NoSlow INSTANCE;
   private final SettingGroup sgGeneral = this.addGroup("General");
   private final SettingGroup sgStrict = this.addGroup("Strict");
   private final Setting<Boolean> blocking;
   private final Setting<Boolean> using;
   private final Setting<Boolean> strict;
   private final Setting<Boolean> grim;
   private final Setting<Boolean> single;
   private final Setting<Integer> delay;
   private int timer;

   public NoSlow() {
      super("No Slow", "Prevents slowing down.", SubCategory.MOVEMENT, true);
      this.blocking = this.sgGeneral.b("Blocking", false, ".");
      this.using = this.sgGeneral.b("Using", false, ".");
      this.strict = this.sgStrict.b("Strict", false, "Sends switch packets to bypass NCP noslow checks.");
      SettingGroup var10001 = this.sgStrict;
      Setting var10005 = this.strict;
      Objects.requireNonNull(var10005);
      this.grim = var10001.b("Grim", false, "Switches to a different slot instead of sending switch packet to the current one.", var10005::get);
      var10001 = this.sgStrict;
      var10005 = this.strict;
      Objects.requireNonNull(var10005);
      this.single = var10001.b("Single Packet", true, "Only sends 1 switch packet after starting to eat. Works on most servers that require this module.", var10005::get);
      this.delay = this.sgStrict.i("Delay", 1, 1, 20, 1, "Tick delay between switch packets.", () -> {
         return !(Boolean)this.single.get() && (Boolean)this.strict.get();
      });
      this.timer = 0;
      INSTANCE = this;
   }

   public static NoSlow getInstance() {
      return INSTANCE;
   }

   public String getInfo() {
      return (Boolean)this.strict.get() ? "Strict" : "Normal";
   }

   public static boolean shouldSlow() {
      if (BlackOut.mc.field_1724 == null) {
         return false;
      } else {
         boolean isBlocking = Aura.getInstance().isBlocking;
         if (getInstance().enabled) {
            if (isBlocking) {
               return !(Boolean)getInstance().blocking.get();
            } else if (BlackOut.mc.field_1724.method_6115()) {
               if (Managers.PACKET.getStack().method_7909() instanceof class_1829) {
                  return !(Boolean)getInstance().blocking.get();
               } else {
                  return !(Boolean)getInstance().using.get();
               }
            } else {
               return false;
            }
         } else {
            return isBlocking || BlackOut.mc.field_1724.method_6115();
         }
      }
   }

   public boolean shouldSendNoSlow(class_1268 hand) {
      if (BlackOut.mc.field_1724 == null) {
         return false;
      } else if (!getInstance().enabled) {
         return false;
      } else if (Aura.getInstance().isBlocking) {
         return (Boolean)getInstance().blocking.get();
      } else if (BlackOut.mc.field_1724.method_6115()) {
         class_1799 stack = OLEPOSSUtils.getItem(hand);
         if (stack == null) {
            return false;
         } else {
            return stack.method_7909() instanceof class_1829 ? (Boolean)getInstance().blocking.get() : (Boolean)getInstance().using.get();
         }
      } else {
         return false;
      }
   }

   @Event
   public void onSend(PacketEvent.Sent event) {
      class_2596 var3 = event.packet;
      if (var3 instanceof class_2886) {
         class_2886 packet = (class_2886)var3;
         if (BlackOut.mc.field_1724.method_6115()) {
            class_1268 hand = BlackOut.mc.field_1724.method_6058();
            if (hand == packet.method_12551() && this.shouldSendNoSlow(hand) && (Boolean)this.strict.get()) {
               this.send(hand);
               this.timer = 0;
            }

            return;
         }
      }

   }

   @Event
   public void onMove(MoveEvent.Pre event) {
      if ((Boolean)this.strict.get()) {
         if (this.shouldSendNoSlow(BlackOut.mc.field_1724.method_6058())) {
            if (++this.timer >= (Integer)this.delay.get() && !(Boolean)this.single.get()) {
               this.send(BlackOut.mc.field_1724.method_6058());
               this.timer = 0;
            }

         }
      }
   }

   private int getGrimSlot(int slot) {
      return slot > 7 ? 0 : slot + 1;
   }

   private void send(class_1268 hand) {
      int currentSlot = Managers.PACKET.slot;
      if ((Boolean)this.grim.get()) {
         if (hand == class_1268.field_5808) {
            this.sendSequencedPostGrim((sequence) -> {
               return new class_2886(class_1268.field_5810, sequence);
            });
            return;
         }

         Managers.PACKET.sendPostPacket(new class_2868(this.getGrimSlot(currentSlot)));
      }

      Managers.PACKET.sendPostPacket(new class_2868(currentSlot));
   }
}
