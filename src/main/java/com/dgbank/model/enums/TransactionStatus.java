package com.dgbank.model.enums;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Possibili stati delle transazioni")
public enum TransactionStatus {
    @Schema(description = "Sospesa")
    PENDING,

    @Schema(description = "Completata")
    COMPLETED,

    @Schema(description = "Fallita")
    FAILED,

    @Schema(description = "Cancellata")
    CANCELLED
}