package graphics;

import java.awt.image.BufferedImage;

public class Animation {
    private BufferedImage[] frames;
    private int currentFrame;
    private int frameDelay; // Number of game updates before switching frame (speed)
    private int counter;
    private boolean loop;
    private boolean finished;

    public Animation(BufferedImage[] frames, int frameDelay) {
        this(frames, frameDelay, true);
    }

    public Animation(BufferedImage[] frames, int frameDelay, boolean loop) {
        this.frames = frames;
        this.frameDelay = frameDelay;
        this.loop = loop;
        this.currentFrame = 0;
        this.counter = 0;
        this.finished = false;
    }

    public void update() {
        if (finished) return;

        counter++;
        if (counter >= frameDelay) {
            counter = 0;
            currentFrame++;
            
            if (currentFrame >= frames.length) {
                if (loop) {
                    currentFrame = 0;
                } else {
                    currentFrame = frames.length - 1;
                    finished = true;
                }
            }
        }
    }
    
    public void reset() {
        currentFrame = 0;
        counter = 0;
        finished = false;
    }

    public BufferedImage getFrame() {
        return frames[currentFrame];
    }
    
    public boolean isFinished() {
        return finished;
    }
}
