package bodevelopment.client.blackout.module.modules.visual.world;

import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.randomstuff.BlackOutColor;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.Objects;
import net.minecraft.class_6854;

public class Ambience extends Module {
   private static Ambience INSTANCE;
   private final SettingGroup sgGeneral = this.addGroup("General");
   private final SettingGroup sgFog = this.addGroup("Fog");
   public final Setting<Boolean> modifyWeather;
   public final Setting<Double> raining;
   public final Setting<Double> thunder;
   public final Setting<Boolean> modifyTime;
   public final Setting<Integer> time;
   public final Setting<Boolean> modifyFog;
   public final Setting<Boolean> removeFog;
   public final Setting<Boolean> thickFog;
   private final Setting<class_6854> shape;
   private final Setting<Double> distance;
   private final Setting<Double> fading;
   public final Setting<BlackOutColor> color;

   public Ambience() {
      super("Ambience", ".", SubCategory.WORLD, true);
      this.modifyWeather = this.sgGeneral.b("Modify Weather", true, "Changes rain and thunder values.");
      SettingGroup var10001 = this.sgGeneral;
      Setting var10008 = this.modifyWeather;
      Objects.requireNonNull(var10008);
      this.raining = var10001.d("Rain", 0.0D, 0.0D, 10.0D, 0.25D, "Rain gradient. 1 = raining, 0 = not raining.", var10008::get);
      var10001 = this.sgGeneral;
      var10008 = this.modifyWeather;
      Objects.requireNonNull(var10008);
      this.thunder = var10001.d("Thunder", 0.0D, 0.0D, 10.0D, 0.25D, "Thunder gradient. 1 = thundering, 0 = not thundering.", var10008::get);
      this.modifyTime = this.sgGeneral.b("Modify Time", true, "Changes world time.");
      var10001 = this.sgGeneral;
      var10008 = this.modifyTime;
      Objects.requireNonNull(var10008);
      this.time = var10001.i("Time", 2000, 0, 24000, 50, ".", var10008::get);
      this.modifyFog = this.sgFog.b("Modify Fog", true, "Changes fog.");
      var10001 = this.sgFog;
      Setting var10005 = this.modifyFog;
      Objects.requireNonNull(var10005);
      this.removeFog = var10001.b("Remove Fog", true, "Removes fog.", var10005::get);
      var10001 = this.sgFog;
      var10005 = this.modifyFog;
      Objects.requireNonNull(var10005);
      this.thickFog = var10001.b("Thick Fog", true, "Makes the fog extremely thick.", var10005::get);
      this.shape = this.sgFog.e("Fog Shape", class_6854.field_36350, "Fog shape.", () -> {
         return (Boolean)this.modifyFog.get() && !(Boolean)this.removeFog.get();
      });
      this.distance = this.sgFog.d("Fog Distance", 25.0D, 0.0D, 100.0D, 1.0D, "How far away should the fog start rendering.", () -> {
         return (Boolean)this.modifyFog.get() && !(Boolean)this.removeFog.get();
      });
      this.fading = this.sgFog.d("Fog Fading", 25.0D, 0.0D, 250.0D, 1.0D, "How smoothly should the fog fade.", () -> {
         return (Boolean)this.modifyFog.get() && !(Boolean)this.removeFog.get();
      });
      this.color = this.sgFog.c("Fog Color", new BlackOutColor(255, 0, 0, 255), "Color of the fog.", () -> {
         return (Boolean)this.modifyFog.get() && !(Boolean)this.removeFog.get();
      });
      INSTANCE = this;
   }

   public static Ambience getInstance() {
      return INSTANCE;
   }

   public boolean modifyFog(boolean terrain) {
      if (!terrain && !(Boolean)this.thickFog.get()) {
         return false;
      } else if (!(Boolean)this.modifyFog.get()) {
         return false;
      } else if ((Boolean)this.removeFog.get()) {
         RenderSystem.setShaderFogColor(0.0F, 0.0F, 0.0F, 0.0F);
         return true;
      } else {
         RenderSystem.setShaderFogColor((float)((BlackOutColor)this.color.get()).red / 255.0F, (float)((BlackOutColor)this.color.get()).green / 255.0F, (float)((BlackOutColor)this.color.get()).blue / 255.0F, (float)((BlackOutColor)this.color.get()).alpha / 255.0F);
         RenderSystem.setShaderFogStart(((Double)this.distance.get()).floatValue());
         RenderSystem.setShaderFogEnd(((Double)this.distance.get()).floatValue() + ((Double)this.fading.get()).floatValue());
         RenderSystem.setShaderFogShape((class_6854)this.shape.get());
         return true;
      }
   }
}
