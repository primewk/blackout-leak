package bodevelopment.client.blackout.mixin.mixins;

import bodevelopment.client.blackout.interfaces.mixin.IExplosionS2CPacket;
import net.minecraft.class_2664;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;

@Mixin({class_2664.class})
public class MixinExplosionS2CPacket implements IExplosionS2CPacket {
   @Mutable
   @Shadow
   @Final
   private float field_12176;
   @Mutable
   @Shadow
   @Final
   private float field_12182;
   @Mutable
   @Shadow
   @Final
   private float field_12183;

   public void blackout_Client$multiplyXZ(float multiplier) {
      this.field_12176 *= multiplier;
      this.field_12182 *= multiplier;
   }

   public void blackout_Client$multiplyY(float multiplier) {
      this.field_12183 *= multiplier;
   }
}
