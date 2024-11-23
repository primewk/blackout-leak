package bodevelopment.client.blackout.module.modules.combat.misc;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.enums.HoleType;
import bodevelopment.client.blackout.enums.RenderShape;
import bodevelopment.client.blackout.enums.RotationType;
import bodevelopment.client.blackout.enums.SwingHand;
import bodevelopment.client.blackout.enums.SwitchMode;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.RenderEvent;
import bodevelopment.client.blackout.event.events.TickEvent;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.modules.combat.defensive.Surround;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.randomstuff.BlackOutColor;
import bodevelopment.client.blackout.randomstuff.ExtrapolationMap;
import bodevelopment.client.blackout.randomstuff.FindResult;
import bodevelopment.client.blackout.randomstuff.Hole;
import bodevelopment.client.blackout.randomstuff.PlaceData;
import bodevelopment.client.blackout.randomstuff.timers.RenderList;
import bodevelopment.client.blackout.randomstuff.timers.TimerList;
import bodevelopment.client.blackout.util.BoxUtils;
import bodevelopment.client.blackout.util.EntityUtils;
import bodevelopment.client.blackout.util.HoleUtils;
import bodevelopment.client.blackout.util.OLEPOSSUtils;
import bodevelopment.client.blackout.util.SettingUtils;
import bodevelopment.client.blackout.util.render.Render3DUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;
import net.minecraft.class_1268;
import net.minecraft.class_1542;
import net.minecraft.class_1747;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_742;

public class HoleFill extends Module {
   private final SettingGroup sgGeneral = this.addGroup("General");
   private final SettingGroup sgSelf = this.addGroup("Self");
   private final SettingGroup sgPlacing = this.addGroup("Placing");
   private final SettingGroup sgRender = this.addGroup("Render");
   private final SettingGroup sgHole = this.addGroup("Hole");
   private final Setting<Boolean> near;
   private final Setting<Double> nearDistance;
   private final Setting<Integer> nearExt;
   private final Setting<Integer> selfExt;
   private final Setting<Boolean> above;
   private final Setting<Boolean> ignoreHole;
   private final Setting<Boolean> ignoreSelfHole;
   private final Setting<Boolean> selfAbove;
   private final Setting<Double> selfDistance;
   private final Setting<Boolean> efficient;
   private final Setting<Boolean> pauseEat;
   private final Setting<SwitchMode> switchMode;
   private final Setting<Surround.PlaceDelayMode> placeDelayMode;
   private final Setting<Integer> placeDelayT;
   private final Setting<Double> placeDelayS;
   private final Setting<Integer> places;
   private final Setting<Double> cooldown;
   private final Setting<List<class_2248>> blocks;
   private final Setting<Integer> boxExtrapolation;
   private final Setting<Boolean> single;
   private final Setting<Boolean> doubleHole;
   private final Setting<Boolean> quad;
   private final Setting<Boolean> placeSwing;
   private final Setting<SwingHand> placeHand;
   private final Setting<Double> renderTime;
   private final Setting<Double> fadeTime;
   private final Setting<RenderShape> renderShape;
   private final Setting<BlackOutColor> lineColor;
   private final Setting<BlackOutColor> sideColor;
   private final List<class_2338> holes;
   private final TimerList<class_2338> timers;
   private final RenderList<class_2338> render;
   private final ExtrapolationMap nearPosition;
   private final ExtrapolationMap boxes;
   private class_1268 hand;
   private int blocksLeft;
   private int placesLeft;
   private FindResult result;
   private boolean switched;
   private boolean shouldIgnoreSelf;
   private int tickTimer;
   private long lastTime;
   public static boolean placing = false;
   private class_2338 prevPos;
   private class_2338 prevHole;
   private long holeTime;

   public HoleFill() {
      super("Hole Fill", "Automatically is a cunt to your enemies.", SubCategory.MISC_COMBAT, true);
      this.near = this.sgGeneral.b("Near", true, ".");
      this.nearDistance = this.sgGeneral.d("Near Distance", 3.0D, 0.0D, 10.0D, 0.1D, ".");
      this.nearExt = this.sgGeneral.i("Extrapolation", 5, 0, 20, 1, ".");
      this.selfExt = this.sgGeneral.i("Self Extrapolation", 2, 0, 20, 1, ".");
      this.above = this.sgGeneral.b("Above", true, "Only places if target is above the hole.");
      this.ignoreHole = this.sgGeneral.b("Ignore Hole", true, "Doesn't place if enemy is in a hole.");
      this.ignoreSelfHole = this.sgSelf.b("Ignore Self Hole", true, "Allows filling near you if you are in a hole.");
      this.selfAbove = this.sgSelf.b("Self Above", true, "Allows filling near you if you aren't above the hole.");
      this.selfDistance = this.sgSelf.d("Self Distance", 3.0D, 0.0D, 10.0D, 0.1D, "Holes must be out of this range.");
      this.efficient = this.sgSelf.b("Efficient", true, "Only places if the hole is closer to target than to us.");
      this.pauseEat = this.sgPlacing.b("Pause Eat", false, ".");
      this.switchMode = this.sgPlacing.e("Switch Mode", SwitchMode.Silent, "Method of switching. Silent is the most reliable but delays crystals on some servers.");
      this.placeDelayMode = this.sgPlacing.e("Place Delay Mode", Surround.PlaceDelayMode.Ticks, "Method of switching. Silent is the most reliable but delays crystals on some servers.");
      this.placeDelayT = this.sgPlacing.i("Place Tick Delay", 1, 0, 20, 1, "Tick delay between places.", () -> {
         return this.placeDelayMode.get() == Surround.PlaceDelayMode.Ticks;
      });
      this.placeDelayS = this.sgPlacing.d("Place Delay", 0.1D, 0.0D, 1.0D, 0.01D, "Delay between places.", () -> {
         return this.placeDelayMode.get() == Surround.PlaceDelayMode.Seconds;
      });
      this.places = this.sgPlacing.i("Places", 1, 1, 20, 1, "How many blocks to place each time.");
      this.cooldown = this.sgPlacing.d("Cooldown", 0.3D, 0.0D, 1.0D, 0.01D, "Waits x seconds before trying to place at the same position if there is more than 1 missing block.");
      this.blocks = this.sgPlacing.bl("Blocks", "Blocks to use.", class_2246.field_10540);
      this.boxExtrapolation = this.sgPlacing.i("Box Extrapolation", 1, 0, 20, 1, "Enemy hitbox extrapolation");
      this.single = this.sgHole.b("Single", true, "Fills 1x1 holes.");
      this.doubleHole = this.sgHole.b("Double", true, "Fills 2x1 holes.");
      this.quad = this.sgHole.b("Quad", true, "Fills 2x2 holes.");
      this.placeSwing = this.sgRender.b("Swing", true, "Renders swing animation when placing a block.");
      SettingGroup var10001 = this.sgRender;
      SwingHand var10003 = SwingHand.RealHand;
      Setting var10005 = this.placeSwing;
      Objects.requireNonNull(var10005);
      this.placeHand = var10001.e("Swing Hand", var10003, "Which hand should be swung.", var10005::get);
      this.renderTime = this.sgRender.d("Render Time", 0.3D, 0.0D, 5.0D, 0.1D, "How long the box should remain in full alpha.");
      this.fadeTime = this.sgRender.d("Fade Time", 1.0D, 0.0D, 5.0D, 0.1D, "How long the box should remain in full alpha.");
      this.renderShape = this.sgRender.e("Render Shape", RenderShape.Full, "Which parts of boxes should be rendered.");
      this.lineColor = this.sgRender.c("Line Color", new BlackOutColor(255, 0, 0, 255), ".");
      this.sideColor = this.sgRender.c("Side Color", new BlackOutColor(255, 0, 0, 50), ".");
      this.holes = new ArrayList();
      this.timers = new TimerList(true);
      this.render = RenderList.getList(false);
      this.nearPosition = new ExtrapolationMap();
      this.boxes = new ExtrapolationMap();
      this.hand = null;
      this.blocksLeft = 0;
      this.placesLeft = 0;
      this.result = null;
      this.switched = false;
      this.shouldIgnoreSelf = false;
      this.tickTimer = 0;
      this.lastTime = 0L;
      this.prevPos = class_2338.field_10980;
      this.prevHole = class_2338.field_10980;
      this.holeTime = 0L;
   }

   @Event
   public void onTick(TickEvent.Post event) {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null) {
         class_2338 pos = new class_2338(BlackOut.mc.field_1724.method_31477(), (int)Math.round(BlackOut.mc.field_1724.method_23318()), BlackOut.mc.field_1724.method_31479());
         if (!pos.equals(this.prevPos) && HoleUtils.inHole(this.prevPos) && !HoleUtils.inHole(pos)) {
            this.prevHole = this.prevPos;
            this.holeTime = System.currentTimeMillis();
         }

         this.prevPos = pos;
         this.update();
      }
   }

   @Event
   public void onRender(RenderEvent.World.Post event) {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null) {
         this.render.update((pos, time, d) -> {
            double progress = 1.0D - Math.max(time - (Double)this.renderTime.get(), 0.0D) / (Double)this.fadeTime.get();
            Render3DUtils.box(BoxUtils.get(pos), ((BlackOutColor)this.sideColor.get()).alphaMulti(progress), ((BlackOutColor)this.lineColor.get()).alphaMulti(progress), (RenderShape)this.renderShape.get());
         });
      }
   }

   private void update() {
      ++this.tickTimer;
      this.updateMaps();
      this.updateHoles();
      this.result = ((SwitchMode)this.switchMode.get()).find(this::valid);
      this.updatePlaces();
      this.updatePlacing();
   }

   private void updatePlacing() {
      this.blocksLeft = Math.min(this.placesLeft, this.result.amount());
      this.hand = OLEPOSSUtils.getHand(this::valid);
      this.switched = false;
      if (!BlackOut.mc.field_1724.method_6115() || !(Boolean)this.pauseEat.get()) {
         this.holes.stream().sorted(Comparator.comparingDouble((pos) -> {
            return pos.method_46558().method_1022(BlackOut.mc.field_1724.method_33571());
         })).forEach(this::place);
         if (this.switched && this.hand == null) {
            ((SwitchMode)this.switchMode.get()).swapBack();
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

   private void updateMaps() {
      this.nearPosition.update((player) -> {
         return player == BlackOut.mc.field_1724 ? (Integer)this.selfExt.get() : (Integer)this.nearExt.get();
      });
      this.boxes.update((player) -> {
         return player == BlackOut.mc.field_1724 ? 0 : (Integer)this.boxExtrapolation.get();
      });
   }

   private void updateHoles() {
      this.holes.clear();
      int range = (int)Math.ceil(SettingUtils.maxPlaceRange() + 1.0D);
      class_2338 p = class_2338.method_49638(BlackOut.mc.field_1724.method_33571());
      List<Hole> holeList = new ArrayList();

      for(int x = -range; x <= range; ++x) {
         for(int y = -range; y <= range; ++y) {
            for(int z = -range; z <= range; ++z) {
               Hole hole = HoleUtils.getHole(p.method_10069(x, y, z));
               if (hole.type != HoleType.NotHole && ((Boolean)this.single.get() || hole.type != HoleType.Single) && ((Boolean)this.doubleHole.get() || hole.type != HoleType.DoubleX && hole.type != HoleType.DoubleZ) && ((Boolean)this.quad.get() || hole.type != HoleType.Quad)) {
                  holeList.add(hole);
               }
            }
         }
      }

      holeList.forEach((holex) -> {
         if (this.validHole(holex)) {
            Stream var10000 = Arrays.stream(holex.positions).filter(this::validPos);
            List var10001 = this.holes;
            Objects.requireNonNull(var10001);
            var10000.forEach(var10001::add);
         }
      });
   }

   private boolean validPos(class_2338 pos) {
      if (this.timers.contains((Object)pos)) {
         return false;
      } else if (!OLEPOSSUtils.replaceable(pos)) {
         return false;
      } else {
         PlaceData data = SettingUtils.getPlaceData(pos);
         return !data.valid() ? false : SettingUtils.inPlaceRange(data.pos());
      }
   }

   private boolean validHole(Hole hole) {
      double pDist = (this.nearPosition.contains(BlackOut.mc.field_1724) ? this.feet(this.nearPosition.get(BlackOut.mc.field_1724)) : BlackOut.mc.field_1724.method_19538()).method_1022(hole.middle);
      if (this.selfNearCheck(hole)) {
         return false;
      } else {
         class_2338[] var4 = hole.positions;
         int var5 = var4.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            class_2338 pos = var4[var6];
            if (EntityUtils.intersects(BoxUtils.get(pos), (entity) -> {
               return !entity.method_7325() && !(entity instanceof class_1542);
            })) {
               return false;
            }
         }

         Iterator var8 = BlackOut.mc.field_1687.method_18456().iterator();

         class_742 player;
         do {
            if (!var8.hasNext()) {
               return false;
            }

            player = (class_742)var8.next();
         } while(player.method_7325() || player == BlackOut.mc.field_1724 || player.method_6032() <= 0.0F || Managers.FRIENDS.isFriend(player) || !this.nearCheck(player, hole, pDist));

         return true;
      }
   }

   private boolean selfNearCheck(Hole hole) {
      if (System.currentTimeMillis() - this.holeTime < 500L && OLEPOSSUtils.contains(hole.positions, this.prevHole)) {
         return false;
      } else {
         class_2338 pos = new class_2338(BlackOut.mc.field_1724.method_31477(), (int)Math.round(BlackOut.mc.field_1724.method_23318()), BlackOut.mc.field_1724.method_31479());
         if ((Boolean)this.ignoreSelfHole.get() && (HoleUtils.inHole(BlackOut.mc.field_1724.method_24515()) || OLEPOSSUtils.collidable(pos))) {
            this.shouldIgnoreSelf = true;
            return false;
         } else if ((Boolean)this.selfAbove.get() && BlackOut.mc.field_1724.method_23318() <= hole.middle.field_1351) {
            this.shouldIgnoreSelf = true;
            return false;
         } else {
            this.shouldIgnoreSelf = false;
            return BlackOut.mc.field_1724.method_19538().method_1022(hole.middle) <= (Double)this.selfDistance.get();
         }
      }
   }

   private boolean nearCheck(class_742 player, Hole hole, double pDist) {
      if (!(Boolean)this.near.get()) {
         return false;
      } else if ((Boolean)this.ignoreHole.get() && this.inHole(player.method_19538()) && this.inHole(BoxUtils.feet(this.nearPosition.get(player)))) {
         return false;
      } else if ((Boolean)this.above.get() && this.nearPosition.get(player).field_1322 <= hole.middle.field_1351 && player.method_23318() <= hole.middle.field_1351) {
         return false;
      } else {
         double eDist = (this.nearPosition.contains(player) ? this.feet(this.nearPosition.get(player)) : player.method_19538()).method_1022(hole.middle.method_1031(0.0D, 1.0D, 0.0D));
         if (eDist > (Double)this.nearDistance.get()) {
            return false;
         } else {
            return System.currentTimeMillis() - this.holeTime < 500L && OLEPOSSUtils.contains(hole.positions, this.prevHole) || this.shouldIgnoreSelf || !(Boolean)this.efficient.get() || !(pDist <= eDist);
         }
      }
   }

   private boolean inHole(class_243 vec) {
      class_2338 pos = new class_2338((int)Math.floor(vec.method_10216()), (int)Math.round(vec.method_10214()), (int)Math.floor(vec.method_10215()));
      return HoleUtils.inHole(pos) || OLEPOSSUtils.collidable(pos);
   }

   private void updatePlaces() {
      switch((Surround.PlaceDelayMode)this.placeDelayMode.get()) {
      case Ticks:
         if (this.placesLeft >= (Integer)this.places.get() || this.tickTimer >= (Integer)this.placeDelayT.get()) {
            this.placesLeft = (Integer)this.places.get();
            this.tickTimer = 0;
         }
         break;
      case Seconds:
         if (this.placesLeft >= (Integer)this.places.get() || (double)(System.currentTimeMillis() - this.lastTime) >= (Double)this.placeDelayS.get() * 1000.0D) {
            this.placesLeft = (Integer)this.places.get();
            this.lastTime = System.currentTimeMillis();
         }
      }

   }

   private void place(class_2338 pos) {
      if (this.blocksLeft > 0) {
         PlaceData data = SettingUtils.getPlaceData(pos);
         if (data != null && data.valid()) {
            placing = true;
            if (!SettingUtils.shouldRotate(RotationType.BlockPlace) || this.rotateBlock(data, RotationType.BlockPlace, "placing")) {
               if (this.switched || this.hand != null || (this.switched = ((SwitchMode)this.switchMode.get()).swap(this.result.slot()))) {
                  this.render.add(pos, (Double)this.renderTime.get() + (Double)this.fadeTime.get());
                  this.timers.add(pos, (Double)this.cooldown.get());
                  this.placeBlock(this.hand, data.pos().method_46558(), data.dir(), data.pos());
                  if ((Boolean)this.placeSwing.get()) {
                     this.clientSwing((SwingHand)this.placeHand.get(), this.hand);
                  }

                  --this.blocksLeft;
                  --this.placesLeft;
                  if (SettingUtils.shouldRotate(RotationType.BlockPlace)) {
                     this.end("placing");
                  }

               }
            }
         }
      }
   }

   private class_243 feet(class_238 box) {
      return new class_243((box.field_1323 + box.field_1320) / 2.0D, box.field_1322, (box.field_1321 + box.field_1324) / 2.0D);
   }
}
