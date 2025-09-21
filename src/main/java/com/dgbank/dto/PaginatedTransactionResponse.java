package com.dgbank.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;


// PaginatedTransactionResponse.java - Per transazioni paginate
@Schema(description = "Response per transazioni con paginazione")
public class PaginatedTransactionResponse extends BaseResponse {
    @Schema(description = "ID account/utente", example = "1")
    private Long accountId;
    
    @Schema(description = "Lista delle transazioni")
    private List<TransactionData> transactions;
    
    @Schema(description = "Pagina corrente", example = "0")
    private int currentPage;
    
    @Schema(description = "Numero totale di pagine", example = "5")
    private int totalPages;
    
    @Schema(description = "Numero totale di elementi", example = "47")
    private long totalElements;
    
    @Schema(description = "Ha pagina successiva", example = "true")
    private boolean hasNext;
    
    @Schema(description = "Ha pagina precedente", example = "false")
    private boolean hasPrevious;
    
    public PaginatedTransactionResponse() {}
    
    public PaginatedTransactionResponse(boolean success, Long accountId, List<TransactionData> transactions,
                                      int currentPage, int totalPages, long totalElements, boolean hasNext, boolean hasPrevious) {
        super(success, "Operazione completata");
        this.accountId = accountId;
        this.transactions = transactions;
        this.currentPage = currentPage;
        this.totalPages = totalPages;
        this.totalElements = totalElements;
        this.hasNext = hasNext;
        this.hasPrevious = hasPrevious;
    }

    // Getter e setter
    public Long getAccountId() { return accountId; }
    public void setAccountId(Long accountId) { this.accountId = accountId; }
    
    public List<TransactionData> getTransactions() { return transactions; }
    public void setTransactions(List<TransactionData> transactions) { this.transactions = transactions; }
    
    public int getCurrentPage() { return currentPage; }
    public void setCurrentPage(int currentPage) { this.currentPage = currentPage; }
    
    public int getTotalPages() { return totalPages; }
    public void setTotalPages(int totalPages) { this.totalPages = totalPages; }
    
    public long getTotalElements() { return totalElements; }
    public void setTotalElements(long totalElements) { this.totalElements = totalElements; }
    
    public boolean isHasNext() { return hasNext; }
    public void setHasNext(boolean hasNext) { this.hasNext = hasNext; }
    
    public boolean isHasPrevious() { return hasPrevious; }
    public void setHasPrevious(boolean hasPrevious) { this.hasPrevious = hasPrevious; }
}
