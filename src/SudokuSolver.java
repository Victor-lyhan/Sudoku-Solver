public class SudokuSolver {
    private static final int SIZE = 9;
    private int[][] board;
    private boolean[][][] candidates;

    public SudokuSolver(int[][] board) {
        this.board = new int[SIZE][SIZE];
        for (int i = 0; i < SIZE; i++) {
            System.arraycopy(board[i], 0, this.board[i], 0, SIZE);
        }
        candidates = new boolean[SIZE][SIZE][SIZE + 1];
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                for (int num = 1; num <= SIZE; num++) {
                    candidates[r][c][num] = true;
                }
            }
        }
        initCandidates();
    }

    private void initCandidates() {
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                if (board[r][c] != 0) {
                    int val = board[r][c];
                    for (int num = 1; num <= SIZE; num++) {
                        candidates[r][c][num] = (num == val);
                    }
                    propagate(r, c, val);
                } else {
                    for (int num = 1; num <= SIZE; num++) {
                        if (!isSafe(r, c, num)) {
                            candidates[r][c][num] = false;
                        }
                    }
                }
            }
        }
    }

    private boolean isSafe(int row, int col, int val) {
        for (int i = 0; i < SIZE; i++) {
            if (board[row][i] == val) return false;
            if (board[i][col] == val) return false;
        }
        int br = (row / 3) * 3;
        int bc = (col / 3) * 3;
        for (int r = br; r < br + 3; r++) {
            for (int c = bc; c < bc + 3; c++) {
                if (board[r][c] == val) return false;
            }
        }
        return true;
    }

    private void propagate(int row, int col, int val) {
        for (int c = 0; c < SIZE; c++) {
            if (c != col) {
                candidates[row][c][val] = false;
            }
        }
        for (int r = 0; r < SIZE; r++) {
            if (r != row) {
                candidates[r][col][val] = false;
            }
        }
        int br = (row / 3) * 3;
        int bc = (col / 3) * 3;
        for (int r = br; r < br + 3; r++) {
            for (int c = bc; c < bc + 3; c++) {
                if (r != row || c != col) {
                    candidates[r][c][val] = false;
                }
            }
        }
    }

    public boolean solve() {
        return backtrack();
    }

    private boolean backtrack() {
        int[] cell = selectNextCell();
        if (cell == null) return true;
        int row = cell[0];
        int col = cell[1];
        for (int val = 1; val <= SIZE; val++) {
            if (candidates[row][col][val]) {
                int[][] backupBoard = copyBoard();
                boolean[][][] backupCandidates = copyCandidates();
                board[row][col] = val;
                placeValue(row, col, val);
                if (backtrack()) {
                    return true;
                }
                board = backupBoard;
                candidates = backupCandidates;
            }
        }
        return false;
    }

    private int[] selectNextCell() {
        int minCount = Integer.MAX_VALUE;
        int[] bestCell = null;
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                if (board[r][c] == 0) {
                    int count = candidateCount(r, c);
                    if (count == 0) return new int[] { r, c };
                    if (count < minCount) {
                        minCount = count;
                        bestCell = new int[] { r, c };
                    }
                }
            }
        }
        return bestCell;
    }

    private int candidateCount(int r, int c) {
        int count = 0;
        for (int val = 1; val <= SIZE; val++) {
            if (candidates[r][c][val]) count++;
        }
        return count;
    }

    private void placeValue(int row, int col, int val) {
        for (int num = 1; num <= SIZE; num++) {
            candidates[row][col][num] = (num == val);
        }
        propagate(row, col, val);
    }

    private int[][] copyBoard() {
        int[][] copy = new int[SIZE][SIZE];
        for (int r = 0; r < SIZE; r++) {
            System.arraycopy(board[r], 0, copy[r], 0, SIZE);
        }
        return copy;
    }

    private boolean[][][] copyCandidates() {
        boolean[][][] copy = new boolean[SIZE][SIZE][SIZE + 1];
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                System.arraycopy(candidates[r][c], 0, copy[r][c], 0, SIZE + 1);
            }
        }
        return copy;
    }

    public void printBoard() {
        for (int r = 0; r < SIZE; r++) {
            if (r % 3 == 0 && r != 0) System.out.println("------+-------+------");
            for (int c = 0; c < SIZE; c++) {
                if (c % 3 == 0 && c != 0) System.out.print("| ");
                System.out.print(board[r][c] == 0 ? ". " : board[r][c] + " ");
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        int[][] puzzle = {
                {0,0,1, 0,0,4, 5,0,0},
                {0,0,0, 5,3,6, 0,0,0},
                {0,0,0, 0,8,0, 2,0,0},

                {6,0,0, 0,4,0, 0,0,0},
                {0,5,0, 8,2,0, 0,0,7},
                {3,0,2, 0,0,7, 0,0,0},

                {4,0,0, 0,7,0, 0,0,0},
                {1,0,0, 0,0,0, 0,8,5},
                {0,0,3, 0,0,0, 0,6,0}
        };
        SudokuSolver solver = new SudokuSolver(puzzle);
        if (solver.solve()) {
            solver.printBoard();
        } else {
            System.out.println("No solution found.");
        }
    }
}