package bodevelopment.client.blackout.module.modules.combat.offensive;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.enums.RenderShape;
import bodevelopment.client.blackout.enums.RotationType;
import bodevelopment.client.blackout.enums.SwingHand;
import bodevelopment.client.blackout.enums.SwitchMode;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.RenderEvent;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.module.MoveUpdateModule;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.randomstuff.BlackOutColor;
import bodevelopment.client.blackout.randomstuff.ExtrapolationMap;
import bodevelopment.client.blackout.randomstuff.FindResult;
import bodevelopment.client.blackout.randomstuff.Rotation;
import bodevelopment.client.blackout.util.BoxUtils;
import bodevelopment.client.blackout.util.OLEPOSSUtils;
import bodevelopment.client.blackout.util.ProjectileUtils;
import bodevelopment.client.blackout.util.RotationUtils;
import bodevelopment.client.blackout.util.render.Render3DUtils;
import bodevelopment.client.blackout.util.render.RenderUtils;
import com.mojang.blaze3d.platform.GlStateManager;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.class_1268;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_1802;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_3532;
import net.minecraft.class_4587;
import net.minecraft.class_742;
import net.minecraft.class_7833;

public class Snombonty extends MoveUpdateModule {
   public final SettingGroup sgGeneral = this.addGroup("General");
   public final SettingGroup sgRender = this.addGroup("Render");
   private final Setting<Boolean> playerVelocity;
   private final Setting<Boolean> onlyPlayers;
   private final Setting<Double> range;
   private final Setting<Double> throwSpeed;
   private final Setting<SwitchMode> switchMode;
   private final Setting<Boolean> extrapolation;
   private final Setting<Double> extrapolationStrength;
   private final Setting<Boolean> instantRotate;
   private final Setting<Boolean> renderSwing;
   private final Setting<SwingHand> swingHand;
   private final Setting<RenderShape> renderShape;
   private final Setting<BlackOutColor> lineColor;
   private final Setting<BlackOutColor> sideColor;
   private final Setting<Boolean> renderSpread;
   private final Setting<BlackOutColor> spreadColor;
   private class_1297 target;
   private class_238 targetBox;
   private class_238 prevBox;
   private double yaw;
   private double pitch;
   private double throwsLeft;
   private int balls;
   private FindResult result;
   private boolean switched;
   private final class_4587 stack;
   private final ExtrapolationMap extMap;
   private final Predicate<class_1799> predicate;
   private final Consumer<double[]> snowballVelocity;

   public Snombonty() {
      super("Snombonty", "Spams snowballs at people.", SubCategory.OFFENSIVE);
      this.playerVelocity = this.sgGeneral.b("Velocity", true, "Uses your own velocity in trajectory calculations.");
      this.onlyPlayers = this.sgGeneral.b("Only Players", true, "Only abuses players.");
      this.range = this.sgGeneral.d("Range", 50.0D, 0.0D, 100.0D, 1.0D, "Doesn't target entities outside of this range.");
      this.throwSpeed = this.sgGeneral.d("Throw Speed", 20.0D, 0.0D, 20.0D, 0.1D, "How many snowballs to throw each second.");
      this.switchMode = this.sgGeneral.e("Switch Mode", SwitchMode.Normal, "Method of switching. Silent is the most reliable.");
      this.extrapolation = this.sgGeneral.b("Extrapolation", true, "Predicts enemy movement.");
      this.extrapolationStrength = this.sgGeneral.d("Extrapolation Strength", 1.0D, 0.0D, 1.0D, 0.01D, "How many snowballs to throw each second.");
      this.instantRotate = this.sgGeneral.b("Instant Rotate", true, "Ignores rotation speed limit.");
      this.renderSwing = this.sgRender.b("Render Swing", false, "Renders swing animation when throwing a snowball.");
      this.swingHand = this.sgRender.e("Swing Hand", SwingHand.RealHand, "Which hand should be swung.");
      this.renderShape = this.sgRender.e("Render Shape", RenderShape.Full, "Which parts of render should be rendered.");
      this.lineColor = this.sgRender.c("Line Color", new BlackOutColor(255, 0, 0, 255), "Line color of rendered boxes.");
      this.sideColor = this.sgRender.c("Side Color", new BlackOutColor(255, 0, 0, 50), "Side color of rendered boxes.");
      this.renderSpread = this.sgRender.b("Render Spread", true, "Renders spread circle on target entity.");
      SettingGroup var10001 = this.sgRender;
      BlackOutColor var10003 = new BlackOutColor(255, 255, 255, 255);
      Setting var10005 = this.renderSpread;
      Objects.requireNonNull(var10005);
      this.spreadColor = var10001.c("Spread Color", var10003, "Color of the spread circle.", var10005::get);
      this.target = null;
      this.targetBox = null;
      this.prevBox = null;
      this.yaw = 0.0D;
      this.pitch = 0.0D;
      this.throwsLeft = 0.0D;
      this.balls = 0;
      this.result = null;
      this.switched = false;
      this.stack = new class_4587();
      this.extMap = new ExtrapolationMap();
      this.predicate = (stack) -> {
         return stack.method_31574(class_1802.field_8543) || stack.method_31574(class_1802.field_8803);
      };
      this.snowballVelocity = (vel) -> {
         vel[0] *= 0.99D;
         vel[1] *= 0.99D;
         vel[2] *= 0.99D;
         vel[1] -= 0.03D;
      };
   }

   @Event
   public void onRender(RenderEvent.World.Post event) {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null && this.target != null) {
         Render3DUtils.box(this.lerpBox(event.tickDelta, this.prevBox, this.targetBox), (BlackOutColor)this.sideColor.get(), (BlackOutColor)this.lineColor.get(), (RenderShape)this.renderShape.get());
         if ((Boolean)this.renderSpread.get()) {
            this.renderSpread(event.tickDelta);
         }

      }
   }

   private void renderSpread(float tickDelta) {
      class_243 cameraPos = BlackOut.mc.field_1773.method_19418().method_19326();
      double x = class_3532.method_16436((double)tickDelta, (this.prevBox.field_1323 + this.prevBox.field_1320) / 2.0D, (this.targetBox.field_1323 + this.targetBox.field_1320) / 2.0D) - cameraPos.field_1352;
      double y = class_3532.method_16436((double)tickDelta, this.prevBox.field_1322, this.targetBox.field_1322) - cameraPos.field_1351 + (double)(this.target.method_17682() / 2.0F);
      double z = class_3532.method_16436((double)tickDelta, (this.prevBox.field_1321 + this.prevBox.field_1324) / 2.0D, (this.targetBox.field_1321 + this.targetBox.field_1324) / 2.0D) - cameraPos.field_1350;
      this.stack.method_22903();
      Render3DUtils.setRotation(this.stack);
      double pitch = RotationUtils.getPitch(BlackOut.mc.field_1773.method_19418().method_19326().method_1031(x, y, z), BlackOut.mc.field_1773.method_19418().method_19326());
      this.stack.method_22904(x, y, z);
      this.stack.method_22905(1.0F, -1.0F, 1.0F);
      this.stack.method_22907(class_7833.field_40716.rotation(-((float)Math.toRadians(RotationUtils.getYaw(BlackOut.mc.field_1773.method_19418().method_19326().method_1031(x, y, z), BlackOut.mc.field_1773.method_19418().method_19326(), 0.0D)))));
      this.stack.method_22907(class_7833.field_40714.rotation(-((float)Math.toRadians(pitch))));
      GlStateManager._disableDepthTest();
      GlStateManager._enableBlend();
      GlStateManager._disableCull();
      RenderUtils.circle2(this.stack, 0.0F, 0.0F, (float)(Math.sqrt(x * x + y * y + z * z) * 0.0174D), ((BlackOutColor)this.spreadColor.get()).getColor().getRGB());
      this.stack.method_22909();
   }

   private class_238 lerpBox(float tickDelta, class_238 prev, class_238 current) {
      return new class_238(class_3532.method_16436((double)tickDelta, prev.field_1323, current.field_1323), class_3532.method_16436((double)tickDelta, prev.field_1322, current.field_1322), class_3532.method_16436((double)tickDelta, prev.field_1321, current.field_1321), class_3532.method_16436((double)tickDelta, prev.field_1320, current.field_1320), class_3532.method_16436((double)tickDelta, prev.field_1325, current.field_1325), class_3532.method_16436((double)tickDelta, prev.field_1324, current.field_1324));
   }

   protected void update(boolean allowAction, boolean fakePos) {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null) {
         if ((Boolean)this.extrapolation.get()) {
            this.extMap.update((player) -> {
               return (int)Math.floor(BlackOut.mc.field_1724.method_19538().method_1022(player.method_19538()) / 5.0D * (Double)this.extrapolationStrength.get());
            });
         } else {
            this.extMap.clear();
         }

         this.findTarget();
         if (this.target != null) {
            class_238 var10001;
            label31: {
               this.prevBox = this.targetBox;
               class_1297 var4 = this.target;
               if (var4 instanceof class_742) {
                  class_742 pl = (class_742)var4;
                  if (this.extMap.contains(pl)) {
                     var10001 = this.extMap.get(pl);
                     break label31;
                  }
               }

               var10001 = this.target.method_5829();
            }

            this.targetBox = var10001;
            if (this.prevBox == null) {
               this.prevBox = this.targetBox;
            }

            if (BoxUtils.middle(this.prevBox).method_1022(BoxUtils.middle(this.targetBox)) > 5.0D) {
               this.prevBox = this.targetBox;
            }

            this.update(allowAction);
         }
      }
   }

   private void update(boolean allowAction) {
      this.throwsLeft += (Double)this.throwSpeed.get() / 20.0D;
      this.result = ((SwitchMode)this.switchMode.get()).find(this.predicate);
      this.throwUpdate(allowAction);
      this.throwsLeft = Math.min(this.throwsLeft, 1.0D);
   }

   private void throwUpdate(boolean allowAction) {
      class_1268 hand = OLEPOSSUtils.getHand(this.predicate);
      if (hand != null || this.result.wasFound()) {
         if (this.rotate((float)this.yaw, (float)this.pitch, 0.0D, 10.0D, RotationType.Other.withInstant((Boolean)this.instantRotate.get()), "throwing")) {
            if (allowAction) {
               if (hand == null) {
                  if (this.result.wasFound()) {
                     this.balls = Math.min((int)Math.floor(this.throwsLeft), this.result.amount());
                  }
               } else {
                  this.balls = this.getBalls(hand);
               }

               while(this.balls > 0) {
                  this.throwSnowBall(hand);
                  --this.balls;
                  --this.throwsLeft;
               }

               if (this.switched) {
                  ((SwitchMode)this.switchMode.get()).swapBack();
               }

            }
         }
      }
   }

   private void throwSnowBall(class_1268 hand) {
      if (hand != null || (this.switched = ((SwitchMode)this.switchMode.get()).swap(this.result.slot()))) {
         this.useItem(hand);
         if ((Boolean)this.renderSwing.get()) {
            this.clientSwing((SwingHand)this.swingHand.get(), hand);
         }

      }
   }

   private int getBalls(class_1268 hand) {
      return Math.min((int)Math.floor(this.throwsLeft), hand == class_1268.field_5808 ? Managers.PACKET.getStack().method_7947() : (hand == class_1268.field_5810 ? BlackOut.mc.field_1724.method_6079().method_7947() : 0));
   }

   private void findTarget() {
      this.target = null;
      double dist = 10000.0D;
      Iterator var3 = BlackOut.mc.field_1687.method_18112().iterator();

      while(true) {
         class_1297 entity;
         double d;
         do {
            do {
               do {
                  do {
                     if (!var3.hasNext()) {
                        return;
                     }

                     entity = (class_1297)var3.next();
                  } while(entity == BlackOut.mc.field_1724);
               } while(!(entity instanceof class_1309));
            } while((Boolean)this.onlyPlayers.get() && !(entity instanceof class_1657));

            class_238 var10000;
            label48: {
               if (entity instanceof class_742) {
                  class_742 pl = (class_742)entity;
                  if (this.extMap.contains(pl)) {
                     var10000 = this.extMap.get(pl);
                     break label48;
                  }
               }

               var10000 = entity.method_5829();
            }

            class_238 box = var10000;
            d = BlackOut.mc.field_1724.method_19538().method_1022(BoxUtils.feet(box));
         } while((Double)this.range.get() > 0.0D && d > (Double)this.range.get());

         Rotation rotation = ProjectileUtils.calcShootingRotation(BlackOut.mc.field_1724.method_33571(), BoxUtils.middle(entity.method_5829()), 1.5D, (Boolean)this.playerVelocity.get(), this.snowballVelocity);
         if (rotation.pitch() != 0.0F && !(rotation.pitch() < -85.0F) && d < dist) {
            this.yaw = (double)rotation.yaw();
            this.pitch = (double)rotation.pitch();
            this.target = entity;
            dist = d;
         }
      }
   }
}
