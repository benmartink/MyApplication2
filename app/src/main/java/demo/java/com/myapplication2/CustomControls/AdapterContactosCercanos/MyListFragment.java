package demo.java.com.myapplication2.CustomControls.AdapterContactosCercanos;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import demo.java.com.myapplication2.EN_Ubicacion;
import demo.java.com.myapplication2.R;

public class MyListFragment extends ListFragment implements OnItemClickListener {

    ArrayList<EN_Ubicacion> objUbicaciones;

    CustomAdapter adapter;
    private List<RowItem> rowItems;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.list_fragment, null, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {

        Toast.makeText(getActivity(), objUbicaciones.get(position).getGUID(), Toast.LENGTH_SHORT)
                .show();

    }

    public void refreshData(ArrayList<EN_Ubicacion> objUbicaciones) {
        //getListView().setAdapter(null);
        this.objUbicaciones = objUbicaciones;
        rowItems = new ArrayList<RowItem>();

        if(objUbicaciones!=null) {
            for (int i = 0; i < objUbicaciones.size(); i++) {
                EN_Ubicacion objUbicacion = objUbicaciones.get(i);
                RowItem items = new RowItem(objUbicacion.getNombreUsuario() + " " + objUbicacion.getApellidoUsuario(), objUbicacion.getGUID());
                rowItems.add(items);
            }
        }

        adapter = new CustomAdapter(getActivity(), rowItems);
        setListAdapter(adapter);
        getListView().setOnItemClickListener(this);

    }
}
