package bodevelopment.client.blackout.gui.clickgui.screens;

import bodevelopment.client.blackout.BlackOut;
import bodevelopment.client.blackout.gui.clickgui.ClickGuiScreen;
import bodevelopment.client.blackout.manager.Managers;
import bodevelopment.client.blackout.manager.managers.FriendsManager;
import bodevelopment.client.blackout.rendering.renderer.TextureRenderer;
import bodevelopment.client.blackout.util.render.RenderUtils;
import com.mojang.authlib.GameProfile;
import java.awt.Color;

public class FriendsScreen extends ClickGuiScreen {
   private static final int lineColor = (new Color(50, 50, 50, 255)).getRGB();
   private boolean first;

   public FriendsScreen() {
      super("Friends", 800.0F, 500.0F, true);
   }

   protected float getLength() {
      return (float)(Managers.FRIENDS.getFriends().size() * 60 + 15);
   }

   public void render() {
      this.renderFriends();
   }

   private void renderFriends() {
      this.stack.method_22903();
      this.stack.method_46416(0.0F, -this.scroll.get() + 15.0F, 0.0F);
      this.first = true;
      Managers.FRIENDS.getFriends().forEach(this::renderFriend);
      this.stack.method_22909();
   }

   private void renderFriend(FriendsManager.Friend friend) {
      if (!this.first) {
         RenderUtils.line(this.stack, -10.0F, 0.0F, this.width + 10.0F, 0.0F, lineColor);
      }

      this.first = false;
      this.stack.method_22903();
      if (friend.seen()) {
         int texture = BlackOut.mc.method_1531().method_4619(BlackOut.mc.method_1582().method_52862(new GameProfile(friend.getUuid(), friend.getName())).comp_1626()).method_4624();
         TextureRenderer.renderFitRounded(this.stack, 10.0F, 10.0F, 40.0F, 40.0F, 0.125F, 0.125F, 0.25F, 0.25F, 20.0F, 40, texture);
      }

      BlackOut.FONT.text(this.stack, friend.getName(), 2.0F, 80.0F, 15.0F, Color.WHITE, false, true);
      BlackOut.FONT.text(this.stack, friend.seen() ? friend.getUuid().toString() : "...", 1.5F, 80.0F, 45.0F, Color.LIGHT_GRAY, false, true);
      this.stack.method_22909();
      this.stack.method_46416(0.0F, 60.0F, 0.0F);
   }
}
