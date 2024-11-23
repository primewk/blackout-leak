package bodevelopment.client.blackout.module.modules.visual.misc;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.event.Event;
import bodevelopment.client.blackout.event.events.KeyEvent;
import bodevelopment.client.blackout.event.events.MouseButtonEvent;
import bodevelopment.client.blackout.event.events.RenderEvent;
import bodevelopment.client.blackout.keys.KeyBind;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.module.Module;
import bodevelopment.client.blackout.module.SubCategory;
import bodevelopment.client.blackout.module.setting.Setting;
import bodevelopment.client.blackout.module.setting.SettingGroup;
import bodevelopment.client.blackout.util.render.RenderUtils;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.class_1297;
import net.minecraft.class_1657;
import net.minecraft.class_4587;
import net.minecraft.class_742;

public class Spectate extends Module {
   private static Spectate INSTANCE;
   private final SettingGroup sgGeneral = this.addGroup("General");
   private final Setting<Boolean> ignoreFriends;
   private final Setting<KeyBind> forwardKey;
   private final Setting<KeyBind> backKey;
   private final List<class_1657> playerEntities;
   private class_1657 target;
   private int prevI;
   private final class_4587 stack;

   public Spectate() {
      super("Spectate", ".", SubCategory.MISC_VISUAL, true);
      this.ignoreFriends = this.sgGeneral.b("Ignore Friends", true, "Doesn't spectate friends.");
      this.forwardKey = this.sgGeneral.k("Forward", ".");
      this.backKey = this.sgGeneral.k("Back", ".");
      this.playerEntities = new ArrayList();
      this.prevI = 0;
      this.stack = new class_4587();
      INSTANCE = this;
   }

   public static Spectate getInstance() {
      return INSTANCE;
   }

   @Event
   public void onRender(RenderEvent.Hud.Pre event) {
      if (BlackOut.mc.field_1724 != null && BlackOut.mc.field_1687 != null) {
         this.stack.method_22903();
         RenderUtils.unGuiScale(this.stack);
         if (this.target instanceof class_742) {
            BlackOut.FONT.text(this.stack, "Spectating " + this.target.method_5477().getString(), 2.0F, (float)BlackOut.mc.method_22683().method_4480() / 2.0F, (float)BlackOut.mc.method_22683().method_4507() / 2.0F + BlackOut.FONT.getHeight() * 3.0F, Color.WHITE, true, true);
         }

         this.stack.method_22909();
      }
   }

   @Event
   public void onKey(KeyEvent event) {
      if (event.pressed) {
         if (((KeyBind)this.forwardKey.get()).isKey(event.key)) {
            this.set(this.move(true));
         }

         if (((KeyBind)this.backKey.get()).isKey(event.key)) {
            this.set(this.move(false));
         }

      }
   }

   @Event
   public void onMouse(MouseButtonEvent event) {
      if (event.pressed) {
         if (((KeyBind)this.forwardKey.get()).isMouse(event.button)) {
            this.set(this.move(true));
         }

         if (((KeyBind)this.backKey.get()).isMouse(event.button)) {
            this.set(this.move(false));
         }

      }
   }

   public class_1297 getEntity() {
      this.updateList();
      if (!this.playerEntities.contains(this.target)) {
         this.set(this.move(false));
      } else {
         this.prevI = this.playerEntities.indexOf(this.target);
      }

      return this.target;
   }

   private void set(int i) {
      if (this.playerEntities.isEmpty()) {
         this.prevI = 0;
         this.target = BlackOut.mc.field_1724;
      } else {
         this.prevI = i;
         this.target = (class_1657)this.playerEntities.get(i);
      }
   }

   private int move(boolean increase) {
      int max = this.playerEntities.size() - 1;
      if (increase) {
         return this.prevI == max ? 0 : this.prevI + 1;
      } else {
         return this.prevI == 0 ? max : this.prevI - 1;
      }
   }

   private void updateList() {
      this.playerEntities.clear();
      Iterator var1 = BlackOut.mc.field_1687.method_18456().iterator();

      while(true) {
         class_1657 player;
         do {
            do {
               if (!var1.hasNext()) {
                  return;
               }

               player = (class_1657)var1.next();
            } while(player == BlackOut.mc.field_1724);
         } while((Boolean)this.ignoreFriends.get() && Managers.FRIENDS.isFriend(player));

         this.playerEntities.add(player);
      }
   }
}
