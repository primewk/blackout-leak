package bodevelopment.client.blackout.module.modules.visual.misc;

import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.util.OLEPOSSUtils;
import bodevelopment.client.blackout.util.render.AnimUtils;
import java.util.Objects;

public class CameraModifier extends Module {
   private static CameraModifier INSTANCE;
   private final SettingGroup sgGeneral = this.addGroup("General");
   public final Setting<Boolean> clip;
   public final Setting<Double> cameraDist;
   public final Setting<Double> smoothTime;
   public final Setting<Boolean> noInverse;
   public final Setting<Boolean> lockY;
   public final Setting<Double> minY;
   public final Setting<Double> maxY;
   public final Setting<Boolean> smoothMove;
   public final Setting<Boolean> smoothF5;
   public final Setting<Double> smoothSpeed;
   public double distProgress;

   public CameraModifier() {
      super("Camera Modifier", ".", SubCategory.MISC_VISUAL, true);
      this.clip = this.sgGeneral.b("Clip", true, ".");
      this.cameraDist = this.sgGeneral.d("Camera Dist", 4.0D, 0.0D, 20.0D, 0.2D, ".");
      this.smoothTime = this.sgGeneral.d("Smooth Time", 0.5D, 0.0D, 5.0D, 0.05D, ".");
      this.noInverse = this.sgGeneral.b("No Inverse", true, ".");
      this.lockY = this.sgGeneral.b("Lock Y", false, ".");
      SettingGroup var10001 = this.sgGeneral;
      Setting var10008 = this.lockY;
      Objects.requireNonNull(var10008);
      this.minY = var10001.d("Min Y", 0.0D, -64.0D, 300.0D, 1.0D, ".", var10008::get);
      var10001 = this.sgGeneral;
      var10008 = this.lockY;
      Objects.requireNonNull(var10008);
      this.maxY = var10001.d("Max Y", 5.0D, -64.0D, 300.0D, 1.0D, ".", var10008::get);
      this.smoothMove = this.sgGeneral.b("Smooth Move", false, ".");
      this.smoothF5 = this.sgGeneral.b("Smooth F5", false, "Only is smooth in f5.");
      var10001 = this.sgGeneral;
      var10008 = this.smoothMove;
      Objects.requireNonNull(var10008);
      this.smoothSpeed = var10001.d("Smooth Speed", 5.0D, 1.0D, 10.0D, 0.1D, ".", var10008::get);
      this.distProgress = 0.0D;
      INSTANCE = this;
   }

   public static CameraModifier getInstance() {
      return INSTANCE;
   }

   public void updateDistance(boolean thirdPerson, double delta) {
      this.distProgress = thirdPerson ? Math.min(this.distProgress + delta, (Double)this.smoothTime.get()) : 0.0D;
   }

   public boolean shouldSmooth(boolean thirdPerson) {
      if (!(Boolean)this.smoothMove.get()) {
         return false;
      } else {
         return !(Boolean)this.smoothF5.get() || thirdPerson;
      }
   }

   public double getCameraDistance() {
      return AnimUtils.easeOutCubic(OLEPOSSUtils.safeDivide(this.distProgress, (Double)this.smoothTime.get())) * (Double)this.cameraDist.get();
   }
}
