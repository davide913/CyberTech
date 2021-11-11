package Profile;

import com.google.cloud.firestore.CollectionReference;

public class ExtensionRequest {
    private CollectionReference LendingID;
    private String id;

    public ExtensionRequest(){}

    public ExtensionRequest(CollectionReference LendingID) {
        this.LendingID = LendingID;
    }

    private void setLendingID(CollectionReference lendingID) {
        LendingID = lendingID;
    }

    public CollectionReference getLendingID() {
        return LendingID;
    }

    @Override
    public boolean equals(Object o){
        if(o instanceof ExtensionRequest){
            ExtensionRequest e = (ExtensionRequest) o;
            return  e.LendingID.equals(this.LendingID);
        }
        return false;
    }
}
