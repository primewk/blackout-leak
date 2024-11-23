package bodevelopment.client.blackout.randomstuff;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.module.modules.visual.misc.CustomChat;
import bodevelopment.client.blackout.rendering.renderer.Renderer;
import bodevelopment.client.blackout.util.render.RenderUtils;
import net.minecraft.class_332;
import net.minecraft.class_408;
import net.minecraft.class_4587;

public class CustomChatScreen extends class_408 {
   private final class_4587 stack = new class_4587();

   public CustomChatScreen() {
      super("");
   }

   public void method_25420(class_332 context, int mouseX, int mouseY, float delta) {
   }

   public void method_25394(class_332 context, int mouseX, int mouseY, float delta) {
      CustomChat customChat = CustomChat.getInstance();
      String text = this.field_2382.method_1882() + "_";
      double f = Math.sin((double)System.currentTimeMillis() / 1000.0D * 2.0D) + 1.0D;
      float textScale = 2.2F;
      float fontHeight = BlackOut.FONT.getHeight() * textScale;
      float width = BlackOut.FONT.getWidth(text) * textScale > 250.0F ? BlackOut.FONT.getWidth(text) * textScale + 2.0F : 250.0F;
      this.stack.method_22903();
      RenderUtils.unGuiScale(this.stack);
      if ((Boolean)customChat.blur.get()) {
         RenderUtils.drawLoadedBlur("hudblur", this.stack, (renderer) -> {
            renderer.rounded(8.0F, (float)BlackOut.mc.method_22683().method_4507() - (fontHeight + 14.0F), width, fontHeight + 4.0F, 6.0F, 10);
         });
         Renderer.onHUDBlur();
      }

      if ((Boolean)customChat.background.get()) {
         RenderUtils.rounded(this.stack, 10.0F, (float)BlackOut.mc.method_22683().method_4507() - (fontHeight + 16.0F), width, fontHeight + 4.0F, 6.0F, (Boolean)customChat.shadow.get() ? 6.0F : 0.0F, ((BlackOutColor)customChat.bgColor.get()).getRGB(), ((BlackOutColor)customChat.shadowColor.get()).getRGB());
      }

      customChat.textColor.render(this.stack, text, textScale, 10.0F, (float)BlackOut.mc.method_22683().method_4507() - (fontHeight + 13.0F), false, false);
      this.stack.method_22909();
   }
}
