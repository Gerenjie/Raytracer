import java.awt.*;
import java.util.*;

public class Chessboard extends Plane {
   Color color2;
   double squareSize;
   double size;
   
   public Chessboard (double x, double y, double z, double constant, Color c, Color d, double ref, double tran, double refr, double s) {
      super (x, y, z, constant, c, ref, tran, refr);
      color2 = d;
      size = s;
   }
   
   public Color getColor(double [] intersectionPoint) {
      double xPos = Math.floor(intersectionPoint[0])/size-(int)(Math.floor(intersectionPoint[0])/size);
      double yPos = Math.floor(intersectionPoint[1])/size-(int)(Math.floor(intersectionPoint[1])/size);
      double zPos = Math.floor(intersectionPoint[2])/size/3.0-(int)(Math.floor(intersectionPoint[2])/size/3.0);
      boolean a =  (xPos>.5) || (xPos<0 && xPos > -.5);    
      boolean b =  (yPos>.5) || (yPos<0 && yPos > -.5); 
      boolean c =  (zPos>.5) || (zPos<0 && zPos > -.5); 
      if ((a && c) || (!a && !c)) { 
         return color;
      }
      else {
         return color2;
      }
   }
   
   public double [] getSlopes () {
      return slopes;
   }
   
   public double getD () {
      return d;
   }
   
   public double [] getNormal (double [] intersectionPoint) {
   
      return Vector.unit(slopes);
   }

   
}
   
   
   
      
   
