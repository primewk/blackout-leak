package bodevelopment.client.blackout.interfaces.mixin;

import java.util.Optional;
import java.util.UUID;
import net.minecraft.class_320;
import net.minecraft.class_320.class_321;

public interface IMinecraftClient {
   void blackout_Client$setSession(String var1, UUID var2, String var3, Optional<String> var4, Optional<String> var5, class_321 var6);

   void blackout_Client$setSession(class_320 var1);

   void blackout_Client$useItem();
}
