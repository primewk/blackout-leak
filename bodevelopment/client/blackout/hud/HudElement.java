package bodevelopment.client.blackout.hud;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.enums.ConfigType;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.rendering.renderer.ColorRenderer;
import com.google.gson.JsonObject;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.class_4587;
import net.minecraft.class_293.class_5596;

public class HudElement {
   public final List<SettingGroup> settingGroups = new ArrayList();
   protected final SettingGroup sgScale = this.addGroup("Scale");
   private final Setting<Double> scale;
   public float x;
   public float y;
   private float width;
   private float height;
   public final String name;
   public final String description;
   public int id;
   public boolean enabled;
   public long toggleTime;
   protected float frameTime;
   protected class_4587 stack;

   public HudElement(String name, String description) {
      this.scale = this.sgScale.d("Scale", 1.0D, 0.1D, 10.0D, 0.1D, ".");
      this.x = 0.0F;
      this.y = 0.0F;
      this.width = 0.0F;
      this.height = 0.0F;
      this.enabled = true;
      this.toggleTime = 0L;
      this.name = name;
      this.description = description;
   }

   protected float getScale() {
      return ((Double)this.scale.get()).floatValue();
   }

   public void toggle() {
      Managers.CONFIG.save(ConfigType.HUD);
      this.enabled = !this.enabled;
   }

   protected void setSize(float width, float height) {
      this.width = width;
      this.height = height;
   }

   public void renderElement(class_4587 stack, float frameTime) {
      if (this.enabled || BlackOut.mc.field_1755 instanceof HudEditor) {
         this.frameTime = frameTime;
         this.stack = stack;
         this.pushStack(stack);
         this.render();
         this.popStack(stack);
      }
   }

   public void renderQuad(class_4587 stack, boolean selected) {
      this.pushStack(stack);
      ColorRenderer renderer = ColorRenderer.getInstance();
      float r = 1.0F;
      float g = this.enabled ? 1.0F : 0.0F;
      float b = this.enabled ? 1.0F : 0.0F;
      renderer.quad(stack, 0.0F, 0.0F, 0.0F, this.width, this.height, r, g, b, selected ? 0.1F : 0.05F);
      renderer.startRender(stack, class_5596.field_29345);
      renderer.quadOutlineShape(0.0F, 0.0F, 0.0F, this.width, this.height, r, g, b, selected ? 1.0F : 0.5F);
      renderer.endRender();
      this.popStack(stack);
   }

   private void pushStack(class_4587 stack) {
      stack.method_22903();
      stack.method_46416(this.x, this.y, 0.0F);
      stack.method_22905(this.getScale(), this.getScale(), 0.0F);
   }

   private void popStack(class_4587 stack) {
      stack.method_22909();
   }

   protected void render() {
   }

   public void onRemove() {
   }

   protected SettingGroup addGroup(String name) {
      SettingGroup group = new SettingGroup(name);
      this.settingGroups.add(group);
      return group;
   }

   public void readSettings(JsonObject jsonObject) {
      this.forEachSetting((s) -> {
         s.read(jsonObject);
      });
   }

   public void writeSettings(JsonObject jsonObject) {
      this.forEachSetting((s) -> {
         s.write(jsonObject);
      });
   }

   public void forEachSetting(Consumer<? super Setting<?>> consumer) {
      this.settingGroups.forEach((group) -> {
         group.settings.forEach(consumer);
      });
   }

   public float getWidth() {
      return this.width * this.getScale();
   }

   public float getHeight() {
      return this.height * this.getScale();
   }

   protected static class Component {
      public final String text;
      public final float width;
      public final Color color;
      public final boolean bold;

      public Component(String text, Color color) {
         this.text = text;
         this.width = BlackOut.FONT.getWidth(text);
         this.color = color;
         this.bold = false;
      }

      public Component(String text) {
         this.text = text;
         this.width = BlackOut.FONT.getWidth(text);
         this.color = null;
         this.bold = false;
      }

      public Component(String text, Boolean bold) {
         this.text = text;
         this.width = bold ? BlackOut.BOLD_FONT.getWidth(text) : BlackOut.FONT.getWidth(text);
         this.color = null;
         this.bold = bold;
      }

      public Component(String text, Color color, Boolean bold) {
         this.text = text;
         this.width = bold ? BlackOut.BOLD_FONT.getWidth(text) : BlackOut.FONT.getWidth(text);
         this.color = color;
         this.bold = bold;
      }
   }
}
