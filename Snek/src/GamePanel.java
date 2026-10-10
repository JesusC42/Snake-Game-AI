import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener {
    static final int SCREEN_WIDTH = 1300;//1200, 800, 1500, 1300
    static final int SCREEN_HEIGHT = 700;//600, 500, 800, 700
    static final int UNIT_SIZE = 50;//best to keep at 50
    static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / (UNIT_SIZE * UNIT_SIZE);
    static final int DELAY = 0;//Snake speed 25, 50, 90
    final int[] x = new int[GAME_UNITS];
    final int[] y = new int[GAME_UNITS];
    static final int INITIAL_SIZE = 6;
    int bodyParts = INITIAL_SIZE;
    int applesEaten;
    int appleX;
    int appleY;
    char direction = 'D';
    boolean running = false;
    Timer timer;
    Random random;
    ArrayList<SquareUnit> squareUnitsArray = new ArrayList<>();

    GamePanel() {
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        generateHamiltonianCycle();
        startGame();
    }
    public void startGame() {
        newApple();
        running = true;
        timer = new Timer(DELAY,this);
        timer.start();
    }
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }
    public void draw(Graphics g) {
        if(running) {
            g.setColor(new Color(255, 0, 0));
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

            /*shows the number of each square unit
            g.setColor(new Color(255, 255, 255));
            g.setFont(new Font("Ink Free", Font.BOLD, 20));
            for (SquareUnit current : squareUnitsArray) {
                if (current != null)
                    g.drawString(String.valueOf(current.getN()), current.getX(), current.getY() + UNIT_SIZE / 2);
                else
                    break;
            }*/
            for(int i = 1; i < bodyParts; i++) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setColor(new Color(0, 150, 0));
                g2.setStroke(new BasicStroke(UNIT_SIZE - 10));
                int centerX = x[i] + UNIT_SIZE / 2;
                int centerY = y[i] + UNIT_SIZE / 2;
                int centerX2 = x[i - 1] + UNIT_SIZE / 2;
                int centerY2 = y[i - 1] + UNIT_SIZE / 2;
                g2.drawLine(centerX, centerY, centerX2, centerY2);
            }
            g.setColor(Color.white);
            g.setFont( new Font("Ink Free", Font.BOLD, 40));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten)) / 2, g.getFont().getSize());
        }
        else {
            gameOver(g);
        }

    }
    public void newApple() {
        if (!checkWin()) {
            appleX = random.nextInt(SCREEN_WIDTH / UNIT_SIZE) * UNIT_SIZE;
            appleY = random.nextInt(SCREEN_HEIGHT / UNIT_SIZE) * UNIT_SIZE;
            for (int i = 0; i < bodyParts; i++)
                if (x[i] == appleX && y[i] == appleY)
                    newApple();
        }
    }

    public void move() {
        for(int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        switch(direction) {
            case 'U':
                y[0] = y[0] - UNIT_SIZE;
                break;
            case 'D':
                y[0] = y[0] + UNIT_SIZE;
                break;
            case 'L':
                x[0] = x[0] - UNIT_SIZE;
                break;
            case 'R':
                x[0] = x[0] + UNIT_SIZE;
                break;
        }
    }
    public void checkApple() {
        if((x[0] == appleX) && (y[0] == appleY)) {
            bodyParts++;
            applesEaten++;
            newApple();
            if (applesEaten < GAME_UNITS * (3 / 5.0))
                reNumberHamiltonianCycle(x[0], y[0]);
        }
    }
    public void checkCollisions() {
        if (!checkWin()) {
            //checks if head collides with body
            for (int i = bodyParts; i > 0; i--) {
                if ((x[0] == x[i] && y[0] == y[i]) && (x[0] != 0 && y[0] != 0)) {
                    running = false;
                    break;
                }
            }
            //check if head touches left border
            if (x[0] < 0) {
                running = false;
            }
            //check if head touches right border
            if (x[0] >= SCREEN_WIDTH) {
                running = false;
            }
            //check if head touches top border
            if (y[0] < 0) {
                running = false;
            }
            //check if head touches bottom border
            if (y[0] >= SCREEN_HEIGHT) {
                running = false;
            }

            if (!running) {
                timer.stop();
            }
        }
    }
    public void gameOver(Graphics g) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        //Score
        g.setColor(Color.white);
        g.setFont( new Font("Ink Free", Font.BOLD, 40));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics1.stringWidth("Score: " + applesEaten)) / 2, g.getFont().getSize());
        //Game Over text
        g.setColor(Color.white);
        g.setFont( new Font("Ink Free", Font.BOLD, 75));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        String winOrLose = bodyParts == GAME_UNITS ? "win!" : "lose!";
        g.drawString("Game Over: You " + winOrLose, (SCREEN_WIDTH - metrics2.stringWidth("Game Over")) / 2, SCREEN_HEIGHT / 2);
    }

    public void generateHamiltonianCycle() {
        squareUnitsArray.add(new SquareUnit(0, 0, squareUnitsArray.size() + 1));
        //automaticGenerateCycle();
        manualGenerateCycle4();
    }

    private int getSquareUnitNumber(int x, int y) {
        for (SquareUnit squareUnit : squareUnitsArray) {
            if (squareUnit.getX() == x && squareUnit.getY() == y)
                return squareUnit.getN();
        }
        return -1;
    }

    private boolean specialCheckCollision(int newX, int newY) {
        int start = 0;
        for (int i = 0; i < squareUnitsArray.size(); i++) {
            if (squareUnitsArray.get(i).getX() == x[0] && squareUnitsArray.get(i).getY() == y[0]) {
                start = i;
                break;
            }
        }
        int end = 0;
        for (int i = start; i < squareUnitsArray.size(); i++) {
            if (squareUnitsArray.get(i).getX() == newX && squareUnitsArray.get(i).getY() == newY) {
                end = i;
                break;
            }
        }
        for (int i = start; i <= end; i++) {
            int currentX = squareUnitsArray.get(i).getX();
            int currentY = squareUnitsArray.get(i).getY();
            for (int j = bodyParts; j > 0; j--) {
                if (currentX == x[j] && currentY == y[j])
                    return false;
            }
        }
        return true;
    }

    public int countSkip(int newX, int newY) {
        int start = 0;
        for (int i = 0; i < squareUnitsArray.size(); i++) {
            if (squareUnitsArray.get(i).getX() == x[0] && squareUnitsArray.get(i).getY() == y[0]) {
                start = i;
                break;
            }
        }
        int end = 0;
        for (int i = start; i < squareUnitsArray.size(); i++) {
            if (squareUnitsArray.get(i).getX() == newX && squareUnitsArray.get(i).getY() == newY) {
                end = i;
                break;
            }
        }
        return end - start;
    }

    public void SnekBot() {
        if (applesEaten < GAME_UNITS * (3 / 5.0)) {
            int target;
            if (getSquareUnitNumber(x[0], y[0]) < getSquareUnitNumber(appleX, appleY))
                target = getSquareUnitNumber(appleX, appleY);
            else
                target = GAME_UNITS;

            int[] pathSkip = new int[4];
            pathSkip[0] = countSkip(x[0] + UNIT_SIZE, y[0]);//right
            pathSkip[1] = countSkip(x[0] - UNIT_SIZE, y[0]);//left
            pathSkip[2] = countSkip(x[0], y[0] - UNIT_SIZE);//up
            pathSkip[3] = countSkip(x[0], y[0] + UNIT_SIZE);//down
            char[] directions = {'R', 'L', 'U', 'D'};

            for (int i = 0; i < pathSkip.length - 1; i++) {
                for (int j = 0; j < pathSkip.length - i - 1; j++) {
                    if (pathSkip[j] > pathSkip[j + 1]) {
                        int temp = pathSkip[j];
                        pathSkip[j] = pathSkip[j + 1];
                        pathSkip[j + 1] = temp;

                        char temp2 = directions[j];
                        directions[j] = directions[j + 1];
                        directions[j + 1] = temp2;
                    }
                }
            }

            for (int i = 3; i >= 0; i--) {
                switch(directions[i]) {
                    case 'R':
                        int newX = x[0] + UNIT_SIZE;
                        if (getSquareUnitNumber(newX, y[0]) >= getSquareUnitNumber(x[0], y[0]) && getSquareUnitNumber(newX, y[0]) <= target && specialCheckCollision(newX, y[0])) {
                            direction = 'R';
                            return;
                        }
                        break;
                    case 'L':
                        int newX2 = x[0] - UNIT_SIZE;
                        if (getSquareUnitNumber(newX2, y[0]) >= getSquareUnitNumber(x[0], y[0]) && getSquareUnitNumber(newX2, y[0]) <= target && specialCheckCollision(newX2, y[0])) {
                            direction = 'L';
                            return;
                        }
                        break;
                    case 'U':
                        int newY = y[0] - UNIT_SIZE;
                        if (getSquareUnitNumber(x[0], newY) >= getSquareUnitNumber(x[0], y[0]) && getSquareUnitNumber(x[0], newY) <= target && specialCheckCollision(x[0], newY)) {
                            direction = 'U';
                            return;
                        }
                        break;
                    case 'D':
                        int newY2 = y[0] + UNIT_SIZE;
                        if (getSquareUnitNumber(x[0], newY2) >= getSquareUnitNumber(x[0], y[0]) && getSquareUnitNumber(x[0], newY2) <= target && specialCheckCollision(x[0], newY2)) {
                            direction = 'D';
                            return;
                        }
                        break;
                }
            }
        }
        for (int i = 0; i < squareUnitsArray.size() - 1; i++) {
            if (squareUnitsArray.get(i).getX() == x[0] && squareUnitsArray.get(i).getY() == y[0]) {
                SquareUnit next = squareUnitsArray.get(i + 1);
                if (next.getX() > x[0])
                    direction = 'R';
                else if (next.getX() < x[0])
                    direction = 'L';
                else if (next.getY() < y[0])
                    direction = 'U';
                else if (next.getY() > y[0])
                    direction = 'D';
                return;
            }
            if (i == squareUnitsArray.size() - 2) {
                SquareUnit next = squareUnitsArray.getFirst();
                if (next.getX() > x[0])
                    direction = 'R';
                else if (next.getX() < x[0])
                    direction = 'L';
                else if (next.getY() < y[0])
                    direction = 'U';
                else if (next.getY() > y[0])
                    direction = 'D';
                return;
            }
        }
    }

    public boolean checkWin() {
        if (bodyParts == GAME_UNITS) {
            running = false;
            timer.stop();
            return true;
        }
        return false;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if(running) {
            checkWin();
            if (!running)
                return;
            move();
            checkApple();
            checkCollisions();
            SnekBot();
        }
        repaint();
    }

    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch(e.getKeyCode()) {
                case KeyEvent.VK_LEFT, KeyEvent.VK_A:
                    if(direction != 'R') {
                        direction = 'L';
                    }
                    break;
                case KeyEvent.VK_RIGHT, KeyEvent.VK_D:
                    if(direction != 'L') {
                        direction = 'R';
                    }
                    break;
                case KeyEvent.VK_UP, KeyEvent.VK_W:
                    if(direction != 'D') {
                        direction = 'U';
                    }
                    break;
                case KeyEvent.VK_DOWN, KeyEvent.VK_S:
                    if(direction != 'U') {
                        direction = 'D';
                    }
                    break;
            }
        }
    }

    public void automaticGenerateCycle() {
        int rows = SCREEN_HEIGHT / UNIT_SIZE;
        int cols = SCREEN_WIDTH / UNIT_SIZE;
        boolean[][] visited = new boolean[rows][cols];

        // Start from top-left corner
        visited[0][0] = true;

        if (!findCycle(0, 0, visited, rows, cols)) {
            System.out.println("Failed to generate Hamiltonian Cycle.");
            System.exit(1);
        }
    }

    private boolean findCycle(int x, int y, boolean[][] visited, int rows, int cols) {
        if (squareUnitsArray.size() == rows * cols) {
            // Check if the last square connects to the first
            SquareUnit start = squareUnitsArray.getFirst();
            return Math.abs(start.getX() - x * UNIT_SIZE) + Math.abs(start.getY() - y * UNIT_SIZE) == UNIT_SIZE;
        }
        int[][] directions = {{0, 1}, {1, 0}, {0, -1}, {-1, 0}};//{0, 1}, {1, 0}, {0, -1}, {-1, 0} down, right, up, left
        for (int[] dir : directions) {
            int newX = x + dir[0];
            int newY = y + dir[1];

            if (isValidMove(newX, newY, visited, rows, cols)) {
                visited[newY][newX] = true;
                squareUnitsArray.add(new SquareUnit(newX * UNIT_SIZE, newY * UNIT_SIZE, squareUnitsArray.size() + 1));

                if (findCycle(newX, newY, visited, rows, cols))
                    return true;

                // Backtrack
                visited[newY][newX] = false;
                squareUnitsArray.removeLast();
            }
        }
        return false;
    }

    private boolean isValidMove(int x, int y, boolean[][] visited, int rows, int cols) {
        return x >= 0 && x < cols && y >= 0 && y < rows && !visited[y][x];
    }

    private void reNumberHamiltonianCycle(int xPos, int yPos) {
        ArrayList<SquareUnit> newSquareUnitsArray = new ArrayList<>();
        int index = getSquareUnitNumber(xPos, yPos) - 1;
        for (int i = 0; i < GAME_UNITS; i++) {
            SquareUnit squareUnit = squareUnitsArray.get(index);
            squareUnit.setN(newSquareUnitsArray.size() + 1);
            newSquareUnitsArray.add(squareUnit);
            index = (index + 1) % GAME_UNITS;
        }
        Collections.copy(squareUnitsArray, newSquareUnitsArray);
    }

    int xPos = 0, yPos = 0;
    private void moveUp(int times) {
        for (int i = 0; i < times; i++) {
            yPos -= UNIT_SIZE;
            squareUnitsArray.add(new SquareUnit(xPos, yPos, squareUnitsArray.size() + 1));
        }
    }

    private void moveDown(int times) {
        for (int i = 0; i < times; i++) {
            yPos += UNIT_SIZE;
            squareUnitsArray.add(new SquareUnit(xPos, yPos, squareUnitsArray.size() + 1));
        }
    }

    private void moveRight(int times) {
        for (int i = 0; i < times; i++) {
            xPos += UNIT_SIZE;
            squareUnitsArray.add(new SquareUnit(xPos, yPos, squareUnitsArray.size() + 1));
        }
    }

    private void moveLeft(int times) {
        for (int i = 0; i < times; i++) {
            xPos -= UNIT_SIZE;
            squareUnitsArray.add(new SquareUnit(xPos, yPos, squareUnitsArray.size() + 1));
        }
    }

    private void manualGenerateCycle1() {//for 800, 500
        moveDown(5);
        moveRight(3);
        moveUp(2);
        moveRight(8);
        moveDown(5);
        moveLeft(4);
        moveUp(1);
        moveRight(3);
        moveUp(3);
        moveLeft(6);
        moveDown(2);
        moveLeft(4);
        moveDown(3);
        moveRight(5);
        moveUp(1);
        moveLeft(4);
        moveUp(1);
        moveRight(4);
        moveUp(2);
        moveRight(4);
        moveDown(1);
        moveLeft(3);
        moveDown(3);
        moveRight(9);
        moveUp(3);
        moveLeft(1);
        moveDown(2);
        moveLeft(2);
        moveUp(1);
        moveRight(1);
        moveUp(1);
        moveLeft(1);
        moveUp(4);
        moveLeft(10);
        moveDown(2);
        moveLeft(1);
        moveUp(3);
        moveRight(12);
        moveDown(4);
        moveRight(2);
        moveUp(1);
        moveLeft(1);
        moveUp(1);
        moveRight(1);
        moveUp(1);
        moveLeft(1);
        moveUp(1);
        moveRight(1);
        moveUp(1);
        moveLeft(14);
    }
    private void manualGenerateCycle2() {//for 800, 500
        moveDown(7);
        moveRight(11);
        moveUp(1);
        moveLeft(10);
        moveUp(5);
        moveRight(4);
        moveDown(3);
        moveLeft(2);
        moveUp(1);
        moveRight(1);
        moveUp(1);
        moveLeft(2);
        moveDown(3);
        moveRight(10);
        moveDown(3);
        moveLeft(12);
        moveDown(1);
        moveRight(15);
        moveUp(9);
        moveLeft(8);
        moveDown(1);
        moveRight(7);
        moveDown(7);
        moveLeft(1);
        moveUp(4);
        moveLeft(7);
        moveUp(1);
        moveRight(7);
        moveUp(1);
        moveLeft(7);
        moveUp(2);
        moveLeft(5);
    }

    private void manualGenerateCycle3() {//for 1300, 700
        moveDown(13);
        moveRight(25);
        moveUp(1);
        moveLeft(24);
        moveUp(11);
        moveRight(23);
        moveDown(1);
        moveLeft(22);
        moveDown(9);
        moveRight(21);
        moveUp(1);
        moveLeft(20);
        moveUp(7);
        moveRight(19);
        moveDown(1);
        moveLeft(18);
        moveDown(5);
        moveRight(20);
        moveDown(2);
        moveRight(1);
        moveUp(3);
        moveLeft(20);
        moveUp(3);
        moveRight(11);
        moveDown(1);
        moveLeft(10);
        moveDown(1);
        moveRight(19);
        moveUp(1);
        moveLeft(8);
        moveUp(1);
        moveRight(8);
        moveUp(1);
        moveLeft(2);
        moveUp(1);
        moveRight(2);
        moveUp(3);
        moveLeft(24);
    }

    private void manualGenerateCycle4() {//1300, 700
        moveDown(5);
        moveRight(13);
        moveDown(1);
        moveLeft(13);
        moveDown(7);
        moveRight(25);
        moveUp(13);
        moveLeft(23);
        moveDown(3);
        moveRight(12);
        moveUp(1);
        moveLeft(11);
        moveUp(1);
        moveRight(21);
        moveDown(9);
        moveLeft(20);
        moveDown(1);
        moveRight(20);
        moveDown(1);
        moveLeft(23);
        moveUp(5);
        moveRight(1);
        moveDown(4);
        moveRight(1);
        moveUp(4);
        moveRight(1);
        moveDown(2);
        moveRight(19);
        moveUp(7);
        moveLeft(5);
        moveDown(1);
        moveRight(4);
        moveDown(5);
        moveLeft(17);
        moveUp(1);
        moveRight(16);
        moveUp(3);
        moveLeft(4);
        moveUp(2);
        moveLeft(2);
        moveDown(1);
        moveRight(1);
        moveDown(2);
        moveRight(4);
        moveDown(1);
        moveLeft(6);
        moveUp(1);
        moveRight(1);
        moveUp(1);
        moveLeft(14);
        moveUp(4);
    }
}