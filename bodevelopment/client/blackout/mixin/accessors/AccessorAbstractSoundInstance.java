package bodevelopment.client.blackout.mixin.accessors;

import net.minecraft.class_1102;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({class_1102.class})
public interface AccessorAbstractSoundInstance {
   @Accessor("volume")
   void setVolume(float var1);

   @Accessor("pitch")
   void setPitch(float var1);
}
