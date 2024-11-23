package bodevelopment.client.blackout.mixin.mixins;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.event.events.BlockStateEvent;
import net.minecraft.class_2338;
import net.minecraft.class_2680;
import net.minecraft.class_2818;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_2818.class})
public class MixinWorldChunk {
   @Inject(
      method = {"setBlockState"},
      at = {@At("TAIL")},
      cancellable = true
   )
   private void onBlockState(class_2338 pos, class_2680 state, boolean moved, CallbackInfoReturnable<class_2680> cir) {
      if (((BlockStateEvent)BlackOut.EVENT_BUS.post(BlockStateEvent.get(pos, state, (class_2680)cir.getReturnValue()))).isCancelled()) {
         cir.cancel();
      }

   }
}
