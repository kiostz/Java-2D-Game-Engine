package main;

import javax.swing.JFrame;

public class Main {

    public static void main(String[] args) {
        GameLogger.setup(); // Initialize the log file
        JFrame window = new JFrame();
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Close window when click on "x" button
        window.setResizable(false); // Disable window resizing
        window.setTitle("Exodus");

        GamePanel gamePanel = new GamePanel();
        window.add(gamePanel);

        window.pack(); //resize window to subcomponent size 'gamePanel'

        window.setLocationRelativeTo(null); // null = Window open at center
        window.setVisible(true);

        gamePanel.setUpGame();
        gamePanel.startGameThread();
    }
}