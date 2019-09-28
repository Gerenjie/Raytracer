public class Shader {
   double [] position = new double [3];
   double [] size = new double [3];
   public Shader (double xPos, double yPos, double zPos, double xLen, double yLen, double zLen) {
      position [0] = xPos;
      position [1] = yPos;
      position [2] = zPos;
      size [0] = xLen;
      size [1] = yLen;
      size [2] = zLen;
   }
   
   public boolean containsPoint (double [] point) {
      if (point[0] < position[0] || point[1] < position[1]  || point[2] < position[2] 
       || point[0] > position[0]+size[0] || point[1] > position[1]+size[1] || point[2] > position[2]+size[2]) {
         return false;
      }
      return true;
   }
   
}