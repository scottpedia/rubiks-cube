package ca.vicpark;

import java.util.ArrayList;
import java.util.Random;

public class Cube implements Cloneable {

    public static final int AXIS_X = 0, AXIS_Y = 1, AXIS_Z = 2;
    public static final int DIR_C = 1, DIR_A = 0; // "C" stands for "clockwise", while "A" stands for "anti-clockwise".

    public static final int COLOR_FX0_0 = 0, COLOR_FY0_1 = 1, COLOR_FZ0_2 = 2, COLOR_FX1_3 = 3, COLOR_FY1_4 = 4, COLOR_FZ1_5 = 5;
    private static final int[] COLORS = {
            COLOR_FX0_0, COLOR_FY0_1, COLOR_FZ0_2, COLOR_FX1_3, COLOR_FY1_4, COLOR_FZ1_5
    };
    private static final int DEFAULT_RANDOM_STEP = 15;
    private ArrayList<String> history = new ArrayList<String>();

    private int[][][] data = {
            {
                    {0, 0, 0},
                    {0, 0, 0}, // Face X-axis, 0 (left)
                    {0, 0, 0}
            },
            {
                    {0, 0, 0},
                    {0, 0, 0}, // Face Y-axis, 0 (back)
                    {0, 0, 0}
            },
            {
                    {0, 0, 0},
                    {0, 0, 0}, // Face Z-axis, 0 (bottom)
                    {0, 0, 0}
            },
            {
                    {0, 0, 0},
                    {0, 0, 0}, // Face X-axis, 1 (right)
                    {0, 0, 0}
            },
            {
                    {0, 0, 0},
                    {0, 0, 0}, // Face Y-axis, 1 (front)
                    {0, 0, 0}
            },
            {
                    {0, 0, 0},
                    {0, 0, 0}, // Face Z-axis, 1 (up)
                    {0, 0, 0}
            }
    };

    public Cube() {
        simuReset();
    }

    public void simuReset() {
        for (int _i = 0; _i < 6; _i++) {
            for (int _ii = 0; _ii < 3; _ii++) {
                for (int _iii = 0; _iii < 3; _iii++) data[_i][_ii][_iii] = this.COLORS[_i];
            }
        }
        this.addHistory((this.history.size() + 1) + "-> Reset.");
    }

    public void randomize() { // using the default step number.
        randomize(this.DEFAULT_RANDOM_STEP);
    }

    public void randomize(int step) {
        if (step < 1) {
            System.err.println("The step number has to be positive. Exiting...");
            System.exit(1);
        }
        Random gen = new Random();
        for (int _i = 0; _i < step; _i++) {
            int _a = gen.nextInt(3), _l = gen.nextInt(3) + 1, _d = gen.nextInt(2);
            localOperation(_a, _l, _d, "Randomizing");
        }
    }

    public String toString() {
        String output = "";
        int _counter = 0;
        String[] notes = {
                "X0-Left",
                "Y0-Back",
                "Z0-bottom",
                "X1-Right",
                "Y2-Front",
                "Z1-up"
        };
        for (int[][] i : data) {
            output += notes[_counter++] + ":\n";
            for (int[] ii : i) {
                for (int iii : ii) {
                    output += String.valueOf(iii) + " ";
                }
                output += "\n";
            }
        }
        return output;
    }

    public int getColor(int axis, int face, int row, int number) { // axis is supplied with constants defined in the class, all other parameters start from 1 instead of 0.
        if (axis > 2 || axis < 0) {
            return -1; // returns -1 when parameters fail to pass tests.
        } else if (face != 0 && face != 1) {
            return -1;
        } else if (row < 1 || row > 6) {
            return -1;
        } else if (number < 1 || number > 3) {
            return -1;
        }
        return (face == 0) ? (data[axis][row - 1][number - 1]) : ((face == 1) ? data[axis + 3][row - 1][number - 1] : null);
    }

    public int setColor(int axis, int face, int row, int number, int color) { // the same as above(getColor)
        if (axis > 2 || axis < 0) {
            return -1; // returns -1 when parameters fail to pass tests.
        } else if (face != 0 && face != 1) {
            return -1;
        } else if (row < 1 || row > 6) {
            return -1;
        } else if (number < 1 || number > 3) {
            return -1;
        }

        data[(face == 0) ? axis : axis + 3][row - 1][number - 1] = color;
        return 0;
    }

    public Cube clonedOperation(int axis, int level, int direction) throws CloneNotSupportedException {
        Cube cloned = (Cube) this.clone();
        // Clone first, and then operate on the replicate.
        cloned.localOperation(axis, level, direction, "Cloned");

        return cloned;
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        Cube cloned = (Cube) super.clone();
        cloned.data = (int[][][]) data.clone();
        for (int _l = 0; _l < data.length; _l++) {
            cloned.data[_l] = (int[][]) data[_l].clone();
            for (int _ll = 0; _ll < data[_l].length; _ll++) {
                cloned.data[_l][_ll] = (int[]) data[_l][_ll].clone();
            }
        }
        cloned.history = (ArrayList<String>) history.clone();
        return cloned;
    }

    private final int[][] cacheFaceRotation(int axis, int face, int rotation){
        int[][] toReturn = new int[3][3];
        for(int _i = 0; _i < 3; _i++){
            for (int _ii = 0; _ii < 3; _ii++){
                if (rotation == DIR_C) {
                    toReturn[_i][_ii] = data[axis + ((face == 1) ? 3 : 0)][2-_ii][_i];
                    // 1,1 -> 3,1
                }else {
                    toReturn[_i][_ii] = data[axis + ((face == 1) ? 3 : 0)][_ii][2-_i];
                }
            }
        }
        return toReturn;
    }

    public final int localOperation(int axis, int level, int direction, String comment) {
        String errMsg = "";
        if (level < 1 || level > 3) {
            errMsg += "The \"level\" parameter can only be an integer ranging from 1 to 3.\n";
        }
        if (direction != 0 && direction != 1) {
            errMsg += "The \"direction\" parameter can only be either 1 or 0.\n";
        }
        if (axis < 0 || axis > 2) {
            errMsg += "The \"axis\" parameter can only be an integer ranging from 0 to 2.\n";
        }
        if (errMsg != "") {
            System.err.println(errMsg + "Please re-enter the correct parameter(s).");
            System.exit(1);
        }

        int[] upCached = {0, 0, 0}, downCached = {0, 0, 0}; // Cached values of the unchanged state.
        int[] leftCached = {0, 0, 0}, rightCached = {0, 0, 0};
        int[][] faceCached = {
            {0,0,0},
            {0,0,0},
            {0,0,0}
        };
        switch (axis) {
            case AXIS_X:
                switch (level) {
                    case 1:
                        for (int _i = 0; _i < 3; _i++) {
                            upCached[_i] = getColor(AXIS_Z, 1, 2 - _i + 1, 3); // verified
                            downCached[_i] = getColor(AXIS_Z, 0, 2 - _i + 1, 3);
                        }
                        for (int _i = 0; _i < 3; _i++) {
                            leftCached[_i] = getColor(AXIS_Y, 1, 2 - _i + 1, 3);
                            rightCached[_i] = getColor(AXIS_Y, 0, _i + 1, 1);
                        }

                        if (direction == 1) {
                            faceCached = this.cacheFaceRotation(axis,1,direction );
                            for (int _i = 0; _i < 3; _i++) {
                                for (int _ii = 0; _ii < 3; _ii++){
                                    data[axis+((level == 3)?0:3)][_i][_ii] = faceCached[_i][_ii];
                                }

                                setColor(AXIS_Z, 1, 2 - _i + 1, 3, leftCached[_i]);
                                setColor(AXIS_Y, 0, _i + 1, 1, upCached[_i]);
                                setColor(AXIS_Z, 0, 2 - _i + 1, 3, rightCached[_i]);
                                setColor(AXIS_Y, 1, 2 - _i + 1, 3, downCached[_i]);
                            }
                        } else {
                            faceCached = this.cacheFaceRotation(axis, 1, direction);
                            for (int _i = 0; _i < 3; _i++) {
                                for (int _ii = 0; _ii < 3; _ii++){
                                    data[axis+((level == 3)?0:3)][_i][_ii] = faceCached[_i][_ii];
                                }
                                setColor(AXIS_Z, 1, 2 - _i + 1, 3, rightCached[_i]);
                                setColor(AXIS_Y, 0, _i + 1, 1, downCached[_i]);
                                setColor(AXIS_Z, 0, 2 - _i + 1, 3, leftCached[_i]);
                                setColor(AXIS_Y, 1, 2 - _i + 1, 3, upCached[_i]);
                            }
                        }
                        break;
                    case 2:
                        for (int _i = 0; _i < 3; _i++) {
                            upCached[_i] = getColor(AXIS_Z, 1, 2 - _i + 1, 2);
                            downCached[_i] = getColor(AXIS_Z, 0, 2 - _i + 1, 2);
                        }
                        for (int _i = 0; _i < 3; _i++) {
                            leftCached[_i] = getColor(AXIS_Y, 1, 2 - _i + 1, 2);
                            rightCached[_i] = getColor(AXIS_Y, 0, _i + 1, 2);
                        }
                        if (direction == 1) {
                            for (int _i = 0; _i < 3; _i++) {
                                setColor(AXIS_Z, 1, 2 - _i + 1, 2, leftCached[_i]);
                                setColor(AXIS_Y, 0, _i + 1, 2, upCached[_i]);
                                setColor(AXIS_Z, 0, 2 - _i + 1, 2, rightCached[_i]);
                                setColor(AXIS_Y, 1, 2 - _i + 1, 2, downCached[_i]);
                            }
                        } else {
                            for (int _i = 0; _i < 3; _i++) {
                                setColor(AXIS_Z, 1, 2 - _i + 1, 2, rightCached[_i]);
                                setColor(AXIS_Y, 0, _i + 1, 2, downCached[_i]);
                                setColor(AXIS_Z, 0, 2 - _i + 1, 2, leftCached[_i]);
                                setColor(AXIS_Y, 1, 2 - _i + 1, 2, upCached[_i]);
                            }
                        }
                        break;
                    case 3:
                        for (int _i = 0; _i < 3; _i++) {
                            upCached[_i] = getColor(AXIS_Z, 1, 2 - _i + 1, 1);
                            downCached[_i] = getColor(AXIS_Z, 0, 2 - _i + 1, 1);
                        }
                        for (int _i = 0; _i < 3; _i++) {
                            leftCached[_i] = getColor(AXIS_Y, 1, 2 - _i + 1, 1);
                            rightCached[_i] = getColor(AXIS_Y, 0, _i + 1, 3);
                        }
                        if (direction == 1) {
                            faceCached = this.cacheFaceRotation(axis,0,direction );
                            for (int _i = 0; _i < 3; _i++) {
                                for (int _ii = 0; _ii < 3; _ii++){
                                    data[axis+((level == 3)?0:3)][_i][_ii] = faceCached[_i][_ii];
                                }

                                setColor(AXIS_Z, 1, 2 - _i + 1, 1, leftCached[_i]);
                                setColor(AXIS_Y, 0, _i + 1, 3, upCached[_i]);
                                setColor(AXIS_Z, 0, 2 - _i + 1, 1, rightCached[_i]);
                                setColor(AXIS_Y, 1, 2 - _i + 1, 1, downCached[_i]);
                            }
                        } else {
                            faceCached = this.cacheFaceRotation(axis,0,direction );
                            for (int _i = 0; _i < 3; _i++) {
                                for (int _ii = 0; _ii < 3; _ii++){
                                    data[axis+((level == 3)?0:3)][_i][_ii] = faceCached[_i][_ii];
                                }

                                setColor(AXIS_Z, 1, 2 - _i + 1, 1, rightCached[_i]);
                                setColor(AXIS_Y, 0, _i + 1, 3, downCached[_i]);
                                setColor(AXIS_Z, 0, 2 - _i + 1, 1, leftCached[_i]);
                                setColor(AXIS_Y, 1, 2 - _i + 1, 1, upCached[_i]);
                            }
                        }
                        break;
                }
                break;
            case AXIS_Y:// Y-1-as-front manipulations
                switch (level) {
                    case 1:
                        for (int _i = 0; _i < 3; _i++) {
                            upCached[_i] = getColor(AXIS_Z, 1, 3, _i + 1);
                            downCached[_i] = getColor(AXIS_Z, 0, 1, 2 - _i + 1);
                        }
                        for (int _i = 0; _i < 3; _i++) {
                            leftCached[_i] = getColor(AXIS_X, 0, 3 - _i, 3);
                            rightCached[_i] = getColor(AXIS_X, 1, _i + 1, 1); // verified
                        }
                        if (direction == 1) { // rotate right
                            faceCached = this.cacheFaceRotation(axis,1,direction );

                            for (int _i = 0; _i < 3; _i++) {
                                for (int _ii = 0; _ii < 3; _ii++){
                                    data[axis+((level == 3)?0:3)][_i][_ii] = faceCached[_i][_ii];
                                }

                                setColor(AXIS_Z, 1, 3, _i + 1, leftCached[_i]); // reversed
                                setColor(AXIS_X, 1, _i + 1, 1, upCached[_i]); // -reversed

                                setColor(AXIS_Z, 0, 1, 2 - _i + 1, rightCached[_i]); // non-reversed

                                setColor(AXIS_X, 0, 2 - _i + 1, 3, downCached[_i]); // non-reversed

                            }
                        } else { // rotate left
                            faceCached = this.cacheFaceRotation(axis,1,direction );
                            for (int _i = 0; _i < 3; _i++) {
                                for (int _ii = 0; _ii < 3; _ii++){
                                    data[axis+((level == 3)?0:3)][_i][_ii] = faceCached[_i][_ii];
                                }

                                setColor(AXIS_Z, 1, 3, _i + 1, rightCached[_i]); // reversed
                                setColor(AXIS_X, 1, _i + 1, 1, downCached[_i]); // reversed

                                setColor(AXIS_Z, 0, 1, 2 - _i + 1, leftCached[_i]); // non-reversed

                                setColor(AXIS_X, 0, 2 - _i + 1, 3, upCached[_i]); // non-reversed

                            }

                        }
                        break;
                    case 2:
                        for (int _i = 0; _i < 3; _i++) {
                            upCached[_i] = getColor(AXIS_Z, 1, 2, _i + 1);
                            downCached[_i] = getColor(AXIS_Z, 0, 2, 2 - _i + 1);
                        }
                        for (int _i = 0; _i < 3; _i++) {
                            leftCached[_i] = getColor(AXIS_X, 0, 3 - _i, 2);
                            rightCached[_i] = getColor(AXIS_X, 1, _i + 1, 2);
                        }
                        if (direction == 1) {
                            for (int _i = 0; _i < 3; _i++) {
                                setColor(AXIS_Z, 1, 2, _i + 1, leftCached[_i]); // reversed
                                setColor(AXIS_X, 1, _i + 1, 2, upCached[_i]); // -reversed

                                setColor(AXIS_Z, 0, 2, 2 - _i + 1, rightCached[_i]); // non-reversed
                                setColor(AXIS_X, 0, 2 - _i + 1, 2, downCached[_i]); // non-reversed
                            }
                        } else {
                            for (int _i = 0; _i < 3; _i++) {
                                setColor(AXIS_Z, 1, 2, _i + 1, rightCached[_i]); // reversed
                                setColor(AXIS_X, 1, _i + 1, 2, downCached[_i]); // reversed

                                setColor(AXIS_Z, 0, 2, 2 - _i + 1, leftCached[_i]); // non-reversed
                                setColor(AXIS_X, 0, 2 - _i + 1, 2, upCached[_i]); // non-reversed
                            }

                        }
                        break;
                    case 3:
                        for (int _i = 0; _i < 3; _i++) {
                            upCached[_i] = getColor(AXIS_Z, 1, 1, _i + 1);
                            downCached[_i] = getColor(AXIS_Z, 0, 3, 2 - _i + 1);
                        }
                        for (int _i = 0; _i < 3; _i++) {
                            leftCached[_i] = getColor(AXIS_X, 0, 3 - _i, 1);
                            rightCached[_i] = getColor(AXIS_X, 1, _i + 1, 3);
                        }
                        if (direction == 1) {
                            faceCached = this.cacheFaceRotation(axis,0,direction );
                            for (int _i = 0; _i < 3; _i++) {
                                for (int _ii = 0; _ii < 3; _ii++){
                                    data[axis+((level == 3)?0:3)][_i][_ii] = faceCached[_i][_ii];
                                }

                                setColor(AXIS_Z, 1, 1, _i + 1, leftCached[_i]); // reversed
                                setColor(AXIS_X, 1, _i + 1, 3, upCached[_i]); // reversed


                                setColor(AXIS_Z, 0, 3, 2 - _i + 1, rightCached[_i]); // non-reversed
                                setColor(AXIS_X, 0, 2 - _i + 1, 1, downCached[_i]); // non-reversed
                            }
                        } else {
                            faceCached = this.cacheFaceRotation(axis,0,direction );
                            for (int _i = 0; _i < 3; _i++) {
                                for (int _ii = 0; _ii < 3; _ii++){
                                    data[axis+((level == 3)?0:3)][_i][_ii] = faceCached[_i][_ii];
                                }

                                setColor(AXIS_Z, 1, 1, _i + 1, rightCached[_i]); // reversed
                                setColor(AXIS_X, 1, _i + 1, 3, downCached[_i]); // reversed

                                setColor(AXIS_Z, 0, 3, 2-_i + 1, leftCached[_i]); // non-reversed
                                setColor(AXIS_X, 0, 2 - _i + 1, 1, upCached[_i]); // non-reversed
                            }
                        }
                        break;
                }
                break;
            case AXIS_Z:// Z-1-as-front manipulations.
                switch (level) {
                    case 1:
                        for (int _i = 0; _i < 3; _i++) {
                            upCached[_i] = getColor(AXIS_Y, 0, 1, 2 - _i + 1);
                            downCached[_i] = getColor(AXIS_Y, 1, 1, 2 - _i + 1);
                        }
                        for (int _i = 0; _i < 3; _i++) {
                            leftCached[_i] = getColor(AXIS_X, 0, 1, 2 - _i + 1);
                            rightCached[_i] = getColor(AXIS_X, 1, 1, 2 - _i + 1);
                        }
                        if (direction == 1) {
                            faceCached = this.cacheFaceRotation(axis,1,direction );

                            for (int _i = 0; _i < 3; _i++) {
                                for (int _ii = 0; _ii < 3; _ii++){
                                    data[axis+((level == 3)?0:3)][_i][_ii] = faceCached[_i][_ii];
                                }

                                setColor(AXIS_Y, 0, 1, 2 - _i + 1, leftCached[_i]);
                                setColor(AXIS_X, 1, 1, 2 - _i + 1, upCached[_i]);
                                setColor(AXIS_Y, 1, 1, 2 - _i + 1, rightCached[_i]);
                                setColor(AXIS_X, 0, 1, 2 - _i + 1, downCached[_i]);
                            }
                        } else {
                            faceCached = this.cacheFaceRotation(axis,1,direction );
                            for (int _i = 0; _i < 3; _i++) {
                                for (int _ii = 0; _ii < 3; _ii++){
                                    data[axis+((level == 3)?0:3)][_i][_ii] = faceCached[_i][_ii];
                                }

                                setColor(AXIS_Y, 0, 1, 2 - _i + 1, rightCached[_i]);
                                setColor(AXIS_X, 1, 1, 2 - _i + 1, downCached[_i]);
                                setColor(AXIS_Y, 1, 1, 2 - _i + 1, leftCached[_i]);
                                setColor(AXIS_X, 0, 1, 2 - _i + 1, upCached[_i]);
                            }
                        }
                        break;
                    case 2:
                        for (int _i = 0; _i < 3; _i++) {
                            upCached[_i] = getColor(AXIS_Y, 0, 2, 2 - _i + 1);
                            downCached[_i] = getColor(AXIS_Y, 1, 2, 2 - _i + 1);
                        }
                        for (int _i = 0; _i < 3; _i++) {
                            leftCached[_i] = getColor(AXIS_X, 0, 2, 2 - _i + 1);
                            rightCached[_i] = getColor(AXIS_X, 1, 2, 2 - _i + 1);
                        }
                        if (direction == 1) {
                            for (int _i = 0; _i < 3; _i++) {
                                setColor(AXIS_Y, 0, 2, 2 - _i + 1, leftCached[_i]);
                                setColor(AXIS_X, 1, 2, 2 - _i + 1, upCached[_i]);
                                setColor(AXIS_Y, 1, 2, 2 - _i + 1, rightCached[_i]);
                                setColor(AXIS_X, 0, 2, 2 - _i + 1, downCached[_i]);
                            }
                        } else {
                            for (int _i = 0; _i < 3; _i++) {
                                setColor(AXIS_Y, 0, 2, 2 - _i + 1, rightCached[_i]);
                                setColor(AXIS_X, 1, 2, 2 - _i + 1, downCached[_i]);
                                setColor(AXIS_Y, 1, 2, 2 - _i + 1, leftCached[_i]);
                                setColor(AXIS_X, 0, 2, 2 - _i + 1, upCached[_i]);
                            }
                        }
                        break;
                    case 3:
                        for (int _i = 0; _i < 3; _i++) {
                            upCached[_i] = getColor(AXIS_Y, 0, 3, 2 - _i + 1);
                            downCached[_i] = getColor(AXIS_Y, 1, 3, 2 - _i + 1);
                        }
                        for (int _i = 0; _i < 3; _i++) {
                            leftCached[_i] = getColor(AXIS_X, 0, 3, 2 - _i + 1);
                            rightCached[_i] = getColor(AXIS_X, 1, 3, 2 - _i + 1);
                        }
                        if (direction == 1) {
                            faceCached = this.cacheFaceRotation(axis,0,direction );
                            for (int _i = 0; _i < 3; _i++) {
                                for (int _ii = 0; _ii < 3; _ii++){
                                    data[axis+((level == 3)?0:3)][_i][_ii] = faceCached[_i][_ii];
                                }

                                setColor(AXIS_Y, 0, 3, 2 - _i + 1, leftCached[_i]);
                                setColor(AXIS_X, 1, 3, 2 - _i + 1, upCached[_i]);
                                setColor(AXIS_Y, 1, 3, 2 - _i + 1, rightCached[_i]);
                                setColor(AXIS_X, 0, 3, 2 - _i + 1, downCached[_i]);
                            }
                        } else {
                            faceCached = this.cacheFaceRotation(axis,0,direction );
                            for (int _i = 0; _i < 3; _i++) {
                                for (int _ii = 0; _ii < 3; _ii++){
                                    data[axis+((level == 3)?0:3)][_i][_ii] = faceCached[_i][_ii];
                                }

                                setColor(AXIS_Y, 0, 3, 2 - _i + 1, rightCached[_i]);
                                setColor(AXIS_X, 1, 3, 2 - _i + 1, downCached[_i]);
                                setColor(AXIS_Y, 1, 3, 2 - _i + 1, leftCached[_i]);
                                setColor(AXIS_X, 0, 3, 2 - _i + 1, upCached[_i]);
                            }
                        }
                        break;
                }
                break;
        }
        addHistory(
                String.valueOf(history.size() + 1) +
                        "-> Axis: " + String.valueOf((char) (88 + axis)) +
                        " Level: " + level +
                        " Direction: " + direction +
                        ((comment == null) ? (new String()) : (" Comment: " + comment)));
        return 0;
    }

    public void addHistory(String step) {
        this.history.add(step);
    }

    public Object[] getHistory() {
        return history.toArray();
    }

    public void clearHistory() {
        this.history.clear();
    }

    public boolean isRestored() {
        for (int[][] _o : data) {
            int tempValue = -1;
            for (int[] _t : _o) {
                for (int _tr : _t) {
                    if (tempValue == -1) {
                        tempValue = _tr;
                    }
                    if (tempValue != _tr) {
                        return false;
                    } else {
                        tempValue = _tr;
                    }
                }
            }
        }
        return true;
    }
}