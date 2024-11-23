package bodevelopment.client.blackout.mixin.accessors;

import net.minecraft.class_4970.class_2251;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({class_2251.class})
public interface AccessorBlockSettings {
   @Accessor("replaceable")
   boolean replaceable();
}
