import java.awt.*;
import java.util.*;

public class Sphere extends Material {
  
   double [] center = new double [3];
   double radius;
   
   
   public Sphere (double x, double y, double z, double rad, Color c, double ref, double tran, double refr) {
      center [0] = x;
      center [1] = y;
      center [2] = z;
      radius = rad;
      color = c;
      reflectivity = 1-ref;
      transparency = tran;
      refractivity = refr;
   }
   public Sphere (double x, double y, double z, double rad) { 
      center[0] = x;
      center[1] = y;
      center[2] = z;
      radius = rad;
      color = Color.BLACK;
      reflectivity = 1;
      transparency = 0;
      refractivity = 1;
   }
   public double [] getCenter () {
      return center;
   }
   
   public double getRadius () {
      return radius;
   }
   

   public double [] getNormal (double [] intersectionPoint) {
      return Vector.unit(Vector.minus(intersectionPoint, center));
      
   }
   
}
   
   
   
      
   
