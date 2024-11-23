package bodevelopment.client.blackout.util.render;

public class AnimUtils {
   public static double getAnimProgressD(long startTime, double time) {
      return Math.min((double)(System.currentTimeMillis() - startTime), time * 1000.0D) / time / 1000.0D;
   }

   public static float getAnimProgressF(long startTime, float time) {
      return (float)getAnimProgressD(startTime, (double)time);
   }

   public static double easeOutBack(double delta) {
      double c1 = 1.70158D;
      double c3 = c1 + 1.0D;
      return 1.0D + c3 * Math.pow(delta - 1.0D, 3.0D) + c1 * Math.pow(delta - 1.0D, 2.0D);
   }

   public static double easeOutBounce(double delta) {
      double n1 = 7.5625D;
      double d1 = 2.75D;
      if (delta < 1.0D / d1) {
         return n1 * delta * delta;
      } else if (delta < 2.0D / d1) {
         return n1 * (delta -= 1.5D / d1) * delta + 0.75D;
      } else {
         return delta < 2.5D / d1 ? n1 * (delta -= 2.25D / d1) * delta + 0.9375D : n1 * (delta -= 2.625D / d1) * delta + 0.984375D;
      }
   }

   public static double easeInSine(double delta) {
      return 1.0D - Math.cos(delta * 3.141592653589793D / 2.0D);
   }

   public static double easeOutSine(double delta) {
      return Math.sin(delta * 3.141592653589793D / 2.0D);
   }

   public static double easeInOutSine(double delta) {
      return -(Math.cos(3.141592653589793D * delta) - 1.0D) / 2.0D;
   }

   public static double easeInQuad(double delta) {
      return delta * delta;
   }

   public static double easeOutQuad(double delta) {
      return 1.0D - (1.0D - delta) * (1.0D - delta);
   }

   public static double easeInOutQuad(double delta) {
      return delta < 0.5D ? 2.0D * delta * delta : 1.0D - Math.pow(-2.0D * delta + 2.0D, 2.0D) / 2.0D;
   }

   public static double easeInCubic(double delta) {
      return delta * delta * delta;
   }

   public static double easeOutCubic(double delta) {
      return 1.0D - Math.pow(1.0D - delta, 3.0D);
   }

   public static double easeInOutCubic(double delta) {
      return delta < 0.5D ? 4.0D * delta * delta * delta : 1.0D - Math.pow(-2.0D * delta + 2.0D, 3.0D) / 2.0D;
   }

   public static double easeInQuart(double delta) {
      return delta * delta * delta * delta;
   }

   public static double easeOutQuart(double delta) {
      return 1.0D - Math.pow(1.0D - delta, 4.0D);
   }

   public static double easeInOutQuart(double delta) {
      return delta < 0.5D ? 8.0D * delta * delta * delta * delta : 1.0D - Math.pow(-2.0D * delta + 2.0D, 4.0D) / 2.0D;
   }

   public static double easeInQuint(double delta) {
      return delta * delta * delta * delta * delta;
   }

   public static double easeOutQuint(double delta) {
      return 1.0D - Math.pow(1.0D - delta, 5.0D);
   }

   public static double easeInOutQuint(double delta) {
      return delta < 0.5D ? 16.0D * delta * delta * delta * delta * delta : 1.0D - Math.pow(-2.0D * delta + 2.0D, 5.0D) / 2.0D;
   }

   public static double easeInExpo(double delta) {
      return delta == 0.0D ? 0.0D : Math.pow(2.0D, 10.0D * delta - 10.0D);
   }

   public static double easeOutExpo(double delta) {
      return delta == 1.0D ? 1.0D : 1.0D - Math.pow(2.0D, -10.0D * delta);
   }

   public static double easeInOutExpo(double delta) {
      return delta == 0.0D ? 0.0D : (delta == 1.0D ? 1.0D : (delta < 0.5D ? Math.pow(2.0D, 20.0D * delta - 10.0D) / 2.0D : (2.0D - Math.pow(2.0D, -20.0D * delta + 10.0D)) / 2.0D));
   }

   public static double easeInCirc(double delta) {
      return 1.0D - Math.sqrt(1.0D - Math.pow(delta, 2.0D));
   }

   public static double easeOutCirc(double delta) {
      return Math.sqrt(1.0D - Math.pow(delta - 1.0D, 2.0D));
   }

   public static double easeInOutCirc(double delta) {
      return delta < 0.5D ? (1.0D - Math.sqrt(1.0D - Math.pow(2.0D * delta, 2.0D))) / 2.0D : (Math.sqrt(1.0D - Math.pow(-2.0D * delta + 2.0D, 2.0D)) + 1.0D) / 2.0D;
   }

   public static double easeInBack(double delta) {
      double c1 = 1.70158D;
      double c3 = c1 + 1.0D;
      return c3 * delta * delta * delta - c1 * delta * delta;
   }

   public static double easeInOutBack(double delta) {
      double c1 = 1.70158D;
      double c2 = c1 * 1.525D;
      return delta < 0.5D ? Math.pow(2.0D * delta, 2.0D) * ((c2 + 1.0D) * 2.0D * delta - c2) / 2.0D : (Math.pow(2.0D * delta - 2.0D, 2.0D) * ((c2 + 1.0D) * (delta * 2.0D - 2.0D) + c2) + 2.0D) / 2.0D;
   }

   public static double easeInElastic(double delta) {
      double c4 = 2.0943951023931953D;
      return delta == 0.0D ? 0.0D : (delta == 1.0D ? 1.0D : -Math.pow(2.0D, 10.0D * delta - 10.0D) * Math.sin((delta * 10.0D - 10.75D) * c4));
   }

   public static double easeOutElastic(double delta) {
      double c4 = 2.0943951023931953D;
      return delta == 0.0D ? 0.0D : (delta == 1.0D ? 1.0D : Math.pow(2.0D, -10.0D * delta) * Math.sin((delta * 10.0D - 0.75D) * c4) + 1.0D);
   }

   public static double easeInOutElastic(double delta) {
      double c5 = 1.3962634015954636D;
      return delta == 0.0D ? 0.0D : (delta == 1.0D ? 1.0D : (delta < 0.5D ? -(Math.pow(2.0D, 20.0D * delta - 10.0D) * Math.sin((20.0D * delta - 11.125D) * c5)) / 2.0D : Math.pow(2.0D, -20.0D * delta + 10.0D) * Math.sin((20.0D * delta - 11.125D) * c5) / 2.0D + 1.0D));
   }

   public static double easeInBounce(double delta) {
      return 1.0D - easeOutBounce(1.0D - delta);
   }

   public static double easeInOutBounce(double delta) {
      return delta < 0.5D ? (1.0D - easeOutBounce(1.0D - 2.0D * delta)) / 2.0D : (1.0D + easeOutBounce(2.0D * delta - 1.0D)) / 2.0D;
   }
}
