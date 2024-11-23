package bodevelopment.client.blackout.gui.clickgui;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.gui.clickgui.screens.ConfigScreen;
import bodevelopment.client.blackout.gui.clickgui.screens.ConsoleScreen;
import bodevelopment.client.blackout.gui.clickgui.screens.FriendsScreen;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.rendering.renderer.Renderer;
import bodevelopment.client.blackout.rendering.renderer.TextureRenderer;
import bodevelopment.client.blackout.rendering.texture.BOTextures;
import bodevelopment.client.blackout.util.ColorUtils;
import bodevelopment.client.blackout.util.GuiColorUtils;
import bodevelopment.client.blackout.util.render.AnimUtils;
import bodevelopment.client.blackout.util.render.RenderUtils;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.class_3532;
import net.minecraft.class_4587;

public class ClickGuiButtons {
   public static final List<ClickGuiButtons.Button> buttons = new ArrayList();
   private static final int BUTTON_WIDTH = 70;
   private static final int BUTTON_SEPARATION = 20;

   private static void a(Supplier<? extends ClickGuiScreen> screen, TextureRenderer icon) {
      buttons.add(new ClickGuiButtons.Button(screen, icon));
   }

   public void render(int mouseX, int mouseY, long openTime, float closeDelta) {
      class_4587 stack = RenderUtils.emptyStack;
      stack.method_22903();
      RenderUtils.unGuiScale(stack);
      double mx = (double)((float)mouseX / (float)RenderUtils.getScale());
      double my = (double)((float)mouseY / (float)RenderUtils.getScale());
      stack.method_22904(((double)BlackOut.mc.method_22683().method_4480() - this.getWidth()) / 2.0D, (double)BlackOut.mc.method_22683().method_4507() - 105.0D - 20.0D, 0.0D);
      float delta = (float)(System.currentTimeMillis() - openTime - 200L) / 500.0F;
      Iterator var12 = buttons.iterator();

      while(var12.hasNext()) {
         ClickGuiButtons.Button button = (ClickGuiButtons.Button)var12.next();
         this.renderButton(stack, button.icon(), class_3532.method_15363(delta -= 0.5F, 0.0F, 1.0F), closeDelta);
      }

      stack.method_22909();
   }

   public boolean onClick(int mouseX, int mouseY) {
      double mx = (double)((float)mouseX * (float)RenderUtils.getScale());
      double my = (double)((float)mouseY * (float)RenderUtils.getScale());
      double offsetX = mx - ((double)BlackOut.mc.method_22683().method_4480() - this.getWidth() + 70.0D) / 2.0D;
      double offsetY = my - (double)(BlackOut.mc.method_22683().method_4507() - 70 - 20);
      offsetY *= offsetY;

      for(Iterator var11 = buttons.iterator(); var11.hasNext(); offsetX -= 90.0D) {
         ClickGuiButtons.Button button = (ClickGuiButtons.Button)var11.next();
         if (Math.sqrt(offsetX * offsetX + offsetY) <= 35.0D) {
            Managers.CLICK_GUI.openScreen((ClickGuiScreen)button.supplier.get());
            return true;
         }
      }

      return false;
   }

   private double getWidth() {
      return (double)(buttons.size() * 70 + (buttons.size() - 1) * 20);
   }

   private void renderButton(class_4587 stack, TextureRenderer icon, float delta, float closeDelta) {
      float prevAlpha = Renderer.getAlpha();
      Renderer.setAlpha((float)Math.sqrt((double)delta) * closeDelta);
      float anim = (float)AnimUtils.easeOutBack((double)delta);
      float offset = anim * -50.0F + 50.0F;
      float half = 35.0F;
      RenderUtils.rounded(stack, half, half + offset, 0.0F, 0.0F, half, 15.0F, GuiColorUtils.bg2.getRGB(), ColorUtils.SHADOW100I);
      float ratio = (float)icon.getWidth() / 36.0F;
      float width = (float)icon.getWidth() / ratio;
      float height = (float)icon.getHeight() / ratio;
      icon.quad(stack, half - width / 2.0F, half - width / 2.0F + offset, width, height, Color.WHITE.getRGB());
      stack.method_46416(90.0F, 0.0F, 0.0F);
      Renderer.setAlpha(prevAlpha);
   }

   static {
      a(ConfigScreen::new, BOTextures.getFolderIconRenderer());
      a(FriendsScreen::new, BOTextures.getPersonIconRenderer());
      a(ConsoleScreen::new, BOTextures.getConsoleIconRenderer());
   }

   public static record Button(Supplier<? extends ClickGuiScreen> supplier, TextureRenderer icon) {
      public Button(Supplier<? extends ClickGuiScreen> supplier, TextureRenderer icon) {
         this.supplier = supplier;
         this.icon = icon;
      }

      public Supplier<? extends ClickGuiScreen> supplier() {
         return this.supplier;
      }

      public TextureRenderer icon() {
         return this.icon;
      }
   }
}
