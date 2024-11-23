package bodevelopment.client.blackout.module.modules.client.settings;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.enums.RotationType;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.TickEvent;
import bodevelopment.client.blackout.interfaces.functional.DoubleConsumer;
import bodevelopment.client.blackout.interfaces.functional.SingleOut;
import bodevelopment.client.blackout.interfaces.mixin.IRaycastContext;
import bodevelopment.client.blackout.interfaces.mixin.IVec3d;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.module.SettingsModule;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.randomstuff.Rotation;
import bodevelopment.client.blackout.util.BoxUtils;
import bodevelopment.client.blackout.util.DamageUtils;
import bodevelopment.client.blackout.util.OLEPOSSUtils;
import bodevelopment.client.blackout.util.RotationUtils;
import bodevelopment.client.blackout.util.SettingUtils;
import it.unimi.dsi.fastutil.floats.FloatFloatPair;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_265;
import net.minecraft.class_2680;
import net.minecraft.class_3532;
import net.minecraft.class_3965;
import net.minecraft.class_239.class_240;
import net.minecraft.class_2828.class_2830;

public class RotationSettings extends SettingsModule {
   private static RotationSettings INSTANCE;
   private final SettingGroup sgGeneral = this.addGroup("General");
   private final SettingGroup sgInteract = this.addGroup("Interact");
   private final SettingGroup sgBlockPlace = this.addGroup("Block Place");
   private final SettingGroup sgMining = this.addGroup("Mining");
   private final SettingGroup sgAttack = this.addGroup("Attack");
   public final Setting<Integer> renderSmoothness;
   public final Setting<Boolean> vanillaRotation;
   public final Setting<RotationSettings.RotationSpeedMode> rotationSpeedMode;
   public final Setting<Boolean> timeBasedSpeed;
   public final Setting<Double> maxMulti;
   public final Setting<Boolean> pauseRotated;
   public final Setting<Integer> delay;
   public final Setting<Integer> averageTicks;
   public final Setting<Double> averageSpeed;
   public final Setting<Double> maxSpeed;
   public final Setting<Double> yawStep;
   public final Setting<Double> pitchStep;
   public final Setting<Double> rotationSpeed;
   public final Setting<Double> minSmoothSpeed;
   public final Setting<Double> maxSmoothSpeed;
   public final Setting<Double> minSmoothAngle;
   public final Setting<Double> maxSmoothAngle;
   public final Setting<Double> yawRandom;
   public final Setting<Double> pitchRandom;
   public final Setting<Double> returnSpeed;
   private final Setting<Integer> jitterStrength;
   public final Setting<RotationSettings.PacketRotationMode> packetRotationMode;
   public final Setting<Boolean> smartPacket;
   public final Setting<Double> packetRotations;
   public final Setting<Boolean> instantPacketRotation;
   private final Setting<Boolean> interactRotate;
   public final Setting<RotationSettings.BlockRotationCheckMode> interactMode;
   public final Setting<Double> interactYawAngle;
   public final Setting<Double> interactPitchAngle;
   public final Setting<Double> interactUpExpand;
   public final Setting<Double> interactDownExpand;
   public final Setting<Double> interactXZExpand;
   private final Setting<Boolean> blockRotate;
   public final Setting<RotationSettings.BlockRotationCheckMode> blockMode;
   public final Setting<Double> blockYawAngle;
   public final Setting<Double> blockPitchAngle;
   public final Setting<Double> blockUpExpand;
   public final Setting<Double> blockDownExpand;
   public final Setting<Double> blockXZExpand;
   private final Setting<Boolean> mineRotate;
   public final Setting<RotationSettings.BlockRotationCheckMode> mineMode;
   public final Setting<RotationSettings.MiningRotMode> mineTiming;
   public final Setting<Double> mineYawAngle;
   public final Setting<Double> minePitchAngle;
   public final Setting<Double> mineUpExpand;
   public final Setting<Double> mineDownExpand;
   public final Setting<Double> mineXZExpand;
   private final Setting<Boolean> attackRotate;
   public final Setting<RotationSettings.RotationCheckMode> attackMode;
   public final Setting<Double> attackYawAngle;
   public final Setting<Double> attackPitchAngle;
   public final Setting<Boolean> attackLimit;
   public final Setting<Double> attackMaxSpeed;
   public final Setting<Integer> attackTicks;
   public final Setting<Double> noOwnTime;
   public final Setting<Double> noOtherTime;
   private int sinceRotated;
   public final class_243 vec;

   public RotationSettings() {
      super("Rotate", false, true);
      this.renderSmoothness = this.sgGeneral.i("Render Smoothness", 1, 0, 10, 1, ".");
      this.vanillaRotation = this.sgGeneral.b("Client Rotation", false, "Turns your head.");
      this.rotationSpeedMode = this.sgGeneral.e("Rotation Speed Mode", RotationSettings.RotationSpeedMode.Normal, ".");
      this.timeBasedSpeed = this.sgGeneral.b("Time Based Speed", true, ".", () -> {
         return this.rotationSpeedMode.get() != RotationSettings.RotationSpeedMode.Balance;
      });
      this.maxMulti = this.sgGeneral.d("Max Multi", 1.0D, 0.0D, 5.0D, 0.05D, ".", () -> {
         return this.rotationSpeedMode.get() != RotationSettings.RotationSpeedMode.Balance && (Boolean)this.timeBasedSpeed.get();
      });
      this.pauseRotated = this.sgGeneral.b("Pause Rotated", false, ".");
      SettingGroup var10001 = this.sgGeneral;
      Setting var10008 = this.pauseRotated;
      Objects.requireNonNull(var10008);
      this.delay = var10001.i("Delay", 3, 0, 10, 1, ".", var10008::get);
      this.averageTicks = this.sgGeneral.i("Average Ticks", 10, 0, 20, 1, ".", () -> {
         return this.rotationSpeedMode.get() == RotationSettings.RotationSpeedMode.Balance;
      });
      this.averageSpeed = this.sgGeneral.d("Average Speed", 20.0D, 0.0D, 100.0D, 1.0D, ".", () -> {
         return this.rotationSpeedMode.get() == RotationSettings.RotationSpeedMode.Balance;
      });
      this.maxSpeed = this.sgGeneral.d("Max Speed", 60.0D, 0.0D, 100.0D, 1.0D, ".", () -> {
         return this.rotationSpeedMode.get() == RotationSettings.RotationSpeedMode.Balance;
      });
      this.yawStep = this.sgGeneral.d("Yaw Step", 90.0D, 0.0D, 180.0D, 1.0D, "How many yaw degrees should be rotated each packet.", () -> {
         return this.rotationSpeedMode.get() == RotationSettings.RotationSpeedMode.Separate;
      });
      this.pitchStep = this.sgGeneral.d("Pitch Step", 45.0D, 0.0D, 180.0D, 1.0D, "How many pitch degrees should be rotated each packet.", () -> {
         return this.rotationSpeedMode.get() == RotationSettings.RotationSpeedMode.Separate;
      });
      this.rotationSpeed = this.sgGeneral.d("Rotation Speed", 90.0D, 0.0D, 360.0D, 1.0D, "How many degrees should be rotated each packet.", () -> {
         return this.rotationSpeedMode.get() == RotationSettings.RotationSpeedMode.Normal;
      });
      this.minSmoothSpeed = this.sgGeneral.d("Min Smooth Speed", 10.0D, 0.0D, 100.0D, 1.0D, ".", () -> {
         return this.rotationSpeedMode.get() == RotationSettings.RotationSpeedMode.Smooth;
      });
      this.maxSmoothSpeed = this.sgGeneral.d("Max Smooth Speed", 75.0D, 0.0D, 100.0D, 1.0D, ".", () -> {
         return this.rotationSpeedMode.get() == RotationSettings.RotationSpeedMode.Smooth;
      });
      this.minSmoothAngle = this.sgGeneral.d("Min Smooth Angle", 5.0D, 0.0D, 100.0D, 1.0D, ".", () -> {
         return this.rotationSpeedMode.get() == RotationSettings.RotationSpeedMode.Smooth;
      });
      this.maxSmoothAngle = this.sgGeneral.d("Max Smooth Angle", 135.0D, 0.0D, 100.0D, 1.0D, ".", () -> {
         return this.rotationSpeedMode.get() == RotationSettings.RotationSpeedMode.Smooth;
      });
      this.yawRandom = this.sgGeneral.d("Yaw Random", 1.0D, 0.0D, 10.0D, 0.1D, ".", () -> {
         return this.rotationSpeedMode.get() == RotationSettings.RotationSpeedMode.Separate;
      });
      this.pitchRandom = this.sgGeneral.d("Pitch Random", 1.0D, 0.0D, 10.0D, 0.1D, ".", () -> {
         return this.rotationSpeedMode.get() == RotationSettings.RotationSpeedMode.Separate;
      });
      this.returnSpeed = this.sgGeneral.d("Return Speed", 20.0D, 0.0D, 180.0D, 1.0D, ".");
      this.jitterStrength = this.sgGeneral.i("Jitter Strength", 1, 0, 10, 1, ".");
      this.packetRotationMode = this.sgGeneral.e("Packet Rotation Mode", RotationSettings.PacketRotationMode.Disabled, ".");
      this.smartPacket = this.sgGeneral.b("Smart Packet", true, "Only packet rotates if you arent already looking at the target.", () -> {
         return this.packetRotationMode.get() != RotationSettings.PacketRotationMode.Disabled;
      });
      this.packetRotations = this.sgGeneral.d("Packet Rotations", 1.0D, 1.0D, 10.0D, 0.1D, ".", () -> {
         return this.packetRotationMode.get() != RotationSettings.PacketRotationMode.Disabled;
      });
      this.instantPacketRotation = this.sgGeneral.b("Instant Packet Rotation", true, ".", () -> {
         return this.packetRotationMode.get() != RotationSettings.PacketRotationMode.Disabled;
      });
      this.interactRotate = this.rotateSetting("Interact", "interacting with a block", this.sgInteract);
      this.interactMode = this.blockModeSetting("Interact", this.sgInteract);
      this.interactYawAngle = this.yawAngleSetting("Interact", this.sgInteract, () -> {
         return this.interactMode.get() == RotationSettings.BlockRotationCheckMode.Angle;
      });
      this.interactPitchAngle = this.pitchAngleSetting("Interact", this.sgInteract, () -> {
         return this.interactMode.get() == RotationSettings.BlockRotationCheckMode.Angle;
      });
      this.interactUpExpand = this.upExpandSetting("Interact", this.sgInteract, () -> {
         return this.interactMode.get() == RotationSettings.BlockRotationCheckMode.Raytrace;
      });
      this.interactDownExpand = this.downExpandSetting("Interact", this.sgInteract, () -> {
         return this.interactMode.get() == RotationSettings.BlockRotationCheckMode.Raytrace;
      });
      this.interactXZExpand = this.hzExpandSetting("Interact", this.sgInteract, () -> {
         return this.interactMode.get() == RotationSettings.BlockRotationCheckMode.Raytrace;
      });
      this.blockRotate = this.rotateSetting("Block Place", "placing a block", this.sgBlockPlace);
      this.blockMode = this.blockModeSetting("Block Place", this.sgBlockPlace);
      this.blockYawAngle = this.yawAngleSetting("Block Place", this.sgBlockPlace, () -> {
         return this.blockMode.get() == RotationSettings.BlockRotationCheckMode.Angle;
      });
      this.blockPitchAngle = this.pitchAngleSetting("Block Place", this.sgBlockPlace, () -> {
         return this.blockMode.get() == RotationSettings.BlockRotationCheckMode.Angle;
      });
      this.blockUpExpand = this.upExpandSetting("Block Place", this.sgBlockPlace, () -> {
         return this.blockMode.get() == RotationSettings.BlockRotationCheckMode.Raytrace;
      });
      this.blockDownExpand = this.downExpandSetting("Block Place", this.sgBlockPlace, () -> {
         return this.blockMode.get() == RotationSettings.BlockRotationCheckMode.Raytrace;
      });
      this.blockXZExpand = this.hzExpandSetting("Block Place", this.sgBlockPlace, () -> {
         return this.blockMode.get() == RotationSettings.BlockRotationCheckMode.Raytrace;
      });
      this.mineRotate = this.rotateSetting("Mining", "mining a block", this.sgMining);
      this.mineMode = this.blockModeSetting("Mining", this.sgMining);
      this.mineTiming = this.sgMining.e("Mining Rotate Timing", RotationSettings.MiningRotMode.End, ".");
      this.mineYawAngle = this.yawAngleSetting("Mining", this.sgMining, () -> {
         return this.mineMode.get() == RotationSettings.BlockRotationCheckMode.Angle;
      });
      this.minePitchAngle = this.pitchAngleSetting("Mining", this.sgMining, () -> {
         return this.mineMode.get() == RotationSettings.BlockRotationCheckMode.Angle;
      });
      this.mineUpExpand = this.upExpandSetting("Mining", this.sgMining, () -> {
         return this.mineMode.get() == RotationSettings.BlockRotationCheckMode.Raytrace;
      });
      this.mineDownExpand = this.downExpandSetting("Mining", this.sgMining, () -> {
         return this.mineMode.get() == RotationSettings.BlockRotationCheckMode.Raytrace;
      });
      this.mineXZExpand = this.hzExpandSetting("Mining", this.sgMining, () -> {
         return this.mineMode.get() == RotationSettings.BlockRotationCheckMode.Raytrace;
      });
      this.attackRotate = this.rotateSetting("Attack", "attacking an entity", this.sgAttack);
      this.attackMode = this.modeSetting("Attack", this.sgAttack);
      this.attackYawAngle = this.yawAngleSetting("Attack", this.sgAttack, () -> {
         return this.attackMode.get() == RotationSettings.RotationCheckMode.Angle;
      });
      this.attackPitchAngle = this.pitchAngleSetting("Attack", this.sgAttack, () -> {
         return this.attackMode.get() == RotationSettings.RotationCheckMode.Angle;
      });
      this.attackLimit = this.sgAttack.b("Attack Limit", false, ".");
      this.attackMaxSpeed = this.sgAttack.d("Max Attack Speed", 30.0D, 0.0D, 100.0D, 1.0D, ".");
      this.attackTicks = this.sgAttack.i("Attack Ticks", 10, 0, 50, 1, ".");
      this.noOwnTime = this.sgAttack.d("No Own Rotate", 0.0D, 0.0D, 5.0D, 0.05D, ".");
      this.noOtherTime = this.sgAttack.d("No Other Rotate", 0.0D, 0.0D, 5.0D, 0.05D, ".");
      this.sinceRotated = 0;
      this.vec = new class_243(0.0D, 0.0D, 0.0D);
      INSTANCE = this;
   }

   public static RotationSettings getInstance() {
      return INSTANCE;
   }

   private Setting<Boolean> rotateSetting(String type, String verb, SettingGroup sg) {
      return sg.b(type + " Rotate", false, "Rotates when + " + verb);
   }

   private Setting<RotationSettings.RotationCheckMode> modeSetting(String type, SettingGroup sg) {
      return sg.e(type + " Mode", RotationSettings.RotationCheckMode.Raytrace, ".");
   }

   private Setting<RotationSettings.BlockRotationCheckMode> blockModeSetting(String type, SettingGroup sg) {
      return sg.e(type + " Mode", RotationSettings.BlockRotationCheckMode.Raytrace, ".");
   }

   private Setting<Double> yawAngleSetting(String type, SettingGroup sg, SingleOut<Boolean> visible) {
      return sg.d(type + " Yaw Angle", 90.0D, 0.0D, 180.0D, 1.0D, "Accepts rotation if yaw angle to target is under this.", visible);
   }

   private Setting<Double> pitchAngleSetting(String type, SettingGroup sg, SingleOut<Boolean> visible) {
      return sg.d(type + " Pitch Angle", 45.0D, 0.0D, 180.0D, 1.0D, "Accepts rotation if yaw angle to target is under this.", visible);
   }

   private Setting<Double> upExpandSetting(String type, SettingGroup sg, SingleOut<Boolean> visible) {
      return sg.d(type + " Up Expand", 0.0D, 0.0D, 1.0D, 0.01D, ".", visible);
   }

   private Setting<Double> downExpandSetting(String type, SettingGroup sg, SingleOut<Boolean> visible) {
      return sg.d(type + " Down Expand", 0.0D, 0.0D, 1.0D, 0.01D, ".", visible);
   }

   private Setting<Double> hzExpandSetting(String type, SettingGroup sg, SingleOut<Boolean> visible) {
      return sg.d(type + " XZ Expand", 0.0D, 0.0D, 1.0D, 0.01D, ".", visible);
   }

   public boolean blockRotationCheck(class_2338 pos, class_2350 dir, float yaw, float pitch, RotationType type) {
      RotationSettings.BlockRotationCheckMode m = this.mode(type);
      class_238 box;
      class_243 end;
      switch(m) {
      case Raytrace:
         box = this.expandedBox(pos, type);
         if (box.method_1006(BlackOut.mc.field_1724.method_33571())) {
            return true;
         }

         if (this.raytraceCheck(box)) {
            return true;
         }
         break;
      case DirectionRaytrace:
         return this.directionRaytraceCheck(BlackOut.mc.field_1724.method_33571(), (double)yaw, (double)pitch, pos, dir);
      case StrictRaytrace:
         end = RotationUtils.rotationVec((double)yaw, (double)pitch, BlackOut.mc.field_1724.method_33571(), 7.0D);
         class_238 box = BoxUtils.get(pos);
         if (box.method_1006(BlackOut.mc.field_1724.method_33571())) {
            return true;
         }

         ((IRaycastContext)DamageUtils.raycastContext).blackout_Client$set(BlackOut.mc.field_1724.method_33571(), end);
         class_3965 result = DamageUtils.raycast(DamageUtils.raycastContext, false);
         if (result.method_17783() == class_240.field_1332 && result.method_17777().equals(pos)) {
            return true;
         }

         return this.raytraceCheck(BlackOut.mc.field_1724.method_33571(), (double)yaw, (double)pitch, box) && SettingUtils.placeRangeTo(pos) < SettingUtils.getPlaceWallsRange();
      case DirectionStrict:
         end = RotationUtils.rotationVec((double)yaw, (double)pitch, BlackOut.mc.field_1724.method_33571(), 7.0D);
         ((IRaycastContext)DamageUtils.raycastContext).blackout_Client$set(BlackOut.mc.field_1724.method_33571(), end);
         class_3965 result = DamageUtils.raycast(DamageUtils.raycastContext, false);
         if (result.method_17783() == class_240.field_1332 && result.method_17777().equals(pos) && result.method_17780() == dir) {
            return true;
         }

         return this.directionRaytraceCheck(BlackOut.mc.field_1724.method_33571(), (double)yaw, (double)pitch, pos, dir) && SettingUtils.placeRangeTo(pos) < SettingUtils.getPlaceWallsRange();
      case Angle:
         box = BoxUtils.get(pos);
         if (box.method_1006(BlackOut.mc.field_1724.method_33571())) {
            return true;
         }

         if (this.angleCheck((double)Managers.ROTATION.prevYaw, (double)Managers.ROTATION.prevPitch, box, RotationType.Attacking)) {
            return true;
         }
         break;
      default:
         return true;
      }

      return false;
   }

   private class_238 expandedBox(class_2338 pos, RotationType type) {
      double var10000;
      switch(type.checkType) {
      case Interact:
         var10000 = (Double)this.interactUpExpand.get();
         break;
      case BlockPlace:
         var10000 = (Double)this.blockUpExpand.get();
         break;
      case Mining:
         var10000 = (Double)this.mineUpExpand.get();
         break;
      default:
         var10000 = 0.0D;
      }

      double up = var10000;
      switch(type.checkType) {
      case Interact:
         var10000 = (Double)this.interactDownExpand.get();
         break;
      case BlockPlace:
         var10000 = (Double)this.blockDownExpand.get();
         break;
      case Mining:
         var10000 = (Double)this.mineDownExpand.get();
         break;
      default:
         var10000 = 0.0D;
      }

      double down = var10000;
      switch(type.checkType) {
      case Interact:
         var10000 = (Double)this.interactXZExpand.get();
         break;
      case BlockPlace:
         var10000 = (Double)this.blockXZExpand.get();
         break;
      case Mining:
         var10000 = (Double)this.mineXZExpand.get();
         break;
      default:
         var10000 = 0.0D;
      }

      double hz = var10000;
      return new class_238((double)pos.method_10263() - hz, (double)pos.method_10264() - down, (double)pos.method_10260() - hz, (double)(pos.method_10263() + 1) + hz, (double)(pos.method_10264() + 1) + up, (double)(pos.method_10260() + 1) + hz);
   }

   public Rotation applyStep(Rotation rotation, RotationType type, boolean rotated) {
      if (rotated) {
         this.sinceRotated = 0;
      }

      float oy = rotation.yaw();
      float op = rotation.pitch();
      double o = (double)(Integer)this.jitterStrength.get() / 5.0D;
      o *= o * o;
      oy += (float)((Math.random() - 0.5D) * o);
      op = (float)class_3532.method_15350((double)op + (Math.random() - 0.5D) * o, -90.0D, 90.0D);
      rotation = new Rotation(oy, op);
      if (type.instant) {
         return rotation;
      } else {
         double dy;
         double dp;
         double total;
         double speed;
         double multi;
         double realY;
         switch((RotationSettings.RotationSpeedMode)this.rotationSpeedMode.get()) {
         case Separate:
            if ((Boolean)this.pauseRotated.get() && this.sinceRotated < (Integer)this.delay.get()) {
               dy = 0.0D;
               dp = 0.0D;
            } else {
               dy = this.yawStep(type);
               dp = this.pitchStep(type);
            }

            return this.applyStep(rotation, dy, dp);
         case Normal:
            dy = Math.abs(RotationUtils.yawAngle((double)Managers.ROTATION.prevYaw, (double)rotation.yaw()));
            dp = Math.abs(RotationUtils.pitchAngle((double)Managers.ROTATION.prevPitch, (double)rotation.pitch()));
            total = Math.sqrt(dy * dy + dp * dp);
            if ((Boolean)this.pauseRotated.get() && this.sinceRotated < (Integer)this.delay.get()) {
               speed = 0.0D;
            } else {
               speed = total == 0.0D ? Double.MAX_VALUE : (Double)this.rotationSpeed.get() / total;
            }

            multi = dy * speed;
            realY = dp * speed;
            return this.applyStep(rotation, multi, realY);
         case Balance:
            dy = Math.abs(RotationUtils.yawAngle((double)Managers.ROTATION.prevYaw, (double)rotation.yaw()));
            dp = Math.abs(RotationUtils.pitchAngle((double)Managers.ROTATION.prevPitch, (double)rotation.pitch()));
            total = Math.sqrt(dy * dy + dp * dp);
            if ((Boolean)this.pauseRotated.get() && this.sinceRotated < (Integer)this.delay.get()) {
               speed = 0.0D;
            } else {
               speed = total == 0.0D ? Double.MAX_VALUE : this.getBalanceSpeed() / total;
            }

            multi = dy * speed;
            realY = dp * speed;
            return this.applyStep(rotation, multi, realY);
         case Smooth:
            dy = Math.abs(RotationUtils.yawAngle((double)Managers.ROTATION.prevYaw, (double)rotation.yaw()));
            dp = Math.abs(RotationUtils.pitchAngle((double)Managers.ROTATION.prevPitch, (double)rotation.pitch()));
            total = Math.sqrt(dy * dy + dp * dp);
            if ((Boolean)this.pauseRotated.get() && this.sinceRotated < (Integer)this.delay.get()) {
               speed = 0.0D;
            } else {
               speed = class_3532.method_15390((Double)this.minSmoothSpeed.get(), (Double)this.maxSmoothSpeed.get(), class_3532.method_15370(total, (Double)this.minSmoothAngle.get(), (Double)this.maxSmoothAngle.get()));
            }

            multi = total == 0.0D ? Double.MAX_VALUE : speed / total;
            realY = dy * multi;
            double realP = dp * multi;
            return this.applyStep(rotation, Math.min(realY, dy), Math.min(realP, dp));
         default:
            return rotation;
         }
      }
   }

   private double getBalanceSpeed() {
      List<FloatFloatPair> list = Managers.ROTATION.tickRotationHistory;
      int length = Math.min(list.size(), (Integer)this.averageTicks.get());
      if (length <= 1) {
         return (Double)this.averageSpeed.get();
      } else {
         FloatFloatPair prev = (FloatFloatPair)list.get(0);
         double total = 0.0D;

         for(int i = 1; i < length; ++i) {
            FloatFloatPair pair = (FloatFloatPair)list.get(i);
            double yaw = Math.abs(RotationUtils.yawAngle((double)pair.firstFloat(), (double)prev.firstFloat()));
            float pitch = pair.secondFloat() - prev.secondFloat();
            prev = pair;
            total += Math.min(Math.sqrt(yaw * yaw + (double)(pitch * pitch)), 180.0D);
         }

         return class_3532.method_15350((Double)this.averageSpeed.get() * (double)length - total, 0.0D, (Double)this.maxSpeed.get());
      }
   }

   @Event
   public void onTick(TickEvent.Pre event) {
      ++this.sinceRotated;
   }

   private Rotation applyStep(Rotation rotation, double yaw, double pitch) {
      return new Rotation(RotationUtils.nextYaw((double)Managers.ROTATION.prevYaw, (double)rotation.yaw(), yaw * this.speedMulti()), RotationUtils.nextPitch((double)Managers.ROTATION.prevPitch, (double)rotation.pitch(), pitch * this.speedMulti()));
   }

   private double speedMulti() {
      return !(Boolean)this.timeBasedSpeed.get() ? 1.0D : Math.min((double)(System.currentTimeMillis() - Managers.ROTATION.prevRotation) / 50.0D, (Double)this.maxMulti.get());
   }

   public boolean attackRotationCheck(class_238 box, float yaw, float pitch) {
      if (box.method_1006(BlackOut.mc.field_1724.method_33571())) {
         return true;
      } else {
         switch((RotationSettings.RotationCheckMode)this.attackMode.get()) {
         case Raytrace:
            if (this.raytraceCheck(BlackOut.mc.field_1724.method_33571(), (double)yaw, (double)pitch, box)) {
               return true;
            }
            break;
         case StrictRaytrace:
            class_243 end = RotationUtils.rotationVec((double)yaw, (double)pitch, BlackOut.mc.field_1724.method_33571(), 7.0D);
            Optional<class_243> pos = box.method_992(BlackOut.mc.field_1724.method_33571(), end);
            if (pos.isEmpty()) {
               return false;
            }

            ((IRaycastContext)DamageUtils.raycastContext).blackout_Client$set(BlackOut.mc.field_1724.method_33571(), (class_243)pos.get());
            boolean visible = DamageUtils.raycast(DamageUtils.raycastContext, false).method_17783() == class_240.field_1333;
            if (visible) {
               return true;
            }

            return this.raytraceCheck(BlackOut.mc.field_1724.method_33571(), (double)yaw, (double)pitch, box) && SettingUtils.attackRangeTo(box, BlackOut.mc.field_1724.method_19538()) < SettingUtils.getAttackWallsRange();
         case Angle:
            if (this.angleCheck((double)Managers.ROTATION.prevYaw, (double)Managers.ROTATION.prevPitch, box, RotationType.Attacking)) {
               return true;
            }

            if (this.raytraceCheck(BlackOut.mc.field_1724.method_33571(), (double)yaw, (double)pitch, box)) {
               return true;
            }
            break;
         default:
            return true;
         }

         return false;
      }
   }

   public class_243 getRotationVec(class_2338 pos, class_2350 dir, class_243 vec, RotationType type) {
      RotationSettings.BlockRotationCheckMode mode = this.mode(type);
      if (mode != RotationSettings.BlockRotationCheckMode.Raytrace && mode != RotationSettings.BlockRotationCheckMode.Angle) {
         if (mode == RotationSettings.BlockRotationCheckMode.DirectionRaytrace) {
            return pos.method_46558().method_1031((double)((float)dir.method_10148() / 2.0F), (double)((float)dir.method_10164() / 2.0F), (double)((float)dir.method_10165() / 2.0F));
         } else {
            class_2680 state = BlackOut.mc.field_1687.method_8320(pos);
            class_265 shape = state.method_26218(BlackOut.mc.field_1687, pos);
            class_238 box;
            if (shape.method_1110()) {
               box = new class_238(0.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D);
            } else {
               box = shape.method_1107();
            }

            double minX = box.field_1323;
            double minY = box.field_1322;
            double minZ = box.field_1321;
            double maxX = box.field_1320;
            double maxY = box.field_1325;
            double maxZ = box.field_1324;
            if (mode == RotationSettings.BlockRotationCheckMode.DirectionStrict) {
               minX = dir.method_10148() > 0 ? maxX : minX;
               minY = dir.method_10164() > 0 ? maxY : minY;
               minZ = dir.method_10165() > 0 ? maxZ : minZ;
               maxX = dir.method_10148() < 0 ? minX : maxX;
               maxY = dir.method_10164() < 0 ? minY : maxY;
               maxZ = dir.method_10165() < 0 ? minZ : maxZ;
            }

            return this.getRaytraceRotationVec(new class_238(minX, minY, minZ, maxX, maxY, maxZ), vec);
         }
      } else {
         return vec == null ? pos.method_46558() : vec;
      }
   }

   public Rotation getRotation(class_2338 pos, class_2350 dir, class_243 vec, RotationType type) {
      return this.getRotation(this.getRotationVec(pos, dir, vec, type));
   }

   public class_243 getAttackRotationVec(class_238 box, class_243 vec) {
      RotationSettings.RotationCheckMode mode = (RotationSettings.RotationCheckMode)this.attackMode.get();
      if (mode != RotationSettings.RotationCheckMode.Raytrace && mode != RotationSettings.RotationCheckMode.Angle) {
         return this.getRaytraceRotationVec(box, vec);
      } else {
         return vec == null ? BoxUtils.middle(box) : vec;
      }
   }

   public class_243 getRaytraceRotationVec(class_238 box, class_243 vec) {
      class_243 v = new class_243(0.0D, 0.0D, 0.0D);
      double cd = 100.0D;
      double ox = class_3532.method_15370(vec.field_1352, box.field_1323, box.field_1320);
      double oy = class_3532.method_15370(vec.field_1351, box.field_1322, box.field_1325);
      double oz = class_3532.method_15370(vec.field_1350, box.field_1321, box.field_1324);
      double lenX = box.method_17939();
      double lenY = box.method_17940();
      double lenZ = box.method_17941();

      for(double x = 0.1D; x <= 0.9D; x += 0.1D) {
         ((IVec3d)v).blackout_Client$setX(box.field_1323 + lenX * x);

         for(double y = 0.1D; y <= 0.9D; y += 0.1D) {
            ((IVec3d)v).blackout_Client$setY(box.field_1322 + lenY * y);

            for(double z = 0.1D; z <= 0.9D; z += 0.1D) {
               ((IVec3d)v).blackout_Client$setZ(box.field_1321 + lenZ * z);
               double distance = BlackOut.mc.field_1724.method_33571().method_1022(v);
               if (!(distance >= SettingUtils.getAttackRange())) {
                  if (distance > SettingUtils.getAttackWallsRange()) {
                     ((IRaycastContext)DamageUtils.raycastContext).blackout_Client$set(BlackOut.mc.field_1724.method_33571(), v);
                     class_3965 result = DamageUtils.raycast(DamageUtils.raycastContext, false);
                     if (result.method_17783() != class_240.field_1333) {
                        continue;
                     }
                  }

                  double dx = ox - x;
                  double dy = oy - y;
                  double dz = oz - z;
                  double d = dx * dx + dy * dy + dz * dz;
                  if (!(d >= cd)) {
                     cd = d;
                     ((IVec3d)vec).blackout_Client$set(v.field_1352, v.field_1351, v.field_1350);
                  }
               }
            }
         }
      }

      return vec;
   }

   public Rotation getAttackRotation(class_238 box, class_243 vec) {
      return this.getRotation(this.getAttackRotationVec(box, vec));
   }

   public Rotation getRotation(class_243 vec) {
      return new Rotation((float)RotationUtils.getYaw(vec), (float)RotationUtils.getPitch(vec));
   }

   public boolean shouldRotate(RotationType type) {
      boolean var10000;
      switch(type.checkType) {
      case Interact:
         var10000 = (Boolean)this.interactRotate.get();
         break;
      case BlockPlace:
         var10000 = (Boolean)this.blockRotate.get();
         break;
      case Mining:
         var10000 = (Boolean)this.mineRotate.get();
         break;
      case Attacking:
         var10000 = (Boolean)this.attackRotate.get();
         break;
      default:
         var10000 = true;
      }

      return var10000;
   }

   public RotationSettings.BlockRotationCheckMode mode(RotationType type) {
      RotationSettings.BlockRotationCheckMode var10000;
      switch(type.checkType) {
      case Interact:
         var10000 = (RotationSettings.BlockRotationCheckMode)this.interactMode.get();
         break;
      case BlockPlace:
         var10000 = (RotationSettings.BlockRotationCheckMode)this.blockMode.get();
         break;
      case Mining:
         var10000 = (RotationSettings.BlockRotationCheckMode)this.mineMode.get();
         break;
      default:
         var10000 = null;
      }

      return var10000;
   }

   public double yawAngle(RotationType type) {
      double var10000;
      switch(type.checkType) {
      case Interact:
         var10000 = (Double)this.interactYawAngle.get();
         break;
      case BlockPlace:
         var10000 = (Double)this.blockYawAngle.get();
         break;
      case Mining:
         var10000 = (Double)this.mineYawAngle.get();
         break;
      case Attacking:
         var10000 = (Double)this.attackYawAngle.get();
         break;
      default:
         var10000 = 0.0D;
      }

      return var10000;
   }

   public double pitchAngle(RotationType type) {
      double var10000;
      switch(type.checkType) {
      case Interact:
         var10000 = (Double)this.interactPitchAngle.get();
         break;
      case BlockPlace:
         var10000 = (Double)this.blockPitchAngle.get();
         break;
      case Mining:
         var10000 = (Double)this.minePitchAngle.get();
         break;
      case Attacking:
         var10000 = (Double)this.attackPitchAngle.get();
         break;
      default:
         var10000 = 0.0D;
      }

      return var10000;
   }

   public double yawStep(RotationType type) {
      return type.instant ? 42069.0D : (Double)this.yawStep.get() + (Math.random() - 0.5D) * 2.0D * (Double)this.yawRandom.get();
   }

   public double pitchStep(RotationType type) {
      return type.instant ? 42069.0D : (Double)this.pitchStep.get() + (Math.random() - 0.5D) * 2.0D * (Double)this.pitchRandom.get();
   }

   public boolean angleCheck(double y, double p, class_238 box, RotationType type) {
      double yawTo = RotationUtils.getYaw(BlackOut.mc.field_1724.method_33571(), box.method_1005(), y);
      double pitchTo = RotationUtils.getPitch(BlackOut.mc.field_1724.method_33571(), box.method_1005());
      return Math.abs(RotationUtils.yawAngle(y, yawTo)) <= this.yawAngle(type) && Math.abs(p - pitchTo) <= this.pitchAngle(type);
   }

   public boolean raytraceCheck(class_238 box) {
      return this.raytraceCheck(BlackOut.mc.field_1724.method_33571(), (double)Managers.ROTATION.prevYaw, (double)Managers.ROTATION.prevPitch, box);
   }

   public boolean raytraceCheck(class_243 pos, double y, double p, class_238 box) {
      double range = pos.method_1022(OLEPOSSUtils.getMiddle(box)) + 3.0D;
      class_243 end = RotationUtils.rotationVec(y, p, pos, range);

      for(float i = 0.0F; i < 1.0F; i = (float)((double)i + 0.01D)) {
         if (box.method_1008(pos.field_1352 + (end.field_1352 - pos.field_1352) * (double)i, pos.field_1351 + (end.field_1351 - pos.field_1351) * (double)i, pos.field_1350 + (end.field_1350 - pos.field_1350) * (double)i)) {
            return true;
         }
      }

      return false;
   }

   public boolean directionRaytraceCheck(class_243 pos, double yaw, double pitch, class_2338 block, class_2350 dir) {
      double range = pos.method_1022(block.method_46558()) + 3.0D;
      class_243 end = RotationUtils.rotationVec(yaw, pitch, pos, range);

      for(float i = 0.0F; i < 1.0F; i = (float)((double)i + 0.001D)) {
         double x = pos.field_1352 + (end.field_1352 - pos.field_1352) * (double)i;
         double y = pos.field_1351 + (end.field_1351 - pos.field_1351) * (double)i;
         double z = pos.field_1350 + (end.field_1350 - pos.field_1350) * (double)i;
         if (this.contains(x, y, z, block)) {
            class_2350 d = this.getDirection(x, y, z);
            if (d == dir) {
               return true;
            }
         }
      }

      return false;
   }

   private class_2350 getDirection(double x, double y, double z) {
      double offsetX = this.offsetFrom(x);
      double offsetY = this.offsetFrom(y);
      double offsetZ = this.offsetFrom(z);
      class_2350 closest = null;
      double dist = 0.0D;
      class_2350[] var16 = class_2350.values();
      int var17 = var16.length;

      for(int var18 = 0; var18 < var17; ++var18) {
         class_2350 dir = var16[var18];
         double d = dir.method_10163().method_40081(offsetX, offsetY, offsetZ);
         if (closest == null || d < dist) {
            closest = dir;
            dist = d;
         }
      }

      return closest;
   }

   private double offsetFrom(double val) {
      return (val - Math.floor(val) - 0.5D) * 2.0D;
   }

   private boolean contains(double x, double y, double z, class_2338 pos) {
      return x >= (double)pos.method_10263() && x <= (double)(pos.method_10263() + 1) && y >= (double)pos.method_10264() && y <= (double)(pos.method_10264() + 1) && z >= (double)pos.method_10260() && z <= (double)(pos.method_10260() + 1);
   }

   public boolean endMineRot() {
      if (!(Boolean)this.mineRotate.get()) {
         return false;
      } else {
         return this.mineTiming.get() == RotationSettings.MiningRotMode.End || this.mineTiming.get() == RotationSettings.MiningRotMode.Double;
      }
   }

   public boolean startMineRot() {
      if (!(Boolean)this.mineRotate.get()) {
         return false;
      } else {
         return this.mineTiming.get() == RotationSettings.MiningRotMode.Start || this.mineTiming.get() == RotationSettings.MiningRotMode.Double;
      }
   }

   public static enum RotationSpeedMode {
      Separate,
      Normal,
      Smooth,
      Balance;

      // $FF: synthetic method
      private static RotationSettings.RotationSpeedMode[] $values() {
         return new RotationSettings.RotationSpeedMode[]{Separate, Normal, Smooth, Balance};
      }
   }

   public static enum PacketRotationMode {
      Disabled((DoubleConsumer)null),
      Basic((yaw, pitch) -> {
         class_243 pos = Managers.PACKET.pos;
         Managers.PACKET.sendPacket(new class_2830(pos.method_10216(), pos.method_10214(), pos.method_10215(), yaw, pitch, Managers.PACKET.isOnGround()));
      }),
      Double((yaw, pitch) -> {
         class_243 pos = Managers.PACKET.pos;

         for(int i = 0; i < 2; ++i) {
            Managers.PACKET.sendPacket(new class_2830(pos.method_10216(), pos.method_10214(), pos.method_10215(), yaw, pitch, Managers.PACKET.isOnGround()));
         }

      }),
      Return((yaw, pitch) -> {
         float prevYaw = Managers.ROTATION.prevYaw;
         float prevPitch = Managers.ROTATION.prevPitch;
         class_243 pos = Managers.PACKET.pos;
         Managers.PACKET.sendPacket(new class_2830(pos.method_10216(), pos.method_10214(), pos.method_10215(), yaw, pitch, Managers.PACKET.isOnGround()));
         Managers.PACKET.sendPacket(new class_2830(pos.method_10216(), pos.method_10214(), pos.method_10215(), prevYaw, prevPitch, Managers.PACKET.isOnGround()));
      });

      private final DoubleConsumer<Float, Float> consumer;

      private PacketRotationMode(DoubleConsumer<Float, Float> consumer) {
         this.consumer = consumer;
      }

      public void send(float yaw, float pitch) {
         this.consumer.accept(yaw, pitch);
      }

      // $FF: synthetic method
      private static RotationSettings.PacketRotationMode[] $values() {
         return new RotationSettings.PacketRotationMode[]{Disabled, Basic, Double, Return};
      }
   }

   public static enum MiningRotMode {
      Start,
      End,
      Double;

      // $FF: synthetic method
      private static RotationSettings.MiningRotMode[] $values() {
         return new RotationSettings.MiningRotMode[]{Start, End, Double};
      }
   }

   public static enum RotationCheckMode {
      Raytrace,
      StrictRaytrace,
      Angle;

      // $FF: synthetic method
      private static RotationSettings.RotationCheckMode[] $values() {
         return new RotationSettings.RotationCheckMode[]{Raytrace, StrictRaytrace, Angle};
      }
   }

   public static enum BlockRotationCheckMode {
      Raytrace,
      DirectionRaytrace,
      StrictRaytrace,
      DirectionStrict,
      Angle;

      // $FF: synthetic method
      private static RotationSettings.BlockRotationCheckMode[] $values() {
         return new RotationSettings.BlockRotationCheckMode[]{Raytrace, DirectionRaytrace, StrictRaytrace, DirectionStrict, Angle};
      }
   }
}
