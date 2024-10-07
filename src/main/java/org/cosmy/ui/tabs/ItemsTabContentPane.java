package org.cosmy.ui.tabs;

import com.azure.cosmos.CosmosAsyncClient;
import com.azure.cosmos.CosmosAsyncContainer;
import com.azure.cosmos.models.CosmosContainerResponse;
import com.azure.cosmos.models.PartitionKeyDefinition;
import com.azure.cosmos.models.SqlParameter;
import com.azure.cosmos.models.SqlQuerySpec;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import javafx.scene.control.TextArea;
import org.cosmy.ConnectionsContainer;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

public class ItemsTabContentPane extends TextArea {
    private final String itemQuery = "SELECT * from c where (c.id = @id)";
    private ObjectMapper jsonPrinter;

    public ItemsTabContentPane(String s) {
        super(s);
        this.jsonPrinter = new ObjectMapper();
        this.jsonPrinter.enable(SerializationFeature.INDENT_OUTPUT, SerializationFeature.FLUSH_AFTER_WRITE_VALUE);
    }

    public void loadItem(String id, String containerName, String databaseName, String accountName) {
        // TODO error handling
        CosmosAsyncClient asyncClient = ConnectionsContainer.getInstance().getConnection(accountName).getAsyncClient();
        CosmosAsyncContainer asyncContainer = asyncClient.getDatabase(databaseName).getContainer(containerName);
        Mono<CosmosContainerResponse> containerDetails = asyncContainer.read();
        containerDetails.handle((cosmosContainerResponse, synchronousSink) -> {
            PartitionKeyDefinition partitionKeyDefinition = cosmosContainerResponse.getProperties().getPartitionKeyDefinition();
            List<String> partitionKeyPaths = partitionKeyDefinition.getPaths();
        }).subscribe();

        SqlQuerySpec readItemQuerySpec = new SqlQuerySpec(itemQuery);
        SqlParameter parameter = new SqlParameter("@id", id);
        readItemQuerySpec.getParameters().add(parameter);
        asyncClient.getDatabase(databaseName).getContainer(containerName).queryItems(readItemQuerySpec, Map.class).handle((response, synchronousSink1) -> {
            try {
                this.setText(jsonPrinter.writeValueAsString(response));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
                //TODO error handling
            }
        }).subscribe();
    }
}
