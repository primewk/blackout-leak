package bodevelopment.client.blackout.mixin.accessors;

import net.minecraft.class_2663;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({class_2663.class})
public interface AccessorEntityStatusC2SPacket {
   @Accessor("id")
   int getId();
}
