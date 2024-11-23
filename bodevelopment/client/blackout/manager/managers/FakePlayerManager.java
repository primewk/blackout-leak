package bodevelopment.client.blackout.manager.managers;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.PacketEvent;
import bodevelopment.client.blackout.event.events.TickEvent;
import bodevelopment.client.blackout.manager.Manager;
import bodevelopment.client.blackout.randomstuff.FakePlayerEntity;
import bodevelopment.client.blackout.util.BoxUtils;
import bodevelopment.client.blackout.util.DamageUtils;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.class_1268;
import net.minecraft.class_1294;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1310;
import net.minecraft.class_1657;
import net.minecraft.class_1829;
import net.minecraft.class_1890;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_2596;
import net.minecraft.class_2664;
import net.minecraft.class_3414;
import net.minecraft.class_3417;
import net.minecraft.class_1297.class_5529;

public class FakePlayerManager extends Manager {
   public final List<FakePlayerEntity> fakePlayers = new ArrayList();
   private final List<FakePlayerEntity.PlayerPos> recorded = new ArrayList();
   private boolean recording = false;

   public void init() {
      BlackOut.EVENT_BUS.subscribe(this, () -> {
         return false;
      });
   }

   @Event
   public void onReceive(PacketEvent.Receive.Pre event) {
      class_2596 var3 = event.packet;
      if (var3 instanceof class_2664) {
         class_2664 packet = (class_2664)var3;
         this.fakePlayers.forEach((entity) -> {
            class_243 pos = new class_243(packet.method_11475(), packet.method_11477(), packet.method_11478());
            class_238 box = entity.method_5829();
            double q = 12.0D;
            double dist = BoxUtils.feet(box).method_1022(pos) / q;
            if (!(dist > 1.0D)) {
               double aa = DamageUtils.getExposure(pos, box, (class_2338)null);
               double ab = (1.0D - dist) * aa;
               float damage = (float)((int)((ab * ab + ab) * 3.5D * q + 1.0D));
               entity.method_5643(BlackOut.mc.field_1724.method_48923().method_48819((class_1297)null, (class_1297)null), damage);
            }
         });
      }

   }

   public void onAttack(FakePlayerEntity player) {
      this.playHitSound(player);
      player.method_5643(BlackOut.mc.field_1724.method_48923().method_48802(BlackOut.mc.field_1724), this.getDamage(player));
   }

   private void playHitSound(FakePlayerEntity target) {
      if (!(this.getDamage(target) <= 0.0F) && !target.method_29504()) {
         boolean bl = BlackOut.mc.field_1724.method_7261(0.5F) > 0.9F;
         boolean sprintHit = BlackOut.mc.field_1724.method_5624() && bl;
         if (sprintHit) {
            BlackOut.mc.field_1687.method_8486(BlackOut.mc.field_1724.method_23317(), BlackOut.mc.field_1724.method_23318(), BlackOut.mc.field_1724.method_23321(), class_3417.field_14999, BlackOut.mc.field_1724.method_5634(), 1.0F, 1.0F, true);
         }

         boolean critical = bl && BlackOut.mc.field_1724.field_6017 > 0.0F && !BlackOut.mc.field_1724.method_24828() && !BlackOut.mc.field_1724.method_6101() && !BlackOut.mc.field_1724.method_5799() && !BlackOut.mc.field_1724.method_6059(class_1294.field_5919) && !BlackOut.mc.field_1724.method_5765() && !BlackOut.mc.field_1724.method_5624();
         double d = (double)(BlackOut.mc.field_1724.field_5973 - BlackOut.mc.field_1724.field_6039);
         boolean bl42 = bl && !critical && !sprintHit && BlackOut.mc.field_1724.method_24828() && d < (double)BlackOut.mc.field_1724.method_6029() && BlackOut.mc.field_1724.method_5998(class_1268.field_5808).method_7909() instanceof class_1829;
         if (bl42) {
            BlackOut.mc.field_1687.method_8486(BlackOut.mc.field_1724.method_23317(), BlackOut.mc.field_1724.method_23318(), BlackOut.mc.field_1724.method_23321(), class_3417.field_14706, BlackOut.mc.field_1724.method_5634(), 1.0F, 1.0F, true);
         } else if (!critical) {
            class_3414 soundEvent = bl ? class_3417.field_14840 : class_3417.field_14625;
            BlackOut.mc.field_1687.method_8486(BlackOut.mc.field_1724.method_23317(), BlackOut.mc.field_1724.method_23318(), BlackOut.mc.field_1724.method_23321(), soundEvent, BlackOut.mc.field_1724.method_5634(), 1.0F, 1.0F, true);
         }

         if (critical) {
            BlackOut.mc.field_1687.method_8486(BlackOut.mc.field_1724.method_23317(), BlackOut.mc.field_1724.method_23318(), BlackOut.mc.field_1724.method_23321(), class_3417.field_15016, BlackOut.mc.field_1724.method_5634(), 1.0F, 1.0F, true);
         }

      } else {
         BlackOut.mc.field_1687.method_8486(BlackOut.mc.field_1724.method_23317(), BlackOut.mc.field_1724.method_23318(), BlackOut.mc.field_1724.method_23321(), class_3417.field_14914, BlackOut.mc.field_1724.method_5634(), 1.0F, 1.0F, true);
      }
   }

   private float getDamage(class_1297 target) {
      float damage = (float)DamageUtils.itemDamage(BlackOut.mc.field_1724.method_6047());
      float g;
      if (target instanceof class_1309) {
         g = class_1890.method_8218(BlackOut.mc.field_1724.method_6047(), ((class_1309)target).method_6046());
      } else {
         g = class_1890.method_8218(BlackOut.mc.field_1724.method_6047(), class_1310.field_6290);
      }

      float h = BlackOut.mc.field_1724.method_7261(0.5F);
      damage *= 0.2F + h * h * 0.8F;
      if ((double)h > 0.9D && BlackOut.mc.field_1724.field_6017 > 0.0F && !BlackOut.mc.field_1724.method_24828() && !BlackOut.mc.field_1724.method_6101() && !BlackOut.mc.field_1724.method_5799() && !BlackOut.mc.field_1724.method_6059(class_1294.field_5919) && !BlackOut.mc.field_1724.method_5765() && target instanceof class_1309 && !BlackOut.mc.field_1724.method_5624()) {
         damage *= 1.5F;
      }

      return damage + g * h;
   }

   @Event
   public void onTick(TickEvent.Post event) {
      if (!this.recording) {
         this.recorded.clear();
      } else {
         FakePlayerEntity.PlayerPos playerPos = new FakePlayerEntity.PlayerPos(BlackOut.mc.field_1724.method_19538(), BlackOut.mc.field_1724.method_18798(), BlackOut.mc.field_1724.method_18376(), BlackOut.mc.field_1724.method_36455(), BlackOut.mc.field_1724.method_36454(), BlackOut.mc.field_1724.method_5791(), BlackOut.mc.field_1724.field_6283);
         this.recorded.add(playerPos);
         if (this.recorded.size() > 1200) {
            this.endRecording();
         }

      }
   }

   public void restart() {
      this.fakePlayers.forEach((player) -> {
         player.progress = 0;
      });
   }

   public void startRecording() {
      this.recording = true;
   }

   public void endRecording() {
      this.recording = false;
   }

   public void add(String name) {
      FakePlayerEntity player = new FakePlayerEntity(name == null ? "KassuK" : name);
      player.set(this.recorded);
      this.recorded.clear();
      this.endRecording();
      this.fakePlayers.add(player);
   }

   public void clear() {
      this.fakePlayers.removeIf((player) -> {
         player.method_5650(class_5529.field_26999);
         return true;
      });
   }

   public static FakePlayerEntity.PlayerPos getPlayerPos(class_1657 player) {
      return new FakePlayerEntity.PlayerPos(player.method_19538(), player.method_18798(), player.method_18376(), player.method_36455(), player.method_36454(), player.method_5791(), player.method_43078());
   }
}
