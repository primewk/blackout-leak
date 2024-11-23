package bodevelopment.client.blackout.module.modules.misc;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.MoveEvent;
import bodevelopment.client.blackout.event.events.PacketEvent;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.modules.movement.Blink;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.util.OLEPOSSUtils;
import java.util.Objects;
import net.minecraft.class_1753;
import net.minecraft.class_1802;
import net.minecraft.class_2596;
import net.minecraft.class_2828;
import net.minecraft.class_2846;
import net.minecraft.class_2848;
import net.minecraft.class_2886;
import net.minecraft.class_2828.class_2829;
import net.minecraft.class_2828.class_2830;
import net.minecraft.class_2846.class_2847;
import net.minecraft.class_2848.class_2849;

public class FastProjectile extends Module {
   private static FastProjectile INSTANCE;
   private final SettingGroup sgGeneral = this.addGroup("General");
   private final Setting<Boolean> posRot;
   private final Setting<Boolean> blink;
   private final Setting<Double> timer;
   private final Setting<Integer> charge;
   private double throwYaw;
   private boolean down;
   public int ticksLeft;
   private boolean enabledBlink;
   private int toSend;
   private double x;
   private double y;
   private double z;
   private boolean ignore;
   private boolean throwIgnore;
   private class_2596<?> throwPacket;

   public FastProjectile() {
      super("Fast Projectile", ".", SubCategory.MISC, true);
      this.posRot = this.sgGeneral.b("Pos Rot", true, ".");
      this.blink = this.sgGeneral.b("Blink", false, ".");
      SettingGroup var10001 = this.sgGeneral;
      Setting var10008 = this.blink;
      Objects.requireNonNull(var10008);
      this.timer = var10001.d("Timer", 1.0D, 1.0D, 10.0D, 0.1D, ".", var10008::get);
      this.charge = this.sgGeneral.i("Charge", 10, 0, 100, 1, ".");
      this.throwYaw = 0.0D;
      this.down = false;
      this.ticksLeft = 0;
      this.enabledBlink = false;
      this.toSend = 0;
      this.ignore = false;
      this.throwIgnore = false;
      this.throwPacket = null;
      INSTANCE = this;
   }

   public static FastProjectile getInstance() {
      return INSTANCE;
   }

   @Event
   public void onSend(PacketEvent.Send event) {
      if (event.packet instanceof class_2828 && !this.ignore && this.ticksLeft > 0) {
         event.setCancelled(true);
      }

      class_2596 var3 = event.packet;
      if (var3 instanceof class_2886) {
         class_2886 packet = (class_2886)var3;
         if (this.throwIgnore || !OLEPOSSUtils.getItem(packet.method_12551()).method_31574(class_1802.field_8634)) {
            return;
         }

         this.move(event);
      }

      var3 = event.packet;
      if (var3 instanceof class_2846) {
         class_2846 packet = (class_2846)var3;
         if (packet.method_12363() == class_2847.field_12974) {
            if (this.throwIgnore || !(BlackOut.mc.field_1724.method_6030().method_7909() instanceof class_1753)) {
               return;
            }

            this.move(event);
         }
      }

   }

   @Event
   public void onMove(MoveEvent.Pre event) {
      if (--this.ticksLeft <= 0) {
         if (this.enabledBlink) {
            Blink.getInstance().disable();
            BlackOut.mc.method_1562().method_48296().method_52915();
            this.enabledBlink = false;
            this.sendPacket(new class_2848(BlackOut.mc.field_1724, class_2849.field_12985));
            this.throwIgnore = true;
            if (this.throwPacket != null) {
               this.sendPacket(this.throwPacket);
            }

            this.throwIgnore = false;
         }

      } else {
         this.toSend = (int)((double)this.toSend + (Double)this.timer.get());
         event.set(this, 0.0D, 0.0D, 0.0D);

         while(this.toSend > 0) {
            this.chargeBlink();
            --this.toSend;
         }

      }
   }

   private void chargeBlink() {
      if (this.down = !this.down) {
         this.x += Math.cos(this.throwYaw) * 1.0E-5D;
         this.z += Math.sin(this.throwYaw) * 1.0E-5D;
         this.send(this.x, this.y + 1.0E-13D, this.z, true);
      } else {
         this.send(this.x, this.y + 2.0E-13D, this.z, false);
         --this.ticksLeft;
      }

   }

   private void move(PacketEvent.Send event) {
      double yaw = Math.toRadians((double)(Managers.ROTATION.prevYaw + 90.0F));
      this.x = BlackOut.mc.field_1724.method_23317();
      this.y = BlackOut.mc.field_1724.method_23318();
      this.z = BlackOut.mc.field_1724.method_23321();
      if ((Boolean)this.blink.get()) {
         this.down = false;
         this.enabledBlink = true;
         this.throwYaw = yaw;
         this.ticksLeft = (Integer)this.charge.get();
         this.throwPacket = event.packet;
         Blink.getInstance().enable();
         event.setCancelled(true);
      }

      this.sendPacket(new class_2848(BlackOut.mc.field_1724, class_2849.field_12981));
      if (!(Boolean)this.blink.get()) {
         for(int i = 0; i < (Integer)this.charge.get(); ++i) {
            this.x += Math.cos(yaw) * 1.0E-5D;
            this.z += Math.sin(yaw) * 1.0E-5D;
            this.send(this.x, this.y + 1.0E-13D, this.z, true);
            this.send(this.x, this.y + 2.0E-13D, this.z, false);
         }

         this.sendPacket(new class_2848(BlackOut.mc.field_1724, class_2849.field_12985));
      }
   }

   private void send(double x, double y, double z, boolean og) {
      this.ignore = true;
      if ((Boolean)this.posRot.get()) {
         this.sendPacket(new class_2830(x, y, z, Managers.ROTATION.prevYaw, Managers.ROTATION.prevPitch, og));
      } else {
         this.sendPacket(new class_2829(x, y, z, og));
      }

      this.ignore = false;
   }
}
