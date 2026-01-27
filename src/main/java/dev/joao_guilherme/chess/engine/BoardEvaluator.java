package dev.joao_guilherme.chess.engine;

import dev.joao_guilherme.chess.board.Board;
import dev.joao_guilherme.chess.board.Position;
import dev.joao_guilherme.chess.enums.Color;
import dev.joao_guilherme.chess.pieces.Pawn;
import dev.joao_guilherme.chess.pieces.Piece;

import java.util.List;
import java.util.Set;

import static dev.joao_guilherme.chess.enums.Color.BLACK;
import static dev.joao_guilherme.chess.enums.Color.WHITE;

public class BoardEvaluator {

    public static float evaluate(Board board) {

        // 1. Material
        float whiteMaterial = board.getPieces(WHITE).stream().mapToInt(Piece::getValue).sum();
        float blackMaterial = board.getPieces(BLACK).stream().mapToInt(Piece::getValue).sum();

        // 2. Mobilidade (peso reduzido)
        float whiteMobility = 0.1f * board.getPieces(WHITE).stream()
                .mapToInt(p -> p.getPossibleMoves(board).size())
                .sum();

        float blackMobility = 0.1f * board.getPieces(BLACK).stream()
                .mapToInt(p -> p.getPossibleMoves(board).size())
                .sum();

        // 3. Controle do centro (E4, D4, E5, D5)
        Set<Position> center = Set.of(
                Position.D4, Position.E4,
                Position.D5, Position.E5
        );

        float whiteCenter = 0.3f * board.getPieces(WHITE).stream()
                .filter(p -> center.contains(p.getPosition()))
                .count();

        float blackCenter = 0.3f * board.getPieces(BLACK).stream()
                .filter(p -> center.contains(p.getPosition()))
                .count();

        // 4. Segurança do Rei
        float whiteKingSafety = isKingSafe(board, WHITE) ? 1.5f : -1.5f;
        float blackKingSafety = isKingSafe(board, BLACK) ? 1.5f : -1.5f;

        // 5. Estrutura de Peões (simples)
        float whitePawnStructure = evaluatePawnStructure(board, WHITE);
        float blackPawnStructure = evaluatePawnStructure(board, BLACK);

        // 6. Penalidade para check
        float whiteCheckPenalty = board.isCheck(WHITE) ? -2 : 0;
        float blackCheckPenalty = board.isCheck(BLACK) ? -2 : 0;

        // 7. Checkmate → infinito
        if (board.isCheckMate(WHITE)) return Float.NEGATIVE_INFINITY;
        if (board.isCheckMate(BLACK)) return Float.POSITIVE_INFINITY;

        float whiteScore =
                whiteMaterial +
                        whiteMobility +
                        whiteCenter +
                        whiteKingSafety +
                        whitePawnStructure +
                        whiteCheckPenalty;

        float blackScore =
                blackMaterial +
                        blackMobility +
                        blackCenter +
                        blackKingSafety +
                        blackPawnStructure +
                        blackCheckPenalty;

        return whiteScore - blackScore;
    }

    private static boolean isKingSafe(Board board, Color color) {
        Piece king = board.findKing(color);
        Position k = king.getPosition();

        // Rei no centro é ruim em meio-jogo
        boolean inCenter = (k.file() >= 'c' && k.file() <= 'f') &&
                (k.rank() >= 3 && k.rank() <= 6);

        if (inCenter) return false;

        // Rei atrás de peões é mais seguro
        long pawnsInFront = board.getPieces(color).stream()
                .filter(Pawn.class::isInstance)
                .filter(p -> Math.abs(p.getPosition().file() - k.file()) <= 1)
                .filter(p -> (color == WHITE && p.getPosition().rank() > k.rank()) ||
                        (color == BLACK && p.getPosition().rank() < k.rank()))
                .count();

        return pawnsInFront >= 1;
    }

    private static float evaluatePawnStructure(Board board, Color color) {
        List<Piece> pawns = board.getPieces(color).stream()
                .filter(Pawn.class::isInstance)
                .toList();

        float score = 0;

        for (Piece p : pawns) {
            Position pos = p.getPosition();

            // Peão isolado (nenhum peão nas colunas adjacentes)
            boolean hasNeighbors = pawns.stream().anyMatch(other ->
                    Math.abs(other.getPosition().file() - pos.file()) == 1
            );
            if (!hasNeighbors) score -= 0.5f;

            // Peão dobrado
            long sameFile = pawns.stream()
                    .filter(other -> other.getPosition().file() == pos.file())
                    .count();
            if (sameFile > 1) score -= 0.25f;

            // Peão passado
            boolean isPassed = pawns.stream()
                    .filter(other -> other.getColor() != color)
                    .noneMatch(other -> other.getPosition().file() == pos.file() &&
                            ((color == WHITE && other.getPosition().rank() > pos.rank()) ||
                                    (color == BLACK && other.getPosition().rank() < pos.rank()))
                    );
            if (isPassed) score += 0.5f;

            // Peão proximo de promover
            boolean isNearPromotion = pawns.stream()
                    .filter(other -> other.getColor() != color)
                    .anyMatch(other -> Math.abs(other.getPosition().file() - pos.file()) == 1);
            if (isNearPromotion) score += 0.5f;
        }

        return score;
    }
}
