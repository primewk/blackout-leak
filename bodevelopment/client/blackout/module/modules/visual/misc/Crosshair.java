package bodevelopment.client.blackout.module.modules.visual.misc;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.RenderEvent;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.randomstuff.BlackOutColor;
import bodevelopment.client.blackout.util.render.RenderUtils;
import net.minecraft.class_4587;

public class Crosshair extends Module {
   private static Crosshair INSTANCE;
   private final SettingGroup sgGeneral = this.addGroup("General");
   public final Setting<Boolean> remove;
   public final Setting<Boolean> t;
   private final Setting<Integer> dist;
   private final Setting<Integer> width;
   private final Setting<Integer> length;
   private final Setting<BlackOutColor> color;
   private final class_4587 stack;

   public Crosshair() {
      super("Crosshair", "Modifies the games crosshair", SubCategory.MISC_VISUAL, true);
      this.remove = this.sgGeneral.b("Remove", false, "Stops the crosshair from rendering");
      this.t = this.sgGeneral.b("T shape", false, ".");
      this.dist = this.sgGeneral.i("Distance", 5, 0, 25, 1, ".", () -> {
         return !(Boolean)this.remove.get();
      });
      this.width = this.sgGeneral.i("Width", 1, 1, 5, 1, ".", () -> {
         return !(Boolean)this.remove.get();
      });
      this.length = this.sgGeneral.i("Length", 10, 0, 50, 1, ".", () -> {
         return !(Boolean)this.remove.get();
      });
      this.color = this.sgGeneral.c("Color", new BlackOutColor(255, 255, 255, 225), ".", () -> {
         return !(Boolean)this.remove.get();
      });
      this.stack = new class_4587();
      INSTANCE = this;
   }

   public static Crosshair getInstance() {
      return INSTANCE;
   }

   @Event
   public void onRender(RenderEvent.Hud.Pre event) {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null) {
         if (!(Boolean)this.remove.get()) {
            this.stack.method_22903();
            RenderUtils.unGuiScale(this.stack);
            this.stack.method_46416((float)BlackOut.mc.method_22683().method_4480() / 2.0F - 1.0F, (float)BlackOut.mc.method_22683().method_4507() / 2.0F - 1.0F, 0.0F);
            int d = (Integer)this.dist.get();
            int w = (Integer)this.width.get();
            int l = (Integer)this.length.get();
            if (!(Boolean)this.t.get()) {
               RenderUtils.rounded(this.stack, (float)(-w) / 2.0F, (float)(-d - l), (float)w, (float)l, 0.0F, 0.0F, ((BlackOutColor)this.color.get()).getRGB(), ((BlackOutColor)this.color.get()).getRGB());
            }

            RenderUtils.rounded(this.stack, (float)d, (float)(-w) / 2.0F, (float)l, (float)w, 0.0F, 0.0F, ((BlackOutColor)this.color.get()).getRGB(), ((BlackOutColor)this.color.get()).getRGB());
            RenderUtils.rounded(this.stack, (float)(-w) / 2.0F, (float)d, (float)w, (float)l, 0.0F, 0.0F, ((BlackOutColor)this.color.get()).getRGB(), ((BlackOutColor)this.color.get()).getRGB());
            RenderUtils.rounded(this.stack, (float)(-d - l), (float)(-w) / 2.0F, (float)l, (float)w, 0.0F, 0.0F, ((BlackOutColor)this.color.get()).getRGB(), ((BlackOutColor)this.color.get()).getRGB());
            this.stack.method_22909();
         }
      }
   }
}
