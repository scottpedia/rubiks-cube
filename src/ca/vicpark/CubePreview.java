package ca.vicpark;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class CubePreview extends JFrame {

    public static final Color[] colorMapping = {
            Color.yellow, // x0 0
            Color.green, // y0 1
            Color.orange, // z0 2
            Color.white, // x1 3
            Color.blue, // y1 4
            Color.red, // z1 5
    };

    BufferedImage img = null, imgInverted = null, axisIndicator = null;
    private static final int[][] PARAMS =
            {
                    {140, 162, 182, 137, 182, 186, 140, 211, (45), (-27)},//x
                    {3, 84, 45, 109, 45, 158, 3, 133, (46), (26)},//y
                    {137, 1, 179, 26, 137, 51, 94, 26, (46), (27)}//z
            };

    private int[][][][][] squareLocations = new int[6][3][3][4][2];
    // each array of 4 integers stores the location of the 4 corners of each sub-face, counted in a clockwise order.

    Cube linkedCube = null;
    ArrayList<ColoredPolygon> blockFaces = new ArrayList<ColoredPolygon>();
    JPanel drawingBoard = null;

    public void setLinkedCube(Cube cube) {
        this.linkedCube = cube;
    }

    public void update() {
        if (this.linkedCube == null) {
            return;
        }
        for (int face = 0; face < 6; face++) {
            for (int row = 0; row < 3; row++) {
                for (int number = 0; number < 3; number++) {
                    this.setColor(face / 2, face % 2, row + 1, number + 1,
                            this.colorMapping[linkedCube.getColor(face / 2, face % 2, row + 1, number + 1)]);
                }
            }
        }
    }

    protected class ColoredPolygon extends Polygon {
        Color color = null;

        public ColoredPolygon(int[] xpoints, int[] ypoints, int npoints, Color color) {
            super(xpoints, ypoints, npoints);
            this.color = color;
        }

        public void setColor(Color c) {
            this.color = c;
        }

        public Color getColor() {
            return this.color;
        }
    }

    private void generateSquareLocations() throws InterruptedException {
        for (int _block = 0; _block < 3; _block++) {
            int[] tempParams = PARAMS[_block].clone();
            for (int _row = 0; _row < 3; _row++) {
                if (_row != 0) {
                    for (int _corner = 0; _corner < 8; _corner++) {
                        if (_block == 2) { // z
                            tempParams[_corner] += (_corner % 2) * 27;//y
                            tempParams[_corner] += ((_corner + 1) % 2) * -45;//x
                        } else if (_block == 1) { // y
                            tempParams[_corner] += (_corner % 2) * 53;
                        } else if (_block == 0) { // x
                            tempParams[_corner] += (_corner % 2) * 53;
                        }
                    }
                }
                for (int _number = 0; _number < 3; _number++) {
                    for (int _x = 0; _x < 8; _x++) { // for one block.
                        squareLocations[3 + _block][_row][_number][_x / 2][_x % 2] = (_x % 2 == 0) ? (tempParams[_x] + tempParams[8] * _number) : (tempParams[_x] + tempParams[9] * _number);
                        squareLocations[_block][_row][_number][_x / 2][_x % 2] = (_x % 2 == 0) ? (276 - (tempParams[_x] + tempParams[8] * _number)) : (320 - (tempParams[_x] + tempParams[9] * _number));
                    }
                }
            }
        }
    }

    private int[] getRealLocationOnImage(int x, int y) {
        return new int[]{x + 182, y + 80};
    }

    public CubePreview(String title) throws HeadlessException, InterruptedException {
        super(title);
        setSize(640, 480);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setDefaultLookAndFeelDecorated(true);
        setMaximumSize(new Dimension(640, 480));
        setMinimumSize(new Dimension(640, 480));
        setAlwaysOnTop(true);

        try {
            img = ImageIO.read(new File("img/rcube.jpg"));
            imgInverted = ImageIO.read(new File("img/rcube-inverted.jpg"));
            axisIndicator = ImageIO.read(new File("img/axis.jpg"));
        } catch (IOException e) {
            System.err.println("Can't read the specified image.");
        }

        generateSquareLocations();

        this.drawingBoard = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.drawImage(img, 22, 100, null); // 182 originally
                g2d.drawImage(imgInverted, 322, 100, null);
                g2d.drawImage(axisIndicator, 288, 15, null);

                if (blockFaces.isEmpty()) {
                    for (int _i = 0; _i < 6; _i++) {
                        for (int _ii = 0; _ii < 3; _ii++) {
                            for (int _iii = 0; _iii < 3; _iii++) {
                                int[] xPoints = new int[4], yPoints = new int[4];
                                for (int point = 0; point < 4; point++) {
                                    if (_i < 3) {
                                        if (_i == 2) {
                                            xPoints[point] = squareLocations[_i][_ii][2 - _iii][point][0] + 322;
                                            yPoints[point] = squareLocations[_i][_ii][2 - _iii][point][1] + 100;
                                        } else {
                                            xPoints[point] = squareLocations[_i][2 - _ii][_iii][point][0] + 322;
                                            yPoints[point] = squareLocations[_i][2 - _ii][_iii][point][1] + 100;
                                        }
                                    } else {
                                        xPoints[point] = squareLocations[_i][_ii][_iii][point][0] + 22;
                                        yPoints[point] = squareLocations[_i][_ii][_iii][point][1] + 100;
                                    }
                                }
                                ColoredPolygon polygon = new ColoredPolygon(xPoints, yPoints, 4, Color.orange);
                                blockFaces.add(polygon);
                                g2d.setPaint(Color.orange);
                                g2d.fillPolygon(polygon);
                            }
                        }
                    }
                }
                for (ColoredPolygon polygon : blockFaces) {
                    g2d.setPaint(polygon.getColor());
                    g2d.fillPolygon(polygon);
                }
            }
        };

        add(this.drawingBoard);
        setVisible(true);
        while (blockFaces.isEmpty()) {
            Thread.sleep(200);
        }
    }

    public void setColor(int axis, int face, int row, int number, Color color) {
        if (!blockFaces.isEmpty()) {
            int tempNum = (axis + face * 3) * 9 + (row - 1) * 3 + number - 1;
            blockFaces.get(tempNum).setColor(color);
            drawingBoard.repaint();
        }
    }

    public static void main(String[] args) {
        try {
            CubePreview cp = new CubePreview("Cube Preview");
            Cube cube = new Cube();
            cp.setLinkedCube(cube);
            cp.update();

            Scanner scanner = new Scanner(System.in);
            int axis, row, direction;
            System.out.println("Scanner set up correctly. Start entering values:");
            while (scanner.hasNextLine()) {
                String input = scanner.nextLine();
                if(input.equals("reset")){
                    System.out.println("to reset...");
                    cube.simuReset();
                }else {
                    String[] values = input.split(",");
                    axis = Integer.valueOf(values[0]);
                    row = Integer.valueOf(values[1]);
                    direction = Integer.valueOf(values[2]);
                    cube.localOperation(axis, row, direction, "Viewer Manual");
                }
                cp.update();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
