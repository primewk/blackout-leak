package bodevelopment.client.blackout.hud;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.enums.ConfigType;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.KeyEvent;
import bodevelopment.client.blackout.event.events.MouseButtonEvent;
import bodevelopment.client.blackout.event.events.MouseScrollEvent;
import bodevelopment.client.blackout.gui.clickgui.ClickGuiScreen;
import bodevelopment.client.blackout.keys.Keys;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.rendering.renderer.ColorRenderer;
import bodevelopment.client.blackout.util.render.RenderUtils;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import net.minecraft.class_2561;
import net.minecraft.class_332;
import net.minecraft.class_437;
import net.minecraft.class_4587;
import net.minecraft.class_293.class_5596;

public class HudEditor extends class_437 {
   private final class_4587 stack = new class_4587();
   private float mx = 0.0F;
   private float my = 0.0F;
   private HudEditor.State state;
   private float holdX;
   private float holdY;
   private boolean still;
   private boolean holding;
   private final List<HudElement> selectedElements;
   private final List<HudElement> picked;
   private float screenHeight;
   private float screenWidth;
   private boolean wasList;
   private final int bgColor;
   private final int lineColor;
   private final HudElementList elementList;
   private final HudEditorSettings settings;
   public ClickGuiScreen openedScreen;

   public HudEditor() {
      super(class_2561.method_30163("HUD Editor"));
      this.state = HudEditor.State.Inactive;
      this.holdX = 0.0F;
      this.holdY = 0.0F;
      this.still = false;
      this.holding = false;
      this.selectedElements = new ArrayList();
      this.picked = new ArrayList();
      this.wasList = false;
      this.bgColor = (new Color(0, 0, 0, 50)).getRGB();
      this.lineColor = (new Color(255, 255, 255, 120)).getRGB();
      this.elementList = new HudElementList();
      this.settings = new HudEditorSettings();
      this.openedScreen = null;
      BlackOut.EVENT_BUS.subscribe(this, () -> {
         return !(BlackOut.mc.field_1755 instanceof HudEditor);
      });
   }

   public void initElements() {
      this.elementList.init();
   }

   public void method_25394(class_332 context, int mouseX, int mouseY, float delta) {
      float prevMx = this.mx;
      float prevMy = this.my;
      this.mx = (float)mouseX * (float)RenderUtils.getScale();
      this.my = (float)mouseY * (float)RenderUtils.getScale();
      float frameTime = delta / 20.0F;
      float scale = 1000.0F / (float)BlackOut.mc.method_1522().field_1480;
      this.screenHeight = (float)BlackOut.mc.method_1522().field_1477 * scale;
      this.screenWidth = (float)BlackOut.mc.method_1522().field_1480 * scale;
      this.mx *= scale;
      this.my *= scale;
      float deltaX = this.mx - prevMx;
      float deltaY = this.my - prevMy;
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null) {
         if (this.holding && this.moved() && this.still) {
            this.still = false;
            HudElement holding = this.holdElement();
            if (holding == null) {
               this.clearSelected();
               this.setState(HudEditor.State.Selecting);
            } else {
               this.setState(HudEditor.State.Moving);
            }
         }

         HudElement element;
         if (this.state == HudEditor.State.Moving) {
            for(Iterator var13 = this.picked.iterator(); var13.hasNext(); element.y += deltaY) {
               element = (HudElement)var13.next();
               element.x += deltaX;
            }
         }

         Managers.HUD.start(this.stack);
         Managers.HUD.render(this.stack, frameTime);
         this.renderBG();
         Managers.HUD.forEachElement((id, elementx) -> {
            elementx.renderQuad(this.stack, this.selectedElements.contains(elementx));
         });
         this.renderSelecting();
         Managers.HUD.end(this.stack);
         this.stack.method_22903();
         RenderUtils.unGuiScale(this.stack);
         this.settings.render(this.stack, frameTime, mouseX, mouseY);
         this.elementList.render(this.stack, frameTime, mouseX, mouseY);
         if (this.openedScreen != null) {
            this.openedScreen.onRender(frameTime, (double)mouseX, (double)mouseY);
         }

         this.stack.method_22909();
      }
   }

   private void renderBG() {
      RenderUtils.quad(this.stack, 0.0F, 0.0F, 1000.0F, this.screenHeight, this.bgColor);
      RenderUtils.fadeLine(this.stack, 0.0F, this.screenHeight / 2.0F, this.screenWidth, this.screenHeight / 2.0F, this.lineColor);
      RenderUtils.fadeLine(this.stack, this.screenWidth / 2.0F, 0.0F, this.screenWidth / 2.0F, this.screenHeight, this.lineColor);
   }

   private boolean moved() {
      double ox = (double)Math.abs(this.holdX - this.mx);
      double oy = (double)Math.abs(this.holdY - this.my);
      return Math.abs(ox * ox + oy * oy) > 0.5D;
   }

   @Event
   public void onKey(KeyEvent event) {
      if (this.openedScreen == null || !this.openedScreen.handleKey(event.key, event.pressed)) {
         if (event.pressed && (event.key == 261 || event.key == 259)) {
            this.picked.forEach(this::deleteElement);
            this.selectedElements.forEach(this::deleteElement);
         }

         this.settings.onKey(event.key, event.pressed);
      }
   }

   private void deleteElement(HudElement element) {
      Managers.HUD.remove(element.id);
      this.settings.onRemoved(element);
   }

   @Event
   public void onClick(MouseButtonEvent event) {
      if (this.openedScreen == null || !this.openedScreen.handleMouse(event.button, event.pressed)) {
         if (!this.settings.onMouse(event.button, event.pressed)) {
            if (this.elementList.onMouse(event.button, event.pressed)) {
               this.holding = event.pressed;
               this.still = true;
               this.holdX = this.mx;
               this.holdY = this.my;
               this.wasList = true;
            } else {
               HudElement element;
               if (event.button == 0) {
                  Managers.CONFIG.save(ConfigType.HUD);
                  Managers.CONFIG.save(ConfigType.Binds);
                  this.holding = event.pressed;
                  if (event.pressed) {
                     this.still = true;
                     this.holdX = this.mx;
                     this.holdY = this.my;
                     this.picked.clear();
                     element = this.holdElement();
                     if (element != null) {
                        this.picked.addAll(this.selectedElements);
                        if (!this.picked.contains(element)) {
                           this.picked.add(element);
                        }
                     }
                  } else {
                     this.picked.clear();
                     if (!this.wasList) {
                        if (this.still) {
                           this.clearSelected();
                           element = this.holdElement();
                           if (element != null) {
                              element.toggle();
                           }
                        } else if (this.state == HudEditor.State.Selecting) {
                           Managers.HUD.forEachElement((id, e) -> {
                              if (this.intersects(e) && !this.selectedElements.contains(e)) {
                                 this.selectedElements.add(e);
                              }

                           });
                        }
                     }

                     this.wasList = false;
                     this.setState(HudEditor.State.Inactive);
                  }
               }

               if (event.button == 1 && event.pressed) {
                  this.picked.clear();
                  this.selectedElements.clear();
                  element = this.holdElement();
                  if (element != null) {
                     this.settings.set(element);
                  }
               }

            }
         }
      }
   }

   @Event
   public void onScroll(MouseScrollEvent event) {
      if (this.openedScreen == null || !this.openedScreen.handleScroll(event.horizontal, event.vertical)) {
         this.elementList.onScroll(event.vertical);
      }
   }

   public void setScreen(ClickGuiScreen screen) {
      if (this.openedScreen != null) {
         this.openedScreen.onClose();
      }

      this.openedScreen = screen;
   }

   public void onListClick(HudElement element) {
      Managers.CONFIG.save(ConfigType.HUD);
      Managers.CONFIG.save(ConfigType.Binds);
      element.x = this.mx - element.getWidth() / 2.0F;
      element.y = this.my - element.getHeight() / 2.0F;
      this.picked.clear();
      this.picked.add(element);
   }

   private void clearSelected() {
      if (!Keys.get(340)) {
         this.selectedElements.clear();
      }
   }

   private HudElement holdElement() {
      Iterator var1 = Managers.HUD.getLoaded().entrySet().iterator();

      Entry entry;
      do {
         if (!var1.hasNext()) {
            return null;
         }

         entry = (Entry)var1.next();
      } while(!this.insideBounds((HudElement)entry.getValue()));

      return (HudElement)entry.getValue();
   }

   private void renderSelecting() {
      if (this.state == HudEditor.State.Selecting) {
         ColorRenderer renderer = ColorRenderer.getInstance();
         float x = Math.min(this.holdX, this.mx);
         float y = Math.min(this.holdY, this.my);
         float w = Math.max(this.holdX, this.mx) - x;
         float h = Math.max(this.holdY, this.my) - y;
         renderer.startRender(this.stack, class_5596.field_27382);
         renderer.quadShape(x, y, w, h, 0.0F, 1.0F, 0.0F, 0.0F, 0.15F);
         renderer.endRender();
         renderer.startRender(this.stack, class_5596.field_29345);
         renderer.quadOutlineShape(x, y, 0.0F, w, h, 1.0F, 0.0F, 0.0F, 0.5F);
         renderer.endRender();
      }
   }

   private boolean intersects(HudElement element) {
      float minX1 = Math.min(this.mx, this.holdX);
      float maxX1 = Math.max(this.mx, this.holdX);
      float minY1 = Math.min(this.my, this.holdY);
      float maxY1 = Math.max(this.my, this.holdY);
      return minX1 < element.x + element.getWidth() && maxX1 > element.x && minY1 < element.y + element.getHeight() && maxY1 > element.y;
   }

   private void setState(HudEditor.State state) {
      this.state = state;
   }

   private boolean insideBounds(HudElement element) {
      return this.insideBounds(element.x, element.y, element.getWidth(), element.getHeight());
   }

   private boolean insideBounds(float x, float y, float w, float h) {
      float offsetX = this.mx - x;
      float offsetY = this.my - y;
      return offsetX >= 0.0F && offsetY >= 0.0F && offsetX <= w && offsetY <= h;
   }

   private static enum State {
      Selecting,
      Moving,
      Inactive;

      // $FF: synthetic method
      private static HudEditor.State[] $values() {
         return new HudEditor.State[]{Selecting, Moving, Inactive};
      }
   }
}
