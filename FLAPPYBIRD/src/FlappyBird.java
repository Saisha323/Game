import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

// OOP CONCEPT: CLASS + INHERITANCE  -> FlappyBird "extends" JPanel (it IS-A JPanel and inherits its methods)
// OOP CONCEPT: INTERFACES           -> implements ActionListener (game loop) and KeyListener (keyboard)
public class FlappyBird extends JPanel implements ActionListener, KeyListener {

    int boardWidth = 360;
    int boardHeight = 640;

    // images
    Image backgroundImg;
    Image birdImg;
    Image topPipeImg;
    Image bottomPipeImg;

    // bird class
    int birdX = boardWidth / 8;
    int birdY = boardHeight / 2;
    int birdWidth = 44;          // CHANGED from 34 so the cat is not squashed
    int birdHeight = 40;         // CHANGED from 24 so the cat is not squashed

    // OOP CONCEPT: INNER (NESTED) CLASS + OBJECT DATA  -> Bird groups the data of one bird
    class Bird {
        int x = birdX;
        int y = birdY;
        int width = birdWidth;
        int height = birdHeight;
        Image img;

        // OOP CONCEPT: CONSTRUCTOR -> runs when "new Bird(img)" is called and sets the image
        Bird(Image img) {
            this.img = img;
        }
    }

    // pipes
    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 64;
    int pipeHeight = 512;

    // OOP CONCEPT: INNER CLASS + CONSTRUCTOR -> every Pipe object stores its own x, y, image and "passed" flag
    class Pipe {
        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;
        Image img;
        boolean passed = false;

        Pipe(Image img) {
            this.img = img;
        }
    }

    // game logic
    Bird bird;                       // OOP CONCEPT: OBJECT REFERENCE (a Bird object lives here)
    int velocityX = -4;
    int velocityY = 0;
    int gravity = 1;

    // OOP CONCEPT: COLLECTION + GENERICS -> ArrayList<Pipe> can hold only Pipe objects
    ArrayList<Pipe> pipes;
    Random random = new Random();    // OOP CONCEPT: using a library class (object of class Random)

    Timer gameLoop;
    Timer placePipesTimer;
    boolean gameOver = false;
    double score = 0;

    // OOP CONCEPT: CONSTRUCTOR of FlappyBird -> sets up the whole game when "new FlappyBird()" is called
    FlappyBird() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        setFocusable(true);
        addKeyListener(this);        // "this" is a KeyListener because of the interface above

        // load images
        backgroundImg = new ImageIcon(getClass().getResource("./flappybirdbg.png")).getImage();
        birdImg = new ImageIcon(getClass().getResource("./flappybird .png")).getImage();   // CHANGED: cat instead of bird
        topPipeImg = new ImageIcon(getClass().getResource("./toppipe.png")).getImage();
        bottomPipeImg = new ImageIcon(getClass().getResource("./bottompipe.png")).getImage();

        bird = new Bird(birdImg);                 // OOP CONCEPT: OBJECT CREATION (new)
        pipes = new ArrayList<Pipe>();

        // place pipes timer
        // OOP CONCEPT: ANONYMOUS INNER CLASS + INTERFACE -> an unnamed class that implements ActionListener
        placePipesTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placePipes();
            }
        });
        placePipesTimer.start();

        // game timer
        gameLoop = new Timer(1000 / 60, this);   // "this" is passed because FlappyBird IS-A ActionListener
        gameLoop.start();
    }

    public void placePipes() {
        int randomPipeY = (int) (pipeY - pipeHeight / 4 - Math.random() * (pipeHeight / 2));
        int openingSpace = boardHeight / 4;

        Pipe topPipe = new Pipe(topPipeImg);      // OBJECT CREATION: many Pipe objects, each with its own data
        topPipe.y = randomPipeY;
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(bottomPipeImg);
        bottomPipe.y = topPipe.y + pipeHeight + openingSpace;
        pipes.add(bottomPipe);
    }

    // OOP CONCEPT: METHOD OVERRIDING -> replaces JPanel's own paintComponent with the game's drawing
    // OOP CONCEPT: super keyword      -> super.paintComponent(g) calls the parent's version first
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        g.drawImage(backgroundImg, 0, 0, boardWidth, boardHeight, null);
        g.drawImage(bird.img, bird.x, bird.y, bird.width, bird.height, null);
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.PLAIN, 32));   // FIXED spelling: "Ariel" -> "Arial"
        if (gameOver) {
            g.drawString("Game Over: " + String.valueOf((int) score), 10, 35);
        } else {
            g.drawString(String.valueOf((int) score), 10, 35);
        }
    }

    public void move() {
        // bird
        velocityY += gravity;
        bird.y += velocityY;
        bird.y = Math.max(bird.y, 0);

        // pipes
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            pipe.x += velocityX;
            if (!pipe.passed && bird.x > pipe.x + pipe.width) {
                pipe.passed = true;
                score += 0.5;
            }
            if (collision(bird, pipe)) {      // OOP CONCEPT: PASSING OBJECTS to a method (message passing)
                gameOver = true;
            }
        }
        if (bird.y > boardHeight) {
            gameOver = true;
        }
    }

    // takes two OBJECTS (a Bird and a Pipe) as parameters and returns true/false
    public boolean collision(Bird a, Pipe b) {
        return a.x < b.x + b.width &&
               a.x + a.width > b.x &&
               a.y < b.y + b.height &&
               a.y + a.height > b.y;
    }

    // OOP CONCEPT: INTERFACE METHOD IMPLEMENTATION + OVERRIDING (@Override) + POLYMORPHISM
    // The Timer only knows "I have an ActionListener" and calls actionPerformed(); our class decides what happens.
    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver) {
            placePipesTimer.stop();
            gameLoop.stop();
        }
    }

    // OOP CONCEPT: INTERFACE METHODS of KeyListener (all three MUST be implemented)
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            velocityY = -9;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }
}
