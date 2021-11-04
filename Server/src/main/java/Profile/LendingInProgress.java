package Profile;

import com.google.cloud.Timestamp;
import com.google.cloud.firestore.CollectionReference;

import java.util.Date;

public class LendingInProgress {

    private CollectionReference IDmaterial;
    private Timestamp ExpiryDate;

    public LendingInProgress(){}

    public LendingInProgress(CollectionReference IDmAterial, Date expiryDate) {
        this.IDmaterial = IDmAterial;
        ExpiryDate = Timestamp.of(expiryDate);
    }

    public CollectionReference getIDmaterial() {
        return IDmaterial;
    }

    private void setIDmaterial(CollectionReference IDmaterial) {
        this.IDmaterial = IDmaterial;
    }

    public Date getExpiryDate() {
        return this.ExpiryDate.toDate();
    }

    private void setExpiryDate(Date expiryDate) {
        ExpiryDate = Timestamp.of(expiryDate);
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof LendingInProgress){
            LendingInProgress lending = (LendingInProgress) o;
            return lending.ExpiryDate.equals(this.ExpiryDate)  && lending.IDmaterial==this.IDmaterial;
        }
        return false;
    }
}
