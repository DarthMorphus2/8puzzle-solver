import java.util.Arrays;
import java.util.HashMap;
import java.util.Stack;
import java.util.PriorityQueue;
import java.util.Scanner;

/*
 * A simple implementation of A* to solve the 8-puzzle problem
 * Parses user input as a string of 9 digits (0-8) where 0 represents the hole
 * Example input: "123045678" refers to this state:
 * 1 2 3
 * 0 4 5
 * 6 7 8
 */



/*
 * State class to represent each state of the 8-puzzle
 * with references to neighboring states (up, down, left, right)
 * and the current position of the tiles
 */
class State { 
    private int[] up;
    private int[] down;
    private int[] left;
    private int[] right;
    private int[] values;

    public State(int[] values) {
        this.values = values;
        this.up = null;
        this.down = null;
        this.left = null;
        this.right = null;
    }

    public int[] getUp() {
        return up;
    }

    public void setUp(int[] up) {
        this.up = up;
    }

    public int[] getDown() {
        return down;
    }

    public void setDown(int[] down) {
        this.down = down;
    }

    public int[] getLeft() {
        return left;
    }

    public void setLeft(int[] left) {
        this.left = left;
    }

    public int[] getRight() {
        return right;
    }

    public void setRight(int[] right) {
        this.right = right;
    }

    public int[] getValues() {
        return values;
    }

}

/**
 * Main class to solve puzzle
 */
public class Puzzle {
    static HashMap<String, State> visited;
    public static void main(String[] args) {

        // Read input from user
        Scanner sc = new Scanner(System.in);

        System.out.print("Enter start position (e.g. 012345678): ");
        String start = sc.nextLine().trim();
        int[] arr1 = parseToArray(start);

        System.out.print("Enter goal position (e.g. 123456780): ");
        String goal = sc.nextLine().trim();
        int[] arr2 = parseToArray(goal);

        sc.close();


        // Initialise hashmap (used to store states, so that we can avoid revisiting them)
        visited = new HashMap<String, State>();

        // Carry out search
        Stack<int[]> result = AStarSearch(arr1, arr2);

        // If no solution found
        if (result == null) {
            System.out.println("No solution found.");
            return;
        }

        // If solution found, prints out steps
        int step = 1;
        while (!result.isEmpty()) {
            int[] state = result.pop();

            if (step == 1) {
                System.out.println("Initial State:");
                step++;
            } else System.out.println("Step " + step++ + ":");

            for (int i = 0; i < 9; i++) {
                if (state[i] == 0)
                    System.out.print("  ");
                else
                    System.out.print(state[i] + " ");

                if ((i + 1) % 3 == 0) System.out.println();
            }
        }
    }

    /* Parse user input into an array */
    public static int[] parseToArray(String s) {
        // Must be exactly 9 characters long
        if (s.length() != 9)
            throw new IllegalArgumentException("State must contain exactly 9 digits (0–8).");

        int[] result = new int[9];
        boolean[] seen = new boolean[9]; 

        for (int i = 0; i < 9; i++) {
            char c = s.charAt(i);
            if (!Character.isDigit(c))
                throw new IllegalArgumentException("State can only contain digits 0–8.");
            
            int value = Character.getNumericValue(c);
            if (value < 0 || value > 8)
                throw new IllegalArgumentException("Each tile must be between 0 and 8.");

            if (seen[value])
                throw new IllegalArgumentException("Duplicate digit '" + value + "' found in state.");

            seen[value] = true;
            result[i] = value;
        }

        // Verify all digits 0–8 are present
        for (int i = 0; i < 9; i++) {
            if (!seen[i])
                throw new IllegalArgumentException("Missing digit '" + i + "' in state.");
        }

        return result;
    }

    /* Helper function, swaps two elements */
    private static int[] swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
        return arr;
    }

    /* Generates a state class from the array */
    private static State generateState(int[] arr) {
        State temp = new State(arr.clone());
        String key = Arrays.toString(arr);
        int zero = indexOf(arr, 0);
        visited.put(key, temp);
        switch (zero) {
            case 0:
                temp.setDown(swap(arr.clone(), 0, 3));
                temp.setRight(swap(arr.clone(), 0, 1));
                break;
            case 1:
                temp.setDown(swap(arr.clone(), 1, 4));
                temp.setLeft(swap(arr.clone(), 1, 0));
                temp.setRight(swap(arr.clone(), 1, 2));
                break;
            case 2:
                temp.setDown(swap(arr.clone(), 2, 5));
                temp.setLeft(swap(arr.clone(), 2, 1));
                break;
            case 3:
                temp.setUp(swap(arr.clone(), 3, 0));
                temp.setDown(swap(arr.clone(), 3, 6));
                temp.setRight(swap(arr.clone(), 3, 4));
                break;
            case 4:
                temp.setUp(swap(arr.clone(), 4, 1));
                temp.setDown(swap(arr.clone(), 4, 7));
                temp.setLeft(swap(arr.clone(), 4, 3));
                temp.setRight(swap(arr.clone(), 4, 5));
                break;
            case 5:
                temp.setUp(swap(arr.clone(), 5, 2));
                temp.setDown(swap(arr.clone(), 5, 8));
                temp.setLeft(swap(arr.clone(), 5, 4));
                break;
            case 6:
                temp.setUp(swap(arr.clone(), 6, 3));
                temp.setRight(swap(arr.clone(), 6, 7));
                break;
            case 7:
                temp.setUp(swap(arr.clone(), 7, 4));
                temp.setLeft(swap(arr.clone(), 7, 6));
                temp.setRight(swap(arr.clone(), 7, 8));
                break;
            case 8:
                temp.setUp(swap(arr.clone(), 8, 5));
                temp.setLeft(swap(arr.clone(), 8, 7));
                break;
            default:
                break;
        }
        return temp;
    }

    /* Returns the index of a value in an array */
    private static int indexOf(int[] arr, int value) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == value) return i;
        }
        return -1;
    }

    /* Calculates the Manhattan distance between two states
     * (the sum of the distances of each tile from its goal position)
     */
    public static int manhattanDistance(int[] current, int[] goal) {
        int distance = 0;
        for (int tile = 1; tile <= 8; tile++) { // skip 0 (the hole)
            int goalIndex = indexOf(goal, tile);
            int currIndex = indexOf(current, tile);

            int goalRow = goalIndex / 3;
            int goalCol = goalIndex % 3;
            int currRow = currIndex / 3;
            int currCol = currIndex % 3;

            distance += Math.abs(goalRow - currRow) + Math.abs(goalCol - currCol);
        }
        return distance;
    }

    /* A* search algorithm */
    @SuppressWarnings("unchecked")
    public static Stack<int[]> AStarSearch(int[] initial, int[] goal) {
        //Initialise stack and priority queue
        Stack<int[]> path = new Stack<int[]>();
        PriorityQueue<Stack<int[]>> queue = new PriorityQueue<>(
            (a, b) -> {
                int[] topA = a.peek();
                int[] topB = b.peek();
                int fA = manhattanDistance(topA, goal) + a.size();
                int fB = manhattanDistance(topB, goal) + b.size();
                return Integer.compare(fA, fB);
            }
        );

        // Push starting point onto queue
        path.push(initial);
        queue.add(path);

        while (!queue.isEmpty()) {
            Stack<int[]> currentPath = queue.poll();
            int[] current = currentPath.peek();
            String key = Arrays.toString(current);

            // If already visited, skip
            if (visited.containsKey(key)) continue;

            // Else, generate state (and add to visited)
            State currentState = generateState(current);

            // Check if goal reached
            if (Arrays.equals(current, goal)) {
                reverseStack(currentPath);
                return currentPath;
            }

            // Explore neighbours
            // If not visited, clone current path, push new state and add to queue
            // If visited, skip
            if (currentState.getUp() != null) {
                String nextKey = Arrays.toString(currentState.getUp());
                if (!visited.containsKey(nextKey)) {
                    Stack<int[]> newPath = (Stack<int[]>) currentPath.clone();
                    newPath.push(currentState.getUp().clone());
                    queue.add(newPath);
                }
            }
            if (currentState.getDown() != null) {
                String nextKey = Arrays.toString(currentState.getDown());
                if (!visited.containsKey(nextKey)) {
                    Stack<int[]> newPath = (Stack<int[]>) currentPath.clone();
                    newPath.push(currentState.getDown().clone());
                    queue.add(newPath);
                }
            }
            if (currentState.getLeft() != null) {
                String nextKey = Arrays.toString(currentState.getLeft());
                if (!visited.containsKey(nextKey)) {
                    Stack<int[]> newPath = (Stack<int[]>) currentPath.clone();
                    newPath.push(currentState.getLeft().clone());
                    queue.add(newPath);
                }
            }
            if (currentState.getRight() != null) {
                String nextKey = Arrays.toString(currentState.getRight());
                if (!visited.containsKey(nextKey)) {
                    Stack<int[]> newPath = (Stack<int[]>) currentPath.clone();
                    newPath.push(currentState.getRight().clone());
                    queue.add(newPath);
                }
            }
        }
        return null;
    }

    /* Helper function, reverses a stack */
    @SuppressWarnings("unchecked")
    public static void reverseStack(Stack<int[]> stack) {
        Stack<int[]> temp = (Stack<int[]>) stack.clone();
        stack.clear();
        while (!temp.isEmpty()) {
            stack.push(temp.pop());
        }

    }
}


