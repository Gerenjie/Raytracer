public class Light extends Material {
  double magnitude;
  double [] position = new double [3];
  double radius;
  
   public Light (double x, double y, double z, double mag, double rad) {
      position [0] = x;
      position [1] = y;
      position [2] = z;
      magnitude = mag;
      radius = rad;
   }

      
   public double [] getPosition () {
      return position;
   }
   public double getMagnitude () {
      return magnitude;
   }
   
   public void setMagnitude (double newMag) {
      magnitude = newMag;
   }     
   public double [] getNormal(double [] intersectionPoint) {
      return new double [] {0, 0, 0};
   }
   public double getRadius () {
      return radius;
   }
        
}