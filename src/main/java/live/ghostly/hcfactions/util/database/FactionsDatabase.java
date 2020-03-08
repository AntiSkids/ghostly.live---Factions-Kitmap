package live.ghostly.hcfactions.util.database;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import live.ghostly.hcfactions.FactionsPlugin;
import lombok.Getter;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

@Getter
public class FactionsDatabase {

    private MongoClient client;
    private MongoDatabase database;
    private MongoCollection<Document> profiles, fights,  koths, citadels, glowstone, crates,  kits, gKits, modes;

    public FactionsDatabase(FactionsPlugin main) {
        if (main.getMainConfig().getBoolean("DATABASE.MONGO.AUTHENTICATION.ENABLED")) {

            List<MongoCredential> credentials = new ArrayList<>();
            credentials.add(MongoCredential.createCredential(main.getMainConfig().getString("DATABASE.MONGO.AUTHENTICATION.USER"), main.getMainConfig().getString("DATABASE.MONGO.AUTHENTICATION.DATABASE"), main.getMainConfig().getString("DATABASE.MONGO.AUTHENTICATION.PASSWORD").toCharArray()));
            client = new MongoClient(new ServerAddress(main.getMainConfig().getString("DATABASE.MONGO.HOST"), main.getMainConfig().getInt("DATABASE.MONGO.PORT")), credentials);
        } else {
            client = new MongoClient(new ServerAddress(main.getMainConfig().getString("DATABASE.MONGO.HOST"), main.getMainConfig().getInt("DATABASE.MONGO.PORT")));
        }

        database = client.getDatabase(main.getMainConfig().getString("DATABASE.MONGO.DATABASE"));
        profiles = database.getCollection("profiles");
        fights = database.getCollection("fights");
        koths = database.getCollection("koths");
        citadels = database.getCollection("citadels");
        glowstone = database.getCollection("glowstone");
        crates = database.getCollection("crates");
        kits = database.getCollection("kits");
        gKits = database.getCollection("gkits");
        modes = database.getCollection("modes");
    }

}
