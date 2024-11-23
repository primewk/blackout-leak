package bodevelopment.client.blackout.mixin.mixins;

import bodevelopment.client.blackout.interfaces.mixin.IChatHud;
import bodevelopment.client.blackout.interfaces.mixin.IChatHudLine;
import bodevelopment.client.blackout.interfaces.mixin.IVisible;
import bodevelopment.client.blackout.module.modules.misc.AntiSpam;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import net.minecraft.class_124;
import net.minecraft.class_2561;
import net.minecraft.class_303;
import net.minecraft.class_327;
import net.minecraft.class_338;
import net.minecraft.class_341;
import net.minecraft.class_5250;
import net.minecraft.class_5348;
import net.minecraft.class_5481;
import net.minecraft.class_7469;
import net.minecraft.class_7591;
import net.minecraft.class_303.class_7590;
import org.apache.commons.lang3.mutable.MutableInt;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin({class_338.class})
public abstract class MixinChatHud implements IChatHud {
   @Shadow
   @Final
   private List<class_303> field_2061;
   @Shadow
   @Final
   private List<class_7590> field_2064;
   @Unique
   private int addedId = -1;
   @Unique
   private class_303 currentLine = null;

   @Shadow
   public abstract void method_1812(class_2561 var1);

   public void blackout_Client$addMessageToChat(class_2561 text, int id) {
      if (id != -1) {
         this.field_2061.removeIf((line) -> {
            return ((IChatHudLine)line).blackout_Client$idEquals(id);
         });
         this.field_2064.removeIf((visible) -> {
            return ((IVisible)visible).blackout_Client$idEquals(id);
         });
      }

      this.addedId = id;
      this.method_1812(text);
      this.addedId = -1;
   }

   @Inject(
      method = {"addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;ILnet/minecraft/client/gui/hud/MessageIndicator;Z)V"},
      at = {@At("HEAD")}
   )
   private void onAddMessage(class_2561 message, class_7469 signature, int ticks, class_7591 indicator, boolean refresh, CallbackInfo ci) {
      AntiSpam antiSpam = AntiSpam.getInstance();
      MutableInt highest = new MutableInt(0);
      this.currentLine = new class_303(ticks, message, signature, indicator);
      if (antiSpam.enabled) {
         AtomicBoolean b = new AtomicBoolean(false);
         this.field_2061.removeIf((line) -> {
            if (antiSpam.isSimilar(((IChatHudLine)line).blackout_Client$getMessage().getString(), message.getString())) {
               highest.setValue(Math.max(((IChatHudLine)line).blackout_Client$getSpam(), highest.getValue()));
               b.set(true);
               this.field_2064.removeIf((visible) -> {
                  return ((IVisible)visible).blackout_Client$messageEquals(line);
               });
               return true;
            } else {
               return false;
            }
         });
         if (b.get()) {
            class_5250 var10004 = message.method_27661();
            class_124 var10005 = class_124.field_1075;
            this.currentLine = new class_303(ticks, var10004.method_27693(var10005 + " (" + (highest.getValue() + 1) + ")"), signature, indicator);
         }
      }

      ((IChatHudLine)this.currentLine).blackout_Client$setSpam(highest.getValue() + 1);
      ((IChatHudLine)this.currentLine).blackout_Client$setMessage(message);
   }

   @Redirect(
      method = {"addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;ILnet/minecraft/client/gui/hud/MessageIndicator;Z)V"},
      at = @At(
   value = "INVOKE",
   target = "Lnet/minecraft/client/util/ChatMessages;breakRenderedChatMessageLines(Lnet/minecraft/text/StringVisitable;ILnet/minecraft/client/font/TextRenderer;)Ljava/util/List;"
)
   )
   private List<class_5481> breakIntoLines(class_5348 message, int width, class_327 textRenderer) {
      return class_341.method_1850(this.currentLine.comp_893(), width, textRenderer);
   }

   @Redirect(
      method = {"addMessage(Lnet/minecraft/text/Text;Lnet/minecraft/network/message/MessageSignatureData;ILnet/minecraft/client/gui/hud/MessageIndicator;Z)V"},
      at = @At(
   value = "INVOKE",
   target = "Ljava/util/List;add(ILjava/lang/Object;)V"
)
   )
   private <E> void addLineToList(List<E> instance, int index, E e) {
      if (e instanceof class_303) {
         ((IChatHudLine)this.currentLine).blackout_Client$setId(this.addedId);
         instance.add(index, this.currentLine);
      } else if (e instanceof class_7590) {
         class_7590 line = (class_7590)e;
         ((IVisible)line).blackout_Client$set(this.addedId);
         ((IVisible)line).blackout_Client$setLine(this.currentLine);
         instance.add(index, e);
      }

   }
}
