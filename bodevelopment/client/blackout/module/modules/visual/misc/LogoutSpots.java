package bodevelopment.client.blackout.module.modules.visual.misc;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.enums.RenderShape;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.GameJoinEvent;
import bodevelopment.client.blackout.event.events.PacketEvent;
import bodevelopment.client.blackout.event.events.RemoveEvent;
import bodevelopment.client.blackout.event.events.RenderEvent;
import bodevelopment.client.blackout.event.events.TickEvent;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.manager.managers.StatsManager;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.randomstuff.BlackOutColor;
import bodevelopment.client.blackout.randomstuff.timers.TimerList;
import bodevelopment.client.blackout.randomstuff.timers.TimerMap;
import bodevelopment.client.blackout.util.BoxUtils;
import bodevelopment.client.blackout.util.ColorUtils;
import bodevelopment.client.blackout.util.render.Render3DUtils;
import bodevelopment.client.blackout.util.render.RenderUtils;
import bodevelopment.client.blackout.util.render.WireframeRenderer;
import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import net.minecraft.class_1297;
import net.minecraft.class_1799;
import net.minecraft.class_241;
import net.minecraft.class_243;
import net.minecraft.class_2487;
import net.minecraft.class_2596;
import net.minecraft.class_2678;
import net.minecraft.class_4587;
import net.minecraft.class_640;
import net.minecraft.class_742;
import net.minecraft.class_7828;

public class LogoutSpots extends Module {
   private static LogoutSpots INSTANCE;
   private final SettingGroup sgRendering = this.addGroup("Rendering");
   private final SettingGroup sgInfo = this.addGroup("Info");
   private final Setting<Boolean> model;
   private final Setting<RenderShape> renderShape;
   private final Setting<BlackOutColor> lineColor;
   private final Setting<BlackOutColor> sideColor;
   private final Setting<Double> maxTime;
   private final Setting<Double> fadeTime;
   private final Setting<Double> infoScale;
   private final Setting<Boolean> name;
   private final Setting<Boolean> armor;
   private final Setting<Boolean> items;
   private final Setting<Boolean> health;
   private final Setting<Boolean> ping;
   private final Setting<Boolean> pops;
   private final Setting<Boolean> time;
   private final List<LogoutSpots.Spot> spots;
   private final TimerMap<UUID, class_1799[]> prevItems;
   private final TimerList<UUID> removedUUIDs;
   private final TimerList<class_742> removedEntities;
   private final class_4587 matrixStack;
   private float alphaMulti;

   public LogoutSpots() {
      super("Logout Spots", "Traces to other entities", SubCategory.MISC_VISUAL, true);
      this.model = this.sgRendering.b("Model", false, ".");
      this.renderShape = this.sgRendering.e("Render Shape", RenderShape.Full, ".");
      this.lineColor = this.sgRendering.c("Line Color", new BlackOutColor(255, 0, 0, 255), ".");
      this.sideColor = this.sgRendering.c("Side Color", new BlackOutColor(255, 0, 0, 50), ".");
      this.maxTime = this.sgRendering.d("Max Time", 60.0D, 0.0D, 100.0D, 1.0D, ".");
      this.fadeTime = this.sgRendering.d("Fade Time", 20.0D, 0.0D, 100.0D, 1.0D, ".");
      this.infoScale = this.sgRendering.d("Info Scale", 1.0D, 0.0D, 2.0D, 0.1D, ".");
      this.name = this.sgInfo.b("Names", true, ".");
      this.armor = this.sgInfo.b("Armor", false, ".");
      this.items = this.sgInfo.b("Items", false, ".");
      this.health = this.sgInfo.b("Health", false, ".");
      this.ping = this.sgInfo.b("Ping", false, ".");
      this.pops = this.sgInfo.b("Pops", false, ".");
      this.time = this.sgInfo.b("Time", false, ".");
      this.spots = new ArrayList();
      this.prevItems = new TimerMap(true);
      this.removedUUIDs = new TimerList(true);
      this.removedEntities = new TimerList(true);
      this.matrixStack = new class_4587();
      INSTANCE = this;
   }

   public static LogoutSpots getInstance() {
      return INSTANCE;
   }

   @Event
   public void onGameJoin(GameJoinEvent event) {
      this.spots.clear();
   }

   public void onEnable() {
      this.spots.clear();
   }

   @Event
   public void onJoin(class_2678 event) {
      this.spots.clear();
   }

   @Event
   public void onEntityRemove(PacketEvent.Receive.Pre event) {
      class_2596 var3 = event.packet;
      if (var3 instanceof class_7828) {
         class_7828 packet = (class_7828)var3;
         packet.comp_1105().stream().filter(this::checkMatchingEntities).forEach((uuid) -> {
            this.removedUUIDs.add(uuid, 1.0D);
         });
      }

   }

   @Event
   public void onRemove(RemoveEvent event) {
      class_1297 var3 = event.entity;
      if (var3 instanceof class_742) {
         class_742 player = (class_742)var3;
         if (this.checkMatchingUUIDs(player)) {
            this.removedEntities.add(player, 1.0D);
         }
      }

   }

   @Event
   public void onTick(TickEvent.Pre event) {
      if (BlackOut.mc.field_1687 != null) {
         BlackOut.mc.field_1687.method_18456().forEach((player) -> {
            UUID uuid = player.method_7334().getId();
            if (!this.itemsEmpty(player)) {
               class_1799[] stacks;
               if (this.prevItems.containsKey(uuid)) {
                  stacks = (class_1799[])this.prevItems.get(uuid);
               } else {
                  stacks = new class_1799[6];
               }

               for(int i = 0; i < 4; ++i) {
                  stacks[i + 1] = player.method_31548().method_7372(3 - i);
               }

               stacks[0] = player.method_6047();
               stacks[5] = player.method_6079();
               this.prevItems.removeKey(uuid);
               this.prevItems.add(uuid, stacks, 0.3D);
            }
         });
      }
   }

   @Event
   public void onRender(RenderEvent.World.Post event) {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null) {
         this.spots.removeIf((spot) -> {
            if (!spot.seen && this.anyPlayerMatches(spot.player.method_7334().getId())) {
               spot.setSeen();
            }

            this.setAlpha(spot);
            if (this.alphaMulti <= 0.0F) {
               return true;
            } else {
               if ((Boolean)this.model.get()) {
                  WireframeRenderer.renderModel(spot.player, ((BlackOutColor)this.lineColor.get()).alphaMulti((double)this.alphaMulti), ((BlackOutColor)this.sideColor.get()).alphaMulti((double)this.alphaMulti), (RenderShape)this.renderShape.get(), spot.tickDelta);
               } else {
                  Render3DUtils.box(spot.player.method_5829(), ((BlackOutColor)this.sideColor.get()).alphaMulti((double)this.alphaMulti), ((BlackOutColor)this.lineColor.get()).alphaMulti((double)this.alphaMulti), (RenderShape)this.renderShape.get());
               }

               return (double)(System.currentTimeMillis() - spot.logTime) > ((Double)this.maxTime.get() + (Double)this.fadeTime.get()) * 1000.0D;
            }
         });
      }
   }

   @Event
   public void onRender2D(RenderEvent.Hud.Pre event) {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null) {
         GlStateManager._disableDepthTest();
         GlStateManager._enableBlend();
         GlStateManager._disableCull();
         this.matrixStack.method_22903();
         RenderUtils.unGuiScale(this.matrixStack);
         this.spots.forEach(this::renderInfo);
         this.matrixStack.method_22909();
      }
   }

   private boolean anyPlayerMatches(UUID uuid) {
      Iterator var2 = BlackOut.mc.field_1687.method_18456().iterator();

      class_742 player;
      do {
         if (!var2.hasNext()) {
            return false;
         }

         player = (class_742)var2.next();
      } while(!player.method_7334().getId().equals(uuid));

      return true;
   }

   private boolean itemsEmpty(class_742 player) {
      for(int i = 0; i < 4; ++i) {
         class_1799 armorStack = player.method_31548().method_7372(3 - i);
         if (!armorStack.method_7960()) {
            return false;
         }
      }

      return player.method_6047().method_7960() && player.method_6079().method_7960();
   }

   private void setAlpha(LogoutSpots.Spot spot) {
      float time = (float)(System.currentTimeMillis() - spot.logTime) / 1000.0F;
      if ((double)time <= (Double)this.maxTime.get()) {
         this.alphaMulti = 1.0F;
      } else {
         this.alphaMulti = 1.0F - (time - ((Double)this.maxTime.get()).floatValue()) / ((Double)this.fadeTime.get()).floatValue();
      }

      if (spot.seen) {
         this.alphaMulti *= Math.max(1.0F - (float)(System.currentTimeMillis() - spot.seenSince) / 300.0F, 0.0F);
      }

   }

   private void renderInfo(LogoutSpots.Spot spot) {
      this.setAlpha(spot);
      int textColor = ColorUtils.withAlpha(-1, (int)(this.alphaMulti * 255.0F));
      class_243 pos = this.infoPos(spot);
      class_241 coords = RenderUtils.getCoords(pos.method_10216(), pos.method_10214(), pos.method_10215(), true);
      if (coords != null) {
         this.matrixStack.method_22903();
         this.matrixStack.method_46416(coords.field_1343, coords.field_1342, 0.0F);
         float scale = this.infoScale(pos) * ((Double)this.infoScale.get()).floatValue();
         this.matrixStack.method_22905(scale, scale, 1.0F);
         this.matrixStack.method_22904(0.0D, -this.infoHeight(spot) / 2.0D, 0.0D);
         if (this.itemsFound(spot)) {
            this.renderArmorAndItems(spot, textColor);
         }

         if ((Boolean)this.name.get()) {
            this.renderName(spot, textColor);
         }

         if ((Boolean)this.health.get() || (Boolean)this.ping.get() || (Boolean)this.pops.get()) {
            this.renderInfoText(spot, textColor);
         }

         if ((Boolean)this.time.get()) {
            this.renderTime(spot, textColor);
         }

         this.matrixStack.method_22909();
      }
   }

   private void renderTime(LogoutSpots.Spot spot, int textColor) {
      BlackOut.FONT.text(this.matrixStack, String.format("%.1fs", (double)(System.currentTimeMillis() - spot.logTime) / 1000.0D), 1.0F, 0.0F, 0.0F, textColor, true, false);
   }

   private void renderInfoText(LogoutSpots.Spot spot, int textColor) {
      List<String> strings = new ArrayList();
      if ((Boolean)this.health.get()) {
         strings.add(String.format("%.1f", spot.health));
      }

      if ((Boolean)this.ping.get()) {
         strings.add(spot.ping + " ms");
      }

      if ((Boolean)this.pops.get()) {
         strings.add("[" + spot.pops + "]");
      }

      double width = (double)((strings.size() - 1) * 3);

      Iterator var6;
      String string;
      for(var6 = strings.iterator(); var6.hasNext(); width += (double)BlackOut.FONT.getWidth(string)) {
         string = (String)var6.next();
      }

      this.matrixStack.method_22903();
      this.matrixStack.method_22904(-width / 2.0D, 0.0D, 0.0D);
      var6 = strings.iterator();

      while(var6.hasNext()) {
         string = (String)var6.next();
         BlackOut.FONT.text(this.matrixStack, string, 1.0F, 0.0F, 0.0F, textColor, false, false);
         this.matrixStack.method_46416(BlackOut.FONT.getWidth(string) + 3.0F, 0.0F, 0.0F);
      }

      this.matrixStack.method_22909();
      this.matrixStack.method_46416(0.0F, BlackOut.FONT.getHeight(), 0.0F);
   }

   private void renderName(LogoutSpots.Spot spot, int textColor) {
      BlackOut.FONT.text(this.matrixStack, spot.player.method_5477().getString(), 1.0F, 0.0F, 0.0F, textColor, true, false);
      this.matrixStack.method_46416(0.0F, BlackOut.FONT.getHeight(), 0.0F);
   }

   private void renderArmorAndItems(LogoutSpots.Spot spot, int textColor) {
      int size = 0;
      Iterator var4 = spot.items.iterator();

      while(true) {
         LogoutSpots.ItemComponent item;
         do {
            if (!var4.hasNext()) {
               this.matrixStack.method_22903();
               this.matrixStack.method_46416((float)(-size * 8), 0.0F, 0.0F);
               spot.items.forEach((itemx) -> {
                  if (itemx.armor() && (Boolean)this.armor.get() || !itemx.armor() && (Boolean)this.items.get()) {
                     this.renderComponent(itemx, textColor);
                  }

               });
               this.matrixStack.method_22909();
               this.matrixStack.method_46416(0.0F, 20.0F, 0.0F);
               return;
            }

            item = (LogoutSpots.ItemComponent)var4.next();
         } while((!item.armor() || !(Boolean)this.armor.get()) && (item.armor() || !(Boolean)this.items.get()));

         ++size;
      }
   }

   private void renderComponent(LogoutSpots.ItemComponent component, int textColor) {
      class_1799 itemStack = component.itemStack();
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alphaMulti);
      RenderUtils.renderItem(this.matrixStack, itemStack.method_7909(), 0.0F, 0.0F, 16.0F);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      if (component.armor()) {
         class_2487 nbt = itemStack.method_7969();
         if (nbt != null && nbt.method_10545("Unbreakable") && nbt.method_10577("Unbreakable")) {
            return;
         }

         int durabilityPercentage = Math.round((float)((itemStack.method_7936() - itemStack.method_7919()) * 100) / (float)itemStack.method_7936());
         BlackOut.FONT.text(this.matrixStack, durabilityPercentage + "%", 0.8F, 8.0F, 16.0F, textColor, true, true);
      } else if (itemStack.method_7946() || itemStack.method_7947() > 1) {
         BlackOut.FONT.text(this.matrixStack, String.valueOf(itemStack.method_7947()), 0.8F, 8.0F, 16.0F, textColor, true, true);
      }

      this.matrixStack.method_46416(16.0F, 0.0F, 0.0F);
   }

   private double infoHeight(LogoutSpots.Spot spot) {
      double height = 0.0D;
      if ((Boolean)this.name.get()) {
         height += (double)BlackOut.FONT.getHeight();
      }

      if (this.itemsFound(spot)) {
         height += 20.0D;
      }

      if ((Boolean)this.health.get() || (Boolean)this.ping.get() || (Boolean)this.pops.get()) {
         height += (double)BlackOut.FONT.getHeight();
      }

      if ((Boolean)this.time.get()) {
         height += (double)BlackOut.FONT.getHeight();
      }

      return height;
   }

   private boolean itemsFound(LogoutSpots.Spot spot) {
      Iterator var2 = spot.items.iterator();

      LogoutSpots.ItemComponent itemComponent;
      do {
         do {
            if (!var2.hasNext()) {
               return false;
            }

            itemComponent = (LogoutSpots.ItemComponent)var2.next();
         } while(itemComponent.armor() && !(Boolean)this.armor.get());
      } while(!itemComponent.armor() && !(Boolean)this.items.get());

      return true;
   }

   private float infoScale(class_243 pos) {
      double distance = BlackOut.mc.field_1773.method_19418().method_19326().method_1020(pos).method_1033();
      float distSqrt = (float)Math.sqrt(distance);
      return 8.0F / distSqrt + 0.05F * distSqrt;
   }

   private class_243 infoPos(LogoutSpots.Spot spot) {
      return BoxUtils.middle(spot.player.method_5829());
   }

   private boolean checkMatchingEntities(UUID uuid) {
      return !this.removedEntities.removeAll((timer) -> {
         class_742 player = (class_742)timer.value;
         GameProfile profile = player.method_7334();
         if (uuid.equals(profile.getId())) {
            this.spots.add(new LogoutSpots.Spot(player));
            return true;
         } else {
            return false;
         }
      });
   }

   private boolean checkMatchingUUIDs(class_742 player) {
      GameProfile profile = player.method_7334();
      return !this.removedUUIDs.removeAll((timer) -> {
         UUID uuid = (UUID)timer.value;
         if (uuid.equals(profile.getId())) {
            this.spots.add(new LogoutSpots.Spot(player));
            return true;
         } else {
            return true;
         }
      });
   }

   private static class Spot {
      private final class_742 player;
      private final float tickDelta;
      private final List<LogoutSpots.ItemComponent> items;
      private final float health;
      private final int ping;
      private final int pops;
      private final long logTime;
      private boolean seen;
      private long seenSince;

      public Spot(class_742 player) {
         this.tickDelta = BlackOut.mc.method_1488();
         this.items = new ArrayList();
         this.logTime = System.currentTimeMillis();
         this.seen = false;
         this.seenSince = 0L;
         this.player = player;
         this.fillItems();
         this.health = player.method_6032() + player.method_6067();
         class_640 entry = BlackOut.mc.method_1562().method_2871(player.method_7334().getId());
         if (entry != null) {
            this.ping = entry.method_2959();
         } else {
            this.ping = -1;
         }

         StatsManager.TrackerData trackerData = Managers.STATS.getStats(player);
         if (trackerData == null) {
            this.pops = 0;
         } else {
            this.pops = trackerData.pops;
         }

      }

      private void fillItems() {
         UUID uuid = this.player.method_7334().getId();
         TimerMap<UUID, class_1799[]> map = LogoutSpots.getInstance().prevItems;
         if (map.containsKey(uuid)) {
            class_1799[] arr = (class_1799[])map.get(uuid);

            for(int i = 0; i < 6; ++i) {
               class_1799 stack = arr[i];
               if (!stack.method_7960()) {
                  this.items.add(new LogoutSpots.ItemComponent(stack, i != 0 && i != 5));
               }
            }
         }

      }

      private void setSeen() {
         this.seen = true;
         this.seenSince = System.currentTimeMillis();
      }
   }

   private static record ItemComponent(class_1799 itemStack, boolean armor) {
      private ItemComponent(class_1799 itemStack, boolean armor) {
         this.itemStack = itemStack;
         this.armor = armor;
      }

      public class_1799 itemStack() {
         return this.itemStack;
      }

      public boolean armor() {
         return this.armor;
      }
   }
}
