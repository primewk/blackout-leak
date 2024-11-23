package bodevelopment.client.blackout.util;

import bodevelopment.client.blackout.enums.RotationType;
import bodevelopment.client.blackout.enums.SwingState;
import bodevelopment.client.blackout.enums.SwingType;
import bodevelopment.client.blackout.interfaces.functional.DoublePredicate;
import bodevelopment.client.blackout.interfaces.mixin.IEndCrystalEntity;
import bodevelopment.client.blackout.module.modules.client.settings.ExtrapolationSettings;
import bodevelopment.client.blackout.module.modules.client.settings.FacingSettings;
import bodevelopment.client.blackout.module.modules.client.settings.RangeSettings;
import bodevelopment.client.blackout.module.modules.client.settings.RaytraceSettings;
import bodevelopment.client.blackout.module.modules.client.settings.RotationSettings;
import bodevelopment.client.blackout.module.modules.client.settings.ServerSettings;
import bodevelopment.client.blackout.module.modules.client.settings.SwingSettings;
import bodevelopment.client.blackout.randomstuff.PlaceData;
import bodevelopment.client.blackout.randomstuff.Rotation;
import net.minecraft.class_1268;
import net.minecraft.class_1511;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_243;

public class SettingUtils {
   private static FacingSettings facing;
   private static RangeSettings range;
   private static RaytraceSettings raytrace;
   private static RotationSettings rotation;
   private static ServerSettings server;
   private static SwingSettings swing;
   private static ExtrapolationSettings extrapolation;

   public static void init() {
      facing = FacingSettings.getInstance();
      range = RangeSettings.getInstance();
      raytrace = RaytraceSettings.getInstance();
      rotation = RotationSettings.getInstance();
      server = ServerSettings.getInstance();
      swing = SwingSettings.getInstance();
      extrapolation = ExtrapolationSettings.getInstance();
   }

   public static double maxInteractRange() {
      return Math.max(getInteractRange(), getInteractWallsRange());
   }

   public static double maxPlaceRange() {
      return Math.max(getPlaceRange(), getPlaceWallsRange());
   }

   public static double maxMineRange() {
      return Math.max(getMineRange(), getMineWallsRange());
   }

   public static double getInteractRange() {
      return (Double)range.interactRange.get();
   }

   public static double getInteractWallsRange() {
      return (Double)range.interactRangeWalls.get();
   }

   public static double interactRangeTo(class_2338 pos) {
      return range.interactRangeTo(pos, (class_243)null);
   }

   public static boolean inInteractRange(class_2338 pos) {
      return range.inInteractRange(pos, (class_243)null);
   }

   public static boolean inInteractRange(class_2338 pos, class_243 from) {
      return range.inInteractRange(pos, from);
   }

   public static boolean inInteractRangeNoTrace(class_2338 pos) {
      return range.inInteractRangeNoTrace(pos, (class_243)null);
   }

   public static boolean inInteractRangeNoTrace(class_2338 pos, class_243 from) {
      return range.inInteractRangeNoTrace(pos, from);
   }

   public static double getPlaceRange() {
      return (Double)range.placeRange.get();
   }

   public static double getPlaceWallsRange() {
      return (Double)range.placeRangeWalls.get();
   }

   public static double placeRangeTo(class_2338 pos) {
      return range.placeRangeTo(pos, (class_243)null);
   }

   public static boolean inPlaceRange(class_2338 pos) {
      return range.inPlaceRange(pos, (class_243)null);
   }

   public static boolean inPlaceRange(class_2338 pos, class_243 from) {
      return range.inPlaceRange(pos, from);
   }

   public static boolean inPlaceRangeNoTrace(class_2338 pos) {
      return range.inPlaceRangeNoTrace(pos, (class_243)null);
   }

   public static boolean inPlaceRangeNoTrace(class_2338 pos, class_243 from) {
      return range.inPlaceRangeNoTrace(pos, from);
   }

   public static double getAttackRange() {
      return (Double)range.attackRange.get();
   }

   public static double getAttackWallsRange() {
      return (Double)range.attackRangeWalls.get();
   }

   public static double attackRangeTo(class_238 bb, class_243 feet) {
      return range.innerAttackRangeTo(bb, feet, false);
   }

   public static double wallAttackRangeTo(class_238 bb, class_243 feet) {
      return range.innerAttackRangeTo(bb, feet, true);
   }

   public static boolean inAttackRange(class_238 bb) {
      return range.inAttackRange(bb, (class_243)null);
   }

   public static boolean inAttackRange(class_238 bb, class_243 from) {
      return range.inAttackRange(bb, from);
   }

   public static boolean inAttackRangeNoTrace(class_238 bb) {
      return range.inAttackRangeNoTrace(bb, (class_243)null);
   }

   public static boolean inAttackRangeNoTrace(class_238 bb, class_243 from) {
      return range.inAttackRangeNoTrace(bb, from);
   }

   public static double getMineRange() {
      return (Double)range.mineRange.get();
   }

   public static double getMineWallsRange() {
      return (Double)range.mineRangeWalls.get();
   }

   public static double mineRangeTo(class_2338 pos) {
      return range.miningRangeTo(pos, (class_243)null);
   }

   public static boolean inMineRange(class_2338 pos) {
      return range.inMineRange(pos);
   }

   public static boolean inMineRangeNoTrace(class_2338 pos) {
      return range.inMineRangeNoTrace(pos);
   }

   public static boolean startMineRot() {
      return rotation.startMineRot();
   }

   public static boolean endMineRot() {
      return rotation.endMineRot();
   }

   public static boolean shouldVanillaRotate() {
      return (Boolean)rotation.vanillaRotation.get();
   }

   public static boolean shouldRotate(RotationType type) {
      return rotation.shouldRotate(type);
   }

   public static boolean blockRotationCheck(class_2338 pos, class_2350 dir, float yaw, float pitch, RotationType type) {
      return rotation.blockRotationCheck(pos, dir, yaw, pitch, type);
   }

   public static boolean attackRotationCheck(class_238 box, float yaw, float pitch) {
      return rotation.attackRotationCheck(box, yaw, pitch);
   }

   public static double yawStep(RotationType type) {
      return rotation.yawStep(type);
   }

   public static double pitchStep(RotationType type) {
      return rotation.pitchStep(type);
   }

   public static Rotation getRotation(class_2338 pos, class_2350 dir, class_243 vec, RotationType type) {
      return rotation.getRotation(pos, dir, vec, type);
   }

   public static class_243 getRotationVec(class_2338 pos, class_2350 dir, class_243 vec, RotationType type) {
      return rotation.getRotationVec(pos, dir, vec, type);
   }

   public static Rotation getRotation(class_243 vec) {
      return rotation.getRotation(vec);
   }

   public static Rotation getAttackRotation(class_238 box, class_243 vec) {
      return rotation.getAttackRotation(box, vec);
   }

   public static double returnSpeed() {
      return (Double)rotation.returnSpeed.get();
   }

   public static Rotation applyStep(Rotation rot, RotationType type, boolean rotated) {
      return rotation.applyStep(rot, type, rotated);
   }

   public static boolean attackLimit() {
      return (Boolean)rotation.attackLimit.get();
   }

   public static double attackSpeed() {
      return (Double)rotation.attackMaxSpeed.get();
   }

   public static int attackTicks() {
      return (Integer)rotation.attackTicks.get();
   }

   public static boolean rotationIgnoreEnabled() {
      return (Double)rotation.noOwnTime.get() > 0.0D || (Double)rotation.noOtherTime.get() > 0.0D;
   }

   public static boolean shouldIgnoreRotations(class_1511 entity) {
      IEndCrystalEntity iEntity = (IEndCrystalEntity)entity;
      long since = System.currentTimeMillis() - iEntity.blackout_Client$getSpawnTime();
      return (double)since < (Double)(iEntity.blackout_Client$isOwn() ? rotation.noOwnTime : rotation.noOtherTime).get() * 1000.0D;
   }

   public static void swing(SwingState state, SwingType type, class_1268 hand) {
      swing.swing(state, type, hand);
   }

   public static void mineSwing(SwingSettings.MiningSwingState state) {
      swing.mineSwing(state);
   }

   public static PlaceData getPlaceData(class_2338 pos) {
      return facing.getPlaceData(pos, (DoublePredicate)null, (DoublePredicate)null, true);
   }

   public static PlaceData getPlaceData(class_2338 pos, boolean ignoreContainers) {
      return facing.getPlaceData(pos, (DoublePredicate)null, (DoublePredicate)null, ignoreContainers);
   }

   public static PlaceData getPlaceData(class_2338 pos, DoublePredicate<class_2338, class_2350> predicateOR, DoublePredicate<class_2338, class_2350> predicateAND, boolean ignoreContainers) {
      return facing.getPlaceData(pos, predicateOR, predicateAND, ignoreContainers);
   }

   public static PlaceData getPlaceData(class_2338 pos, DoublePredicate<class_2338, class_2350> predicateOR, DoublePredicate<class_2338, class_2350> predicateAND) {
      return facing.getPlaceData(pos, predicateOR, predicateAND, true);
   }

   public static class_2350 getPlaceOnDirection(class_2338 pos) {
      return facing.getPlaceOnDirection(pos);
   }

   public static boolean shouldInteractTrace() {
      return (Boolean)raytrace.interactTrace.get();
   }

   public static boolean shouldPlaceTrace() {
      return (Boolean)raytrace.placeTrace.get();
   }

   public static boolean shouldAttackTrace() {
      return (Boolean)raytrace.attackTrace.get();
   }

   public static boolean shouldMineTrace() {
      return (Boolean)raytrace.mineTrace.get();
   }

   public static boolean interactTrace(class_2338 pos) {
      return raytrace.interactTrace(pos);
   }

   public static boolean placeTrace(class_2338 pos) {
      return raytrace.placeTrace(pos);
   }

   public static boolean attackTrace(class_238 bb) {
      return raytrace.attackTrace(bb);
   }

   public static boolean mineTrace(class_2338 pos) {
      return raytrace.mineTrace(pos);
   }

   public static boolean oldCrystals() {
      return (Boolean)server.oldCrystals.get();
   }

   public static boolean cc() {
      return (Boolean)server.cc.get();
   }

   public static boolean grimMovement() {
      return (Boolean)server.grimMovement.get();
   }

   public static boolean grimPackets() {
      return (Boolean)server.grimPackets.get();
   }

   public static boolean grimUsing() {
      return (Boolean)server.grimUsing.get();
   }

   public static boolean strictSprint() {
      return (Boolean)server.strictSprint.get();
   }

   public static boolean stepPredict() {
      return (Boolean)extrapolation.stepPredict.get();
   }

   public static int reverseStepTicks() {
      return (Integer)extrapolation.reverseStepTicks.get();
   }

   public static boolean reverseStepPredict() {
      return (Boolean)extrapolation.reverseStepPredict.get();
   }

   public static int stepTicks() {
      return (Integer)extrapolation.stepTicks.get();
   }

   public static boolean jumpPredict() {
      return (Boolean)extrapolation.jumpPredict.get();
   }
}
