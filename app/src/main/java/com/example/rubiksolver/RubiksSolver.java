package com.example.rubiksolver;

import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RubiksSolver {

    public static String TAG = RubiksSolver.class.getSimpleName();

    final static int BLANK = 0;
    final static int RED = 1;
    final static int WHITE = 2;
    final static int YELLOW = 3;
    final static int GREEN = 4;
    final static int BLUE = 5;
    final static int ORANGE = 6;

    final static List<Node> MASTER_LIST = new ArrayList<Node>() {
        @Override
        public boolean add(Node node) {
            //Log.e(TAG, "Adding node ,Size : " + size());
            return super.add(node);
        }
    };

    final static int[][] DESIRED_OUTPUT = new int[][]{{RED, GREEN, BLUE},
            {BLUE, ORANGE, RED},
            {BLUE, RED, RED}};

    final static int[][] CURRENT_STATE = new int[][]{{RED, RED, WHITE, WHITE, WHITE},
            {RED, RED, YELLOW, WHITE, YELLOW},
            {GREEN, GREEN, YELLOW, YELLOW, BLANK},
            {GREEN, GREEN, BLUE, ORANGE, ORANGE},
            {BLUE, BLUE, BLUE, ORANGE, ORANGE}};

    public static void main(String args[]) {
        MASTER_LIST.clear();
        Node node = new Node();
        node.array = CURRENT_STATE;
        MASTER_LIST.add(node);
        int recursionStage = 0;
        findSolution(node, recursionStage);
        Log.e(TAG, "Exiting");
        /*Node copy = getCopy(node);
        copy.array[0][0] = 10;
        if (copy.equals(node)) {
            Log.e(TAG, "Equal");
        } else {
            Log.e(TAG, "Not Equal");
        }*/
    }

    private static boolean isDesiredOutputReached(Node node) {
        int[][] currentState = node.array;
        int startRow = 1;
        int endRow = 3;
        int startColumn = 1;
        int endColumn = 3;
        for (int i = startRow; i <= endRow; i++) {
            for (int j = startColumn; j <= endColumn; j++) {
                if (currentState[i][j] != DESIRED_OUTPUT[i - 1][j - 1]) {
                    return false;
                }
            }
        }
        return true;
    }


    private static void findSolution(Node start, int recursionStage) {
        //printNode(start);
        int nextStage = recursionStage + 1;
        //Log.e(TAG, "recursion : " + recursionStage);
        if (isDesiredOutputReached(start)) {
            Log.e(TAG, "got desired output");
            return;
        }
        List<Node> nextCombinations = getAllNextCombinations(start);
        for (Node combination : nextCombinations) {
            if (!MASTER_LIST.contains(combination)) {
                MASTER_LIST.add(combination);
                findSolution(combination, nextStage);
            }
        }
    }

    private static void printNode(Node node) {
        StringBuilder stringBuilder = new StringBuilder();
        int startRow = 1;
        int endRow = 3;
        int startColumn = 1;
        int endColumn = 3;
        for (int i = startRow; i <= endRow; i++) {
            for (int j = startColumn; j <= endColumn; j++) {
                stringBuilder.append(getColor(node.array[i][j]) + " ");
            }
        }
        String log = stringBuilder.toString();
        Log.e(TAG, "Node : " + log);
        stringBuilder.delete(0, log.length() - 1);
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                stringBuilder.append(getColor(DESIRED_OUTPUT[i][j]) + " ");
            }
        }
        Log.e(TAG, "Desired Node : " + stringBuilder.toString());
    }

    private static String getColor(int value) {
        switch (value) {
            case RED:
                return "Red";
            case GREEN:
                return "Green";
            case WHITE:
                return "White";
            case YELLOW:
                return "Yellow";
            case BLUE:
                return "Blue";
            case ORANGE:
                return "Orange";
            case BLANK:
                return "Blank";
        }
        return null;
    }


    public static List<Node> getAllNextCombinations(Node start) {
        List<Node> list = new ArrayList<>();
        int highestRank = -1;
        Node bottom = applyMovement(start, Movement.BOTTOM);
        int bottomRank = getRank(bottom);
        if (bottomRank > highestRank) {
            highestRank = bottomRank;
        }
        Node top = applyMovement(start, Movement.TOP);
        int topRank = getRank(top);
        if (topRank > highestRank) {
            highestRank = topRank;
        }
        Node right = applyMovement(start, Movement.RIGHT);
        int rightRank = getRank(right);
        if (rightRank > highestRank) {
            highestRank = rightRank;
        }
        Node left = applyMovement(start, Movement.LEFT);
        int leftRank = getRank(left);
        if (leftRank > highestRank) {
            highestRank = leftRank;
        }
        if (bottom != null && highestRank == bottomRank) {
            list.add(bottom);
        }
        if (top != null && highestRank == topRank) {
            list.add(top);
        }
        if (right != null && highestRank == rightRank) {
            list.add(right);
        }
        if (left != null && highestRank == leftRank) {
            list.add(left);
        }
        return list;
    }


    public static int getRank(Node node) {
        if (node == null) {
            return -1;
        }
        int rank = 0;
        int[][] currentState = node.array;
        int startRow = 1;
        int endRow = 3;
        int startColumn = 1;
        int endColumn = 3;
        for (int i = startRow; i <= endRow; i++) {
            for (int j = startColumn; j <= endColumn; j++) {
                if (currentState[i][j] == DESIRED_OUTPUT[i - 1][j - 1]) {
                    rank++;
                }
            }
        }
        Log.e(TAG, "Rank : " + rank);
        if (rank == 8) {
            printNode(node);
        }
        return rank;
    }

    public static Node applyMovement(Node node, Movement movement) {
        Node copyNode = getCopy(node);
        int rowIdBlank = 0;
        int columnIdBlank = 0;
        for (int i = 0; i < 5; i++) {
            boolean found = false;
            for (int j = 0; j < 5; j++) {
                if (copyNode.array[i][j] == BLANK) {
                    rowIdBlank = i;
                    columnIdBlank = j;
                    found = true;
                    break;
                }
            }
            if (found) {
                break;
            }
        }
        switch (movement) {
            case LEFT:
                if (columnIdBlank == 0) {
                    return null;
                } else {
                    copyNode.array[rowIdBlank][columnIdBlank] = copyNode.array[rowIdBlank][columnIdBlank - 1];
                    copyNode.array[rowIdBlank][columnIdBlank - 1] = BLANK;
                }
                break;
            case RIGHT:
                if (columnIdBlank == 4) {
                    return null;
                } else {
                    copyNode.array[rowIdBlank][columnIdBlank] = copyNode.array[rowIdBlank][columnIdBlank + 1];
                    copyNode.array[rowIdBlank][columnIdBlank + 1] = BLANK;
                }
                break;
            case TOP:
                if (rowIdBlank == 0) {
                    return null;
                } else {
                    copyNode.array[rowIdBlank][columnIdBlank] = copyNode.array[rowIdBlank - 1][columnIdBlank];
                    copyNode.array[rowIdBlank - 1][columnIdBlank] = BLANK;
                }
                break;
            case BOTTOM:
                if (rowIdBlank == 4) {
                    return null;
                } else {
                    copyNode.array[rowIdBlank][columnIdBlank] = copyNode.array[rowIdBlank + 1][columnIdBlank];
                    copyNode.array[rowIdBlank + 1][columnIdBlank] = BLANK;
                }
                break;
        }
        return copyNode;
    }


    private static Node getCopy(Node node) {
        int[][] newArray = new int[5][5];
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                newArray[i][j] = node.array[i][j];
            }
        }
        Node node1 = new Node();
        node1.array = newArray;
        return node1;
    }

    public static enum Movement {
        LEFT, RIGHT, TOP, BOTTOM
    }


    public static class Node {

        int[][] array;

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node node = (Node) o;
            //boolean isEqual = Arrays.equals(array, node.array);
            boolean isEqual = equal(array, node.array);
            if (isEqual) {
                Log.e(TAG, "equals");
            }
            return isEqual;
        }

        public static boolean equal(final int[][] arr1, final int[][] arr2) {
            if (arr1 == null) {
                return (arr2 == null);
            }
            if (arr2 == null) {
                return false;
            }
            if (arr1.length != arr2.length) {
                return false;
            }
            for (int i = 0; i < arr1.length; i++) {
                if (!Arrays.equals(arr1[i], arr2[i])) {
                    return false;
                }
            }
            return true;
        }

    }

}
