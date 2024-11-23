package bodevelopment.client.blackout.module.modules.legit;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.enums.SwingHand;
import bodevelopment.client.blackout.enums.SwitchMode;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.BlockStateEvent;
import bodevelopment.client.blackout.event.events.PacketEvent;
import bodevelopment.client.blackout.event.events.RenderEvent;
import bodevelopment.client.blackout.event.events.TickEvent;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.randomstuff.FindResult;
import bodevelopment.client.blackout.randomstuff.timers.TimerList;
import bodevelopment.client.blackout.randomstuff.timers.TimerMap;
import bodevelopment.client.blackout.util.InvUtils;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import net.minecraft.class_1268;
import net.minecraft.class_1792;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_1831;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_239;
import net.minecraft.class_2596;
import net.minecraft.class_2680;
import net.minecraft.class_2741;
import net.minecraft.class_2879;
import net.minecraft.class_2885;
import net.minecraft.class_3965;
import net.minecraft.class_239.class_240;

public class AnchorCharge extends Module {
   private final SettingGroup sgGeneral = this.addGroup("General");
   private final Setting<SwitchMode> glowstoneSwitch;
   private final Setting<Boolean> fullCharge;
   private final Setting<Boolean> allowOffhand;
   private final Setting<SwitchMode> explodeSwitch;
   private final Setting<Double> speed;
   private final Setting<Boolean> onlyOwn;
   private final Setting<Double> ownTime;
   private double actions;
   private int prevAnchor;
   private final TimerList<class_2338> own;
   private final TimerMap<class_2338, Integer> charges;
   private final Map<class_2338, class_2680> realStates;
   private final Predicate<class_1799> emptyPredicate;

   public AnchorCharge() {
      super("Anchor Charge", "Automatically charges and explodes anchors.", SubCategory.LEGIT, true);
      this.glowstoneSwitch = this.sgGeneral.e("Glowstone Switch", SwitchMode.Normal, "Method of switching.");
      this.fullCharge = this.sgGeneral.b("Full Charge", false, "Uses 4 glowstone.");
      this.allowOffhand = this.sgGeneral.b("Allow Offhand", true, "Blows up the anchor with offhand. Not possible in vanilla.");
      this.explodeSwitch = this.sgGeneral.e("Explode Switch", SwitchMode.Normal, "Method of switching.");
      this.speed = this.sgGeneral.d("Speed", 4.0D, 0.0D, 20.0D, 0.1D, "Actions per second.");
      this.onlyOwn = this.sgGeneral.b("Only Own", true, ".");
      this.ownTime = this.sgGeneral.d("Own Time", 2.0D, 0.0D, 10.0D, 0.1D, ".");
      this.actions = 0.0D;
      this.prevAnchor = -1;
      this.own = new TimerList(false);
      this.charges = new TimerMap(false);
      this.realStates = new ConcurrentHashMap();
      this.emptyPredicate = (stack) -> {
         if (stack != null && !stack.method_7960()) {
            class_1792 item = stack.method_7909();
            return item instanceof class_1831;
         } else {
            return true;
         }
      };
   }

   @Event
   public void onRender(RenderEvent.World.Pre event) {
      this.actions += event.frameTime * (Double)this.speed.get();
      this.actions = Math.min(this.actions, 1.0D);
      this.realStates.entrySet().removeIf((entry) -> {
         class_2338 pos = (class_2338)entry.getKey();
         if (this.charges.containsKey(pos)) {
            return false;
         } else {
            BlackOut.mc.field_1687.method_8501(pos, (class_2680)entry.getValue());
            return true;
         }
      });
   }

   @Event
   public void onState(BlockStateEvent event) {
      this.charges.update();
      if (this.charges.containsKey(event.pos)) {
         if (event.state.method_26204() == class_2246.field_23152) {
            int c = (Integer)event.state.method_11654(class_2741.field_23187);
            if (c < (Integer)this.charges.get(event.pos)) {
               event.setCancelled(true);
            }
         } else if ((Integer)this.charges.get(event.pos) != -1) {
            event.setCancelled(true);
         }

         this.realStates.remove(event.pos);
         this.realStates.put(event.pos, event.state);
      }

   }

   @Event
   public void onSent(PacketEvent.Sent event) {
      class_2596 var3 = event.packet;
      if (var3 instanceof class_2885) {
         class_2885 packet = (class_2885)var3;
         class_2338 pos = packet.method_12543().method_17777().method_10093(packet.method_12543().method_17780());
         class_1799 holdingStack = packet.method_12546() == class_1268.field_5808 ? Managers.PACKET.getStack() : BlackOut.mc.field_1724.method_6079();
         if (holdingStack.method_7909() == class_1802.field_23141) {
            this.own.add(pos, (Double)this.ownTime.get());
         }
      }

   }

   @Event
   public void onTick(TickEvent.Pre event) {
      this.own.update();
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null) {
         if (Managers.PACKET.getStack().method_7909() == class_1802.field_23141) {
            this.prevAnchor = Managers.PACKET.slot;
         }

         class_239 var3 = BlackOut.mc.field_1765;
         if (var3 instanceof class_3965) {
            class_3965 hitResult = (class_3965)var3;
            if (hitResult.method_17783() != class_240.field_1333) {
               class_2680 state = BlackOut.mc.field_1687.method_8320(hitResult.method_17777());
               if (state.method_26204() == class_2246.field_23152) {
                  if (!(Boolean)this.onlyOwn.get() || this.own.contains((Object)hitResult.method_17777())) {
                     if (!(this.actions <= 0.0D)) {
                        if (!(Boolean)this.fullCharge.get() && (Integer)state.method_11654(class_2741.field_23187) > 0) {
                           this.explode(hitResult);
                        } else {
                           this.charge(hitResult);
                        }

                     }
                  }
               }
            }
         }
      }
   }

   private void charge(class_3965 blockHitResult) {
      class_1268 hand = null;
      if (Managers.PACKET.getStack().method_7909() == class_1802.field_8801) {
         hand = class_1268.field_5808;
      }

      if ((Boolean)this.allowOffhand.get() && BlackOut.mc.field_1724.method_6079().method_7909() == class_1802.field_8801) {
         hand = class_1268.field_5810;
      }

      FindResult result = ((SwitchMode)this.glowstoneSwitch.get()).find(class_1802.field_8801);
      boolean switched = false;
      if (hand != null || result.wasFound() && (switched = ((SwitchMode)this.glowstoneSwitch.get()).swap(result.slot()))) {
         hand = hand == null ? class_1268.field_5808 : hand;
         class_2338 pos = blockHitResult.method_17777();
         class_2680 state = BlackOut.mc.field_1687.method_8320(pos);
         if ((Integer)state.method_11654(class_2741.field_23187) < 4) {
            int c = (Integer)state.method_11654(class_2741.field_23187) + 1;
            this.charges.add(pos, c, 0.3D);
            BlackOut.mc.field_1687.method_8501(pos, (class_2680)state.method_11657(class_2741.field_23187, c));
         } else {
            this.charges.add(pos, -1, 0.3D);
            BlackOut.mc.field_1687.method_8501(pos, class_2246.field_10124.method_9564());
         }

         BlackOut.mc.field_1761.method_2896(BlackOut.mc.field_1724, hand, blockHitResult);
         BlackOut.mc.method_1562().method_52787(new class_2879(hand));
         this.clientSwing(SwingHand.RealHand, hand);
         --this.actions;
         if (switched) {
            ((SwitchMode)this.glowstoneSwitch.get()).swapBack();
         }

      }
   }

   private void explode(class_3965 blockHitResult) {
      class_1268 hand = null;
      FindResult result = InvUtils.findNullable(((SwitchMode)this.explodeSwitch.get()).hotbar, false, this.emptyPredicate);
      boolean switched = false;
      if (this.prevAnchor > -1 && BlackOut.mc.field_1724.method_31548().method_5438(this.prevAnchor).method_7909() == class_1802.field_23141 && ((SwitchMode)this.explodeSwitch.get()).hotbar) {
         if (!(switched = ((SwitchMode)this.explodeSwitch.get()).swap(this.prevAnchor))) {
            return;
         }
      } else {
         if (this.emptyPredicate.test(Managers.PACKET.getStack())) {
            hand = class_1268.field_5808;
         }

         if ((Boolean)this.allowOffhand.get() && this.emptyPredicate.test(Managers.PACKET.getStack())) {
            hand = class_1268.field_5810;
         }

         if (hand == null && (!result.wasFound() || !(switched = ((SwitchMode)this.explodeSwitch.get()).swap(result.slot())))) {
            return;
         }
      }

      class_2338 pos = blockHitResult.method_17777();
      this.charges.add(pos, -1, 0.3D);
      BlackOut.mc.field_1687.method_8501(pos, class_2246.field_10124.method_9564());
      hand = hand == null ? class_1268.field_5808 : hand;
      BlackOut.mc.field_1761.method_2896(BlackOut.mc.field_1724, hand, blockHitResult);
      this.clientSwing(SwingHand.RealHand, hand);
      --this.actions;
      if (switched) {
         ((SwitchMode)this.explodeSwitch.get()).swapBack();
      }

   }
}
