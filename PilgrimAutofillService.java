package com.example.clipboardassist;

import android.service.autofill.AutofillService;
import android.service.autofill.FillRequest;
import android.service.autofill.FillCallback;
import android.service.autofill.FillResponse;
import android.service.autofill.Dataset;
import android.view.autofill.AutofillId;
import android.view.autofill.AutofillValue;
import android.widget.RemoteViews;
import android.os.CancellationSignal;
import android.app.assist.AssistStructure;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class PilgrimAutofillService extends AutofillService {
    @Override public void onFillRequest(FillRequest request, CancellationSignal cancellationSignal, FillCallback callback) {
        try {
            AssistStructure structure=request.getFillContexts().get(request.getFillContexts().size()-1).getStructure();
            ArrayList<Field> fields=new ArrayList<>(); collect(structure.getWindowNodeAt(0).getRootViewNode(),fields);
            FillResponse.Builder response=new FillResponse.Builder(); JSONArray profiles=ProfileStore.profiles(this); JSONObject general=ProfileStore.general(this);
            if(general.length()>0)addDataset(response,fields,general,"General details");
            for(int i=0;i<profiles.length();i++){JSONObject p=profiles.optJSONObject(i);if(p!=null)addDataset(response,fields,p,ProfileStore.value(p,"Full name").isEmpty()?"Pilgrim "+(i+1):ProfileStore.value(p,"Full name"));}
            callback.onSuccess(response.build());
        } catch(Exception e) { callback.onSuccess(null); }
    }
    private void collect(AssistStructure.ViewNode node,List<Field> out){AutofillId id=node.getAutofillId();if(id!=null){String label="";if(node.getHint()!=null)label=node.getHint().toString();if(label.isEmpty()&&node.getText()!=null)label=node.getText().toString();String[] hints=node.getAutofillHints();if(hints!=null&&hints.length>0)label=label+" "+hints[0];String norm=label.toLowerCase();if(looksLikeField(norm))out.add(new Field(id,norm));}for(int i=0;i<node.getChildCount();i++)collect(node.getChildAt(i),out);}
    private boolean looksLikeField(String s){return s.contains("name")||s.contains("age")||s.contains("gender")||s.contains("email")||s.contains("mobile")||s.contains("phone")||s.contains("address")||s.contains("city")||s.contains("state")||s.contains("country")||s.contains("pin")||s.contains("postal")||s.contains("zip")||s.contains("aadhar")||s.contains("aadhaar")||s.contains("passport")||s.contains("id number")||s.contains("proof");}
    private String match(JSONObject data,String label){if(label.contains("email"))return first(data,"Email address","Email");if(label.contains("city"))return first(data,"City");if(label.contains("state"))return first(data,"State");if(label.contains("country"))return first(data,"Country");if(label.contains("address"))return first(data,"Address");if(label.contains("pin")||label.contains("postal")||label.contains("zip"))return first(data,"PIN code");if(label.contains("mobile")||label.contains("phone"))return first(data,"Mobile number");if(label.contains("age"))return first(data,"Age");if(label.contains("gender"))return first(data,"Gender");if(label.contains("aadhar")||label.contains("aadhaar")||label.contains("passport")||label.contains("id number"))return first(data,"ID proof number");if(label.contains("proof"))return first(data,"ID proof type");if(label.contains("name"))return first(data,"Full name");return "";}
    private String first(JSONObject o,String... keys){for(String k:keys){String v=ProfileStore.value(o,k);if(!v.isEmpty())return v;}return "";}
    private void addDataset(FillResponse.Builder response,List<Field> fields,JSONObject data,String title){try{RemoteViews presentation=new RemoteViews(getPackageName(),android.R.layout.simple_list_item_1);presentation.setTextViewText(android.R.id.text1,title);Dataset.Builder ds=new Dataset.Builder(presentation);int count=0;for(Field f:fields){String value=match(data,f.label);if(!value.isEmpty()){ds.setValue(f.id,AutofillValue.forText(value));count++;}}if(count>0)response.addDataset(ds.build());}catch(Exception ignored){}}
    @Override public void onSaveRequest(android.service.autofill.SaveRequest request, android.service.autofill.SaveCallback callback){callback.onSuccess();}
    private static class Field {final AutofillId id;final String label;Field(AutofillId i,String l){id=i;label=l;}}
}
