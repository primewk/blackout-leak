package bodevelopment.client.blackout.rendering.font;

public class CharInfo {
   public int x;
   public int y;
   public int width;
   public int height;
   public float tx;
   public float ty;
   public float tw;
   public float th;

   public CharInfo(int x, int y, int width, int height) {
      this.x = x;
      this.y = y;
      this.width = width;
      this.height = height;
   }

   public void calcTexCoords(int width, int height) {
      this.tx = (float)this.x / (float)width;
      this.ty = (float)this.y / (float)height;
      this.tw = (float)this.width / (float)width;
      this.th = (float)this.height / (float)height;
   }
}
