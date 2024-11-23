package bodevelopment.client.blackout.helpers;

import bodevelopment.client.blackout.enums.RotationType;
import bodevelopment.client.blackout.interfaces.functional.RotationCheck;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.modules.client.settings.RotationSettings;
import bodevelopment.client.blackout.module.modules.combat.defensive.AutoMend;
import bodevelopment.client.blackout.module.modules.combat.defensive.AutoPot;
import bodevelopment.client.blackout.module.modules.combat.defensive.Burrow;
import bodevelopment.client.blackout.module.modules.combat.defensive.BurrowRewrite;
import bodevelopment.client.blackout.module.modules.combat.defensive.ExpThrower;
import bodevelopment.client.blackout.module.modules.combat.defensive.Surround;
import bodevelopment.client.blackout.module.modules.combat.misc.HoleFill;
import bodevelopment.client.blackout.module.modules.combat.misc.Quiver;
import bodevelopment.client.blackout.module.modules.combat.offensive.Aura;
import bodevelopment.client.blackout.module.modules.combat.offensive.AutoCrystal;
import bodevelopment.client.blackout.module.modules.combat.offensive.AutoMine;
import bodevelopment.client.blackout.module.modules.combat.offensive.PistonCrystal;
import bodevelopment.client.blackout.module.modules.combat.offensive.Snombonty;
import bodevelopment.client.blackout.module.modules.misc.FastUse;
import bodevelopment.client.blackout.module.modules.movement.Scaffold;
import bodevelopment.client.blackout.randomstuff.PlaceData;
import bodevelopment.client.blackout.randomstuff.Rotation;
import bodevelopment.client.blackout.util.BoxUtils;
import bodevelopment.client.blackout.util.RotationUtils;
import bodevelopment.client.blackout.util.SettingUtils;
import it.unimi.dsi.fastutil.floats.FloatFloatPair;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_243;

public class RotationHelper {
   private int priority;
   private String name;
   private static final List<Class<? extends Module>> priorities = new ArrayList();

   protected void set(Module module) {
      this.priority = this.getPriority(module);
      this.name = module.name;
   }

   private int getPriority(Module m) {
      return priorities.contains(m.getClass()) ? priorities.indexOf(m.getClass()) : 0;
   }

   protected double getRotationTime() {
      return 0.3D;
   }

   protected boolean rotate(float yaw, float pitch, RotationType type, String key) {
      return this.rotate(yaw, pitch, 0.0D, type, key);
   }

   protected boolean rotate(float yaw, float pitch, double priority, RotationType type, String key) {
      return this.rotate(yaw, pitch, priority, 2.0D, type, key);
   }

   protected boolean rotate(float yaw, float pitch, double priority, double precision, RotationType type, String key) {
      return this.rotate(yaw, pitch, true, true, priority, type, (y, p) -> {
         return Math.abs(RotationUtils.yawAngle((double)yaw, (double)y)) < precision && (double)Math.abs(pitch - p) < precision;
      }, key);
   }

   protected boolean rotateYaw(float yaw, RotationType type, String key) {
      return this.rotateYaw(yaw, 0.0D, type, key);
   }

   protected boolean rotateYaw(float yaw, double priority, RotationType type, String key) {
      return this.rotateYaw(yaw, priority, 2.0D, type, key);
   }

   protected boolean rotateYaw(float yaw, double priority, double precision, RotationType type, String key) {
      return this.rotate(yaw, 0.0F, true, false, priority, type, (y, p) -> {
         return Math.abs(RotationUtils.yawAngle((double)yaw, (double)y)) <= precision;
      }, key);
   }

   protected boolean rotatePitch(float pitch, RotationType type, String key) {
      return this.rotatePitch(pitch, 0.0D, type, key);
   }

   protected boolean rotatePitch(float pitch, double priority, RotationType type, String key) {
      return this.rotatePitch(pitch, priority, 2.0D, type, key);
   }

   protected boolean rotatePitch(float pitch, double priority, double precision, RotationType type, String key) {
      return this.rotate(0.0F, pitch, false, true, priority, type, (y, p) -> {
         return (double)Math.abs(p - pitch) <= precision;
      }, key);
   }

   protected boolean rotateBlock(PlaceData data, class_243 vec, RotationType type, String key) {
      return this.rotateBlock(data, vec, type, 0.0D, key);
   }

   protected boolean rotateBlock(PlaceData data, class_243 vec, RotationType type, double priority, String key) {
      return this.rotateBlock(data.pos(), data.dir(), vec, type, priority, key);
   }

   protected boolean rotateBlock(class_2338 pos, class_2350 dir, class_243 vec, RotationType type, String key) {
      return this.rotateBlock(pos, dir, vec, type, 0.0D, key);
   }

   protected boolean rotateBlock(class_2338 pos, class_2350 dir, class_243 vec, RotationType type, double priority, String key) {
      Rotation rotation = SettingUtils.getRotation(pos, dir, vec, type);
      return this.rotate(rotation.yaw(), rotation.pitch(), true, true, priority, type, (y, p) -> {
         return SettingUtils.blockRotationCheck(pos, dir, y, p, type);
      }, key);
   }

   protected boolean rotateBlock(PlaceData data, RotationType type, String key) {
      return this.rotateBlock(data, type, 0.0D, key);
   }

   protected boolean rotateBlock(PlaceData data, RotationType type, double priority, String key) {
      return this.rotateBlock(data.pos(), data.dir(), type, priority, key);
   }

   protected boolean rotateBlock(class_2338 pos, class_2350 dir, RotationType type, String key) {
      return this.rotateBlock(pos, dir, type, 0.0D, key);
   }

   protected boolean rotateBlock(class_2338 pos, class_2350 dir, RotationType type, double priority, String key) {
      Rotation rotation = SettingUtils.getRotation(pos, dir, pos.method_46558(), type);
      return this.rotate(rotation.yaw(), rotation.pitch(), true, true, priority, type, (y, p) -> {
         return SettingUtils.blockRotationCheck(pos, dir, y, p, type);
      }, key);
   }

   protected boolean attackRotate(class_238 box, String key) {
      return this.attackRotate(box, BoxUtils.middle(box), key);
   }

   protected boolean attackRotate(class_238 box, double priority, String key) {
      return this.attackRotate(box, BoxUtils.middle(box), priority, key);
   }

   protected boolean attackRotate(class_238 box, class_243 vec, String key) {
      return this.attackRotate(box, vec, 0.0D, key);
   }

   protected boolean attackRotate(class_238 box, class_243 vec, double priority, String key) {
      Rotation rotation = SettingUtils.getAttackRotation(box, vec);
      return this.rotate(rotation.yaw(), rotation.pitch(), true, true, priority, RotationType.Attacking, (y, p) -> {
         return SettingUtils.attackRotationCheck(box, y, p);
      }, key);
   }

   protected boolean rotate(float yaw, float pitch, boolean rotateYaw, boolean rotatePitch, double priority, RotationType type, RotationCheck rotationCheck, String key) {
      boolean canYaw = rotateYaw && (priority + (double)this.priority >= Managers.ROTATION.priorityYaw || this.getKey(key).equals(Managers.ROTATION.keyYaw) || System.currentTimeMillis() >= Managers.ROTATION.timeYaw);
      boolean canPitch = rotatePitch && (priority + (double)this.priority >= Managers.ROTATION.priorityPitch || this.getKey(key).equals(Managers.ROTATION.keyPitch) || System.currentTimeMillis() >= Managers.ROTATION.timePitch);
      boolean rotated = this.rotationCheck(rotationCheck);
      Rotation rotation = SettingUtils.applyStep(new Rotation(rotateYaw ? yaw : Managers.ROTATION.nextYaw, rotatePitch ? pitch : Managers.ROTATION.nextPitch), type, rotated);
      if (canYaw) {
         this.setYaw(rotation.yaw(), priority, key);
      }

      if (canPitch) {
         this.setPitch(rotation.pitch(), priority, key);
      }

      RotationSettings settings = RotationSettings.getInstance();
      if (settings.packetRotationMode.get() != RotationSettings.PacketRotationMode.Disabled && Managers.ROTATION.packetsLeft > 0.0D && (!rotated || !(Boolean)settings.smartPacket.get())) {
         float packetYaw;
         float packetPitch;
         if ((Boolean)settings.instantPacketRotation.get()) {
            packetYaw = yaw;
            packetPitch = pitch;
         } else {
            packetYaw = rotation.yaw();
            packetPitch = rotation.pitch();
         }

         ((RotationSettings.PacketRotationMode)settings.packetRotationMode.get()).send(packetYaw, packetPitch);
         --Managers.ROTATION.packetsLeft;
      }

      return (type != RotationType.Attacking || this.checkAttackLimit()) && rotated;
   }

   protected boolean checkAttackLimit() {
      if (!SettingUtils.attackLimit()) {
         return true;
      } else {
         for(int i = 0; i < Math.min(Managers.ROTATION.rotationHistory.size(), SettingUtils.attackTicks()); ++i) {
            FloatFloatPair pair = (FloatFloatPair)Managers.ROTATION.rotationHistory.get(i);
            double f = Math.sqrt((double)(pair.firstFloat() * pair.firstFloat() + pair.secondFloat() * pair.secondFloat()));
            if (f > SettingUtils.attackSpeed()) {
               return false;
            }
         }

         return true;
      }
   }

   protected void setPitch(float pitch, double priority, String key) {
      Managers.ROTATION.nextPitch = pitch;
      Managers.ROTATION.timePitch = (long)((double)System.currentTimeMillis() + this.getRotationTime() * 1000.0D);
      Managers.ROTATION.keyPitch = this.getKey(key);
      Managers.ROTATION.priorityPitch = priority + (double)this.priority;
   }

   protected void setYaw(float yaw, double priority, String key) {
      Managers.ROTATION.nextYaw = yaw;
      Managers.ROTATION.timeYaw = (long)((double)System.currentTimeMillis() + this.getRotationTime() * 1000.0D);
      Managers.ROTATION.keyYaw = this.getKey(key);
      Managers.ROTATION.priorityYaw = priority + (double)this.priority;
   }

   protected String getKey(String key) {
      return this.name + key;
   }

   protected void end(String key) {
      if (Objects.equals(Managers.ROTATION.keyYaw, this.getKey(key))) {
         Managers.ROTATION.priorityYaw = -1.0D;
      }

      if (Objects.equals(Managers.ROTATION.keyPitch, this.getKey(key))) {
         Managers.ROTATION.priorityPitch = -1.0D;
      }

   }

   protected boolean rotationCheck(RotationCheck check) {
      return check.test(Managers.ROTATION.prevYaw, Managers.ROTATION.prevPitch);
   }

   static {
      priorities.add(Aura.class);
      priorities.add(Snombonty.class);
      priorities.add(AutoCrystal.class);
      priorities.add(Scaffold.class);
      priorities.add(AutoMine.class);
      priorities.add(PistonCrystal.class);
      priorities.add(HoleFill.class);
      priorities.add(AutoMend.class);
      priorities.add(ExpThrower.class);
      priorities.add(FastUse.class);
      priorities.add(Quiver.class);
      priorities.add(Surround.class);
      priorities.add(Burrow.class);
      priorities.add(BurrowRewrite.class);
      priorities.add(AutoPot.class);
   }
}
