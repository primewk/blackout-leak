package bodevelopment.client.blackout.module.modules.combat.offensive;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.enums.RenderShape;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.PacketEvent;
import bodevelopment.client.blackout.event.events.RenderEvent;
import bodevelopment.client.blackout.event.events.TickEvent;
import bodevelopment.client.blackout.mixin.accessors.AccessorInteractEntityC2SPacket;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.randomstuff.BlackOutColor;
import bodevelopment.client.blackout.randomstuff.Pair;
import bodevelopment.client.blackout.randomstuff.timers.TickTimerList;
import bodevelopment.client.blackout.randomstuff.timers.TimerMap;
import bodevelopment.client.blackout.util.render.Render3DUtils;
import net.minecraft.class_1297;
import net.minecraft.class_238;
import net.minecraft.class_243;
import net.minecraft.class_2596;
import net.minecraft.class_2824;
import net.minecraft.class_745;
import net.minecraft.class_2824.class_5907;

public class BackTrack extends Module {
   private static BackTrack INSTANCE;
   private final SettingGroup sgGeneral = this.addGroup("General");
   public final Setting<Integer> time;
   public final Setting<Integer> maxTime;
   private final Setting<RenderShape> renderShape;
   private final Setting<BlackOutColor> lineColor;
   private final Setting<BlackOutColor> sideColor;
   public final TickTimerList<Pair<class_745, class_238>> hit;
   public final TickTimerList<Pair<class_745, class_238>> spoofed;
   public final TimerMap<class_745, class_243> realPositions;

   public BackTrack() {
      super("Back Track", ".", SubCategory.OFFENSIVE, true);
      this.time = this.sgGeneral.i("Ticks", 5, 0, 20, 1, ".");
      this.maxTime = this.sgGeneral.i("Max Ticks", 50, 0, 100, 1, ".");
      this.renderShape = this.sgGeneral.e("Render Shape", RenderShape.Full, "Which parts of render should be rendered.");
      this.lineColor = this.sgGeneral.c("Line Color", new BlackOutColor(255, 0, 0, 255), "Line color of rendered boxes.");
      this.sideColor = this.sgGeneral.c("Side Color", new BlackOutColor(255, 0, 0, 50), "Side color of rendered boxes.");
      this.hit = new TickTimerList(false);
      this.spoofed = new TickTimerList(false);
      this.realPositions = new TimerMap(false);
      INSTANCE = this;
   }

   public static BackTrack getInstance() {
      return INSTANCE;
   }

   @Event
   public void onSend(PacketEvent.Send event) {
      class_2596 var3 = event.packet;
      if (var3 instanceof class_2824) {
         class_2824 packet = (class_2824)var3;
         AccessorInteractEntityC2SPacket accessor = (AccessorInteractEntityC2SPacket)packet;
         if (accessor.getType().method_34211() == class_5907.field_29172) {
            class_1297 var5 = BlackOut.mc.field_1687.method_8469(accessor.getId());
            if (var5 instanceof class_745) {
               class_745 player = (class_745)var5;
               class_238 box = player.method_5829();
               Pair<class_745, class_238> pair = new Pair(player, box);
               this.hit.remove((timer) -> {
                  return ((class_745)((Pair)timer.value).method_15442()).equals(player);
               });
               this.hit.add(pair, (Integer)this.time.get());
               if (!this.spoofed.contains((timer) -> {
                  return ((class_745)((Pair)timer.value).method_15442()).equals(player);
               })) {
                  this.spoofed.remove((timer) -> {
                     return ((class_745)((Pair)timer.value).method_15442()).equals(player);
                  });
                  this.spoofed.add(pair, (Integer)this.maxTime.get());
               }
            }
         }
      }

   }

   @Event
   public void onRender(RenderEvent.World.Post event) {
      this.realPositions.remove((key, value) -> {
         if (System.currentTimeMillis() > value.endTime) {
            return true;
         } else {
            class_238 box = new class_238(((class_243)value.value).method_10216() - 0.3D, ((class_243)value.value).method_10214(), ((class_243)value.value).method_10215() - 0.3D, ((class_243)value.value).method_10216() + 0.3D, ((class_243)value.value).method_10214() + key.method_5829().method_17940(), ((class_243)value.value).method_10215() + 0.3D);
            Render3DUtils.box(box, (BlackOutColor)this.sideColor.get(), (BlackOutColor)this.lineColor.get(), (RenderShape)this.renderShape.get());
            return false;
         }
      });
   }

   @Event
   public void onTick(TickEvent.Pre event) {
      this.hit.timers.removeIf((item) -> {
         if (item.ticks-- <= 0) {
            this.spoofed.remove((timer) -> {
               return ((class_745)((Pair)item.value).method_15442()).equals(((Pair)timer.value).method_15442());
            });
            return true;
         } else {
            return false;
         }
      });
      this.spoofed.update();
   }
}
