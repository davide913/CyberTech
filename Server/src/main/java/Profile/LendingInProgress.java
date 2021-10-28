package Profile;

import com.google.cloud.firestore.CollectionReference;
import com.google.type.DateTime;

class LendingInProgress {

    private CollectionReference IDmaterial;
    private DateTime ExpiryDate;

    public LendingInProgress(CollectionReference IDmAterial, DateTime expiryDate) {
        this.IDmaterial = IDmAterial;
        ExpiryDate = expiryDate;
    }

    public CollectionReference getIDmAteriale() {
        return IDmaterial;
    }

    public DateTime getExpiryDate() {
        return ExpiryDate;
    }
}
