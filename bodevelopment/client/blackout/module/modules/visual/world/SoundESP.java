package bodevelopment.client.blackout.module.modules.visual.world;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.enums.FilterMode;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.PlaySoundEvent;
import bodevelopment.client.blackout.event.events.RenderEvent;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.randomstuff.BlackOutColor;
import bodevelopment.client.blackout.randomstuff.timers.RenderList;
import bodevelopment.client.blackout.util.render.RenderUtils;
import java.util.Iterator;
import java.util.List;
import net.minecraft.class_1113;
import net.minecraft.class_241;
import net.minecraft.class_243;
import net.minecraft.class_3414;
import net.minecraft.class_3417;
import net.minecraft.class_3532;
import net.minecraft.class_4587;
import net.minecraft.class_7923;

public class SoundESP extends Module {
   private final SettingGroup sgGeneral = this.addGroup("General");
   private final Setting<FilterMode> filterMode;
   private final Setting<List<class_3414>> sounds;
   private final Setting<BlackOutColor> color;
   private final Setting<Double> fadeIn;
   private final Setting<Double> renderTime;
   private final Setting<Double> fadeOut;
   private final Setting<Double> scale;
   private final Setting<Double> scaleInc;
   private final RenderList<SoundESP.SoundRender> renderList;
   private final class_4587 stack;

   public SoundESP() {
      super("Sound ESP", ".", SubCategory.WORLD, true);
      this.filterMode = this.sgGeneral.e("Filter Mode", FilterMode.Whitelist, ".");
      this.sounds = this.sgGeneral.r("Sounds", ".", class_7923.field_41172, (sound) -> {
         return sound.method_14833().method_12832();
      }, class_3417.field_15152);
      this.color = this.sgGeneral.c("Color", new BlackOutColor(255, 255, 255, 255), ".");
      this.fadeIn = this.sgGeneral.d("Fade In", 0.1D, 0.0D, 10.0D, 0.1D, ".");
      this.renderTime = this.sgGeneral.d("Render Time", 0.2D, 0.0D, 10.0D, 0.1D, ".");
      this.fadeOut = this.sgGeneral.d("Fade Out", 0.5D, 0.0D, 10.0D, 0.1D, ".");
      this.scale = this.sgGeneral.d("Scale", 1.0D, 0.0D, 10.0D, 0.1D, ".");
      this.scaleInc = this.sgGeneral.d("Scale Increase", 1.0D, 0.0D, 5.0D, 0.05D, "How much should the scale increase when enemy is further away.");
      this.renderList = RenderList.getList(false);
      this.stack = new class_4587();
   }

   @Event
   public void onSound(PlaySoundEvent event) {
      class_1113 instance = event.sound;
      if (this.filterMode.get() != FilterMode.Blacklist || !this.contains(instance)) {
         if (this.filterMode.get() != FilterMode.Whitelist || this.contains(instance)) {
            this.renderList.add(new SoundESP.SoundRender(instance.method_4784(), instance.method_4779(), instance.method_4778(), instance.method_4775().method_12832()), (Double)this.fadeIn.get() + (Double)this.renderTime.get() + (Double)this.fadeOut.get());
         }
      }
   }

   @Event
   public void onRender(RenderEvent.Hud.Post event) {
      this.stack.method_22903();
      RenderUtils.unGuiScale(this.stack);
      class_243 camPos = BlackOut.mc.field_1773.method_19418().method_19326();
      this.renderList.update((render, time, delta) -> {
         this.draw(render.x(), render.y(), render.z(), render.text(), time, camPos);
      });
      this.stack.method_22909();
   }

   private void draw(double x, double y, double z, String string, double time, class_243 camPos) {
      class_241 f = RenderUtils.getCoords(x, y, z, true);
      if (f != null) {
         double alpha = class_3532.method_15350(this.getAlpha(time), 0.0D, 1.0D);
         float scale = this.getScale(x, y, z, camPos);
         BlackOut.FONT.text(this.stack, string, scale, f.field_1343, f.field_1342, ((BlackOutColor)this.color.get()).alphaMultiRGB(alpha), true, true);
      }
   }

   private float getScale(double x, double y, double z, class_243 camPos) {
      double dx = x - camPos.field_1352;
      double dy = y - camPos.field_1351;
      double dz = z - camPos.field_1350;
      float dist = (float)Math.sqrt(Math.sqrt(dx * dx + dy * dy + dz * dz));
      return ((Double)this.scale.get()).floatValue() * 8.0F / dist + ((Double)this.scaleInc.get()).floatValue() / 20.0F * dist;
   }

   private double getAlpha(double time) {
      if (time <= (Double)this.fadeIn.get()) {
         return time / (Double)this.fadeIn.get();
      } else {
         return time >= (Double)this.fadeIn.get() && time <= (Double)this.fadeIn.get() + (Double)this.renderTime.get() ? 1.0D : 1.0D - (time - (Double)this.fadeIn.get() - (Double)this.renderTime.get()) / (Double)this.fadeOut.get();
      }
   }

   private boolean contains(class_1113 instance) {
      Iterator var2 = ((List)this.sounds.get()).iterator();

      class_3414 event;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         event = (class_3414)var2.next();
      } while(!instance.method_4775().equals(event.method_14833()));

      return true;
   }

   private static record SoundRender(double x, double y, double z, String text) {
      private SoundRender(double x, double y, double z, String text) {
         this.x = x;
         this.y = y;
         this.z = z;
         this.text = text;
      }

      public double x() {
         return this.x;
      }

      public double y() {
         return this.y;
      }

      public double z() {
         return this.z;
      }

      public String text() {
         return this.text;
      }
   }
}
