/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package theorybuildingse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Sergio
 */
public class ArregloCiclos {

    private final ArrayList<List<String>> lOriginalNodes = new ArrayList<>();
    private final ArrayList<int[][]> lMatrizAdj = new ArrayList<>();

    private int vueltaArreglos = 0;

    public ArregloCiclos(int[][] matrizAdj, List<String> nodes) {
        this.lOriginalNodes.add(nodes);
        this.lMatrizAdj.add(matrizAdj);
        while (CycleDetectionDFSIterative.hasCycle(lMatrizAdj.get(vueltaArreglos))) {
//            System.out.println("-- VUELTA ciclo -- ");
            TarjanCondensedGraph g = new TarjanCondensedGraph(lMatrizAdj.get(vueltaArreglos));
            for (Set<Integer> SCC : g.findSCCs()) {
                List<Integer> lSCC = new ArrayList<>(SCC);
                Collections.sort(lSCC);
                if (SCC.size() > 1) {
//                    System.out.println("Encuentra ciclo: " + SCC);
                    lMatrizAdj.add(fusionarNodosCiclo(lMatrizAdj.get(vueltaArreglos), lSCC));

                    vueltaArreglos++;
                    break;
                }
            }
        }
    }

    public List<String> getNodes() {
        return lOriginalNodes.get(lOriginalNodes.size() - 1);
    }

    public int[][] getMatrizFusionCiclos() {
        return lMatrizAdj.get(lMatrizAdj.size() - 1);
    }

    public int[][] restaurarCiclos(int[][] matrizAdj) throws Exception {
        for (int i = lOriginalNodes.size() - 1; i > 0; i--) {
            vueltaArreglos = i;

            List<String> lNodes = lOriginalNodes.get(i);
            int n = lNodes.size() - 1;
//            System.out.println("-- VUELTA ciclo Restaura -- :: " + vueltaArreglos);
//            for (String lNode : lNodes) {
//                System.out.println("Nodo: " + lNode);
//            }
            matrizAdj = agregarNodosCiclo(matrizAdj, n, lNodes.get(n).split("/").length - 1);
            //vueltaArreglos--;
        }
        vueltaArreglos = 0;
        return matrizAdj;
    }

    private int[][] fusionarNodosCiclo(int[][] matriz, List<Integer> nodosSeleccionados) {
        int n = matriz.length;
        int nuevoTamano = n - nodosSeleccionados.size() + 1; // Tamaño de la nueva matriz
        int nuevoNodoIndex = nuevoTamano - 1;

//        System.out.println("n:: " + n);
//        System.out.println("nuevoTamano:: " + nuevoTamano);
//        System.out.println("nuevoNodoIndex:: " + nuevoNodoIndex);
        // Crear una nueva matriz de adyacencia
        int[][] nuevaMatriz = new int[nuevoTamano][nuevoTamano];

        // Mapeo de índices antiguos a nuevos
        Map<Integer, Integer> mapeo = new HashMap<>();
        int nuevoIndice = 0;
        lOriginalNodes.add(new ArrayList<>());
        for (int i = 0; i < n; i++) {
            if (!nodosSeleccionados.contains(i)) {
                mapeo.put(i, nuevoIndice++);
                // Añado los nombre de los nodos no seleccionados
                lOriginalNodes.get(vueltaArreglos + 1).add(lOriginalNodes.get(vueltaArreglos).get(i));
            }
        }
        // Añado el nombre del nodo comun
        String nodosSelectName = "";
        for (Integer nodoSeleccionado : nodosSeleccionados) {
//            System.out.println("lOriginalNodes.size:: " + lOriginalNodes.size());
//            System.out.println("vueltaArreglos:: " + vueltaArreglos);
//            System.out.println("lOriginalNodes.get(vueltaArreglos):: " + lOriginalNodes.get(vueltaArreglos).size());
//            System.out.println("nodoSeleccionado:: " + nodoSeleccionado);
            nodosSelectName += lOriginalNodes.get(vueltaArreglos).get(nodoSeleccionado) + "/";
        }
        lOriginalNodes.get(vueltaArreglos + 1).add(nodosSelectName.substring(0, nodosSelectName.length() - 1));

        mapeo.put(-1, nuevoNodoIndex); // -1 representa el nuevo nodo combinado

        // Llenar la nueva matriz
        for (int i = 0; i < n; i++) {
            if (nodosSeleccionados.contains(i)) {
                continue;
            }

            for (int j = 0; j < n; j++) {
                if (nodosSeleccionados.contains(j)) {
                    // Conexión hacia el nuevo nodo
                    nuevaMatriz[mapeo.get(i)][nuevoNodoIndex] += matriz[i][j];
                } else {
                    // Conexión entre nodos no seleccionados
                    nuevaMatriz[mapeo.get(i)][mapeo.get(j)] = matriz[i][j];
                }
            }
        }

        // Completar las conexiones del nuevo nodo combinado
        for (int seleccionado : nodosSeleccionados) {
            for (int i = 0; i < n; i++) {
                if (!nodosSeleccionados.contains(i)) {
                    nuevaMatriz[nuevoNodoIndex][mapeo.get(i)] += matriz[seleccionado][i];
                }
            }
        }

        return nuevaMatriz;
    }

    private int relacionNodos(int nuevoNodo, int posicion) throws Exception {
        List<String> nodosIniciales = lOriginalNodes.get(vueltaArreglos - 1);

//        System.out.println("lOriginalNodes size:: " + lOriginalNodes.size());
//        System.out.println("vueltaArreglos:: " + vueltaArreglos);
//        System.out.println("lOriginalNodes.get(vueltaArreglos) size:: " + lOriginalNodes.get(vueltaArreglos).size());
//        System.out.println("nuevoNodo:: " + nuevoNodo);
//        System.out.println("posicion:: " + posicion);
        if (lOriginalNodes.get(vueltaArreglos).size() <= nuevoNodo) {
            nuevoNodo = lOriginalNodes.get(vueltaArreglos).size() - 1;
        }
//        System.out.println("nuevoNodo String:: " + lOriginalNodes.get(vueltaArreglos).get(nuevoNodo));
//        System.out.println("split length:: " + lOriginalNodes.get(vueltaArreglos).get(nuevoNodo).split("/").length);

        String nodoBuscado = lOriginalNodes.get(vueltaArreglos).get(nuevoNodo).split("/")[posicion];

//        System.out.println("nodoBuscado:: " + nodoBuscado);
        for (int i = 0; i < nodosIniciales.size(); i++) {
            // System.out.println("nodosIniciales:: " + nodosIniciales.get(i));
            String[] subNodos = nodosIniciales.get(i).split("/");
            if (nodoBuscado.equals(nodosIniciales.get(i))) {
                return i;
            } else if (subNodos.length > 1) {
                for (String subNodo : subNodos) {
                    if (nodoBuscado.equals(subNodo)) {
                        return i;
                    }
                }
            }
        }
        throw new Exception("Error [ArregloCiclos.realacionNodos()] :: Nodo no encontrado.");
    }

    private int[][] agregarNodosCiclo(int[][] matriz, int nodoSeleccionado, int NumNuevosNodos) throws Exception {
        int n = matriz.length; // Tamaño original de la matriz sin el ciclo expandido
        int nuevoTamano = n + NumNuevosNodos;

        int[][] nuevaMatriz = new int[nuevoTamano][nuevoTamano];

        // 1\) Copiar la matriz original a la nueva (re-mapeando índices al nivel anterior)
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (matriz[i][j] > 0) {
                    nuevaMatriz[relacionNodos(i, 0)][relacionNodos(j, 0)] = matriz[i][j];
                }
            }
        }

        // 2\) Índices de los nuevos nodos en la matriz restaurada
        int primerNuevoNodoMatriz = n;                 // índice del primer NUEVO nodo en la matriz
        int ultimoNuevoNodoMatriz = nuevoTamano - 1;   // índice del último NUEVO nodo en la matriz

        // 3\) Número de subnodos del nodo combinado en este nivel
        //    Ojo: aquí usamos *nodoSeleccionado* como índice en lOriginalNodes
        String combinado = lOriginalNodes.get(vueltaArreglos)
                .get(nodoSeleccionado);
        String[] partes = combinado.split("/");
        int len = partes.length - 1; // última posición de subnodo

        // 4\) Conexión del primer nodo del ciclo hacia el nodo seleccionado (en la matriz nueva)
        //    - `primerNuevoNodoMatriz` es índice de matriz
        //    - `1` es la posición del segundo subnodo dentro del combinado (el primero es el que ya estaba)
        nuevaMatriz[relacionNodos(primerNuevoNodoMatriz, 1)]
                [relacionNodos(nodoSeleccionado, 0)] = 1;

        // 5\) Conexión del nodo seleccionado hacia el último nodo del ciclo
        nuevaMatriz[relacionNodos(nodoSeleccionado, 0)]
                [relacionNodos(ultimoNuevoNodoMatriz, len)] = 1;

        // 6\) Conectar los nuevos nodos entre sí para formar el ciclo
        //    Recorremos las posiciones intermedias del nombre combinado
        for (int i = 0; i < NumNuevosNodos - 1; i++) {
            // Todos los subnodos están dentro del mismo combinado (`nodoSeleccionado`)
            int nodoActual = relacionNodos(nodoSeleccionado, i + 1);
            int nodoSiguiente = relacionNodos(nodoSeleccionado, i + 2);

            // Conexión del nodo actual al siguiente nodo en el ciclo
            nuevaMatriz[nodoSiguiente][nodoActual] = 1;
        }

        return nuevaMatriz;
    }

    private int[][] agregarNodosCiclo_OLD(int[][] matriz, int nodoSeleccionado, int NumNuevosNodos) throws Exception {
        int n = matriz.length; // Tamaño original de la matriz
        int nuevoTamano = n + NumNuevosNodos;

        // Crear la nueva matriz de adyacencia
        int[][] nuevaMatriz = new int[nuevoTamano][nuevoTamano];

        // Copiar la matriz original a la nueva matriz
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (matriz[i][j] > 0) {
//                    System.out.println(" -> Reconstruye Nodos originales :: " + i + " - " + j + " .. "
//                            + lOriginalNodes.get(0).get(i) + " - " + lOriginalNodes.get(0).get(j) + " :: "
//                            + relacionNodos(i, 0) + " - " + relacionNodos(j, 0) + " .. "
//                            + lOriginalNodes.get(0).get(relacionNodos(i, 0)) + " - " + lOriginalNodes.get(0).get(relacionNodos(j, 0)));

                    nuevaMatriz[relacionNodos(i, 0)][relacionNodos(j, 0)] = matriz[i][j];
//                    System.out.println("\n-- MATRIZ Parcial -- ");
//                    TheoryToImplicationTheory.printMatrix(nuevaMatriz, lOriginalNodes.get(0));
                }
            }
        }

        // Índices de los nuevos nodos
        int primerNuevoNodo = n;

//        System.out.println(" -> Reconstruye Nodos ciclo ini :: " + primerNuevoNodo + " - " + nodoSeleccionado + " .. "
//                + lOriginalNodes.get(0).get(primerNuevoNodo) + " - " + lOriginalNodes.get(0).get(nodoSeleccionado));
        // Conexión del primer nodo al nodo seleccionado
        nuevaMatriz[relacionNodos(primerNuevoNodo, 1)][relacionNodos(nodoSeleccionado, 0)] = 1;

//        System.out.println("\n-- MATRIZ Parcial -- ");
//        TheoryToImplicationTheory.printMatrix(nuevaMatriz, lOriginalNodes.get(0));
//
//        System.out.println(" -> Reconstruye Nodos ciclo fin :: " + nodoSeleccionado + " - " + (nuevoTamano - 1) + " .. "
//                + lOriginalNodes.get(0).get(nodoSeleccionado) + " - " + lOriginalNodes.get(0).get((nuevoTamano - 1)));
        // Conexión del nodo seleccionado al ultimo nodo
        int len = lOriginalNodes.get(vueltaArreglos).get(primerNuevoNodo - 1).split("/").length - 1;
        nuevaMatriz[relacionNodos(nodoSeleccionado, 0)][relacionNodos(nuevoTamano - 1, len)] = 1;
//
//        System.out.println("\n-- MATRIZ Parcial -- fin ");
//        TheoryToImplicationTheory.printMatrix(nuevaMatriz, lOriginalNodes.get(0));

        // Conectar los nuevos nodos formando un ciclo con el nodo seleccionado
        for (int i = 0; i < NumNuevosNodos - 1; i++) {
            int nodoActual = relacionNodos(primerNuevoNodo - 1, i + 1);//+ i, i + 1);
            int nodoSiguiente = relacionNodos(primerNuevoNodo - 1, i + 2);//nodoActual + 1, i + 2);

//            System.out.println(" -> Reconstruye Nodos ciclo medio :: " + nodoActual + " - " + nodoSiguiente + " .. "
//                    + lOriginalNodes.get(0).get(nodoActual) + " - " + lOriginalNodes.get(0).get(nodoSiguiente));
            // Conexión del nodo actual al siguiente nodo en el ciclo
            nuevaMatriz[nodoSiguiente][nodoActual] = 1;
//            System.out.println("\n-- MATRIZ Parcial -- ");
//            TheoryToImplicationTheory.printMatrix(nuevaMatriz, lOriginalNodes.get(0));
        }

        return nuevaMatriz;
    }

    public static void main(String[] args) {
        int[][] matrix = new int[10][10];
        matrix[0][7] = 1;
        matrix[2][1] = 1;
        matrix[1][0] = 1;
        matrix[1][3] = 1;
        matrix[3][4] = 1;
        matrix[5][0] = 1;
        matrix[1][6] = 1;
        matrix[7][8] = 1;
        matrix[8][2] = 1;

        matrix[6][9] = 1;
//        TarjanCondensedGraph g1 = new TarjanCondensedGraph(matrix);
//        ArrayList<Set<Integer>> SCCs = g1.findSCCs();  // Obtengo los componentes fuertemente conexos
//
//        g1.printListSetNodes();
        List<String> nodes = new ArrayList<>();
        nodes.add("A");
        nodes.add("B");
        nodes.add("C");
        nodes.add("D");
        nodes.add("E");
        nodes.add("F");
        nodes.add("G");
        nodes.add("H");
        nodes.add("I");
        nodes.add("J");

        System.out.println("\n-- MATRIZ ORIGINAL --");

        TheoryToImplicationTheory.printMatrix(matrix, nodes);

        MatrixToLatex.toLatex(matrix);

        dibujaGrafos DG = new dibujaGrafos();
        DG.dibujar(matrix, nodes);

        ArregloCiclos AR = new ArregloCiclos(matrix, nodes);

        int[][] matrix1 = AR.getMatrizFusionCiclos();
        List<String> nodes1 = AR.getNodes();

        System.out.println("\n-- MATRIZ SIN CICLOS --");

        TheoryToImplicationTheory.printMatrix(matrix1, nodes1);

        MatrixToLatex.toLatex(matrix1);

        DG.redibujar(matrix1, nodes1);

        int[][] matrix2;
        try {

            System.out.println("\n-- MATRIZ RECOSTRUIDA --");
            matrix2 = AR.restaurarCiclos(matrix1);

            TheoryToImplicationTheory.printMatrix(matrix2, nodes);

            MatrixToLatex.toLatex(matrix2);

            DG.redibujar(matrix2, nodes);
        } catch (Exception ex) {
            System.out.println("Error:: " + ex.getMessage());
            ex.printStackTrace();
        }
        DG.close();
    }
}
