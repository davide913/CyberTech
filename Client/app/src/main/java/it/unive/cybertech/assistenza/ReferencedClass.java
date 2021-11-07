package it.unive.cybertech.assistenza;

import android.app.Application;

import java.util.ArrayList;
import java.util.List;

public class ReferencedClass extends Application {
    private static List<RequestInfo> requestInfoList = new ArrayList<RequestInfo>();
    private static int nextId = 0;

    public ReferencedClass() {
        RequestInfo requestInfo = new RequestInfo(nextId, "Un titolo di prova", "Prova Location", "07/11/2021");
        requestInfoList.add(requestInfo);
    }

    public static List<RequestInfo> getRequestInfoList() {
        return requestInfoList;
    }

    public static void setRequestInfoList(List<RequestInfo> requestInfoList) {
        ReferencedClass.requestInfoList = requestInfoList;
    }

    public static int getNextId() {
        return nextId;
    }

    public static void setNextId(int nextId) {
        ReferencedClass.nextId = nextId;
    }
}
