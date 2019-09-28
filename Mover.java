import java.awt.*;
import java.util.*;
public class Mover {
   public static void main(String [] args) {
      double xPos = 250;
      double yPos = 250;
      double mX = 300;
      double mY = 300;
      double direction = 0;
      DrawingPanel canvas = new DrawingPanel(500, 500);
      Graphics g = canvas.getGraphics();
      while (true) {
         g.setColor(new Color(255, 255, 255));
         g.fillRect(0, 0, 500, 500);
         g.setColor(new Color(0, 0, 0));
         g.drawLine((int)xPos+5, (int)yPos+5, (int)xPos, (int)yPos);
         g.drawLine((int)mX, (int)mY, (int)mX, (int)mY+5);
         xPos += Math.cos(direction) * 5;
         yPos += Math.sin(direction) * 5;
         try {
            Thread.sleep(50);
         }
         catch (Exception e){
         }
      }
   }
}
      