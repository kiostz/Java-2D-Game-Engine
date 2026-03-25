package main;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class KeyHandler implements KeyListener {

    GamePanel gp;
    public boolean upPressed, downPressed, leftPressed, rightPressed,
            enterPressed, interactPressed;
    // DEBUG
    public boolean checkDrawTime; // Existing debug flag for draw time
    public boolean debugMode = false; // New debug flag for visualizing areas

    public KeyHandler(GamePanel gp) {
        this.gp = gp;
    }

    @Override
    public void keyTyped(KeyEvent e) {

    }

    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        // Use switch to handle logic based on the current game state
        switch (gp.gameState) {
            // Handling logic when the game is in the active playing state
            case GamePanel.playState:
                // Move player up
                if (code == KeyEvent.VK_W) { upPressed = true; }
                // Move player down
                if (code == KeyEvent.VK_S) { downPressed = true; }
                // Move player left
                if (code == KeyEvent.VK_A) { leftPressed = true; }
                // Move player right
                if (code == KeyEvent.VK_D) { rightPressed = true; }
                // Interact or confirm
                if (code == KeyEvent.VK_ENTER || code == KeyEvent.VK_F) { enterPressed = true; interactPressed = true; }
                // Change state to paused
                if (code == KeyEvent.VK_ESCAPE) { gp.gameState = GamePanel.pauseState; }
                // Exit switch once a case is handled
                break;

            // Handling logic when the game is paused
            case GamePanel.pauseState:
                // Return to the play state
                if (code == KeyEvent.VK_ESCAPE) { gp.gameState = GamePanel.playState; }
                // Exit switch
                break;

            // Handling logic during NPC or event dialogues
            case GamePanel.dialogueState:
                // Advance or close dialogue
                if (code == KeyEvent.VK_ENTER || code == KeyEvent.VK_F) {
                    gp.gameState = GamePanel.playState;
                }
                // Exit switch
                break;

            // Handle Title Screen
            case GamePanel.titleState:
                if (code == KeyEvent.VK_UP || code == KeyEvent.VK_W) {
                    gp.ui.commandNum--;
                    gp.playSound(2);
                    if (gp.ui.commandNum < 0) {
                        gp.ui.commandNum = 2;
                    }
                }
                if (code == KeyEvent.VK_DOWN || code == KeyEvent.VK_S) {
                    gp.ui.commandNum++;
                    gp.playSound(2);
                    if (gp.ui.commandNum > 2) {
                        gp.ui.commandNum = 0;
                    }
                }
                if (code == KeyEvent.VK_ENTER) {
                    switch (gp.ui.commandNum) {
                        case 0:
                            gp.gameState = GamePanel.playState;
                            gp.playSound(3);
                            gp.stopMusic();
                            gp.playMusic(5);
                            break;
                        case 1:
                            // add later
                            break;
                        case 2:
                            System.exit(0);
                            break;
                    }
                }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int code = e.getKeyCode();
        if (code == KeyEvent.VK_W) {
            upPressed = false;
        }
        if (code == KeyEvent.VK_S) {
            downPressed = false;
        }
        if (code == KeyEvent.VK_A) {
            leftPressed = false;
        }
        if (code == KeyEvent.VK_D) {
            rightPressed = false;
        }

        // DEBUG
        if (code == KeyEvent.VK_T) {
            checkDrawTime = !checkDrawTime; // Toggle existing draw time debug
            debugMode = !debugMode; // Toggle new debug mode for visualizing areas
        }
    }
}
