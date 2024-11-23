package bodevelopment.client.blackout.module.modules.misc;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.enums.RenderShape;
import bodevelopment.client.blackout.enums.RotationType;
import bodevelopment.client.blackout.enums.SwingHand;
import bodevelopment.client.blackout.enums.SwitchMode;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.PacketEvent;
import bodevelopment.client.blackout.event.events.RenderEvent;
import bodevelopment.client.blackout.event.events.TickEvent;
import bodevelopment.client.blackout.interfaces.functional.EpicInterface;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.modules.client.settings.SwingSettings;
import bodevelopment.client.blackout.module.modules.combat.offensive.AutoMine;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.randomstuff.BlackOutColor;
import bodevelopment.client.blackout.randomstuff.FindResult;
import bodevelopment.client.blackout.randomstuff.Pair;
import bodevelopment.client.blackout.util.BlockUtils;
import bodevelopment.client.blackout.util.BoxUtils;
import bodevelopment.client.blackout.util.InvUtils;
import bodevelopment.client.blackout.util.SettingUtils;
import bodevelopment.client.blackout.util.render.Render3DUtils;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.class_1268;
import net.minecraft.class_1799;
import net.minecraft.class_2189;
import net.minecraft.class_2246;
import net.minecraft.class_2248;
import net.minecraft.class_2338;
import net.minecraft.class_2350;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_265;
import net.minecraft.class_2680;
import net.minecraft.class_2846;
import net.minecraft.class_2868;
import net.minecraft.class_3532;
import net.minecraft.class_2846.class_2847;

public class Nuker extends Module {
   private final SettingGroup sgGeneral = this.addGroup("General");
   private final SettingGroup sgSpeed = this.addGroup("Speed");
   private final SettingGroup sgRender = this.addGroup("Render");
   private final Setting<Boolean> pauseEat;
   private final Setting<Boolean> packet;
   private final Setting<SwitchMode> pickaxeSwitch;
   private final Setting<Boolean> allowInventory;
   private final Setting<Boolean> resetOnSwitch;
   private final Setting<Boolean> ncpProgress;
   private final Setting<List<class_2248>> blocks;
   private final Setting<Boolean> down;
   private final Setting<Boolean> creative;
   private final Setting<Double> speed;
   private final Setting<Boolean> onGroundCheck;
   private final Setting<Boolean> effectCheck;
   private final Setting<Boolean> waterCheck;
   private final Setting<Integer> maxInstants;
   private final Setting<Boolean> mineStartSwing;
   private final Setting<Boolean> mineEndSwing;
   private final Setting<SwingHand> mineHand;
   private final Setting<Boolean> animationColor;
   private final Setting<AutoMine.AnimationMode> animationMode;
   private final Setting<Double> animationExponent;
   private final Setting<RenderShape> renderShape;
   private final Setting<BlackOutColor> lineStartColor;
   private final Setting<BlackOutColor> sideStartColor;
   private final Setting<BlackOutColor> lineEndColor;
   private final Setting<BlackOutColor> sideEndColor;
   private final List<Pair<class_2338, Double>> instants;
   private class_2338 minePos;
   private class_2338 prevPos;
   private boolean startedThisTick;
   private double progress;
   private int minedFor;
   private double prevProgress;
   private double currentProgress;
   private boolean shouldRestart;
   public boolean started;
   private class_2338 ended;

   public Nuker() {
      super("Nuker", "Breaks blocks.", SubCategory.MISC, true);
      this.pauseEat = this.sgGeneral.b("Pause Eat", false, "Pauses when eating");
      this.packet = this.sgGeneral.b("Packet", true, "Doesn't remove the block client side.");
      this.pickaxeSwitch = this.sgGeneral.e("Pickaxe Switch", SwitchMode.InvSwitch, "Method of switching. InvSwitch is used in most clients.");
      this.allowInventory = this.sgGeneral.b("Allow Inventory", false, ".", () -> {
         return ((SwitchMode)this.pickaxeSwitch.get()).inventory;
      });
      this.resetOnSwitch = this.sgGeneral.b("Reset On Switch", true, "Resets mining when switched held item.");
      this.ncpProgress = this.sgGeneral.b("NCP Progress", true, "Uses ncp mining progress checks.");
      this.blocks = this.sgGeneral.bl("Blocks", ".");
      this.down = this.sgGeneral.b("Down", false, "Allows mining down.");
      this.creative = this.sgSpeed.b("Creative", false, ".");
      this.speed = this.sgSpeed.d("Speed", 1.0D, 0.0D, 2.0D, 0.05D, "Vanilla speed multiplier.", () -> {
         return !(Boolean)this.creative.get();
      });
      this.onGroundCheck = this.sgSpeed.b("On Ground Check", true, "Mines 5x slower when not on ground.", () -> {
         return !(Boolean)this.creative.get();
      });
      this.effectCheck = this.sgSpeed.b("Effect Check", true, "Modifies mining speed depending on haste and mining fatigue.", () -> {
         return !(Boolean)this.creative.get();
      });
      this.waterCheck = this.sgSpeed.b("Water Check", true, "Mines 5x slower while submerged in water.", () -> {
         return !(Boolean)this.creative.get();
      });
      this.maxInstants = this.sgSpeed.i("Max Instants", 5, 1, 20, 1, "Maximum amount if instant mines per tick.");
      this.mineStartSwing = this.sgRender.b("Mine Start Swing", false, "Renders swing animation when starting to mine.");
      this.mineEndSwing = this.sgRender.b("Mine End Swing", false, "Renders swing animation when ending mining.");
      this.mineHand = this.sgRender.e("Mine Hand", SwingHand.RealHand, "Which hand should be swung.", () -> {
         return (Boolean)this.mineStartSwing.get() || (Boolean)this.mineEndSwing.get();
      });
      this.animationColor = this.sgRender.b("Animation Color", true, "Changes color smoothly.");
      this.animationMode = this.sgRender.e("Animation Mode", AutoMine.AnimationMode.Full, ".");
      this.animationExponent = this.sgRender.d("Animation Exponent", 1.0D, 0.0D, 10.0D, 0.1D, ".");
      this.renderShape = this.sgRender.e("Render Shape", RenderShape.Full, "Which parts should be rendered.");
      this.lineStartColor = this.sgRender.c("Line Start Color", new BlackOutColor(255, 0, 0, 0), ".");
      this.sideStartColor = this.sgRender.c("Side Start Color", new BlackOutColor(255, 0, 0, 0), ".");
      this.lineEndColor = this.sgRender.c("Line End Color", new BlackOutColor(255, 0, 0, 255), ".");
      this.sideEndColor = this.sgRender.c("Side End Color", new BlackOutColor(255, 0, 0, 50), ".");
      this.instants = new ArrayList();
      this.minePos = null;
      this.prevPos = null;
      this.startedThisTick = false;
      this.progress = 0.0D;
      this.minedFor = 0;
      this.prevProgress = 0.0D;
      this.currentProgress = 0.0D;
      this.shouldRestart = false;
      this.started = false;
      this.ended = class_2338.field_10980;
   }

   @Event
   public void onSent(PacketEvent.Sent event) {
      if ((Boolean)this.resetOnSwitch.get() && event.packet instanceof class_2868) {
         this.shouldRestart = true;
      }

   }

   @Event
   public void onRender(RenderEvent.World.Post event) {
      if (!this.started) {
         this.prevProgress = 0.0D;
         this.currentProgress = 0.0D;
      }

      this.updateRender(event.tickDelta);
   }

   @Event
   public void onTick(TickEvent.Pre event) {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null) {
         this.startedThisTick = false;
         if (this.shouldUpdatePos()) {
            this.minePos = this.findPos();
         }

         if (this.minePos != null && (!SettingUtils.inMineRange(this.minePos) || SettingUtils.getPlaceOnDirection(this.minePos) == null)) {
            this.started = false;
            this.minePos = null;
         }

         if (this.instants.isEmpty()) {
            this.updateStartOrAbort(this.minePos);
         } else {
            this.instants.forEach((pair) -> {
               this.updateStartOrAbort((class_2338)pair.method_15442());
            });
         }

         this.updateMining();
         this.prevProgress = this.currentProgress;
         this.currentProgress = this.getProgress();
      }
   }

   private boolean shouldUpdatePos() {
      return this.minePos == null || !((List)this.blocks.get()).contains(BlackOut.mc.field_1687.method_8320(this.minePos).method_26204());
   }

   private class_2338 findPos() {
      this.instants.clear();
      class_2338 middle = class_2338.method_49638(BlackOut.mc.field_1724.method_33571());
      int rad = (int)Math.ceil(SettingUtils.maxMineRange());
      class_2338 bestPos = null;
      double bestDist = -1.0D;
      double bestDelta = 0.0D;
      int feetY = (int)Math.round(BlackOut.mc.field_1724.method_23318());

      for(int x = -rad; x <= rad; ++x) {
         for(int y = -rad; y <= rad; ++y) {
            for(int z = -rad; z <= rad; ++z) {
               class_2338 pos = middle.method_10069(x, y, z);
               if ((Boolean)this.down.get() || pos.method_10264() >= feetY) {
                  class_2680 state = BlackOut.mc.field_1687.method_8320(pos);
                  if (((List)this.blocks.get()).contains(state.method_26204())) {
                     double delta = this.getBestDelta(pos);
                     boolean isInstant = (Boolean)this.creative.get() || delta >= 1.0D;
                     if (!(delta < bestDelta) || isInstant) {
                        double dist = BlackOut.mc.field_1724.method_33571().method_1022(pos.method_46558());
                        if ((!(dist > bestDist) || !(bestDist > 0.0D) || isInstant) && SettingUtils.getPlaceOnDirection(pos) != null && SettingUtils.inMineRange(pos)) {
                           if (isInstant) {
                              if (this.instants.size() < (Integer)this.maxInstants.get()) {
                                 this.instants.add(new Pair(pos, dist));
                              } else {
                                 for(int i = 0; i < this.instants.size(); ++i) {
                                    Pair<class_2338, Double> pair = (Pair)this.instants.get(i);
                                    if (!((Double)pair.method_15441() < dist)) {
                                       this.instants.remove(i);
                                       this.instants.add(new Pair(pos, dist));
                                       break;
                                    }
                                 }
                              }
                           }

                           bestPos = pos;
                           bestDist = delta > bestDelta ? -1.0D : dist;
                           bestDelta = delta;
                        }
                     }
                  }
               }
            }
         }
      }

      if (this.instants.size() > 0) {
         return (class_2338)((Pair)this.instants.get(0)).method_15442();
      } else {
         return bestPos;
      }
   }

   private double getProgress() {
      if (this.minePos == null) {
         return 0.0D;
      } else {
         class_1799 itemStack = this.findBestSlot((stack) -> {
            return BlockUtils.getBlockBreakingDelta(stack, BlackOut.mc.field_1687.method_8320(this.minePos), (Boolean)this.effectCheck.get(), (Boolean)this.waterCheck.get(), (Boolean)this.onGroundCheck.get());
         }).stack();
         return (Boolean)this.ncpProgress.get() ? (double)this.minedFor / (1.0D / BlockUtils.getBlockBreakingDelta(itemStack, BlackOut.mc.field_1687.method_8320(this.minePos), (Boolean)this.effectCheck.get(), (Boolean)this.waterCheck.get(), (Boolean)this.onGroundCheck.get())) : this.progress;
      }
   }

   private double getBestDelta(class_2338 pos) {
      double best = 0.0D;

      for(int i = ((SwitchMode)this.pickaxeSwitch.get()).hotbar ? 0 : 9; i < (((SwitchMode)this.pickaxeSwitch.get()).inventory && (Boolean)this.allowInventory.get() ? BlackOut.mc.field_1724.method_31548().method_5439() : 9); ++i) {
         class_1799 stack = BlackOut.mc.field_1724.method_31548().method_5438(i);
         best = Math.max(best, BlockUtils.getBlockBreakingDelta(stack, BlackOut.mc.field_1687.method_8320(pos), (Boolean)this.effectCheck.get(), (Boolean)this.waterCheck.get(), (Boolean)this.onGroundCheck.get()));
      }

      return best;
   }

   private void updateStartOrAbort(class_2338 pos) {
      if (this.shouldRestart) {
         if (pos != null) {
            this.abort(pos);
         }

         this.shouldRestart = false;
         this.started = false;
      }

      if (pos == null) {
         if (this.prevPos != null && !this.prevPos.equals(this.ended)) {
            this.abort(this.prevPos);
         }
      } else {
         if (!pos.equals(this.prevPos)) {
            this.started = false;
         }

         if (!this.started && !this.paused()) {
            class_2350 dir = SettingUtils.getPlaceOnDirection(pos);
            if (!SettingUtils.startMineRot() || this.rotateBlock(pos, dir, pos.method_46558(), RotationType.Mining, "mining")) {
               this.start(pos);
            }
         }
      }

      this.prevPos = pos;
   }

   private boolean paused() {
      return (Boolean)this.pauseEat.get() && BlackOut.mc.field_1724.method_6115();
   }

   private void start(class_2338 pos) {
      class_2350 dir = SettingUtils.getPlaceOnDirection(pos);
      if (dir != null) {
         if (this.prevPos != null && !this.prevPos.equals(pos) && !this.prevPos.equals(this.ended)) {
            this.abort(this.prevPos);
         }

         boolean holding = (Boolean)this.creative.get() || BlockUtils.getBlockBreakingDelta(pos, Managers.PACKET.getStack(), (Boolean)this.effectCheck.get(), (Boolean)this.waterCheck.get(), (Boolean)this.onGroundCheck.get()) >= 1.0D;
         FindResult result = this.findBestSlot((stack) -> {
            return BlockUtils.getBlockBreakingDelta(pos, stack, (Boolean)this.effectCheck.get(), (Boolean)this.waterCheck.get(), (Boolean)this.onGroundCheck.get());
         });
         boolean canInstant = (Boolean)this.creative.get() || BlockUtils.getBlockBreakingDelta(pos, result.stack(), (Boolean)this.effectCheck.get(), (Boolean)this.waterCheck.get(), (Boolean)this.onGroundCheck.get()) >= 1.0D;
         if (!holding && !canInstant) {
            this.currentProgress = 0.0D;
            this.prevProgress = 0.0D;
            this.started = true;
            this.startedThisTick = true;
            this.progress = 0.0D;
            this.minedFor = 0;
         } else {
            this.minePos = null;
            this.ended = pos;
            if (!(Boolean)this.packet.get()) {
               BlackOut.mc.field_1687.method_8501(pos, class_2246.field_10124.method_9564());
            }

            Managers.BLOCK.set(pos, class_2246.field_10124, true, true);
         }

         if (canInstant && !holding) {
            ((SwitchMode)this.pickaxeSwitch.get()).swap(result.slot());
         }

         this.sendSequenced((s) -> {
            return new class_2846(class_2847.field_12968, pos, dir, s);
         });
         SettingUtils.mineSwing(SwingSettings.MiningSwingState.Start);
         if (canInstant && !holding) {
            ((SwitchMode)this.pickaxeSwitch.get()).swapBack();
         }

         this.end("mining");
         if ((Boolean)this.mineStartSwing.get()) {
            this.clientSwing((SwingHand)this.mineHand.get(), class_1268.field_5808);
         }

      }
   }

   private void updateMining() {
      if (this.minePos != null && !this.startedThisTick) {
         boolean holding = this.itemMinedCheck(Managers.PACKET.getStack());
         int slot = this.findBestSlot((stack) -> {
            return BlockUtils.getBlockBreakingDelta(this.minePos, stack, (Boolean)this.effectCheck.get(), (Boolean)this.waterCheck.get(), (Boolean)this.onGroundCheck.get());
         }).slot();
         class_1799 bestStack = holding ? Managers.PACKET.getStack() : BlackOut.mc.field_1724.method_31548().method_5438(slot);
         if ((Boolean)this.ncpProgress.get()) {
            ++this.minedFor;
         } else {
            this.progress += BlockUtils.getBlockBreakingDelta(this.minePos, bestStack, (Boolean)this.effectCheck.get(), (Boolean)this.waterCheck.get(), (Boolean)this.onGroundCheck.get());
         }

         if (this.itemMinedCheck(bestStack)) {
            this.mineEndUpdate(holding, slot);
         } else if (this.almostMined(bestStack) && SettingUtils.endMineRot()) {
            this.preRotate();
         }

      }
   }

   private void mineEndUpdate(boolean holding, int slot) {
      if (!this.paused()) {
         this.endMining(holding, slot);
      }

   }

   private void endMining(boolean holding, int slot) {
      if (!(this.getBlock(this.minePos) instanceof class_2189)) {
         class_2350 dir = SettingUtils.getPlaceOnDirection(this.minePos);
         if (dir != null) {
            if (!SettingUtils.endMineRot() || this.rotateBlock(this.minePos, dir, this.minePos.method_46558(), RotationType.Mining, "mining")) {
               boolean switched = false;
               if (holding || (switched = ((SwitchMode)this.pickaxeSwitch.get()).swap(slot))) {
                  this.ended = this.minePos;
                  this.sendSequenced((s) -> {
                     return new class_2846(class_2847.field_12973, this.minePos, dir, s);
                  });
                  SettingUtils.mineSwing(SwingSettings.MiningSwingState.End);
                  if ((Boolean)this.mineEndSwing.get()) {
                     this.clientSwing((SwingHand)this.mineHand.get(), class_1268.field_5808);
                  }

                  if (!(Boolean)this.packet.get()) {
                     BlackOut.mc.field_1687.method_8501(this.minePos, class_2246.field_10124.method_9564());
                  }

                  Managers.BLOCK.set(this.minePos, class_2246.field_10124, true, true);
                  Managers.ENTITY.addSpawning(this.minePos);
                  this.started = false;
                  this.minePos = null;
                  this.end("mining");
                  if (switched) {
                     ((SwitchMode)this.pickaxeSwitch.get()).swapBack();
                  }

               }
            }
         }
      }
   }

   private void preRotate() {
      if (!(this.getBlock(this.minePos) instanceof class_2189)) {
         if (SettingUtils.inMineRange(this.minePos)) {
            class_2350 dir = SettingUtils.getPlaceOnDirection(this.minePos);
            if (dir != null) {
               this.rotateBlock(this.minePos, dir, this.minePos.method_46558(), RotationType.Mining, "mining");
            }
         }
      }
   }

   private class_2248 getBlock(class_2338 pos) {
      return Managers.BLOCK.blockState(pos).method_26204();
   }

   private FindResult findBestSlot(EpicInterface<class_1799, Double> test) {
      return InvUtils.findBest(((SwitchMode)this.pickaxeSwitch.get()).hotbar, ((SwitchMode)this.pickaxeSwitch.get()).inventory && (Boolean)this.allowInventory.get(), test);
   }

   private boolean itemMinedCheck(class_1799 stack) {
      if ((Boolean)this.ncpProgress.get()) {
         return (double)this.minedFor * (Double)this.speed.get() >= Math.ceil(1.0D / BlockUtils.getBlockBreakingDelta(this.minePos, stack, (Boolean)this.effectCheck.get(), (Boolean)this.waterCheck.get(), (Boolean)this.onGroundCheck.get()));
      } else {
         return this.progress * (Double)this.speed.get() >= 1.0D;
      }
   }

   private boolean almostMined(class_1799 stack) {
      if (this.getBlock(this.minePos) instanceof class_2189) {
         return false;
      } else if (!SettingUtils.inMineRange(this.minePos)) {
         return false;
      } else if (SettingUtils.getPlaceOnDirection(this.minePos) == null) {
         return false;
      } else if ((Boolean)this.ncpProgress.get()) {
         return (double)(this.minedFor + 2) >= Math.ceil(1.0D / BlockUtils.getBlockBreakingDelta(this.minePos, stack, (Boolean)this.effectCheck.get(), (Boolean)this.waterCheck.get(), (Boolean)this.onGroundCheck.get()));
      } else {
         return this.progress >= 0.9D;
      }
   }

   private void updateRender(float tickDelta) {
      double p = this.currentProgress;
      if (this.minePos != null && this.prevProgress < p && p < Double.POSITIVE_INFINITY) {
         p = class_3532.method_16436((double)tickDelta, this.prevProgress, p);
         p = class_3532.method_15350(p, 0.0D, 1.0D);
         p = 1.0D - Math.pow(1.0D - p, (Double)this.animationExponent.get());
         p = Math.min(p / 2.0D, 0.5D);
         BlackOutColor sideColor = this.getSideColor(p * 2.0D);
         BlackOutColor lineColor = this.getLineColor(p * 2.0D);
         class_238 box = this.getBox(p, (AutoMine.AnimationMode)this.animationMode.get());
         if (box != null) {
            Render3DUtils.box(box, sideColor, lineColor, (RenderShape)this.renderShape.get());
         }
      }

   }

   private class_238 getBox(double p, AutoMine.AnimationMode mode) {
      double up = 0.5D;
      double down = 0.5D;
      double sides = 0.5D;
      switch(mode) {
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
         break;
      case Double:
         double p2 = p * 2.0D - 0.5D;
         up = p2;
         down = p2;
         sides = p2;
      }

      return this.getBox(sides, up, down);
   }

   private void abort(class_2338 pos) {
      this.sendPacket(new class_2846(class_2847.field_12971, pos, class_2350.field_11033, 0));
      this.started = false;
   }

   private class_238 getBox(double sides, double up, double down) {
      class_265 shape = BlackOut.mc.field_1687.method_8320(this.minePos).method_26218(BlackOut.mc.field_1687, this.minePos);
      if (shape.method_1110()) {
         return null;
      } else {
         class_238 from = shape.method_1107();
         class_243 middle = BoxUtils.middle(from);
         class_243 scale = new class_243(from.method_17939(), from.method_17940(), from.method_17941());
         return this.fromScale(middle, scale, sides, up, down).method_996(this.minePos);
      }
   }

   private class_238 fromScale(class_243 m, class_243 s, double sides, double up, double down) {
      return new class_238(m.field_1352 - sides * s.field_1352, m.field_1351 - down * s.field_1351, m.field_1350 - sides * s.field_1350, m.field_1352 + sides * s.field_1352, m.field_1351 + up * s.field_1351, m.field_1350 + sides * s.field_1350);
   }

   private BlackOutColor getSideColor(double p) {
      if ((Boolean)this.animationColor.get()) {
         return ((BlackOutColor)this.sideStartColor.get()).lerp(p, (BlackOutColor)this.sideEndColor.get());
      } else {
         return p > 0.9D ? (BlackOutColor)this.sideEndColor.get() : (BlackOutColor)this.sideStartColor.get();
      }
   }

   private BlackOutColor getLineColor(double p) {
      if ((Boolean)this.animationColor.get()) {
         return ((BlackOutColor)this.lineStartColor.get()).lerp(p, (BlackOutColor)this.lineEndColor.get());
      } else {
         return p > 0.9D ? (BlackOutColor)this.lineEndColor.get() : (BlackOutColor)this.lineStartColor.get();
      }
   }
}
