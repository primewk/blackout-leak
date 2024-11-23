package bodevelopment.client.blackout.util;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.manager.Managers;
import net.minecraft.class_1294;
import net.minecraft.class_1799;
import net.minecraft.class_1890;
import net.minecraft.class_1893;
import net.minecraft.class_1922;
import net.minecraft.class_2246;
import net.minecraft.class_2338;
import net.minecraft.class_2680;
import net.minecraft.class_3486;

public class BlockUtils {
   public static boolean mineable(class_2338 pos) {
      class_2680 state = BlackOut.mc.field_1687.method_8320(pos);
      return state.method_26204() == class_2246.field_9987 ? false : state.method_51367();
   }

   public static double getBlockBreakingDelta(class_2338 pos, class_1799 stack) {
      return getBlockBreakingDelta(pos, stack, true, true, true);
   }

   public static double getBlockBreakingDelta(class_2338 pos, class_1799 stack, boolean effects, boolean water, boolean onGround) {
      return getBlockBreakingDelta(stack, BlackOut.mc.field_1687.method_8320(pos), effects, water, onGround);
   }

   public static double getBlockBreakingDelta(class_1799 stack, class_2680 state, boolean effects, boolean water, boolean onGround) {
      float f = state.method_26214((class_1922)null, (class_2338)null);
      if (f == -1.0F) {
         return 0.0D;
      } else {
         int i = state.method_29291() && !stack.method_7951(state) ? 100 : 30;
         return getBlockBreakingSpeed(state, stack, effects, water, onGround) / (double)f / (double)i;
      }
   }

   public static double getBlockBreakingSpeed(class_2680 state, class_1799 stack, boolean effects, boolean water, boolean onGround) {
      float f = stack.method_7924(state);
      if (f > 1.0F && !stack.method_7960() && class_1890.method_8222(stack).containsKey(class_1893.field_9131)) {
         int i = (Integer)class_1890.method_8222(stack).get(class_1893.field_9131);
         if (i > 0 && !stack.method_7960()) {
            f += (float)(i * i + 1);
         }
      }

      if (effects && BlackOut.mc.field_1724.method_6059(class_1294.field_5917)) {
         f *= 1.0F + (float)(BlackOut.mc.field_1724.method_6112(class_1294.field_5917).method_5578() + 1) * 0.2F;
      }

      if (effects && BlackOut.mc.field_1724.method_6059(class_1294.field_5901)) {
         float var10001;
         switch(BlackOut.mc.field_1724.method_6112(class_1294.field_5901).method_5578()) {
         case 0:
            var10001 = 0.3F;
            break;
         case 1:
            var10001 = 0.09F;
            break;
         case 2:
            var10001 = 0.0027F;
            break;
         default:
            var10001 = 8.1E-4F;
         }

         f *= var10001;
      }

      if (water && BlackOut.mc.field_1724.method_5777(class_3486.field_15517) && !class_1890.method_8200(BlackOut.mc.field_1724)) {
         f /= 5.0F;
      }

      if (onGround && !Managers.PACKET.isOnGround()) {
         f /= 5.0F;
      }

      return (double)f;
   }
}
