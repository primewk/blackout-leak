package bodevelopment.client.blackout.randomstuff;

import java.awt.Color;
import net.minecraft.class_3532;
import net.minecraft.class_5253.class_5254;

public class BlackOutColor {
   public static final BlackOutColor WHITE = new BlackOutColor(255, 255, 255, 255);
   public int red;
   public int green;
   public int blue;
   public int alpha;

   public BlackOutColor(int red, int green, int blue, int alpha) {
      this.red = red;
      this.green = green;
      this.blue = blue;
      this.alpha = alpha;
   }

   public BlackOutColor copy() {
      return new BlackOutColor(this.red, this.green, this.blue, this.alpha);
   }

   public Color getColor() {
      return new Color(this.red, this.green, this.blue, this.alpha);
   }

   public BlackOutColor alphaMulti(double m) {
      return new BlackOutColor(this.red, this.green, this.blue, (int)Math.round((double)this.alpha * m));
   }

   public int alphaMultiRGB(double m) {
      return (byte)((int)((double)this.alpha * m)) << 24 | this.red << 16 | this.green << 8 | this.blue;
   }

   public int getRGB() {
      return this.alpha << 24 | this.red << 16 | this.green << 8 | this.blue;
   }

   public void set(int red, int green, int blue) {
      this.red = red;
      this.green = green;
      this.blue = blue;
   }

   public void set(int red, int green, int blue, int alpha) {
      this.red = red;
      this.green = green;
      this.blue = blue;
      this.alpha = alpha;
   }

   public void setRed(int red) {
      this.red = red;
   }

   public void setGreen(int green) {
      this.green = green;
   }

   public void setBlue(int blue) {
      this.blue = blue;
   }

   public void setAlpha(int alpha) {
      this.alpha = alpha;
   }

   public BlackOutColor lerp(double delta, BlackOutColor to) {
      return new BlackOutColor((int)class_3532.method_16436(delta, (double)this.red, (double)to.red), (int)class_3532.method_16436(delta, (double)this.green, (double)to.green), (int)class_3532.method_16436(delta, (double)this.blue, (double)to.blue), (int)class_3532.method_16436(delta, (double)this.alpha, (double)to.alpha));
   }

   public BlackOutColor withAlpha(int alpha) {
      return new BlackOutColor(this.red, this.green, this.blue, alpha);
   }

   public static BlackOutColor from(int color) {
      return new BlackOutColor(class_5254.method_27765(color), class_5254.method_27766(color), class_5254.method_27767(color), class_5254.method_27762(color));
   }
}
