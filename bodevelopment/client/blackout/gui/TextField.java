package bodevelopment.client.blackout.gui;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.keys.Keys;
import bodevelopment.client.blackout.util.render.RenderUtils;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.class_3532;
import net.minecraft.class_4587;
import org.lwjgl.glfw.GLFW;

public class TextField {
   private static final Map<String, String> shiftModified = new HashMap();
   private static final Map<String, String> altModified = new HashMap();
   private String content = "";
   private int typingIndex = 0;
   private long lastType = 0L;
   private boolean active = false;
   private int heldKey = 0;
   private long prevHeld = 0L;
   private float width;
   private float height;
   private double mx;
   private double my;
   private float scale;
   private float radius;

   public void render(class_4587 stack, float scale, double mx, double my, float x, float y, float width, float height, float radius, float shadow, Color textColor, Color bgColor) {
      this.width = width;
      this.height = height;
      this.mx = mx - (double)x;
      this.my = my - (double)y;
      this.scale = scale;
      this.radius = radius;
      this.limitIndex();
      RenderUtils.rounded(stack, x, y, width, height, radius, shadow, bgColor.getRGB(), (new Color(0, 0, 0, (int)Math.floor((double)bgColor.getAlpha() * 0.6D))).getRGB());
      BlackOut.FONT.text(stack, this.content, scale, x, y + height / 2.0F, textColor, false, true);
      float offset = this.getOffset();
      if (!Keys.get(this.heldKey)) {
         this.heldKey = 0;
      }

      if (this.heldKey > 0 && System.currentTimeMillis() - this.prevHeld > 500L) {
         this.type(this.heldKey, true);
         this.prevHeld += 50L;
      }

      if (this.active && (System.currentTimeMillis() - this.lastType) % 1000L < 500L) {
         RenderUtils.quad(stack, x + offset - 2.0F, y - BlackOut.FONT.getHeight() * scale / 2.0F + 1.0F, scale, BlackOut.FONT.getHeight() * scale - 2.0F, textColor.getRGB());
      }

   }

   public void setActive(boolean active) {
      this.active = active;
   }

   public void setContent(String content) {
      this.content = content;
   }

   public String getContent() {
      return this.content;
   }

   private float getOffset() {
      float offset = 0.0F;
      int index = 0;
      if (index >= this.typingIndex) {
         return offset;
      } else {
         String[] var3 = this.content.split("");
         int var4 = var3.length;

         for(int var5 = 0; var5 < var4; ++var5) {
            String c = var3[var5];
            offset += BlackOut.FONT.getWidth(c) * this.scale;
            ++index;
            if (index >= this.typingIndex) {
               return offset;
            }
         }

         return 0.0F;
      }
   }

   public boolean click(int button, boolean state) {
      if (this.isEmpty()) {
         return false;
      } else if (!(this.mx < (double)(-this.radius)) && !(this.mx > (double)(this.width + this.radius)) && !(this.my < (double)(-this.radius)) && !(this.my > (double)(this.height + this.radius))) {
         if (button == 0 && state) {
            this.typingIndex = this.getIndex();
            this.lastType = System.currentTimeMillis();
            return true;
         } else {
            return true;
         }
      } else {
         return false;
      }
   }

   private int getIndex() {
      float offset = 0.0F;
      int i = 0;
      int closestI = 0;
      double closest = 1000.0D;
      double d = Math.abs((double)offset - this.mx);
      if (d < closest) {
         closest = d;
      }

      String[] var8 = this.content.split("");
      int var9 = var8.length;

      for(int var10 = 0; var10 < var9; ++var10) {
         String c = var8[var10];
         offset += BlackOut.FONT.getWidth(c) * this.scale;
         ++i;
         d = Math.abs((double)offset - this.mx);
         if (d < closest) {
            closestI = i;
            closest = d;
         }
      }

      return closestI;
   }

   public void type(int key, boolean state) {
      if (state && (key >= 39 && key <= 92 || key == 262 || key == 263 || key == 259 || key == 32)) {
         if (key != this.heldKey) {
            this.prevHeld = System.currentTimeMillis();
         }

         this.heldKey = key;
         this.limitIndex();
         switch(key) {
         case 32:
            this.addChar(" ");
            return;
         case 259:
            if (this.typingIndex > 0) {
               String mogus = this.content;
               this.content = mogus.substring(0, this.typingIndex - 1);
               if (mogus.length() >= this.typingIndex) {
                  String var10001 = this.content;
                  this.content = var10001 + mogus.substring(this.typingIndex);
               }

               this.lastType = System.currentTimeMillis();
               if (this.typingIndex > 0) {
                  --this.typingIndex;
               }
            }

            return;
         case 262:
            this.lastType = System.currentTimeMillis();
            this.typingIndex = class_3532.method_15340(this.typingIndex + 1, 0, this.content.length());
            return;
         case 263:
            this.lastType = System.currentTimeMillis();
            this.typingIndex = class_3532.method_15340(this.typingIndex - 1, 0, this.content.length());
            return;
         default:
            this.addChar(this.modify(GLFW.glfwGetKeyName(key, 1)));
         }
      }

   }

   private void limitIndex() {
      this.typingIndex = class_3532.method_15340(this.typingIndex, 0, this.content.length());
   }

   private String modify(String string) {
      boolean shift = Keys.get(340);
      boolean alt = Keys.get(346);
      if (shift && alt) {
         return "";
      } else if (shift) {
         return shiftModified.containsKey(string) ? (String)shiftModified.get(string) : string.toUpperCase();
      } else {
         return alt && altModified.containsKey(string) ? (String)altModified.get(string) : string;
      }
   }

   private void addChar(String c) {
      String mogus = this.content;
      this.content = "";
      String var10001;
      if (mogus.length() >= this.typingIndex) {
         var10001 = this.content;
         this.content = var10001 + mogus.substring(0, this.typingIndex);
      }

      this.content = this.content + c;
      if (mogus.length() >= this.typingIndex) {
         var10001 = this.content;
         this.content = var10001 + mogus.substring(this.typingIndex);
      }

      this.lastType = System.currentTimeMillis();
      ++this.typingIndex;
   }

   public boolean isEmpty() {
      return this.content.equals("");
   }

   public void clear() {
      this.content = "";
      this.typingIndex = 0;
   }

   static {
      shiftModified.put("1", "!");
      shiftModified.put("2", "\"");
      shiftModified.put("3", "#");
      shiftModified.put("4", "¤");
      shiftModified.put("5", "%");
      shiftModified.put("6", "&");
      shiftModified.put("7", "/");
      shiftModified.put("8", "(");
      shiftModified.put("9", ")");
      shiftModified.put("0", "=");
      shiftModified.put("+", "?");
      shiftModified.put(",", ";");
      shiftModified.put(".", ":");
      shiftModified.put("-", "_");
      altModified.put("2", "@");
      altModified.put("3", "£");
      altModified.put("4", "$");
      altModified.put("5", "€");
      altModified.put("7", "{");
      altModified.put("8", "[");
      altModified.put("9", "]");
      altModified.put("0", "}");
      altModified.put("+", "\\");
   }
}
