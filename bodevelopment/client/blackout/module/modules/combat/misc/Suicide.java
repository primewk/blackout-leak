package bodevelopment.client.blackout.module.modules.combat.misc;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.TickEvent;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.modules.combat.offensive.AutoCrystal;
import bodevelopment.client.blackout.module.modules.combat.offensive.BedAura;
import bodevelopment.client.blackout.module.modules.combat.offensive.CreeperAura;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.util.ItemUtils;
import bodevelopment.client.blackout.util.OLEPOSSUtils;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import net.minecraft.class_1304;
import net.minecraft.class_1713;
import net.minecraft.class_1723;
import net.minecraft.class_1738;
import net.minecraft.class_418;

public class Suicide extends Module {
   private static Suicide INSTANCE;
   private final SettingGroup sgGeneral = this.addGroup("General");
   public final Setting<Boolean> disableDeath;
   public final Setting<Boolean> useCA;
   public final Setting<Boolean> enableCA;
   public final Setting<Boolean> useBA;
   public final Setting<Boolean> enableBA;
   public final Setting<Boolean> useCreeper;
   public final Setting<Boolean> enableCreeper;
   public final Setting<Boolean> offHand;
   public final Setting<Integer> dropArmor;

   public Suicide() {
      super("Suicide", "Commits suicide. Recommended.", SubCategory.MISC_COMBAT, true);
      this.disableDeath = this.sgGeneral.b("Disable On Death", true, "Disables the module on death.");
      this.useCA = this.sgGeneral.b("Use Auto Crystal", true, "Uses Auto Crystal to kill you.");
      SettingGroup var10001 = this.sgGeneral;
      Setting var10005 = this.useCA;
      Objects.requireNonNull(var10005);
      this.enableCA = var10001.b("Enable Auto Crystal", true, "Enables Auto Crystal when enabled.", var10005::get);
      this.useBA = this.sgGeneral.b("Use Bed Aura", false, "Uses Bed Aura to kill you.");
      var10001 = this.sgGeneral;
      var10005 = this.useBA;
      Objects.requireNonNull(var10005);
      this.enableBA = var10001.b("Enable Bed Aura", true, "Enables Bed Aura on toggle", var10005::get);
      this.useCreeper = this.sgGeneral.b("Use Creeper Aura", false, "Uses Creeper Aura to kill you.");
      var10001 = this.sgGeneral;
      var10005 = this.useCreeper;
      Objects.requireNonNull(var10005);
      this.enableCreeper = var10001.b("Enable Creeper Aura", true, "Enables Creeper Aura on toggle.", var10005::get);
      this.offHand = this.sgGeneral.b("Off Hand", false, "Stops Off Hand from saving you while suiciding");
      this.dropArmor = this.sgGeneral.i("Drop Armor", 0, 0, 4, 1, "How many armor pieces to drop.");
      INSTANCE = this;
   }

   public static Suicide getInstance() {
      return INSTANCE;
   }

   public void onEnable() {
      if ((Boolean)this.useCA.get() && (Boolean)this.enableCA.get()) {
         AutoCrystal.getInstance().enable();
      }

      if ((Boolean)this.useBA.get() && (Boolean)this.enableBA.get()) {
         BedAura.getInstance().enable();
      }

      if ((Boolean)this.useCreeper.get() && (Boolean)this.enableCreeper.get()) {
         CreeperAura.getInstance().enable();
      }

      this.dropArmor();
   }

   @Event
   public void onTick(TickEvent.Post event) {
      if (BlackOut.mc.field_1755 instanceof class_418 && (Boolean)this.disableDeath.get()) {
         this.disable("died");
      }

   }

   private void dropArmor() {
      if ((Integer)this.dropArmor.get() >= 1) {
         boolean dropped = false;

         for(Iterator var2 = this.toDrop((Integer)this.dropArmor.get()).iterator(); var2.hasNext(); dropped = true) {
            class_1304 equipmentSlot = (class_1304)var2.next();
            BlackOut.mc.field_1761.method_2906(0, 8 - equipmentSlot.method_5927(), 0, class_1713.field_7795, BlackOut.mc.field_1724);
         }

         if (dropped && BlackOut.mc.field_1724.field_7512 instanceof class_1723) {
            BlackOut.mc.field_1724.method_7346();
         }

      }
   }

   private List<class_1304> toDrop(int amount) {
      List<class_1304> list = (List)Arrays.stream(OLEPOSSUtils.equipmentSlots).filter((slot) -> {
         return BlackOut.mc.field_1724.method_31548().method_7372(slot.method_5927()).method_7909() instanceof class_1738;
      }).sorted(Comparator.comparingDouble((slot) -> {
         return ItemUtils.getArmorValue(BlackOut.mc.field_1724.method_31548().method_7372(slot.method_5927()));
      })).collect(Collectors.toList());
      return list.subList(Math.max(0, list.size() - amount), list.size());
   }
}
