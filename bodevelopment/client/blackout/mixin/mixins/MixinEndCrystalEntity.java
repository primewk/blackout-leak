package bodevelopment.client.blackout.mixin.mixins;

import bodevelopment.client.blackout.interfaces.mixin.IEndCrystalEntity;
import bodevelopment.client.blackout.module.modules.visual.misc.NoRender;
import net.minecraft.class_1511;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin({class_1511.class})
public class MixinEndCrystalEntity implements IEndCrystalEntity {
   @Unique
   private final long spawnTime = System.currentTimeMillis();
   @Unique
   private boolean isOwn = false;

   public long blackout_Client$getSpawnTime() {
      return this.spawnTime;
   }

   public boolean blackout_Client$isOwn() {
      return this.isOwn;
   }

   public void blackout_Client$markOwn() {
      this.isOwn = true;
   }

   @Inject(
      method = {"shouldShowBottom"},
      at = {@At("HEAD")},
      cancellable = true
   )
   private void cancelBottom(CallbackInfoReturnable<Boolean> cir) {
      NoRender noRender = NoRender.getInstance();
      if (noRender.enabled && (Boolean)noRender.crystalBase.get()) {
         cir.setReturnValue(false);
      }

   }
}
