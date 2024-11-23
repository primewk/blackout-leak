package bodevelopment.client.blackout.module.modules.visual.misc;

import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import net.minecraft.class_1268;
import net.minecraft.class_4587;
import net.minecraft.class_7833;

public class ViewModel extends Module {
   private static ViewModel INSTANCE;
   private final SettingGroup sgGeneral = this.addGroup("General");
   private final SettingGroup sgMain = this.addGroup("Main");
   private final SettingGroup sgOff = this.addGroup("Off");
   public final Setting<Double> fov;
   private final Setting<Boolean> renderMain;
   private final Setting<Double> mainX;
   private final Setting<Double> mainY;
   private final Setting<Double> mainZ;
   private final Setting<Double> mainScaleX;
   private final Setting<Double> mainScaleY;
   private final Setting<Double> mainScaleZ;
   private final Setting<Double> mainRotX;
   private final Setting<Double> mainRotY;
   private final Setting<Double> mainRotZ;
   private final Setting<Double> mainRotSpeedX;
   private final Setting<Double> mainRotSpeedY;
   private final Setting<Double> mainRotSpeedZ;
   private final Setting<Boolean> renderOff;
   private final Setting<Double> offX;
   private final Setting<Double> offY;
   private final Setting<Double> offZ;
   private final Setting<Double> offScaleX;
   private final Setting<Double> offScaleY;
   private final Setting<Double> offScaleZ;
   private final Setting<Double> offRotX;
   private final Setting<Double> offRotY;
   private final Setting<Double> offRotZ;
   private final Setting<Double> offRotSpeedX;
   private final Setting<Double> offRotSpeedY;
   private final Setting<Double> offRotSpeedZ;
   private float mainRotationX;
   private float mainRotationY;
   private float mainRotationZ;
   private long mainTime;
   private float offRotationX;
   private float offRotationY;
   private float offRotationZ;
   private long offTime;

   public ViewModel() {
      super("View Model", "Modifies where hands and held items are rendered.", SubCategory.MISC_VISUAL, false);
      this.fov = this.sgGeneral.d("Hand FOV", 70.0D, 10.0D, 170.0D, 5.0D, ".");
      this.renderMain = this.sgMain.b("Render Main", true, "");
      this.mainX = this.sgMain.d("Main X", 0.0D, -1.0D, 1.0D, 0.02D, "");
      this.mainY = this.sgMain.d("Main Y", 0.0D, -1.0D, 1.0D, 0.02D, "");
      this.mainZ = this.sgMain.d("Main Z", 0.0D, -1.0D, 1.0D, 0.02D, "");
      this.mainScaleX = this.sgMain.d("Main Scale X", 1.0D, 0.0D, 2.0D, 0.02D, "");
      this.mainScaleY = this.sgMain.d("Main Scale Y", 1.0D, 0.0D, 2.0D, 0.02D, "");
      this.mainScaleZ = this.sgMain.d("Main Scale Z", 1.0D, 0.0D, 2.0D, 0.02D, "");
      this.mainRotX = this.sgMain.d("Main Rotation X", 0.0D, -180.0D, 180.0D, 5.0D, "");
      this.mainRotY = this.sgMain.d("Main Rotation Y", 0.0D, -180.0D, 180.0D, 5.0D, "");
      this.mainRotZ = this.sgMain.d("Main Rotation Z", 0.0D, -180.0D, 180.0D, 5.0D, "");
      this.mainRotSpeedX = this.sgMain.d("Main Rotation Speed X", 0.0D, -180.0D, 180.0D, 5.0D, "");
      this.mainRotSpeedY = this.sgMain.d("Main Rotation Speed Y", 0.0D, -180.0D, 180.0D, 5.0D, "");
      this.mainRotSpeedZ = this.sgMain.d("Main Rotation Speed Z", 0.0D, -180.0D, 180.0D, 5.0D, "");
      this.renderOff = this.sgOff.b("Render Off", true, "");
      this.offX = this.sgOff.d("Off X", 0.0D, -1.0D, 1.0D, 0.02D, "");
      this.offY = this.sgOff.d("Off Y", 0.0D, -1.0D, 1.0D, 0.02D, "");
      this.offZ = this.sgOff.d("Off Z", 0.0D, -1.0D, 1.0D, 0.02D, "");
      this.offScaleX = this.sgOff.d("Off Scale X", 1.0D, 0.0D, 2.0D, 0.02D, "");
      this.offScaleY = this.sgOff.d("Off Scale Y", 1.0D, 0.0D, 2.0D, 0.02D, "");
      this.offScaleZ = this.sgOff.d("Off Scale Z", 1.0D, 0.0D, 2.0D, 0.02D, "");
      this.offRotX = this.sgOff.d("Off Rotation X", 0.0D, -180.0D, 180.0D, 5.0D, "");
      this.offRotY = this.sgOff.d("Off Rotation Y", 0.0D, -180.0D, 180.0D, 5.0D, "");
      this.offRotZ = this.sgOff.d("Off Rotation Z", 0.0D, -180.0D, 180.0D, 5.0D, "");
      this.offRotSpeedX = this.sgOff.d("Off Rotation Speed X", 0.0D, -180.0D, 180.0D, 5.0D, "");
      this.offRotSpeedY = this.sgOff.d("Off Rotation Speed Y", 0.0D, -180.0D, 180.0D, 5.0D, "");
      this.offRotSpeedZ = this.sgOff.d("Off Rotation Speed Z", 0.0D, -180.0D, 180.0D, 5.0D, "");
      this.mainRotationX = 0.0F;
      this.mainRotationY = 0.0F;
      this.mainRotationZ = 0.0F;
      this.mainTime = 0L;
      this.offRotationX = 0.0F;
      this.offRotationY = 0.0F;
      this.offRotationZ = 0.0F;
      this.offTime = 0L;
      INSTANCE = this;
   }

   public static ViewModel getInstance() {
      return INSTANCE;
   }

   public void transform(class_4587 stack, class_1268 hand) {
      if (hand == class_1268.field_5808) {
         this.transform(stack, this.mainX, this.mainY, this.mainZ);
      } else {
         this.transform(stack, this.offX, this.offY, this.offZ);
      }

   }

   public boolean shouldCancel(class_1268 hand) {
      if (hand == class_1268.field_5808) {
         return !(Boolean)this.renderMain.get();
      } else {
         return !(Boolean)this.renderOff.get();
      }
   }

   public void scaleAndRotate(class_4587 stack, class_1268 hand) {
      stack.method_22903();
      Setting scaleX;
      Setting scaleY;
      Setting scaleZ;
      Setting rotX;
      Setting rotY;
      Setting rotZ;
      float rotationX;
      float rotationY;
      float rotationZ;
      double delta;
      if (hand == class_1268.field_5808) {
         scaleX = this.mainScaleX;
         scaleY = this.mainScaleY;
         scaleZ = this.mainScaleZ;
         rotX = this.mainRotX;
         rotY = this.mainRotY;
         rotZ = this.mainRotZ;
         delta = (double)(System.currentTimeMillis() - this.mainTime) / 250.0D;
         this.mainTime = System.currentTimeMillis();
         if ((Double)this.mainRotSpeedX.get() == 0.0D) {
            this.mainRotationX = 0.0F;
         } else {
            this.mainRotationX = (float)((double)this.mainRotationX + delta * (Double)this.mainRotSpeedX.get());
         }

         if ((Double)this.mainRotSpeedY.get() == 0.0D) {
            this.mainRotationY = 0.0F;
         } else {
            this.mainRotationY = (float)((double)this.mainRotationY + delta * (Double)this.mainRotSpeedY.get());
         }

         if ((Double)this.mainRotSpeedZ.get() == 0.0D) {
            this.mainRotationZ = 0.0F;
         } else {
            this.mainRotationZ = (float)((double)this.mainRotationZ + delta * (Double)this.mainRotSpeedZ.get());
         }

         rotationX = this.mainRotationX;
         rotationY = this.mainRotationY;
         rotationZ = this.mainRotationZ;
      } else {
         scaleX = this.offScaleX;
         scaleY = this.offScaleY;
         scaleZ = this.offScaleZ;
         rotX = this.offRotX;
         rotY = this.offRotY;
         rotZ = this.offRotZ;
         delta = (double)(System.currentTimeMillis() - this.offTime) / 250.0D;
         this.offTime = System.currentTimeMillis();
         if ((Double)this.offRotSpeedX.get() == 0.0D) {
            this.offRotationX = 0.0F;
         } else {
            this.offRotationX = (float)((double)this.offRotationX + delta * (Double)this.offRotSpeedX.get());
         }

         if ((Double)this.offRotSpeedY.get() == 0.0D) {
            this.offRotationY = 0.0F;
         } else {
            this.offRotationY = (float)((double)this.offRotationY + delta * (Double)this.offRotSpeedY.get());
         }

         if ((Double)this.offRotSpeedZ.get() == 0.0D) {
            this.offRotationZ = 0.0F;
         } else {
            this.offRotationZ = (float)((double)this.offRotationZ + delta * (Double)this.offRotSpeedZ.get());
         }

         rotationX = this.offRotationX;
         rotationY = this.offRotationY;
         rotationZ = this.offRotationZ;
      }

      stack.method_22907(class_7833.field_40714.rotationDegrees(((Double)rotX.get()).floatValue() + rotationX));
      stack.method_22907(class_7833.field_40716.rotationDegrees(((Double)rotY.get()).floatValue() + rotationY));
      stack.method_22907(class_7833.field_40718.rotationDegrees(((Double)rotZ.get()).floatValue() + rotationZ));
      stack.method_22904(-0.5D, -0.5D, -0.5D);
      stack.method_22903();
      stack.method_22904(0.5D, 0.5D, 0.5D);
      stack.method_22905(((Double)scaleX.get()).floatValue(), ((Double)scaleY.get()).floatValue(), ((Double)scaleZ.get()).floatValue());
   }

   public void post(class_4587 stack) {
      stack.method_22909();
   }

   public void postRender(class_4587 stack) {
      stack.method_22909();
      stack.method_22909();
   }

   private void transform(class_4587 stack, Setting<Double> x, Setting<Double> y, Setting<Double> z) {
      stack.method_22903();
      stack.method_22904((Double)x.get(), (Double)y.get(), -(Double)z.get());
   }
}
