package com.evertimes.datatype;

public class Matrix {
    public static Vector multiplyMatrixByVector(double[][] matrix, Vector vector){
        return new Vector(matrix[0][0]*vector.x+matrix[0][1]*vector.y+matrix[0][2]*vector.z,
                matrix[1][0]*vector.x+matrix[1][1]*vector.y+matrix[1][2]*vector.z,
                matrix[2][0]*vector.x+matrix[2][1]*vector.y+matrix[2][2]*vector.z);
    }
    public static double[][] constructRotationYMatrix(double radians){
        return new double[][]
                {{Math.cos(radians),0,Math.sin(radians)},
                {0,1,0},
                {-Math.sin(radians),0,Math.cos(radians)}};
    }
}

