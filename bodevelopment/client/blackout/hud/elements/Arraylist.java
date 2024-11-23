package bodevelopment.client.blackout.hud.elements;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.enums.FilterMode;
import bodevelopment.client.blackout.hud.HudElement;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.ParentCategory;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.module.setting.multisettings.TextColorMultiSetting;
import bodevelopment.client.blackout.randomstuff.BlackOutColor;
import bodevelopment.client.blackout.randomstuff.ShaderSetup;
import bodevelopment.client.blackout.rendering.font.CustomFontRenderer;
import bodevelopment.client.blackout.rendering.framebuffer.FrameBuffer;
import bodevelopment.client.blackout.rendering.renderer.Renderer;
import bodevelopment.client.blackout.rendering.shader.Shaders;
import bodevelopment.client.blackout.util.render.AnimUtils;
import bodevelopment.client.blackout.util.render.RenderUtils;
import java.awt.Color;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import net.minecraft.class_4587;
import org.apache.commons.lang3.mutable.MutableFloat;

public class Arraylist extends HudElement {
   public final SettingGroup sgGeneral = this.addGroup("General");
   private final Setting<FilterMode> filterMode;
   private final Setting<List<Module>> moduleList;
   private final Setting<Boolean> drawInfo;
   private final Setting<Boolean> rounded;
   private final Setting<Boolean> sideBar;
   private final Setting<Boolean> bg;
   private final Setting<Boolean> useBlur;
   public final Setting<Arraylist.BracketMode> brackets;
   private final Setting<BlackOutColor> bgColor;
   private final Setting<Integer> bloomIntensity;
   private final Setting<BlackOutColor> bloomColor;
   private final TextColorMultiSetting textColor;
   private final Setting<BlackOutColor> customInfoColor;
   private int i;
   private String info;
   public static Map<Module, MutableFloat> deltaMap = new HashMap();

   public Arraylist() {
      super("Arraylist", "Shows currently enabled modules.");
      this.filterMode = this.sgGeneral.e("Filter Mode", FilterMode.Blacklist, ".");
      this.moduleList = this.sgGeneral.l("Modules", "Only renders these modules.", Managers.MODULE.getModules(), (module) -> {
         return module.name;
      });
      this.drawInfo = this.sgGeneral.b("Show Info", true, ".");
      this.rounded = this.sgGeneral.b("Rounded", true, "Renders a rounded background (cool af)");
      this.sideBar = this.sgGeneral.b("Side bar", true, "Renders a sidebar", () -> {
         return !(Boolean)this.rounded.get();
      });
      this.bg = this.sgGeneral.b("Background", true, "Renders a background");
      this.useBlur = this.sgGeneral.b("Blur", true, "Uses a blur effect", () -> {
         return true;
      });
      this.brackets = this.sgGeneral.e("Bracket Style", Arraylist.BracketMode.None, ".");
      SettingGroup var10001 = this.sgGeneral;
      BlackOutColor var10003 = new BlackOutColor(0, 0, 0, 50);
      Setting var10005 = this.bg;
      Objects.requireNonNull(var10005);
      this.bgColor = var10001.c("Background Color", var10003, ".", var10005::get);
      this.bloomIntensity = this.sgGeneral.i("Bloom Intensity", 3, 0, 10, 1, ".");
      this.bloomColor = this.sgGeneral.c("Bloom Color", new BlackOutColor(0, 0, 0, 100), "", () -> {
         return (Integer)this.bloomIntensity.get() > 0;
      });
      this.textColor = TextColorMultiSetting.of(this.sgGeneral, "Text");
      this.customInfoColor = this.sgGeneral.c("Info Color", new BlackOutColor(150, 150, 150, 255), "Info text color.", () -> {
         return true;
      });
      this.i = 0;
      this.info = "";
      this.setSize(75.0F, 125.0F);
   }

   public static void updateDeltas() {
      deltaMap.forEach((module, mutableFloat) -> {
         float delta = BlackOut.mc.method_1534() / 20.0F * 4.0F;
         mutableFloat.setValue(module.enabled ? Math.min(mutableFloat.getValue() + delta, 1.0F) : Math.max(mutableFloat.getValue() - delta, 0.0F));
      });
   }

   public void render() {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null) {
         Comparator<Module> comparator = Comparator.comparingDouble((m) -> {
            CustomFontRenderer var10000 = BlackOut.FONT;
            String var10001 = m.getDisplayName();
            return (double)var10000.getWidth(var10001 + (m.getInfo() == null ? "" : this.getInfo(m.getInfo())));
         });
         List<Module> modules = Managers.MODULE.getModules().stream().filter((module) -> {
            if (!((FilterMode)this.filterMode.get()).shouldAccept(module, (List)this.moduleList.get())) {
               return false;
            } else {
               return module.category.parent != ParentCategory.CLIENT;
            }
         }).sorted(comparator.reversed()).toList();
         FrameBuffer buffer;
         if ((Integer)this.bloomIntensity.get() > 0) {
            buffer = Managers.FRAME_BUFFER.getBuffer("arraylist");
            FrameBuffer bloomBuffer = Managers.FRAME_BUFFER.getBuffer("arraylist-bloom");
            buffer.clear(0.0F, 0.0F, 0.0F, 1.0F);
            buffer.bind(true);
            this.render(modules, (module) -> {
               String text = module.getDisplayName();
               this.info = this.getInfo(module.getInfo());
               float width = BlackOut.FONT.getWidth(text + this.info);
               if ((Boolean)this.rounded.get()) {
                  RenderUtils.rounded(this.stack, -BlackOut.FONT.getWidth(text) - 2.0F - BlackOut.FONT.getWidth(this.info), 1.5F, width - 2.0F, BlackOut.FONT.getHeight() - 2.0F, 2.8F, 0.0F, Color.WHITE.getRGB(), Color.WHITE.getRGB());
               } else {
                  RenderUtils.quad(this.stack, -BlackOut.FONT.getWidth(text) - 4.0F - BlackOut.FONT.getWidth(this.info), 0.0F, width + 2.0F, BlackOut.FONT.getHeight() + 2.0F, Color.WHITE.getRGB());
               }

            });
            buffer.unbind();
            bloomBuffer.clear(0.0F, 0.0F, 0.0F, 1.0F);
            bloomBuffer.bind(true);
            RenderUtils.renderBufferWith(buffer, Shaders.screentex, new ShaderSetup((setup) -> {
               setup.set("alpha", 1.0F);
            }));
            bloomBuffer.unbind();
            RenderUtils.blurBufferBW("arraylist-bloom", (Integer)this.bloomIntensity.get() + 1);
            bloomBuffer.bind(true);
            Renderer.setTexture(buffer.getTexture(), 1);
            RenderUtils.renderBufferWith(bloomBuffer, Shaders.subtract, new ShaderSetup((setup) -> {
               setup.set("uTexture1", 1);
            }));
            bloomBuffer.unbind();
            RenderUtils.renderBufferWith(bloomBuffer, Shaders.shaderbloom, new ShaderSetup((setup) -> {
               setup.color("clr", ((BlackOutColor)this.bloomColor.get()).getRGB());
            }));
         }

         if ((Boolean)this.rounded.get()) {
            buffer = Managers.FRAME_BUFFER.getBuffer("arraylist");
            buffer.clear(0.0F, 0.0F, 0.0F, 1.0F);
            buffer.bind(true);
            this.render(modules, (module) -> {
               String text = module.getDisplayName();
               this.info = this.getInfo(module.getInfo());
               float width = BlackOut.FONT.getWidth(text + this.info) - 2.0F;
               RenderUtils.rounded(this.stack, -BlackOut.FONT.getWidth(text) - 2.0F - BlackOut.FONT.getWidth(this.info), 1.5F, width, BlackOut.FONT.getHeight() - 2.0F, 3.0F, 0.0F, Color.WHITE.getRGB(), Color.WHITE.getRGB());
            });
            buffer.unbind();
            if ((Boolean)this.useBlur.get()) {
               RenderUtils.renderBufferOverlay(buffer, Managers.FRAME_BUFFER.getBuffer("hudblur").getTexture());
               Renderer.onHUDBlur();
            }

            if ((Boolean)this.bg.get()) {
               RenderUtils.renderBufferWith(buffer, Shaders.shaderbloom, new ShaderSetup((setup) -> {
                  setup.color("clr", ((BlackOutColor)this.bgColor.get()).getRGB());
               }));
            }
         }

         this.renderTexts(modules);
      }
   }

   private void renderTexts(List<Module> modules) {
      this.render(modules, (module) -> {
         Color infoColor = ((BlackOutColor)this.customInfoColor.get()).getColor();
         String text = module.getDisplayName();
         this.info = this.getInfo(module.getInfo());
         float width = BlackOut.FONT.getWidth(text + this.info) + 2.0F;
         if ((Boolean)this.sideBar.get() && !(Boolean)this.rounded.get()) {
            RenderUtils.quad(this.stack, -2.0F, 0.0F, 1.0F, BlackOut.FONT.getHeight() + 2.0F, this.textColor.getTextColor().getRGB());
         }

         if ((Boolean)this.useBlur.get() && !(Boolean)this.rounded.get()) {
            RenderUtils.drawLoadedBlur("hudblur", this.stack, (renderer) -> {
               renderer.rounded(-BlackOut.FONT.getWidth(text) - (4.0F + BlackOut.FONT.getWidth(this.info)), 0.0F, width, BlackOut.FONT.getHeight() + 2.0F, 0.0F, 10);
            });
            Renderer.onHUDBlur();
         }

         if ((Boolean)this.bg.get() && !(Boolean)this.rounded.get()) {
            RenderUtils.quad(this.stack, -BlackOut.FONT.getWidth(text) - (4.0F + BlackOut.FONT.getWidth(this.info)), 0.0F, width, BlackOut.FONT.getHeight() + 2.0F, ((BlackOutColor)this.bgColor.get()).getRGB());
         }

         float x = -BlackOut.FONT.getWidth(text) - (3.0F + BlackOut.FONT.getWidth(this.info));
         this.textColor.render(this.stack, text, 1.0F, x, 1.0F, false, false);
         BlackOut.FONT.text(this.stack, this.info, 1.0F, -BlackOut.FONT.getWidth(this.info) - 3.0F, 1.0F, infoColor, false, false);
      });
   }

   private String getBracket() {
      String var10000;
      switch((Arraylist.BracketMode)this.brackets.get()) {
      case None:
         var10000 = "";
         break;
      case Round:
         var10000 = "()";
         break;
      case Square:
         var10000 = "[]";
         break;
      case Wiggly:
         var10000 = "{}";
         break;
      case Triangle:
         var10000 = "<>";
         break;
      default:
         throw new IncompatibleClassChangeError();
      }

      return var10000;
   }

   private String getInfo(String infoString) {
      if (this.brackets.get() == Arraylist.BracketMode.None) {
         this.info = infoString == null ? "" : " " + infoString;
      } else {
         this.info = infoString == null ? "" : " " + this.getBracket().charAt(0) + infoString + this.getBracket().charAt(1);
      }

      return (Boolean)this.drawInfo.get() ? this.info : "";
   }

   private void render(List<Module> list, Consumer<Module> consumer) {
      this.stack.method_22903();
      this.stack.method_46416(75.0F, 0.0F, 0.0F);
      this.setSize(75.0F, Math.max((BlackOut.FONT.getHeight() + 2.0F) * (float)this.i, 123.0F));
      this.i = 0;
      list.forEach((module) -> {
         float delta = ((MutableFloat)deltaMap.get(module)).floatValue();
         if (!(delta <= 0.0F)) {
            delta = (float)AnimUtils.easeOutQuad((double)delta);
            float yDelta = (float)(Math.min((double)delta, 0.3D) / 0.3D);
            float xDelta = (float)((Math.max((double)delta, 0.3D) - 0.3D) / 0.7D);
            this.stack.method_22903();
            float prevAlpha = Renderer.getAlpha();
            Renderer.setAlpha(prevAlpha * delta);
            class_4587 var10000 = this.stack;
            float var10001 = 1.0F - xDelta;
            CustomFontRenderer var10002 = BlackOut.FONT;
            String var10003 = module.getDisplayName();
            var10000.method_46416(var10001 * var10002.getWidth(var10003 + " " + this.getInfo(module.getInfo())) + 1.0F, 0.0F, 0.0F);
            consumer.accept(module);
            Renderer.setAlpha(prevAlpha);
            this.stack.method_22909();
            this.stack.method_46416(0.0F, yDelta * (BlackOut.FONT.getHeight() + 2.0F), 0.0F);
            ++this.i;
         }
      });
      this.stack.method_22909();
   }

   public static enum BracketMode {
      None,
      Round,
      Square,
      Wiggly,
      Triangle;

      // $FF: synthetic method
      private static Arraylist.BracketMode[] $values() {
         return new Arraylist.BracketMode[]{None, Round, Square, Wiggly, Triangle};
      }
   }
}
