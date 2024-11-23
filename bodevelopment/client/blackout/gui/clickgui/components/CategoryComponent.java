package bodevelopment.client.blackout.gui.clickgui.components;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.gui.clickgui.ClickGui;
import bodevelopment.client.blackout.gui.clickgui.Component;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.modules.client.GuiSettings;
import bodevelopment.client.blackout.randomstuff.BlackOutColor;
import bodevelopment.client.blackout.util.ColorUtils;
import bodevelopment.client.blackout.util.GuiColorUtils;
import bodevelopment.client.blackout.util.GuiRenderUtils;
import bodevelopment.client.blackout.util.render.RenderUtils;
import net.minecraft.class_4587;

public class CategoryComponent extends Component {
   public final SubCategory category;

   public CategoryComponent(class_4587 stack, SubCategory category) {
      super(stack);
      this.category = category;
   }

   public float render() {
      if (this.category == ClickGui.selectedCategory) {
         RenderUtils.rounded(this.stack, (float)this.x, (float)this.y - BlackOut.FONT.getHeight() * 1.5F, 170.0F, BlackOut.FONT.getHeight() * 3.0F, 6.0F, 2.0F, ((BlackOutColor)GuiSettings.getInstance().selectorColor.get()).getRGB(), ColorUtils.SHADOW100I);
         if ((Boolean)GuiSettings.getInstance().selectorBar.get()) {
            RenderUtils.rounded(this.stack, (float)this.x, (float)this.y - BlackOut.FONT.getHeight() * 1.5F, 1.0F, BlackOut.FONT.getHeight() * 3.0F, 2.0F, (float)(Integer)GuiSettings.getInstance().selectorGlow.get(), GuiRenderUtils.getGuiColors(1.0F).getRGB(), GuiRenderUtils.getGuiColors(1.0F).getRGB());
         }
      }

      BlackOut.FONT.text(this.stack, this.category.name(), 2.0F, (float)(this.x + 10), (float)this.y, GuiColorUtils.category, false, true);
      return 35.0F;
   }

   public void onMouse(int button, boolean pressed) {
      if (button == 0 && pressed && this.mx > (double)(this.x - 5) && this.mx < (double)(this.x + 175) && this.my > (double)((float)this.y - BlackOut.FONT.getHeight() * 1.5F - 5.0F) && this.my < (double)((float)this.y + BlackOut.FONT.getHeight() * 3.0F + 5.0F)) {
         ClickGui.selectedCategory = this.category;
      }

   }
}
