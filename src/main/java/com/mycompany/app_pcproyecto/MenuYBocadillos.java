/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JDialog.java to edit this template
 */
package com.mycompany.app_pcproyecto;

import static com.mycompany.app_pcproyecto.Principal.connection;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 *
 * @author salva
 */
public class MenuYBocadillos extends javax.swing.JDialog {

    private void insertarMenuOBocadillos(Connection connection) throws SQLException {
        String nombre = jTextNombre.getText();
        BigDecimal precio = new BigDecimal(jTextPrecio.getText());
        boolean bocadillo = jCheckBoxBocadillo.isSelected();
        boolean menu = jCheckBoxMenu.isSelected();
        boolean tostadas = jCheckBoxTostadas.isSelected();
        boolean tapas = jCheckBoxTapas.isSelected();

        try {
            // Verificar si el plato ya existe
            boolean platoNoExiste = true;
            if (menu) {
                platoNoExiste = platoNoExiste(connection, nombre, true, false, false, false);
            } else if (bocadillo) {
                platoNoExiste = platoNoExiste(connection, nombre, false, true, false, false);
            } else if (tostadas) {
                platoNoExiste = platoNoExiste(connection, nombre, false, false, true, false);
            } else if (tapas) {
                platoNoExiste = platoNoExiste(connection, nombre, false, false, false, true);
            }

            if (platoNoExiste) {
                String sql = "INSERT INTO menu_bocadillos (nombre, precio, menu, bocadillo, tostadas, tapas)"
                        + "VALUES (?, ?, ?, ?, ?, ?)";
                try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                    pstmt.setString(1, nombre);
                    pstmt.setBigDecimal(2, precio);
                    pstmt.setBoolean(3, menu);
                    pstmt.setBoolean(4, bocadillo);
                    pstmt.setBoolean(5, tostadas);
                    pstmt.setBoolean(6, tapas);
                    pstmt.executeUpdate();
                }
                limpiarCampos();
                JOptionPane.showMessageDialog(this, "Menu o bocadillo añadido correctamente.");

            } else {
                // Mostrar mensaje de error
                JOptionPane.showMessageDialog(this, "El menu o bocadillo ya existe en la base de datos.", "Error", JOptionPane.ERROR_MESSAGE);

            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            // Manejo del error, por ejemplo, mostrar un mensaje al usuario
            JOptionPane.showMessageDialog(this, "El plato no se ha guardado correctamente, volver a intentar.", "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            // Cerrar la conexión al finalizar
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private void cargarPlatos(Connection connection) throws SQLException {
        try {
            String sql = "SELECT id, nombre, precio, menu, bocadillo, tostadas, tapas FROM menu_bocadillos ORDER BY id";
            try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                try (ResultSet rs = pstmt.executeQuery()) {
                    jComboBoxActualizar.removeAllItems(); // Eliminar todos los elementos existentes
                    while (rs.next()) {
                        Integer id = rs.getInt("id");
                        String nombre = rs.getString("nombre");
                        BigDecimal precio = rs.getBigDecimal("precio");
                        boolean menu = rs.getBoolean("menu");
                        boolean bocadillo = rs.getBoolean("bocadillo");
                        boolean tostadas = rs.getBoolean("tostadas");
                        boolean tapas = rs.getBoolean("tapas");

                        // Construir el texto del item del JComboBox con el tipo de plato
                        String tipoPlato;
                        if (menu) {
                            tipoPlato = "Menu";
                        } else if (bocadillo) {
                            tipoPlato = "Bocadillo";
                        } else if (tostadas) {
                            tipoPlato = "Tostada";
                        } else if (tapas) {
                            tipoPlato = "Tapa";
                        } else {
                            tipoPlato = "No especificado";
                        }

                        String item = id + ": " + nombre + " - " + tipoPlato + " - " + precio.toString();
                        jComboBoxActualizar.addItem(item);
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            // Manejo del error, por ejemplo, mostrar un mensaje al usuario
            JOptionPane.showMessageDialog(this, "Error al cargar los platos.", "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            // Cerrar la conexión al finalizar
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private boolean platoNoExiste(Connection con, String nombre, boolean menu, boolean bocadillo, boolean tostadas, boolean tapas) throws SQLException {
        String sql = null;
        if (menu) {
            sql = "SELECT COUNT(*) FROM menu_bocadillos WHERE nombre = ? AND menu = true";
        } else if (bocadillo) {
            sql = "SELECT COUNT(*) FROM menu_bocadillos WHERE nombre = ? AND bocadillo = true";
        } else if (tostadas) {
            sql = "SELECT COUNT(*) FROM menu_bocadillos WHERE nombre = ? AND tostadas = true";
        } else if (tapas) {
            sql = "SELECT COUNT(*) FROM menu_bocadillos WHERE nombre = ? AND tapas = true";
        }
        try (PreparedStatement pstmt = con.prepareStatement(sql)) {
            pstmt.setString(1, nombre);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    return count == 0;
                }
            }
        }
        return false;
    }

    private void limpiarCampos() {
        jTextNombre.setText("");
        jTextPrecio.setText("");
        jCheckBoxMenu.setSelected(false);
        jCheckBoxBocadillo.setSelected(false);
        jCheckBoxTostadas.setSelected(false);
        jCheckBoxTapas.setSelected(false);
    }

    /**
     * Creates new form MenuYBocadillos
     */
    public MenuYBocadillos(java.awt.Frame parent, boolean modal) throws SQLException {
        super(parent, modal);
        initComponents();

        cargarPlatos(connection());
        //        this.setSize(anchoPredeterminado, altoPredeterminado);
        this.setSize(500, 550);
        setLocationRelativeTo(null);

        // Agregar el elemento predeterminado al JComboBox al principio
        jComboBoxActualizar.insertItemAt("< Selecciona un dato >", 0);
        // Establecer el elemento predeterminado como seleccionado
        jComboBoxActualizar.setSelectedIndex(0);

        // Configurar el ActionListener para el JComboBox
        jComboBoxActualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxActualizarActionPerformed(evt);
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jCheckBoxBocadilloActualizar = new javax.swing.JCheckBox();
        jLabel11 = new javax.swing.JLabel();
        jCheckBoxMenuActualizar = new javax.swing.JCheckBox();
        jTextNombreActualizar = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jCheckBoxMenu = new javax.swing.JCheckBox();
        jComboBoxActualizar = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        jButtonActualizar = new javax.swing.JButton();
        jTextNombre = new javax.swing.JTextField();
        jSeparator1 = new javax.swing.JSeparator();
        jLabel3 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jTextPrecioActualizar = new javax.swing.JTextField();
        jButtonGuardar = new javax.swing.JButton();
        jLabel13 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jTextFieldID = new javax.swing.JTextField();
        jTextPrecio = new javax.swing.JTextField();
        jCheckBoxBocadillo = new javax.swing.JCheckBox();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jCheckBoxTostadas = new javax.swing.JCheckBox();
        jLabel12 = new javax.swing.JLabel();
        jCheckBoxTostadasActualizar = new javax.swing.JCheckBox();
        jCheckBoxTapas = new javax.swing.JCheckBox();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jCheckBoxTapasActualizar = new javax.swing.JCheckBox();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItemSalir = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jLabel1.setText("Ingresar nuevo Menu o Bocadillo:");

        jCheckBoxBocadilloActualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxBocadilloActualizarActionPerformed(evt);
            }
        });

        jLabel11.setText("Bocadillo");

        jCheckBoxMenuActualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxMenuActualizarActionPerformed(evt);
            }
        });

        jLabel8.setText("Nombre:");

        jCheckBoxMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxMenuActionPerformed(evt);
            }
        });

        jComboBoxActualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxActualizarActionPerformed(evt);
            }
        });

        jLabel2.setText("Nombre:");

        jButtonActualizar.setText("Actualizar datos");
        jButtonActualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonActualizarActionPerformed(evt);
            }
        });

        jSeparator1.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        jLabel3.setText("Precio:");

        jLabel5.setText("Bocadillo");

        jButtonGuardar.setText("Guardar");
        jButtonGuardar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonGuardarActionPerformed(evt);
            }
        });

        jLabel13.setText("ID");

        jLabel9.setText("Precio:");

        jLabel10.setText("Menu");

        jLabel7.setText("Actualizar datos:");

        jTextFieldID.setEditable(false);

        jCheckBoxBocadillo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxBocadilloActionPerformed(evt);
            }
        });

        jLabel4.setText("Menu");

        jLabel6.setText("Tostadas");

        jCheckBoxTostadas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxTostadasActionPerformed(evt);
            }
        });

        jLabel12.setText("Tostadas");

        jCheckBoxTostadasActualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxTostadasActualizarActionPerformed(evt);
            }
        });

        jCheckBoxTapas.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxTapasActionPerformed(evt);
            }
        });

        jLabel14.setText("Tapas variadas");

        jLabel15.setText("Tapas variadas");

        jCheckBoxTapasActualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxTapasActualizarActionPerformed(evt);
            }
        });

        jMenu1.setText("Inicio");

        jMenuItemSalir.setText("Salir");
        jMenuItemSalir.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItemSalirActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItemSalir);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(41, 41, 41)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jCheckBoxBocadillo)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel14)
                                .addGap(18, 18, 18)
                                .addComponent(jCheckBoxTapas))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jCheckBoxMenu)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel6)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jCheckBoxTostadas))))
                    .addComponent(jLabel13, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel7, javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jComboBoxActualizar, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel10)
                            .addComponent(jLabel11))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jCheckBoxBocadilloActualizar)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel15)
                                .addGap(18, 18, 18)
                                .addComponent(jCheckBoxTapasActualizar))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jCheckBoxMenuActualizar)
                                .addGap(18, 18, 18)
                                .addComponent(jLabel12)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jCheckBoxTostadasActualizar))))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jButtonActualizar)
                        .addGroup(layout.createSequentialGroup()
                            .addComponent(jLabel8)
                            .addGap(52, 52, 52)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jTextFieldID, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jTextPrecioActualizar, javax.swing.GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
                                .addComponent(jTextNombreActualizar))))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(jButtonGuardar)
                        .addGroup(layout.createSequentialGroup()
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel2)
                                .addComponent(jLabel3))
                            .addGap(58, 58, 58)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jTextNombre, javax.swing.GroupLayout.DEFAULT_SIZE, 240, Short.MAX_VALUE)
                                .addComponent(jTextPrecio)))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextNombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextPrecio, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(jLabel6)
                            .addComponent(jCheckBoxTostadas, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addGap(9, 9, 9)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(jLabel5)
                                .addComponent(jCheckBoxBocadillo))
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel14)
                                .addComponent(jCheckBoxTapas))))
                    .addComponent(jCheckBoxMenu))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButtonGuardar)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jComboBoxActualizar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13)
                    .addComponent(jTextFieldID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextNombreActualizar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel9)
                    .addComponent(jTextPrecioActualizar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel10)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel11))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel12)
                            .addComponent(jCheckBoxTostadasActualizar))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jCheckBoxBocadilloActualizar)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel15)
                                .addComponent(jCheckBoxTapasActualizar))))
                    .addComponent(jCheckBoxMenuActualizar))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButtonActualizar)
                .addGap(15, 15, 15))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jCheckBoxBocadilloActualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxBocadilloActualizarActionPerformed
        // TODO add your handling code here:
        if (jCheckBoxBocadilloActualizar.isSelected()) {
            jCheckBoxMenuActualizar.setSelected(false);
            jCheckBoxTostadasActualizar.setSelected(false);
            jCheckBoxTapasActualizar.setSelected(false);
        }
    }//GEN-LAST:event_jCheckBoxBocadilloActualizarActionPerformed

    private void jCheckBoxMenuActualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxMenuActualizarActionPerformed
        // TODO add your handling code here:
        if (jCheckBoxMenuActualizar.isSelected()) {
            jCheckBoxBocadilloActualizar.setSelected(false);
            jCheckBoxTostadasActualizar.setSelected(false);
            jCheckBoxTapasActualizar.setSelected(false);
        }
    }//GEN-LAST:event_jCheckBoxMenuActualizarActionPerformed

    private void jCheckBoxMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxMenuActionPerformed
        // TODO add your handling code here:
        if (jCheckBoxMenu.isSelected()) {
            jCheckBoxBocadillo.setSelected(false);
            jCheckBoxTostadas.setSelected(false);
            jCheckBoxTapas.setSelected(false);
        }
    }//GEN-LAST:event_jCheckBoxMenuActionPerformed

    private void jComboBoxActualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBoxActualizarActionPerformed
        // TODO add your handling code here:
        String seleccion = (String) jComboBoxActualizar.getSelectedItem();
        if (seleccion != null && !seleccion.equals("< Selecciona un dato >")) {
            String[] parts = seleccion.split(": ");
            if (parts.length != 2) {
                JOptionPane.showMessageDialog(this, "Error al obtener el nombre del dato.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            int idPlato;
            try {
                idPlato = Integer.parseInt(parts[0].trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Error al obtener el ID del dato.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Connection connection = null;
            try {
                connection = Principal.connection();
                String sql = "SELECT id, nombre, precio, menu, bocadillo, tostadas, tapas FROM menu_bocadillos WHERE id = ?";
                try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
                    pstmt.setInt(1, idPlato);// Usar el ID del plato obtenido correctamente
                    try (ResultSet rs = pstmt.executeQuery()) {
                        if (rs.next()) {
                            jTextFieldID.setText(rs.getInt("id") + ""); // Establece el ID del plato
                            jTextNombreActualizar.setText(rs.getString("nombre")); // Establece el nombre del plato
                            jTextPrecioActualizar.setText(rs.getBigDecimal("precio").toString()); // Establece el precio del plato
                            jCheckBoxMenuActualizar.setSelected(rs.getBoolean("menu")); // Establece el valor de menu
                            jCheckBoxBocadilloActualizar.setSelected(rs.getBoolean("bocadillo")); // Establece el valor de bocadillo
                            jCheckBoxTostadasActualizar.setSelected(rs.getBoolean("tostadas"));
                            jCheckBoxTapasActualizar.setSelected(rs.getBoolean("tapas"));
                        }
                    }
                }

            } catch (SQLException ex) {
                ex.printStackTrace();
                // Manejo del error, por ejemplo, mostrar un mensaje al usuario
                JOptionPane.showMessageDialog(this, "Error al obtener los datos.", "Error", JOptionPane.ERROR_MESSAGE);
            } finally {
                // Cerrar la conexión al finalizar
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        } else {
            // Vaciar los campos
            jTextFieldID.setText("");
            jTextNombreActualizar.setText("");
            jTextPrecioActualizar.setText("");
            jCheckBoxBocadilloActualizar.setSelected(false);
            jCheckBoxMenuActualizar.setSelected(false);
            jCheckBoxTostadasActualizar.setSelected(false);
            jCheckBoxTapasActualizar.setSelected(false);
        }
    }//GEN-LAST:event_jComboBoxActualizarActionPerformed

    private void jButtonActualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonActualizarActionPerformed
        // TODO add your handling code here:
        String selectedPlato = (String) jComboBoxActualizar.getSelectedItem();

        // Obtener el ID del plato seleccionado
        String[] parts = selectedPlato.split(" - ");
        if (parts.length != 3) {
            JOptionPane.showMessageDialog(this, "Error al obtener el nombre del menu o bocadillo.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int idPlato;
        try {
            idPlato = Integer.parseInt(jTextFieldID.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Error al obtener el ID del menu o bocadillo.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Obtener los nuevos valores
        String nuevoNombre;
        BigDecimal nuevoPrecio;
        boolean bocadillos;
        boolean menu;
        boolean tostadas;
        boolean tapas;
        try {
            nuevoNombre = jTextNombreActualizar.getText();
            nuevoPrecio = new BigDecimal(jTextPrecioActualizar.getText());
            bocadillos = jCheckBoxBocadilloActualizar.isSelected();
            menu = jCheckBoxMenuActualizar.isSelected();
            tostadas = jCheckBoxTostadasActualizar.isSelected();
            tapas = jCheckBoxTapasActualizar.isSelected();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Error al obtener los nuevos valores.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Establecer la conexión a la base de datos
        Connection connection = null;
        try {
            connection = Principal.connection();
            // Actualizar el plato en la base de datos
            String sqlUpdate = "UPDATE menu_bocadillos SET nombre = ?, precio = ?, menu = ?, bocadillo = ?, tostadas = ?, tapas = ? WHERE id = ?";
            try (PreparedStatement pstmtUpdate = connection.prepareStatement(sqlUpdate)) {
                pstmtUpdate.setString(1, nuevoNombre);
                pstmtUpdate.setBigDecimal(2, nuevoPrecio);
                pstmtUpdate.setBoolean(3, menu);
                pstmtUpdate.setBoolean(4, bocadillos);
                pstmtUpdate.setBoolean(5, tostadas);
                pstmtUpdate.setBoolean(6, tapas);
                pstmtUpdate.setInt(7, idPlato);
                int rowsUpdated = pstmtUpdate.executeUpdate();
                if (rowsUpdated > 0) {
                    JOptionPane.showMessageDialog(this, "Datos de menu o bocadillo, actualizado correctamente");
                    cargarPlatos(connection()); // Recargar los datos del JComboBox
                } else {
                    JOptionPane.showMessageDialog(this, "No se encontró el plato a actualizar.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error al actualizar el plato.", "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            // Cerrar la conexión al finalizar
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }//GEN-LAST:event_jButtonActualizarActionPerformed

    private void jButtonGuardarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonGuardarActionPerformed
        try {
            // TODO add your handling code here:
            insertarMenuOBocadillos(connection());
            cargarPlatos(connection());
        } catch (SQLException ex) {
            Logger.getLogger(Caldos.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Agregar el elemento predeterminado al JComboBox al principio
        jComboBoxActualizar.insertItemAt("< Selecciona un dato >", 0);
        // Establecer el elemento predeterminado como seleccionado
        jComboBoxActualizar.setSelectedIndex(0);

        // Configurar el ActionListener para el JComboBox
        jComboBoxActualizar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBoxActualizarActionPerformed(evt);
            }
        });
    }//GEN-LAST:event_jButtonGuardarActionPerformed

    private void jCheckBoxBocadilloActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxBocadilloActionPerformed
        // TODO add your handling code here:
        if (jCheckBoxBocadillo.isSelected()) {
            jCheckBoxMenu.setSelected(false);
            jCheckBoxTostadas.setSelected(false);
            jCheckBoxTapas.setSelected(false);
        }
    }//GEN-LAST:event_jCheckBoxBocadilloActionPerformed

    private void jMenuItemSalirActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItemSalirActionPerformed
        // TODO add your handling code here:
        dispose();
    }//GEN-LAST:event_jMenuItemSalirActionPerformed

    private void jCheckBoxTostadasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxTostadasActionPerformed
        // TODO add your handling code here:
        if (jCheckBoxTostadas.isSelected()) {
            jCheckBoxMenu.setSelected(false);
            jCheckBoxBocadillo.setSelected(false);
            jCheckBoxTapas.setSelected(false);
        }
    }//GEN-LAST:event_jCheckBoxTostadasActionPerformed

    private void jCheckBoxTostadasActualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxTostadasActualizarActionPerformed
        // TODO add your handling code here:
        if (jCheckBoxTostadasActualizar.isSelected()) {
            jCheckBoxBocadilloActualizar.setSelected(false);
            jCheckBoxMenuActualizar.setSelected(false);
            jCheckBoxTapasActualizar.setSelected(false);
        }
    }//GEN-LAST:event_jCheckBoxTostadasActualizarActionPerformed

    private void jCheckBoxTapasActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxTapasActionPerformed
        // TODO add your handling code here:
        if (jCheckBoxTapas.isSelected()) {
            jCheckBoxMenu.setSelected(false);
            jCheckBoxTostadas.setSelected(false);
            jCheckBoxBocadillo.setSelected(false);
        }
    }//GEN-LAST:event_jCheckBoxTapasActionPerformed

    private void jCheckBoxTapasActualizarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxTapasActualizarActionPerformed
        // TODO add your handling code here:
        if (jCheckBoxTapasActualizar.isSelected()) {
            jCheckBoxBocadilloActualizar.setSelected(false);
            jCheckBoxTostadasActualizar.setSelected(false);
            jCheckBoxMenuActualizar.setSelected(false);
        }
    }//GEN-LAST:event_jCheckBoxTapasActualizarActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MenuYBocadillos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MenuYBocadillos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MenuYBocadillos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MenuYBocadillos.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                MenuYBocadillos dialog = null;
                try {
                    dialog = new MenuYBocadillos(new javax.swing.JFrame(), true);
                } catch (SQLException ex) {
                    Logger.getLogger(MenuYBocadillos.class.getName()).log(Level.SEVERE, null, ex);
                }
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButtonActualizar;
    private javax.swing.JButton jButtonGuardar;
    private javax.swing.JCheckBox jCheckBoxBocadillo;
    private javax.swing.JCheckBox jCheckBoxBocadilloActualizar;
    private javax.swing.JCheckBox jCheckBoxMenu;
    private javax.swing.JCheckBox jCheckBoxMenuActualizar;
    private javax.swing.JCheckBox jCheckBoxTapas;
    private javax.swing.JCheckBox jCheckBoxTapasActualizar;
    private javax.swing.JCheckBox jCheckBoxTostadas;
    private javax.swing.JCheckBox jCheckBoxTostadasActualizar;
    private javax.swing.JComboBox<String> jComboBoxActualizar;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItemSalir;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextField jTextFieldID;
    private javax.swing.JTextField jTextNombre;
    private javax.swing.JTextField jTextNombreActualizar;
    private javax.swing.JTextField jTextPrecio;
    private javax.swing.JTextField jTextPrecioActualizar;
    // End of variables declaration//GEN-END:variables
}
