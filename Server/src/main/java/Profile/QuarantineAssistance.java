package Profile;

import com.google.cloud.firestore.CollectionReference;
import com.google.type.DateTime;

public class QuarantineAssistance {
    private CollectionReference AssistanceType;
    private String Description;
    private CollectionReference InCharge;
    private DateTime DeliveryDate;
    //private raccolta chatPrivata


    public QuarantineAssistance() {
        AssistanceType = null;
        Description = "Quarantine Assistance empty";
        InCharge = null;
        DeliveryDate = null;
    }

    public QuarantineAssistance(CollectionReference assistanceType, String description, CollectionReference inCharge, DateTime deliveryDate) {
        AssistanceType = assistanceType;
        Description = description;
        InCharge = inCharge;
        DeliveryDate = deliveryDate;
    }



    public CollectionReference getAssistanceType() {
        return AssistanceType;
    }

    private void setAssistanceType(CollectionReference assistanceType) {
        AssistanceType = assistanceType;
    }

    public String getDescription() {
        return Description;
    }

    private void setDescription(String description) {
        Description = description;
    }

    public CollectionReference getInCharge() {
        return InCharge;
    }

    private void setInCharge(CollectionReference inCharge) {
        InCharge = inCharge;
    }

    public DateTime getDeliveryDate() {
        return DeliveryDate;
    }

    private void setDeliveryDate(DateTime deliveryDate) {
        DeliveryDate = deliveryDate;
    }
}
