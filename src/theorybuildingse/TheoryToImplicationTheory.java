package theorybuildingse;

import java.util.*;

public class TheoryToImplicationTheory {

    public static void main(String[] args) {
        // Ejemplo de entrada: Teoría lógica representada como una lista de implicaciones
        List<String> theory = Arrays.asList("A=>B", "B=>C", "~C=>D");
        //List<String> theory = Arrays.asList("Q=>R", "P=>R", "S=>Q", "~S=>~P");

        // Generar la matriz de adyacencia
        Map<String, Set<String>> implicationGraph = generateImplicationGraph(theory);

        // Crear una lista de nodos únicos
        List<String> nodes = new ArrayList<>(implicationGraph.keySet());
        Collections.sort(nodes);

        // Generar la matriz de adyacencia
        int[][] adjacencyMatrix = generateAdjacencyMatrix(implicationGraph, nodes);

        // Imprimir resultados
        System.out.println("Nodos: " + nodes);
        System.out.println("Matriz de adyacencia:");
        printMatrix(adjacencyMatrix, nodes);
    }
    public static List<String> getNodesFromTheory(List<String> theory){
        // Generar la matriz de adyacencia
        Map<String, Set<String>> implicationGraph = generateImplicationGraph(theory);

        // Crear una lista de nodos únicos
        List<String> nodes = new ArrayList<>(implicationGraph.keySet());
        Collections.sort(nodes);
        
        return nodes;
    }
    public static int[][] getImplicationTheoryFromTheory(List<String> theory){
        // Generar la matriz de adyacencia
        Map<String, Set<String>> implicationGraph = generateImplicationGraph(theory);

        // Crear una lista de nodos únicos
        List<String> nodes = new ArrayList<>(implicationGraph.keySet());
        Collections.sort(nodes);

        // Generar la matriz de adyacencia
        int[][] adjacencyMatrix = generateAdjacencyMatrix(implicationGraph, nodes);
        
        return adjacencyMatrix;
    }

    // Genera el grafo de implicación a partir de las premisas y sus transposiciones
    private static Map<String, Set<String>> generateImplicationGraph(List<String> theory) {
        Map<String, Set<String>> graph = new HashMap<>();

        for (String implication : theory) {
            // Dividir la implicación en antecedentes y consecuentes
            String[] parts = implication.split("=>");
            if (parts.length != 2) continue;

            String antecedent = normalize(parts[0].trim());
            String consequent = normalize(parts[1].trim());

            // Añadir la implicación directa
            graph.computeIfAbsent(antecedent, k -> new HashSet<>()).add(consequent);

            // Añadir la transposición lógica (¬B -> ¬A)
            String negAntecedent = normalize("~" + antecedent);
            String negConsequent = normalize("~" + consequent);
            graph.computeIfAbsent(negConsequent, k -> new HashSet<>()).add(negAntecedent);

            // Asegurar que los nodos estén en el grafo aunque no tengan conexiones salientes
            graph.putIfAbsent(antecedent, new HashSet<>());
            graph.putIfAbsent(consequent, new HashSet<>());
            graph.putIfAbsent(negAntecedent, new HashSet<>());
            graph.putIfAbsent(negConsequent, new HashSet<>());
        }

        return graph;
    }

    // Normaliza un literal eliminando dobles negaciones
    private static String normalize(String literal) {
        while (literal.contains("~~")) {
            literal = literal.replace("~~", "");
        }
        return literal;
    }

    // Genera la matriz de adyacencia a partir del grafo de implicaciones
    private static int[][] generateAdjacencyMatrix(Map<String, Set<String>> graph, List<String> nodes) {
        int size = nodes.size();
        int[][] matrix = new int[size][size];
        Map<String, Integer> nodeIndex = new HashMap<>();

        // Mapear cada nodo a su índice en la lista
        for (int i = 0; i < size; i++) {
            nodeIndex.put(nodes.get(i), i);
        }

        // Llenar la matriz con las conexiones del grafo
        for (Map.Entry<String, Set<String>> entry : graph.entrySet()) {
            String from = entry.getKey();
            int fromIndex = nodeIndex.getOrDefault(from, -1);

            if (fromIndex == -1) continue;

            for (String to : entry.getValue()) {
                int toIndex = nodeIndex.getOrDefault(to, -1);
                if (toIndex != -1) {
                    matrix[fromIndex][toIndex] = 1;
                }
            }
        }

        return matrix;
    }

    // Imprime la matriz de adyacencia con etiquetas de nodos
    public static void printMatrix(int[][] matrix, List<String> nodes) {
        // Imprimir encabezados
        System.out.print("\t");
        for (String node : nodes) {
            System.out.print(node + "\t");
        }
        System.out.println();

        // Imprimir filas de la matriz
        for (int i = 0; i < matrix.length; i++) {
            System.out.print(nodes.get(i) + "\t");
            for (int j = 0; j < matrix[i].length; j++) {
                System.out.print(matrix[i][j] + "\t");
            }
            System.out.println();
        }
    }
}
