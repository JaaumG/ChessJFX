package dev.joao_guilherme.chess.movements;


public interface SupportsHistory {

    void injectRecord(MoveRecordBuilder moveRecordBuilder);
}
