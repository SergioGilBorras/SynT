/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package theorybuildingse;

/**
 *
 * @author Sergio
 */
public class MatrixToLatex {
    public static String toLatex(int[][] matrix) {
        StringBuilder latex = new StringBuilder();
        latex.append("\n\n$$\\tiny\nA_{\\mathcal{G}_T} = \\begin{bmatrix}\n");
        for (int[] matrix1 : matrix) {
            for (int j = 0; j < matrix1.length; j++) {
                latex.append(matrix1[j]);
                if (j < matrix1.length - 1) {
                    latex.append(" & ");
                }
            }
            latex.append(" \\\\\n");
        }
        latex.append("\\end{bmatrix}\n$$\n");
        return latex.toString();
    }

    public static void main(String[] args) {
        int[][] matrix = {
            {1, 2, 3},
            {4, 5, 6},
            {7, 8, 9}
        };
        System.out.println(toLatex(matrix));
    }
}
