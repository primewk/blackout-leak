package bodevelopment.client.blackout.manager.managers;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.KeyEvent;
import bodevelopment.client.blackout.event.events.RenderEvent;
import bodevelopment.client.blackout.gui.clickgui.ClickGui;
import bodevelopment.client.blackout.hud.HudEditor;
import bodevelopment.client.blackout.hud.HudElement;
import bodevelopment.client.blackout.hud.elements.Arraylist;
import bodevelopment.client.blackout.manager.Manager;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.randomstuff.Pair;
import bodevelopment.client.blackout.rendering.renderer.Renderer;
import bodevelopment.client.blackout.util.ClassUtils;
import bodevelopment.client.blackout.util.SharedFeatures;
import bodevelopment.client.blackout.util.render.RenderUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import net.minecraft.class_437;
import net.minecraft.class_4587;

public class HUDManager extends Manager {
   public final HudEditor HUD_EDITOR = new HudEditor();
   private final List<Pair<String, Class<? extends HudElement>>> elements = new ArrayList();
   private final Map<Integer, HudElement> loaded = new HashMap();
   private float progress = 0.0F;
   private final class_4587 stack = new class_4587();

   public List<Pair<String, Class<? extends HudElement>>> getElements() {
      return this.elements;
   }

   public Map<Integer, HudElement> getLoaded() {
      return this.loaded;
   }

   public Class<? extends HudElement> getClass(String name) {
      Iterator var2 = this.elements.iterator();

      Pair pair;
      do {
         if (!var2.hasNext()) {
            return null;
         }

         pair = (Pair)var2.next();
      } while(!((String)pair.method_15442()).equals(name));

      return (Class)pair.method_15441();
   }

   public void init() {
      BlackOut.EVENT_BUS.subscribe(this, () -> {
         return false;
      });
      this.elements.clear();
      List<Class<? extends HudElement>> hudClasses = new ArrayList();
      ClassUtils.forEachClass((clazz) -> {
         hudClasses.add(clazz);
      }, HudElement.class.getCanonicalName().replace(HudElement.class.getSimpleName(), "elements"));
      hudClasses.stream().sorted(Comparator.comparing(Class::getSimpleName)).forEach(this::add);
      this.HUD_EDITOR.initElements();
   }

   @Event
   public void onRender(RenderEvent.Hud.Pre event) {
      this.progress = this.getProgress((float)event.frameTime * 5.0F);
      Arraylist.updateDeltas();
      if (!(this.progress <= 0.0F) && !(BlackOut.mc.field_1755 instanceof HudEditor)) {
         this.start(this.stack);
         Renderer.setAlpha(this.progress);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.progress);
         this.render(this.stack, (float)event.frameTime);
         Renderer.setAlpha(1.0F);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         this.end(this.stack);
      }
   }

   @Event
   public void onKey(KeyEvent event) {
      if (event.key == 345 && event.pressed && BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null) {
         if (BlackOut.mc.field_1755 == null || BlackOut.mc.field_1755 instanceof HudEditor) {
            this.toggle();
         }

      }
   }

   private float getProgress(float delta) {
      class_437 screen = BlackOut.mc.field_1755;
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null) {
         if (screen instanceof HudEditor) {
            return 1.0F;
         } else {
            return screen != null && (!(screen instanceof ClickGui) || Managers.CLICK_GUI.CLICK_GUI.isOpen()) && !SharedFeatures.shouldSilentScreen() ? Math.max(this.progress - delta, 0.0F) : Math.min(this.progress + delta, 1.0F);
         }
      } else {
         return 0.0F;
      }
   }

   public void start(class_4587 stack) {
      stack.method_22903();
      float s = 1000.0F / (float)BlackOut.mc.method_1522().field_1480;
      RenderUtils.unGuiScale(stack);
      s = 1.0F / s;
      stack.method_22905(s, s, s);
   }

   public void render(class_4587 stack, float frameTime) {
      Managers.HUD.forEachElement((id, element) -> {
         element.renderElement(stack, frameTime);
      });
   }

   public void clear() {
      this.loaded.values().forEach(HudElement::onRemove);
      this.loaded.clear();
   }

   public void remove(int id) {
      if (this.loaded.containsKey(id)) {
         ((HudElement)this.loaded.remove(id)).onRemove();
      }

   }

   public void end(class_4587 stack) {
      stack.method_22909();
   }

   public void forEachElement(BiConsumer<? super Integer, ? super HudElement> consumer) {
      this.loaded.forEach(consumer);
   }

   public void add(HudElement element) {
      for(int i = 0; i < 1000; ++i) {
         ++i;
         if (!this.loaded.containsKey(i)) {
            this.loaded.put(i, element);
            element.id = i;
            return;
         }
      }

   }

   private void add(Class<? extends HudElement> clazz) {
      this.elements.add(new Pair(clazz.getSimpleName().replace(" ", ""), clazz));
   }

   private void toggle() {
      BlackOut.mc.method_1507(BlackOut.mc.field_1755 instanceof HudEditor ? null : this.HUD_EDITOR);
   }
}
