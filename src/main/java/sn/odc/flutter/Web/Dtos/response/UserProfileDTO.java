// UserProfileDTO.java
package sn.odc.flutter.Web.Dtos.response;

import sn.odc.flutter.Datas.Entity.User;
import java.util.List;

public class UserProfileDTO {
    private User user;
    private List<TransactionResponseDTO> transactions;

    public UserProfileDTO(User user, List<TransactionResponseDTO> transactions) {
        this.user = user;
        this.transactions = transactions;
    }

    // Getters and Setters
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<TransactionResponseDTO> getTransactions() {
        return transactions;
    }

    public void setTransactions(List<TransactionResponseDTO> transactions) {
        this.transactions = transactions;
    }
}