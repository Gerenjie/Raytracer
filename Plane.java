import java.awt.*;
import java.util.*;

public class Plane extends Material {
  
   double [] slopes = new double [3];
   double d;
   
   
   public Plane (double x, double y, double z, double constant, Color c, double ref, double tran, double refr) {
      slopes [0] = x;
      slopes [1] = y;
      slopes [2] = z;
      d = constant;
      color = c;
      reflectivity = 1-ref;
      transparency = tran;
      refractivity = refr;
      
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
   
   
   
      
   
