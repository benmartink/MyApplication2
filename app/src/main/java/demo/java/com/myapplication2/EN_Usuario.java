package demo.java.com.myapplication2;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by MARTIN on 08/08/2016.
 */
public class EN_Usuario {

    private String Nombres;
    private String Apellidos;
    private String Correo;
    private String Clave;
    private int Activo;
    private String Cargo;
    private String CelularFijo;
    private String Direccion;
    private String ImagenFondo;
    private String GUID;

    public static EN_Usuario fromJson(JSONObject jsonObject) {
        EN_Usuario b = new EN_Usuario();
        // Deserialize json into object fields
        try {
            b.setGUID(jsonObject.getString("GUID"));
            b.setCorreo(jsonObject.getString("Correo"));
            b.setClave(jsonObject.getString("Clave"));
            b.setActivo(jsonObject.getInt("Activo"));
            b.setNombres(jsonObject.getString("Nombres"));
            b.setApellidos(jsonObject.getString("Apellidos"));
            b.setCelularFijo(jsonObject.getString("CelularFijo"));
            b.setCargo(jsonObject.getString("Cargo"));
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return b;
    }

    public static ArrayList<EN_Usuario> fromJson(JSONArray jsonarray) {
        ArrayList<EN_Usuario> bArray = new ArrayList<EN_Usuario>();

        // Deserialize json into object fields
        try {
            for (int i = 0; i < jsonarray.length(); i++) {
                EN_Usuario b = new EN_Usuario();
                JSONObject _jsonobject = jsonarray.getJSONObject(i);
                b.setGUID(_jsonobject.getString("GUID"));
                b.setCorreo(_jsonobject.getString("Correo"));
                b.setClave(_jsonobject.getString("Clave"));
                b.setActivo(_jsonobject.getInt("Activo"));
                b.setNombres(_jsonobject.getString("Nombres"));
                b.setApellidos(_jsonobject.getString("Apellidos"));
                b.setCelularFijo(_jsonobject.getString("CelularFijo"));
                b.setCargo(_jsonobject.getString("Cargo"));
                bArray.add(b);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return bArray;
    }

    public String getNombres() {
        return Nombres;
    }

    public void setNombres(String nombres) {
        Nombres = nombres;
    }

    public String getApellidos() {
        return Apellidos;
    }

    public void setApellidos(String apellidos) {
        Apellidos = apellidos;
    }

    public String getCorreo() {
        return Correo;
    }

    public void setCorreo(String correo) {
        Correo = correo;
    }

    public String getClave() {
        return Clave;
    }

    public void setClave(String clave) {
        Clave = clave;
    }

    public int getActivo() {
        return Activo;
    }

    public void setActivo(int activo) {
        Activo = activo;
    }

    public String getCargo() {
        return Cargo;
    }

    public void setCargo(String cargo) {
        Cargo = cargo;
    }

    public String getCelularFijo() {
        return CelularFijo;
    }

    public void setCelularFijo(String celularFijo) {
        CelularFijo = celularFijo;
    }

    public String getDireccion() {
        return Direccion;
    }

    public void setDireccion(String direccion) {
        Direccion = direccion;
    }

    public String getImagenFondo() {
        return ImagenFondo;
    }

    public void setImagenFondo(String imagenFondo) {
        ImagenFondo = imagenFondo;
    }

    public String getGUID() {
        return GUID;
    }

    public void setGUID(String GUID) {
        this.GUID = GUID;
    }
}
