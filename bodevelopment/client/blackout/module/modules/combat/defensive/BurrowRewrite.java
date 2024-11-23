package bodevelopment.client.blackout.module.modules.combat.defensive;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.enums.RotationType;
import bodevelopment.client.blackout.enums.SwitchMode;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.MoveEvent;
import bodevelopment.client.blackout.event.events.PacketEvent;
import bodevelopment.client.blackout.event.events.TickEvent;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.OnlyDev;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.modules.misc.Timer;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.randomstuff.FindResult;
import bodevelopment.client.blackout.randomstuff.PlaceData;
import bodevelopment.client.blackout.util.OLEPOSSUtils;
import bodevelopment.client.blackout.util.SettingUtils;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import net.minecraft.class_1268;
import net.minecraft.class_1747;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_243;
import net.minecraft.class_2708;
import net.minecraft.class_2828.class_2829;
import net.minecraft.class_2828.class_2830;

@OnlyDev
public class BurrowRewrite extends Module {
   private final SettingGroup sgGeneral = this.addGroup("General");
   private final SettingGroup sgRubberband = this.addGroup("Rubberband");
   private final Setting<BurrowRewrite.BurrowMode> mode;
   private final Setting<Boolean> checkCollisions;
   private final Setting<Boolean> attack;
   private final Setting<SwitchMode> switchMode;
   private final Setting<List<class_2248>> blocks;
   private final Setting<Boolean> instant;
   private final Setting<Boolean> useTimer;
   private final Setting<Double> timer;
   private final Setting<Boolean> smartRotate;
   private final Setting<Boolean> instantRotate;
   private final Setting<Integer> jumpTicks;
   private final Setting<Double> offset;
   private final Setting<Integer> packets;
   private final Setting<Boolean> smooth;
   private final Setting<Boolean> syncPacket;
   private boolean shouldCancel;
   private int tick;
   private class_243 startPos;
   private long prevFinish;
   private boolean modifiedTimer;
   private final Predicate<class_1799> predicate;

   public BurrowRewrite() {
      super("Burrow Rewrite", ".", SubCategory.DEFENSIVE, true);
      this.mode = this.sgGeneral.e("Mode", BurrowRewrite.BurrowMode.Offset, ".");
      this.checkCollisions = this.sgGeneral.b("Check Entities", true, ".");
      this.attack = this.sgGeneral.b("Attack", true, ".");
      this.switchMode = this.sgGeneral.e("Switch Mode", SwitchMode.Silent, "Method of switching.");
      this.blocks = this.sgGeneral.bl("Blocks", "Blocks to use.", class_2246.field_10540, class_2246.field_10443);
      this.instant = this.sgGeneral.b("Instant", true, ".");
      this.useTimer = this.sgGeneral.b("Use Timer", false, ".", () -> {
         return !(Boolean)this.instant.get();
      });
      this.timer = this.sgGeneral.d("Timer", 1.0D, 1.0D, 5.0D, 0.05D, ".", () -> {
         return !(Boolean)this.instant.get() && (Boolean)this.useTimer.get();
      });
      this.smartRotate = this.sgGeneral.b("Smart Rotate", true, ".");
      this.instantRotate = this.sgGeneral.b("Instant Rotate", true, ".");
      this.jumpTicks = this.sgGeneral.i("Jump Ticks", 3, 3, 10, 1, ".");
      this.offset = this.sgRubberband.d("Offset", 1.0D, -10.0D, 10.0D, 0.2D, ".", () -> {
         return this.mode.get() == BurrowRewrite.BurrowMode.Offset;
      });
      this.packets = this.sgRubberband.i("Packets", 1, 1, 20, 1, ".", () -> {
         return this.mode.get() == BurrowRewrite.BurrowMode.Offset;
      });
      this.smooth = this.sgRubberband.b("Smooth", false, "Enabled scaffold after burrowing.");
      SettingGroup var10001 = this.sgRubberband;
      Setting var10005 = this.smooth;
      Objects.requireNonNull(var10005);
      this.syncPacket = var10001.b("Sync Packet", false, ".", var10005::get);
      this.shouldCancel = true;
      this.tick = 0;
      this.startPos = class_243.field_1353;
      this.prevFinish = 0L;
      this.modifiedTimer = false;
      this.predicate = (stack) -> {
         class_1792 patt4262$temp = stack.method_7909();
         boolean var10000;
         if (patt4262$temp instanceof class_1747) {
            class_1747 blockItem = (class_1747)patt4262$temp;
            if (((List)this.blocks.get()).contains(blockItem.method_7711())) {
               var10000 = true;
               return var10000;
            }
         }

         var10000 = false;
         return var10000;
      };
   }

   @Event
   public void onReceive(PacketEvent.Receive.Post event) {
      if (event.packet instanceof class_2708 && this.shouldCancel) {
         this.shouldCancel = false;
         event.setCancelled(true);
      }

   }

   @Event
   public void onMove(MoveEvent.Pre event) {
      if (!OLEPOSSUtils.inside(BlackOut.mc.field_1724, BlackOut.mc.field_1724.method_5829().method_1012(0.0D, (double)this.calcY(), 0.0D)) && System.currentTimeMillis() - this.prevFinish >= 1000L && !this.notFound()) {
         if (!(Boolean)this.instant.get() && (Boolean)this.useTimer.get()) {
            this.modifiedTimer = true;
            Timer.set(((Double)this.timer.get()).floatValue());
         }

         if (BlackOut.mc.field_1724.method_24828()) {
            if (this.mode.get() == BurrowRewrite.BurrowMode.Cancel) {
               this.shouldCancel = true;
               this.sendPacket(new class_2829(BlackOut.mc.field_1724.method_23317(), 1337.0D, BlackOut.mc.field_1724.method_23321(), false));
            }

            if ((Boolean)this.instant.get()) {
               class_243 prevPos = BlackOut.mc.field_1724.method_19538();
               BlackOut.mc.field_1724.method_33574(BlackOut.mc.field_1724.method_19538().method_1031(0.0D, (double)this.calcY(), 0.0D));
               PlaceData data = this.preInstant(prevPos);
               BlackOut.mc.field_1724.method_33574(prevPos);
               if (data == null) {
                  return;
               }

               double y = 0.0D;
               float yVel = 0.42F;

               for(int i = 0; i < (Integer)this.jumpTicks.get(); ++i) {
                  y += (double)yVel;
                  yVel = (yVel - 0.08F) * 0.98F;
                  this.sendPacket(new class_2829(BlackOut.mc.field_1724.method_23317(), BlackOut.mc.field_1724.method_23318() + y, BlackOut.mc.field_1724.method_23321(), false));
               }

               this.place(data);
            } else {
               this.tick = 0;
               this.startPos = BlackOut.mc.field_1724.method_19538();
               event.setY(this, 0.41999998688697815D);
               BlackOut.mc.field_1724.method_6043();
            }
         }

         if (this.tick >= 0 && !(Boolean)this.instant.get()) {
            event.setXZ(this, 0.0D, 0.0D);
            this.tickJumping();
         }

      } else {
         if (this.modifiedTimer) {
            this.modifiedTimer = false;
            Timer.reset();
         }

      }
   }

   private boolean notFound() {
      if (OLEPOSSUtils.getHand(this.predicate) == null && !((SwitchMode)this.switchMode.get()).find(this.predicate).wasFound()) {
         this.disable("no blocks found");
         return true;
      } else {
         return false;
      }
   }

   private PlaceData preInstant(class_243 prevPos) {
      PlaceData data = SettingUtils.getPlaceData(class_2338.method_49638(prevPos));
      if (!data.valid()) {
         return null;
      } else {
         return SettingUtils.shouldRotate(RotationType.BlockPlace) && !this.rotateBlock(data, RotationType.BlockPlace.withInstant((Boolean)this.instantRotate.get()), "block") ? null : data;
      }
   }

   private void tickJumping() {
      boolean lastTick = ++this.tick == (Integer)this.jumpTicks.get();
      if (lastTick) {
         this.tick = -1;
      }

      class_243 prevPos = BlackOut.mc.field_1724.method_19538();
      BlackOut.mc.field_1724.method_33574(this.startPos.method_1031(0.0D, (double)this.calcY(), 0.0D));
      PlaceData data = this.getPlaceData();
      if (data.valid()) {
         boolean rotated = this.rotateBlock(data, RotationType.BlockPlace.withInstant((Boolean)this.instantRotate.get()), "placing");
         if (lastTick) {
            if (rotated) {
               this.place(data);
            }

            this.tick = -1;
         }

         BlackOut.mc.field_1724.method_33574(prevPos);
      }
   }

   private void place(PlaceData data) {
      class_1268 hand = OLEPOSSUtils.getHand(this.predicate);
      if (hand == null) {
         FindResult result = ((SwitchMode)this.switchMode.get()).find(this.predicate);
         if (!result.wasFound() || !((SwitchMode)this.switchMode.get()).swap(result.slot())) {
            return;
         }
      }

      this.prevFinish = System.currentTimeMillis();
      this.placeBlock(hand, data);
      if (this.mode.get() == BurrowRewrite.BurrowMode.Offset) {
         this.rubberband();
      }

      if (hand == null) {
         ((SwitchMode)this.switchMode.get()).swapBack();
      }

   }

   @Event
   public void onTick(TickEvent.Post event) {
   }

   private void rubberband() {
      double x = BlackOut.mc.field_1724.method_23317();
      double y = BlackOut.mc.field_1724.method_23318() + (Double)this.offset.get();
      double z = BlackOut.mc.field_1724.method_23321();

      for(int i = 0; i < (Integer)this.packets.get(); ++i) {
         this.sendPacket(new class_2829(x, y, z, false));
      }

      if ((Boolean)this.smooth.get()) {
         this.sendPacket(Managers.PACKET.incrementedPacket(BlackOut.mc.field_1724.method_19538()));
         if ((Boolean)this.syncPacket.get()) {
            Managers.PACKET.sendInstantly(new class_2830(this.startPos.method_10216(), this.startPos.method_10214(), this.startPos.method_10215(), Managers.ROTATION.prevYaw, Managers.ROTATION.prevPitch, false));
         }

      }
   }

   private PlaceData getPlaceData() {
      return SettingUtils.getPlaceData(class_2338.method_49638(this.startPos));
   }

   private float calcY() {
      float velocity = 0.42F;
      float y = 0.0F;

      for(int i = 0; i < (Integer)this.jumpTicks.get(); ++i) {
         y += velocity;
         velocity = (velocity - 0.08F) * 0.98F;
      }

      return y;
   }

   public static enum BurrowMode {
      Offset,
      Cancel;

      // $FF: synthetic method
      private static BurrowRewrite.BurrowMode[] $values() {
         return new BurrowRewrite.BurrowMode[]{Offset, Cancel};
      }
   }
}
