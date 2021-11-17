package Profile;

import com.google.cloud.firestore.CollectionReference;

class ExtensionRequest {
    private CollectionReference LendingID;

    public ExtensionRequest(CollectionReference LendingID) {
        this.LendingID = LendingID;
    }

    public CollectionReference getLendingID() {
        return LendingID;
    }
}
