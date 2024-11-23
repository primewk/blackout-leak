package bodevelopment.client.blackout.module.modules.visual.misc;

import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.RenderEvent;
import bodevelopment.client.blackout.keys.KeyBind;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.util.render.AnimUtils;
import net.minecraft.class_3532;

public class FovModifier extends Module {
   private static FovModifier INSTANCE;
   private final SettingGroup sgGeneral = this.addGroup("General");
   public final Setting<Double> fov;
   public final Setting<KeyBind> zoomKey;
   public final Setting<Double> zoom;
   public final Setting<Double> zoomTime;
   private double progress;

   public FovModifier() {
      super("FOV Modifier", "Modifies FOV.", SubCategory.MISC_VISUAL, true);
      this.fov = this.sgGeneral.d("FOV", 100.0D, 10.0D, 170.0D, 5.0D, ".");
      this.zoomKey = this.sgGeneral.k("Zoom Key", ".");
      this.zoom = this.sgGeneral.d("Zoom FOV", 30.0D, 5.0D, 100.0D, 1.0D, ".", () -> {
         return ((KeyBind)this.zoomKey.get()).value != null;
      });
      this.zoomTime = this.sgGeneral.d("Zoom Time", 0.3D, 0.0D, 5.0D, 0.05D, ".", () -> {
         return ((KeyBind)this.zoomKey.get()).value != null;
      });
      this.progress = 0.0D;
      INSTANCE = this;
   }

   public static FovModifier getInstance() {
      return INSTANCE;
   }

   @Event
   public void onRender(RenderEvent.World.Pre event) {
      this.progress = class_3532.method_15350(this.progress + (((KeyBind)this.zoomKey.get()).isPressed() ? event.frameTime / (Double)this.zoomTime.get() : -event.frameTime / (Double)this.zoomTime.get()), 0.0D, 1.0D);
   }

   public double getFOV() {
      return class_3532.method_16436(AnimUtils.easeInOutCubic(this.progress), (Double)this.fov.get(), (Double)this.zoom.get());
   }
}
