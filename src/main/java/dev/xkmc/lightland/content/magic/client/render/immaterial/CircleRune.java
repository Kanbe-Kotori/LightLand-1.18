package dev.xkmc.lightland.content.magic.client.render.immaterial;

import java.util.ArrayList;
import java.util.Random;

public class CircleRune {

    public static final int WIDTH = 6;
    public static final int HEIGHT = 8;
    public static final int WEIGHT_MIN = 11;
    public static final int WEIGHT_MAX = 21;

    private static int SOLID = 0xFFFFFFFF;
    private static int TIP = 0x99FFFFFF;

    public boolean[][] value = new boolean[HEIGHT][WIDTH];
    public Random random = new Random();

    private CircleRune() {
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                value[i][j] = false;
            }
        }
    }

    public int[][] getBitmap(int color) {
        int[][] bmp = new int[WIDTH][HEIGHT];

        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                if (value[i][j])
                    bmp[j][i] = (countNeighbours(j, i, 1) == 1 ? TIP : SOLID) | color;
                else
                    bmp[j][i] = 0;
            }
        }

        return bmp;
    }


    public static CircleRune create() {
        CircleRune rune = new CircleRune();
        int w = 0;
        do {
            if (rune.coin( 5/7D ))
                rune.random1();
            else
                rune.random2();

            w = rune.getWeight();

        } while (w < WEIGHT_MIN || w > WEIGHT_MAX || !rune.isConnected( w ));
        return rune;
    }

    private void random1() {
        value[0][0] = coin( 4/5D );
        value[0][2] = coin( 4/5D );
        value[0][4] = coin( 4/5D );

        value[3][0] = coin( 4/5D );
        value[3][2] = coin( 4/5D );
        value[3][4] = coin( 4/5D );

        value[6][0] = coin( 4/5D );
        value[6][2] = coin( 4/5D );
        value[6][4] = coin( 2/5D );

        value[0][1] = (value[0][0] && value[0][2] && coin( 3/4D ));
        value[0][3] = (value[0][2] && value[0][4] && coin( 3/4D ));

        value[1][0] = value[2][0] = (value[0][0] && value[3][0] && coin( 2/3D ));
        value[1][2] = value[2][2] = (value[0][2] && value[3][2] && coin( 1/4D ));
        value[1][4] = value[2][4] = (value[0][4] && value[3][4] && coin( 4/5D ));

        value[3][1] = (value[3][0] && value[3][2] && coin( 3/4D ));
        value[3][3] = (value[3][2] && value[3][4] && coin( 2/5D ));

        value[4][0] = value[5][0] = (value[3][0] && value[6][0] && coin( 3/4D ));
        value[4][2] = value[5][2] = (value[3][2] && value[6][2] && coin( 2/4D ));
        value[4][4] = value[5][4] = (value[3][4] && value[6][4] && coin( 3/4D ));

        value[6][1] = (value[6][0] && value[6][2] && coin( 4/5D ));
        value[6][3] = (value[6][2] && value[6][4] && coin( 4/5D ));

        if (value[4][2] && (value[3][1] != value[3][3]))
            value[5][2] = false;
    }

    private void random2() {
        value[0][0] = coin( 1/2D );
        value[0][2] = coin( 1/2D );
        value[0][4] = coin( 1/2D );

        value[2][0] = coin( 3/4D );
        value[2][2] = coin( 3/4D );
        value[2][4] = coin( 3/4D );

        value[4][0] = coin( 1/2D );
        value[4][2] = coin( 1/2D );
        value[4][4] = coin( 1/2D );

        value[6][0] = coin( 1/2D );
        value[6][2] = coin( 1/2D );
        value[6][4] = coin( 1/2D );

        value[0][1] = (value[0][0] && value[0][2] && coin( 1/4D ));
        value[0][3] = (value[0][2] && value[0][4] && coin( 1/4D ));

        value[1][0] = (value[0][0] && value[2][0] && coin( 3/4D ));
        value[1][2] = (value[0][2] && value[2][2] && coin( 3/4D ));
        value[1][4] = (value[0][4] && value[2][4] && coin( 3/4D ));

        value[2][1] = (value[2][0] && value[2][2] && coin( 3/4D ));
        value[2][3] = (value[2][2] && value[2][4] && coin( 3/4D ));

        value[3][0] = (value[2][0] && value[4][0] && coin( 3/4D ));
        value[3][2] = (value[2][2] && value[4][2] && coin( 1/2D ));
        value[3][4] = (value[2][4] && value[4][4] && coin( 3/4D ));

        value[4][1] = value[4][2];
        value[4][3] = value[4][2];

        value[5][0] = (value[4][0] && value[6][0] && coin( 3/4D ));
        value[5][2] = (value[4][2] && value[6][2] && coin( 1/2D ));
        value[5][4] = (value[4][4] && value[6][4] && coin( 3/4D ));

        value[6][1] = (value[6][0] && value[6][2] && coin( 1/4D ));
        value[6][3] = (value[6][2] && value[6][4] && coin( 1/4D ));
    }

    private boolean isConnected(int weight) {

        // Checking horizontal bounds
        var left = false;
        var right = false;
        for (int i = 0; i < HEIGHT; i++) {
            left = left || value[i][0];
            right = right || value[i][4];
        }
        if (!left || !right)
            return false;

        // Checking vertical bounds
        var top = false;
        var bottom = false;
        for (int j = 0; j < WIDTH; j++) {
            top = top || value[0][j];
            bottom = bottom || value[6][j];
        }
        if (!top || !bottom)
            return false;

        // Searching for dots
        int dotX = -1;
        int dotY = -1;
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                if (value[i][j] && countNeighbours(j, i, 1) == 0) {
                    // It's a dot
                    if (dotX == -1) {
                        if (countNeighbours(j, i, 2) == 0)
                            // The dot is too far from other strokes
                            return false;
                        // It's the first dot
                        dotX = j;
                        dotY = i;
                    } else
                        // There is more than 1 dot
                        return false;
                }
            }
        }

        int x = 0, y = 0;
        // Searching for a starting point to fill
        // which is not a dot
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                if (value[i][j] && (i != dotY || j != dotX)) {
                    x = j;
                    y = i;
                    break;
                }
            }
        }

        ArrayList<Integer> checked = new ArrayList();
        int a = fill(x, y, checked);

        return (a == weight || a == weight - 1);
    }

    private int fill(int x, int y, ArrayList<Integer> checked) {
        var index = x + y * WIDTH;
        if (!value[y][x] || checked.indexOf( index ) != -1)
            return 0;

        checked.add(index);

        var a = 1;
        if (x > 0)
            a += fill(x - 1, y, checked);
        if (x < WIDTH-1)
            a += fill(x + 1, y, checked);
        if (y > 0)
            a += fill(x, y - 1, checked);
        if (y < HEIGHT-1)
            a += fill(x, y + 1, checked);

        return a;
    }

    private int getWeight(){
        var w = 0;
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                if(value[i][j])
                    w++;
            }
        }
        return w;
    }

    public int diff(CircleRune another) {
        var r = 0;

        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                if(value[i][j] != another.value[i][j])
                    r++;
            }
        }

        return r;
    }

    private int countNeighbours(int x, int y, int r) {
        var n = 0;

        if (x > r-1 && value[y][x-r])		n++;
        if (x < WIDTH-r && value[y][x+r])	n++;
        if (y > r-1 && value[y-r][x])		n++;
        if (y < HEIGHT-r && value[y+r][x])	n++;

        return n;
    }


    private boolean coin(double chance) {
        return (random.nextDouble() < chance);
    }
}
