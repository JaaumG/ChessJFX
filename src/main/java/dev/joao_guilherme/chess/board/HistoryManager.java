package dev.joao_guilherme.chess.board;

import dev.joao_guilherme.chess.movements.MoveRecord;

import java.util.ArrayDeque;
import java.util.Deque;

public class HistoryManager {

    private final Deque<MoveRecord> history = new ArrayDeque<>();
    private final Deque<MoveRecord> redoStack = new ArrayDeque<>();

    public void push(MoveRecord moveRecord) {
        history.push(moveRecord);
        redoStack.clear();
    }

    public MoveRecord pop() {
        return history.isEmpty() ? null : history.pop();
    }

    public MoveRecord peek() {
        return history.peek();
    }

    public void pushRedo(MoveRecord moveRecord) {
        redoStack.push(moveRecord);
    }

    public MoveRecord popRedo() {
        return redoStack.isEmpty() ? null : redoStack.pop();
    }

    public boolean hasRedo() {
        return !redoStack.isEmpty();
    }
}

