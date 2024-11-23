package bodevelopment.client.blackout.module.modules.movement;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.enums.RotationType;
import bodevelopment.client.blackout.enums.SwingHand;
import bodevelopment.client.blackout.enums.SwitchMode;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.MoveEvent;
import bodevelopment.client.blackout.event.events.RenderEvent;
import bodevelopment.client.blackout.interfaces.functional.DoublePredicate;
import bodevelopment.client.blackout.interfaces.mixin.IVec3d;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.module.MoveUpdateModule;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.modules.combat.defensive.Surround;
import bodevelopment.client.blackout.module.modules.misc.Timer;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.module.setting.multisettings.BoxMultiSetting;
import bodevelopment.client.blackout.randomstuff.BlackOutColor;
import bodevelopment.client.blackout.randomstuff.FindResult;
import bodevelopment.client.blackout.randomstuff.PlaceData;
import bodevelopment.client.blackout.randomstuff.Rotation;
import bodevelopment.client.blackout.randomstuff.timers.TimerList;
import bodevelopment.client.blackout.rendering.renderer.Renderer;
import bodevelopment.client.blackout.util.BoxUtils;
import bodevelopment.client.blackout.util.DamageUtils;
import bodevelopment.client.blackout.util.EntityUtils;
import bodevelopment.client.blackout.util.OLEPOSSUtils;
import bodevelopment.client.blackout.util.RotationUtils;
import bodevelopment.client.blackout.util.SettingUtils;
import bodevelopment.client.blackout.util.render.AnimUtils;
import bodevelopment.client.blackout.util.render.RenderUtils;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import net.minecraft.class_1268;
import net.minecraft.class_1297;
import net.minecraft.class_1511;
import net.minecraft.class_1542;
import net.minecraft.class_1747;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_3532;
import net.minecraft.class_4587;

public class Scaffold extends MoveUpdateModule {
   private static Scaffold INSTANCE;
   private final SettingGroup sgGeneral = this.addGroup("General");
   private final SettingGroup sgPlacing = this.addGroup("Placing");
   private final SettingGroup sgAttack = this.addGroup("Attack");
   private final SettingGroup sgRender = this.addGroup("Render");
   private final Setting<Boolean> smart;
   private final Setting<Scaffold.TowerMode> tower;
   private final Setting<Boolean> towerMoving;
   public final Setting<Boolean> safeWalk;
   private final Setting<Boolean> useTimer;
   private final Setting<Double> timer;
   private final Setting<Boolean> constantRotate;
   private final Setting<Double> rotationTime;
   private final Setting<Integer> support;
   private final Setting<Boolean> keepY;
   private final Setting<Boolean> allowTower;
   private final Setting<Scaffold.YawMode> smartYaw;
   private final Setting<SwitchMode> switchMode;
   private final Setting<List<class_2248>> blocks;
   private final Setting<Surround.PlaceDelayMode> placeDelayMode;
   private final Setting<Integer> placeDelayT;
   private final Setting<Double> placeDelayS;
   private final Setting<Integer> places;
   private final Setting<Double> cooldown;
   private final Setting<Integer> extrapolation;
   private final Setting<Boolean> instantRotate;
   private final Setting<Boolean> attack;
   private final Setting<Double> attackSpeed;
   private final Setting<Boolean> drawBlocks;
   private final Setting<BlackOutColor> customColor;
   private final Setting<Boolean> bg;
   private final Setting<Boolean> blur;
   private final Setting<Boolean> shadow;
   private final Setting<BlackOutColor> bgColor;
   private final Setting<BlackOutColor> shadowColor;
   private final Setting<Boolean> placeSwing;
   private final Setting<SwingHand> placeHand;
   private final Setting<Boolean> attackSwing;
   private final Setting<SwingHand> attackHand;
   private final Setting<Scaffold.RenderMode> renderMode;
   private final BoxMultiSetting rendering;
   private final Setting<Double> renderTime;
   private int placeTickTimer;
   private double placeTimer;
   private long lastAttack;
   private int placesLeft;
   private int blocksLeft;
   float delta;
   private final class_4587 stack;
   private boolean changedTimer;
   private class_243 movement;
   private FindResult result;
   private boolean switched;
   private class_1268 hand;
   private final TimerList<class_2338> placed;
   private boolean towerRotate;
   private final List<class_2338> positions;
   private final List<class_2338> valids;
   private final List<class_238> boxes;
   private final TimerList<class_2338> render;
   private int jumpProgress;
   private final float[] velocities;
   private final float[] slowVelocities;
   private double startY;

   public Scaffold() {
      super("Scaffold", "Places blocks under your feet.", SubCategory.MOVEMENT);
      this.smart = this.sgGeneral.b("Smart", true, "Only places on blocks that you can reach.");
      this.tower = this.sgGeneral.e("Tower", Scaffold.TowerMode.NCP, "Flies up with blocks.");
      this.towerMoving = this.sgGeneral.b("Moving Tower", false, "Allows you to move while towering.");
      this.safeWalk = this.sgGeneral.b("Safe Walk", true, "Stops you from falling off.");
      this.useTimer = this.sgGeneral.b("Use Timer", false, "Should we use timer.");
      SettingGroup var10001 = this.sgGeneral;
      Setting var10008 = this.useTimer;
      Objects.requireNonNull(var10008);
      this.timer = var10001.d("Timer", 1.088D, 0.0D, 10.0D, 0.1D, "Should we use timer.", var10008::get);
      this.constantRotate = this.sgGeneral.b("Constant Rotate", false, "Stops you from falling off.");
      this.rotationTime = this.sgGeneral.d("Rotation Time", 1.0D, 0.0D, 1.0D, 0.01D, "Keeps rotations for x seconds.");
      this.support = this.sgPlacing.i("Support", 3, 0, 5, 1, "Max amount of support blocks.");
      this.keepY = this.sgGeneral.b("Keep Y", false, ".");
      var10001 = this.sgGeneral;
      Setting var10005 = this.keepY;
      Objects.requireNonNull(var10005);
      this.allowTower = var10001.b("Allow Tower", true, "Doesn't keep y while standing still.", var10005::get);
      this.smartYaw = this.sgGeneral.e("Yaw Mode", Scaffold.YawMode.Normal, ".");
      this.switchMode = this.sgPlacing.e("Switch Mode", SwitchMode.Normal, "Method of switching. Silent is the most reliable.");
      this.blocks = this.sgPlacing.bl("Blocks", "Blocks to use.", class_2246.field_10540, class_2246.field_22423, class_2246.field_22108, class_2246.field_10340, class_2246.field_10161, class_2246.field_10375);
      this.placeDelayMode = this.sgPlacing.e("Place Delay Mode", Surround.PlaceDelayMode.Ticks, ".");
      this.placeDelayT = this.sgPlacing.i("Place Tick Delay", 1, 0, 20, 1, "Tick delay between places.", () -> {
         return this.placeDelayMode.get() == Surround.PlaceDelayMode.Ticks;
      });
      this.placeDelayS = this.sgPlacing.d("Place Delay", 0.1D, 0.0D, 1.0D, 0.01D, "Delay between places.", () -> {
         return this.placeDelayMode.get() == Surround.PlaceDelayMode.Seconds;
      });
      this.places = this.sgPlacing.i("Places", 1, 1, 20, 1, "How many blocks to place each time.");
      this.cooldown = this.sgPlacing.d("Cooldown", 0.3D, 0.0D, 1.0D, 0.01D, "Waits x seconds before trying to place at the same position if there is more than 1 missing block.");
      this.extrapolation = this.sgPlacing.i("Extrapolation", 3, 1, 20, 1, "Predicts movement.");
      this.instantRotate = this.sgGeneral.b("Instant Rotate", true, "Ignores rotation speed limit.");
      this.attack = this.sgAttack.b("Attack", true, "Attacks crystals blocking surround.");
      var10001 = this.sgAttack;
      var10008 = this.attack;
      Objects.requireNonNull(var10008);
      this.attackSpeed = var10001.d("Attack Speed", 4.0D, 0.0D, 20.0D, 0.05D, "How many times to attack every second.", var10008::get);
      this.drawBlocks = this.sgRender.b("Show Blocks", true, "Draws the amount of blocks you have");
      var10001 = this.sgRender;
      BlackOutColor var10003 = new BlackOutColor(255, 255, 255, 255);
      var10005 = this.drawBlocks;
      Objects.requireNonNull(var10005);
      this.customColor = var10001.c("Text Color", var10003, "Text Color", var10005::get);
      var10001 = this.sgRender;
      var10005 = this.drawBlocks;
      Objects.requireNonNull(var10005);
      this.bg = var10001.b("Background", true, "Draws a background", var10005::get);
      var10001 = this.sgRender;
      var10005 = this.drawBlocks;
      Objects.requireNonNull(var10005);
      this.blur = var10001.b("Block Blur", true, ".", var10005::get);
      var10001 = this.sgRender;
      var10005 = this.drawBlocks;
      Objects.requireNonNull(var10005);
      this.shadow = var10001.b("Shadow", true, ".", var10005::get);
      this.bgColor = this.sgRender.c("Background Color", new BlackOutColor(0, 0, 0, 50), ".", () -> {
         return (Boolean)this.drawBlocks.get() && (Boolean)this.bg.get();
      });
      this.shadowColor = this.sgRender.c("Shadow Color", new BlackOutColor(0, 0, 0, 100), ".", () -> {
         return (Boolean)this.drawBlocks.get() && (Boolean)this.bg.get() && (Boolean)this.shadow.get();
      });
      this.placeSwing = this.sgRender.b("Place Swing", true, "Renders swing animation when placing a block.");
      var10001 = this.sgRender;
      SwingHand var1 = SwingHand.RealHand;
      var10005 = this.placeSwing;
      Objects.requireNonNull(var10005);
      this.placeHand = var10001.e("Place Swing Hand", var1, "Which hand should be swung.", var10005::get);
      this.attackSwing = this.sgRender.b("Attack Swing", true, "Renders swing animation when attacking a block.");
      var10001 = this.sgRender;
      var1 = SwingHand.RealHand;
      var10005 = this.attackSwing;
      Objects.requireNonNull(var10005);
      this.attackHand = var10001.e("Attack Swing Hand", var1, "Which hand should be swung.", var10005::get);
      this.renderMode = this.sgRender.e("Render Mode", Scaffold.RenderMode.Placed, "Which parts should be rendered.");
      this.rendering = BoxMultiSetting.of(this.sgRender);
      var10001 = this.sgAttack;
      var10008 = this.attack;
      Objects.requireNonNull(var10008);
      this.renderTime = var10001.d("Render Time", 1.0D, 0.0D, 5.0D, 0.1D, "How many times to attack every second.", var10008::get);
      this.placeTickTimer = 0;
      this.placeTimer = 0.0D;
      this.lastAttack = 0L;
      this.placesLeft = 0;
      this.blocksLeft = 0;
      this.delta = 0.0F;
      this.stack = new class_4587();
      this.changedTimer = false;
      this.movement = class_243.field_1353;
      this.result = null;
      this.switched = false;
      this.hand = null;
      this.placed = new TimerList(true);
      this.towerRotate = false;
      this.positions = new ArrayList();
      this.valids = new ArrayList();
      this.boxes = new ArrayList();
      this.render = new TimerList(false);
      this.jumpProgress = -1;
      this.velocities = new float[]{0.42F, 0.3332F, 0.2468F};
      this.slowVelocities = new float[]{0.42F, 0.3332F, 0.2468F, 0.0F};
      this.startY = 0.0D;
      INSTANCE = this;
   }

   public static Scaffold getInstance() {
      return INSTANCE;
   }

   public void onEnable() {
      if (!(Boolean)this.constantRotate.get()) {
         this.end("placing");
      }

      this.startY = BlackOut.mc.field_1724.method_23318();
   }

   public void onDisable() {
      this.placeTimer = 0.0D;
      this.delta = 0.0F;
      this.placesLeft = (Integer)this.places.get();
      if (this.changedTimer) {
         Timer.reset();
         this.changedTimer = false;
      }

   }

   protected double getRotationTime() {
      return (Double)this.rotationTime.get();
   }

   @Event
   public void onRender(RenderEvent.World.Post event) {
      this.render.update();
      switch((Scaffold.RenderMode)this.renderMode.get()) {
      case Placed:
         this.render.forEach((timer) -> {
            double progress = 1.0D - class_3532.method_15350(class_3532.method_15370((double)System.currentTimeMillis(), (double)timer.startTime, (double)timer.endTime), 0.0D, 1.0D);
            this.rendering.render(BoxUtils.get((class_2338)timer.value), (float)progress, 1.0F);
         });
         break;
      case NotPlaced:
         this.positions.forEach((pos) -> {
            this.rendering.render(BoxUtils.get(pos), 1.0F, 1.0F);
         });
         this.render.forEach((timer) -> {
            double progress = 1.0D - class_3532.method_15350(class_3532.method_15370((double)System.currentTimeMillis(), (double)timer.startTime, (double)timer.endTime), 0.0D, 1.0D);
            this.rendering.render(BoxUtils.get((class_2338)timer.value), (float)progress, 1.0F);
         });
      }

   }

   @Event
   public void onRenderHud(RenderEvent.Hud.Pre event) {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null) {
         this.updateResult();
         class_1268 hand = OLEPOSSUtils.getHand(this::valid);
         class_1799 itemStack;
         if (hand != null) {
            itemStack = Managers.PACKET.stackInHand(hand);
         } else {
            if (!this.result.wasFound()) {
               return;
            }

            itemStack = this.result.stack();
         }

         String text = String.valueOf(itemStack.method_7947());
         float textScale = 3.0F;
         float width = BlackOut.FONT.getWidth(text) * textScale + 26.0F;
         float height = BlackOut.FONT.getHeight() * textScale;
         if (this.enabled) {
            this.delta = (float)Math.min((double)this.delta + event.frameTime * 4.0D, 1.0D);
         } else {
            this.delta = (float)Math.max((double)this.delta - event.frameTime * 4.0D, 0.0D);
         }

         if ((Boolean)this.drawBlocks.get()) {
            this.stack.method_22903();
            RenderUtils.unGuiScale(this.stack);
            float anim = (float)AnimUtils.easeOutQuart((double)this.delta);
            this.stack.method_46416((float)BlackOut.mc.method_22683().method_4480() / 2.0F - width / 2.0F, (float)BlackOut.mc.method_22683().method_4507() / 2.0F + height + 2.0F, 0.0F);
            this.stack.method_22905(anim, anim, 1.0F);
            float prevAlpha = Renderer.getAlpha();
            Renderer.setAlpha(anim);
            this.stack.method_22903();
            this.stack.method_46416(width / -2.0F + width / 2.0F, (height + 2.0F) / -2.0F + height / 2.0F, 0.0F);
            if ((Boolean)this.blur.get()) {
               RenderUtils.drawLoadedBlur("hudblur", this.stack, (renderer) -> {
                  renderer.rounded(0.0F, 0.0F, width, height, 6.0F, 10);
               });
               Renderer.onHUDBlur();
            }

            if ((Boolean)this.bg.get()) {
               RenderUtils.rounded(this.stack, 0.0F, 0.0F, width, height, 6.0F, (Boolean)this.shadow.get() ? 6.0F : 0.0F, ((BlackOutColor)this.bgColor.get()).getRGB(), ((BlackOutColor)this.shadowColor.get()).getRGB());
            }

            RenderUtils.renderItem(this.stack, itemStack.method_7909(), 3.0F, 3.0F, 24.0F);
            BlackOut.FONT.text(this.stack, text, textScale, 26.0F, 1.0F, ((BlackOutColor)this.customColor.get()).getColor(), false, false);
            Renderer.setAlpha(prevAlpha);
            this.stack.method_22909();
            this.stack.method_22909();
         }

      }
   }

   public void preTick() {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null) {
         ++this.placeTickTimer;
         if ((Boolean)this.useTimer.get()) {
            Timer.set(((Double)this.timer.get()).floatValue());
            this.changedTimer = true;
         }

         super.preTick();
      }
   }

   public void postMove() {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null) {
         super.postMove();
      }
   }

   public void postTick() {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null) {
         if (BlackOut.mc.field_1724.method_24828()) {
            this.startY = BlackOut.mc.field_1724.method_23318();
         }

         if (this.canTower() && this.towerRotate && SettingUtils.shouldRotate(RotationType.BlockPlace)) {
            PlaceData data = SettingUtils.getPlaceData(BlackOut.mc.field_1724.method_24515(), (DoublePredicate)null, (DoublePredicate)null);
            if (data.valid()) {
               this.rotateBlock(data, RotationType.BlockPlace, -0.1D, "tower");
            }
         }

         super.postTick();
      }
   }

   @Event
   public void onMove(MoveEvent.Pre event) {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null) {
         this.movement = event.movement;
         this.updateTower(event);
      }
   }

   private void updateTower(MoveEvent.Pre event) {
      if (this.canTower()) {
         switch((Scaffold.TowerMode)this.tower.get()) {
         case NCP:
            if (BlackOut.mc.field_1690.field_1903.method_1434() && ((Boolean)this.towerMoving.get() || BlackOut.mc.field_1724.field_3913.field_3905 == 0.0F && BlackOut.mc.field_1724.field_3913.field_3907 == 0.0F)) {
               this.towerRotate = true;
               if (BlackOut.mc.field_1724.method_24828() || this.jumpProgress == 3) {
                  this.jumpProgress = 0;
               }

               if (this.jumpProgress > -1 && this.jumpProgress < 3) {
                  if (!(Boolean)this.towerMoving.get()) {
                     event.setXZ(this, 0.0D, 0.0D);
                  }

                  event.setY(this, (double)this.velocities[this.jumpProgress]);
                  ((IVec3d)BlackOut.mc.field_1724.method_18798()).blackout_Client$setY((double)this.velocities[this.jumpProgress]);
                  ++this.jumpProgress;
               }
            } else {
               this.jumpProgress = -1;
               this.towerRotate = false;
            }
            break;
         case SlowNCP:
            if (BlackOut.mc.field_1690.field_1903.method_1434() && ((Boolean)this.towerMoving.get() || BlackOut.mc.field_1724.field_3913.field_3905 == 0.0F && BlackOut.mc.field_1724.field_3913.field_3907 == 0.0F)) {
               this.towerRotate = true;
               if (BlackOut.mc.field_1724.method_24828() || this.jumpProgress == 4) {
                  this.jumpProgress = 0;
               }

               if (this.jumpProgress > -1 && this.jumpProgress < 4) {
                  if (!(Boolean)this.towerMoving.get()) {
                     event.setXZ(this, 0.0D, 0.0D);
                  }

                  event.setY(this, (double)this.slowVelocities[this.jumpProgress]);
                  ((IVec3d)BlackOut.mc.field_1724.method_18798()).blackout_Client$setY((double)this.slowVelocities[this.jumpProgress]);
                  ++this.jumpProgress;
               }
            } else {
               this.jumpProgress = -1;
               this.towerRotate = false;
            }
            break;
         case TP:
            if (!BlackOut.mc.field_1690.field_1903.method_1434() || !(Boolean)this.towerMoving.get() && (BlackOut.mc.field_1724.field_3913.field_3905 != 0.0F || BlackOut.mc.field_1724.field_3913.field_3907 != 0.0F)) {
               this.jumpProgress = -1;
               this.towerRotate = false;
            } else {
               this.towerRotate = true;
               if (BlackOut.mc.field_1724.method_24828() || this.jumpProgress == 1) {
                  this.jumpProgress = 0;
               }

               if (this.jumpProgress == 0) {
                  if (!(Boolean)this.towerMoving.get()) {
                     event.setXZ(this, 0.0D, 0.0D);
                  }

                  event.setY(this, 1.0D);
                  ((IVec3d)BlackOut.mc.field_1724.method_18798()).blackout_Client$setY(1.0D);
                  ++this.jumpProgress;
               }
            }
            break;
         case Disabled:
            if (BlackOut.mc.field_1690.field_1903.method_1434() && ((Boolean)this.towerMoving.get() || BlackOut.mc.field_1724.field_3913.field_3905 == 0.0F && BlackOut.mc.field_1724.field_3913.field_3907 == 0.0F)) {
               this.towerRotate = true;
               if (BlackOut.mc.field_1724.method_24828() || this.jumpProgress == 1) {
                  this.jumpProgress = 0;
               }

               if (this.jumpProgress == 0) {
                  ++this.jumpProgress;
               }
            } else {
               this.jumpProgress = -1;
               this.towerRotate = false;
            }
         }

      }
   }

   protected void update(boolean allowAction, boolean fakePos) {
      if (fakePos) {
         this.updateBlocks(this.movement);
      }

      this.placeBlocks(allowAction);
   }

   private boolean canTower() {
      if (this.shouldKeepY() && BlackOut.mc.field_1724.method_23318() >= this.startY) {
         return false;
      } else {
         return OLEPOSSUtils.getHand(this::valid) != null || this.updateResult().wasFound();
      }
   }

   private void placeBlocks(boolean allowAction) {
      this.valids.clear();
      this.valids.addAll(this.positions.stream().filter(this::validBlock).toList());
      this.updateAttack(allowAction);
      this.updateResult();
      this.updatePlaces();
      this.blocksLeft = Math.min(this.placesLeft, this.result.amount());
      this.hand = this.getHand();
      this.switched = false;
      this.positions.stream().filter((pos) -> {
         return !EntityUtils.intersects(BoxUtils.get(pos), this::validEntity);
      }).sorted(Comparator.comparingDouble(RotationUtils::getYaw)).forEach((pos) -> {
         this.place(pos, allowAction);
      });
      if (this.switched && this.hand == null) {
         ((SwitchMode)this.switchMode.get()).swapBack();
      }

   }

   private FindResult updateResult() {
      return this.result = ((SwitchMode)this.switchMode.get()).find(this::valid);
   }

   private void updateBlocks(class_243 motion) {
      this.boxes.clear();
      this.positions.clear();
      class_2350[] directions = this.getDirections(motion);
      class_238 box = BlackOut.mc.field_1724.method_5829();
      double x;
      if (this.shouldKeepY()) {
         x = box.field_1322 - Math.min(this.startY, box.field_1322);
         box = box.method_35578(box.field_1325 - x);
         box = box.method_35575(box.field_1322 - x);
      }

      this.addBlocks(box, directions, (Integer)this.support.get());
      x = motion.field_1352;
      double y = motion.field_1351;
      double z = motion.field_1350;
      boolean onGround = this.inside(box.method_989(0.0D, -0.04D, 0.0D));

      for(int i = 0; i < (Integer)this.extrapolation.get(); ++i) {
         if (!(Boolean)this.smart.get() || !this.inside(box.method_989(x, 0.0D, 0.0D))) {
            box = box.method_989(x, 0.0D, 0.0D);
         }

         if (!(Boolean)this.smart.get() || !this.inside(box.method_989(0.0D, 0.0D, z))) {
            box = box.method_989(0.0D, 0.0D, z);
         }

         if (!this.shouldKeepY()) {
            if (onGround) {
               if (BlackOut.mc.field_1690.field_1903.method_1434()) {
                  y = 0.42D;
               } else {
                  y = 0.0D;
               }
            }

            if (!this.inside(box.method_989(0.0D, y, 0.0D))) {
               if (box.field_1322 + y <= Math.floor(box.field_1322)) {
                  box = box.method_989(0.0D, -(box.field_1322 % 1.0D), 0.0D);
               } else {
                  box = box.method_989(0.0D, y, 0.0D);
               }
            }

            onGround = this.inside(box.method_989(0.0D, -0.04D, 0.0D)) || box.field_1322 % 1.0D == 0.0D;
            y = (y - 0.08D) * 0.98D;
         }

         this.boxes.add(box);
         this.addBlocks(box, directions, 1);
      }

   }

   private boolean shouldKeepY() {
      return (Boolean)this.allowTower.get() && this.movement.method_37267() < 0.1D ? false : (Boolean)this.keepY.get();
   }

   private boolean inside(class_238 box) {
      return OLEPOSSUtils.inside(BlackOut.mc.field_1724, box);
   }

   private boolean addBlocks2(class_238 box, class_2350[] directions, int b) {
      class_2338 feetPos = class_2338.method_49638(BoxUtils.feet(box).method_1031(0.0D, -0.5D, 0.0D));
      if (OLEPOSSUtils.replaceable(feetPos) && !this.positions.contains(feetPos) && !this.intersects(feetPos)) {
         if (b < 1 && this.validSupport(feetPos, true)) {
            this.positions.add(feetPos);
            return true;
         } else {
            int l = directions.length;
            class_2350[] drr = new class_2350[b];

            for(int i = 0; (double)i < Math.pow((double)l, (double)b); ++i) {
               for(int j = 0; j < b; ++j) {
                  class_2350 dir = directions[i / (int)Math.pow((double)l, (double)j) % l];
                  drr[b - j - 1] = dir;
               }

               if (this.validSupport(feetPos, false, drr)) {
                  class_2338 pos = feetPos;
                  this.addPos(feetPos);
                  class_2350[] var14 = drr;
                  int var10 = drr.length;

                  for(int var11 = 0; var11 < var10; ++var11) {
                     class_2350 dir = var14[var11];
                     pos = pos.method_10093(dir);
                     this.addPos(pos);
                  }

                  return true;
               }
            }

            return false;
         }
      } else {
         return true;
      }
   }

   private void addPos(class_2338 pos) {
      if (OLEPOSSUtils.replaceable(pos) && !this.positions.contains(pos)) {
         this.positions.add(0, pos);
      }

   }

   private void addBlocks(class_238 box, class_2350[] directions, int max) {
      for(int i = 0; i < max; ++i) {
         if (this.addBlocks2(box, directions, i)) {
            return;
         }
      }

   }

   private boolean validSupport(class_2338 feet, boolean useFeet, class_2350... dirs) {
      class_2338 pos = feet;
      if (!useFeet) {
         class_2350[] var5 = dirs;
         int var6 = dirs.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            class_2350 dir = var5[var7];
            pos = pos.method_10093(dir);
         }
      }

      return !this.positions.contains(pos) && OLEPOSSUtils.replaceable(pos) && !this.intersects(pos) ? SettingUtils.getPlaceData(pos, (p, d) -> {
         return this.placed.contains((Object)p) || this.positions.contains(p);
      }, (DoublePredicate)null).valid() : false;
   }

   private boolean intersects(class_2338 pos) {
      class_238 box = BoxUtils.get(pos);
      Iterator var3 = this.boxes.iterator();

      class_238 bb;
      do {
         if (!var3.hasNext()) {
            return EntityUtils.intersects(BoxUtils.get(pos), (entity) -> {
               return !(entity instanceof class_1542);
            });
         }

         bb = (class_238)var3.next();
      } while(!bb.method_994(box));

      return true;
   }

   private class_2350[] getDirections(class_243 motion) {
      double dir = RotationUtils.getYaw(new class_243(0.0D, 0.0D, 0.0D), motion, 0.0D);
      class_2350 moveDir = class_2350.method_10150(dir);
      return new class_2350[]{moveDir.method_10153(), moveDir, moveDir.method_10160(), moveDir.method_10170(), class_2350.field_11036, class_2350.field_11033};
   }

   private boolean validBlock(class_2338 pos) {
      if (!OLEPOSSUtils.replaceable(pos)) {
         return false;
      } else {
         PlaceData data = SettingUtils.getPlaceData(pos, (p, d) -> {
            return this.placed.contains((Object)p);
         }, (DoublePredicate)null);
         if (!data.valid()) {
            return false;
         } else if (!SettingUtils.inPlaceRange(data.pos())) {
            return false;
         } else {
            return !this.placed.contains((Object)pos);
         }
      }
   }

   private void updateAttack(boolean allowAction) {
      if ((Boolean)this.attack.get()) {
         if (!((double)(System.currentTimeMillis() - this.lastAttack) < 1000.0D / (Double)this.attackSpeed.get())) {
            class_1297 blocking = this.getBlocking();
            if (blocking != null) {
               if (!SettingUtils.shouldRotate(RotationType.Attacking) || this.attackRotate(blocking.method_5829(), -0.1D, "attacking")) {
                  if (allowAction) {
                     this.attackEntity(blocking);
                     if (SettingUtils.shouldRotate(RotationType.Attacking) && (Boolean)this.constantRotate.get()) {
                        this.end("attacking");
                     }

                     if ((Boolean)this.attackSwing.get()) {
                        this.clientSwing((SwingHand)this.attackHand.get(), class_1268.field_5808);
                     }

                     this.lastAttack = System.currentTimeMillis();
                  }
               }
            }
         }
      }
   }

   private void place(class_2338 pos, boolean allowAction) {
      if (this.validBlock(pos)) {
         if (this.result.amount() > 0) {
            if (this.blocksLeft > 0) {
               PlaceData data = SettingUtils.getPlaceData(pos, (p, d) -> {
                  return this.placed.contains((Object)p);
               }, (DoublePredicate)null);
               if (SettingUtils.shouldRotate(RotationType.BlockPlace)) {
                  Rotation rotation = SettingUtils.getRotation(data.pos(), data.dir(), data.pos().method_46558(), RotationType.BlockPlace);
                  class_243 vec = this.getRotationVec(data.pos(), (double)rotation.pitch());
                  if (vec != null) {
                     if (!this.rotateBlock(data.pos(), data.dir(), vec, RotationType.BlockPlace.withInstant((Boolean)this.instantRotate.get()), "placing")) {
                        return;
                     }
                  } else if (!this.rotateBlock(data, RotationType.BlockPlace.withInstant((Boolean)this.instantRotate.get()), "placing")) {
                     return;
                  }
               }

               if (allowAction) {
                  if (this.switched || this.hand != null || (this.switched = ((SwitchMode)this.switchMode.get()).swap(this.result.slot()))) {
                     this.placeBlock(this.hand, data.pos().method_46558(), data.dir(), data.pos());
                     this.setBlock(pos);
                     this.render.add(pos, (Double)this.renderTime.get());
                     if ((Boolean)this.placeSwing.get()) {
                        this.clientSwing((SwingHand)this.placeHand.get(), this.hand);
                     }

                     this.placed.add(pos, (Double)this.cooldown.get());
                     --this.blocksLeft;
                     --this.placesLeft;
                     if (SettingUtils.shouldRotate(RotationType.BlockPlace) && !(Boolean)this.constantRotate.get()) {
                        this.end("placing");
                     }

                  }
               }
            }
         }
      }
   }

   private class_243 getRotationVec(class_2338 pos, double pitch) {
      double yaw;
      if (this.movement.method_37268() > 0.0D) {
         switch((Scaffold.YawMode)this.smartYaw.get()) {
         case SemiLocked:
            yaw = (double)(Math.round(RotationUtils.getYaw(this.movement, class_243.field_1353, 0.0D) / 45.0D) * 45L);
            break;
         case Locked:
            yaw = (double)(Math.round(RotationUtils.getYaw(this.movement, class_243.field_1353, 0.0D) / 90.0D) * 90L);
            break;
         case Back:
            yaw = RotationUtils.getYaw(this.movement, class_243.field_1353, 0.0D);
            break;
         default:
            return null;
         }
      } else {
         yaw = (double)Managers.ROTATION.prevYaw;
         if (pitch == 90.0D) {
            --pitch;
         }
      }

      return BoxUtils.clamp(RotationUtils.rotationVec(yaw, pitch, BlackOut.mc.field_1724.method_33571(), BlackOut.mc.field_1724.method_33571().method_1022(pos.method_46558())), BoxUtils.get(pos));
   }

   private void setBlock(class_2338 pos) {
      class_1792 item = BlackOut.mc.field_1724.method_31548().method_5438(this.result.slot()).method_7909();
      if (item instanceof class_1747) {
         class_1747 block = (class_1747)item;
         Managers.PACKET.addToQueue((handler) -> {
            BlackOut.mc.field_1687.method_8501(pos, block.method_7711().method_9564());
            this.blockPlaceSound(pos, block);
         });
      }
   }

   private boolean validEntity(class_1297 entity) {
      if (entity instanceof class_1511 && System.currentTimeMillis() - this.lastAttack < 100L) {
         return false;
      } else {
         return !(entity instanceof class_1542);
      }
   }

   private class_1297 getBlocking() {
      class_1297 crystal = null;
      double lowest = 1000.0D;
      Iterator var4 = BlackOut.mc.field_1687.method_18112().iterator();

      while(true) {
         class_1297 entity;
         do {
            do {
               do {
                  if (!var4.hasNext()) {
                     return crystal;
                  }

                  entity = (class_1297)var4.next();
               } while(!(entity instanceof class_1511));
            } while(BlackOut.mc.field_1724.method_5739(entity) > 5.0F);
         } while(!SettingUtils.inAttackRange(entity.method_5829()));

         Iterator var6 = this.valids.iterator();

         while(var6.hasNext()) {
            class_2338 pos = (class_2338)var6.next();
            if (BoxUtils.get(pos).method_994(entity.method_5829())) {
               double dmg = DamageUtils.crystalDamage(BlackOut.mc.field_1724, BlackOut.mc.field_1724.method_5829(), entity.method_19538());
               if (dmg < lowest) {
                  crystal = entity;
                  lowest = dmg;
               }
            }
         }
      }
   }

   private void updatePlaces() {
      switch((Surround.PlaceDelayMode)this.placeDelayMode.get()) {
      case Ticks:
         if (this.placesLeft >= (Integer)this.places.get() || this.placeTickTimer >= (Integer)this.placeDelayT.get()) {
            this.placesLeft = (Integer)this.places.get();
            this.placeTickTimer = 0;
         }
         break;
      case Seconds:
         if (this.placesLeft >= (Integer)this.places.get() || this.placeTimer >= (Double)this.placeDelayS.get()) {
            this.placesLeft = (Integer)this.places.get();
            this.placeTimer = 0.0D;
         }
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

   private class_1268 getHand() {
      if (this.valid(Managers.PACKET.getStack())) {
         return class_1268.field_5808;
      } else {
         return this.valid(BlackOut.mc.field_1724.method_6079()) ? class_1268.field_5810 : null;
      }
   }

   public static enum TowerMode {
      Disabled,
      NCP,
      SlowNCP,
      TP;

      // $FF: synthetic method
      private static Scaffold.TowerMode[] $values() {
         return new Scaffold.TowerMode[]{Disabled, NCP, SlowNCP, TP};
      }
   }

   public static enum YawMode {
      Normal,
      SemiLocked,
      Locked,
      Back;

      // $FF: synthetic method
      private static Scaffold.YawMode[] $values() {
         return new Scaffold.YawMode[]{Normal, SemiLocked, Locked, Back};
      }
   }

   public static enum RenderMode {
      Placed,
      NotPlaced;

      // $FF: synthetic method
      private static Scaffold.RenderMode[] $values() {
         return new Scaffold.RenderMode[]{Placed, NotPlaced};
      }
   }

   public static record Render(class_2338 pos, long time) {
      public Render(class_2338 pos, long time) {
         this.pos = pos;
         this.time = time;
      }

      public class_2338 pos() {
         return this.pos;
      }

      public long time() {
         return this.time;
      }
   }
}
