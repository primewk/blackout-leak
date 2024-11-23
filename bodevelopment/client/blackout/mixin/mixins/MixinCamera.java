package bodevelopment.client.blackout.mixin.mixins;

import bodevelopment.client.blackout.interfaces.mixin.IVec3d;
import bodevelopment.client.blackout.module.modules.visual.misc.CameraModifier;
import bodevelopment.client.blackout.module.modules.visual.misc.Freecam;
import bodevelopment.client.blackout.module.modules.visual.misc.Spectate;
import bodevelopment.client.blackout.util.OLEPOSSUtils;
import net.minecraft.class_1297;
import net.minecraft.class_1309;
import net.minecraft.class_1922;
import net.minecraft.class_2350;
import net.minecraft.class_243;
import net.minecraft.class_3532;
import net.minecraft.class_4184;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_4184.class})
public abstract class MixinCamera {
   @Shadow
   private boolean field_18709;
   @Shadow
   private class_1922 field_18710;
   @Shadow
   private class_1297 field_18711;
   @Shadow
   private boolean field_18719;
   @Shadow
   private float field_18718;
   @Shadow
   private float field_18717;
   @Shadow
   private float field_18722;
   @Shadow
   private float field_18721;
   @Shadow
   private float field_47549;
   @Unique
   private long prevTime = 0L;
   @Unique
   private class_243 prevPos;

   public MixinCamera() {
      this.prevPos = class_243.field_1353;
   }

   @Shadow
   protected abstract double method_19318(double var1);

   @Shadow
   protected abstract void method_19325(float var1, float var2);

   @Shadow
   protected abstract void method_19324(double var1, double var3, double var5);

   @Shadow
   protected abstract void method_19327(double var1, double var3, double var5);

   @Shadow
   public abstract class_243 method_19326();

   @Shadow
   public abstract float method_19330();

   @Shadow
   public abstract float method_19329();

   @Shadow
   protected abstract void method_19322(class_243 var1);

   @Inject(
      method = {"update"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void cameraClip(class_1922 area, class_1297 focusedEntity, boolean thirdPerson, boolean inverseView, float tickDelta, CallbackInfo ci) {
      ci.cancel();
      CameraModifier modifier = CameraModifier.getInstance();
      Spectate spectate = Spectate.getInstance();
      Freecam freecam = Freecam.getInstance();
      this.field_18709 = true;
      this.field_18710 = area;
      this.field_18711 = focusedEntity;
      this.field_18719 = thirdPerson;
      this.field_47549 = tickDelta;
      double delta = (double)(System.currentTimeMillis() - this.prevTime) / 1000.0D;
      this.prevTime = System.currentTimeMillis();
      this.method_19325(focusedEntity.method_5705(tickDelta), focusedEntity.method_5695(tickDelta));
      this.method_19327(class_3532.method_16436((double)tickDelta, focusedEntity.field_6014, focusedEntity.method_23317()), class_3532.method_16436((double)tickDelta, focusedEntity.field_6036, focusedEntity.method_23318()) + (double)class_3532.method_16439(tickDelta, this.field_18722, this.field_18721), class_3532.method_16436((double)tickDelta, focusedEntity.field_5969, focusedEntity.method_23321()));
      if (modifier.enabled) {
         modifier.updateDistance(thirdPerson, delta);
      }

      class_1297 spectateEntity = spectate != null && spectate.enabled ? spectate.getEntity() : null;
      if (!freecam.enabled) {
         freecam.pos = this.method_19326();
      }

      boolean movedPrev;
      if (modifier.enabled) {
         if (modifier.shouldSmooth(thirdPerson)) {
            movedPrev = true;
            this.movePos(this.method_19326(), delta, modifier);
         } else {
            movedPrev = false;
         }

         class_243 pos = this.method_19326();
         this.method_19327(pos.method_10216(), (Boolean)modifier.lockY.get() ? class_3532.method_15350(pos.method_10214(), (Double)modifier.minY.get(), (Double)modifier.maxY.get()) : pos.method_10214(), pos.method_10215());
      } else {
         movedPrev = false;
      }

      if (!movedPrev) {
         this.prevPos = this.method_19326();
      }

      if (!freecam.enabled) {
         ((IVec3d)freecam.velocity).blackout_Client$set(0.0D, 0.0D, 0.0D);
      }

      if (spectateEntity != null) {
         this.method_19322(OLEPOSSUtils.getLerpedPos(spectateEntity, (double)tickDelta).method_1031(0.0D, (double)spectateEntity.method_18381(spectateEntity.method_18376()), 0.0D));
         this.method_19325(spectateEntity.method_5705(tickDelta), spectateEntity.method_5695(tickDelta));
      } else if (freecam.enabled) {
         this.method_19322(freecam.getPos(this.method_19330(), this.method_19329()));
      } else if (thirdPerson) {
         if (inverseView) {
            this.method_19325(this.field_18718 + 180.0F, -this.field_18717);
         }

         double distance = modifier.enabled ? modifier.getCameraDistance() : 4.0D;
         this.method_19324(-((Boolean)modifier.clip.get() && modifier.enabled ? distance : this.method_19318(distance)), 0.0D, 0.0D);
      } else if (focusedEntity instanceof class_1309 && ((class_1309)focusedEntity).method_6113()) {
         class_2350 direction = ((class_1309)focusedEntity).method_18401();
         this.method_19325(direction != null ? direction.method_10144() - 180.0F : 0.0F, 0.0F);
         this.method_19324(0.0D, 0.3D, 0.0D);
      }

   }

   @Unique
   private void movePos(class_243 to, double delta, CameraModifier modifier) {
      double dist = this.prevPos.method_1022(to);
      double movement = dist * (Double)modifier.smoothSpeed.get() * delta;
      double newDist = class_3532.method_15350(dist - movement, 0.0D, dist);
      double f = dist == 0.0D && newDist == 0.0D ? 1.0D : newDist / dist;
      class_243 offset = to.method_1020(this.prevPos);
      class_243 m = offset.method_1021(1.0D - f);
      this.prevPos = this.prevPos.method_1019(m);
      this.method_19322(this.prevPos);
   }
}
