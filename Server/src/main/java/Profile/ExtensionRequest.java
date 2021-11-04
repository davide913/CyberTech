package Profile;

import com.google.cloud.firestore.CollectionReference;

class ExtensionRequest {
    private CollectionReference LendingID;

    public ExtensionRequest(CollectionReference LendingID) {
        this.LendingID = LendingID;
    }

    private void setLendingID(CollectionReference lendingID) {
        LendingID = lendingID;
    }

    public CollectionReference getLendingID() {
        return LendingID;
    }
}
