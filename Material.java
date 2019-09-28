import java.awt.*;
import java.util.*;

public abstract class Material {
   
   Color color;
   double reflectivity;
   double transparency;
   double refractivity;
   
   

   public Color getColor (double [] intersectionPoint) {
      return color;
   }
   
   public abstract double [] getNormal(double [] intersectionPoint);
   
   public double getReflectivity() {
      return reflectivity;
   }
   
   public double getTransparency () {
      return transparency;
   }
   public double getRefractivity() {
      return refractivity;
   }
   public void setTransparency (double newTran) {
      transparency = newTran;
   }
   public void setReflectivity (double newRef) {
      reflectivity = newRef;
   }
      
}