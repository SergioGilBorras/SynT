/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package theorybuildingse;

import java.util.List;
import java.util.Scanner;
import org.graphstream.graph.*;
import org.graphstream.graph.implementations.*;
import org.graphstream.ui.swing_viewer.SwingViewer;
import org.graphstream.ui.view.Viewer;

/**
 *
 * @author Sergio
 */
public class dibujaGrafos implements AutoCloseable {

    private final Graph graph;
    private final Scanner scanner;

    public dibujaGrafos() {
        System.setProperty("org.graphstream.ui", "swing"); // Requiere JavaFX para renderizado
        graph = new SingleGraph("Grafo dirigido");
        init();

        scanner = new Scanner(System.in);
    }

    private void init() {
        String currentDir = System.getProperty("user.dir");
        graph.setAttribute("ui.quality", true);
        graph.setAttribute("ui.antialias", true);
        graph.setAttribute("ui.stylesheet", "url('file:///" + currentDir + "/stylesCSS/stylesheet.css')");

        graph.setAttribute("ui.layout", "force");
        graph.setAttribute("layout.force.minRepulsion", 1500);
        graph.setAttribute("layout.force.prefDistance", 150);

    }

    private void demo() {
        // Agregar nodos
        graph.addNode("A");
        graph.addNode("B");
        graph.addNode("C");

        // Agregar aristas dirigidas
        graph.addEdge("AB", "A", "B", true);
        graph.addEdge("BC", "B", "C", true);
        graph.addEdge("CA", "C", "A", true);

        // Configuración de estilos
        graph.getNode("A").setAttribute("ui.label", "A");
        graph.getNode("B").setAttribute("ui.label", "B");
        graph.getNode("C").setAttribute("ui.label", "C");

        graph.display().setCloseFramePolicy(Viewer.CloseFramePolicy.EXIT); // Muestra el grafo

    }

    public void dibujar(int[][] matrizAdj, List<String> nodes) {
        for (String node : nodes) {
            graph.addNode(node).setAttribute("ui.label", node);
        }
        for (int i = 0; i < matrizAdj.length; i++) {
            for (int j = 0; j < matrizAdj[i].length; j++) {
                if (matrizAdj[i][j] > 0) {
                    graph.addEdge(nodes.get(i) + nodes.get(j), nodes.get(i), nodes.get(j), true);
                }
            }
        }
        Viewer viewer = graph.display();
        viewer.setCloseFramePolicy(Viewer.CloseFramePolicy.CLOSE_VIEWER); 
    }

    public Viewer dibujarView(int[][] matrizAdj, List<String> nodes) {
        for (String node : nodes) {
            graph.addNode(node).setAttribute("ui.label", node);
        }
        for (int i = 0; i < matrizAdj.length; i++) {
            for (int j = 0; j < matrizAdj[i].length; j++) {
                if (matrizAdj[i][j] > 0) {
                    graph.addEdge(nodes.get(i) + nodes.get(j), nodes.get(i), nodes.get(j), true);
                }
            }
        }
        Viewer viewer = new SwingViewer(graph, Viewer.ThreadingModel.GRAPH_IN_GUI_THREAD);

        viewer.addDefaultView(false);

        return viewer;
    }

    public void redibujar(int[][] matrizAdj, List<String> nodes) {
        esperaEnter();
        graph.clear();
        init();

        for (String node : nodes) {
            graph.addNode(node).setAttribute("ui.label", node);
        }
        for (int i = 0; i < matrizAdj.length; i++) {
            for (int j = 0; j < matrizAdj[i].length; j++) {
                if (matrizAdj[i][j] > 0) {
                    graph.addEdge(nodes.get(i) + nodes.get(j), nodes.get(i), nodes.get(j), true);
                }
            }
        }
    }

    private void esperaEnter() {
        System.out.println("Pulsa Enter para continuar ...");
        scanner.nextLine();
        System.out.println("Continuado...");
    }

    public static void main(String[] args) {
        dibujaGrafos DG = new dibujaGrafos();
        DG.demo();
    }

    @Override
    public void close() {
        if (scanner != null) {
            scanner.close();
            System.out.println("dibujaGrafos ha sido cerrado.");
        }
    }
}
