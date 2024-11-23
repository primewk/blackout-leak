package bodevelopment.client.blackout.mixin.mixins;

import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.module.modules.client.Notifications;
import bodevelopment.client.blackout.util.ChatUtils;
import net.minecraft.class_408;
import net.minecraft.class_634;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin({class_408.class})
public class MixinChatScreen {
   @Redirect(
      method = {"sendMessage"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/network/ClientPlayNetworkHandler;sendChatMessage(Ljava/lang/String;)V"
)
   )
   private void onMessage(class_634 instance, String content) {
      if (content.startsWith(Managers.COMMANDS.prefix)) {
         String rur = Managers.COMMANDS.onCommand(content.substring(1).split(" "));
         if (rur == null) {
            ChatUtils.addMessage("Unrecognized command!");
         } else {
            String var10000 = Notifications.getInstance().getClientPrefix();
            ChatUtils.addMessage(var10000 + " " + rur);
         }
      } else {
         instance.method_45729(content);
      }

   }
}
