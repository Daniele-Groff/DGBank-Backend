package com.dgbank.model.enums;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Tipo di transazione")
public enum TransactionType {
    @Schema(description = "Trasferimento")
    TRANSFER,

    @Schema(description = "Deposito")
    DEPOSIT,

    @Schema(description = "Prelievo")
    WITHDRAWAL,

    @Schema(description = "Pagamento")
    PAYMENT
}
