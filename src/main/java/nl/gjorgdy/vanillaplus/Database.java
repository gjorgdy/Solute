package nl.gjorgdy.vanillaplus;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;

import static com.mongodb.client.model.Filters.eq;

public class Database {

    // Construct Database URI
    private static final String uri = "mongodb://helix:Pg56Uk7VwAnS4KHJDHkaxQtCaFv9Zl@hexasis.eu:27017";
    private static final String dbId = "helix_alpha";
    private static final String colId = "vanilla_plus";
    public static boolean AVAILABLE;

    public Database() {
        // Try to connect to database
        try {
            MongoClient mongoClient = MongoClients.create(uri);

            MongoDatabase database = mongoClient.getDatabase(dbId);
            MongoCollection<Document> collection = database.getCollection(colId);

            VanillaPlus.LOGGER.info("Connected to Database");
            AVAILABLE = true;
        } catch (Exception e) {
            VanillaPlus.LOGGER.info("Could not connect to database");
            VanillaPlus.LOGGER.info(e.getMessage());
            AVAILABLE = false;
        }
    }

}
