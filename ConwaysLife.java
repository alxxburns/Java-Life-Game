// Conway's Game of Life program 
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.image.*;
import java.io.*;

public class ConwaysLife extends JPanel implements Runnable, MouseListener, ActionListener {
    // member data
    private BufferStrategy strategy;
    private Graphics offscreenBuffer;
    private boolean gameState[][] = new boolean[40][40];
    private boolean isPlaying = false;

    // constructor
    public ConwaysLife() {
        // Initialise the game state
        for (int x = 0; x < 40; x++) {
            for (int y = 0; y < 40; y++) {
                gameState[x][y] = false;
            }
        }

        // register the JPanel to receive mouse events
        addMouseListener(this);

        // create and start animation thread
        Thread t = new Thread(this);
        t.start();
    }

    // thread's entry point
    public void run() {
        while (true) {
            if (isPlaying) {
                // 1: sleep for 1/5 sec
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                // 2: animate game objects
                boolean newState[][] = new boolean[40][40];
                for (int x = 0; x < 40; x++) {
                    for (int y = 0; y < 40; y++) {
                        int neighbors = countNeighbors(x, y);
                        if (gameState[x][y]) {
                            newState[x][y] = (neighbors == 2 || neighbors == 3);
                        } else {
                            newState[x][y] = (neighbors == 3);
                        }
                    }
                }
                gameState = newState;
                // 3: force a panel repaint
                this.repaint();
            }
        }
    }

    // mouse events which must be implemented for MouseListener
    public void mousePressed(MouseEvent e) {
        // determine which cell of the gameState array was clicked on
        int x = e.getX() / 20;
        int y = e.getY() / 20;
        // check if button was clicked
        if (!isPlaying) {
            if (x >= 16 && x <= 23 && y >= 18 && y <= 22) {
                startGame();
            } else if (x >= 16 && x <= 23 && y >= 23 && y <= 27) {
                randomizeGameState();
                this.repaint();
            }
        }
    }

    public void mouseReleased(MouseEvent e) {
    }

    public void mouseEntered(MouseEvent e) {
    }

    public void mouseExited(MouseEvent e) {
    }

    public void mouseClicked(MouseEvent e) {
    }

    public void paint(Graphics g) {
    	JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
    	frame.createBufferStrategy(2);
    	strategy = frame.getBufferStrategy();
    	offscreenBuffer = strategy.getDrawGraphics();

        // clear the canvas with a big black rectangle
        offscreenBuffer.setColor(Color.BLACK);
        offscreenBuffer.fillRect(0, 0, 800, 800);

        // redraw all game objects
        offscreenBuffer.setColor(Color.WHITE);
        int x, y;
        for (x = 0; x < 40; x++) {
            for (y = 0; y < 40; y++) {
                if (gameState[x][y]) {
                    offscreenBuffer.fillRect(x * 20, y * 20, 20, 20);
                }
            }
        }

        // render buttons if game is not playing
        if (!isPlaying) {
            offscreenBuffer.setColor(Color.GRAY);
            offscreenBuffer.fillRect(320, 360, 160, 80);
            offscreenBuffer.fillRect(320, 460, 160, 80);
            offscreenBuffer.setColor(Color.WHITE);
            offscreenBuffer.drawString("Start", 370, 400);
            offscreenBuffer.drawString("Random", 350, 500);
        } else {
            offscreenBuffer.setColor(Color.WHITE);
            offscreenBuffer.fillRect(320, 360, 160, 80);
            offscreenBuffer.setColor(Color.BLACK);
            offscreenBuffer.drawRect(320, 360, 160, 80);
            offscreenBuffer.setColor(Color.WHITE);
            offscreenBuffer.drawString("Stop", 370, 400);
        }

        strategy.show(); // flip the buffers
    }


    	// start the game
    	private void startGame() {
        isPlaying = true;
    	}
        
    	// stop the game
        private void stopGame() {
            isPlaying = false;
        }

        // generate a random game state
        private void randomizeGameState() {
            for (int x = 0; x < 40; x++) {
                for (int y = 0; y < 40; y++) {
                    gameState[x][y] = Math.random() < 0.5;
                }
            }
        }

        // count the number of live neighbors for a given cell
        private int countNeighbors(int x, int y) {
            int count = 0;
            for (int dx = -1; dx <= 1; dx++) {
                for (int dy = -1; dy <= 1; dy++) {
                    if (dx == 0 && dy == 0) {
                        continue;
                    }
                    int nx = x + dx;
                    int ny = y + dy;
                    if (nx >= 0 && nx < 40 && ny >= 0 && ny < 40 && gameState[nx][ny]) {
                        count++;
                    }
                }
            }
            return count;
        }

        // handle button clicks
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals("Start")) {
                startGame();
            } else if (e.getActionCommand().equals("Random")) {
                randomizeGameState();
                this.repaint();
            } else if (e.getActionCommand().equals("Stop")) {
                stopGame();
            }
        }

        // save the current game state to a file
        private void saveGameState() {
            try {
                ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream("gamestate.dat"));
                out.writeObject(gameState);
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // load a game state from a file
        private void loadGameState() {
            try {
                ObjectInputStream in = new ObjectInputStream(new FileInputStream("gamestate.dat"));
                gameState = (boolean[][]) in.readObject();
                in.close();
                this.repaint();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }

        public static void main(String[] args) {
            ConwaysLife game = new ConwaysLife();
            JFrame frame = new JFrame();
            frame.add(game);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setVisible(true);

            // create buttons
            JButton startButton = new JButton("Start");
            startButton.setActionCommand("Start");
            startButton.addActionListener(game);

            JButton stopButton = new JButton("Stop");
            stopButton.setActionCommand("Stop");
            stopButton.addActionListener(game);

            JButton randomButton = new JButton("Random");
            randomButton.setActionCommand("Random");
            randomButton.addActionListener(game);

            JButton saveButton = new JButton("Save");
            saveButton.setActionCommand("Save");
            saveButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    game.saveGameState();
                }
            });

            JButton loadButton = new JButton("Load");
            loadButton.setActionCommand("Load");
            loadButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    game.loadGameState();
                }
            });

            // add buttons to panel
            JPanel buttonPanel = new JPanel();
            buttonPanel.add(startButton);
            buttonPanel.add(stopButton);
            buttonPanel.add(randomButton);
            buttonPanel.add(saveButton);
            buttonPanel.add(loadButton);

         // add panel to frame
            frame.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
            frame.pack();
            frame.setVisible(true);
        }
    }


