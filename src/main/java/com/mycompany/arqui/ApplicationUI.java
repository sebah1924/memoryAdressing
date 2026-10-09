/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.arqui;

/**
 *
 * @author estudiante
 */
import javax.swing.*;
import java.awt.*;

public class ApplicationUI extends JFrame {

    // Componentes visuales
    private JTextField txtSegment, txtOffset;
    
    // CONEXIÓN CON EL NEGOCIO (Instancia de tu clase)
    private RealMode negocioRealMode;

    public ApplicationUI() {
        // Inicializamos la capa de negocio separada
        this.negocioRealMode = new RealMode();

        // Configuración básica de la ventana
        setTitle("Simulador de Direccionamiento x86");
        setSize(550, 450);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Modo Real", createRealModePanel());
        tabs.addTab("Modo Protegido", new JPanel()); 
        tabs.addTab("Reversing Descriptor", new JPanel()); 

        add(tabs, BorderLayout.CENTER);
    }

    private JPanel createRealModePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtSegment = createHexTextField();
        txtOffset = createHexTextField();
        JButton btnCalcular = new JButton("Iniciar Animacion Pedagogica");
        
        // Panel de Casillas (3 filas, 5 columnas)
        JPanel pnlCasillas = new JPanel(new GridLayout(3, 5, 4, 4));
        pnlCasillas.setBorder(BorderFactory.createTitledBorder("Visualizacion por Celdas"));
        
        JLabel[][] celdas = new JLabel[3][5];
        for (int f = 0; f < 3; f++) {
            for (int c = 0; c < 5; c++) {
                celdas[f][c] = new JLabel(" ", SwingConstants.CENTER);
                celdas[f][c].setFont(new Font("Monospaced", Font.BOLD, 18));
                celdas[f][c].setOpaque(true);
                celdas[f][c].setBackground(new Color(245, 245, 245));
                celdas[f][c].setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
                celdas[f][c].setPreferredSize(new Dimension(35, 35));
                pnlCasillas.add(celdas[f][c]);
            }
        }

        // Posicionamiento en el GridBagLayout
        gbc.gridx = 0; gbc.gridy = 0; panel.add(new JLabel("Segmento (Hex):"), gbc);
        gbc.gridx = 1; panel.add(txtSegment, gbc);
        gbc.gridx = 0; gbc.gridy = 1; panel.add(new JLabel("Offset (Hex):"), gbc);
        gbc.gridx = 1; panel.add(txtOffset, gbc);
        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2; panel.add(btnCalcular, gbc);
        gbc.gridy = 3; panel.add(pnlCasillas, gbc);

        // --- ACCIÓN DEL BOTÓN CON INTEGRACIÓN DE NEGOCIO ---
        btnCalcular.addActionListener(e -> {
            String segStr = txtSegment.getText().trim().toUpperCase();
            String offStr = txtOffset.getText().trim().toUpperCase();

            if (segStr.isEmpty() || offStr.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Ingresa ambos valores.", "Campos Vacios", JOptionPane.WARNING_MESSAGE);
                return;
            }

            final String seg4 = String.format("%4s", segStr).replace(' ', '0');
            final String off4 = String.format("%4s", offStr).replace(' ', '0');

            // 1. INPUT: Conversión de la UI a enteros decimales
            int segmentDecimal = Integer.parseInt(seg4, 16);
            int offsetDecimal = Integer.parseInt(off4, 16);

            // 2. ALGORITMO: Consumo de la clase de negocio externa
            int resultadoDecimal = negocioRealMode.calculateRealMode(segmentDecimal, offsetDecimal);
            
            // 3. OUTPUT: Formateo final para las casillas animadas
            final String resultadoHex = String.format("%05X", resultadoDecimal);

            btnCalcular.setEnabled(false);

            // Limpiar casillas
            for (int f = 0; f < 3; f++) {
                for (int c = 0; c < 5; c++) {
                    celdas[f][c].setText(" ");
                    celdas[f][c].setBackground(new Color(245, 245, 245));
                }
            }

            // Animación por pasos temporizados
            Timer timer = new Timer(1000, new java.awt.event.ActionListener() {
                private int paso = 0;

                @Override
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    paso++;
                    switch (paso) {
                        case 1: // Segmento en fila 0
                            for (int i = 0; i < 4; i++) {
                                celdas[0][i].setText(String.valueOf(seg4.charAt(i)));
                                celdas[0][i].setBackground(new Color(173, 216, 230));
                            }
                            break;
                        case 2: // Desplazamiento (Añadir '0')
                            celdas[0][4].setText("0");
                            celdas[0][4].setBackground(new Color(255, 182, 193));
                            break;
                        case 3: // Offset en fila 1
                            for (int i = 0; i < 4; i++) {
                                celdas[1][i + 1].setText(String.valueOf(off4.charAt(i)));
                                celdas[1][i + 1].setBackground(new Color(144, 238, 144));
                            }
                            celdas[1][0].setText("+"); // Signo de suma en la celda libre
                            break;
                        case 4: // Resultado final en fila 2
                            for (int i = 0; i < 5; i++) {
                                celdas[2][i].setText(String.valueOf(resultadoHex.charAt(i)));
                                celdas[2][i].setBackground(new Color(255, 215, 0));
                            }
                            ((Timer)evt.getSource()).stop();
                            btnCalcular.setEnabled(true);
                            break;
                    }
                }
            });
            timer.start();
        });

        return panel;
    }

    // Filtro para forzar entradas Hexadecimales de máximo 4 dígitos
    private JTextField createHexTextField() {
        JTextField field = new JTextField(6);
        field.setFont(new Font("Monospaced", Font.PLAIN, 14));
        field.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyTyped(java.awt.event.KeyEvent e) {
                char c = e.getKeyChar();
                String text = field.getText();
                boolean isHex = (c >= '0' && c <= '9') || (c >= 'a' && c <= 'f') || (c >= 'A' && c <= 'F');
                if (!isHex || text.length() >= 4) {
                    e.consume();
                }
            }
        });
        return field;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ApplicationUI().setVisible(true));
    }
}
