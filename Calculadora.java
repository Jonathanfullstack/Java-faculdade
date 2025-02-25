import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Calculadora {
    private JFrame frame;
    private JTextField textField;
    private StringBuilder input;

    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            try {
                Calculadora window = new Calculadora();
                window.frame.setVisible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    public Calculadora() {
        input = new StringBuilder();
        frame = new JFrame();
        frame.setTitle("Calculadora");
        frame.setBounds(100, 100, 400, 500);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

        // Cor de fundo da janela
        frame.getContentPane().setBackground(new Color(240, 240, 240));

        // Campo de texto com fundo e borda estilizada
        textField = new JTextField();
        textField.setFont(new Font("Arial", Font.PLAIN, 24));
        textField.setBounds(10, 11, 364, 50);
        textField.setHorizontalAlignment(SwingConstants.RIGHT);
        textField.setBackground(new Color(245, 245, 245));
        textField.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 2));
        textField.setEditable(false);
        frame.getContentPane().add(textField);

        // Definindo as cores dos botões
        String[] buttons = {
                "7", "8", "9", "/",
                "4", "5", "6", "*",
                "1", "2", "3", "-",
                "0", ".", "=", "+", "C"
        };

        criarBotoes(buttons);
    }

    // Método para criar os botões e adicioná-los ao layout
    private void criarBotoes(String[] buttons) {
        int xPos = 10, yPos = 70;
        for (String text : buttons) {
            JButton button = new JButton(text);
            button.setFont(new Font("Arial", Font.PLAIN, 20));
            button.setBounds(xPos, yPos, 80, 80);
            button.setFocusPainted(false);
            button.setBackground(new Color(240, 240, 240));  // Cor de fundo dos botões
            button.setForeground(new Color(40, 40, 40));     // Cor do texto
            button.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 2));
            button.setPreferredSize(new Dimension(80, 80));

            // Adiciona um efeito de sombreado nos botões quando pressionados
            button.addActionListener(new ButtonClickListener());
            button.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    button.setBackground(new Color(220, 220, 220));  // Cor ao passar o mouse
                }

                @Override
                public void mouseExited(java.awt.event.MouseEvent e) {
                    button.setBackground(new Color(240, 240, 240));  // Cor normal
                }
            });

            frame.getContentPane().add(button);

            xPos += 90;
            if (xPos > 270) {
                xPos = 10;
                yPos += 90;
            }
        }
    }

    // ActionListener para capturar cliques nos botões
    private class ButtonClickListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String command = e.getActionCommand();

            if (command.equals("=")) {
                try {
                    String result = calcular(input.toString());
                    textField.setText(result);
                    input.setLength(0); // Limpa a entrada após o cálculo
                } catch (Exception ex) {
                    textField.setText("Erro");
                }
            } else if (command.equals("C")) {
                input.setLength(0);
                textField.setText("");
            } else {
                input.append(command);
                textField.setText(input.toString());
            }
        }
    }

    // Método para calcular a expressão
    private String calcular(String expression) {
        try {
            return String.valueOf(eval(expression));
        } catch (Exception e) {
            return "Erro";
        }
    }

    // Método simples para avaliação da expressão
    private double eval(String expression) {
        return new Object() {
            int pos = -1, c;

            void nextChar() {
                c = (++pos < expression.length()) ? expression.charAt(pos) : -1;
            }

            boolean eat(int charToEat) {
                while (c == ' ') nextChar();
                if (c == charToEat) {
                    nextChar();
                    return true;
                }
                return false;
            }

            double parse() {
                nextChar();
                double x = parseExpression();
                if (pos < expression.length()) {
                    throw new RuntimeException("Erro de sintaxe");
                }
                return x;
            }

            double parseExpression() {
                double x = parseTerm();
                for (;;) {
                    if (eat('+')) x += parseTerm();
                    else if (eat('-')) x -= parseTerm();
                    else return x;
                }
            }

            double parseTerm() {
                double x = parseFactor();
                for (;;) {
                    if (eat('*')) x *= parseFactor();
                    else if (eat('/')) x /= parseFactor();
                    else return x;
                }
            }

            double parseFactor() {
                if (eat('+')) return parseFactor();
                if (eat('-')) return -parseFactor();

                double x;
                int startPos = this.pos;
                if (eat('(')) {
                    x = parseExpression();
                    eat(')');
                } else if ((c >= '0' && c <= '9') || c == '.') {
                    while ((c >= '0' && c <= '9') || c == '.') nextChar();
                    x = Double.parseDouble(expression.substring(startPos, this.pos));
                } else {
                    throw new RuntimeException("Erro de sintaxe");
                }

                return x;
            }
        }.parse();
    }
}
