package ca.vicpark;

public class Main {

    public static void main(String[] args) throws CloneNotSupportedException {
        // write your code here
        int randomizeSteps = 7;
        int numberOfThreads = 1000;
        Cube[] cubes = new Cube[numberOfThreads];
        try {
            Cube instance = new Cube();
            CubePreview preview = new CubePreview("Cube Preview");
            preview.setLinkedCube(instance);
            instance.randomize(randomizeSteps);
            preview.update();
            for (int i = 0; i < cubes.length; i++) {
                cubes[i] = (Cube) instance.clone();
            }
            int rounds = 0;
            while (true) {
                for (int i = 0; i < cubes.length; i++) {
                    cubes[i].randomize(1);
                    if (cubes[i].isRestored()) {
                        System.out.println("Restoration Achieved, after " + rounds + " rounds, " + i + " tries.");
                        for (Object _h : cubes[i].getHistory()) System.out.println(_h);
                        throw new Exception();
                    }
                }
                if (rounds % 5000 == 0) {
                    System.out.println(rounds + " have passed, no restoration yet...");
                }
                if (rounds % 100 == 0 && rounds != 0) {
                    for (int i = 0; i < cubes.length; i++) {
                        cubes[i] = (Cube) instance.clone();
                    }
                    System.gc();
                }
                rounds++;
            }
        } catch (Exception e) {
            System.exit(0);
        }
    }
}