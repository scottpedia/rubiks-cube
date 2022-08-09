package ca.vicpark;

import java.util.ArrayList;

public class Main {

    public static void main(String[] args) throws CloneNotSupportedException {
        // write your code here
        Cube instance = new Cube();
        instance.randomize(4);
//        instance.localOperation(Cube.AXIS_Z,3,0,"manual");
        System.out.println("Randomized cube:\n" + instance.toString());

        ArrayList<Cube> latestGen = new ArrayList<Cube>(), previousGen = new ArrayList<Cube>();
        previousGen.add(instance);
        int _counter = 0;
        try {
            do {
                for (Cube c : previousGen) { // iterate the 3x3x2=18 possibilities upon each and every Cube setting from the previous Gen.
                    for (int axis = 0; axis < 3; axis++) {
                        for (int level = 1; level < 4; level++) {
                            for (int direction = 0; direction < 2; direction++) {
                                Cube cloned = c.clonedOperation(axis, level, direction);
                                if (cloned.isRestored()) {
                                    for (Object _o: cloned.getHistory()) System.out.println(_o);
                                    System.out.println("Restoration Achieved.");
                                    throw new Exception("Restoration Achieved.");
                                } else {
                                    latestGen.add(cloned);
                                }
                            }
                        }
                    }
                }
                System.out.println(++_counter + " round finished, no restoration yet.");
                previousGen = (ArrayList<Cube>) latestGen.clone();
                latestGen.clear();
                System.gc();
            } while (true);
        }catch (Exception e){
            System.exit(0);
        }
    }
}