package bodevelopment.client.blackout.module.modules.combat.offensive;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.enums.RenderShape;
import bodevelopment.client.blackout.enums.RotationType;
import bodevelopment.client.blackout.enums.SwingHand;
import bodevelopment.client.blackout.enums.SwitchMode;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.BlockStateEvent;
import bodevelopment.client.blackout.event.events.RenderEvent;
import bodevelopment.client.blackout.event.events.TickEvent;
import bodevelopment.client.blackout.interfaces.functional.DoublePredicate;
import bodevelopment.client.blackout.keys.KeyBind;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.modules.combat.misc.Suicide;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.randomstuff.BlackOutColor;
import bodevelopment.client.blackout.randomstuff.ExtrapolationMap;
import bodevelopment.client.blackout.randomstuff.FindResult;
import bodevelopment.client.blackout.randomstuff.Pair;
import bodevelopment.client.blackout.randomstuff.PlaceData;
import bodevelopment.client.blackout.randomstuff.timers.RenderList;
import bodevelopment.client.blackout.randomstuff.timers.TimerList;
import bodevelopment.client.blackout.util.BoxUtils;
import bodevelopment.client.blackout.util.DamageUtils;
import bodevelopment.client.blackout.util.EntityUtils;
import bodevelopment.client.blackout.util.OLEPOSSUtils;
import bodevelopment.client.blackout.util.RotationUtils;
import bodevelopment.client.blackout.util.SettingUtils;
import bodevelopment.client.blackout.util.render.Render3DUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import net.minecraft.class_1268;
import net.minecraft.class_1309;
import net.minecraft.class_1542;
import net.minecraft.class_1657;
import net.minecraft.class_1748;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_2244;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_2358;
import net.minecraft.class_238;
import net.minecraft.class_2383;
import net.minecraft.class_243;
import net.minecraft.class_2680;
import net.minecraft.class_2742;
import net.minecraft.class_742;
import net.minecraft.class_2350.class_2353;
import net.minecraft.class_2828.class_2831;

public class BedAura extends Module {
   private static BedAura INSTANCE;
   private final SettingGroup sgPlace = this.addGroup("Place");
   private final SettingGroup sgExplode = this.addGroup("Explode");
   private final SettingGroup sgSlow = this.addGroup("Slow");
   private final SettingGroup sgFacePlace = this.addGroup("Face Place");
   private final SettingGroup sgDamage = this.addGroup("Damage");
   private final SettingGroup sgExtrapolation = this.addGroup("Extrapolation");
   private final SettingGroup sgRender = this.addGroup("Render");
   private final SettingGroup sgBoxRender = this.addGroup("Box Render");
   private final SettingGroup sgExplodeRender = this.addGroup("Explode Render");
   private final SettingGroup sgCalculation = this.addGroup("Calculations");
   private final Setting<Boolean> place;
   private final Setting<Boolean> pauseEatPlace;
   private final Setting<Double> placeSpeed;
   private final Setting<Boolean> noHitbox;
   private final Setting<Boolean> floor;
   private final Setting<Boolean> fireBlocking;
   private final Setting<Boolean> serverDir;
   private final Setting<Boolean> rotate;
   private final Setting<BedAura.RotationMode> rotationMode;
   private final Setting<Boolean> pauseOffGround;
   private final Setting<SwitchMode> switchMode;
   private final Setting<Boolean> pauseEatExplode;
   private final Setting<AutoCrystal.DelayMode> existedMode;
   private final Setting<Double> existed;
   private final Setting<Integer> existedTicks;
   private final Setting<AutoCrystal.ActionSpeedMode> explodeSpeedMode;
   private final Setting<Double> explodeSpeedLimit;
   private final Setting<Double> constantExplodeSpeed;
   private final Setting<Double> explodeSpeed;
   private final Setting<Double> rotationHeight;
   private final Setting<Double> slowDamage;
   private final Setting<Double> slowSpeed;
   private final Setting<Double> slowHealth;
   private final Setting<KeyBind> holdFacePlace;
   private final Setting<Double> facePlaceHealth;
   private final Setting<Double> armorFacePlace;
   private final Setting<Double> facePlaceDamage;
   private final Setting<Boolean> ignoreSlow;
   private final Setting<Double> minPlace;
   private final Setting<Boolean> checkSelfPlacing;
   private final Setting<Double> maxSelfPlace;
   private final Setting<Double> minSelfRatio;
   private final Setting<Boolean> checkFriendPlacing;
   private final Setting<Double> maxFriendPlace;
   private final Setting<Double> minFriendRatio;
   private final Setting<Boolean> checkEnemyExplode;
   private final Setting<Double> minExplode;
   private final Setting<Boolean> checkSelfExplode;
   private final Setting<Double> maxSelfExplode;
   private final Setting<Double> minSelfExplodeRatio;
   private final Setting<Boolean> checkFriendExplode;
   private final Setting<Double> maxFriendExplode;
   private final Setting<Double> minFriendExplodeRatio;
   private final Setting<Double> forcePop;
   private final Setting<Double> selfPop;
   private final Setting<Double> friendPop;
   private final Setting<Integer> extrapolation;
   private final Setting<Integer> selfExt;
   private final Setting<Integer> hitboxExt;
   private final Setting<Boolean> damageWait;
   private final Setting<Integer> waitExt;
   private final Setting<Boolean> placeSwing;
   private final Setting<SwingHand> placeHand;
   private final Setting<Boolean> explodeSwing;
   private final Setting<SwingHand> explodeHand;
   private final Setting<Boolean> renderBox;
   private final Setting<Double> renderTime;
   private final Setting<Double> fadeTime;
   private final Setting<Double> animMoveSpeed;
   private final Setting<Double> animMoveExponent;
   private final Setting<RenderShape> renderShape;
   private final Setting<BlackOutColor> lineColor;
   private final Setting<BlackOutColor> sideColor;
   private final Setting<Boolean> separateBox;
   private final Setting<RenderShape> renderHeadShape;
   private final Setting<BlackOutColor> headLineColor;
   private final Setting<BlackOutColor> headSideColor;
   private final Setting<Boolean> renderDamage;
   private final Setting<Boolean> renderExplode;
   private final Setting<Double> explodeRenderTime;
   private final Setting<Double> explodeFadeTime;
   private final Setting<RenderShape> explodeRenderShape;
   private final Setting<BlackOutColor> explodeLineColor;
   private final Setting<BlackOutColor> explodeSideColor;
   private final Setting<Boolean> separateExplode;
   private final Setting<RenderShape> explodeHeadShape;
   private final Setting<BlackOutColor> explodeHeadLineColor;
   private final Setting<BlackOutColor> explodeHeadSideColor;
   private final Setting<Double> damageValue;
   private final Setting<Double> selfDmgValue;
   private final Setting<Double> friendDmgValue;
   private final Setting<Double> rotationValue;
   private final Setting<Integer> maxTargets;
   private final Setting<Double> enemyDistance;
   private final ExtrapolationMap extMap;
   private final ExtrapolationMap hitboxMap;
   private final List<class_1657> targets;
   private final RenderList<Pair<class_2338, class_2350>> renderList;
   private class_2338 placePos;
   private class_2350 placeDir;
   private class_2338 explodePos;
   private double selfHealth;
   private double enemyHealth;
   private double friendHealth;
   private double selfDamage;
   private double enemyDamage;
   private double friendDamage;
   private class_1309 target;
   private class_2338 targetCalcBest;
   private double targetCalcValue;
   private int targetCalcR;
   private int targetProgress;
   private class_2338 calcBest;
   private class_2350 calcDir;
   private double calcValue;
   private int calcR;
   private class_2338 calcMiddle;
   private int progress;
   private long lastExplode;
   private long lastPlace;
   private final TimerList<class_2338> explodeTimers;
   private final TimerList<class_2338> ignoreState;
   private boolean suicide;
   private boolean facePlacing;
   private class_2338 renderPos;
   private class_2350 renderDir;
   private double renderProgress;
   private final Map<class_2338, Double[]> damageCache;
   public static class_742 targetedPlayer = null;

   public BedAura() {
      super("Bed Aura", "Places and blows up beds.", SubCategory.OFFENSIVE, true);
      this.place = this.sgPlace.b("Place", true, "Places crystals.");
      this.pauseEatPlace = this.sgPlace.b("Pause Eat Place", false, "Pauses placing while eating.");
      this.placeSpeed = this.sgPlace.d("Place Speed", 20.0D, 0.0D, 20.0D, 0.1D, ".");
      this.noHitbox = this.sgPlace.b("No Hitbox", true, "Doesn't care about hitboxes (for 5b5t).");
      this.floor = this.sgPlace.b("Floor", false, "Beds can only be placed on top of blocks.");
      this.fireBlocking = this.sgPlace.b("Fire Blocking", false, "Doesn't allow placing inside fire.");
      this.serverDir = this.sgPlace.b("Server Direction", true, ".");
      SettingGroup var10001 = this.sgPlace;
      Setting var10005 = this.serverDir;
      Objects.requireNonNull(var10005);
      this.rotate = var10001.b("Rotate", true, ".", var10005::get);
      this.rotationMode = this.sgPlace.e("Rotation Mode", BedAura.RotationMode.Instant, ".", () -> {
         return !(Boolean)this.serverDir.get() || (Boolean)this.rotate.get();
      });
      this.pauseOffGround = this.sgPlace.b("Pause Off Ground", true, ".");
      this.switchMode = this.sgPlace.e("Switch", SwitchMode.Silent, "Mode for switching to beds in main hand.");
      this.pauseEatExplode = this.sgExplode.b("Pause Eat Explode", false, "Pauses attacking while eating.");
      this.existedMode = this.sgExplode.e("Existed Mode", AutoCrystal.DelayMode.Ticks, "Should crystal existed times be counted in seconds or ticks.");
      this.existed = this.sgExplode.d("Explode Delay", 0.0D, 0.0D, 1.0D, 0.01D, "How many seconds should the crystal exist before attacking.", () -> {
         return this.existedMode.get() == AutoCrystal.DelayMode.Seconds;
      });
      this.existedTicks = this.sgExplode.i("Explode Delay Ticks", 0, 0, 20, 1, "How many ticks should the crystal exist before attacking.", () -> {
         return this.existedMode.get() == AutoCrystal.DelayMode.Ticks;
      });
      this.explodeSpeedMode = this.sgExplode.e("Explode Speed Mode", AutoCrystal.ActionSpeedMode.Sync, ".");
      this.explodeSpeedLimit = this.sgExplode.d("Explode Speed Limit", 0.0D, 0.0D, 20.0D, 0.1D, "Maximum amount of attacks every second. 0 = no limit", () -> {
         return this.explodeSpeedMode.get() == AutoCrystal.ActionSpeedMode.Sync;
      });
      this.constantExplodeSpeed = this.sgExplode.d("Constant Explode Speed", 10.0D, 0.0D, 20.0D, 0.1D, ".", () -> {
         return this.explodeSpeedMode.get() == AutoCrystal.ActionSpeedMode.Sync;
      });
      this.explodeSpeed = this.sgExplode.d("Explode Speed", 20.0D, 0.0D, 20.0D, 0.1D, ".", () -> {
         return this.explodeSpeedMode.get() == AutoCrystal.ActionSpeedMode.Normal;
      });
      this.rotationHeight = this.sgExplode.d("Rotation Height", 0.25D, 0.0D, 0.5D, 0.01D, "Height for rotations.");
      this.slowDamage = this.sgSlow.d("Slow Damage", 3.0D, 0.0D, 20.0D, 0.1D, "Switches to slow speed when the target would take under this amount of damage.");
      this.slowSpeed = this.sgSlow.d("Slow Speed", 2.0D, 0.0D, 20.0D, 0.1D, "How many times should the module place per second when damage is under slow damage.");
      this.slowHealth = this.sgSlow.d("Slow Health", 10.0D, 0.0D, 20.0D, 0.5D, "Only slow places if enemy has over x health.");
      this.holdFacePlace = this.sgFacePlace.k("Hold Face Place", "Faceplaces when holding this key.");
      this.facePlaceHealth = this.sgFacePlace.d("Face Place Health", 0.0D, 0.0D, 10.0D, 0.1D, "Automatically face places if enemy has under this much health.");
      this.armorFacePlace = this.sgFacePlace.d("Armor Face Place", 10.0D, 0.0D, 100.0D, 1.0D, "Face places if enemy's any armor piece is under this durability.");
      this.facePlaceDamage = this.sgFacePlace.d("Face Place Damage", 0.0D, 0.0D, 10.0D, 0.1D, "Sets min place and min attack to this.");
      this.ignoreSlow = this.sgFacePlace.b("Ignore Slow", true, "Doesn't slow place when faceplacing.");
      this.minPlace = this.sgDamage.d("Min Place", 5.0D, 0.0D, 20.0D, 0.1D, "Minimum damage to place.");
      this.checkSelfPlacing = this.sgDamage.b("Self Placing", true, "Checks self damage when placing.");
      var10001 = this.sgDamage;
      Setting var10008 = this.checkSelfPlacing;
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
      this.checkEnemyExplode = this.sgDamage.b("Enemy Explode", true, "Checks enemy damage when attacking.");
      var10001 = this.sgDamage;
      var10008 = this.checkEnemyExplode;
      Objects.requireNonNull(var10008);
      this.minExplode = var10001.d("Min Explode", 5.0D, 0.0D, 20.0D, 0.1D, "Minimum damage to attack.", var10008::get);
      this.checkSelfExplode = this.sgDamage.b("Self Explode", true, "Checks self damage when attacking.");
      var10001 = this.sgDamage;
      var10008 = this.checkSelfExplode;
      Objects.requireNonNull(var10008);
      this.maxSelfExplode = var10001.d("Max Explode", 10.0D, 0.0D, 20.0D, 0.1D, "Max self damage for attacking.", var10008::get);
      var10001 = this.sgDamage;
      var10008 = this.checkSelfExplode;
      Objects.requireNonNull(var10008);
      this.minSelfExplodeRatio = var10001.d("Min Explode Ratio", 2.0D, 0.0D, 20.0D, 0.1D, "Min self damage ratio for attacking (enemy / self).", var10008::get);
      this.checkFriendExplode = this.sgDamage.b("Friend Explode", true, "Checks friend damage when attacking.");
      var10001 = this.sgDamage;
      var10008 = this.checkFriendExplode;
      Objects.requireNonNull(var10008);
      this.maxFriendExplode = var10001.d("Max Friend Explode", 12.0D, 0.0D, 20.0D, 0.1D, "Max friend damage for attacking.", var10008::get);
      var10001 = this.sgDamage;
      var10008 = this.checkFriendExplode;
      Objects.requireNonNull(var10008);
      this.minFriendExplodeRatio = var10001.d("Min Friend Explode Ratio", 1.0D, 0.0D, 20.0D, 0.1D, "Min friend damage ratio for attacking (enemy / friend).", var10008::get);
      this.forcePop = this.sgDamage.d("Force Pop", 0.0D, 0.0D, 5.0D, 0.25D, "Ignores damage checks if any enemy will be popped in x hits.");
      this.selfPop = this.sgDamage.d("Anti Pop", 1.0D, 0.0D, 5.0D, 0.25D, "Ignores damage checks if any enemy will be popped in x hits.");
      this.friendPop = this.sgDamage.d("Anti Friend Pop", 0.0D, 0.0D, 5.0D, 0.25D, "Ignores damage checks if any enemy will be popped in x hits.");
      this.extrapolation = this.sgExtrapolation.i("Extrapolation", 0, 0, 20, 1, "How many ticks of movement should be predicted for enemy damage checks.");
      this.selfExt = this.sgExtrapolation.i("Self Extrapolation", 0, 0, 20, 1, "How many ticks of movement should be predicted for self damage checks.");
      this.hitboxExt = this.sgExtrapolation.i("Hitbox Extrapolation", 0, 0, 20, 1, "How many ticks of movement should be predicted for hitboxes in placing checks.");
      this.damageWait = this.sgExtrapolation.b("Damage Wait", false, ".");
      var10001 = this.sgExtrapolation;
      var10008 = this.damageWait;
      Objects.requireNonNull(var10008);
      this.waitExt = var10001.i("Wait Extra Extrapolation", 0, 0, 20, 1, ".", var10008::get);
      this.placeSwing = this.sgRender.b("Place Swing", false, "Renders swing animation when placing a crystal.");
      this.placeHand = this.sgRender.e("Place Hand", SwingHand.RealHand, "Which hand should be swung.");
      this.explodeSwing = this.sgRender.b("Explode Swing", false, "Renders swing animation when attacking a crystal.");
      this.explodeHand = this.sgRender.e("Explode Hand", SwingHand.RealHand, "Which hand should be swung.");
      this.renderBox = this.sgBoxRender.b("Render Box", true, "Renders box on placement.");
      var10001 = this.sgBoxRender;
      var10008 = this.renderBox;
      Objects.requireNonNull(var10008);
      this.renderTime = var10001.d("Box Render Time", 0.3D, 0.0D, 10.0D, 0.1D, "How long the box should remain in full alpha value.", var10008::get);
      var10001 = this.sgBoxRender;
      var10008 = this.renderBox;
      Objects.requireNonNull(var10008);
      this.fadeTime = var10001.d("Box Fade Time", 1.0D, 0.0D, 10.0D, 0.1D, "How long the fading should take.", var10008::get);
      var10001 = this.sgBoxRender;
      var10008 = this.renderBox;
      Objects.requireNonNull(var10008);
      this.animMoveSpeed = var10001.d("Box Move Speed", 2.0D, 0.0D, 10.0D, 0.1D, "How fast should blackout mode box move.", var10008::get);
      var10001 = this.sgBoxRender;
      var10008 = this.renderBox;
      Objects.requireNonNull(var10008);
      this.animMoveExponent = var10001.d("Box Move Exponent", 3.0D, 0.0D, 10.0D, 0.1D, "Moves faster when longer away from the target.", var10008::get);
      var10001 = this.sgBoxRender;
      RenderShape var10003 = RenderShape.Full;
      var10005 = this.renderBox;
      Objects.requireNonNull(var10005);
      this.renderShape = var10001.e("Box Render Shape", var10003, "Which parts of render should be rendered.", var10005::get);
      var10001 = this.sgBoxRender;
      BlackOutColor var1 = new BlackOutColor(255, 0, 0, 255);
      var10005 = this.renderBox;
      Objects.requireNonNull(var10005);
      this.lineColor = var10001.c("Box Line Color", var1, "Line color of rendered boxes.", var10005::get);
      var10001 = this.sgBoxRender;
      var1 = new BlackOutColor(255, 0, 0, 50);
      var10005 = this.renderBox;
      Objects.requireNonNull(var10005);
      this.sideColor = var10001.c("Box Side Color", var1, "Side color of rendered boxes.", var10005::get);
      var10001 = this.sgBoxRender;
      var10005 = this.renderBox;
      Objects.requireNonNull(var10005);
      this.separateBox = var10001.b("Separate Box", true, ".", var10005::get);
      this.renderHeadShape = this.sgBoxRender.e("Box Head Shape", RenderShape.Full, "Which parts of render should be rendered.", () -> {
         return (Boolean)this.renderBox.get() && (Boolean)this.separateBox.get();
      });
      this.headLineColor = this.sgBoxRender.c("Box Head Line Color", new BlackOutColor(255, 255, 255, 255), "Line color of rendered boxes.", () -> {
         return (Boolean)this.renderBox.get() && (Boolean)this.separateBox.get();
      });
      this.headSideColor = this.sgBoxRender.c("Box Head Side Color", new BlackOutColor(255, 255, 255, 50), "Side color of rendered boxes.", () -> {
         return (Boolean)this.renderBox.get() && (Boolean)this.separateBox.get();
      });
      var10001 = this.sgBoxRender;
      var10005 = this.renderBox;
      Objects.requireNonNull(var10005);
      this.renderDamage = var10001.b("Render Damage", true, ".", var10005::get);
      this.renderExplode = this.sgExplodeRender.b("Render Explode", true, "Renders box on placement.");
      var10001 = this.sgExplodeRender;
      var10008 = this.renderExplode;
      Objects.requireNonNull(var10008);
      this.explodeRenderTime = var10001.d("Explode Render Time", 0.3D, 0.0D, 10.0D, 0.1D, "How long the box should remain in full alpha value.", var10008::get);
      var10001 = this.sgExplodeRender;
      var10008 = this.renderExplode;
      Objects.requireNonNull(var10008);
      this.explodeFadeTime = var10001.d("Explode Fade Time", 1.0D, 0.0D, 10.0D, 0.1D, "How long the fading should take.", var10008::get);
      var10001 = this.sgExplodeRender;
      var10003 = RenderShape.Full;
      var10005 = this.renderExplode;
      Objects.requireNonNull(var10005);
      this.explodeRenderShape = var10001.e("Explode Render Shape", var10003, "Which parts of render should be rendered.", var10005::get);
      var10001 = this.sgExplodeRender;
      var1 = new BlackOutColor(255, 0, 0, 255);
      var10005 = this.renderExplode;
      Objects.requireNonNull(var10005);
      this.explodeLineColor = var10001.c("Explode Line Color", var1, "Line color of rendered boxes.", var10005::get);
      var10001 = this.sgExplodeRender;
      var1 = new BlackOutColor(255, 0, 0, 50);
      var10005 = this.renderExplode;
      Objects.requireNonNull(var10005);
      this.explodeSideColor = var10001.c("Explode Side Color", var1, "Side color of rendered boxes.", var10005::get);
      var10001 = this.sgExplodeRender;
      var10005 = this.renderExplode;
      Objects.requireNonNull(var10005);
      this.separateExplode = var10001.b("Separate Explode", true, ".", var10005::get);
      this.explodeHeadShape = this.sgExplodeRender.e("Box Head Shape", RenderShape.Full, "Which parts of render should be rendered.", () -> {
         return (Boolean)this.renderExplode.get() && (Boolean)this.separateExplode.get();
      });
      this.explodeHeadLineColor = this.sgExplodeRender.c("Box Head Line Color", new BlackOutColor(255, 255, 255, 255), "Line color of rendered boxes.", () -> {
         return (Boolean)this.renderExplode.get() && (Boolean)this.separateExplode.get();
      });
      this.explodeHeadSideColor = this.sgExplodeRender.c("Box Head Side Color", new BlackOutColor(255, 255, 255, 50), "Side color of rendered boxes.", () -> {
         return (Boolean)this.renderExplode.get() && (Boolean)this.separateExplode.get();
      });
      this.damageValue = this.sgCalculation.d("Damage Value", 1.0D, -2.0D, 2.0D, 0.05D, ".");
      this.selfDmgValue = this.sgCalculation.d("Self Damage Value", -1.0D, -2.0D, 2.0D, 0.05D, ".");
      this.friendDmgValue = this.sgCalculation.d("Friend Damage Value", 0.0D, -2.0D, 2.0D, 0.05D, ".");
      this.rotationValue = this.sgCalculation.d("Rotation Value", 3.0D, -5.0D, 10.0D, 0.1D, ".");
      this.maxTargets = this.sgCalculation.i("Max Targets", 3, 1, 10, 1, ".");
      this.enemyDistance = this.sgCalculation.d("Enemy Distance", 10.0D, 0.0D, 100.0D, 1.0D, ".");
      this.extMap = new ExtrapolationMap();
      this.hitboxMap = new ExtrapolationMap();
      this.targets = new ArrayList();
      this.renderList = RenderList.getList(false);
      this.placePos = null;
      this.placeDir = null;
      this.explodePos = null;
      this.selfHealth = 0.0D;
      this.enemyHealth = 0.0D;
      this.friendHealth = 0.0D;
      this.selfDamage = 0.0D;
      this.enemyDamage = 0.0D;
      this.friendDamage = 0.0D;
      this.target = null;
      this.targetCalcBest = null;
      this.targetCalcValue = 0.0D;
      this.targetCalcR = 0;
      this.targetProgress = 0;
      this.calcBest = null;
      this.calcDir = null;
      this.calcValue = 0.0D;
      this.calcR = 0;
      this.calcMiddle = null;
      this.progress = 0;
      this.lastExplode = 0L;
      this.lastPlace = 0L;
      this.explodeTimers = new TimerList(true);
      this.ignoreState = new TimerList(true);
      this.suicide = false;
      this.facePlacing = false;
      this.renderPos = null;
      this.renderDir = null;
      this.renderProgress = 0.0D;
      this.damageCache = new HashMap();
      INSTANCE = this;
   }

   public static BedAura getInstance() {
      return INSTANCE;
   }

   @Event
   public void onState(BlockStateEvent event) {
      if (event.state.method_26204() instanceof class_2244) {
         this.explodeTimers.remove((timer) -> {
            return ((class_2338)timer.value).equals(event.pos);
         });
      } else if (!this.validBlock(event.state.method_26204())) {
         return;
      }

      if (this.ignoreState.contains((Object)event.pos)) {
         event.cancel();
      }

   }

   @Event
   public void onTick(TickEvent.Post event) {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null) {
         this.calc(1.0F);
         this.updatePos();
      }

   }

   @Event
   public void onRender(RenderEvent.World.Post event) {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null) {
         this.updateFacePlacing();
         this.calc(event.tickDelta);
         targetedPlayer = null;
         if (this.explodePos != null && !this.pausedExploding()) {
            this.updateExplode();
         }

         this.updateRender();
         if (this.placePos != null && (Boolean)this.place.get() && !this.pausedPlacing()) {
            this.calcDamage(this.placePos.method_10093(this.placeDir));
            class_1309 var3 = this.target;
            if (var3 instanceof class_742) {
               class_742 player = (class_742)var3;
               targetedPlayer = player;
            }

            this.renderPos = this.placePos;
            this.renderDir = this.placeDir;
            this.renderProgress = 0.0D;
            this.updatePlace();
         } else {
            this.renderProgress = Math.min(this.renderProgress + event.frameTime, (Double)this.renderTime.get() + (Double)this.fadeTime.get());
         }
      }
   }

   private boolean validBlock(class_2248 block) {
      return OLEPOSSUtils.replaceable(block);
   }

   private void updateRender() {
      if ((Boolean)this.renderBox.get() && this.renderPos != null && this.renderProgress < (Double)this.renderTime.get() + (Double)this.fadeTime.get()) {
         this.render(this.renderPos, this.renderDir, this.separateBox, this.headLineColor, this.headSideColor, this.renderHeadShape, this.lineColor, this.sideColor, this.renderShape, this.getAlpha(this.renderProgress, this.renderTime, this.fadeTime));
      }

      if ((Boolean)this.renderExplode.get()) {
         this.renderList.update((pair, time, delta) -> {
            this.render((class_2338)pair.method_15442(), (class_2350)pair.method_15441(), this.separateExplode, this.explodeHeadLineColor, this.explodeHeadSideColor, this.explodeHeadShape, this.explodeLineColor, this.explodeSideColor, this.explodeRenderShape, this.getAlpha(time, this.explodeRenderTime, this.explodeFadeTime));
         });
      }

   }

   private double getAlpha(double time, Setting<Double> rt, Setting<Double> ft) {
      return 1.0D - Math.max(time - (Double)rt.get(), 0.0D) / (Double)ft.get();
   }

   private void render(class_2338 feetPos, class_2350 dir, Setting<Boolean> separate, Setting<BlackOutColor> headLines, Setting<BlackOutColor> headSides, Setting<RenderShape> headShape, Setting<BlackOutColor> feetLines, Setting<BlackOutColor> feetSides, Setting<RenderShape> feetShape, double alpha) {
      if ((Boolean)separate.get()) {
         Render3DUtils.box(this.getBoxAt(feetPos), ((BlackOutColor)feetSides.get()).alphaMulti(alpha), ((BlackOutColor)feetLines.get()).alphaMulti(alpha), (RenderShape)feetShape.get());
         Render3DUtils.box(this.getBoxAt(feetPos.method_10093(dir)), ((BlackOutColor)headSides.get()).alphaMulti(alpha), ((BlackOutColor)headLines.get()).alphaMulti(alpha), (RenderShape)headShape.get());
      } else {
         class_238 box = this.getDirectionBox(feetPos, dir);
         Render3DUtils.box(box, ((BlackOutColor)headSides.get()).alphaMulti(alpha), ((BlackOutColor)headLines.get()).alphaMulti(alpha), (RenderShape)headShape.get());
      }
   }

   private class_238 getBoxAt(class_2338 pos) {
      return new class_238((double)pos.method_10263(), (double)pos.method_10264(), (double)pos.method_10260(), (double)(pos.method_10263() + 1), (double)pos.method_10264() + 0.55D, (double)(pos.method_10260() + 1));
   }

   private class_238 getDirectionBox(class_2338 pos, class_2350 dir) {
      double minX = (double)pos.method_10263();
      double minY = (double)pos.method_10264();
      double minZ = (double)pos.method_10260();
      double maxX = (double)(pos.method_10263() + 1);
      double maxY = (double)pos.method_10264() + 0.55D;
      double maxZ = (double)(pos.method_10260() + 1);
      switch(dir) {
      case field_11043:
         --minZ;
         break;
      case field_11035:
         ++maxZ;
         break;
      case field_11039:
         --minX;
         break;
      case field_11034:
         ++maxX;
      }

      return new class_238(minX, minY, minZ, maxX, maxY, maxZ);
   }

   private boolean pausedExploding() {
      return (Boolean)this.pauseEatExplode.get() && BlackOut.mc.field_1724.method_6115();
   }

   private boolean pausedPlacing() {
      return (Boolean)this.pauseEatPlace.get() && BlackOut.mc.field_1724.method_6115();
   }

   private void updatePlace() {
      if (OLEPOSSUtils.replaceable(this.placePos)) {
         if (this.placeDelayCheck()) {
            this.place();
         }
      }
   }

   private boolean placeDelayCheck() {
      return (double)(System.currentTimeMillis() - this.lastPlace) >= 1000.0D / this.shouldSlow() ? (Double)this.slowSpeed.get() : (Double)this.placeSpeed.get();
   }

   private void updateFacePlacing() {
      this.facePlacing = ((KeyBind)this.holdFacePlace.get()).isPressed();
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

   private boolean shouldSlow() {
      this.calcDamage(this.placePos.method_10093(this.placeDir));
      if ((Boolean)this.ignoreSlow.get() && this.shouldFacePlace()) {
         return false;
      } else if (this.enemyHealth < (Double)this.slowHealth.get()) {
         return false;
      } else {
         return this.enemyDamage <= (Double)this.slowDamage.get();
      }
   }

   private void place() {
      PlaceData data = SettingUtils.getPlaceData(this.placePos, (DoublePredicate)null, (p, d) -> {
         if ((Boolean)this.floor.get() && d != class_2350.field_11033) {
            return false;
         } else {
            return !(BlackOut.mc.field_1687.method_8320(p).method_26204() instanceof class_2244);
         }
      });
      if (data.valid()) {
         class_1268 hand = OLEPOSSUtils.getHand(OLEPOSSUtils::isBed);
         FindResult result = ((SwitchMode)this.switchMode.get()).find(OLEPOSSUtils::isBed);
         if (hand != null || result.wasFound()) {
            if (!SettingUtils.shouldRotate(RotationType.BlockPlace) || this.rotateBlock(data, data.pos().method_46558().method_43206(data.dir(), 0.5D), RotationType.BlockPlace, "placing")) {
               if (!(Boolean)this.pauseOffGround.get() || BlackOut.mc.field_1724.method_24828()) {
                  switch((BedAura.RotationMode)this.rotationMode.get()) {
                  case Instant:
                     this.sendPacket(new class_2831(this.placeDir.method_10144(), Managers.ROTATION.nextPitch, Managers.PACKET.isOnGround()));
                     break;
                  case Manager:
                     if (!this.rotateYaw(this.placeDir.method_10144(), RotationType.Other, "placing")) {
                        return;
                     }
                  }

                  boolean switched = false;
                  if (hand != null || (switched = ((SwitchMode)this.switchMode.get()).swap(result.slot()))) {
                     this.placeBlock(hand, data.pos().method_46558(), data.dir(), data.pos());
                     class_1799 stack = hand == null ? result.stack() : Managers.PACKET.handStack(hand);
                     class_1792 var7 = stack.method_7909();
                     if (var7 instanceof class_1748) {
                        class_1748 bedItem = (class_1748)var7;
                        class_2680 feetState = (class_2680)((class_2680)bedItem.method_7711().method_9564().method_11657(class_2244.field_9967, class_2742.field_12557)).method_11657(class_2383.field_11177, this.placeDir);
                        class_2680 headState = (class_2680)((class_2680)bedItem.method_7711().method_9564().method_11657(class_2244.field_9967, class_2742.field_12560)).method_11657(class_2383.field_11177, this.placeDir);
                        BlackOut.mc.field_1687.method_8501(this.placePos, feetState);
                        BlackOut.mc.field_1687.method_8501(this.placePos.method_10093(this.placeDir), headState);
                        this.ignoreState.add(this.placePos, 0.3D);
                        this.ignoreState.add(this.placePos.method_10093(this.placeDir), 0.3D);
                     }

                     this.lastPlace = System.currentTimeMillis();
                     if ((Boolean)this.placeSwing.get()) {
                        this.clientSwing((SwingHand)this.placeHand.get(), hand);
                     }

                     this.end("placing");
                     if (switched) {
                        ((SwitchMode)this.switchMode.get()).swapBack();
                     }

                  }
               }
            }
         }
      }
   }

   private void updateExplode() {
      class_2680 state = BlackOut.mc.field_1687.method_8320(this.explodePos);
      if (state.method_26204() instanceof class_2244) {
         switch((AutoCrystal.ActionSpeedMode)this.explodeSpeedMode.get()) {
         case Sync:
            if ((Double)this.explodeSpeedLimit.get() > 0.0D && (double)(System.currentTimeMillis() - this.lastExplode) <= 1000.0D / (Double)this.explodeSpeedLimit.get()) {
               return;
            }

            if (this.explodeTimers.contains((Object)this.explodePos)) {
               return;
            }
            break;
         case Normal:
            if ((double)(System.currentTimeMillis() - this.lastExplode) <= 1000.0D / (Double)this.explodeSpeed.get()) {
               return;
            }
         }

         this.explode(this.explodePos);
      }
   }

   private void explode(class_2338 pos) {
      class_2350 dir = SettingUtils.getPlaceOnDirection(pos);
      if (dir != null) {
         class_243 placeVec = new class_243((double)pos.method_10263() + 0.5D, (double)pos.method_10264() + (Double)this.rotationHeight.get(), (double)pos.method_10260() + 0.5D);
         this.end("placing");
         if (!SettingUtils.shouldRotate(RotationType.Interact) || this.rotateBlock(pos, dir, placeVec, RotationType.Interact, 0.1D, "explode")) {
            class_2680 state = BlackOut.mc.field_1687.method_8320(pos);
            class_2350 direction = (class_2350)state.method_11654(class_2383.field_11177);
            class_2338 headPos;
            class_2338 feetPos;
            if (state.method_11654(class_2244.field_9967) == class_2742.field_12560) {
               headPos = pos;
               feetPos = pos.method_10093(direction.method_10153());
            } else {
               feetPos = pos;
               headPos = pos.method_10093(direction);
            }

            this.renderList.add(new Pair(feetPos, direction), (Double)this.explodeRenderTime.get() + (Double)this.explodeFadeTime.get());
            BlackOut.mc.field_1687.method_8501(feetPos, class_2246.field_10124.method_9564());
            BlackOut.mc.field_1687.method_8501(headPos, class_2246.field_10124.method_9564());
            this.ignoreState.add(feetPos, 0.3D);
            this.ignoreState.add(headPos, 0.3D);
            this.interactBlock(class_1268.field_5808, placeVec, dir, pos);
            this.explodeTimers.add(pos, 1.0D / (Double)this.constantExplodeSpeed.get());
            this.lastExplode = System.currentTimeMillis();
            this.explodePos = null;
            if ((Boolean)this.explodeSwing.get()) {
               this.clientSwing((SwingHand)this.explodeHand.get(), class_1268.field_5808);
            }

            this.end("explode");
         }
      }
   }

   private void calc(float tickDelta) {
      if (this.calcMiddle != null) {
         int d = this.calcR * 2 + 1;
         int target = d * d * d;

         int i;
         int x;
         int y;
         int z;
         class_2338 pos;
         for(i = this.progress; (float)i < (float)target * tickDelta; ++i) {
            this.progress = i;
            x = i % d - this.calcR;
            y = i / d % d - this.calcR;
            z = i / d / d % d - this.calcR;
            pos = this.calcMiddle.method_10069(x, y, z);
            this.calcPos(pos);
         }

         d = this.targetCalcR * 2 + 1;
         target = d * d * d;

         for(i = this.targetProgress; (float)i < (float)target * tickDelta; ++i) {
            this.targetProgress = i;
            x = i % d - this.targetCalcR;
            y = i / d % d - this.targetCalcR;
            z = i / d / d % d - this.targetCalcR;
            pos = this.calcMiddle.method_10069(x, y, z);
            this.calcTarget(pos);
         }

      }
   }

   private void calcTarget(class_2338 pos) {
      class_2680 state = BlackOut.mc.field_1687.method_8320(pos);
      if (state.method_26204() instanceof class_2244) {
         if (SettingUtils.getPlaceOnDirection(pos) != null) {
            if (SettingUtils.inPlaceRange(pos)) {
               this.calcDamage(state.method_11654(class_2244.field_9967) == class_2742.field_12560 ? pos : pos.method_10093((class_2350)state.method_11654(class_2383.field_11177)));
               if (this.explodeDamageCheck()) {
                  double value = this.getValue(pos, false);
                  if (!(value <= this.targetCalcValue)) {
                     this.targetCalcBest = pos;
                     this.targetCalcValue = value;
                  }
               }
            }
         }
      }
   }

   private boolean explodeDamageCheck() {
      if (this.selfDamage * (Double)this.selfPop.get() > this.selfHealth) {
         return false;
      } else if (this.friendDamage * (Double)this.friendPop.get() > this.friendHealth) {
         return false;
      } else if (this.enemyDamage * (Double)this.forcePop.get() > this.enemyHealth) {
         return true;
      } else if ((Boolean)this.checkEnemyExplode.get() && this.enemyDamage < this.shouldFacePlace() ? (Double)this.facePlaceDamage.get() : (Double)this.minExplode.get()) {
         return false;
      } else {
         if ((Boolean)this.checkSelfExplode.get()) {
            if (this.selfDamage > (Double)this.maxSelfExplode.get()) {
               return false;
            }

            if ((Boolean)this.checkEnemyExplode.get() && this.enemyDamage / this.selfDamage < (Double)this.minSelfExplodeRatio.get()) {
               return false;
            }
         }

         if ((Boolean)this.checkFriendExplode.get()) {
            if (this.friendDamage > (Double)this.maxFriendExplode.get()) {
               return false;
            } else {
               return !(this.enemyDamage / this.friendDamage < (Double)this.minFriendExplodeRatio.get());
            }
         } else {
            return true;
         }
      }
   }

   private boolean placeDamageCheck() {
      if (this.selfDamage * (Double)this.selfPop.get() > this.selfHealth) {
         return false;
      } else if (this.friendDamage * (Double)this.friendPop.get() > this.friendHealth) {
         return false;
      } else if (this.enemyDamage * (Double)this.forcePop.get() > this.enemyHealth) {
         return true;
      } else if (this.enemyDamage < this.shouldFacePlace() ? (Double)this.facePlaceDamage.get() : (Double)this.minPlace.get()) {
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

   private void calcPos(class_2338 pos) {
      if (this.validBlock(pos)) {
         if (!(Boolean)this.floor.get() || this.validFloor(pos.method_10074())) {
            if (this.inRangeToEnemies(pos)) {
               boolean midInRange = SettingUtils.inInteractRange(pos) && SettingUtils.getPlaceOnDirection(pos) != null;
               this.calcDamage(pos);
               if (this.placeDamageCheck()) {
                  double value = this.getValue(pos, true);
                  if (!(value <= this.calcValue)) {
                     Iterator var5 = class_2353.field_11062.iterator();

                     while(true) {
                        class_2350 dir;
                        class_2338 pos2;
                        PlaceData data;
                        do {
                           do {
                              do {
                                 do {
                                    do {
                                       do {
                                          if (!var5.hasNext()) {
                                             return;
                                          }

                                          dir = (class_2350)var5.next();
                                          pos2 = pos.method_10093(dir);
                                       } while((Boolean)this.serverDir.get() && Math.abs(RotationUtils.yawAngle(RotationUtils.getYaw(pos2), (double)dir.method_10153().method_10144())) > 45.0D);
                                    } while(!this.validBlock(pos2));
                                 } while((Boolean)this.floor.get() && !this.validFloor(pos2.method_10074()));

                                 data = SettingUtils.getPlaceData(pos2, (DoublePredicate)null, (p, d) -> {
                                    if ((Boolean)this.floor.get() && d != class_2350.field_11033) {
                                       return false;
                                    } else {
                                       return !(BlackOut.mc.field_1687.method_8320(p).method_26204() instanceof class_2244);
                                    }
                                 });
                              } while(!data.valid());
                           } while(!midInRange && SettingUtils.getPlaceOnDirection(pos2) == null);
                        } while(!midInRange && !SettingUtils.inInteractRange(pos2));

                        if (SettingUtils.inPlaceRange(data.pos())) {
                           if (!(Boolean)this.noHitbox.get() && EntityUtils.intersects(BoxUtils.get(pos2), (entity) -> {
                              return !(entity instanceof class_1542);
                           }, this.hitboxMap.getMap())) {
                              return;
                           }

                           this.calcBest = pos;
                           this.calcValue = value;
                           this.calcDir = dir;
                        }
                     }
                  }
               }
            }
         }
      }
   }

   private boolean validFloor(class_2338 pos) {
      return BlackOut.mc.field_1687.method_8320(pos).method_26204() instanceof class_2244 ? false : OLEPOSSUtils.solid2(pos);
   }

   private boolean validBlock(class_2338 pos) {
      class_2248 block = BlackOut.mc.field_1687.method_8320(pos).method_26204();
      if (!this.validBlock(block) && !(block instanceof class_2244)) {
         return false;
      } else {
         return !(Boolean)this.fireBlocking.get() || !(block instanceof class_2358);
      }
   }

   private void updatePos() {
      Suicide suicideModule = Suicide.getInstance();
      this.suicide = suicideModule.enabled && (Boolean)suicideModule.useBA.get();
      this.findTargets();
      this.extMap.update((player) -> {
         return player == BlackOut.mc.field_1724 ? (Integer)this.selfExt.get() : (Integer)this.extrapolation.get();
      });
      this.hitboxMap.update((player) -> {
         return player == BlackOut.mc.field_1724 ? 0 : (Integer)this.hitboxExt.get();
      });
      this.placePos = this.calcBest == null ? null : this.calcBest.method_10093(this.calcDir);
      this.placeDir = this.calcDir == null ? null : this.calcDir.method_10153();
      this.explodePos = this.targetCalcBest;
      this.startCalc();
   }

   private void startCalc() {
      this.selfHealth = this.getHealth(BlackOut.mc.field_1724);
      this.calcBest = null;
      this.calcValue = -42069.0D;
      this.progress = 0;
      this.calcR = (int)Math.ceil(SettingUtils.maxPlaceRange());
      this.calcMiddle = class_2338.method_49638(BlackOut.mc.field_1724.method_33571());
      this.targetCalcBest = null;
      this.targetCalcValue = -42069.0D;
      this.targetCalcR = (int)Math.ceil(SettingUtils.maxInteractRange());
      this.targetProgress = 0;
      this.damageCache.clear();
   }

   private double getValue(class_2338 pos, boolean place) {
      double value = 0.0D;
      if (place && SettingUtils.shouldRotate(RotationType.BlockPlace) || !place && SettingUtils.shouldRotate(RotationType.Interact)) {
         value += this.rotationMod(pos.method_46558());
      }

      value += this.enemyMod();
      value += this.selfMod();
      value += this.friendMod();
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

   private double rotationMod(class_243 pos) {
      double yawStep = 45.0D;
      double pitchStep = 22.0D;
      int yawSteps = (int)Math.ceil(Math.abs(RotationUtils.yawAngle((double)Managers.ROTATION.prevYaw, RotationUtils.getYaw(pos)) / yawStep));
      int pitchSteps = (int)Math.ceil(Math.abs(RotationUtils.pitchAngle((double)Managers.ROTATION.prevPitch, RotationUtils.getPitch(pos)) / pitchStep));
      int steps = Math.max(yawSteps, pitchSteps);
      return (double)(3 - Math.min(steps, 3)) * (Double)this.rotationValue.get();
   }

   private boolean inRangeToEnemies(class_2338 pos) {
      class_243 vec = pos.method_46558();
      if (this.suicide) {
         return BoxUtils.middle(BlackOut.mc.field_1724.method_5829()).method_1022(vec) < 3.0D;
      } else {
         Iterator var3 = this.targets.iterator();

         class_1657 player;
         do {
            if (!var3.hasNext()) {
               return false;
            }

            player = (class_1657)var3.next();
         } while(!(BoxUtils.middle(player.method_5829()).method_1022(vec) < 3.0D));

         return true;
      }
   }

   private void findTargets() {
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
                        map.forEach((playerx, d) -> {
                           this.targets.add(playerx);
                        });
                        return;
                     }

                     player = (class_1657)var2.next();
                  } while(player == BlackOut.mc.field_1724);
               } while(player.method_6032() <= 0.0F);

               distance = (double)BlackOut.mc.field_1724.method_5739(player);
            } while(distance > (Double)this.enemyDistance.get());

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

   private void calcDamage(class_2338 pos) {
      class_243 vec = pos.method_46558();
      if (this.damageCache.containsKey(pos)) {
         Double[] array = (Double[])this.damageCache.get(pos);
         this.selfDamage = array[0];
         this.enemyDamage = array[1];
         this.friendDamage = array[2];
         this.enemyHealth = array[3];
         this.friendHealth = array[4];
      } else {
         this.selfDamage = DamageUtils.anchorDamage(BlackOut.mc.field_1724, this.extMap.get(BlackOut.mc.field_1724), vec, pos);
         this.enemyDamage = 0.0D;
         this.friendDamage = 0.0D;
         if (this.suicide) {
            this.enemyDamage = this.selfDamage;
            this.selfDamage = 0.0D;
            this.friendDamage = 0.0D;
            this.enemyHealth = 20.0D;
            this.friendHealth = 36.0D;
            this.cache(pos);
         } else {
            this.enemyHealth = 20.0D;
            this.friendHealth = 20.0D;
            this.targets.forEach((player) -> {
               class_238 box = this.extMap.get(player);
               if (!(player.method_6032() <= 0.0F) && player != BlackOut.mc.field_1724) {
                  double dmg = DamageUtils.anchorDamage(player, box, vec, pos);
                  double health = this.getHealth(player);
                  if (Managers.FRIENDS.isFriend(player)) {
                     if (dmg > this.friendDamage) {
                        this.friendDamage = dmg;
                        this.friendHealth = health;
                     }
                  } else if (dmg > this.enemyDamage) {
                     this.enemyDamage = dmg;
                     this.enemyHealth = health;
                     this.target = player;
                  }

               }
            });
            this.cache(pos);
         }
      }
   }

   private void cache(class_2338 pos) {
      this.damageCache.put(pos, new Double[]{this.selfDamage, this.enemyDamage, this.friendDamage, this.enemyHealth, this.friendHealth});
   }

   private double getHealth(class_1309 entity) {
      return (double)(entity.method_6032() + entity.method_6067());
   }

   public static enum RotationMode {
      Instant,
      Manager;

      // $FF: synthetic method
      private static BedAura.RotationMode[] $values() {
         return new BedAura.RotationMode[]{Instant, Manager};
      }
   }
}
