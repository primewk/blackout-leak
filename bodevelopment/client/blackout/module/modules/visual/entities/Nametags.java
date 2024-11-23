package bodevelopment.client.blackout.module.modules.visual.entities;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.enums.FilterMode;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.RenderEvent;
import bodevelopment.client.blackout.event.events.TickEvent;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.manager.managers.StatsManager;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.modules.combat.misc.AntiBot;
import bodevelopment.client.blackout.module.modules.visual.misc.Freecam;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.module.setting.multisettings.BackgroundMultiSetting;
import bodevelopment.client.blackout.randomstuff.BlackOutColor;
import bodevelopment.client.blackout.rendering.renderer.Renderer;
import bodevelopment.client.blackout.util.ColorUtils;
import bodevelopment.client.blackout.util.EnchantmentNames;
import bodevelopment.client.blackout.util.render.RenderUtils;
import com.mojang.blaze3d.platform.GlStateManager;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Map.Entry;
import net.minecraft.class_1297;
import net.minecraft.class_1299;
import net.minecraft.class_1309;
import net.minecraft.class_1542;
import net.minecraft.class_1657;
import net.minecraft.class_1799;
import net.minecraft.class_1887;
import net.minecraft.class_1890;
import net.minecraft.class_241;
import net.minecraft.class_2487;
import net.minecraft.class_3532;
import net.minecraft.class_4587;
import net.minecraft.class_640;
import net.minecraft.class_742;

public class Nametags extends Module {
   private static Nametags INSTANCE;
   private final SettingGroup sgGeneral = this.addGroup("General");
   private final SettingGroup sgItems = this.addGroup("Items");
   private final SettingGroup sgEnchantments = this.addGroup("Enchantments");
   private final SettingGroup sgColor = this.addGroup("Color");
   private final Setting<Double> scale;
   private final Setting<Double> scaleInc;
   private final Setting<Double> yOffset;
   private final Setting<Nametags.NameMode> nameMode;
   public final Setting<Boolean> ping;
   public final Setting<Boolean> pops;
   public final Setting<Boolean> showId;
   public final Setting<Boolean> rounded;
   public final Setting<Boolean> shadow;
   private final Setting<Boolean> blur;
   private final Setting<List<class_1299<?>>> entityTypes;
   private final Setting<Boolean> armor;
   private final Setting<Boolean> hand;
   private final Setting<Double> itemScale;
   private final Setting<Double> itemOffset;
   private final Setting<Double> itemSeparation;
   private final Setting<FilterMode> filterMode;
   private final Setting<List<class_1887>> enchantments;
   private final Setting<Boolean> drawEnchants;
   private final Setting<Boolean> enchantsAbove;
   private final Setting<Boolean> shortNames;
   private final Setting<Double> enchantScale;
   private final Setting<Double> compact;
   private final Setting<Boolean> center;
   private final Setting<Double> enchantmentsOffset;
   private final Setting<BlackOutColor> txt;
   private final BackgroundMultiSetting background;
   private final Setting<BlackOutColor> friendColor;
   public final Setting<Nametags.ColorMode> colorMode;
   private final Setting<BlackOutColor> hp;
   private final class_4587 stack;
   private final List<class_1297> entities;
   private final List<Nametags.Component> components;
   private float length;
   private float offset;

   public Nametags() {
      super("Nametags", "Renders custom nametags", SubCategory.ENTITIES, true);
      this.scale = this.sgGeneral.d("Scale", 1.0D, 0.0D, 10.0D, 0.1D, ".");
      this.scaleInc = this.sgGeneral.d("Scale Increase", 1.0D, 0.0D, 5.0D, 0.05D, "How much should the scale increase when enemy is further away.");
      this.yOffset = this.sgGeneral.d("Y", 0.0D, 0.0D, 1.0D, 0.01D, ".");
      this.nameMode = this.sgGeneral.e("Name Mode", Nametags.NameMode.EntityName, "");
      this.ping = this.sgGeneral.b("Show Ping", true, ".");
      this.pops = this.sgGeneral.b("Show Pops", true, ".");
      this.showId = this.sgGeneral.b("Show Id", false, "");
      this.rounded = this.sgGeneral.b("Rounded", true, ".");
      this.shadow = this.sgGeneral.b("Shadow", true, ".");
      this.blur = this.sgGeneral.b("Blur", true, ".");
      this.entityTypes = this.sgGeneral.el("Entities", ".", class_1299.field_6097);
      this.armor = this.sgItems.b("Armor", false, ".");
      this.hand = this.sgItems.b("Hands", false, ".");
      this.itemScale = this.sgItems.d("Item Scale", 1.0D, 0.0D, 3.0D, 0.03D, ".");
      this.itemOffset = this.sgItems.d("Item Offset", 1.0D, 0.0D, 2.0D, 0.02D, ".");
      this.itemSeparation = this.sgItems.d("Item Separation", 0.0D, 0.0D, 5.0D, 0.05D, ".");
      this.filterMode = this.sgEnchantments.e("Enchantment Mode", FilterMode.Blacklist, ".");
      this.enchantments = this.sgEnchantments.l("Enchantments", ".", EnchantmentNames.enchantments, EnchantmentNames::getLongName);
      this.drawEnchants = this.sgEnchantments.b("Draw Enchants", false, ".");
      SettingGroup var10001 = this.sgEnchantments;
      Setting var10005 = this.drawEnchants;
      Objects.requireNonNull(var10005);
      this.enchantsAbove = var10001.b("Enchantments Above", true, ".", var10005::get);
      var10001 = this.sgEnchantments;
      var10005 = this.drawEnchants;
      Objects.requireNonNull(var10005);
      this.shortNames = var10001.b("Short Names", true, ".", var10005::get);
      var10001 = this.sgEnchantments;
      Setting var10008 = this.drawEnchants;
      Objects.requireNonNull(var10008);
      this.enchantScale = var10001.d("Enchantment Scale", 0.5D, 0.0D, 2.0D, 0.02D, ".", var10008::get);
      var10001 = this.sgEnchantments;
      var10008 = this.drawEnchants;
      Objects.requireNonNull(var10008);
      this.compact = var10001.d("Compact Enchantments", 0.0D, 0.0D, 1.0D, 0.01D, ".", var10008::get);
      var10001 = this.sgEnchantments;
      var10005 = this.drawEnchants;
      Objects.requireNonNull(var10005);
      this.center = var10001.b("Center Enchantments", true, ".", var10005::get);
      this.enchantmentsOffset = this.sgEnchantments.d("Enchantments Offset", 0.0D, 0.0D, 1.0D, 0.01D, ".", () -> {
         return (Boolean)this.drawEnchants.get() && !(Boolean)this.center.get();
      });
      this.txt = this.sgColor.c("Text Color", new BlackOutColor(255, 255, 255, 255), ".");
      this.background = BackgroundMultiSetting.of(this.sgGeneral, "Nametag");
      this.friendColor = this.sgColor.c("Friend Color", new BlackOutColor(150, 150, 255, 255), ".");
      this.colorMode = this.sgColor.e("Mode", Nametags.ColorMode.Dynamic, "What style to use");
      this.hp = this.sgColor.c("Health Color", new BlackOutColor(150, 150, 150, 255), ".", () -> {
         return this.colorMode.get() == Nametags.ColorMode.Custom;
      });
      this.stack = new class_4587();
      this.entities = new ArrayList();
      this.components = new ArrayList();
      INSTANCE = this;
   }

   public static Nametags getInstance() {
      return INSTANCE;
   }

   public static boolean shouldCancelLabel(class_1297 entity) {
      Nametags nametags = getInstance();
      if (nametags.enabled && nametags.shouldRender(entity)) {
         return true;
      } else {
         ESP esp = ESP.getInstance();
         return esp.enabled && esp.shouldRender(entity);
      }
   }

   @Event
   public void onTick(TickEvent.Post event) {
      if (BlackOut.mc.field_1687 != null && BlackOut.mc.field_1724 != null) {
         this.entities.clear();
         BlackOut.mc.field_1687.field_27733.method_31791((entity) -> {
            if (this.shouldRender(entity)) {
               this.entities.add(entity);
            }

         });
         this.entities.sort(Comparator.comparingDouble((entity) -> {
            return -BlackOut.mc.field_1773.method_19418().method_19326().method_1022(entity.method_19538());
         }));
      }
   }

   @Event
   public void onRender(RenderEvent.Hud.Post event) {
      if (BlackOut.mc.field_1687 != null && BlackOut.mc.field_1724 != null) {
         GlStateManager._disableDepthTest();
         GlStateManager._enableBlend();
         GlStateManager._disableCull();
         this.stack.method_22903();
         RenderUtils.unGuiScale(this.stack);
         this.entities.forEach((entity) -> {
            this.renderNameTag((double)event.tickDelta, entity);
         });
         this.stack.method_22909();
      }
   }

   public void renderNameTag(double tickDelta, class_1297 entity) {
      double x = class_3532.method_16436(tickDelta, entity.field_6014, entity.method_23317());
      double y = class_3532.method_16436(tickDelta, entity.field_6036, entity.method_23318());
      double z = class_3532.method_16436(tickDelta, entity.field_5969, entity.method_23321());
      float d = (float)BlackOut.mc.field_1773.method_19418().method_19326().method_1023(x, y, z).method_1033();
      float s = this.getScale(d);
      this.stack.method_22903();
      class_241 f = RenderUtils.getCoords(x, y + entity.method_5829().method_17940() + (Double)this.yOffset.get(), z, true);
      if (f == null) {
         this.stack.method_22909();
      } else {
         this.stack.method_46416(f.field_1343, f.field_1342, 0.0F);
         this.stack.method_22905(s, s, s);
         this.components.clear();
         Color color1;
         if (entity instanceof class_1657 && Managers.FRIENDS.isFriend((class_1657)entity)) {
            color1 = ((BlackOutColor)this.friendColor.get()).getColor();
         } else {
            color1 = ((BlackOutColor)this.txt.get()).getColor();
         }

         this.components.add(new Nametags.Component(((Nametags.NameMode)this.nameMode.get()).getName(entity), color1));
         if (entity instanceof class_1542) {
            class_1542 itemEntity = (class_1542)entity;
            int count = itemEntity.method_6983().method_7947();
            if (count > 1) {
               this.components.add(new Nametags.Component(count + "x", Color.WHITE));
            }
         }

         if (entity instanceof class_742) {
            class_742 player = (class_742)entity;
            if ((Boolean)this.ping.get()) {
               class_640 entry = BlackOut.mc.method_1562().method_2871(entity.method_5667());
               if (entry != null) {
                  this.components.add(new Nametags.Component(entry.method_2959() + "ms", color1));
               }
            }

            StatsManager.TrackerData trackerData = Managers.STATS.getStats(player);
            if ((Boolean)this.pops.get() && trackerData != null && trackerData.pops > 0) {
               this.components.add(new Nametags.Component("[" + trackerData.pops + "]", color1));
            }
         }

         if ((Boolean)this.showId.get()) {
            this.components.add(new Nametags.Component("id:" + entity.method_5628(), color1));
         }

         if (entity instanceof class_1309) {
            class_1309 livingEntity = (class_1309)entity;
            this.components.add(new Nametags.Component(String.format("%.1f", livingEntity.method_6032() + livingEntity.method_6067()), BlackOut.FONT.getWidth("20.0"), this.getColor(livingEntity, livingEntity.method_6032() + livingEntity.method_6067())));
         }

         this.length = 0.0F;
         this.offset = 0.0F;
         this.components.forEach((component) -> {
            this.length += BlackOut.FONT.getWidth(component.text);
         });
         this.length += (float)(this.components.size() * 5 - 5);
         this.stack.method_22903();
         this.stack.method_46416(-this.length / 2.0F, -9.0F, 0.0F);
         if ((Boolean)this.blur.get()) {
            RenderUtils.drawLoadedBlur("hudblur", this.stack, (renderer) -> {
               renderer.rounded(-2.0F, -5.0F, this.length + 4.0F, 10.0F, (Boolean)this.rounded.get() ? 3.0F : 0.0F, 10);
            });
            Renderer.onHUDBlur();
         }

         this.background.render(this.stack, -2.0F, -5.0F, this.length + 4.0F, 10.0F, (Boolean)this.rounded.get() ? 3.0F : 0.0F, (Boolean)this.shadow.get() ? 3.0F : 0.0F);
         this.components.forEach((component) -> {
            BlackOut.FONT.text(this.stack, component.text, 1.0F, this.offset, 0.0F, component.color, false, true);
            this.offset += component.width + 5.0F;
         });
         this.stack.method_22909();
         if ((Boolean)this.armor.get() || (Boolean)this.hand.get()) {
            this.stack.method_22903();
            this.stack.method_22904(0.0D, -16.0D * (Double)this.itemOffset.get() - 6.0D, 0.0D);
            this.stack.method_22903();
            float wbg = 16.0F;
            float separation = (((Double)this.itemSeparation.get()).floatValue() - 1.0F) * 4.0F;
            this.stack.method_22905(((Double)this.itemScale.get()).floatValue(), ((Double)this.itemScale.get()).floatValue(), 1.0F);
            this.stack.method_46416((wbg * 4.0F + separation * 3.0F) / -2.0F, -16.0F, 0.0F);
            if (entity instanceof class_742) {
               class_742 livingEntity = (class_742)entity;
               if ((Boolean)this.hand.get()) {
                  this.renderHandItem(livingEntity.method_6047(), wbg, separation, -1);
                  this.renderHandItem(livingEntity.method_6079(), wbg, separation, 4);
               }

               if ((Boolean)this.armor.get()) {
                  for(int i = 0; i < 4; ++i) {
                     class_1799 itemStack = livingEntity.method_31548().method_7372(3 - i);
                     if (!itemStack.method_7960()) {
                        RenderUtils.renderItem(this.stack, itemStack.method_7909(), (float)i * (wbg + separation), 0.0F, 16.0F);
                        class_2487 nbt = itemStack.method_7969();
                        if (nbt == null || !nbt.method_10545("Unbreakable") || !nbt.method_10577("Unbreakable")) {
                           int durabilityPercentage = Math.round((float)((itemStack.method_7936() - itemStack.method_7919()) * 100) / (float)itemStack.method_7936());
                           this.drawItemText(durabilityPercentage + " %", wbg, separation, i);
                           this.drawEnchantments(itemStack, wbg, separation, i);
                        }
                     }
                  }
               }
            }

            this.stack.method_22909();
            this.stack.method_22909();
         }

         this.stack.method_22909();
      }
   }

   private void renderHandItem(class_1799 itemStack, float wbg, float separation, int i) {
      if (!itemStack.method_7960()) {
         RenderUtils.renderItem(this.stack, itemStack.method_7909(), (float)i * (wbg + separation), 0.0F, 16.0F);
         if (itemStack.method_7946() || itemStack.method_7947() > 1) {
            this.drawItemText(String.valueOf(itemStack.method_7947()), wbg, separation, i);
         }

         this.drawEnchantments(itemStack, wbg, separation, i);
      }
   }

   private void drawItemText(String text, float wbg, float separation, int i) {
      BlackOut.FONT.text(this.stack, text, 0.6F, (float)i * (wbg + separation) + wbg / 2.0F, 16.0F, ((BlackOutColor)this.txt.get()).getRGB(), true, true);
   }

   private void drawEnchantments(class_1799 itemStack, float wbg, float separation, int i) {
      Map<class_1887, Integer> map = class_1890.method_8222(itemStack);
      if (!map.isEmpty() && (Boolean)this.drawEnchants.get()) {
         int y = 0;
         Iterator var7 = map.entrySet().iterator();

         while(var7.hasNext()) {
            Entry<class_1887, Integer> entry = (Entry)var7.next();
            class_1887 enchantment = (class_1887)entry.getKey();
            if (((FilterMode)this.filterMode.get()).shouldAccept(enchantment, (List)this.enchantments.get())) {
               String name = EnchantmentNames.getName(enchantment, (Boolean)this.shortNames.get());
               int level = (Integer)entry.getValue();
               String text;
               if (level > 1) {
                  text = name + " " + level;
               } else {
                  text = name;
               }

               float cy = (float)((Boolean)this.enchantsAbove.get() ? -y - 1 : y) * BlackOut.FONT.getHeight() * ((Double)this.enchantScale.get()).floatValue() * (1.0F - ((Double)this.compact.get()).floatValue() / 3.0F);
               BlackOut.FONT.text(this.stack, text, ((Double)this.enchantScale.get()).floatValue(), (float)i * (wbg + separation) + ((Boolean)this.center.get() ? 1.0F : ((Double)this.enchantmentsOffset.get()).floatValue()) * wbg / 2.0F, cy, ((BlackOutColor)this.txt.get()).getRGB(), (Boolean)this.center.get(), false);
               ++y;
            }
         }

      }
   }

   public boolean shouldRender(class_1297 entity) {
      AntiBot antiBot = AntiBot.getInstance();
      if (antiBot.enabled && antiBot.mode.get() == AntiBot.HandlingMode.Ignore && entity instanceof class_742) {
         class_742 player = (class_742)entity;
         if (antiBot.getBots().contains(player)) {
            return false;
         }
      }

      if (!((List)this.entityTypes.get()).contains(entity.method_5864())) {
         return false;
      } else {
         return entity != BlackOut.mc.field_1724 ? true : Freecam.getInstance().enabled;
      }
   }

   private Color getColor(class_1309 entity, float health) {
      return this.colorMode.get() == Nametags.ColorMode.Custom ? ((BlackOutColor)this.hp.get()).getColor() : ColorUtils.lerpColor((double)Math.min(health / (entity.method_6063() + (float)(entity instanceof class_1657 ? 16 : 0)), 1.0F), new Color(255, 0, 0, 255), new Color(0, 255, 0, 255));
   }

   private float getScale(float d) {
      float distSqrt = (float)Math.sqrt((double)d);
      return ((Double)this.scale.get()).floatValue() * 8.0F / distSqrt + ((Double)this.scaleInc.get()).floatValue() / 20.0F * distSqrt;
   }

   public static enum NameMode {
      Display,
      EntityName;

      private String getName(class_1297 entity) {
         return this == Display ? entity.method_5476().getString() : entity.method_5477().getString();
      }

      // $FF: synthetic method
      private static Nametags.NameMode[] $values() {
         return new Nametags.NameMode[]{Display, EntityName};
      }
   }

   public static enum ColorMode {
      Custom,
      Dynamic;

      // $FF: synthetic method
      private static Nametags.ColorMode[] $values() {
         return new Nametags.ColorMode[]{Custom, Dynamic};
      }
   }

   private static class Component {
      private final String text;
      private final float width;
      private final Color color;

      public Component(String text, Color color) {
         this.text = text;
         this.width = BlackOut.FONT.getWidth(text);
         this.color = color;
      }

      public Component(String text, float width, Color color) {
         this.text = text;
         this.width = width;
         this.color = color;
      }
   }
}
