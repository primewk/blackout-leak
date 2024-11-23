package bodevelopment.client.blackout.mixin.accessors;

import net.minecraft.class_2824;
import net.minecraft.class_2824.class_5906;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({class_2824.class})
public interface AccessorInteractEntityC2SPacket {
   @Accessor("entityId")
   @Final
   @Mutable
   void setId(int var1);

   @Accessor("entityId")
   int getId();

   @Accessor("type")
   class_5906 getType();
}
