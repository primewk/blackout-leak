package bodevelopment.client.blackout.mixin.mixins;

import bodevelopment.client.blackout.interfaces.mixin.IChatHudLine;
import net.minecraft.class_2561;
import net.minecraft.class_303;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin({class_303.class})
public class MixinChatHudLine implements IChatHudLine {
   @Unique
   private int id;
   @Unique
   private class_2561 message;
   @Unique
   private int spam;

   public void blackout_Client$setId(int id) {
      this.id = id;
   }

   public boolean blackout_Client$idEquals(int id) {
      return this.id == id;
   }

   public class_2561 blackout_Client$getMessage() {
      return this.message;
   }

   public void blackout_Client$setMessage(class_2561 message) {
      this.message = message;
   }

   public void blackout_Client$setSpam(int spam) {
      this.spam = spam;
   }

   public int blackout_Client$getSpam() {
      return this.spam;
   }
}
