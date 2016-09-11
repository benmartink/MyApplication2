package demo.java.com.myapplication2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by MARTIN on 13/08/2016.
 */
public class EN_Ubicacion {
    private String CoordenadaLatitud;
    private String CoordenadaLongitud;
    private String NombreUsuario;
    private String ApellidoUsuario;
    private String GUID;

    public static ArrayList<EN_Ubicacion> fromJson(JSONArray jsonarray) {
        ArrayList<EN_Ubicacion> bArray = new ArrayList<EN_Ubicacion>();
        // Deserialize json into object fields
        try {
            //JSONArray jsonarray = new JSONArray(jsonObject.toString());
            for (int i = 0; i < jsonarray.length(); i++) {
                JSONObject _jsonobject = jsonarray.getJSONObject(i);

                EN_Ubicacion b = new EN_Ubicacion();
                b.setGUID(_jsonobject.getString("GUID"));
                b.setCoordenadaLatitud(_jsonobject.getString("CoordenadaLatitud"));
                b.setCoordenadaLongitud(_jsonobject.getString("CoordenadaLongitud"));
                b.setNombreUsuario(_jsonobject.getString("NombreUsuario"));
                b.setApellidoUsuario(_jsonobject.getString("ApellidoUsuario"));
                bArray.add(b);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return bArray;
    }

    public String getCoordenadaLatitud() {
        return CoordenadaLatitud;
    }

    public void setCoordenadaLatitud(String coordenadaLatitud) {
        CoordenadaLatitud = coordenadaLatitud;
    }

    public String getCoordenadaLongitud() {
        return CoordenadaLongitud;
    }

    public void setCoordenadaLongitud(String coordenadaLongitud) {
        CoordenadaLongitud = coordenadaLongitud;
    }

    public String getNombreUsuario() {
        return NombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        NombreUsuario = nombreUsuario;
    }

    public String getApellidoUsuario() {
        return ApellidoUsuario;
    }

    public void setApellidoUsuario(String apellidoUsuario) {
        ApellidoUsuario = apellidoUsuario;
    }

    public String getGUID() {
        return GUID;
    }

    public void setGUID(String GUID) {
        this.GUID = GUID;
    }
}
