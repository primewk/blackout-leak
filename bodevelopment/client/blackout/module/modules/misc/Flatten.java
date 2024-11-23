package bodevelopment.client.blackout.module.modules.misc;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.event.events.TickEvent;
import bodevelopment.client.blackout.module.ObsidianModule;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.randomstuff.PlaceData;
import bodevelopment.client.blackout.util.SettingUtils;
import java.util.Objects;
import net.minecraft.class_2338;

public class Flatten extends ObsidianModule {
   private final Setting<Boolean> setY;
   private final Setting<Integer> y;
   private int height;

   public Flatten() {
      super("Flatten", "Places blocks under you.", SubCategory.MISC);
      this.setY = this.sgGeneral.b("Set Y", true, "");
      SettingGroup var10001 = this.sgGeneral;
      Setting var10008 = this.setY;
      Objects.requireNonNull(var10008);
      this.y = var10001.i("Y", 3, -64, 300, 1, "", var10008::get);
      this.height = 0;
   }

   public void onTick(TickEvent.Pre event) {
      super.onTick(event);
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1724.method_24828()) {
         this.height = (int)(Math.round(BlackOut.mc.field_1724.method_23318()) - 1L);
      }

   }

   protected void addPlacements() {
      this.insideBlocks.forEach((pos) -> {
         if (!this.blockPlacements.contains(pos)) {
            PlaceData data = SettingUtils.getPlaceData(pos);
            if (!data.valid()) {
               class_2338 support = this.findSupport(pos);
               if (support == null) {
                  return;
               }
            } else if (!SettingUtils.inPlaceRange(data.pos())) {
               return;
            }

            this.blockPlacements.add(pos);
         }

      });
   }

   protected void addInsideBlocks() {
      class_2338 center = BlackOut.mc.field_1724.method_24515().method_33096((Boolean)this.setY.get() ? (Integer)this.y.get() : this.height);

      for(int x = -6; x <= 6; ++x) {
         for(int z = -6; z <= 6; ++z) {
            this.insideBlocks.add(center.method_10069(x, 0, z));
         }
      }

   }
}
