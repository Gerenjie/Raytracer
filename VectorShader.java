public class VectorShader extends Shader {
   double [] vector = new double [3];
   
   public VectorShader (double xPos, double yPos, double zPos, double xLen, double yLen, double zLen, double vecX, double vecY, double vecZ) {
      super (xPos, yPos, zPos, xLen, yLen, zLen);
      /*position [0] = xPos;
      position [1] = yPos;
      position [2] = zPos;
      size [0] = xLen;
      size [1] = yLen;
      size [2] = zLen;*/
      vector [0] = vecX;
      vector [1] = vecY;
      vector [2] = vecZ;
   }
   
   public double [] getVector () {
      return vector;
   }
}