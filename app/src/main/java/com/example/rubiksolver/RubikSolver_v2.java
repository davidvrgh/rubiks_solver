package com.example.rubiksolver;

import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

public class RubikSolver_v2 {

    public static String TAG = RubikSolver_v2.class.getSimpleName();

    final static int BLANK = 0;
    final static int RED = 1;
    final static int WHITE = 2;
    final static int YELLOW = 3;
    final static int GREEN = 4;
    final static int BLUE = 5;
    final static int ORANGE = 6;

    final static int[][] DESIRED_OUTPUT = new int[][]{{RED, GREEN, BLUE},
            {BLUE, ORANGE, RED},
            {BLUE, RED, RED}};

    final static int[][] CURRENT_STATE = new int[][]{{RED, RED, WHITE, WHITE, WHITE},
            {RED, RED, YELLOW, WHITE, YELLOW},
            {GREEN, GREEN, YELLOW, YELLOW, BLANK},
            {GREEN, GREEN, BLUE, ORANGE, ORANGE},
            {BLUE, BLUE, BLUE, ORANGE, ORANGE}};

    final static PriorityQueue<Board> QUEUE = new PriorityQueue<>(new BoardComparator());
    final static List<Board> MASTER_LIST = new ArrayList<>();

    public static void main(String args[]) {
        QUEUE.clear();
        Board initialState = new Board();
        initialState.array = CURRENT_STATE;
        initialState.mLevel = 1;
        MASTER_LIST.add(initialState);
        QUEUE.add(initialState);
        Board nextBoard;
        while ((nextBoard = QUEUE.poll()) != null) {
            if (nextBoard.isDesiredOutputReached()) {
                Log.e(TAG, "Got desired output");
                return;
            }
            List<Board> children = getAllNextCombinations(nextBoard);
            //MASTER_LIST.addAll(children);
            QUEUE.addAll(children);
        }
    }


    private static Board getCopy(Board node) {
        int[][] newArray = new int[5][5];
        for (int i = 0; i < 5; i++) {
            for (int j = 0; j < 5; j++) {
                newArray[i][j] = node.array[i][j];
            }
        }
        Board node1 = new Board();
        node1.array = newArray;
        node1.mLevel = node.mLevel + 1;
        return node1;
    }

    public static List<Board> getAllNextCombinations(Board start) {
        List<Board> list = new ArrayList<>();
        Board bottom = applyMovement(start, RubiksSolver.Movement.BOTTOM);
        Board top = applyMovement(start, RubiksSolver.Movement.TOP);
        Board right = applyMovement(start, RubiksSolver.Movement.RIGHT);
        Board left = applyMovement(start, RubiksSolver.Movement.LEFT);
        if (bottom != null && !MASTER_LIST.contains(bottom)) {
            list.add(bottom);
        }
        if (top != null && !MASTER_LIST.contains(top)) {
            list.add(top);
        }
        if (right != null && !MASTER_LIST.contains(right)) {
            list.add(right);
        }
        if (left != null && !MASTER_LIST.contains(left)) {
            list.add(left);
        }
        return list;
    }


    public static Board applyMovement(Board node, RubiksSolver.Movement movement) {
        Board copyNode = getCopy(node);
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


    private static int getRank(int[][] problemBoard, int[][] goalBoard, int level) {
        int[][] numberBoard = new int[5][5];
        int totalDistance = 0;
        int iterationIndex = 1;
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                int distance = 0;
                if (goalBoard[i][j] == problemBoard[1 + i][1 + j]) {
                    distance = 0;
                    numberBoard[i + 1][j + 1] = iterationIndex++;
                } else {
                    CoordinateAndDistance cordAndDist = findNearestUnnumberedIndicesWithRequiredColor(i + 1, j + 1, problemBoard, goalBoard[i][j], numberBoard);
                    numberBoard[cordAndDist.coordinates[0]][cordAndDist.coordinates[1]] = iterationIndex++;
                    distance = cordAndDist.distance;
                }
                totalDistance += distance;
            }
        }
        if (totalDistance < 8) {
            Log.e(TAG, " We are getting closer ");
        }
        Log.e(TAG, " Level : " + level + " Distance : " + totalDistance + " Rank : " + (level + totalDistance));
        return level + totalDistance;
    }

    private static CoordinateAndDistance findNearestUnnumberedIndicesWithRequiredColor(int currentRow, int currentColumn, int[][] problemBoard, int neededColor, int[][] numberBoard) {
       /* Queue<int[]> positions = new LinkedList<>();
        positions.addAll(getNeigbourIndices(currentRow, currentColumn));
        while (!positions.isEmpty()) {
            int[] nieghbour = positions.poll();
            if (numberBoard[nieghbour[0]][nieghbour[1]] == 0 && problemBoard[nieghbour[0]][nieghbour[1]] == neededColor) {
                return nieghbour;
            } else {
                positions.addAll(getNeigbourIndices(nieghbour[0], nieghbour[1]));
            }
        }*/
        int distance = 0;
        List<int[]> frontier = new ArrayList<>();
        frontier.addAll(getNeigbourIndices(currentRow, currentColumn));
        distance++;
        while (!frontier.isEmpty()) {
            List<int[]> newFrontier = new ArrayList<>();
            for (int[] child : frontier) {
                if (numberBoard[child[0]][child[1]] == 0 && problemBoard[child[0]][child[1]] == neededColor) {
                    return new CoordinateAndDistance(child, distance);
                } else {
                    newFrontier.addAll(getNeigbourIndices(child[0], child[1]));
                }
            }
            frontier = newFrontier;
            distance++;
        }
        return null;
    }

    public static class CoordinateAndDistance {
        int[] coordinates;
        int distance;

        CoordinateAndDistance(int[] coordinates, int distance) {
            this.coordinates = coordinates;
            this.distance = distance;
        }
    }


    private static List<int[]> getNeigbourIndices(int row, int column) {
        List<int[]> list = new ArrayList<>();
        int leftRow = row;
        int leftColumn = column - 1;
        int rightRow = row;
        int rightColumn = column + 1;
        int topRow = row - 1;
        int topColumn = column;
        int bottomRow = row + 1;
        int bottomColumn = column;
        if (areIndicesValid(leftRow, leftColumn)) {
            list.add(new int[]{leftRow, leftColumn});
        }
        if (areIndicesValid(rightRow, rightColumn)) {
            list.add(new int[]{rightRow, rightColumn});
        }
        if (areIndicesValid(topRow, topColumn)) {
            list.add(new int[]{topRow, topColumn});
        }
        if (areIndicesValid(bottomRow, bottomColumn)) {
            list.add(new int[]{bottomRow, bottomColumn});
        }
        return list;
    }

    private static boolean areIndicesValid(int row, int column) {
        return row >= 0 && row <= 4 && column >= 0 && column <= 4;
    }


    public static class BoardComparator implements Comparator<Board> {
        @Override
        public int compare(Board board1, Board board2) {
            if (board1.getRank() > board2.getRank()) {
                return 1;
            } else if (board1.getRank() < board2.getRank()) {
                return -1;
            } else {
                return 0;
            }
        }
    }

    public static class Board {

        int[][] array;
        Integer mRank = null;
        int mLevel = -1;

        public Board() {

        }


        public int getRank() {
            /*if (mRank != null) {
                return mRank;
            } else {
                int rank = 0;
                int[][] currentState = array;
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
                mRank = rank - mLevel;
                Log.e(TAG, " Level : " + mLevel + " Same squares : " + rank);
            }
            return mRank;*/
            if (mRank != null) {
                return mRank;
            } else {
                mRank = RubikSolver_v2.getRank(array, DESIRED_OUTPUT, mLevel);
                return mRank;
            }
        }


        public boolean isDesiredOutputReached() {
            return false;
        }


        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Board node = (Board) o;
            return equal(array, node.array);
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
