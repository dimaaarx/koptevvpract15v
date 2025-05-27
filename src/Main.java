import java.util.Scanner;
import java.io.*;

public class Main {
    private static char[][] board;
    private static int boardSize = 3;
    private static char currentPlayer = 'X';
    private static boolean gameOver = false;
    private static final Scanner scanner = new Scanner(System.in);
    private static String playerX = "Гравець X";
    private static String playerO = "Гравець O";

    public static void main(String[] args) {
        loadSettingsFromFile();
        while (true) {
            int choice = showMenu();
            switch (choice) {
                case 1:
                    startGame();
                    break;
                case 2:
                    changeSettings();
                    break;
                case 3:
                    showStatistics();
                    break;
                case 0:
                    System.out.println("Вихід з гри");
                    return;
                default:
                    System.out.println("Невірний вибір. Спробуйте ще раз.");
            }
        }
    }

    private static int showMenu() {
        System.out.println("=== Головне меню ===");
        System.out.println("1. Почати гру");
        System.out.println("2. Налаштування");
        System.out.println("3. Показати статистику");
        System.out.println("0. Вихід");
        System.out.print("Ваш вибір: ");
        return scanner.nextInt();
    }

    private static void startGame() {
        board = new char[boardSize][boardSize];
        initializeBoard();
        gameOver = false;
        currentPlayer = 'X';

        while (!gameOver) {
            displayBoard();
            playerMove();
            checkGameState();
            if (!gameOver) {
                switchPlayer();
            }
        }
    }

    private static void initializeBoard() {
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                board[i][j] = ' ';
            }
        }
    }

    private static void displayBoard() {
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                System.out.print(board[i][j]);
                if (j < boardSize - 1) System.out.print(" | ");
            }
            System.out.println();
            if (i < boardSize - 1) {
                for (int k = 0; k < boardSize * 4 - 1; k++) System.out.print("-");
                System.out.println();
            }
        }
    }

    private static void playerMove() {
        int row, col;
        while (true) {
            System.out.println((currentPlayer == 'X' ? playerX : playerO) + ", введіть рядок і стовпець (1-" + boardSize + "): ");
            row = scanner.nextInt() - 1;
            col = scanner.nextInt() - 1;
            if (row >= 0 && row < boardSize && col >= 0 && col < boardSize && board[row][col] == ' ') {
                board[row][col] = currentPlayer;
                break;
            } else {
                System.out.println("Невірний хід, спробуйте ще раз.");
            }
        }
    }

    private static void checkGameState() {
        if (checkWin()) {
            displayBoard();
            System.out.println("Гравець " + (currentPlayer == 'X' ? playerX : playerO) + " переміг!");
            saveGameStatistics(String.valueOf(currentPlayer));
            gameOver = true;
        } else if (isDraw()) {
            displayBoard();
            System.out.println("Нічия!");
            saveGameStatistics("Draw");
            gameOver = true;
        }
    }

    private static boolean checkWin() {
        for (int i = 0; i < boardSize; i++) {
            if (checkRow(i) || checkColumn(i)) return true;
        }
        return checkDiagonals();
    }

    private static boolean checkRow(int row) {
        for (int j = 1; j < boardSize; j++) {
            if (board[row][j] != board[row][0] || board[row][j] == ' ') return false;
        }
        return true;
    }
    private static boolean checkColumn(int col) {
        for (int i = 1; i < boardSize; i++) {
            if (board[i][col] != board[0][col] || board[i][col] == ' ') return false;
        }
        return true;
    }

    private static boolean checkDiagonals() {
        boolean mainDiagonal = true, antiDiagonal = true;
        for (int i = 1; i < boardSize; i++) {
            if (board[i][i] != board[0][0] || board[i][i] == ' ') mainDiagonal = false;
            if (board[i][boardSize - i - 1] != board[0][boardSize - 1] || board[i][boardSize - i - 1] == ' ') antiDiagonal = false;
        }
        return mainDiagonal || antiDiagonal;
    }

    private static boolean isDraw() {
        for (int i = 0; i < boardSize; i++) {
            for (int j = 0; j < boardSize; j++) {
                if (board[i][j] == ' ') return false;
            }
        }
        return true;
    }

    private static void switchPlayer() {
        currentPlayer = (currentPlayer == 'X') ? 'O' : 'X';
    }

    private static void changeSettings() {
        System.out.println("Оберіть розмір поля: 3, 5, 7, 9");
    }

    private static void saveSettingsToFile() {
        try (PrintWriter out = new PrintWriter("settings.txt")) {
            out.println(boardSize);
            out.println(playerX);
            out.println(playerO);
        } catch (Exception e) {
            System.out.println("Помилка збереження налаштувань.");
        }
    }

    private static void loadSettingsFromFile() {
        try (Scanner fileScanner = new Scanner(new File("settings.txt"))) {
            boardSize = Integer.parseInt(fileScanner.nextLine());
            playerX = fileScanner.nextLine();
            playerO = fileScanner.nextLine();
        } catch (Exception e) {
            boardSize = 3;
            playerX = "Гравець X";
            playerO = "Гравець O";
        }
    }

    private static void saveGameStatistics(String result) {
        try (FileWriter writer = new FileWriter("stats.txt", true)) {
            java.time.LocalDateTime now = java.time.LocalDateTime.now();
            String timestamp = now.toString();
            String playerName = (result.equals("X")) ? playerX : (result.equals("O")) ? playerO : "Нічия";
            writer.write(playerName + "," + result + "," + timestamp + "," + boardSize + "\n");
        } catch (Exception e) {
            System.out.println("Помилка збереження статистики.");
        }
    }

    private static void showStatistics() {
        try (Scanner fileScanner = new Scanner(new File("stats.txt"))) {
            System.out.println("=== Статистика ігор ===");
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    System.out.println("Гравець: " + parts[0] +
                            ", Результат: " + parts[1] +
                            ", Дата/час: " + parts[2] +
                            ", Розмір поля: " + parts[3]);
                }
            }
        } catch (Exception e) {
            System.out.println("Файл статистики не знайдено або порожній.");
        }
    }
}
