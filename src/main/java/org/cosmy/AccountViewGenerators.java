package org.cosmy;

import javafx.event.EventType;
import javafx.scene.control.TreeItem;
import org.cosmy.model.CosmosAccount;
import reactor.core.publisher.Mono;

public class AccountViewGenerators {

    public static TreeItem<String> generateEmptyCollapsedView(CosmosAccount account) {
        TreeItem<String> item = new TreeItem<>(account.getName());
//        if (account.databaseCount() <= 0) {
            item.getChildren().add(new TreeItem<>());
//        }
        item.addEventHandler(EventType.ROOT, event -> {
            switch (event.getEventType().getName()) {
                case "BranchExpandedEvent":
                    expandAccountView(item, account);
                    break;
                default:
                    System.out.println("Unhandled event: " + event.getEventType().getName());
            }
        });
        return item;
    }

    private static void expandAccountView(TreeItem<String> item, CosmosAccount account) {
        if (!account.isAccountRefreshed()) {
            item.getChildren().removeFirst();
            account.refresh();
            account.iterateDatabases().forEachRemaining(cosmosDatabase -> {
                    item.getChildren().add(cosmosDatabase.generateView());
                });
            account.setAccountRefreshed(true);
        }
    }
}
