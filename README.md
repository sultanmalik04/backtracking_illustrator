# Backtracking Algorithm Visualizer

A web application that helps visualize backtracking algorithms written in Java. The application provides a user-friendly interface where users can input their Java code and see a visual representation of how the backtracking algorithm works.

## Features

- Modern, responsive UI built with Next.js and Tailwind CSS
- Code editor with syntax highlighting
- File upload support for Java files
- Real-time visualization of backtracking algorithms
- Spring Boot backend for code analysis and visualization generation

## Prerequisites

- Node.js 18+ and npm
- Java 17+
- Maven

## Setup

### Frontend (Next.js)

1. Navigate to the project root directory:
   ```bash
   cd backtracking_illustrator
   ```

2. Install dependencies:
   ```bash
   npm install
   ```

3. Start the development server:
```bash
npm run dev
   ```

The frontend will be available at `http://localhost:3000`

### Backend (Spring Boot)

1. Navigate to the backend directory:
   ```bash
   cd backend
   ```

2. Build the project:
   ```bash
   mvn clean install
   ```

3. Run the application:
   ```bash
   mvn spring-boot:run
   ```

The backend will be available at `http://localhost:8080`

## Usage

1. Open your browser and navigate to `http://localhost:3000`
2. Enter your Java backtracking algorithm code in the editor or upload a Java file
3. Click the "Visualize" button
4. The visualization will appear in the right panel

## Example Java Code

Here's an example of a backtracking algorithm that can be visualized:

```java
public class NQueens {
    public void solveNQueens(int n) {
        int[] board = new int[n];
        solveNQueensUtil(board, 0, n);
    }

    private boolean solveNQueensUtil(int[] board, int col, int n) {
        if (col >= n) {
            return true;
        }

        for (int i = 0; i < n; i++) {
            if (isSafe(board, i, col, n)) {
                board[col] = i;
                if (solveNQueensUtil(board, col + 1, n)) {
                    return true;
                }
                board[col] = 0;
            }
        }
        return false;
    }

    private boolean isSafe(int[] board, int row, int col, int n) {
        for (int i = 0; i < col; i++) {
            if (board[i] == row || Math.abs(board[i] - row) == Math.abs(i - col)) {
                return false;
            }
        }
        return true;
    }
}
```

## Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

## License

This project is licensed under the MIT License - see the LICENSE file for details.
