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

    private static final float MOBILITY_WEIGHT = 0.05f;
    private static final float CENTER_WEIGHT = 0.3f;
    private static final float KING_SAFETY_WEIGHT = 1.5f;
    private static final float CHECK_PENALTY_WEIGHT = 2f;
    private static final float PAWN_STRUCTURE_WEIGHT = 0.5f;

    public static float evaluate(Board board) {

        float whiteMaterial = 0;
        float blackMaterial = 0;
        float whiteMobility = 0;
        float blackMobility = 0;
        float whiteCenter = 0;
        float blackCenter = 0;

        Set<Position> center = Set.of(
                Position.D4, Position.E4,
                Position.D5, Position.E5
        );

        for (Color c : List.of(WHITE, BLACK)) {
            int material = 0;
            int mobility = 0;
            long centerCount = 0;

            for (Piece piece : board.getPieces(c)) {
                material += piece.getValue();
                mobility += piece.getPossibleMoves(board).size();
                if (center.contains(piece.getPosition())) {
                    centerCount++;
                }
            }

            if (c == WHITE) {
                whiteMaterial = material;
                whiteMobility = MOBILITY_WEIGHT * mobility;
                whiteCenter = CENTER_WEIGHT * centerCount;
            } else {
                blackMaterial = material;
                blackMobility = MOBILITY_WEIGHT * mobility;
                blackCenter = CENTER_WEIGHT * centerCount;
            }
        }

        // 4. Segurança do Rei
        float whiteKingSafety = isKingSafe(board, WHITE) ? KING_SAFETY_WEIGHT : -KING_SAFETY_WEIGHT;
        float blackKingSafety = isKingSafe(board, BLACK) ? KING_SAFETY_WEIGHT : -KING_SAFETY_WEIGHT;

        // 5. Estrutura de Peões (simples)
        float whitePawnStructure = evaluatePawnStructure(board, WHITE);
        float blackPawnStructure = evaluatePawnStructure(board, BLACK);

        // 6. Penalidade para check
        float whiteCheckPenalty = board.isCheck(WHITE) ? -CHECK_PENALTY_WEIGHT : 0;
        float blackCheckPenalty = board.isCheck(BLACK) ? -CHECK_PENALTY_WEIGHT : 0;

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
