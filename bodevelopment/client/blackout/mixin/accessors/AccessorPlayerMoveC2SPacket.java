package bodevelopment.client.blackout.mixin.accessors;

import net.minecraft.class_2828;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin({class_2828.class})
public interface AccessorPlayerMoveC2SPacket {
   @Accessor("yaw")
   @Mutable
   void setYaw(float var1);

   @Accessor("pitch")
   @Mutable
   void setPitch(float var1);

   @Accessor("onGround")
   @Mutable
   void setOnGround(boolean var1);
}
