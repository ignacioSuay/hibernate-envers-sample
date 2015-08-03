package sample.data.jpa.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

/**
 * Created by suay on 7/29/15.
 */
@Component
public class Transactor {

    @Autowired
    private PlatformTransactionManager transactionManager;

    public void perform(UnitOfWork unitOfWork) {
         DefaultTransactionDefinition definition = new DefaultTransactionDefinition();
         TransactionStatus transaction = transactionManager.getTransaction(definition);

         try {
                 unitOfWork.work();
                 transactionManager.commit(transaction);
         } catch (Exception e) {
                 transactionManager.rollback(transaction);
                 e.printStackTrace();
         }
    }
}
