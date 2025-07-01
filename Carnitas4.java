import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Carnitas4 extends JFrame {

    private JTabbedPane tabbedPane;
    private int orderCounter = 1;

    private static final Color NARANJA_FONDO = new Color(255, 218, 185);
    private static final Color NARANJA_BOTON = new Color(243, 156, 18);

    public Carnitas4() {
        setTitle("Carnitas 'El Buen Gusto'");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);

        UIManager.put("TabbedPane.font", new Font("Arial", Font.BOLD, 17));
        UIManager.put("OptionPane.messageFont", new Font("Arial", Font.PLAIN, 17));
        UIManager.put("ComboBox.font", new Font("Arial", Font.PLAIN, 17));
        UIManager.put("Label.font", new Font("Arial", Font.PLAIN, 17));

        UIManager.put("OptionPane.yesButtonText", "Sí");
        UIManager.put("OptionPane.noButtonText", "No");
        UIManager.put("OptionPane.cancelButtonText", "Cancelar");
        UIManager.put("OptionPane.okButtonText", "Aceptar");


        tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(NARANJA_FONDO);

        addNewOrderTab();

        add(tabbedPane);
    }

    public void addNewOrderTab() {
        String title = "Orden " + orderCounter++;
        PedidoPanel newOrderPanel = new PedidoPanel(this);
        tabbedPane.addTab(title, newOrderPanel);
        tabbedPane.setSelectedComponent(newOrderPanel);
    }

    public void closeCurrentOrderTab() {
        if (tabbedPane.getTabCount() > 0) {
            int selectedIndex = tabbedPane.getSelectedIndex();
            tabbedPane.remove(selectedIndex);
        }
        if (tabbedPane.getTabCount() == 0) {
            addNewOrderTab();
        }
    }

    class PedidoPanel extends JPanel {

        private JTextArea ticketArea;
        private JLabel totalLabel;
        private List<Map<String, Object>> pedidoActual;
        private final Carnitas4 parentFrame;

        private final String[] tiposDeCarne = {
            "Maciza", "Cuero", "Costilla", "Tripa", "Buche", "Lengua",
            "Cabeza", "Oreja", "Chamorro", "Surtida", "Maciza con Cuero"
        };
        private static final double PRECIO_KILO_CARNE = 180.00;
        private static final double PRECIO_KILO_TORTILLA = 20.00;
        private static final double PRECIO_TACO = 18.00;
        private static final double PRECIO_QUESADILLA = 10.00;
        private static final double PRECIO_REFRESCO_CHICO = 25.00;
        private static final double PRECIO_REFRESCO_GRANDE = 45.00;
        private static final double PRECIO_VASO = 1.00;

        public PedidoPanel(Carnitas4 parentFrame) {
            this.parentFrame = parentFrame;
            this.pedidoActual = new ArrayList<>();
            setLayout(new BorderLayout(10, 10));
            setBackground(NARANJA_FONDO);
            setBorder(new EmptyBorder(10, 10, 10, 10));

            add(crearPanelDeProductos(), BorderLayout.CENTER);
            add(crearPanelDePedido(), BorderLayout.EAST);
            add(crearPanelDeAcciones(), BorderLayout.SOUTH);
        }

        private JPanel crearPanelDeProductos() {
            JPanel panel = new JPanel(new GridLayout(3, 3, 10, 10));
            panel.setOpaque(false);

            crearBotonProducto(panel, "TACOS", e -> manejarPedidoTaco());
            crearBotonProducto(panel, "QUESADILLAS", e -> manejarPedidoSimple("Quesadilla", PRECIO_QUESADILLA));
            crearBotonProducto(panel, "REFRESCOS", e -> manejarPedidoRefresco());
            crearBotonProducto(panel, "VASOS", e -> manejarPedidoSimple("Vaso", PRECIO_VASO));
            crearBotonProducto(panel, "CARNE POR PESO", e -> manejarPedidoCarne());
            crearBotonProducto(panel, "TORTILLAS", e -> manejarPedidoTortillas());

            return panel;
        }

        private JPanel crearPanelDePedido() {
            JPanel panel = new JPanel(new BorderLayout(15, 15));

            TitledBorder titledBorder = BorderFactory.createTitledBorder("PEDIDO ACTUAL");
            Font titleFont = new Font("Arial", Font.BOLD, 20);
            titledBorder.setTitleFont(titleFont);
            panel.setBorder(titledBorder);

            panel.setPreferredSize(new Dimension(500, 0));
            panel.setOpaque(false);

            ticketArea = new JTextArea();
            ticketArea.setEditable(false);
            ticketArea.setFont(new Font("Monospaced", Font.PLAIN, 17));
            JScrollPane scrollPane = new JScrollPane(ticketArea);

            totalLabel = new JLabel("TOTAL: $0.00", SwingConstants.CENTER);
            totalLabel.setFont(new Font("Arial", Font.BOLD, 24));
            totalLabel.setForeground(new Color(0, 102, 0));

            panel.add(scrollPane, BorderLayout.CENTER);
            panel.add(totalLabel, BorderLayout.SOUTH);
            return panel;
        }

        private JPanel crearPanelDeAcciones() {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
            panel.setOpaque(false);

            JButton finalizarBtn = new JButton("FINALIZAR PEDIDO");
            personalizarBotonAccion(finalizarBtn);
            finalizarBtn.addActionListener(e -> finalizarPedido());

            JButton eliminarBtn = new JButton("ELIMINAR ARTICULO");
            personalizarBotonAccion(eliminarBtn);
            eliminarBtn.addActionListener(e -> eliminarItemDelPedido());

            JButton nuevoPedidoBtn = new JButton("NUEVO PEDIDO");
            personalizarBotonAccion(nuevoPedidoBtn);
            nuevoPedidoBtn.addActionListener(e -> parentFrame.addNewOrderTab());

            panel.add(finalizarBtn);
            panel.add(eliminarBtn);
            panel.add(nuevoPedidoBtn);
            return panel;
        }

        private void manejarPedidoTortillas() {
            String[] opciones = {"Por Peso (kg)", "Por Cantidad ($)"};
            int eleccion = JOptionPane.showOptionDialog(this, "Vender tortillas:", "Tortillas",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[0]);

            if (eleccion == -1) return;

            if (eleccion == 0) {
                String[] pesos = {"1/4 kg", "1/2 kg", "1 kg"};
                String pesoSel = (String) JOptionPane.showInputDialog(this, "Seleccione el peso:", "Peso",
                    JOptionPane.QUESTION_MESSAGE, null, pesos, pesos[0]);
                if (pesoSel != null) {
                    double precio = 0;
                    if (pesoSel.equals("1/4 kg")) precio = PRECIO_KILO_TORTILLA * 0.25;
                    if (pesoSel.equals("1/2 kg")) precio = PRECIO_KILO_TORTILLA * 0.50;
                    if (pesoSel.equals("1 kg")) precio = PRECIO_KILO_TORTILLA * 1.0;
                    agregarItemAlPedido("Tortillas " + pesoSel, 1, precio);
                }
            } else {
                String montoStr = JOptionPane.showInputDialog(this, "Ingrese la cantidad en pesos ($) de tortillas", "Monto", JOptionPane.QUESTION_MESSAGE);
                try {
                    double monto = Double.parseDouble(montoStr);
                    if (monto > 0) {
                        agregarItemAlPedido(String.format("Tortillas ($%.2f)", monto), 1, monto);
                    }
                } catch (NumberFormatException | NullPointerException ignored) {}
            }
        }

        private void manejarPedidoCarne() {
            String[] opciones = {"Por Peso (kg)", "Por Cantidad ($)"};
            int eleccion = JOptionPane.showOptionDialog(this, "Comprar carne:", "Carne",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[0]);

            if (eleccion == -1) return;

            String tipoCarne = (String) JOptionPane.showInputDialog(
                this, "Seleccione el tipo de carne:", "Tipo de Carne",
                JOptionPane.QUESTION_MESSAGE, null, tiposDeCarne, tiposDeCarne[0]);

            if (tipoCarne == null) return;

            if (eleccion == 0) {
                String[] pesos = {"1/4 kg", "1/2 kg", "3/4 kg", "1 kg"};
                String pesoSeleccionado = (String) JOptionPane.showInputDialog(this, "Seleccione el peso:", "Peso",
                    JOptionPane.QUESTION_MESSAGE, null, pesos, pesos[0]);

                if (pesoSeleccionado != null) {
                    double precio = 0;
                    if (pesoSeleccionado.equals("1/4 kg")) precio = PRECIO_KILO_CARNE * 0.25;
                    if (pesoSeleccionado.equals("1/2 kg")) precio = PRECIO_KILO_CARNE * 0.50;
                    if (pesoSeleccionado.equals("3/4 kg")) precio = PRECIO_KILO_CARNE * 0.75;
                    if (pesoSeleccionado.equals("1 kg")) precio = PRECIO_KILO_CARNE * 1.0;
                    agregarItemAlPedido(pesoSeleccionado + " de " + tipoCarne, 1, precio);
                }
            } else {
                String montoStr = JOptionPane.showInputDialog(this, "Ingrese la cantidad en pesos ($) de " + tipoCarne, "Monto", JOptionPane.QUESTION_MESSAGE);
                try {
                    double monto = Double.parseDouble(montoStr);
                    if (monto > 0) {
                        agregarItemAlPedido(String.format("$%.2f de %s", monto, tipoCarne), 1, monto);
                    }
                } catch (NumberFormatException | NullPointerException ex) {}
            }
        }

        private void manejarPedidoTaco() {
            String tipoCarne = (String) JOptionPane.showInputDialog(
                this, "Seleccione el tipo de carne:", "Tipo de Taco",
                JOptionPane.QUESTION_MESSAGE, null, tiposDeCarne, tiposDeCarne[0]);

            if (tipoCarne != null) {
                String cantidadStr = JOptionPane.showInputDialog(this, "¿Cuántos tacos de " + tipoCarne + "?", "Cantidad", JOptionPane.QUESTION_MESSAGE);
                try {
                    int cantidad = Integer.parseInt(cantidadStr);
                    if (cantidad > 0) {
                        agregarItemAlPedido("Taco de " + tipoCarne, cantidad, PRECIO_TACO);
                    }
                } catch (NumberFormatException ex) {}
            }
        }

        private void manejarPedidoSimple(String nombreProducto, double precioUnitario) {
            String cantidadStr = JOptionPane.showInputDialog(this, "¿Cuántas unidades de " + nombreProducto + "?", "Cantidad", JOptionPane.QUESTION_MESSAGE);
            try {
                int cantidad = Integer.parseInt(cantidadStr);
                if (cantidad > 0) {
                    agregarItemAlPedido(nombreProducto, cantidad, precioUnitario);
                }
            } catch (NumberFormatException ex) {}
        }

        private void manejarPedidoRefresco() {
            String[] opciones = {"Refresco Chico ($" + PRECIO_REFRESCO_CHICO + ")", "Refresco Grande ($" + PRECIO_REFRESCO_GRANDE + ")"};
            int eleccion = JOptionPane.showOptionDialog(this, "Elija el tamaño del refresco", "Refrescos",
                JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, opciones, opciones[0]);

            if (eleccion == 0) {
                manejarPedidoSimple("Refresco Chico", PRECIO_REFRESCO_CHICO);
            } else if (eleccion == 1) {
                manejarPedidoSimple("Refresco Grande", PRECIO_REFRESCO_GRANDE);
            }
        }

        private void finalizarPedido() {
            if (pedidoActual.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No hay productos en el pedido actual.", "Pedido Vacío", JOptionPane.WARNING_MESSAGE);
                return;
            }
            int confirm = JOptionPane.showConfirmDialog(this,
                "¿Desea finalizar y cerrar esta orden?\nTotal: " + totalLabel.getText(),
                "Confirmar Finalización", JOptionPane.YES_NO_OPTION);

            if (confirm == JOptionPane.YES_OPTION) {
                parentFrame.closeCurrentOrderTab();
            }
        }

        private void eliminarItemDelPedido() {
            if (pedidoActual.isEmpty()) {
                JOptionPane.showMessageDialog(this, "No hay productos para eliminar.", "Pedido Vacío", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String[] itemsParaMostrar = new String[pedidoActual.size()];
            for (int i = 0; i < pedidoActual.size(); i++) {
                Map<String, Object> item = pedidoActual.get(i);
                itemsParaMostrar[i] = String.format("%d x %s ($%.2f)",
                    (int) item.get("cantidad"), (String) item.get("nombre"), (double) item.get("subtotal"));
            }

            Object itemSel = JOptionPane.showInputDialog(this, "Seleccione el elemento a eliminar:",
                "Eliminar Elemento", JOptionPane.QUESTION_MESSAGE, null, itemsParaMostrar, itemsParaMostrar[0]);

            if (itemSel != null) {
                for (int i = 0; i < itemsParaMostrar.length; i++) {
                    if (itemsParaMostrar[i].equals(itemSel)) {
                        pedidoActual.remove(i);
                        break;
                    }
                }
                actualizarTicket();
            }
        }

        private void agregarItemAlPedido(String nombre, int cantidad, double precioUnitario) {
            Map<String, Object> item = new HashMap<>();
            item.put("nombre", nombre);
            item.put("cantidad", cantidad);
            item.put("subtotal", cantidad * precioUnitario);
            pedidoActual.add(item);
            actualizarTicket();
        }

        private void actualizarTicket() {
            StringBuilder sb = new StringBuilder();
            double total = 0.0;
            sb.append(String.format("%-30s %-5s %-10s\n", "Producto", "Cant.", "Subtotal"));
            sb.append("----------------------------------------------\n");
            for (Map<String, Object> item : pedidoActual) {
                sb.append(String.format("%-30.30s %-5d $%-9.2f\n",
                    item.get("nombre"), item.get("cantidad"), item.get("subtotal")));
                total += (double) item.get("subtotal");
            }
            ticketArea.setText(sb.toString());
            totalLabel.setText(String.format("TOTAL: $%.2f", total));
        }

        private void personalizarBotonAccion(JButton boton) {
            boton.setFont(new Font("Arial", Font.BOLD, 20));
            boton.setBackground(NARANJA_BOTON);
            boton.setForeground(Color.BLACK);
        }

        private void crearBotonProducto(JPanel panel, String nombre, java.awt.event.ActionListener action) {
            JButton boton = new JButton(nombre);
            boton.setFont(new Font("Arial", Font.BOLD, 28));
            boton.setBackground(NARANJA_BOTON);
            boton.setForeground(Color.BLACK);
            boton.addActionListener(action);
            panel.add(boton);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new Carnitas4().setVisible(true));
    }
}