package bodevelopment.client.blackout.gui.clickgui.screens;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.gui.TextField;
import bodevelopment.client.blackout.gui.clickgui.ClickGuiScreen;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.module.modules.client.ThemeSettings;
import bodevelopment.client.blackout.module.setting.settings.ColorSetting;
import bodevelopment.client.blackout.randomstuff.BlackOutColor;
import bodevelopment.client.blackout.randomstuff.ShaderSetup;
import bodevelopment.client.blackout.rendering.renderer.Renderer;
import bodevelopment.client.blackout.rendering.shader.Shaders;
import bodevelopment.client.blackout.util.ColorUtils;
import bodevelopment.client.blackout.util.GuiColorUtils;
import bodevelopment.client.blackout.util.SelectedComponent;
import bodevelopment.client.blackout.util.render.RenderUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import java.awt.Color;
import net.minecraft.class_286;
import net.minecraft.class_287;
import net.minecraft.class_289;
import net.minecraft.class_290;
import net.minecraft.class_3532;
import net.minecraft.class_757;
import net.minecraft.class_293.class_5596;
import net.minecraft.class_5253.class_5254;
import org.joml.Matrix4f;

public class ColorScreen extends ClickGuiScreen {
   private final ColorSetting colorSetting;
   private int selecting = 0;
   private float prevCircleX = 0.0F;
   private float prevCircleY = 0.0F;
   private float prevHueX = 0.0F;
   private final float[] colorX = new float[7];
   private final float[] themeX = new float[3];
   private final ColorScreen.ColorField[] themeFields = new ColorScreen.ColorField[3];
   private final ColorScreen.ColorField[] textFields = new ColorScreen.ColorField[6];
   private final Color transparent = new Color(0, 0, 0, 0);
   private static final int offset2 = 200;

   public ColorScreen(ColorSetting colorSetting, String label) {
      super(label, 700.0F, 400.0F, true);
      this.colorSetting = colorSetting;

      int i;
      for(i = 0; i < this.textFields.length; ++i) {
         this.textFields[i] = new ColorScreen.ColorField();
      }

      for(i = 0; i < this.themeFields.length; ++i) {
         this.themeFields[i] = new ColorScreen.ColorField();
      }

   }

   public void render() {
      RenderUtils.roundedBottom(this.stack, 0.0F, 0.0F, 175.0F, this.height - 40.0F, 10.0F, 0.0F, GuiColorUtils.bg1.getRGB(), ColorUtils.SHADOW100I);
      RenderUtils.leftFade(this.stack, 165.0F, 0.0F, 20.0F, this.height - 30.0F, (new Color(0, 0, 0, 50)).getRGB());
      RenderUtils.bottomFade(this.stack, -10.0F, 0.0F, 195.0F, 20.0F, (new Color(0, 0, 0, 50)).getRGB());
      this.stack.method_22903();
      int clr = (new Color(15, 15, 15, 255)).getRGB();
      RenderUtils.rounded(this.stack, 0.0F, 20.0F, 175.0F, 30.0F, 2.0F, 2.0F, clr, clr);
      RenderUtils.rounded(this.stack, 0.0F, 60.0F, 175.0F, 30.0F, 2.0F, 2.0F, clr, clr);
      RenderUtils.rounded(this.stack, 0.0F, 100.0F, 175.0F, 30.0F, 2.0F, 2.0F, clr, clr);
      RenderUtils.rounded(this.stack, 135.0F, 28.0F, 28.0F, 14.0F, 3.0F, 3.0F, this.colorSetting.actual.getRGB(), this.colorSetting.actual.getRGB());
      RenderUtils.rounded(this.stack, 135.0F, 68.0F, 28.0F, 14.0F, 3.0F, 3.0F, ThemeSettings.getInstance().getMain(), ThemeSettings.getInstance().getMain());
      RenderUtils.rounded(this.stack, 135.0F, 108.0F, 28.0F, 14.0F, 3.0F, 3.0F, ThemeSettings.getInstance().getSecond(), ThemeSettings.getInstance().getSecond());
      BlackOut.FONT.text(this.stack, "Normal", 2.0F, 10.0F, 35.0F, Color.WHITE, false, true);
      BlackOut.FONT.text(this.stack, "Theme 1", 2.0F, 10.0F, 75.0F, Color.WHITE, false, true);
      BlackOut.FONT.text(this.stack, "Theme 2", 2.0F, 10.0F, 115.0F, Color.WHITE, false, true);
      this.stack.method_22909();
      this.mx -= 200.0D;
      this.stack.method_22903();
      this.stack.method_46416(200.0F, 0.0F, 0.0F);
      this.renderPicker();
      if (this.colorSetting.theme > 0) {
         this.renderThemeBars();
      } else {
         this.renderBars();
      }

      this.stack.method_22909();
      if (this.colorSetting.theme > 0) {
         this.handleThemeSliders();
      } else {
         this.handleSliders();
      }

      ColorScreen.ColorField[] var2 = this.textFields;
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         ColorScreen.ColorField field = var2[var4];
         field.textField.setActive(SelectedComponent.is(field.selectedId));
      }

      this.mx += 200.0D;
   }

   private void handleSliders() {
      float progress;
      float[] HSB;
      int rgb;
      int red;
      int green;
      int blue;
      switch(this.selecting) {
      case 1:
         progress = (float)class_3532.method_15350(class_3532.method_15370(this.mx, 0.0D, 500.0D), 0.0D, 1.0D);
         float clickBri = (float)(1.0D - class_3532.method_15350(class_3532.method_15370(this.my, 10.0D, 210.0D), 0.0D, 1.0D));
         float[] HSB = this.getHSB(false);
         red = Color.HSBtoRGB(HSB[0], progress, clickBri);
         green = class_5254.method_27765(red);
         blue = class_5254.method_27766(red);
         int blue = class_5254.method_27767(red);
         this.colorSetting.get().set(green, blue, blue);
         break;
      case 2:
         progress = (float)class_3532.method_15350(class_3532.method_15370(this.mx, 0.0D, 500.0D), 0.0D, 1.0D);
         HSB = this.getHSB(false);
         rgb = Color.HSBtoRGB(progress, HSB[1], HSB[2]);
         red = class_5254.method_27765(rgb);
         green = class_5254.method_27766(rgb);
         blue = class_5254.method_27767(rgb);
         this.colorSetting.get().set(red, green, blue);
         break;
      case 3:
         progress = (float)class_3532.method_15350(class_3532.method_15370(this.mx, 255.0D, 500.0D), 0.0D, 1.0D) * 255.0F;
         this.colorSetting.get().setRed((int)progress);
         break;
      case 4:
         progress = (float)class_3532.method_15350(class_3532.method_15370(this.mx, 255.0D, 500.0D), 0.0D, 1.0D) * 255.0F;
         this.colorSetting.get().setGreen((int)progress);
         break;
      case 5:
         progress = (float)class_3532.method_15350(class_3532.method_15370(this.mx, 255.0D, 500.0D), 0.0D, 1.0D) * 255.0F;
         this.colorSetting.get().setBlue((int)progress);
         break;
      case 6:
         progress = (float)class_3532.method_15350(class_3532.method_15370(this.mx, 0.0D, 245.0D), 0.0D, 1.0D) * 255.0F;
         this.colorSetting.get().setAlpha((int)progress);
         break;
      case 7:
         progress = (float)class_3532.method_15350(class_3532.method_15370(this.mx, 0.0D, 245.0D), 0.0D, 1.0D);
         HSB = this.getHSB(false);
         rgb = Color.HSBtoRGB(HSB[0], progress, HSB[2]);
         red = class_5254.method_27765(rgb);
         green = class_5254.method_27766(rgb);
         blue = class_5254.method_27767(rgb);
         this.colorSetting.get().set(red, green, blue);
         break;
      case 8:
         progress = (float)class_3532.method_15350(class_3532.method_15370(this.mx, 0.0D, 245.0D), 0.0D, 1.0D);
         HSB = this.getHSB(false);
         rgb = Color.HSBtoRGB(HSB[0], HSB[1], progress);
         red = class_5254.method_27765(rgb);
         green = class_5254.method_27766(rgb);
         blue = class_5254.method_27767(rgb);
         this.colorSetting.get().set(red, green, blue);
      }

   }

   private void handleThemeSliders() {
      switch(this.selecting) {
      case 1:
         this.colorSetting.saturation = (float)class_3532.method_15350(class_3532.method_15370(this.mx, 0.0D, 500.0D), 0.0D, 1.0D) * 2.0F - 1.0F;
         break;
      case 2:
         this.colorSetting.brightness = (float)class_3532.method_15350(class_3532.method_15370(this.mx, 0.0D, 500.0D), 0.0D, 1.0D) * 2.0F - 1.0F;
         break;
      case 3:
         this.colorSetting.alpha = (int)(class_3532.method_15350(class_3532.method_15370(this.mx, 0.0D, 500.0D), 0.0D, 1.0D) * 255.0D);
      }

   }

   protected boolean insideScrollBounds() {
      return this.mx > -10.0D && this.mx < 175.0D && this.my > -50.0D && this.my < (double)(this.height + 10.0F);
   }

   public void onMouse(int button, boolean state) {
      if (button == 0) {
         boolean b = false;
         ColorScreen.ColorField[] var4 = this.colorSetting.theme == 0 ? this.textFields : this.themeFields;
         int var5 = var4.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            ColorScreen.ColorField textField = var4[var6];
            if (textField.textField.click(0, state)) {
               b = true;
               SelectedComponent.setId(textField.selectedId);
               break;
            }
         }

         if (state && !b) {
            if (this.colorSetting.theme == 0) {
               if (this.inside(200, 700, 10, 210)) {
                  this.selecting = 1;
               }

               if (this.inside(200, 700, 225, 238)) {
                  this.selecting = 2;
               }

               if (this.inside(455, 700, 260, 273)) {
                  this.selecting = 3;
               }

               if (this.inside(455, 700, 300, 313)) {
                  this.selecting = 4;
               }

               if (this.inside(455, 700, 340, 353)) {
                  this.selecting = 5;
               }

               if (this.inside(200, 445, 340, 353)) {
                  this.selecting = 6;
               }

               if (this.inside(200, 445, 260, 273)) {
                  this.selecting = 7;
               }

               if (this.inside(200, 445, 300, 313)) {
                  this.selecting = 8;
               }
            } else {
               if (this.inside(200, 700, 260, 273)) {
                  this.selecting = 1;
               }

               if (this.inside(200, 700, 300, 313)) {
                  this.selecting = 2;
               }

               if (this.inside(200, 700, 340, 353)) {
                  this.selecting = 3;
               }
            }

            if (this.selecting > 0) {
               Managers.CONFIG.saveAll();
            } else if (this.inside(0, 200, 20, 50)) {
               this.colorSetting.theme = 0;
               Managers.CONFIG.saveAll();
            } else if (this.inside(0, 200, 60, 90)) {
               this.colorSetting.theme = 1;
               Managers.CONFIG.saveAll();
            } else if (this.inside(0, 200, 100, 130)) {
               this.colorSetting.theme = 2;
               Managers.CONFIG.saveAll();
            }
         } else {
            this.selecting = 0;
         }

      }
   }

   public void onKey(int key, boolean state) {
      int var5;
      if (key == 257) {
         ColorScreen.ColorField field = null;
         ColorScreen.ColorField[] var9 = this.textFields;
         var5 = var9.length;

         ColorScreen.ColorField colorField;
         int var10;
         for(var10 = 0; var10 < var5; ++var10) {
            colorField = var9[var10];
            if (SelectedComponent.is(colorField.selectedId)) {
               field = colorField;
               break;
            }
         }

         var9 = this.themeFields;
         var5 = var9.length;

         for(var10 = 0; var10 < var5; ++var10) {
            colorField = var9[var10];
            if (SelectedComponent.is(colorField.selectedId)) {
               field = colorField;
               break;
            }
         }

         if (field != null) {
            SelectedComponent.reset();
         }

      } else {
         ColorScreen.ColorField[] var3 = this.textFields;
         int var4 = var3.length;

         ColorScreen.ColorField field;
         for(var5 = 0; var5 < var4; ++var5) {
            field = var3[var5];
            field.textField.type(key, state);
         }

         var3 = this.themeFields;
         var4 = var3.length;

         for(var5 = 0; var5 < var4; ++var5) {
            field = var3[var5];
            field.textField.type(key, state);
         }

      }
   }

   private void setValue(ColorScreen.ColorField colorField, int id) {
      int val = 0;

      try {
         val = Integer.parseInt(colorField.textField.getContent());
      } catch (NumberFormatException var9) {
      }

      val = class_3532.method_15340(val, 0, 255);
      float[] hsb;
      int rgb;
      switch(id) {
      case 0:
         this.colorSetting.get().setRed(val);
         break;
      case 1:
         this.colorSetting.get().setGreen(val);
         break;
      case 2:
         this.colorSetting.get().setBlue(val);
         break;
      case 3:
         this.colorSetting.get().setAlpha(val);
         break;
      case 4:
         hsb = this.getHSB(false);
         rgb = Color.HSBtoRGB(hsb[0], (float)val / 255.0F, hsb[2]);
         this.colorSetting.get().set(class_5254.method_27765(rgb), class_5254.method_27766(rgb), class_5254.method_27767(rgb));
         break;
      case 5:
         hsb = this.getHSB(false);
         rgb = Color.HSBtoRGB(hsb[0], hsb[1], (float)val / 255.0F);
         this.colorSetting.get().set(class_5254.method_27765(rgb), class_5254.method_27766(rgb), class_5254.method_27767(rgb));
         break;
      case 6:
         try {
            this.colorSetting.saturation = Float.parseFloat(colorField.textField.getContent());
         } catch (NumberFormatException var8) {
         }
         break;
      case 7:
         try {
            this.colorSetting.brightness = Float.parseFloat(colorField.textField.getContent());
         } catch (NumberFormatException var7) {
         }
         break;
      case 8:
         try {
            this.colorSetting.alpha = val;
         } catch (NumberFormatException var6) {
         }
      }

   }

   private boolean inside(int minX, int maxX, int minY, int maxY) {
      return this.mx >= (double)minX && this.mx <= (double)maxX && this.my >= (double)minY && this.my <= (double)maxY;
   }

   private void renderHueBar(float x, float y, float w, float h) {
      float hue = this.getHSB(false)[0];
      RenderUtils.roundedShadow(this.stack, x, y, w, h, 0.0F, 10.0F, (new Color(0, 0, 0, 100)).getRGB());
      this.renderHueQuad(x, y, w, h);
      float hueX;
      if (this.selecting == 2) {
         hueX = (float)class_3532.method_15350(this.mx, 0.0D, 500.0D);
      } else {
         hueX = class_3532.method_16439(hue, x, x + w);
      }

      this.prevHueX = class_3532.method_37166(this.prevHueX, hueX, this.frameTime * 20.0F);
      RenderUtils.roundedShadow(this.stack, this.prevHueX, y, 0.0F, h, 0.0F, 10.0F, (new Color(0, 0, 0, 100)).getRGB());
      RenderUtils.quad(this.stack, this.prevHueX - 3.0F, y - 2.0F, 6.0F, h + 4.0F, Color.HSBtoRGB(class_3532.method_37960(this.prevHueX, 0.0F, 500.0F), 1.0F, 1.0F));
   }

   private void renderBars() {
      this.renderHueBar(0.0F, 225.0F, 500.0F, 13.0F);
      this.renderBar(255.0F, 260.0F, 245.0F, 0, (float)this.colorSetting.get().red / 255.0F, "Red");
      this.renderBar(255.0F, 300.0F, 245.0F, 1, (float)this.colorSetting.get().green / 255.0F, "Green");
      this.renderBar(255.0F, 340.0F, 245.0F, 2, (float)this.colorSetting.get().blue / 255.0F, "Blue");
      this.renderBar(0.0F, 340.0F, 245.0F, 3, (float)this.colorSetting.get().alpha / 255.0F, "Alpha");
      this.renderBar(0.0F, 260.0F, 245.0F, 4, this.getHSB(false)[1], "Saturation");
      this.renderBar(0.0F, 300.0F, 245.0F, 5, this.getHSB(false)[2], "Brightness");
   }

   private void renderThemeBars() {
      this.renderThemeBar(0.0F, 260.0F, 500.0F, 0, this.colorSetting.saturation, this.colorSetting.saturation / 2.0F + 0.5F, "Saturation");
      this.renderThemeBar(0.0F, 300.0F, 500.0F, 1, this.colorSetting.brightness, this.colorSetting.brightness / 2.0F + 0.5F, "Brightness");
      this.renderThemeBar(0.0F, 340.0F, 500.0F, 2, (float)this.colorSetting.alpha, (float)this.colorSetting.alpha / 255.0F, "Alpha");
   }

   private void renderThemeBar(float x, float y, float w, int id, float number, float p, String name) {
      this.themeX[id] = class_3532.method_16439(Math.min(this.frameTime * 20.0F, 1.0F), this.themeX[id], p);
      Color left;
      Color right;
      switch(id) {
      case 1:
         left = Color.BLACK;
         right = Color.WHITE;
         break;
      case 2:
         left = new Color(255, 255, 255, 0);
         right = Color.WHITE;
         break;
      default:
         left = Color.WHITE;
         right = Color.RED;
      }

      RenderUtils.roundedShadow(this.stack, x, y, w, 13.0F, 0.0F, 10.0F, ColorUtils.SHADOW100I);
      this.renderQuad(x, y, w, 13.0F, (float)left.getRed() / 255.0F, (float)left.getGreen() / 255.0F, (float)left.getBlue() / 255.0F, (float)left.getAlpha() / 255.0F, (float)right.getRed() / 255.0F, (float)right.getGreen() / 255.0F, (float)right.getBlue() / 255.0F, (float)right.getAlpha() / 255.0F);
      RenderUtils.roundedShadow(this.stack, x + w * this.themeX[id], y - 2.0F, 0.0F, 17.0F, 0.0F, 10.0F, ColorUtils.SHADOW100I);
      RenderUtils.quad(this.stack, x - 3.0F + w * this.themeX[id], y - 2.0F, 6.0F, 17.0F, ColorUtils.lerpColor((double)this.themeX[id], left, right).getRGB());
      BlackOut.FONT.text(this.stack, name, 1.5F, x, y - 10.0F, Color.WHITE, false, true);
      ColorScreen.ColorField field = this.themeFields[id];
      if (!SelectedComponent.is(field.selectedId)) {
         field.textField.setContent(id == 2 ? String.valueOf((int)number) : String.format("%.2f", number));
      } else {
         this.setValue(field, id + 6);
      }

      field.textField.render(this.stack, 1.5F, this.mx, this.my, x + 210.0F, y - 12.0F, 22.0F, 3.0F, 5.0F, 3.0F, Color.WHITE, this.transparent);
   }

   private void renderBar(float x, float y, float w, int id, float p, String name) {
      this.colorX[id] = class_3532.method_16439(Math.min(this.frameTime * 20.0F, 1.0F), this.colorX[id], p);
      BlackOutColor left = this.colorSetting.get().copy();
      BlackOutColor right = this.colorSetting.get().copy();
      float[] HSB;
      int rgb;
      int red;
      int green;
      int blue;
      switch(id) {
      case 0:
         left.setRed(0);
         right.setRed(255);
         left.setAlpha(255);
         right.setAlpha(255);
         break;
      case 1:
         left.setGreen(0);
         right.setGreen(255);
         left.setAlpha(255);
         right.setAlpha(255);
         break;
      case 2:
         left.setBlue(0);
         right.setBlue(255);
         left.setAlpha(255);
         right.setAlpha(255);
         break;
      case 3:
         left.setAlpha(0);
         right.setAlpha(255);
         break;
      case 4:
         left.setRed(255);
         left.setGreen(255);
         left.setBlue(255);
         HSB = this.getHSB(false);
         rgb = Color.HSBtoRGB(HSB[0], 1.0F, HSB[2]);
         red = class_5254.method_27765(rgb);
         green = class_5254.method_27766(rgb);
         blue = class_5254.method_27767(rgb);
         right.setRed(red);
         right.setGreen(green);
         right.setBlue(blue);
         left.setAlpha(255);
         right.setAlpha(255);
         break;
      case 5:
         left.setRed(0);
         left.setGreen(0);
         left.setBlue(0);
         HSB = this.getHSB(false);
         rgb = Color.HSBtoRGB(HSB[0], HSB[1], 1.0F);
         red = class_5254.method_27765(rgb);
         green = class_5254.method_27766(rgb);
         blue = class_5254.method_27767(rgb);
         right.setRed(red);
         right.setGreen(green);
         right.setBlue(blue);
         left.setAlpha(255);
         right.setAlpha(255);
      }

      RenderUtils.roundedShadow(this.stack, x, y, w, 13.0F, 0.0F, 10.0F, (new Color(0, 0, 0, 100)).getRGB());
      this.renderQuad(x, y, w, 13.0F, (float)left.red / 255.0F, (float)left.green / 255.0F, (float)left.blue / 255.0F, (float)left.alpha / 255.0F, (float)right.red / 255.0F, (float)right.green / 255.0F, (float)right.blue / 255.0F, (float)right.alpha / 255.0F);
      RenderUtils.roundedShadow(this.stack, x + w * this.colorX[id], y - 2.0F, 0.0F, 17.0F, 0.0F, 10.0F, (new Color(0, 0, 0, 100)).getRGB());
      RenderUtils.quad(this.stack, x - 3.0F + w * this.colorX[id], y - 2.0F, 6.0F, 17.0F, left.lerp((double)this.colorX[id], right).getColor().getRGB());
      BlackOut.FONT.text(this.stack, name, 1.5F, x, y - 10.0F, Color.WHITE, false, true);
      ColorScreen.ColorField field = this.textFields[id];
      if (!SelectedComponent.is(field.selectedId)) {
         field.textField.setContent(String.valueOf(Math.round(p * 255.0F)));
      } else {
         this.setValue(field, id);
      }

      field.textField.render(this.stack, 1.5F, this.mx, this.my, x + 210.0F, y - 12.0F, 22.0F, 3.0F, 5.0F, 3.0F, Color.WHITE, this.transparent);
   }

   private void renderQuad(float x, float y, float w, float h, float rl, float gl, float bl, float al, float rr, float gr, float br, float ar) {
      Matrix4f matrix4f = this.stack.method_23760().method_23761();
      RenderSystem.enableBlend();
      RenderSystem.setShader(class_757::method_34540);
      class_287 bufferBuilder = class_289.method_1348().method_1349();
      bufferBuilder.method_1328(class_5596.field_27382, class_290.field_1576);
      bufferBuilder.method_22918(matrix4f, x + w, y, 0.0F).method_22915(rr, gr, br, ar).method_1344();
      bufferBuilder.method_22918(matrix4f, x, y, 0.0F).method_22915(rl, gl, bl, al).method_1344();
      bufferBuilder.method_22918(matrix4f, x, y + h, 0.0F).method_22915(rl, gl, bl, al).method_1344();
      bufferBuilder.method_22918(matrix4f, x + w, y + h, 0.0F).method_22915(rr, gr, br, ar).method_1344();
      class_286.method_43433(bufferBuilder.method_1326());
      RenderSystem.disableBlend();
   }

   private void renderHueQuad(float x, float y, float w, float h) {
      Matrix4f matrix4f = this.stack.method_23760().method_23761();
      RenderSystem.enableBlend();
      RenderSystem.setShader(class_757::method_34540);
      class_287 bufferBuilder = class_289.method_1348().method_1349();
      bufferBuilder.method_1328(class_5596.field_27382, class_290.field_1576);
      bufferBuilder.method_22918(matrix4f, x + w / 6.0F * 6.0F, y, 0.0F).method_22915(1.0F, 0.0F, 0.0F, 1.0F).method_1344();
      bufferBuilder.method_22918(matrix4f, x + w / 6.0F * 5.0F, y, 0.0F).method_22915(1.0F, 0.0F, 1.0F, 1.0F).method_1344();
      bufferBuilder.method_22918(matrix4f, x + w / 6.0F * 5.0F, y + h, 0.0F).method_22915(1.0F, 0.0F, 1.0F, 1.0F).method_1344();
      bufferBuilder.method_22918(matrix4f, x + w / 6.0F * 6.0F, y + h, 0.0F).method_22915(1.0F, 0.0F, 0.0F, 1.0F).method_1344();
      bufferBuilder.method_22918(matrix4f, x + w / 6.0F * 5.0F, y, 0.0F).method_22915(1.0F, 0.0F, 1.0F, 1.0F).method_1344();
      bufferBuilder.method_22918(matrix4f, x + w / 6.0F * 4.0F, y, 0.0F).method_22915(0.0F, 0.0F, 1.0F, 1.0F).method_1344();
      bufferBuilder.method_22918(matrix4f, x + w / 6.0F * 4.0F, y + h, 0.0F).method_22915(0.0F, 0.0F, 1.0F, 1.0F).method_1344();
      bufferBuilder.method_22918(matrix4f, x + w / 6.0F * 5.0F, y + h, 0.0F).method_22915(1.0F, 0.0F, 1.0F, 1.0F).method_1344();
      bufferBuilder.method_22918(matrix4f, x + w / 6.0F * 4.0F, y, 0.0F).method_22915(0.0F, 0.0F, 1.0F, 1.0F).method_1344();
      bufferBuilder.method_22918(matrix4f, x + w / 6.0F * 3.0F, y, 0.0F).method_22915(0.0F, 1.0F, 1.0F, 1.0F).method_1344();
      bufferBuilder.method_22918(matrix4f, x + w / 6.0F * 3.0F, y + h, 0.0F).method_22915(0.0F, 1.0F, 1.0F, 1.0F).method_1344();
      bufferBuilder.method_22918(matrix4f, x + w / 6.0F * 4.0F, y + h, 0.0F).method_22915(0.0F, 0.0F, 1.0F, 1.0F).method_1344();
      bufferBuilder.method_22918(matrix4f, x + w / 6.0F * 3.0F, y, 0.0F).method_22915(0.0F, 1.0F, 1.0F, 1.0F).method_1344();
      bufferBuilder.method_22918(matrix4f, x + w / 6.0F * 2.0F, y, 0.0F).method_22915(0.0F, 1.0F, 0.0F, 1.0F).method_1344();
      bufferBuilder.method_22918(matrix4f, x + w / 6.0F * 2.0F, y + h, 0.0F).method_22915(0.0F, 1.0F, 0.0F, 1.0F).method_1344();
      bufferBuilder.method_22918(matrix4f, x + w / 6.0F * 3.0F, y + h, 0.0F).method_22915(0.0F, 1.0F, 1.0F, 1.0F).method_1344();
      bufferBuilder.method_22918(matrix4f, x + w / 6.0F * 2.0F, y, 0.0F).method_22915(0.0F, 1.0F, 0.0F, 1.0F).method_1344();
      bufferBuilder.method_22918(matrix4f, x + w / 6.0F, y, 0.0F).method_22915(1.0F, 1.0F, 0.0F, 1.0F).method_1344();
      bufferBuilder.method_22918(matrix4f, x + w / 6.0F, y + h, 0.0F).method_22915(1.0F, 1.0F, 0.0F, 1.0F).method_1344();
      bufferBuilder.method_22918(matrix4f, x + w / 6.0F * 2.0F, y + h, 0.0F).method_22915(0.0F, 1.0F, 0.0F, 1.0F).method_1344();
      bufferBuilder.method_22918(matrix4f, x + w / 6.0F, y, 0.0F).method_22915(1.0F, 1.0F, 0.0F, 1.0F).method_1344();
      bufferBuilder.method_22918(matrix4f, x, y, 0.0F).method_22915(1.0F, 0.0F, 0.0F, 1.0F).method_1344();
      bufferBuilder.method_22918(matrix4f, x, y + h, 0.0F).method_22915(1.0F, 0.0F, 0.0F, 1.0F).method_1344();
      bufferBuilder.method_22918(matrix4f, x + w / 6.0F, y + h, 0.0F).method_22915(1.0F, 1.0F, 0.0F, 1.0F).method_1344();
      class_286.method_43433(bufferBuilder.method_1326());
      RenderSystem.disableBlend();
   }

   private void renderPicker() {
      int rgb = Color.HSBtoRGB(this.getHSB(true)[0], 1.0F, 1.0F);
      int red = class_5254.method_27765(rgb);
      int green = class_5254.method_27766(rgb);
      int blue = class_5254.method_27767(rgb);
      this.renderPickerQuad(10.0F, 500.0F, 200.0F, (float)red / 255.0F, (float)green / 255.0F, (float)blue / 255.0F);
      float circleX;
      float circleY;
      if (this.colorSetting.theme == 0 && this.selecting == 1) {
         circleX = (float)class_3532.method_15350(this.mx, 0.0D, 500.0D);
         circleY = (float)class_3532.method_15350(this.my, 10.0D, 210.0D);
      } else {
         float[] HSB = this.getHSB(false);
         circleX = HSB[1] * 500.0F;
         circleY = (float)class_3532.method_48781(HSB[2], 210, 10);
      }

      this.prevCircleX = class_3532.method_37166(this.prevCircleX, circleX, this.frameTime * 20.0F);
      this.prevCircleY = class_3532.method_37166(this.prevCircleY, circleY, this.frameTime * 20.0F);
      BlackOutColor color = this.colorSetting.get();
      RenderUtils.rounded(this.stack, this.prevCircleX, this.prevCircleY, 0.0F, 0.0F, 10.0F, 4.0F, ColorUtils.withAlpha(color.getRGB(), 255), ColorUtils.SHADOW100I);
   }

   private void renderPickerQuad(float oy, float w, float h, float red, float green, float blue) {
      Renderer.setMatrices(this.stack);
      Matrix4f matrix4f = Renderer.emptyMatrix;
      RenderSystem.enableBlend();
      class_287 bufferBuilder = class_289.method_1348().method_1349();
      bufferBuilder.method_1328(class_5596.field_27382, class_290.field_1592);
      bufferBuilder.method_22918(matrix4f, w, oy, 0.0F).method_1344();
      bufferBuilder.method_22918(matrix4f, 0.0F, oy, 0.0F).method_1344();
      bufferBuilder.method_22918(matrix4f, 0.0F, oy + h, 0.0F).method_1344();
      bufferBuilder.method_22918(matrix4f, w, oy + h, 0.0F).method_1344();
      Shaders.picker.render(bufferBuilder, new ShaderSetup((setup) -> {
         setup.set("pos", 0.0F, oy, w, h);
         setup.set("clr", red, green, blue);
      }));
      RenderSystem.disableBlend();
   }

   private float[] getHSB(boolean unmodified) {
      BlackOutColor value = unmodified ? this.colorSetting.getUnmodified() : this.colorSetting.get();
      return Color.RGBtoHSB(value.red, value.green, value.blue, new float[3]);
   }

   private static class ColorField {
      private final TextField textField = new TextField();
      private final int selectedId = SelectedComponent.nextId();
   }
}
