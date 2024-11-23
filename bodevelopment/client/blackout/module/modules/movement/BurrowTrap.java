package bodevelopment.client.blackout.module.modules.movement;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.enums.RotationType;
import bodevelopment.client.blackout.enums.SwitchMode;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.MoveEvent;
import bodevelopment.client.blackout.event.events.TickEvent;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.modules.misc.Timer;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.randomstuff.FindResult;
import bodevelopment.client.blackout.randomstuff.PlaceData;
import bodevelopment.client.blackout.util.BoxUtils;
import bodevelopment.client.blackout.util.EntityUtils;
import bodevelopment.client.blackout.util.OLEPOSSUtils;
import bodevelopment.client.blackout.util.SettingUtils;
import java.util.List;
import java.util.Objects;
import net.minecraft.class_1268;
import net.minecraft.class_1542;
import net.minecraft.class_1747;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_3417;
import net.minecraft.class_3419;

public class BurrowTrap extends Module {
   public final SettingGroup sgGeneral = this.addGroup("General");
   private final Setting<SwitchMode> switchMode;
   public final Setting<List<class_2248>> blocks;
   private final Setting<Boolean> packet;
   private final Setting<Boolean> instantRotation;
   public final Setting<Boolean> useTimer;
   public final Setting<Double> timer;
   private class_2338 pos;
   private int progress;
   private boolean placed;
   private final double[] offsets;

   public BurrowTrap() {
      super("Burrow Trap", "Burrows without lagback.", SubCategory.MOVEMENT, true);
      this.switchMode = this.sgGeneral.e("Switch Mode", SwitchMode.Silent, "Method of switching. Silent is the most reliable but delays crystals on some servers.");
      this.blocks = this.sgGeneral.bl("Blocks", "Blocks to use.", class_2246.field_10540);
      this.packet = this.sgGeneral.b("Packet", false, ".");
      this.instantRotation = this.sgGeneral.b("Instant Rotation", true, ".");
      this.useTimer = this.sgGeneral.b("Use Timer", true, ".");
      SettingGroup var10001 = this.sgGeneral;
      Setting var10008 = this.useTimer;
      Objects.requireNonNull(var10008);
      this.timer = var10001.d("Timer", 2.0D, 0.0D, 5.0D, 0.05D, ".", var10008::get);
      this.pos = null;
      this.progress = 0;
      this.placed = false;
      this.offsets = new double[]{0.42D, 0.3332D, 0.2468D};
   }

   public void onEnable() {
      this.pos = BlackOut.mc.field_1724.method_24515();
      this.progress = -1;
      this.placed = false;
   }

   public void onDisable() {
      if ((Boolean)this.useTimer.get()) {
         Timer.reset();
      }

   }

   @Event
   public void onMove(MoveEvent.Pre event) {
      if (this.progress >= 0) {
         if (this.progress < 3) {
            double offset = this.offsets[this.progress];
            ++this.progress;
            event.set(this, 0.0D, offset, 0.0D);
         } else {
            ++this.progress;
            event.set(this, 0.0D, 0.0D, 0.0D);
            if (this.progress > 3) {
               this.disable("success");
            }
         }

      }
   }

   @Event
   public void onRender(TickEvent.Post event) {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null && this.pos != null) {
         if (!EntityUtils.intersects(BoxUtils.get(this.pos), (entity) -> {
            return !(entity instanceof class_1542) && entity != BlackOut.mc.field_1724;
         })) {
            PlaceData data = SettingUtils.getPlaceData(this.pos);
            if (data.valid()) {
               class_1268 hand = OLEPOSSUtils.getHand(this::valid);
               boolean switched = false;
               FindResult result = ((SwitchMode)this.switchMode.get()).find(this::valid);
               if (hand != null || result.wasFound()) {
                  if (this.progress == -1) {
                     this.progress = 0;
                  }

                  if ((Boolean)this.useTimer.get()) {
                     Timer.set(((Double)this.timer.get()).floatValue());
                  }

                  if (!this.placed) {
                     if (!SettingUtils.shouldRotate(RotationType.BlockPlace) || this.rotateBlock(data, RotationType.BlockPlace.withInstant((Boolean)this.instantRotation.get()), "placing")) {
                        if (!EntityUtils.intersects(BoxUtils.get(this.pos), (entity) -> {
                           return entity == BlackOut.mc.field_1724;
                        })) {
                           if (hand != null || (switched = ((SwitchMode)this.switchMode.get()).swap(result.slot()))) {
                              this.placeBlock(hand, data.pos().method_46558(), data.dir(), data.pos());
                              this.placed = true;
                              if (!(Boolean)this.packet.get()) {
                                 this.setBlock(this.pos, result.stack().method_7909());
                              }

                              if (switched) {
                                 ((SwitchMode)this.switchMode.get()).swapBack();
                              }

                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private void setBlock(class_2338 pos, class_1792 item) {
      if (item instanceof class_1747) {
         class_1747 block = (class_1747)item;
         Managers.PACKET.addToQueue((handler) -> {
            BlackOut.mc.field_1687.method_8501(pos, block.method_7711().method_9564());
            BlackOut.mc.field_1687.method_8486((double)pos.method_10263(), (double)pos.method_10264(), (double)pos.method_10260(), class_3417.field_14574, class_3419.field_15245, 1.0F, 1.0F, false);
         });
      }
   }

   private boolean valid(class_1799 stack) {
      class_1792 var3 = stack.method_7909();
      boolean var10000;
      if (var3 instanceof class_1747) {
         class_1747 block = (class_1747)var3;
         if (((List)this.blocks.get()).contains(block.method_7711())) {
            var10000 = true;
            return var10000;
         }
      }

      var10000 = false;
      return var10000;
   }
}
