package edu.cmu.andrew.karim.server.managers;

import com.mongodb.client.MongoCollection;
import edu.cmu.andrew.karim.server.exceptions.AppException;
import edu.cmu.andrew.karim.server.exceptions.AppInternalServerException;
import edu.cmu.andrew.karim.server.utils.MongoPool;
import edu.cmu.andrew.karim.server.utils.AppLogger;
import org.bson.Document;

public class Manager {
    protected MongoCollection<Document> userCollection;
    protected MongoCollection<Document> sessionCollection;

    public Manager() {
        this.userCollection = MongoPool.getInstance().getCollection("users");
    }

    protected AppException handleException(String message, Exception e){
        if (e instanceof AppException && !(e instanceof AppInternalServerException))
            return (AppException)e;
        AppLogger.error(message, e);
        return new AppInternalServerException(-1);
    }
}
