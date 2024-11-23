package bodevelopment.client.blackout.interfaces.mixin;

import net.minecraft.class_1297;
import net.minecraft.class_243;
import net.minecraft.class_3959.class_242;
import net.minecraft.class_3959.class_3960;

public interface IRaycastContext {
   void blackout_Client$set(class_243 var1, class_243 var2, class_3960 var3, class_242 var4, class_1297 var5);

   void blackout_Client$set(class_243 var1, class_243 var2);

   void blackout_Client$set(class_3960 var1, class_242 var2, class_1297 var3);

   void blackout_Client$setStart(class_243 var1);

   void blackout_Client$setEnd(class_243 var1);
}
