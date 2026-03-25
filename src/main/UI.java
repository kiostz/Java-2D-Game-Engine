package main;

import java.awt.*;
import java.text.DecimalFormat;

public class UI {
    GamePanel gp;
    Graphics2D g2;
    public boolean messageOn = false;
    public String message = "";
    public int messageCounter = 0;
    public boolean gameFinished = false;
    public String currentDialogue = "";
    public int commandNum = 0;
    
    // Timer
    double playTime;
    DecimalFormat dFormat = new DecimalFormat("#0.00");

    public UI(GamePanel gp) {
        this.gp = gp;
    }
    public void showMessage(String text) {
        message = text;
        messageOn = true;
    }
    public void draw(Graphics2D g2) {
        this.g2 = g2;
        // Enable Anti-Aliasing for text to make Chinese characters clear
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // MAIN MENU TITLE STATE
        if (gp.gameState == GamePanel.titleState) {
            g2.setFont(gp.fontManager.getFont().deriveFont(Font.BOLD, 28F));
            drawTitleScreen();
        }
        // PLAY STATE
        if(gp.gameState == GamePanel.playState) {
            // Do play state stuff later
            if (messageOn) {
                g2.setColor(Color.white );
                g2.setFont(g2.getFont().deriveFont(12F));
                g2.drawString(message, gp.player.screenX - gp.tileSize / 2, gp.player.screenY);

                messageCounter++;
                if (messageCounter > 120) { // 2 seconds
                    messageCounter = 0;
                    messageOn = false;
                }
            }
            // Play Time
            playTime += (double)1/60;
            g2.setColor(Color.white );
            g2.drawString("Time: " + dFormat.format(playTime), gp.tileSize * 11, 65);
        }
        // PAUSE STATE
        if (gp.gameState == GamePanel.pauseState) {
            drawPauseScreen();
        }
        // DIALOGUE STATE
        if (gp.gameState == GamePanel.dialogueState) {
            drawDialogueScreen();
        }
        
        // GAME OVER STATE
        if (gameFinished) {
            g2.setFont(gp.fontManager.getFont().deriveFont(Font.BOLD, 40F));
            g2.setColor(Color.white);
            String text;
            int textLength;
            int x;
            int y;

            text = "You found the treasure!";
            textLength = (int)g2.getFontMetrics().getStringBounds(text, g2).getWidth();
            x = gp.screenWidth/2 - textLength/2;
            y = gp.screenHeight/2 - (gp.tileSize*3);
            g2.drawString(text, x, y);

            text = "Your Time is :" + dFormat.format(playTime) + "!";
            textLength = (int)g2.getFontMetrics().getStringBounds(text, g2).getWidth();
            x = gp.screenWidth/2 - textLength/2;
            y = gp.screenHeight/2 + (gp.tileSize*4);
            g2.drawString(text, x, y);

            g2.setFont(gp.fontManager.getFont().deriveFont(Font.BOLD, 80F));
            g2.setColor(Color.yellow);
            text = "Congratulations!";
            textLength = (int)g2.getFontMetrics().getStringBounds(text, g2).getWidth();
            x = gp.screenWidth/2 - textLength/2;
            y = gp.screenHeight/2 + (gp.tileSize*2);
            g2.drawString(text, x, y);

            gp.gameThread = null;
        }
    }
    public void drawTitleScreen() {
        // SCREEN BACKGROUND
        g2.setColor(new Color(100, 200, 120));
        g2.fillRect(0, 0, gp.screenWidth, gp.screenHeight);
        // TITLE NAME SETTING
        g2.setFont(gp.fontManager.getFont().deriveFont(Font.BOLD, 84F));
        String text = "My NAme is JEsUS";
        int x = getXForCenteredText(text);
        int y = gp.tileSize * 2;
        // DRAW TITLE NAME SHADOW
        g2.setColor(Color.BLACK);
        g2.drawString(text, x + 5, y + 5);
        // DRAW TITLE NAME
        g2.setColor(Color.white);
        g2.drawString(text, x, y);
        // IMAGE
        x = gp.screenWidth/2 - (gp.tileSize/2);
        y += gp.tileSize * 2;
        g2.drawImage(gp.player.idleDown.getFrame(), x, y, gp.tileSize, gp.tileSize, null);
        // MENU
        g2.setFont(gp.fontManager.getFont().deriveFont(Font.BOLD, 40F));
        text = "Start Game";
        x = getXForCenteredText(text);
        y += gp.tileSize * 2;
        g2.drawString(text, x, y);
        if(commandNum == 0) {
            g2.drawString(">", x - gp.tileSize, y);
        }

        text = "Load Game";
        x = getXForCenteredText(text);
        y += gp.tileSize * 1;
        g2.drawString(text, x, y);
        if(commandNum == 1) {
            g2.drawString(">", x - gp.tileSize, y);
        }

        text = "Quit";
        x = getXForCenteredText(text);
        y += gp.tileSize * 1;
        g2.drawString(text, x, y);
        if(commandNum == 2) {
            g2.drawString(">", x - gp.tileSize, y);
        }
    }
    public void drawPauseScreen() {
        String text = "Paused";
        int x = getXForCenteredText(text);
        int y = gp.screenHeight/2;

        g2.drawString(text, x, y);
    }
    public int getXForCenteredText(String text) {
        int length = (int)g2.getFontMetrics().getStringBounds(text, g2).getWidth();
        return gp.screenWidth/2 - length/2;
    }
    public void drawDialogueScreen() {
        // Dialogue Window
        // Determine dialogue window dimensions
        int x = gp.tileSize * 2;
        int y = gp.tileSize / 2;
        int width = gp.screenWidth - (gp.tileSize * 4);
        int height = gp.tileSize * 2;
        // Draw the window background
        drawDialogueWindow(x, y, width, height);

        // Apply font style and size specifically for the dialogue text
        // Using a float (28F) ensures Java calls the correct method signature
        g2.setFont(gp.fontManager.getFont().deriveFont(Font.PLAIN, 28F));
        x += gp.tileSize;
        y += gp.tileSize;

        for (String line: currentDialogue.split("\n")) {
            g2.drawString(line, x, y);
            y += g2.getFont().getSize() + 8;
        }
    }
    public void drawDialogueWindow(int x, int y, int width, int height) {
        Color color = new Color(0, 0, 0, 180);
        g2.setColor(color);
        g2.fillRoundRect(x, y, width, height, 10, 10);
        g2.setColor(Color.white);
        g2.setStroke(new BasicStroke(3));
        g2.drawRoundRect(x + 5, y+ 5, width - 10, height - 10, 10, 10);
    }
    public void drawPlayerDialogue() {

    }
}
