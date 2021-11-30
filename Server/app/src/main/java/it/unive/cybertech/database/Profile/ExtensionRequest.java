package it.unive.cybertech.database.Profile;


import static it.unive.cybertech.database.Connection.Database.addToCollection;
import static it.unive.cybertech.database.Connection.Database.deleteFromCollection;
import static it.unive.cybertech.database.Connection.Database.getDocument;
import static it.unive.cybertech.database.Connection.Database.getReference;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import it.unive.cybertech.database.Profile.Exception.NoExtensionRequestException;

public class ExtensionRequest {
    private DocumentReference lendingID;
    private String id;

    public ExtensionRequest(){}

    private ExtensionRequest(DocumentReference lendingID, String id) {
        this.lendingID = lendingID;
        this.id = id;
    }

    private void setLendingID(DocumentReference lendingID) {
        this.lendingID = lendingID;
    }

    public DocumentReference getLendingID() {
        return lendingID;
    }

    public String getId() {
        return id;
    }

    private void setId(String id) {
        this.id = id;
    }

    public static ExtensionRequest addExtensionRequest(LendingInProgress lending) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference("lendingInProgress", lending.getId());

        Map<String, Object> myExtension = new HashMap<>();          //create "table"
        myExtension.put("lendingID", docRef);

        DocumentReference addedDocRef = addToCollection("extensionRequest", myExtension);

        return new ExtensionRequest(docRef, addedDocRef.getId() );
    }

    public boolean removeExtensionRequest(){
        this.id = null;
        this.lendingID = null;
        return deleteFromCollection("extensionRequest", this.id);
    }

    public Task<Void> updateExtensionRequestAsync(@NonNull LendingInProgress lending) throws ExecutionException, InterruptedException {
        DocumentReference docRef = getReference("extensionRequest", this.id);
        DocumentSnapshot document = getDocument(docRef);

        if(document.exists()) {
            DocumentReference docRefLending = getReference("lendingInProgress", lending.getId());

            return docRef.update("lendingID", docRefLending);
        }
        else
            throw new NoExtensionRequestException("No extension request found with this id: " + id);
    }

    public boolean updateExtensionRequest(@NonNull LendingInProgress lending) {
        try {
            Task<Void> t = this.updateExtensionRequestAsync(lending);
            Tasks.await(t);
            this.lendingID = getReference("lendingInProgress", lending.getId());
            return true;
        } catch (ExecutionException | InterruptedException | NoExtensionRequestException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof ExtensionRequest){
            ExtensionRequest e = (ExtensionRequest) o;
            return  e.id.equals(this.id);
        }
        return false;
    }
}
