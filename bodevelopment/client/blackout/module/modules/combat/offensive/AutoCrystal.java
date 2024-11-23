package bodevelopment.client.blackout.module.modules.combat.offensive;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.enums.RotationType;
import bodevelopment.client.blackout.enums.SwingHand;
import bodevelopment.client.blackout.enums.SwingState;
import bodevelopment.client.blackout.enums.SwingType;
import bodevelopment.client.blackout.enums.SwitchMode;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.EntityAddEvent;
import bodevelopment.client.blackout.event.events.MoveEvent;
import bodevelopment.client.blackout.event.events.PacketEvent;
import bodevelopment.client.blackout.event.events.RemoveEvent;
import bodevelopment.client.blackout.event.events.RenderEvent;
import bodevelopment.client.blackout.event.events.TickEvent;
import bodevelopment.client.blackout.interfaces.functional.EpicInterface;
import bodevelopment.client.blackout.interfaces.mixin.IEndCrystalEntity;
import bodevelopment.client.blackout.interfaces.mixin.IRaycastContext;
import bodevelopment.client.blackout.keys.KeyBind;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.mixin.accessors.AccessorInteractEntityC2SPacket;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.modules.combat.misc.Suicide;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.module.setting.multisettings.BoxMultiSetting;
import bodevelopment.client.blackout.randomstuff.ExtrapolationMap;
import bodevelopment.client.blackout.randomstuff.FindResult;
import bodevelopment.client.blackout.randomstuff.Rotation;
import bodevelopment.client.blackout.randomstuff.timers.RenderList;
import bodevelopment.client.blackout.randomstuff.timers.TickTimerList;
import bodevelopment.client.blackout.randomstuff.timers.TimerList;
import bodevelopment.client.blackout.randomstuff.timers.TimerMap;
import bodevelopment.client.blackout.util.BoxUtils;
import bodevelopment.client.blackout.util.ChatUtils;
import bodevelopment.client.blackout.util.DamageUtils;
import bodevelopment.client.blackout.util.EntityUtils;
import bodevelopment.client.blackout.util.InvUtils;
import bodevelopment.client.blackout.util.MovementPrediction;
import bodevelopment.client.blackout.util.OLEPOSSUtils;
import bodevelopment.client.blackout.util.RotationUtils;
import bodevelopment.client.blackout.util.SettingUtils;
import bodevelopment.client.blackout.util.render.Render3DUtils;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Predicate;
import net.minecraft.class_124;
import net.minecraft.class_1268;
import net.minecraft.class_1294;
import net.minecraft.class_1297;
import net.minecraft.class_1303;
import net.minecraft.class_1309;
import net.minecraft.class_1511;
import net.minecraft.class_1542;
import net.minecraft.class_1657;
import net.minecraft.class_1683;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1831;
import net.minecraft.class_2189;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_2596;
import net.minecraft.class_265;
import net.minecraft.class_2680;
import net.minecraft.class_2824;
import net.minecraft.class_2868;
import net.minecraft.class_2885;
import net.minecraft.class_3532;
import net.minecraft.class_3965;
import net.minecraft.class_742;
import net.minecraft.class_3959.class_242;
import net.minecraft.class_3959.class_3960;

public class AutoCrystal extends Module {
   private static AutoCrystal INSTANCE;
   private final SettingGroup sgPlace = this.addGroup("Place");
   private final SettingGroup sgAttack = this.addGroup("Attack");
   private final SettingGroup sgIdPredict = this.addGroup("ID Predict", "Very unstable. Might kick or just not work");
   private final SettingGroup sgInhibit = this.addGroup("Inhibit");
   private final SettingGroup sgSlow = this.addGroup("Slow");
   private final SettingGroup sgFacePlace = this.addGroup("Face Place");
   private final SettingGroup sgRaytraceBypass = this.addGroup("Raytrace Bypass");
   private final SettingGroup sgRotation = this.addGroup("Rotation");
   private final SettingGroup sgSwitch = this.addGroup("Switch");
   private final SettingGroup sgDamage = this.addGroup("Damage");
   private final SettingGroup sgExtrapolation = this.addGroup("Extrapolation");
   private final SettingGroup sgDamageWait = this.addGroup("Damage Wait");
   private final SettingGroup sgRender = this.addGroup("Render");
   private final SettingGroup sgCalculation = this.addGroup("Calculations");
   private final SettingGroup sgCompatibility = this.addGroup("Compatibility");
   private final SettingGroup sgDebug = this.addGroup("Debug");
   private final Setting<Boolean> place;
   private final Setting<Boolean> pauseEatPlace;
   private final Setting<AutoCrystal.ActionSpeedMode> placeSpeedMode;
   private final Setting<Double> placeSpeedLimit;
   private final Setting<Double> constantPlaceSpeed;
   private final Setting<Double> placeSpeed;
   private final Setting<AutoCrystal.DelayMode> placeDelayMode;
   private final Setting<Double> placeDelay;
   private final Setting<Integer> placeDelayTicks;
   private final Setting<Boolean> ahd;
   private final Setting<Integer> ahdTries;
   private final Setting<Integer> ahdTime;
   private final Setting<Boolean> ignoreItems;
   private final Setting<Boolean> ignoreExp;
   private final Setting<Boolean> requireRotation;
   private final Setting<Boolean> attack;
   private final Setting<Integer> attackPackets;
   private final Setting<Boolean> pauseEatAttack;
   private final Setting<Boolean> onlyOwn;
   private final Setting<Boolean> antiWeakness;
   private final Setting<AutoCrystal.ExistedMode> existedCheckMode;
   private final Setting<AutoCrystal.DelayMode> existedMode;
   private final Setting<Double> existed;
   private final Setting<Integer> existedTicks;
   private final Setting<AutoCrystal.ActionSpeedMode> attackSpeedMode;
   private final Setting<Double> attackSpeedLimit;
   private final Setting<Double> constantAttackSpeed;
   private final Setting<Double> attackSpeed;
   private final Setting<AutoCrystal.SetDeadMode> setDead;
   private final Setting<Double> cpsTime;
   private final Setting<Integer> predictAttacks;
   private final Setting<Integer> idStart;
   private final Setting<Integer> predictStep;
   private final Setting<Integer> predictFlexibility;
   private final Setting<Boolean> predictSwing;
   private final Setting<Boolean> inhibit;
   private final Setting<Integer> inhibitTicks;
   private final Setting<Integer> inhibitAttacks;
   private final Setting<Boolean> fullInhibit;
   private final Setting<Integer> fullInhibitTicks;
   private final Setting<Integer> fullInhibitAttacks;
   private final Setting<Boolean> inhibitCollide;
   private final Setting<Double> slowDamage;
   private final Setting<Double> slowSpeed;
   private final Setting<Double> slowHealth;
   private final Setting<KeyBind> holdFacePlace;
   private final Setting<Double> facePlaceHealth;
   private final Setting<Double> armorFacePlace;
   private final Setting<Double> facePlaceDamage;
   private final Setting<Boolean> ignoreSlow;
   public final Setting<Boolean> raytraceBypass;
   public final Setting<Integer> raytraceDelay;
   public final Setting<Integer> raytraceTime;
   public final Setting<Integer> raytraceAngle;
   private final Setting<Boolean> moveOffset;
   private final Setting<Double> placeHeight;
   private final Setting<Double> attackHeight;
   private final Setting<AutoCrystal.ACSwitchMode> switchMode;
   private final Setting<SwitchMode> antiWeaknessSwitch;
   private final Setting<Double> placeSwitchPenalty;
   private final Setting<Double> attackSwitchPenalty;
   private final Setting<Double> minPlace;
   private final Setting<Boolean> checkSelfPlacing;
   private final Setting<Double> maxSelfPlace;
   private final Setting<Double> minSelfRatio;
   private final Setting<Boolean> checkFriendPlacing;
   private final Setting<Double> maxFriendPlace;
   private final Setting<Double> minFriendRatio;
   private final Setting<Boolean> checkEnemyAttack;
   private final Setting<Double> minAttack;
   private final Setting<Boolean> checkSelfAttack;
   private final Setting<Double> maxSelfAttack;
   private final Setting<Double> minSelfAttackRatio;
   private final Setting<Boolean> checkFriendAttack;
   private final Setting<Double> maxFriendAttack;
   private final Setting<Double> minFriendAttackRatio;
   private final Setting<Double> forcePop;
   private final Setting<Double> selfPop;
   private final Setting<Double> friendPop;
   private final Setting<AutoCrystal.AntiPopMode> antiPopMode;
   private final Setting<Integer> extrapolation;
   private final Setting<Integer> selfExt;
   private final Setting<AutoCrystal.RangeExtMode> rangeExtMode;
   private final Setting<Integer> rangeExt;
   private final Setting<Double> hitboxExpand;
   private final Setting<Boolean> extrapolateHitbox;
   private final Setting<Boolean> flexibleHitbox;
   private final Setting<Double> preferHitboxExpand;
   private final Setting<Double> hitboxValue;
   private final Setting<Boolean> damageWait;
   private final Setting<Integer> waitStartExt;
   private final Setting<Integer> waitEndExt;
   private final Setting<Double> minDifference;
   private final Setting<Integer> maxWait;
   private final Setting<Boolean> placeSwing;
   private final Setting<SwingHand> placeHand;
   private final Setting<Boolean> attackSwing;
   private final Setting<SwingHand> attackHand;
   private final Setting<Boolean> render;
   private final Setting<AutoCrystal.RenderMode> renderMode;
   private final Setting<Double> renderTime;
   private final Setting<Double> fadeTime;
   private final Setting<Double> animMoveSpeed;
   private final Setting<Double> animMoveExponent;
   private final Setting<AutoCrystal.AnimationMode> animationMode;
   private final Setting<Double> animSizeExponent;
   private final BoxMultiSetting renderSetting;
   private final Setting<Boolean> renderDamage;
   private final Setting<Boolean> renderExt;
   private final Setting<Boolean> renderBoxExt;
   private final Setting<Boolean> renderSelfExt;
   private final Setting<Double> damageValue;
   private final Setting<Double> selfDmgValue;
   private final Setting<Double> friendDmgValue;
   private final Setting<Double> moveValue;
   private final Setting<Double> selfMoveValue;
   private final Setting<Double> friendMoveValue;
   private final Setting<Double> rotationValue;
   private final Setting<Double> wallValue;
   private final Setting<Double> noRotateValue;
   private final Setting<Double> raytraceBypassValue;
   private final Setting<Integer> maxTargets;
   private final Setting<Boolean> noCollide;
   private final Setting<Boolean> spawningCollide;
   private final Setting<Boolean> attackCollide;
   private final Setting<Double> antiJitter;
   private final Setting<Double> antiJitterTime;
   private final Setting<Double> autoMineCollideValue;
   private final Setting<AutoCrystal.AsyncMode> async;
   private final Setting<Boolean> rotationFriendly;
   private final Setting<Double> rangeValue;
   private final Setting<Double> rangeStartDist;
   private final Setting<Boolean> eco;
   private final Setting<Double> prePlaceProgress;
   private final Setting<Boolean> autoMineAttack;
   private final Setting<Double> autoMineAttackProgress;
   private final Setting<Boolean> debugPlace;
   private final Setting<Boolean> debugAttack;
   private final Setting<Boolean> removeTime;
   private final TimerList<class_238> spawning;
   private final TickTimerList<class_2338> existedTicksList;
   private final TimerList<class_2338> existedList;
   private final TickTimerList<class_2338> placeDelayTicksList;
   private final TimerList<class_2338> placeDelayList;
   private final TimerList<Integer> attackTimers;
   private final ExtrapolationMap extMap;
   private final ExtrapolationMap minWaitExtMap;
   private final ExtrapolationMap maxWaitExtMap;
   private final Map<class_1297, class_238> boxMap;
   private final List<class_238> valueBoxes;
   private final TickTimerList<Integer> attacked;
   private final TickTimerList<int[]> inhibitList;
   private final TickTimerList<int[]> fullInhibitList;
   private final TimerList<class_2338> own;
   private final TimerMap<class_2338, Integer> hitBoxDesyncList;
   private final TickTimerList<Integer> waitTimes;
   private double moveModifier;
   public class_2338 placePos;
   private class_243 rangePos;
   private boolean shouldCalc;
   private class_1511 targetCrystal;
   private class_1309 target;
   private double selfHealth;
   private double enemyHealth;
   private double friendHealth;
   private double selfDamage;
   public double enemyDamage;
   private double friendDamage;
   private boolean isPop;
   private boolean suicide;
   private long lastAttack;
   private long lastPlace;
   private long lastSwitch;
   private long lastCalc;
   private long lastChange;
   public boolean placing;
   private boolean facePlacing;
   private class_243 movement;
   private final List<class_1657> targets;
   private final Map<class_1657, Float> moveDirs;
   private final RenderList<class_2338> earthRender;
   private double renderProgress;
   private class_2338 renderPos;
   private class_243 renderVec;
   private class_243 renderTargetVec;
   private class_2350 crystalDir;
   private FindResult crystalResult;
   private class_1268 crystalHand;
   private boolean lastWasAttack;
   private boolean antiWeaknessAvailable;
   private FindResult awResult;
   private final List<Long> explosions;
   private double cps;
   private int bypassTimer;
   private int raytraceLeft;
   private final Predicate<class_1799> antiWeaknessPredicate;
   public class_742 targetedPlayer;
   private int confirmedId;
   private int sentId;

   public AutoCrystal() {
      super("Auto Crystal", "Places and attacks crystals.", SubCategory.OFFENSIVE, true);
      this.place = this.sgPlace.b("Place", true, "Places crystals.");
      this.pauseEatPlace = this.sgPlace.b("Pause Eat Place", false, "Pauses placing while eating.");
      this.placeSpeedMode = this.sgPlace.e("Place Speed Mode", AutoCrystal.ActionSpeedMode.Sync, ".");
      this.placeSpeedLimit = this.sgPlace.d("Place Speed Limit", 0.0D, 0.0D, 20.0D, 0.1D, "Maximum amount of places every second. 0 = no limit", () -> {
         return this.placeSpeedMode.get() == AutoCrystal.ActionSpeedMode.Sync;
      });
      this.constantPlaceSpeed = this.sgPlace.d("Constant Place Speed", 10.0D, 0.0D, 20.0D, 0.1D, ".", () -> {
         return this.placeSpeedMode.get() == AutoCrystal.ActionSpeedMode.Sync;
      });
      this.placeSpeed = this.sgPlace.d("Place Speed", 20.0D, 0.0D, 20.0D, 0.1D, ".", () -> {
         return this.placeSpeedMode.get() == AutoCrystal.ActionSpeedMode.Normal;
      });
      this.placeDelayMode = this.sgPlace.e("Place Delay Mode", AutoCrystal.DelayMode.Ticks, ".");
      this.placeDelay = this.sgPlace.d("Place Delay", 0.0D, 0.0D, 1.0D, 0.01D, ".", () -> {
         return this.placeDelayMode.get() == AutoCrystal.DelayMode.Seconds;
      });
      this.placeDelayTicks = this.sgPlace.i("Place Delay Ticks", 0, 0, 20, 1, ".", () -> {
         return this.placeDelayMode.get() == AutoCrystal.DelayMode.Ticks;
      });
      this.ahd = this.sgPlace.b("AHD", true, "");
      SettingGroup var10001 = this.sgPlace;
      Setting var10008 = this.ahd;
      Objects.requireNonNull(var10008);
      this.ahdTries = var10001.i("AHD Tries", 3, 0, 20, 1, "", var10008::get);
      var10001 = this.sgPlace;
      var10008 = this.ahd;
      Objects.requireNonNull(var10008);
      this.ahdTime = var10001.i("AHD Time", 20, 0, 100, 1, "", var10008::get);
      this.ignoreItems = this.sgPlace.b("Ignore Items", true, "");
      this.ignoreExp = this.sgPlace.b("Ignore Exp", true, "");
      this.requireRotation = this.sgPlace.b("Require Rotation", true, "Places crystals.", () -> {
         return SettingUtils.shouldRotate(RotationType.Interact);
      });
      this.attack = this.sgAttack.b("Attack", true, "Attacks crystals.");
      this.attackPackets = this.sgAttack.i("Attack Packets", 1, 0, 5, 1, "Sends this many attack packets each hit. Probably useless but u could test some stuff.");
      this.pauseEatAttack = this.sgAttack.b("Pause Eat Attack", false, "Pauses attacking while eating.");
      this.onlyOwn = this.sgAttack.b("Only Own", false, "Only attacks crystals placed by you.");
      this.antiWeakness = this.sgAttack.b("Anti Weakness", true, ".");
      this.existedCheckMode = this.sgAttack.e("Existed Check Mode", AutoCrystal.ExistedMode.Client, ".");
      this.existedMode = this.sgAttack.e("Existed Mode", AutoCrystal.DelayMode.Ticks, "Should crystal existed times be counted in seconds or ticks.");
      this.existed = this.sgAttack.d("Explode Delay", 0.0D, 0.0D, 1.0D, 0.01D, "How many seconds should the crystal exist before attacking.", () -> {
         return this.existedMode.get() == AutoCrystal.DelayMode.Seconds;
      });
      this.existedTicks = this.sgAttack.i("Explode Delay Ticks", 0, 0, 20, 1, "How many ticks should the crystal exist before attacking.", () -> {
         return this.existedMode.get() == AutoCrystal.DelayMode.Ticks;
      });
      this.attackSpeedMode = this.sgAttack.e("Attack Speed Mode", AutoCrystal.ActionSpeedMode.Sync, ".");
      this.attackSpeedLimit = this.sgAttack.d("Attack Speed Limit", 0.0D, 0.0D, 20.0D, 0.1D, "Maximum amount of attacks every second. 0 = no limit", () -> {
         return this.attackSpeedMode.get() == AutoCrystal.ActionSpeedMode.Sync;
      });
      this.constantAttackSpeed = this.sgAttack.d("Constant Attack Speed", 10.0D, 0.0D, 20.0D, 0.1D, ".", () -> {
         return this.attackSpeedMode.get() == AutoCrystal.ActionSpeedMode.Sync;
      });
      this.attackSpeed = this.sgAttack.d("Attack Speed", 20.0D, 0.0D, 20.0D, 0.1D, ".", () -> {
         return this.attackSpeedMode.get() == AutoCrystal.ActionSpeedMode.Normal;
      });
      this.setDead = this.sgAttack.e("Set Dead", AutoCrystal.SetDeadMode.Disabled, "Hides the crystal after hitting it. Not needed since the module already is smart enough.");
      this.cpsTime = this.sgAttack.d("Cps Time", 5.0D, 1.0D, 20.0D, 0.1D, "Average cps from past x seconds.");
      this.predictAttacks = this.sgIdPredict.i("Predict Attacks", 0, 0, 10, 1, ".");
      this.idStart = this.sgIdPredict.i("Id Start", 1, 1, 5, 1, ".");
      this.predictStep = this.sgIdPredict.i("Predict Step", 1, 1, 5, 1, ".");
      this.predictFlexibility = this.sgIdPredict.i("Predict Flexiblity", 2, 0, 10, 1, "Might wanna make high on higher ping and stable server.");
      this.predictSwing = this.sgIdPredict.b("Predict Swing", true, ".");
      this.inhibit = this.sgInhibit.b("Inhibit", true, "Ignores crystals after a certain amount of time or attacks.");
      this.inhibitTicks = this.sgInhibit.i("Inhibit Ticks", 10, 0, 100, 1, ".");
      this.inhibitAttacks = this.sgInhibit.i("Inhibit Attacks", 1, 1, 10, 1, ".");
      var10001 = this.sgInhibit;
      Setting var10005 = this.inhibit;
      Objects.requireNonNull(var10005);
      this.fullInhibit = var10001.b("Full Inhibit", true, "Ignores crystals after a certain amount of time or attacks.", var10005::get);
      this.fullInhibitTicks = this.sgInhibit.i("Full Inhibit Ticks", 100, 0, 400, 5, ".", () -> {
         return (Boolean)this.inhibit.get() && (Boolean)this.fullInhibit.get();
      });
      this.fullInhibitAttacks = this.sgInhibit.i("Full Inhibit Attacks", 2, 1, 10, 1, ".", () -> {
         return (Boolean)this.inhibit.get() && (Boolean)this.fullInhibit.get();
      });
      this.inhibitCollide = this.sgInhibit.b("Inhibit Collide", false, "Doesn't allow place pos to collide with inhibit crystals.", () -> {
         return (Boolean)this.inhibit.get() && (Boolean)this.fullInhibit.get();
      });
      this.slowDamage = this.sgSlow.d("Slow Damage", 3.0D, 0.0D, 20.0D, 0.1D, "Switches to slow speed when the target would take under this amount of damage.");
      this.slowSpeed = this.sgSlow.d("Slow Speed", 2.0D, 0.0D, 20.0D, 0.1D, "How many times should the module place per second when damage is under slow damage.");
      this.slowHealth = this.sgSlow.d("Slow Health", 10.0D, 0.0D, 20.0D, 0.5D, "Only slow places if enemy has over x health.");
      this.holdFacePlace = this.sgFacePlace.k("Hold Face Place", "Faceplaces when holding this key.");
      this.facePlaceHealth = this.sgFacePlace.d("Face Place Health", 0.0D, 0.0D, 10.0D, 0.1D, "Automatically face places if enemy has under this much health.");
      this.armorFacePlace = this.sgFacePlace.d("Armor Face Place", 10.0D, 0.0D, 100.0D, 1.0D, "Face places if enemy's any armor piece is under this durability.");
      this.facePlaceDamage = this.sgFacePlace.d("Face Place Damage", 0.0D, 0.0D, 10.0D, 0.1D, "Sets min place and min attack to this.");
      this.ignoreSlow = this.sgFacePlace.b("Ignore Slow", true, "Doesn't slow place when faceplacing.");
      this.raytraceBypass = this.sgRaytraceBypass.b("Raytrace Bypass", false, ".");
      var10001 = this.sgRaytraceBypass;
      var10008 = this.raytraceBypass;
      Objects.requireNonNull(var10008);
      this.raytraceDelay = var10001.i("Raytrace Delay", 10, 0, 100, 1, ".", var10008::get);
      var10001 = this.sgRaytraceBypass;
      var10008 = this.raytraceBypass;
      Objects.requireNonNull(var10008);
      this.raytraceTime = var10001.i("Raytrace Time", 15, 0, 100, 1, ".", var10008::get);
      var10001 = this.sgRaytraceBypass;
      var10008 = this.raytraceBypass;
      Objects.requireNonNull(var10008);
      this.raytraceAngle = var10001.i("Raytrace Min Angle", 45, 0, 100, 1, ".", var10008::get);
      this.moveOffset = this.sgRotation.b("Move Offset", true, ".");
      this.placeHeight = this.sgRotation.d("Place Height", 1.0D, 0.0D, 1.0D, 0.01D, "Height for place rotations.");
      this.attackHeight = this.sgRotation.d("Attack Height", 0.0D, 0.0D, 2.0D, 0.01D, "Height for attack rotations.");
      this.switchMode = this.sgSwitch.e("Switch", AutoCrystal.ACSwitchMode.Disabled, "Mode for switching to crystal in main hand.");
      this.antiWeaknessSwitch = this.sgSwitch.e("Anti-Weakness Switch", SwitchMode.Silent, ".");
      this.placeSwitchPenalty = this.sgSwitch.d("Place Switch Penalty", 0.0D, 0.0D, 1.0D, 0.05D, "Time to wait after switching before placing crystals.");
      this.attackSwitchPenalty = this.sgSwitch.d("Attack Switch Penalty", 0.0D, 0.0D, 1.0D, 0.05D, "Time to wait after switching before attacking crystals.");
      this.minPlace = this.sgDamage.d("Min Place", 5.0D, 0.0D, 20.0D, 0.1D, "Minimum damage to place.");
      this.checkSelfPlacing = this.sgDamage.b("Self Placing", true, "Checks self damage when placing.");
      var10001 = this.sgDamage;
      var10008 = this.checkSelfPlacing;
      Objects.requireNonNull(var10008);
      this.maxSelfPlace = var10001.d("Max Place", 10.0D, 0.0D, 20.0D, 0.1D, "Max self damage for placing.", var10008::get);
      var10001 = this.sgDamage;
      var10008 = this.checkSelfPlacing;
      Objects.requireNonNull(var10008);
      this.minSelfRatio = var10001.d("Min Place Ratio", 2.0D, 0.0D, 20.0D, 0.1D, "Min self damage ratio for placing (enemy / self).", var10008::get);
      this.checkFriendPlacing = this.sgDamage.b("Friend Placing", true, "Checks friend damage when placing.");
      var10001 = this.sgDamage;
      var10008 = this.checkFriendPlacing;
      Objects.requireNonNull(var10008);
      this.maxFriendPlace = var10001.d("Max Friend Place", 12.0D, 0.0D, 20.0D, 0.1D, "Max friend damage for placing.", var10008::get);
      var10001 = this.sgDamage;
      var10008 = this.checkFriendPlacing;
      Objects.requireNonNull(var10008);
      this.minFriendRatio = var10001.d("Min Friend Place Ratio", 1.0D, 0.0D, 20.0D, 0.1D, "Min friend damage ratio for placing (enemy / friend).", var10008::get);
      this.checkEnemyAttack = this.sgDamage.b("Enemy Attack", true, "Checks enemy damage when attacking.");
      var10001 = this.sgDamage;
      var10008 = this.checkEnemyAttack;
      Objects.requireNonNull(var10008);
      this.minAttack = var10001.d("Min Attack", 5.0D, 0.0D, 20.0D, 0.1D, "Minimum damage to attack.", var10008::get);
      this.checkSelfAttack = this.sgDamage.b("Self Attack", true, "Checks self damage when attacking.");
      var10001 = this.sgDamage;
      var10008 = this.checkSelfAttack;
      Objects.requireNonNull(var10008);
      this.maxSelfAttack = var10001.d("Max Attack", 10.0D, 0.0D, 20.0D, 0.1D, "Max self damage for attacking.", var10008::get);
      this.minSelfAttackRatio = this.sgDamage.d("Min Attack Ratio", 2.0D, 0.0D, 20.0D, 0.1D, "Min self damage ratio for attacking (enemy / self).", () -> {
         return (Boolean)this.checkSelfAttack.get() && (Boolean)this.checkEnemyAttack.get();
      });
      this.checkFriendAttack = this.sgDamage.b("Friend Attack", true, "Checks friend damage when attacking.");
      var10001 = this.sgDamage;
      var10008 = this.checkFriendAttack;
      Objects.requireNonNull(var10008);
      this.maxFriendAttack = var10001.d("Max Friend Attack", 12.0D, 0.0D, 20.0D, 0.1D, "Max friend damage for attacking.", var10008::get);
      var10001 = this.sgDamage;
      var10008 = this.checkFriendAttack;
      Objects.requireNonNull(var10008);
      this.minFriendAttackRatio = var10001.d("Min Friend Attack Ratio", 1.0D, 0.0D, 20.0D, 0.1D, "Min friend damage ratio for attacking (enemy / friend).", var10008::get);
      this.forcePop = this.sgDamage.d("Force Pop", 0.0D, 0.0D, 5.0D, 0.25D, "Ignores damage checks if any enemy will be popped in x hits.");
      this.selfPop = this.sgDamage.d("Anti Pop", 1.0D, 0.0D, 5.0D, 0.25D, "Ignores damage checks if any enemy will be popped in x hits.");
      this.friendPop = this.sgDamage.d("Anti Friend Pop", 0.0D, 0.0D, 5.0D, 0.25D, "Ignores damage checks if any enemy will be popped in x hits.");
      this.antiPopMode = this.sgDamage.e("Anti Pop Mode", AutoCrystal.AntiPopMode.Change, ".");
      this.extrapolation = this.sgExtrapolation.i("Extrapolation", 0, 0, 20, 1, "How many ticks of movement should be predicted for enemy damage checks.");
      this.selfExt = this.sgExtrapolation.i("Self Extrapolation", 0, 0, 20, 1, "How many ticks of movement should be predicted for self damage checks.");
      this.rangeExtMode = this.sgExtrapolation.e("Range Extrapolation Mode", AutoCrystal.RangeExtMode.Semi, ".");
      this.rangeExt = this.sgExtrapolation.i("Range Extrapolation", 0, 0, 20, 1, "How many ticks of movement should be predicted for attack ranges before placing.");
      this.hitboxExpand = this.sgExtrapolation.d("Hitbox Expand", 1.0D, 0.0D, 2.0D, 0.02D, "");
      this.extrapolateHitbox = this.sgExtrapolation.b("Extrapolate Hitbox", false, "");
      this.flexibleHitbox = this.sgExtrapolation.b("Flexible Hitbox", false, ".", () -> {
         return (Double)this.hitboxExpand.get() > 0.0D;
      });
      this.preferHitboxExpand = this.sgExtrapolation.d("Value Hitbox Expand", 2.0D, 0.0D, 2.0D, 0.02D, "");
      this.hitboxValue = this.sgExtrapolation.d("Hitbox Value", -8.0D, -10.0D, 10.0D, 0.2D, ".");
      this.damageWait = this.sgDamageWait.b("Damage Wait", false, ".");
      this.waitStartExt = this.sgDamageWait.i("Wait Start Extrapolation", 2, 0, 20, 1, ".");
      this.waitEndExt = this.sgDamageWait.i("Wait End Extrapolation", 5, 0, 20, 1, ".");
      this.minDifference = this.sgDamageWait.d("Min Difference", 0.0D, 0.0D, 10.0D, 0.1D, ".");
      this.maxWait = this.sgDamageWait.i("Max Wait", 3, 0, 20, 1, ".");
      this.placeSwing = this.sgRender.b("Place Swing", false, "Renders swing animation when placing a crystal.");
      this.placeHand = this.sgRender.e("Place Hand", SwingHand.RealHand, "Which hand should be swung.");
      this.attackSwing = this.sgRender.b("Attack Swing", false, "Renders swing animation when attacking a crystal.");
      this.attackHand = this.sgRender.e("Attack Hand", SwingHand.RealHand, "Which hand should be swung.");
      this.render = this.sgRender.b("Render", true, "Renders box on placement.");
      var10001 = this.sgRender;
      AutoCrystal.RenderMode var10003 = AutoCrystal.RenderMode.BlackOut;
      var10005 = this.render;
      Objects.requireNonNull(var10005);
      this.renderMode = var10001.e("Render Mode", var10003, "What should the render look like.", var10005::get);
      this.renderTime = this.sgRender.d("Render Time", 0.3D, 0.0D, 10.0D, 0.1D, "How long the box should remain in full alpha value.", () -> {
         return this.renderMode.get() == AutoCrystal.RenderMode.Earthhack || this.renderMode.get() == AutoCrystal.RenderMode.Simple || this.renderMode.get() == AutoCrystal.RenderMode.Confirm;
      });
      this.fadeTime = this.sgRender.d("Fade Time", 1.0D, 0.0D, 10.0D, 0.1D, "How long the fading should take.", () -> {
         return this.renderMode.get() == AutoCrystal.RenderMode.Earthhack || this.renderMode.get() == AutoCrystal.RenderMode.Simple || this.renderMode.get() == AutoCrystal.RenderMode.Confirm;
      });
      this.animMoveSpeed = this.sgRender.d("Move Speed", 2.0D, 0.0D, 10.0D, 0.1D, "How fast should blackout mode box move.", () -> {
         return this.renderMode.get() == AutoCrystal.RenderMode.BlackOut;
      });
      this.animMoveExponent = this.sgRender.d("Move Exponent", 3.0D, 0.0D, 10.0D, 0.1D, "Moves faster when longer away from the target.", () -> {
         return this.renderMode.get() == AutoCrystal.RenderMode.BlackOut;
      });
      this.animationMode = this.sgRender.e("Animation Size Mode", AutoCrystal.AnimationMode.Full, ".");
      this.animSizeExponent = this.sgRender.d("Animation Size Exponent", 3.0D, 0.0D, 10.0D, 0.1D, "How fast should blackout mode box grow.", () -> {
         return this.renderMode.get() == AutoCrystal.RenderMode.BlackOut;
      });
      this.renderSetting = BoxMultiSetting.of(this.sgRender, "Box");
      this.renderDamage = this.sgRender.b("Render Damage", true, ".");
      this.renderExt = this.sgRender.b("Render Extrapolation", false, "Renders boxes at players' predicted positions.");
      this.renderBoxExt = this.sgRender.b("Render Box Extrapolation", false, "Renders boxes at players' predicted positions.");
      this.renderSelfExt = this.sgRender.b("Render Self Extrapolation", false, "Renders box at your predicted position.");
      this.damageValue = this.sgCalculation.d("Damage Value", 1.0D, -5.0D, 5.0D, 0.1D, ".");
      this.selfDmgValue = this.sgCalculation.d("Self Damage Value", -1.0D, -5.0D, 5.0D, 0.05D, ".");
      this.friendDmgValue = this.sgCalculation.d("Friend Damage Value", 0.0D, -5.0D, 5.0D, 0.05D, ".");
      this.moveValue = this.sgCalculation.d("Move Dir Value", 0.0D, -5.0D, 5.0D, 0.1D, "Adds x value if enemy is moving towards the position.");
      this.selfMoveValue = this.sgCalculation.d("Self Move Value", 0.0D, -5.0D, 5.0D, 0.1D, "Adds x value if enemy is moving towards the position.");
      this.friendMoveValue = this.sgCalculation.d("Friend Move Value", 0.0D, -5.0D, 5.0D, 0.1D, ".");
      this.rotationValue = this.sgCalculation.d("Rotation Value", 0.0D, -5.0D, 5.0D, 0.1D, ".");
      this.wallValue = this.sgCalculation.d("Wall Value", 0.0D, -5.0D, 5.0D, 0.1D, ".");
      this.noRotateValue = this.sgCalculation.d("No Rotate Value", 0.0D, -5.0D, 5.0D, 0.1D, ".", SettingUtils::rotationIgnoreEnabled);
      var10001 = this.sgCalculation;
      var10008 = this.raytraceBypass;
      Objects.requireNonNull(var10008);
      this.raytraceBypassValue = var10001.d("Raytrace Bypass Value", -4.0D, -5.0D, 5.0D, 0.1D, ".", var10008::get);
      this.maxTargets = this.sgCalculation.i("Max Targets", 3, 1, 10, 1, ".");
      this.noCollide = this.sgCalculation.b("No Collide", false, "Doesn't place if any crystal is half inside the pos.");
      var10001 = this.sgCalculation;
      var10005 = this.noCollide;
      Objects.requireNonNull(var10005);
      this.spawningCollide = var10001.b("Spawning Collide", false, "Doesn't place if any spawning crystal is half inside the pos.", var10005::get);
      var10001 = this.sgCalculation;
      var10005 = this.noCollide;
      Objects.requireNonNull(var10005);
      this.attackCollide = var10001.b("Attack Collide", false, ".", var10005::get);
      this.antiJitter = this.sgCalculation.d("Anti Jitter", 0.5D, 0.0D, 5.0D, 0.1D, "Doesn't place if any crystal is half inside the pos.");
      this.antiJitterTime = this.sgCalculation.d("Anti Jitter Time", 0.2D, 0.0D, 1.0D, 0.01D, ".", () -> {
         return (Double)this.antiJitter.get() != 0.0D;
      });
      this.autoMineCollideValue = this.sgCalculation.d("Auto Mine Collide Value", 0.0D, -5.0D, 5.0D, 0.1D, ".");
      this.async = this.sgCalculation.e("Async", AutoCrystal.AsyncMode.Basic, "");
      this.rotationFriendly = this.sgCalculation.b("Rotation Friendly", true, ".");
      this.rangeValue = this.sgCalculation.d("Range Value", 1.0D, -5.0D, 5.0D, 0.1D, ".");
      this.rangeStartDist = this.sgCalculation.d("Range Start Dist", 0.0D, 0.0D, 6.0D, 0.1D, ".", () -> {
         return (Double)this.rangeValue.get() != 0.0D;
      });
      this.eco = this.sgCalculation.b("Eco", false, ".");
      this.prePlaceProgress = this.sgCompatibility.d("Pre Place Progress", 0.9D, 0.0D, 1.0D, 0.01D, ".");
      this.autoMineAttack = this.sgCompatibility.b("Auto Mine Attack", true, ".");
      var10001 = this.sgCompatibility;
      var10008 = this.autoMineAttack;
      Objects.requireNonNull(var10008);
      this.autoMineAttackProgress = var10001.d("Auto Mine Attack Progress", 0.75D, 0.0D, 1.0D, 0.01D, ".", var10008::get);
      this.debugPlace = this.sgDebug.b("Debug Place", false, ".");
      this.debugAttack = this.sgDebug.b("Debug Attack", false, ".");
      this.removeTime = this.sgDebug.b("Remove Time", false, ".");
      this.spawning = new TimerList(true);
      this.existedTicksList = new TickTimerList(true);
      this.existedList = new TimerList(true);
      this.placeDelayTicksList = new TickTimerList(true);
      this.placeDelayList = new TimerList(true);
      this.attackTimers = new TimerList(true);
      this.extMap = new ExtrapolationMap();
      this.minWaitExtMap = new ExtrapolationMap();
      this.maxWaitExtMap = new ExtrapolationMap();
      this.boxMap = new HashMap();
      this.valueBoxes = new ArrayList();
      this.attacked = new TickTimerList(true);
      this.inhibitList = new TickTimerList(true);
      this.fullInhibitList = new TickTimerList(true);
      this.own = new TimerList(true);
      this.hitBoxDesyncList = new TimerMap(true);
      this.waitTimes = new TickTimerList(true);
      this.moveModifier = 0.0D;
      this.placePos = null;
      this.rangePos = null;
      this.shouldCalc = false;
      this.targetCrystal = null;
      this.target = null;
      this.selfHealth = 0.0D;
      this.enemyHealth = 0.0D;
      this.friendHealth = 0.0D;
      this.selfDamage = 0.0D;
      this.enemyDamage = 0.0D;
      this.friendDamage = 0.0D;
      this.isPop = false;
      this.suicide = false;
      this.lastAttack = 0L;
      this.lastPlace = 0L;
      this.lastSwitch = 0L;
      this.lastCalc = 0L;
      this.lastChange = 0L;
      this.placing = false;
      this.facePlacing = false;
      this.movement = new class_243(0.0D, 0.0D, 0.0D);
      this.targets = new ArrayList();
      this.moveDirs = new HashMap();
      this.earthRender = RenderList.getList(false);
      this.renderProgress = 0.0D;
      this.renderPos = class_2338.field_10980;
      this.renderVec = class_243.field_1353;
      this.renderTargetVec = class_243.field_1353;
      this.crystalDir = class_2350.field_11033;
      this.crystalResult = null;
      this.crystalHand = null;
      this.lastWasAttack = false;
      this.antiWeaknessAvailable = false;
      this.explosions = Collections.synchronizedList(new ArrayList());
      this.cps = 0.0D;
      this.bypassTimer = 0;
      this.raytraceLeft = 0;
      this.antiWeaknessPredicate = (stack) -> {
         return stack.method_7909() instanceof class_1831;
      };
      this.targetedPlayer = null;
      this.confirmedId = 0;
      this.sentId = 0;
      INSTANCE = this;
   }

   public static AutoCrystal getInstance() {
      return INSTANCE;
   }

   public void onEnable() {
      this.placePos = null;
      this.shouldCalc = true;
   }

   public boolean shouldSkipListeners() {
      return false;
   }

   public String getInfo() {
      return String.format("%.1f", this.cps);
   }

   @Event
   public void onEntity(EntityAddEvent.Pre event) {
      if ((Integer)this.predictAttacks.get() > 0) {
         this.confirmedId = Math.max(this.confirmedId, event.id);
         if (this.sentId > this.confirmedId) {
            this.sentId = class_3532.method_15340(this.sentId, this.confirmedId, this.confirmedId + (Integer)this.predictFlexibility.get());
         } else {
            this.sentId = this.confirmedId;
         }
      }

      if (this.enabled) {
         if (event.entity instanceof class_1511) {
            class_2338 p = event.entity.method_24515();
            if (p.equals(this.placePos)) {
               this.explosions.add(System.currentTimeMillis());
            }

            if (this.existedCheckMode.get() == AutoCrystal.ExistedMode.Client) {
               this.addExisted(p);
            }

            this.placeDelayList.remove((timer) -> {
               return ((class_2338)timer.value).equals(p);
            });
            this.placeDelayTicksList.remove((timer) -> {
               return ((class_2338)timer.value).equals(p);
            });
            this.spawning.remove((timer) -> {
               return class_2338.method_49638(BoxUtils.feet((class_238)timer.value)).equals(event.entity.method_24515());
            });
            if ((Boolean)this.ahd.get()) {
               this.hitBoxDesyncList.remove((pos, timer) -> {
                  return pos.equals(p.method_10074());
               });
            }

         }
      }
   }

   @Event
   public void onEntity(EntityAddEvent.Post event) {
      if (this.enabled && event.entity instanceof class_1511) {
         switch((AutoCrystal.AsyncMode)this.async.get()) {
         case Basic:
            this.updateAttacking();
            break;
         case Dumb:
            this.updateAttacking();
            this.updatePlacing(true);
            break;
         case Heavy:
            if (this.updateCalc()) {
               this.updatePos();
            }

            this.update(true);
         }
      }

   }

   @Event
   public void onMovePre(MoveEvent.Pre event) {
      this.moveModifier -= 0.1D;
      this.moveModifier += event.movement.method_1033();
      this.moveModifier = class_3532.method_15350(this.moveModifier, 0.0D, 1.0D);
   }

   @Event
   public void onMove(MoveEvent.Post event) {
      if (this.enabled) {
         if (this.updateCalc()) {
            this.updatePos();
         }

         this.update(false);
      }

   }

   @Event
   public void onTickPre(TickEvent.Pre event) {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null && this.enabled) {
         --this.raytraceLeft;
         if (this.shouldRaytraceBypass(this.placePos) && ++this.bypassTimer > (Integer)this.raytraceDelay.get()) {
            Rotation rotation = this.raytraceRotation(this.placePos, true);
            if (rotation != null) {
               this.rotate(rotation.yaw(), rotation.pitch(), 1.0D, RotationType.Other, "raytrace");
               this.bypassTimer = 0;
               this.raytraceLeft = (Integer)this.raytraceTime.get();
            }
         }

         if ((Boolean)this.ahd.get()) {
            this.hitBoxDesyncList.remove((pos, timer) -> {
               return !this.almostColliding(pos.method_10084());
            });
         }

      }
   }

   @Event
   public void onTickPost(TickEvent.Post event) {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null && this.enabled) {
         this.end("raytrace");
         this.movement = BlackOut.mc.field_1724.method_19538().method_1023(BlackOut.mc.field_1724.field_6014, BlackOut.mc.field_1724.field_6036, BlackOut.mc.field_1724.field_5969);
         this.update(true);
      }
   }

   @Event
   public void onRender(RenderEvent.World.Post event) {
      if (!this.enabled) {
         this.updateRender(event.frameTime, true);
      } else {
         this.updateFacePlace();
         this.updateAntiWeakness();
         this.cps = 0.0D;
         synchronized(this.explosions) {
            this.explosions.removeIf((time) -> {
               double p = (double)(System.currentTimeMillis() - time) / 1000.0D;
               if (p >= (Double)this.cpsTime.get()) {
                  return true;
               } else {
                  double d = Math.min((Double)this.cpsTime.get() - p, 1.0D);
                  this.cps += d;
                  return false;
               }
            });
         }

         this.cps /= (Double)this.cpsTime.get() - 0.5D;
         if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null) {
            this.update(true);
            this.updateRender(event.frameTime, false);
         }

      }
   }

   @Event
   public void onSent(PacketEvent.Sent event) {
      if (this.enabled) {
         if (event.packet instanceof class_2868) {
            this.lastSwitch = System.currentTimeMillis();
         }

         class_2596 var3 = event.packet;
         if (var3 instanceof class_2885) {
            class_2885 packet = (class_2885)var3;
            this.own.replace(packet.method_12543().method_17777().method_10084(), 2.0D);
         }

      }
   }

   @Event
   public void onRemove(RemoveEvent event) {
      if ((Boolean)this.removeTime.get()) {
         class_1297 var3 = event.entity;
         if (var3 instanceof class_1511) {
            class_1511 entity = (class_1511)var3;
            long diff = System.currentTimeMillis() - ((IEndCrystalEntity)entity).blackout_Client$getSpawnTime();
            this.debug("removed after", diff + "ms");
         }
      }

   }

   private void debug(String string, String value) {
      ChatUtils.addMessage(string + " " + class_124.field_1075 + value);
   }

   private void updateAntiWeakness() {
      this.antiWeaknessAvailable = this.canAntiWeakness();
   }

   private boolean canAntiWeakness() {
      this.awResult = null;
      if (!(Boolean)this.antiWeakness.get()) {
         return false;
      } else {
         return this.antiWeaknessPredicate.test(Managers.PACKET.getStack()) ? true : (this.awResult = ((SwitchMode)this.antiWeaknessSwitch.get()).find(this.antiWeaknessPredicate)).wasFound();
      }
   }

   private void updateMaps() {
      this.updateMap(this.extMap, (playerx) -> {
         return playerx == BlackOut.mc.field_1724 ? (Integer)this.selfExt.get() : (Integer)this.extrapolation.get();
      });
      if ((Boolean)this.damageWait.get()) {
         this.updateMap(this.minWaitExtMap, (playerx) -> {
            return (Integer)this.waitStartExt.get();
         });
         this.updateMap(this.maxWaitExtMap, (playerx) -> {
            return (Integer)this.waitEndExt.get();
         });
      }

      this.boxMap.clear();
      Iterator var1 = BlackOut.mc.field_1687.method_18456().iterator();

      class_742 player;
      class_238 valueBox;
      while(var1.hasNext()) {
         player = (class_742)var1.next();
         valueBox = this.expanded(player, (Boolean)this.extrapolateHitbox.get(), (Double)this.hitboxExpand.get());
         this.boxMap.put(player, valueBox);
      }

      this.valueBoxes.clear();
      var1 = BlackOut.mc.field_1687.method_18456().iterator();

      while(var1.hasNext()) {
         player = (class_742)var1.next();
         valueBox = this.expanded(player, false, (Double)this.preferHitboxExpand.get());
         this.valueBoxes.add(valueBox);
      }

   }

   private class_238 expanded(class_742 player, boolean extrapolate, double multi) {
      class_243 velocity = this.clampVec(player.method_19538(), player.field_6014, player.field_6036, player.field_5969);
      class_243 newVelocity = MovementPrediction.adjustMovementForCollisions(player, velocity);
      class_238 box;
      if (extrapolate) {
         box = player.method_5829().method_989(newVelocity.method_10216(), 0.0D, newVelocity.method_10215()).method_1012(0.0D, newVelocity.method_10214(), 0.0D);
      } else {
         box = player.method_5829();
      }

      List<class_265> list = BlackOut.mc.field_1687.method_20743(player, box.method_18804(velocity));
      class_243 vec = class_1297.method_20736(player, velocity.method_18805(multi, 0.0D, multi), box, BlackOut.mc.field_1687, list);
      return box.method_18804(vec);
   }

   private class_243 clampVec(class_243 pos, double x, double y, double z) {
      class_243 vec = pos.method_1023(x, y, z);
      double lengthH = vec.method_37267();
      if (lengthH > 0.3D) {
         double sus = 0.3D / lengthH;
         return vec.method_18805(sus, 1.0D, sus);
      } else {
         return vec;
      }
   }

   private void updateMap(ExtrapolationMap extrapolationMap, EpicInterface<class_1297, Integer> ticks) {
      Map<class_1297, class_238> map = extrapolationMap.getMap();
      map.clear();
      Managers.EXTRAPOLATION.getDataMap().forEach((player, data) -> {
         if (this.targets.contains(player)) {
            class_238 box = data.extrapolate(player, (Integer)ticks.get(player));
            map.put(player, box);
         }
      });
   }

   private void updateTargets() {
      Map<class_1657, Double> map = new HashMap();
      Iterator var2 = BlackOut.mc.field_1687.method_18456().iterator();

      while(true) {
         while(true) {
            class_1657 player;
            double distance;
            do {
               do {
                  do {
                     if (!var2.hasNext()) {
                        this.targets.clear();
                        this.targets.add(BlackOut.mc.field_1724);
                        map.forEach((playerx, d) -> {
                           this.targets.add(playerx);
                        });
                        this.moveDirs.clear();
                        this.targets.forEach((playerx) -> {
                           class_243 movement = playerx.method_19538().method_1023(playerx.field_6014, playerx.field_6036, playerx.field_5969);
                           if (!(movement.method_37268() < 0.01D)) {
                              this.moveDirs.put(playerx, (float)RotationUtils.getYaw(class_243.field_1353, movement, 0.0D));
                           }
                        });
                        this.updateMaps();
                        return;
                     }

                     player = (class_1657)var2.next();
                  } while(player == BlackOut.mc.field_1724);
               } while(player.method_6032() <= 0.0F);

               distance = (double)BlackOut.mc.field_1724.method_5739(player);
            } while(distance > 15.0D);

            if (map.size() < (Integer)this.maxTargets.get()) {
               map.put(player, distance);
            } else {
               Iterator var6 = map.entrySet().iterator();

               while(var6.hasNext()) {
                  Entry<class_1657, Double> entry = (Entry)var6.next();
                  if ((Double)entry.getValue() > distance) {
                     map.remove(entry.getKey());
                     map.put(player, distance);
                     break;
                  }
               }
            }
         }
      }
   }

   private boolean updateCalc() {
      if (this.shouldCalc()) {
         this.shouldCalc = true;
      }

      return this.shouldCalc;
   }

   private void updateRender(double delta, boolean disabled) {
      if ((Boolean)this.render.get()) {
         this.renderBasic(delta, disabled);
         if (!disabled) {
            this.renderExtrapolation();
         }

      }
   }

   private void renderBasic(double delta, boolean disabled) {
      boolean renderActive = this.placePos != null && this.placing && !disabled;
      this.renderProgress = class_3532.method_15350(this.renderProgress + (renderActive ? delta : -delta), 0.0D, (Double)this.fadeTime.get() + (Double)this.renderTime.get());
      double p = Math.min(this.renderProgress, (Double)this.fadeTime.get()) / (Double)this.fadeTime.get();
      switch((AutoCrystal.RenderMode)this.renderMode.get()) {
      case Earthhack:
         this.earthRender.update((pos, time, d) -> {
            float progress = (float)(1.0D - Math.max(time - (Double)this.renderTime.get(), 0.0D) / (Double)this.fadeTime.get());
            this.renderSetting.render(BoxUtils.get(pos), progress, 1.0F);
            this.calcDamage(new class_243((double)pos.method_10263() + 0.5D, (double)(pos.method_10264() + 1), (double)pos.method_10260() + 0.5D), false);
            if ((Boolean)this.renderDamage.get()) {
               Render3DUtils.text(String.format("%.1f", this.enemyDamage), pos.method_46558(), (new Color(255, 255, 255, (int)(progress * 255.0F))).getRGB(), 1.0F);
            }

         });
         break;
      case BlackOut:
         if (renderActive) {
            this.renderPos = this.placePos;
            this.renderTargetVec = new class_243((double)this.placePos.method_10263() + 0.5D, (double)this.placePos.method_10264() - 0.5D, (double)this.placePos.method_10260() + 0.5D);
         }

         if (this.renderProgress <= 0.0D) {
            this.renderVec = this.renderTargetVec;
         } else {
            this.moveRender(delta);
         }

         double progress = 1.0D - Math.pow(1.0D - p, (Double)this.animSizeExponent.get());
         if (p > 0.0D) {
            this.renderSetting.render(this.getBox(this.renderVec, progress / 2.0D), (float)p, (float)p);
            if ((Boolean)this.renderDamage.get()) {
               this.calcDamage(new class_243((double)this.renderPos.method_10263() + 0.5D, (double)this.renderPos.method_10264(), (double)this.renderPos.method_10260() + 0.5D), false);
               Render3DUtils.text(String.format("%.1f", this.enemyDamage), this.renderVec, (new Color(255, 255, 255, (int)(progress * 255.0D))).getRGB(), 1.0F);
            }
         }
         break;
      case Simple:
         if (renderActive) {
            this.renderPos = this.placePos.method_10074();
         }

         if (p > 0.0D) {
            this.renderSetting.render(BoxUtils.get(this.renderPos), (float)p, (float)p);
            this.calcDamage(new class_243((double)this.renderPos.method_10263() + 0.5D, (double)(this.renderPos.method_10264() + 1), (double)this.renderPos.method_10260() + 0.5D), false);
            if ((Boolean)this.renderDamage.get()) {
               Render3DUtils.text(String.format("%.1f", this.enemyDamage), this.renderPos.method_46558(), (new Color(255, 255, 255, (int)(p * 255.0D))).getRGB(), 1.0F);
            }
         }
         break;
      case Confirm:
         if (p > 0.0D) {
            this.renderSetting.render(BoxUtils.get(this.renderPos), (float)p, (float)p);
            this.calcDamage(new class_243((double)this.renderPos.method_10263() + 0.5D, (double)(this.renderPos.method_10264() + 1), (double)this.renderPos.method_10260() + 0.5D), false);
            if ((Boolean)this.renderDamage.get()) {
               Render3DUtils.text(String.format("%.1f", this.enemyDamage), this.renderPos.method_46558(), (new Color(255, 255, 255, (int)(p * 255.0D))).getRGB(), 1.0F);
            }
         }
      }

   }

   private void moveRender(double delta) {
      double dist = this.renderVec.method_1022(this.renderTargetVec);
      double movement = ((Double)this.animMoveSpeed.get() * 5.0D + dist * ((Double)this.animMoveExponent.get() - 1.0D) * 3.0D) * delta;
      double newDist = class_3532.method_15350(dist - movement, 0.0D, dist);
      double f = dist == 0.0D && newDist == 0.0D ? 1.0D : newDist / dist;
      class_243 offset = this.renderTargetVec.method_1020(this.renderVec);
      class_243 m = offset.method_1021(1.0D - f);
      this.renderVec = this.renderVec.method_1019(m);
   }

   private class_238 getBox(class_243 middle, double p) {
      double up = 0.5D;
      double down = 0.5D;
      double sides = 0.5D;
      switch((AutoCrystal.AnimationMode)this.animationMode.get()) {
      case Full:
         up = p;
         down = p;
         sides = p;
         break;
      case Up:
         down = p * 2.0D - 0.5D;
         break;
      case Down:
         up = p * 2.0D - 0.5D;
      }

      return new class_238(middle.method_10216() - sides, middle.method_10214() - down, middle.method_10215() - sides, middle.method_10216() + sides, middle.method_10214() + up, middle.method_10215() + sides);
   }

   private void renderExtrapolation() {
      if ((Boolean)this.renderExt.get()) {
         this.extMap.forEach((player, box) -> {
            if (player != BlackOut.mc.field_1724) {
               this.renderSetting.render(box);
            }

         });
      }

      if ((Boolean)this.renderBoxExt.get()) {
         this.boxMap.forEach((player, box) -> {
            this.renderSetting.render(box);
         });
      }

      if ((Boolean)this.renderSelfExt.get() && this.extMap.contains(BlackOut.mc.field_1724)) {
         this.renderSetting.render(this.extMap.get(BlackOut.mc.field_1724));
      }

   }

   private void update(boolean canPlace) {
      this.placing = false;
      if (this.updateAttacking()) {
         this.end("attacking");
      }

      this.updatePlacing(canPlace);
      if (this.placing) {
         this.calcDamage(new class_243((double)this.placePos.method_10263() + 0.5D, (double)this.placePos.method_10264(), (double)this.placePos.method_10260() + 0.5D), false);
         class_1309 var3 = this.target;
         if (var3 instanceof class_742) {
            class_742 player = (class_742)var3;
            this.targetedPlayer = player;
         } else {
            this.targetedPlayer = null;
         }
      } else {
         this.targetedPlayer = null;
      }

   }

   private boolean canPlace() {
      this.crystalResult = ((AutoCrystal.ACSwitchMode)this.switchMode.get()).find(class_1802.field_8301);
      if (this.switchMode.get() == AutoCrystal.ACSwitchMode.Gapple && this.gappleSwitch(this.placePos != null)) {
         return false;
      } else if (this.placePos == null) {
         return false;
      } else {
         this.crystalHand = OLEPOSSUtils.getHand((stack) -> {
            return stack.method_7909() == class_1802.field_8301;
         });
         if (this.crystalHand == null && !this.crystalResult.wasFound()) {
            return false;
         } else if ((Boolean)this.pauseEatPlace.get() && BlackOut.mc.field_1724.method_6115()) {
            return false;
         } else if ((double)(System.currentTimeMillis() - this.lastSwitch) < (Double)this.placeSwitchPenalty.get() * 1000.0D) {
            return false;
         } else if (this.targetCrystal != null && (Boolean)this.eco.get()) {
            return false;
         } else {
            this.crystalDir = SettingUtils.getPlaceOnDirection(this.placePos.method_10074());
            return this.crystalDir != null;
         }
      }
   }

   private boolean canAttack() {
      if (!(Boolean)this.attack.get()) {
         return false;
      } else {
         this.targetCrystal = null;
         double bestVal = 0.0D;
         Iterator var3 = BlackOut.mc.field_1687.method_18112().iterator();

         while(true) {
            class_1511 crystal;
            double value;
            do {
               class_1297 entity;
               do {
                  do {
                     if (!var3.hasNext()) {
                        if (this.targetCrystal == null) {
                           return false;
                        }

                        if (this.hasWeakness() && !this.antiWeaknessAvailable) {
                           return false;
                        }

                        if ((Boolean)this.pauseEatAttack.get() && BlackOut.mc.field_1724.method_6115()) {
                           return false;
                        }

                        return this.existedCheck(this.targetCrystal);
                     }

                     entity = (class_1297)var3.next();
                  } while(!(entity instanceof class_1511));

                  crystal = (class_1511)entity;
               } while(!this.canAttack(entity, (class_2338)null));

               value = this.getAttackValue(crystal);
            } while(this.targetCrystal != null && value < bestVal);

            this.targetCrystal = crystal;
            bestVal = value;
         }
      }
   }

   private boolean shouldDamageWait(class_1297 entity) {
      double min = this.getHighestDamage(this.minWaitExtMap, entity);
      double max = this.getHighestDamage(this.maxWaitExtMap, entity);
      double difference = max - min;
      if (difference < (Double)this.minDifference.get()) {
         return false;
      } else {
         if (!this.waitTimes.contains((Object)entity.method_5628())) {
            this.waitTimes.add(entity.method_5628(), (Integer)this.maxWait.get() + 50);
         }

         int ticksLeft = this.waitTimes.get((timer) -> {
            return (Integer)timer.value == entity.method_5628();
         }).ticks;
         return ticksLeft > 50;
      }
   }

   private double getHighestDamage(ExtrapolationMap map, class_1297 entity) {
      AtomicReference<Double> highest = new AtomicReference(0.0D);
      Iterator var4 = this.targets.iterator();

      while(var4.hasNext()) {
         class_1657 player = (class_1657)var4.next();
         if (!(this.suicide ^ player == BlackOut.mc.field_1724) && !Managers.FRIENDS.isFriend(player)) {
            highest.set(Math.max((Double)highest.get(), this.crystalDamage(player, map.get(player), entity.method_19538())));
         }
      }

      return (Double)highest.get();
   }

   private boolean hasWeakness() {
      return BlackOut.mc.field_1724.method_6088().containsKey(class_1294.field_5911);
   }

   private void updateFacePlace() {
      this.facePlacing = ((KeyBind)this.holdFacePlace.get()).isPressed();
   }

   private boolean updateAttacking() {
      this.placing = false;
      if (!this.canAttack()) {
         return true;
      } else if (!this.doAttackRotate()) {
         return false;
      } else if (this.shouldAutoMineStop(this.targetCrystal)) {
         return false;
      } else if ((double)(System.currentTimeMillis() - this.lastSwitch) < (Double)this.attackSwitchPenalty.get() * 1000.0D) {
         return false;
      } else {
         switch((AutoCrystal.ActionSpeedMode)this.attackSpeedMode.get()) {
         case Sync:
            if ((Double)this.attackSpeedLimit.get() > 0.0D && (double)(System.currentTimeMillis() - this.lastAttack) <= 1000.0D / (Double)this.attackSpeedLimit.get()) {
               return false;
            }

            if (this.attackTimers.contains((Object)this.targetCrystal.method_5628())) {
               return false;
            }
            break;
         case Normal:
            if ((double)(System.currentTimeMillis() - this.lastAttack) <= 1000.0D / (Double)this.attackSpeed.get()) {
               return false;
            }
         }

         if (this.startAntiWeakness()) {
            return false;
         } else {
            this.attack(this.targetCrystal.method_5628(), this.targetCrystal.method_19538(), false);
            this.endAntiWeakness();
            return true;
         }
      }
   }

   private boolean startAntiWeakness() {
      if (this.hasWeakness() && this.awResult != null) {
         return !((SwitchMode)this.antiWeaknessSwitch.get()).swap(this.awResult.slot());
      } else {
         return false;
      }
   }

   private void endAntiWeakness() {
      if (this.hasWeakness() && this.awResult != null) {
         ((SwitchMode)this.antiWeaknessSwitch.get()).swapBack();
      }
   }

   private boolean doAttackRotate() {
      if (this.shouldRaytraceBypass(this.placePos) && this.raytraceLeft > 0) {
         return true;
      } else if (!SettingUtils.shouldRotate(RotationType.Attacking)) {
         return true;
      } else {
         return SettingUtils.shouldIgnoreRotations(this.targetCrystal) ? this.checkAttackLimit() : this.attackRotate(this.targetCrystal.method_5829(), this.getAttackVec(this.targetCrystal.method_19538()), this.lastWasAttack ? -0.1D : 0.1D, "attacking");
      }
   }

   private void attack(int id, class_243 vec, boolean predict) {
      class_2338 pos = class_2338.method_49638(vec);
      if (!predict) {
         this.attackTimers.add(id, 1.0D / (Double)this.constantAttackSpeed.get());
         this.lastAttack = System.currentTimeMillis();
         Managers.ENTITY.setSemiDead(id);
         this.lastWasAttack = true;
         if (this.attacked.contains((Object)id)) {
            this.attacked.remove((timer) -> {
               return (Integer)timer.value == id;
            });
         }

         this.attacked.add(id, 10);
         int var10002;
         TickTimerList.TickTimer t;
         int[] i;
         if ((Boolean)this.inhibit.get()) {
            t = this.inhibitList.get((timer) -> {
               return ((int[])timer.value)[0] == id;
            });
            if (t != null) {
               var10002 = ((int[])t.value)[1]--;
               i = (int[])t.value;
            } else {
               i = new int[]{id, (Integer)this.inhibitAttacks.get() - 1};
            }

            this.inhibitList.remove(t);
            this.inhibitList.add(i, (Integer)this.inhibitTicks.get());
         }

         if ((Boolean)this.inhibit.get() && (Boolean)this.fullInhibit.get()) {
            t = this.fullInhibitList.get((timer) -> {
               return ((int[])timer.value)[0] == id;
            });
            if (t != null) {
               var10002 = ((int[])t.value)[1]--;
               i = (int[])t.value;
            } else {
               i = new int[]{id, (Integer)this.fullInhibitAttacks.get() - 1};
            }

            this.fullInhibitList.remove(t);
            this.fullInhibitList.add(i, (Integer)this.fullInhibitTicks.get());
         }
      }

      for(int i = 0; i < (predict ? 1 : (Integer)this.attackPackets.get()); ++i) {
         this.sendAttack(id, !predict || (Boolean)this.predictSwing.get());
      }

      if (!predict) {
         this.addPlaceDelay(pos);
         this.existedTicksList.remove((timer) -> {
            return ((class_2338)timer.value).equals(pos);
         });
         this.existedList.remove((timer) -> {
            return ((class_2338)timer.value).equals(pos);
         });
         this.spawning.clear();
         this.end("attacking");
         if ((Boolean)this.debugAttack.get()) {
            this.debug("attacked after", System.currentTimeMillis() - ((IEndCrystalEntity)this.targetCrystal).blackout_Client$getSpawnTime() + "ms");
         }

         if (this.setDead.get() != AutoCrystal.SetDeadMode.Disabled) {
            Managers.ENTITY.setDead(id, this.setDead.get() == AutoCrystal.SetDeadMode.Full);
         }
      } else if ((Boolean)this.debugAttack.get()) {
         this.debug("predicted", id + " (" + this.confirmedId + " " + this.sentId + ")");
      }

   }

   private void sendAttack(int id, boolean swing) {
      class_2824 packet = class_2824.method_34206(BlackOut.mc.field_1724, BlackOut.mc.field_1724.method_5715());
      ((AccessorInteractEntityC2SPacket)packet).setId(id);
      if (swing) {
         SettingUtils.swing(SwingState.Pre, SwingType.Attacking, class_1268.field_5808);
      }

      this.sendPacket(packet);
      if (swing) {
         SettingUtils.swing(SwingState.Post, SwingType.Attacking, class_1268.field_5808);
      }

      if ((Boolean)this.attackSwing.get()) {
         this.clientSwing((SwingHand)this.attackHand.get(), class_1268.field_5808);
      }

   }

   private boolean isBlocked(class_2338 pos) {
      class_238 box = new class_238((double)pos.method_10263(), (double)pos.method_10264(), (double)pos.method_10260(), (double)(pos.method_10263() + 1), (double)(pos.method_10264() + (SettingUtils.cc() ? 1 : 2)), (double)(pos.method_10260() + 1));
      Iterator var3 = this.spawning.getTimers().iterator();

      TimerList.Timer t;
      do {
         if (!var3.hasNext()) {
            return false;
         }

         t = (TimerList.Timer)var3.next();
      } while(!((class_238)t.value).method_994(box));

      return true;
   }

   private boolean almostExistedCheck(class_1297 entity) {
      class_2338 pos = entity.method_24515();
      if (this.existedMode.get() == AutoCrystal.DelayMode.Seconds) {
         if (!this.existedList.contains((Object)pos)) {
            return true;
         } else {
            double time = (double)(this.existedList.getEndTime(pos) - System.currentTimeMillis()) / 1000.0D;
            return time <= 0.35D;
         }
      } else if (!this.existedTicksList.contains((Object)pos)) {
         return true;
      } else {
         int ticks = this.existedTicksList.getTicksLeft(pos);
         return ticks <= 7;
      }
   }

   private boolean existedCheck(class_1297 entity) {
      class_2338 pos = entity.method_24515();
      if (this.existedMode.get() == AutoCrystal.DelayMode.Seconds) {
         return this.existedList.getEndTime(pos) - System.currentTimeMillis() < 250L;
      } else {
         return this.existedTicksList.getTicksLeft(pos) <= 5;
      }
   }

   private boolean placeDelayCheck() {
      if (this.placeDelayMode.get() == AutoCrystal.DelayMode.Seconds) {
         return this.placeDelayList.getEndTime(this.placePos) - System.currentTimeMillis() < 250L;
      } else {
         return this.placeDelayTicksList.getTicksLeft(this.placePos) <= 5;
      }
   }

   private void addExisted(class_2338 pos) {
      if (this.existedMode.get() == AutoCrystal.DelayMode.Seconds) {
         if ((Double)this.existed.get() > 0.0D && !this.existedList.contains((Object)pos)) {
            this.existedList.add(pos, (Double)this.existed.get() + 0.25D);
         }
      } else if ((Integer)this.existedTicks.get() > 0 && !this.existedTicksList.contains((Object)pos)) {
         this.existedTicksList.add(pos, (Integer)this.existedTicks.get() + 5);
      }

   }

   private void addPlaceDelay(class_2338 pos) {
      if (this.placeDelayMode.get() == AutoCrystal.DelayMode.Seconds) {
         if ((Double)this.placeDelay.get() > 0.0D && !this.placeDelayList.contains((Object)pos)) {
            this.placeDelayList.add(pos, (Double)this.placeDelay.get() + 0.25D);
         }
      } else if ((Integer)this.placeDelayTicks.get() > 0 && !this.placeDelayTicksList.contains((Object)pos)) {
         this.placeDelayTicksList.add(pos, (Integer)this.placeDelayTicks.get() + 5);
      }

   }

   private void updatePlacing(boolean canPlace) {
      if (this.canPlace()) {
         this.placing = true;
         if (!SettingUtils.shouldRotate(RotationType.Interact) || this.rotateBlock(this.placePos.method_10074(), this.crystalDir, this.getPlaceVec(this.placePos), RotationType.Interact, "placing") || this.shouldRaytraceBypass(this.placePos) && this.raytraceLeft >= 0 || !(Boolean)this.requireRotation.get()) {
            if (canPlace) {
               if (this.speedCheck()) {
                  if (this.placeDelayCheck()) {
                     if (this.antiPopMode.get() == AutoCrystal.AntiPopMode.Pause) {
                        this.calcDamage(new class_243((double)this.placePos.method_10263() + 0.5D, (double)this.placePos.method_10264(), (double)this.placePos.method_10260() + 0.5D), false);
                        if (this.selfDamage * (Double)this.selfPop.get() > this.selfHealth) {
                           return;
                        }

                        if (this.friendDamage * (Double)this.friendPop.get() > this.friendHealth) {
                           return;
                        }
                     }

                     boolean switched = false;
                     if (this.switchMode.get() != AutoCrystal.ACSwitchMode.Gapple && this.crystalResult.wasFound()) {
                        switched = ((AutoCrystal.ACSwitchMode)this.switchMode.get()).swap(this.crystalResult.slot());
                     }

                     if (this.crystalHand == null && !switched) {
                        this.placing = false;
                     } else {
                        this.place(this.placePos.method_10074(), this.crystalDir, this.crystalHand);
                        if ((Integer)this.predictAttacks.get() > 0) {
                           this.sendPredictions(new class_243((double)this.placePos.method_10263() + 0.5D, (double)this.placePos.method_10264(), (double)this.placePos.method_10260() + 0.5D));
                        }

                        if (switched) {
                           ((AutoCrystal.ACSwitchMode)this.switchMode.get()).swapBack();
                        }

                     }
                  }
               }
            }
         }
      }
   }

   private void sendPredictions(class_243 pos) {
      for(int i = 0; i <= (Integer)this.predictAttacks.get(); ++i) {
         this.attack(this.sentId + (Integer)this.idStart.get() + i * (Integer)this.predictStep.get(), pos, true);
      }

      ++this.sentId;
   }

   private boolean gappleSwitch(boolean canPlace) {
      FindResult gapResult = ((AutoCrystal.ACSwitchMode)this.switchMode.get()).find(OLEPOSSUtils::isGapple);
      class_1792 mainHandItem = BlackOut.mc.field_1724.method_6047().method_7909();
      class_1792 offHandItem = BlackOut.mc.field_1724.method_6079().method_7909();
      boolean holdingGapples = mainHandItem == class_1802.field_8463 || mainHandItem == class_1802.field_8367;
      boolean holdingCrystals = mainHandItem == class_1802.field_8301;
      boolean gapplesInOffhand = offHandItem == class_1802.field_8463 || offHandItem == class_1802.field_8367;
      boolean crystalsInOffhand = offHandItem == class_1802.field_8301;
      if (BlackOut.mc.field_1690.field_1904.method_1434() && gapResult.wasFound()) {
         if (!holdingGapples && holdingCrystals && !gapplesInOffhand) {
            return ((AutoCrystal.ACSwitchMode)this.switchMode.get()).swap(gapResult.slot());
         }
      } else if (this.crystalResult.wasFound() && holdingGapples && !holdingCrystals && canPlace && !crystalsInOffhand) {
         return !((AutoCrystal.ACSwitchMode)this.switchMode.get()).swap(this.crystalResult.slot());
      }

      return !holdingCrystals && !crystalsInOffhand;
   }

   private class_243 getPlaceVec(class_2338 pos) {
      double y = (double)(pos.method_10264() - 1) + (Double)this.placeHeight.get();
      double x = 0.0D;
      double z = 0.0D;
      if ((Boolean)this.moveOffset.get()) {
         x = class_3532.method_15350(BlackOut.mc.field_1724.method_18798().field_1352, -0.5D, 0.5D);
         z = class_3532.method_15350(BlackOut.mc.field_1724.method_18798().field_1350, -0.5D, 0.5D);
      }

      return this.horizontalOffsetVec((double)pos.method_10263() + 0.5D + x, y, (double)pos.method_10260() + 0.5D + z);
   }

   private class_243 getAttackVec(class_243 feet) {
      double y = feet.field_1351 + (Double)this.attackHeight.get();
      double x = 0.0D;
      double z = 0.0D;
      if ((Boolean)this.moveOffset.get()) {
         x = class_3532.method_15350(BlackOut.mc.field_1724.method_18798().field_1352, -0.5D, 0.5D);
         z = class_3532.method_15350(BlackOut.mc.field_1724.method_18798().field_1350, -0.5D, 0.5D);
      }

      return this.horizontalOffsetVec(feet.field_1352 + x, y, feet.field_1350 + z);
   }

   private class_243 horizontalOffsetVec(double x, double y, double z) {
      double ox = class_3532.method_15350(this.movement.field_1352, -0.5D, 0.5D);
      double oz = class_3532.method_15350(this.movement.field_1350, -0.5D, 0.5D);
      return new class_243(x - ox, y, z - oz);
   }

   private boolean speedCheck() {
      switch((AutoCrystal.ActionSpeedMode)this.placeSpeedMode.get()) {
      case Sync:
         if ((Double)this.placeSpeedLimit.get() > 0.0D && (double)(System.currentTimeMillis() - this.lastPlace) < 1000.0D / (Double)this.placeSpeedLimit.get()) {
            return false;
         } else {
            if (!this.shouldSlow() && !this.isBlocked(this.placePos)) {
               return true;
            }

            return (double)(System.currentTimeMillis() - this.lastPlace) > 1000.0D / this.getPlaceSpeed((Double)this.constantPlaceSpeed.get());
         }
      case Normal:
         return (double)(System.currentTimeMillis() - this.lastPlace) > 1000.0D / this.getPlaceSpeed((Double)this.placeSpeed.get());
      default:
         return true;
      }
   }

   private double getPlaceSpeed(double normal) {
      return this.shouldSlow() ? (Double)this.slowSpeed.get() : normal;
   }

   private boolean shouldSlow() {
      if ((Boolean)this.ignoreSlow.get() && this.shouldFacePlace()) {
         return false;
      } else {
         this.calcDamage(new class_243((double)this.placePos.method_10263() + 0.5D, (double)this.placePos.method_10264(), (double)this.placePos.method_10260() + 0.5D), false);
         return this.placePos != null && this.enemyDamage <= (Double)this.slowDamage.get() && this.enemyHealth > (Double)this.slowHealth.get();
      }
   }

   private void place(class_2338 pos, class_2350 dir, class_1268 hand) {
      this.shouldCalc = true;
      this.lastPlace = System.currentTimeMillis();
      this.spawning.add(OLEPOSSUtils.getCrystalBox(pos.method_10084()), 0.5D);
      this.earthRender.add(pos, (Double)this.fadeTime.get() + (Double)this.renderTime.get());
      this.renderProgress = (Double)this.fadeTime.get() + (Double)this.renderTime.get();
      this.lastWasAttack = false;
      if (this.existedCheckMode.get() == AutoCrystal.ExistedMode.Server) {
         this.addExisted(pos.method_10084());
      }

      this.interactBlock(hand, pos.method_46558(), dir, pos);
      if ((Boolean)this.placeSwing.get()) {
         this.clientSwing((SwingHand)this.placeHand.get(), hand);
      }

      if ((Boolean)this.ahd.get() && this.almostColliding(pos.method_10084())) {
         int t = (Integer)this.ahdTries.get();
         if (this.hitBoxDesyncList.containsKey(pos)) {
            t = (Integer)this.hitBoxDesyncList.get(pos) - 1;
         }

         this.hitBoxDesyncList.removeKey(pos);
         this.hitBoxDesyncList.add(pos, t, (double)(Integer)this.ahdTime.get());
      }

      if ((Boolean)this.debugPlace.get()) {
         this.debug("placed after", System.currentTimeMillis() - this.lastAttack + "ms");
      }

      if (this.renderMode.get() == AutoCrystal.RenderMode.Confirm) {
         this.renderPos = pos;
      }

      this.end("placing");
   }

   private void updatePos() {
      this.updateTargets();
      this.shouldCalc = false;
      Suicide suicideModule = Suicide.getInstance();
      this.suicide = suicideModule.enabled && (Boolean)suicideModule.useCA.get();
      class_238 rangeBox = Managers.EXTRAPOLATION.extrapolate(BlackOut.mc.field_1724, (Integer)this.rangeExt.get());
      if (rangeBox == null) {
         this.rangePos = BlackOut.mc.field_1724.method_33571();
      } else {
         this.rangePos = new class_243((rangeBox.field_1323 + rangeBox.field_1320) / 2.0D, rangeBox.field_1322, (rangeBox.field_1321 + rangeBox.field_1324) / 2.0D);
      }

      class_2338 newPos = this.getPlacePos(class_2338.method_49638(this.rangePos.method_1031(0.0D, (double)BlackOut.mc.field_1724.method_18381(BlackOut.mc.field_1724.method_18376()), 0.0D)), (int)Math.ceil(SettingUtils.maxInteractRange()));
      if (!Objects.equals(newPos, this.placePos)) {
         this.lastChange = System.currentTimeMillis();
      }

      this.placePos = newPos;
      this.lastCalc = System.currentTimeMillis();
   }

   private boolean almostColliding(class_2338 pos) {
      class_238 blockBox = BoxUtils.crystalSpawnBox(pos);
      Iterator var3 = this.targets.iterator();

      class_238 box;
      do {
         if (!var3.hasNext()) {
            return false;
         }

         class_1657 player = (class_1657)var3.next();
         box = player.method_5829().method_1014(0.02D);
      } while(!box.method_994(blockBox));

      return true;
   }

   private boolean shouldCalc() {
      if (!(Boolean)this.rotationFriendly.get()) {
         return true;
      } else if (!SettingUtils.shouldRotate(RotationType.Interact)) {
         return true;
      } else if (System.currentTimeMillis() - this.lastCalc > 100L) {
         return true;
      } else if (this.placePos == null) {
         return true;
      } else if (!this.crystalBlock(this.placePos)) {
         return true;
      } else {
         class_2350 dir = SettingUtils.getPlaceOnDirection(this.placePos.method_10074());
         if (dir == null) {
            return true;
         } else if (this.inPlaceRange(this.placePos.method_10074()) && SettingUtils.inAttackRange(OLEPOSSUtils.getCrystalBox(this.placePos))) {
            if (this.intersects(this.placePos)) {
               return true;
            } else {
               this.calcDamage(new class_243((double)this.placePos.method_10263() + 0.5D, (double)this.placePos.method_10264(), (double)this.placePos.method_10260() + 0.5D), false);
               return !this.placeDamageCheck();
            }
         } else {
            return true;
         }
      }
   }

   private class_2338 getPlacePos(class_2338 center, int rad) {
      if (!(Boolean)this.place.get()) {
         return null;
      } else {
         class_2338 bestPos = null;
         boolean bestPop = false;
         this.selfHealth = this.getHealth(BlackOut.mc.field_1724);
         double highest = 0.0D;

         for(int x = -rad; x <= rad; ++x) {
            for(int y = -rad - 1; y <= rad - 1; ++y) {
               for(int z = -rad; z <= rad; ++z) {
                  class_2338 pos = center.method_10069(x, y, z);
                  if (this.crystalBlock(pos)) {
                     class_2350 dir = SettingUtils.getPlaceOnDirection(pos.method_10074());
                     if (dir != null && (!(Boolean)this.ahd.get() || !this.hitBoxDesyncList.contains((p, timer) -> {
                        return p.equals(pos.method_10074()) && (Integer)timer.value <= 0;
                     })) && this.inPlaceRange(pos.method_10074()) && this.inAttackRangePlacing(pos)) {
                        this.calcDamage(new class_243((double)pos.method_10263() + 0.5D, (double)pos.method_10264(), (double)pos.method_10260() + 0.5D), false);
                        if ((!bestPop || this.isPop) && this.placeDamageCheck()) {
                           double value = this.getPlaceValue(pos);
                           if (!(value + ((Boolean)this.raytraceBypass.get() ? (Double)this.raytraceBypassValue.get() : 0.0D) <= highest)) {
                              boolean shouldRaytrace = this.shouldRaytraceBypass(pos);
                              if (shouldRaytrace) {
                                 value += (Double)this.raytraceBypassValue.get();
                              }

                              if (!shouldRaytrace && (!SettingUtils.placeTrace(pos.method_10074()) || !SettingUtils.attackTrace(BoxUtils.crystalSpawnBox(pos)))) {
                                 value += (Double)this.wallValue.get();
                              }

                              if (!(value <= highest) && (!shouldRaytrace || this.raytraceRotation(pos, false) != null) && !this.intersects(pos)) {
                                 highest = value;
                                 bestPos = pos;
                                 bestPop = this.isPop;
                              }
                           }
                        }
                     }
                  }
               }
            }
         }

         return bestPos;
      }
   }

   private boolean inAttackRangePlacing(class_2338 pos) {
      switch((AutoCrystal.RangeExtMode)this.rangeExtMode.get()) {
      case Semi:
         if (this.inAttackRangePlacing(OLEPOSSUtils.getCrystalBox(pos), (class_243)null)) {
            return true;
         }

         if ((Integer)this.rangeExt.get() > 0 && this.inAttackRangePlacing(OLEPOSSUtils.getCrystalBox(pos), this.rangePos)) {
            return true;
         }
         break;
      case Full:
         if (this.inAttackRangePlacing(OLEPOSSUtils.getCrystalBox(pos), this.rangePos)) {
            return true;
         }
      }

      return false;
   }

   private boolean inAttackRangePlacing(class_238 box, class_243 from) {
      if ((Boolean)this.raytraceBypass.get() && SettingUtils.inAttackRangeNoTrace(box, from)) {
         return true;
      } else {
         return !(Boolean)this.raytraceBypass.get() && SettingUtils.inAttackRange(box, from);
      }
   }

   private boolean inPlaceRange(class_2338 pos) {
      return !(Boolean)this.raytraceBypass.get() ? SettingUtils.inInteractRange(pos) : SettingUtils.inInteractRangeNoTrace(pos);
   }

   private boolean intersects(class_2338 pos) {
      class_238 box = new class_238((double)pos.method_10263(), (double)pos.method_10264(), (double)pos.method_10260(), (double)(pos.method_10263() + 1), (double)(pos.method_10264() + (SettingUtils.cc() ? 1 : 2)), (double)(pos.method_10260() + 1));
      if (!(Boolean)this.ignoreItems.get() && EntityUtils.intersectsWithSpawningItem(pos)) {
         return true;
      } else if (EntityUtils.intersects(box, (entity) -> {
         return this.validForIntersects(entity, pos);
      }, (Boolean)this.flexibleHitbox.get() && pos.equals(this.placePos) ? null : this.boxMap)) {
         return true;
      } else if ((Boolean)this.noCollide.get() && (Boolean)this.spawningCollide.get()) {
         Iterator var3 = this.spawning.getTimers().iterator();

         class_238 b;
         do {
            if (!var3.hasNext()) {
               return false;
            }

            TimerList.Timer<class_238> timer = (TimerList.Timer)var3.next();
            b = (class_238)timer.value;
         } while(!b.method_994(box) || pos.equals(class_2338.method_49638(BoxUtils.feet(b))));

         return true;
      } else {
         return false;
      }
   }

   private boolean validForIntersects(class_1297 entity, class_2338 pos) {
      if ((Boolean)this.ignoreItems.get() && entity instanceof class_1542) {
         return false;
      } else if (!(Boolean)this.ignoreExp.get() || !(entity instanceof class_1303) && !(entity instanceof class_1683)) {
         if (entity instanceof class_1511) {
            class_1511 crystal = (class_1511)entity;
            if (this.canAttack(crystal, pos)) {
               return false;
            }
         }

         return !(entity instanceof class_1657) || !entity.method_7325();
      } else {
         return false;
      }
   }

   private boolean canAttack(class_1297 entity, class_2338 placingPos) {
      boolean placing = placingPos != null;
      class_238 box = entity.method_5829();
      if (placing) {
         if (!this.inAttackRangePlacing(box, (class_243)null)) {
            return false;
         }
      } else if (!SettingUtils.inAttackRange(box)) {
         return false;
      }

      if ((Boolean)this.onlyOwn.get() && !((IEndCrystalEntity)entity).blackout_Client$isOwn()) {
         return false;
      } else if (!placing && (Boolean)this.inhibit.get() && this.inhibitList.contains((timer) -> {
         return ((int[])timer.value)[0] == entity.method_5628() && ((int[])timer.value)[1] <= 0;
      })) {
         return false;
      } else if ((Boolean)this.inhibit.get() && (Boolean)this.fullInhibit.get() && (Boolean)this.inhibitCollide.get() && this.fullInhibitList.contains((timer) -> {
         return ((int[])timer.value)[0] == entity.method_5628() && ((int[])timer.value)[1] <= 0;
      })) {
         return false;
      } else if (!placing && !this.almostExistedCheck(entity)) {
         return false;
      } else if (!placing && (Boolean)this.damageWait.get() && this.shouldDamageWait(entity)) {
         return false;
      } else if (placing && this.shouldNoCollide(entity.method_5628()) && !entity.method_24515().equals(placingPos)) {
         return false;
      } else {
         this.calcDamage(BoxUtils.feet(box), true);
         return this.attackDamageCheck(placing || this.placePos != null && box.method_994(BoxUtils.crystalSpawnBox(this.placePos)), placing);
      }
   }

   private boolean shouldNoCollide(int id) {
      if (!(Boolean)this.noCollide.get()) {
         return false;
      } else if (!(Boolean)this.attackCollide.get()) {
         return true;
      } else {
         return !this.attacked.contains((Object)id);
      }
   }

   private boolean placeDamageCheck() {
      if (this.antiPopMode.get() == AutoCrystal.AntiPopMode.Change) {
         if (this.selfDamage * (Double)this.selfPop.get() > this.selfHealth) {
            return false;
         }

         if (this.friendDamage * (Double)this.friendPop.get() > this.friendHealth) {
            return false;
         }
      }

      if (this.enemyDamage * (Double)this.forcePop.get() > this.enemyHealth) {
         return true;
      } else {
         double minDmg = this.getMinDmg(this.minPlace);
         if (this.enemyDamage < minDmg) {
            return false;
         } else {
            if ((Boolean)this.checkSelfPlacing.get()) {
               if (this.selfDamage > (Double)this.maxSelfPlace.get()) {
                  return false;
               }

               if (this.enemyDamage / this.selfDamage < (Double)this.minSelfRatio.get()) {
                  return false;
               }
            }

            if ((Boolean)this.checkFriendPlacing.get()) {
               if (this.friendDamage > (Double)this.maxFriendPlace.get()) {
                  return false;
               } else {
                  return !(this.enemyDamage / this.friendDamage < (Double)this.minFriendRatio.get());
               }
            } else {
               return true;
            }
         }
      }
   }

   private boolean attackDamageCheck(boolean onlySelf, boolean placing) {
      if (placing && this.antiPopMode.get() == AutoCrystal.AntiPopMode.Pause) {
         if (this.selfDamage * (Double)this.selfPop.get() > this.selfHealth) {
            return false;
         }

         if (this.friendDamage > 0.0D && this.friendDamage * (Double)this.friendPop.get() > this.friendHealth) {
            return false;
         }
      }

      if (this.enemyDamage * (Double)this.forcePop.get() > this.enemyHealth) {
         return true;
      } else {
         if (!onlySelf) {
            double minDmg = this.getMinDmg(this.minAttack);
            if ((Boolean)this.checkEnemyAttack.get() && this.enemyDamage < minDmg) {
               return false;
            }
         }

         if ((Boolean)this.checkSelfAttack.get()) {
            if (this.selfDamage > (Double)this.maxSelfAttack.get()) {
               return false;
            }

            if (!onlySelf && (Boolean)this.checkEnemyAttack.get() && this.enemyDamage / this.selfDamage < (Double)this.minSelfAttackRatio.get()) {
               return false;
            }
         }

         if ((Boolean)this.checkFriendAttack.get()) {
            if (this.friendDamage > (Double)this.maxFriendAttack.get()) {
               return false;
            } else {
               return !onlySelf || !(this.friendDamage > 0.0D) || !(this.enemyDamage / this.friendDamage < (Double)this.minFriendAttackRatio.get());
            }
         } else {
            return true;
         }
      }
   }

   private double getMinDmg(Setting<Double> normal) {
      return this.shouldFacePlace() ? (Double)this.facePlaceDamage.get() : (Double)normal.get();
   }

   private boolean shouldFacePlace() {
      if (this.facePlacing) {
         return true;
      } else if (this.enemyHealth <= (Double)this.facePlaceHealth.get()) {
         return true;
      } else if (this.target == null) {
         return false;
      } else {
         Iterator var1 = this.target.method_5661().iterator();

         class_1799 stack;
         do {
            if (!var1.hasNext()) {
               return false;
            }

            stack = (class_1799)var1.next();
         } while(!stack.method_7963() || !(1.0D - (double)stack.method_7919() / (double)stack.method_7936() <= (Double)this.armorFacePlace.get() / 100.0D));

         return true;
      }
   }

   private double getAttackValue(class_1511 crystal) {
      double value = 0.0D;
      class_243 feet = crystal.method_19538();
      if (SettingUtils.shouldRotate(RotationType.Attacking)) {
         value += this.rotationMod(feet);
      }

      if (crystal.method_24515().equals(this.placePos) && (double)(System.currentTimeMillis() - this.lastChange) < (Double)this.antiJitterTime.get() * 1000.0D) {
         value += (Double)this.antiJitter.get();
      }

      class_2338 collidePos = this.autoMineIgnore();
      if (collidePos != null && crystal.method_5829().method_994(BoxUtils.get(collidePos))) {
         value += (Double)this.autoMineCollideValue.get();
      }

      value += this.moveMod(feet);
      value += this.enemyMod();
      value += this.selfMod();
      value += this.friendMod();
      value += this.distMod(SettingUtils.attackRangeTo(crystal.method_5829(), feet));
      if (SettingUtils.shouldIgnoreRotations(crystal)) {
         value -= (Double)this.noRotateValue.get();
      }

      return value;
   }

   private double getPlaceValue(class_2338 pos) {
      double value = 0.0D;
      class_243 middle = pos.method_46558();
      if (SettingUtils.shouldRotate(RotationType.Interact)) {
         value += this.rotationMod(middle);
      }

      if (pos.equals(this.placePos) && (double)(System.currentTimeMillis() - this.lastChange) < (Double)this.antiJitterTime.get() * 1000.0D) {
         value += (Double)this.antiJitter.get();
      }

      class_2338 collidePos = this.autoMineIgnore();
      if (collidePos != null && BoxUtils.get(pos).method_994(BoxUtils.get(collidePos))) {
         value += (Double)this.autoMineCollideValue.get();
      }

      if (!this.valueBoxes.isEmpty()) {
         class_238 boxAt = BoxUtils.crystalSpawnBox(pos);
         Iterator var7 = this.valueBoxes.iterator();

         while(var7.hasNext()) {
            class_238 box = (class_238)var7.next();
            if (box.method_994(boxAt)) {
               value += (Double)this.hitboxValue.get();
               break;
            }
         }
      }

      value += this.moveMod(new class_243((double)pos.method_10263() + 0.5D, (double)(pos.method_10264() + 1), (double)pos.method_10260() + 0.5D));
      value += this.enemyMod();
      value += this.selfMod();
      value += this.friendMod();
      value += this.distMod(SettingUtils.placeRangeTo(pos));
      return value;
   }

   private double enemyMod() {
      return this.enemyDamage * (Double)this.damageValue.get();
   }

   private double selfMod() {
      return this.selfDamage * (Double)this.selfDmgValue.get();
   }

   private double friendMod() {
      return this.friendDamage * (Double)this.friendDmgValue.get();
   }

   private double distMod(double range) {
      return Math.max(range - (Double)this.rangeStartDist.get(), 0.0D) * -(Double)this.rangeValue.get() * this.moveModifier;
   }

   private double rotationMod(class_243 pos) {
      double yawStep = 45.0D;
      double pitchStep = 22.0D;
      int yawSteps = (int)Math.ceil(Math.abs(RotationUtils.yawAngle((double)Managers.ROTATION.prevYaw, RotationUtils.getYaw(pos)) / yawStep));
      int pitchSteps = (int)Math.ceil(Math.abs(RotationUtils.pitchAngle((double)Managers.ROTATION.prevPitch, RotationUtils.getPitch(pos)) / pitchStep));
      int steps = Math.max(yawSteps, pitchSteps);
      return (double)(3 - Math.min(steps, 3)) * (Double)this.rotationValue.get() / 3.0D;
   }

   private double moveMod(class_243 vec) {
      double val = 0.0D;

      double v;
      for(Iterator var4 = this.moveDirs.entrySet().iterator(); var4.hasNext(); val += v) {
         Entry<class_1657, Float> entry = (Entry)var4.next();
         class_1657 player = (class_1657)entry.getKey();
         double steps = Math.abs(RotationUtils.yawAngle((double)(Float)entry.getValue(), RotationUtils.getYaw(player.method_19538(), vec, 0.0D))) / 10.0D;
         double valueMulti;
         if (!this.suicide && player == BlackOut.mc.field_1724) {
            valueMulti = (Double)this.selfMoveValue.get();
         } else if (Managers.FRIENDS.isFriend(player)) {
            valueMulti = (Double)this.friendMoveValue.get();
         } else {
            valueMulti = (Double)this.moveValue.get();
         }

         v = Math.max(3.0D - steps, 0.0D);
         v *= valueMulti;
         v *= 1.0D - class_3532.method_15350(Math.abs(vec.method_10214() - player.method_23318()) - 1.0D, 0.0D, 1.0D);
      }

      return val;
   }

   public boolean shouldAutoMineStop(class_1297 entity) {
      AutoMine autoMine = AutoMine.getInstance();
      if (autoMine.enabled && autoMine.started && (Boolean)this.autoMineAttack.get() && !(autoMine.getCurrentProgress() < (Double)this.autoMineAttackProgress.get()) && autoMine.minePos != null && OLEPOSSUtils.solid2(autoMine.minePos)) {
         switch(autoMine.mineType) {
         case Cev:
         case TrapCev:
         case SurroundCev:
            return class_2338.method_49638(entity.method_19538().method_1031(0.0D, -0.3D, 0.0D)).equals(autoMine.minePos);
         case SurroundMiner:
         case AutoCity:
         case AntiBurrow:
         case Manual:
            return BoxUtils.get(autoMine.minePos).method_994(entity.method_5829());
         default:
            return false;
         }
      } else {
         return false;
      }
   }

   public void calcDamage(class_243 vec, boolean attacking) {
      this.selfDamage = this.crystalDamage(BlackOut.mc.field_1724, attacking ? BlackOut.mc.field_1724.method_5829() : this.extMap.get(BlackOut.mc.field_1724), vec);
      this.enemyDamage = 0.0D;
      this.friendDamage = 0.0D;
      this.isPop = false;
      this.enemyHealth = 20.0D;
      this.friendHealth = 20.0D;
      this.target = null;
      if (this.suicide) {
         this.enemyDamage = this.selfDamage;
         this.selfDamage = 0.0D;
         this.target = BlackOut.mc.field_1724;
      } else {
         this.extMap.forEach((entity, box) -> {
            if (entity instanceof class_1657) {
               class_1657 player = (class_1657)entity;
               if (!(player.method_6032() <= 0.0F) && player != BlackOut.mc.field_1724) {
                  double dmg = this.crystalDamage(player, box, vec);
                  double health = this.getHealth(player);
                  boolean wouldPop = dmg * (Double)this.forcePop.get() > health;
                  if (Managers.FRIENDS.isFriend(player)) {
                     if (dmg > this.friendDamage) {
                        this.friendDamage = dmg;
                        this.friendHealth = health;
                     }

                  } else if (!this.isPop || wouldPop || !((Double)this.forcePop.get() > 0.0D)) {
                     if (wouldPop && !this.isPop && (Double)this.forcePop.get() > 0.0D || dmg > this.enemyDamage) {
                        this.enemyDamage = dmg;
                        this.enemyHealth = health;
                        this.target = player;
                        this.isPop = wouldPop;
                     }

                  }
               }
            }
         });
      }
   }

   private double crystalDamage(class_1657 player, class_238 box, class_243 vec) {
      return DamageUtils.crystalDamage(player, box, vec, this.autoMineIgnore());
   }

   private class_2338 autoMineIgnore() {
      AutoMine autoMine = AutoMine.getInstance();
      return autoMine.enabled && autoMine.started && autoMine.getCurrentProgress() >= (Double)this.prePlaceProgress.get() ? autoMine.minePos : null;
   }

   private double getHealth(class_1657 player) {
      return (double)(player.method_6032() + player.method_6067());
   }

   private boolean crystalBlock(class_2338 pos) {
      class_2248 block = this.getState(pos.method_10074()).method_26204();
      if (block != class_2246.field_10540 && block != class_2246.field_9987) {
         return false;
      } else if (!this.air(pos)) {
         return false;
      } else {
         return !SettingUtils.oldCrystals() || this.air(pos.method_10084());
      }
   }

   private boolean air(class_2338 pos) {
      return this.getState(pos).method_26204() instanceof class_2189;
   }

   private class_2680 getState(class_2338 pos) {
      return Managers.BLOCK.blockState(pos);
   }

   private boolean shouldRaytraceBypass(class_2338 pos) {
      if (!(Boolean)this.raytraceBypass.get()) {
         return false;
      } else if (pos == null) {
         return false;
      } else {
         return !SettingUtils.interactTrace(pos.method_10074()) && SettingUtils.placeRangeTo(pos) < SettingUtils.getAttackWallsRange();
      }
   }

   private Rotation raytraceRotation(class_2338 pos, boolean getBest) {
      class_2350 placeDir = SettingUtils.getPlaceOnDirection(pos.method_10074());
      if (placeDir == null) {
         return null;
      } else {
         class_243 vec = SettingUtils.getRotationVec(pos.method_10074(), placeDir, this.getPlaceVec(pos), RotationType.Interact);
         Rotation rotation = SettingUtils.getRotation(vec);
         double minDist = BlackOut.mc.field_1724.method_33571().method_1025(vec);
         ((IRaycastContext)DamageUtils.raycastContext).blackout_Client$set(class_3960.field_17558, class_242.field_1348, BlackOut.mc.field_1724);
         float bestDist = 69420.0F;
         float bestPitch = -420.0F;
         boolean prevWas = false;

         for(float p = 90.0F; p >= -90.0F; p -= 10.0F) {
            float dist = Math.abs(rotation.pitch() - p);
            if (!(dist < (float)(Integer)this.raytraceAngle.get()) && !(dist > bestDist)) {
               class_243 pitchPos = RotationUtils.rotationVec((double)rotation.yaw(), (double)p, BlackOut.mc.field_1724.method_33571(), 10.0D);
               ((IRaycastContext)DamageUtils.raycastContext).blackout_Client$set(BlackOut.mc.field_1724.method_33571(), pitchPos);
               class_3965 result = DamageUtils.raycast(DamageUtils.raycastContext, false);
               boolean isHigher = BlackOut.mc.field_1724.method_33571().method_1025(result.method_17784()) > minDist;
               if (isHigher && prevWas) {
                  if (!getBest) {
                     return new Rotation(rotation.yaw(), p);
                  }

                  bestDist = dist;
                  bestPitch = p;
               }

               prevWas = isHigher;
            }
         }

         if (bestPitch == -420.0F) {
            return null;
         } else {
            return new Rotation(rotation.yaw(), bestPitch);
         }
      }
   }

   public static enum ActionSpeedMode {
      Sync,
      Normal;

      // $FF: synthetic method
      private static AutoCrystal.ActionSpeedMode[] $values() {
         return new AutoCrystal.ActionSpeedMode[]{Sync, Normal};
      }
   }

   public static enum DelayMode {
      Seconds,
      Ticks;

      // $FF: synthetic method
      private static AutoCrystal.DelayMode[] $values() {
         return new AutoCrystal.DelayMode[]{Seconds, Ticks};
      }
   }

   public static enum ExistedMode {
      Client,
      Server;

      // $FF: synthetic method
      private static AutoCrystal.ExistedMode[] $values() {
         return new AutoCrystal.ExistedMode[]{Client, Server};
      }
   }

   public static enum SetDeadMode {
      Disabled,
      Render,
      Full;

      // $FF: synthetic method
      private static AutoCrystal.SetDeadMode[] $values() {
         return new AutoCrystal.SetDeadMode[]{Disabled, Render, Full};
      }
   }

   public static enum ACSwitchMode {
      Disabled(false, false),
      Normal(true, false),
      Gapple(true, false),
      Silent(true, false),
      InvSwitch(true, true),
      PickSilent(true, true);

      public final boolean hotbar;
      public final boolean inventory;

      private ACSwitchMode(boolean h, boolean i) {
         this.hotbar = h;
         this.inventory = i;
      }

      public void swapBack() {
         switch(this) {
         case Silent:
            InvUtils.swapBack();
            break;
         case InvSwitch:
            InvUtils.invSwapBack();
            break;
         case PickSilent:
            InvUtils.pickSwapBack();
         }

      }

      public boolean swap(int slot) {
         switch(this) {
         case Silent:
         case Normal:
         case Gapple:
            InvUtils.swap(slot);
            return true;
         case InvSwitch:
            InvUtils.invSwap(slot);
            return true;
         case PickSilent:
            InvUtils.pickSwap(slot);
            return true;
         default:
            return false;
         }
      }

      public FindResult find(Predicate<class_1799> predicate) {
         return InvUtils.find(this.hotbar, this.inventory, predicate);
      }

      public FindResult find(class_1792 item) {
         return InvUtils.find(this.hotbar, this.inventory, item);
      }

      // $FF: synthetic method
      private static AutoCrystal.ACSwitchMode[] $values() {
         return new AutoCrystal.ACSwitchMode[]{Disabled, Normal, Gapple, Silent, InvSwitch, PickSilent};
      }
   }

   public static enum AntiPopMode {
      Pause,
      Change;

      // $FF: synthetic method
      private static AutoCrystal.AntiPopMode[] $values() {
         return new AutoCrystal.AntiPopMode[]{Pause, Change};
      }
   }

   public static enum RangeExtMode {
      Semi,
      Full;

      // $FF: synthetic method
      private static AutoCrystal.RangeExtMode[] $values() {
         return new AutoCrystal.RangeExtMode[]{Semi, Full};
      }
   }

   public static enum RenderMode {
      Simple,
      Confirm,
      BlackOut,
      Earthhack;

      // $FF: synthetic method
      private static AutoCrystal.RenderMode[] $values() {
         return new AutoCrystal.RenderMode[]{Simple, Confirm, BlackOut, Earthhack};
      }
   }

   public static enum AnimationMode {
      Full,
      Up,
      Down,
      None;

      // $FF: synthetic method
      private static AutoCrystal.AnimationMode[] $values() {
         return new AutoCrystal.AnimationMode[]{Full, Up, Down, None};
      }
   }

   public static enum AsyncMode {
      Disabled,
      Basic,
      Dumb,
      Heavy;

      // $FF: synthetic method
      private static AutoCrystal.AsyncMode[] $values() {
         return new AutoCrystal.AsyncMode[]{Disabled, Basic, Dumb, Heavy};
      }
   }
}
