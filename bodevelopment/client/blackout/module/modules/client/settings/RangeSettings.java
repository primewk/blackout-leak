package bodevelopment.client.blackout.module.modules.client.settings;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.PacketEvent;
import bodevelopment.client.blackout.event.events.TickEvent;
import bodevelopment.client.blackout.interfaces.mixin.IVec3d;
import bodevelopment.client.blackout.mixin.accessors.AccessorInteractEntityC2SPacket;
import bodevelopment.client.blackout.module.SettingsModule;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.util.BoxUtils;
import bodevelopment.client.blackout.util.OLEPOSSUtils;
import bodevelopment.client.blackout.util.RotationUtils;
import bodevelopment.client.blackout.util.SettingUtils;
import net.minecraft.class_1297;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_2596;
import net.minecraft.class_2824;
import net.minecraft.class_3532;
import net.minecraft.class_2824.class_5907;

public class RangeSettings extends SettingsModule {
   private static RangeSettings INSTANCE;
   private final SettingGroup sgInteract = this.addGroup("Interact");
   private final SettingGroup sgPlace = this.addGroup("Place");
   private final SettingGroup sgAttack = this.addGroup("Attack");
   private final SettingGroup sgMine = this.addGroup("Mine");
   public final Setting<Double> interactRange;
   public final Setting<Double> interactRangeWalls;
   public final Setting<RangeSettings.BlockRangeMode> interactRangeMode;
   public final Setting<Double> interactBlockWidth;
   public final Setting<Double> interactBlockHeight;
   public final Setting<Double> interactHeight;
   public final Setting<Double> placeRange;
   public final Setting<Double> placeRangeWalls;
   public final Setting<RangeSettings.BlockRangeMode> placeRangeMode;
   public final Setting<Double> blockWidth;
   public final Setting<Double> blockHeight;
   public final Setting<Double> placeHeight;
   public final Setting<Double> attackRange;
   public final Setting<RangeSettings.AttackRangeMode> attackRangeMode;
   public final Setting<Double> closestAttackWidth;
   public final Setting<Double> closestAttackHeight;
   public final Setting<Double> attackRangeWalls;
   public final Setting<RangeSettings.AttackRangeMode> wallAttackRangeMode;
   public final Setting<Double> closestWallAttackWidth;
   public final Setting<Double> closestWallAttackHeight;
   public final Setting<Boolean> reduce;
   public final Setting<Boolean> wallReduce;
   public final Setting<Double> reduceAmount;
   public final Setting<Double> reduceStep;
   public final Setting<Double> mineRange;
   public final Setting<Double> mineRangeWalls;
   public final Setting<RangeSettings.MineRangeMode> mineRangeMode;
   public final Setting<Double> closestMiningWidth;
   public final Setting<Double> closestMiningHeight;
   public final Setting<Double> miningHeight;
   public double reducedAmount;

   public RangeSettings() {
      super("Range", false, true);
      this.interactRange = this.sgInteract.d("Interact Range", 5.2D, 0.0D, 6.0D, 0.05D, "Range for interacting with blocks.");
      this.interactRangeWalls = this.sgInteract.d("Interact Range Walls", 5.2D, 0.0D, 6.0D, 0.05D, "Range for interacting behind blocks.");
      this.interactRangeMode = this.sgInteract.e("Interact Range Mode", RangeSettings.BlockRangeMode.NCP, "Where to calculate place ranges from.");
      this.interactBlockWidth = this.sgInteract.d("Interact Block Width", 1.0D, 0.0D, 2.0D, 0.05D, "How wide should the box be for closest place range.", () -> {
         return this.interactRangeMode.get() == RangeSettings.BlockRangeMode.CustomBox;
      });
      this.interactBlockHeight = this.sgInteract.d("Interact Block Height", 1.0D, 0.0D, 2.0D, 0.05D, "How high should the box be for closest place range.", () -> {
         return this.interactRangeMode.get() == RangeSettings.BlockRangeMode.CustomBox;
      });
      this.interactHeight = this.sgInteract.d("Interact Height", 0.5D, 0.0D, 1.0D, 0.05D, "The height to calculate ranges from.", () -> {
         return this.interactRangeMode.get() == RangeSettings.BlockRangeMode.Height;
      });
      this.placeRange = this.sgPlace.d("Place Range", 5.2D, 0.0D, 6.0D, 0.05D, "Range for placing.");
      this.placeRangeWalls = this.sgPlace.d("Place Range Walls", 5.2D, 0.0D, 6.0D, 0.05D, "Range for placing behind blocks.");
      this.placeRangeMode = this.sgPlace.e("Place Range Mode", RangeSettings.BlockRangeMode.NCP, "Where to calculate place ranges from.");
      this.blockWidth = this.sgPlace.d("Block Width", 1.0D, 0.0D, 2.0D, 0.05D, "How wide should the box be for closest place range.", () -> {
         return this.placeRangeMode.get() == RangeSettings.BlockRangeMode.CustomBox;
      });
      this.blockHeight = this.sgPlace.d("Block Height", 1.0D, 0.0D, 2.0D, 0.05D, "How high should the box be for closest place range.", () -> {
         return this.placeRangeMode.get() == RangeSettings.BlockRangeMode.CustomBox;
      });
      this.placeHeight = this.sgPlace.d("Place Height", 0.5D, 0.0D, 1.0D, 0.05D, "The height to calculate ranges from.", () -> {
         return this.placeRangeMode.get() == RangeSettings.BlockRangeMode.Height;
      });
      this.attackRange = this.sgAttack.d("Attack Range", 4.8D, 0.0D, 6.0D, 0.05D, "Range for attacking entities.");
      this.attackRangeMode = this.sgAttack.e("Attack Range Mode", RangeSettings.AttackRangeMode.NCP, "Where to calculate attack ranges from.");
      this.closestAttackWidth = this.sgAttack.d("Closest Attack Width", 1.0D, 0.0D, 3.0D, 0.05D, "How wide should the box be for closest range.", () -> {
         return ((RangeSettings.AttackRangeMode)this.attackRangeMode.get()).equals(RangeSettings.AttackRangeMode.CustomBox);
      });
      this.closestAttackHeight = this.sgAttack.d("Closest Attack Height", 1.0D, 0.0D, 3.0D, 0.05D, "How high should the box be for closest range.", () -> {
         return ((RangeSettings.AttackRangeMode)this.attackRangeMode.get()).equals(RangeSettings.AttackRangeMode.CustomBox);
      });
      this.attackRangeWalls = this.sgAttack.d("Attack Range Walls", 4.8D, 0.0D, 6.0D, 0.05D, "Range for attacking entities behind blocks.");
      this.wallAttackRangeMode = this.sgAttack.e("Wall Attack Range Mode", RangeSettings.AttackRangeMode.NCP, "Where to calculate attack ranges from.");
      this.closestWallAttackWidth = this.sgAttack.d("Closest Wall Attack Width", 1.0D, 0.0D, 3.0D, 0.05D, "How wide should the box be for closest range.", () -> {
         return ((RangeSettings.AttackRangeMode)this.wallAttackRangeMode.get()).equals(RangeSettings.AttackRangeMode.CustomBox);
      });
      this.closestWallAttackHeight = this.sgAttack.d("Closest Wall Attack Height", 1.0D, 0.0D, 3.0D, 0.05D, "How high should the box be for closest range.", () -> {
         return ((RangeSettings.AttackRangeMode)this.wallAttackRangeMode.get()).equals(RangeSettings.AttackRangeMode.CustomBox);
      });
      this.reduce = this.sgAttack.b("Reduce", false, "Reduces range on every hit by reduce step until it reaches (range - reduce amount).");
      this.wallReduce = this.sgAttack.b("Wall Reduce", false, ".");
      this.reduceAmount = this.sgAttack.d("Reduce Amount", 0.8D, 0.0D, 6.0D, 0.05D, "Check description from 'Reduce' setting.", () -> {
         return (Boolean)this.reduce.get() || (Boolean)this.wallReduce.get();
      });
      this.reduceStep = this.sgAttack.d("Reduce Step", 0.14D, 0.0D, 1.0D, 0.01D, "Check description from 'Reduce' setting.", () -> {
         return (Boolean)this.reduce.get() || (Boolean)this.wallReduce.get();
      });
      this.mineRange = this.sgMine.d("Mine Range", 5.2D, 0.0D, 6.0D, 0.05D, "Range for mining.");
      this.mineRangeWalls = this.sgMine.d("Mine Range Walls", 5.2D, 0.0D, 6.0D, 0.05D, "Range for mining behind blocks.");
      this.mineRangeMode = this.sgMine.e("Mine Range Mode", RangeSettings.MineRangeMode.NCP, "Where to calculate mining ranges from.");
      this.closestMiningWidth = this.sgMine.d("Closest Mine Width", 1.0D, 0.0D, 3.0D, 0.05D, "How wide should the box be for closest range.", () -> {
         return this.mineRangeMode.get() == RangeSettings.MineRangeMode.CustomBox;
      });
      this.closestMiningHeight = this.sgMine.d("Closest Mine Height", 1.0D, 0.0D, 3.0D, 0.05D, "How tall should the box be for closest range.", () -> {
         return this.mineRangeMode.get() == RangeSettings.MineRangeMode.CustomBox;
      });
      this.miningHeight = this.sgMine.d("Mine Height", 0.5D, 0.0D, 1.0D, 0.05D, "The height above block bottom to calculate ranges from.", () -> {
         return this.mineRangeMode.get() == RangeSettings.MineRangeMode.Height;
      });
      this.reducedAmount = 0.0D;
      INSTANCE = this;
   }

   public static RangeSettings getInstance() {
      return INSTANCE;
   }

   @Event
   public void onAttack(PacketEvent.Sent event) {
      class_2596 var3 = event.packet;
      if (var3 instanceof class_2824) {
         class_2824 packet = (class_2824)var3;
         if (((AccessorInteractEntityC2SPacket)packet).getType().method_34211() == class_5907.field_29172) {
            class_1297 entity = BlackOut.mc.field_1687.method_8469(((AccessorInteractEntityC2SPacket)packet).getId());
            if (entity != null) {
               this.registerAttack(entity.method_5829());
            }
         }
      }

   }

   @Event
   public void onTick(TickEvent.Pre event) {
      this.reducedAmount = class_3532.method_15350(this.reducedAmount, 0.0D, (Double)this.reduceAmount.get());
   }

   private void registerAttack(class_238 bb) {
      double distance = this.attackRangeTo(bb, (class_243)null);
      double range = SettingUtils.attackTrace(bb) ? (Double)this.attackRange.get() : (Double)this.attackRangeWalls.get();
      if (distance <= range - (Double)this.reduceAmount.get()) {
         this.reducedAmount = Math.max(this.reducedAmount - (Double)this.reduceStep.get(), 0.0D);
      } else {
         this.reducedAmount = Math.min(this.reducedAmount + (Double)this.reduceStep.get(), (Double)this.reduceAmount.get());
      }

   }

   public boolean inInteractRange(class_2338 pos, class_243 from) {
      double dist = this.interactRangeTo(pos, from);
      return dist >= 0.0D && dist <= SettingUtils.interactTrace(pos) ? (Double)this.interactRange.get() : (Double)this.interactRangeWalls.get();
   }

   public boolean inInteractRangeNoTrace(class_2338 pos, class_243 from) {
      double dist = this.interactRangeTo(pos, from);
      return dist >= 0.0D && dist <= Math.max((Double)this.interactRange.get(), (Double)this.interactRangeWalls.get());
   }

   public double interactRangeTo(class_2338 pos, class_243 from) {
      if (from == null) {
         from = BlackOut.mc.field_1724.method_19538();
      }

      from = from.method_1031(0.0D, (double)BlackOut.mc.field_1724.method_18381(BlackOut.mc.field_1724.method_18376()), 0.0D);
      class_243 bottom = new class_243((double)pos.method_10263() + 0.5D, (double)pos.method_10264(), (double)pos.method_10260() + 0.5D);
      double var10000;
      switch((RangeSettings.BlockRangeMode)this.interactRangeMode.get()) {
      case NCP:
         var10000 = from.method_1022(bottom.method_1031(0.0D, 0.5D, 0.0D));
         break;
      case Height:
         var10000 = from.method_1022(bottom.method_1031(0.0D, (Double)this.interactHeight.get(), 0.0D));
         break;
      case Vanilla:
         var10000 = from.method_1022(OLEPOSSUtils.getClosest(BlackOut.mc.field_1724.method_33571(), bottom, 1.0D, 1.0D));
         break;
      case CustomBox:
         var10000 = from.method_1022(OLEPOSSUtils.getClosest(BlackOut.mc.field_1724.method_33571(), bottom, (Double)this.interactBlockWidth.get(), (Double)this.interactBlockHeight.get()));
         break;
      default:
         throw new IncompatibleClassChangeError();
      }

      return var10000;
   }

   public boolean inPlaceRange(class_2338 pos, class_243 from) {
      double dist = this.placeRangeTo(pos, from);
      return dist >= 0.0D && dist <= SettingUtils.placeTrace(pos) ? (Double)this.placeRange.get() : (Double)this.placeRangeWalls.get();
   }

   public boolean inPlaceRangeNoTrace(class_2338 pos, class_243 from) {
      double dist = this.placeRangeTo(pos, from);
      return dist >= 0.0D && dist <= Math.max((Double)this.placeRange.get(), (Double)this.placeRangeWalls.get());
   }

   public double placeRangeTo(class_2338 pos, class_243 from) {
      if (from == null) {
         from = BlackOut.mc.field_1724.method_19538();
      }

      from = from.method_1031(0.0D, (double)BlackOut.mc.field_1724.method_18381(BlackOut.mc.field_1724.method_18376()), 0.0D);
      class_243 feet = new class_243((double)pos.method_10263() + 0.5D, (double)pos.method_10264(), (double)pos.method_10260() + 0.5D);
      double var10000;
      switch((RangeSettings.BlockRangeMode)this.placeRangeMode.get()) {
      case NCP:
         var10000 = from.method_1022(feet.method_1031(0.0D, 0.5D, 0.0D));
         break;
      case Height:
         var10000 = from.method_1022(feet.method_1031(0.0D, (Double)this.placeHeight.get(), 0.0D));
         break;
      case Vanilla:
         var10000 = from.method_1022(OLEPOSSUtils.getClosest(BlackOut.mc.field_1724.method_33571(), feet, 1.0D, 1.0D));
         break;
      case CustomBox:
         var10000 = from.method_1022(OLEPOSSUtils.getClosest(BlackOut.mc.field_1724.method_33571(), feet, (Double)this.blockWidth.get(), (Double)this.blockHeight.get()));
         break;
      default:
         throw new IncompatibleClassChangeError();
      }

      return var10000;
   }

   public boolean inAttackRange(class_238 bb, class_243 from) {
      boolean visible = SettingUtils.attackTrace(bb);
      return this.innerAttackRangeTo(bb, from, !visible) <= this.reducedRange(visible ? this.attackRange : this.attackRangeWalls, visible);
   }

   private double reducedRange(Setting<Double> distance, boolean walls) {
      return (Boolean)(walls ? this.wallReduce : this.reduce).get() ? (Double)distance.get() - this.reducedAmount : (Double)distance.get();
   }

   public boolean inAttackRangeNoTrace(class_238 bb, class_243 from) {
      return this.attackRangeTo(bb, from) <= this.reducedRange(this.attackRange, false);
   }

   private double wallAttackRangeTo(class_238 bb, class_243 from) {
      return this.innerAttackRangeTo(bb, from, true);
   }

   private double attackRangeTo(class_238 bb, class_243 from) {
      return this.innerAttackRangeTo(bb, from, false);
   }

   public double innerAttackRangeTo(class_238 bb, class_243 from, boolean walls) {
      RangeSettings.AttackRangeMode mode = (RangeSettings.AttackRangeMode)(walls ? this.wallAttackRangeMode : this.attackRangeMode).get();
      if (from == null) {
         from = BlackOut.mc.field_1724.method_19538();
      }

      if (mode != RangeSettings.AttackRangeMode.Simple) {
         from = from.method_1031(0.0D, (double)BlackOut.mc.field_1724.method_18381(BlackOut.mc.field_1724.method_18376()), 0.0D);
      }

      double var10000;
      class_243 feet;
      switch(mode) {
      case NCP:
         feet = BoxUtils.feet(bb);
         var10000 = from.method_1022(new class_243(feet.field_1352, Math.min(Math.max(from.method_10214(), bb.field_1322), bb.field_1325), feet.field_1350));
         break;
      case Vanilla:
         var10000 = from.method_1022(OLEPOSSUtils.getClosest(BlackOut.mc.field_1724.method_33571(), BoxUtils.feet(bb), Math.abs(bb.field_1323 - bb.field_1320), Math.abs(bb.field_1322 - bb.field_1325)));
         break;
      case Middle:
         var10000 = from.method_1022(new class_243((bb.field_1323 + bb.field_1320) / 2.0D, (bb.field_1322 + bb.field_1325) / 2.0D, (bb.field_1321 + bb.field_1324) / 2.0D));
         break;
      case CustomBox:
         var10000 = from.method_1022(OLEPOSSUtils.getClosest(BlackOut.mc.field_1724.method_33571(), BoxUtils.feet(bb), Math.abs(bb.field_1323 - bb.field_1320) * (Double)this.closestWallAttackWidth.get(), Math.abs(bb.field_1322 - bb.field_1325) * (Double)this.closestWallAttackHeight.get()));
         break;
      case UpdatedNCP:
         feet = BoxUtils.feet(bb);
         var10000 = from.method_1022(new class_243(feet.field_1352, Math.min(Math.max(from.method_10214(), bb.field_1322), bb.field_1325), feet.field_1350)) - this.getDistFromCenter(bb, feet, from);
         break;
      case Simple:
         var10000 = from.method_1022(BoxUtils.feet(bb));
         break;
      default:
         throw new IncompatibleClassChangeError();
      }

      return var10000;
   }

   public double getDistFromCenter(class_238 bb, class_243 feet, class_243 from) {
      class_243 pos = new class_243(feet.field_1352, feet.field_1351, feet.field_1350);
      class_243 vec1 = new class_243(from.method_10216() - pos.method_10216(), 0.0D, from.method_10215() - pos.method_10215());
      double halfWidth = bb.method_17939() / 2.0D;
      if (vec1.method_1033() < halfWidth * (double)class_3532.field_15724) {
         return 0.0D;
      } else {
         if (vec1.method_10215() > 0.0D) {
            ((IVec3d)pos).blackout_Client$setZ(pos.method_10215() + halfWidth);
         } else if (vec1.method_10215() < 0.0D) {
            ((IVec3d)pos).blackout_Client$setZ(pos.method_10215() - halfWidth);
         } else if (vec1.method_10216() > 0.0D) {
            ((IVec3d)pos).blackout_Client$setX(pos.method_10216() + halfWidth);
         } else {
            ((IVec3d)pos).blackout_Client$setX(pos.method_10216() - halfWidth);
         }

         class_243 vec2 = new class_243(pos.method_10216() - feet.method_10216(), 0.0D, pos.method_10215() - feet.method_10215());
         double angle = RotationUtils.radAngle(vec1, vec2);
         if (angle > 0.7853981633974483D) {
            angle = 1.5707963267948966D - angle;
         }

         return angle >= 0.0D && angle <= 0.7853981633974483D ? halfWidth / Math.cos(angle) : 0.0D;
      }
   }

   public boolean inMineRange(class_2338 pos) {
      double dist = this.miningRangeTo(pos, (class_243)null);
      return dist >= 0.0D && dist <= SettingUtils.mineTrace(pos) ? (Double)this.mineRange.get() : (Double)this.mineRangeWalls.get();
   }

   public boolean inMineRangeNoTrace(class_2338 pos) {
      double dist = this.miningRangeTo(pos, (class_243)null);
      return dist >= 0.0D && dist <= Math.max((Double)this.mineRange.get(), (Double)this.mineRangeWalls.get());
   }

   public double miningRangeTo(class_2338 pos, class_243 from) {
      if (from == null) {
         from = BlackOut.mc.field_1724.method_19538();
      }

      from = from.method_1031(0.0D, (double)BlackOut.mc.field_1724.method_18381(BlackOut.mc.field_1724.method_18376()), 0.0D);
      class_243 feet = new class_243((double)pos.method_10263() + 0.5D, (double)pos.method_10264(), (double)pos.method_10260() + 0.5D);
      double var10000;
      switch((RangeSettings.MineRangeMode)this.mineRangeMode.get()) {
      case NCP:
         var10000 = from.method_1022(feet.method_1031(0.0D, 0.5D, 0.0D));
         break;
      case Height:
         var10000 = from.method_1022(feet.method_1031(0.0D, (Double)this.miningHeight.get(), 0.0D));
         break;
      case Vanilla:
         var10000 = from.method_1022(OLEPOSSUtils.getClosest(BlackOut.mc.field_1724.method_33571(), feet, 1.0D, 1.0D));
         break;
      case CustomBox:
         var10000 = from.method_1022(OLEPOSSUtils.getClosest(BlackOut.mc.field_1724.method_33571(), feet, (Double)this.closestMiningWidth.get(), (Double)this.closestMiningHeight.get()));
         break;
      default:
         throw new IncompatibleClassChangeError();
      }

      return var10000;
   }

   public static enum BlockRangeMode {
      NCP,
      Height,
      Vanilla,
      CustomBox;

      // $FF: synthetic method
      private static RangeSettings.BlockRangeMode[] $values() {
         return new RangeSettings.BlockRangeMode[]{NCP, Height, Vanilla, CustomBox};
      }
   }

   public static enum AttackRangeMode {
      NCP,
      UpdatedNCP,
      Simple,
      Vanilla,
      Middle,
      CustomBox;

      // $FF: synthetic method
      private static RangeSettings.AttackRangeMode[] $values() {
         return new RangeSettings.AttackRangeMode[]{NCP, UpdatedNCP, Simple, Vanilla, Middle, CustomBox};
      }
   }

   public static enum MineRangeMode {
      NCP,
      Height,
      Vanilla,
      CustomBox;

      // $FF: synthetic method
      private static RangeSettings.MineRangeMode[] $values() {
         return new RangeSettings.MineRangeMode[]{NCP, Height, Vanilla, CustomBox};
      }
   }
}
