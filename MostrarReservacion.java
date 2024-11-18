import javax.swing.*;
import java.awt.*;
import java.sql.*;
import javax.swing.table.DefaultTableModel;
import java.util.Vector;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class MostrarReservacion extends JFrame {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/reservas_villa_mon_coeur"; // base de datos
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "myrf0424";

    public MostrarReservacion() {
        setTitle("Ver Reservaciones");
        setSize(800, 400); // Ajusté el tamaño para más comodidad
        setLocationRelativeTo(null); // Centra la ventana

        // Crear el panel y la tabla
        JPanel panel = new JPanel(new BorderLayout());
        JTable table = new JTable();
        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);
        add(panel);

        // Mostrar las reservaciones en la tabla
        mostrarReservaciones(table);

        // Agregar el listener para cerrar la ventana y abrir el menú
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                new Menu(); // Crear una nueva instancia de Menu
            }
        });
    }

    private void mostrarReservaciones(JTable table) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // Llamar al procedimiento almacenado 'Mostrar_reserva_infovuelo'
            CallableStatement stmt = conn.prepareCall("{CALL Mostrar_reserva_infovuelo()}");

            // Ejecutar la llamada
            ResultSet resultSet = stmt.executeQuery();

            // Establecer el modelo de la tabla con los datos obtenidos
            table.setModel(buildTableModel(resultSet));
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al cargar las reservaciones");
        }
    }

    // Método para construir el modelo de la tabla a partir de un ResultSet
    public static DefaultTableModel buildTableModel(ResultSet rs) throws SQLException {
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        // Obtener los nombres de las columnas
        Vector<String> columnNames = new Vector<>(columnCount);
        for (int column = 1; column <= columnCount; column++) {
            columnNames.add(metaData.getColumnName(column));
        }

        // Obtener los datos de las filas
        Vector<Vector<Object>> data = new Vector<>();
        while (rs.next()) {
            Vector<Object> vector = new Vector<>(columnCount);
            for (int columnIndex = 1; columnIndex <= columnCount; columnIndex++) {
                vector.add(rs.getObject(columnIndex));
            }
            data.add(vector);
        }

        // Crear y retornar el modelo de la tabla
        return new DefaultTableModel(data, columnNames);
    }

    // Método principal para ejecutar la aplicación
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MostrarReservacion mostrarReservacion = new MostrarReservacion(); // Usar el nombre correcto de la clase
            mostrarReservacion.setVisible(true); // Mostrar la ventana
        });
    }
}
