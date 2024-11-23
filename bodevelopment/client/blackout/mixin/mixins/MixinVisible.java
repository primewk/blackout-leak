package bodevelopment.client.blackout.mixin.mixins;

import bodevelopment.client.blackout.interfaces.mixin.IVisible;
import net.minecraft.class_303;
import net.minecraft.class_303.class_7590;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin({class_7590.class})
public class MixinVisible implements IVisible {
   @Unique
   private int id;
   @Unique
   private class_303 line;

   public void blackout_Client$set(int id) {
      this.id = id;
   }

   public boolean blackout_Client$idEquals(int id) {
      return this.id == id;
   }

   public boolean blackout_Client$messageEquals(class_303 line) {
      return this.line.equals(line);
   }

   public void blackout_Client$setLine(class_303 line) {
      this.line = line;
   }
}
