package bodevelopment.client.blackout.rendering.font;

import bodevelopment.client.blackout.rendering.texture.BOTextures;
import bodevelopment.client.blackout.util.FileUtils;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class BOFont {
   private final String name;
   private final int fontSize;
   private int width = 0;
   private int height = 0;
   public final Map<Integer, CharInfo> map = new HashMap();
   private int id;

   public BOFont(String name, int fontSize) {
      this.name = name;
      this.fontSize = fontSize;
      this.generate();
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public int getId() {
      return this.id;
   }

   public int getFontSize() {
      return this.fontSize;
   }

   private Font registerFont() {
      try {
         GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
         InputStream fontStream = FileUtils.getResourceStream("fonts", this.name + ".ttf");
         Font font = Font.createFont(0, fontStream);
         graphicsEnvironment.registerFont(font);
         return font;
      } catch (Exception var4) {
         var4.printStackTrace();
         return null;
      }
   }

   public void generate() {
      Font font = new Font(this.registerFont().getName(), 0, this.fontSize);
      BufferedImage image = new BufferedImage(1, 1, 2);
      Graphics2D graphics2D = image.createGraphics();
      graphics2D.setFont(font);
      FontMetrics metrics = graphics2D.getFontMetrics();
      int estWidth = (int)(Math.sqrt((double)font.getNumGlyphs()) * (double)font.getSize() + 1.0D);
      int lineHeight = metrics.getHeight();
      this.height = lineHeight;
      int yStep = lineHeight * 2;
      int x = 0;
      int y = yStep;

      int i;
      CharInfo info;
      for(i = 0; i < font.getNumGlyphs(); ++i) {
         if (font.canDisplay(i)) {
            info = new CharInfo(x, y, metrics.charWidth(i), metrics.getHeight());
            this.map.put(i, info);
            this.width = Math.max(x + info.width, this.width);
            x += info.width + 5;
            if (x > estWidth) {
               x = 0;
               y += yStep;
               this.height += yStep;
            }
         }
      }

      this.height += lineHeight;
      graphics2D.dispose();
      image = new BufferedImage(this.width, this.height, 2);
      graphics2D = image.createGraphics();
      graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
      graphics2D.setFont(font);
      graphics2D.setColor(Color.WHITE);

      for(i = 0; i < font.getNumGlyphs(); ++i) {
         if (font.canDisplay(i)) {
            info = (CharInfo)this.map.get(i);
            info.calcTexCoords(this.width, this.height);
            graphics2D.drawString(String.valueOf((char)i), info.x, info.y);
         }
      }

      graphics2D.dispose();
      this.id = BOTextures.upload(image, true).id();
   }
}
